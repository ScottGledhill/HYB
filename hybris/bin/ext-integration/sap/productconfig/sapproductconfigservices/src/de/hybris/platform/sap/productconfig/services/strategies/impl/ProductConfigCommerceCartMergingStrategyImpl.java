/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartMergingStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


public class ProductConfigCommerceCartMergingStrategyImpl extends DefaultCommerceCartMergingStrategy
{

	private static final Logger LOG = Logger.getLogger(ProductConfigCommerceCartMergingStrategyImpl.class);
	private ProductConfigurationService productConfigurationService;


	@Override
	public void mergeCarts(final CartModel fromCart, final CartModel toCart, final List<CommerceCartModification> modifications)
			throws CommerceCartMergingException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ProductConfig before mergeCarts, fromCart=" + fromCart.getGuid() + "; toCart=" + toCart.getGuid());
		}

		final Map<String, List<String>> extConfigsBeforeMerge = collectCartEntriesByProductCodeAndQuantity(fromCart);
		final Map<PK, AbstractOrderEntryModel> toCartEntriesBeforeMerge = collectCartEntriesByPk(toCart);
		super.mergeCarts(fromCart, toCart, modifications);
		final int changeCounter = reApplyExtConfigsAfterMerge(toCart, toCartEntriesBeforeMerge, extConfigsBeforeMerge);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Restored " + changeCounter + " configurations in cart " + toCart.getGuid()
					+ (changeCounter > 0 ? "; saving cart" : "; not saving cart"));
		}

		if (changeCounter > 0)
		{
			getModelService().save(toCart);
		}

		LOG.debug("ProductConfig after mergeCarts");
	}

	/**
	 * Puts the cart entries into a map with PK as key.
	 *
	 * @param cart
	 *           cart to be processed
	 * @return map of cart entries by PK
	 */
	protected Map<PK, AbstractOrderEntryModel> collectCartEntriesByPk(final CartModel cart)
	{
		final Map<PK, AbstractOrderEntryModel> map = new HashMap<>(cart.getEntries().size());
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			map.put(entry.getPk(), entry);
		}
		return map;
	}

	/**
	 * Puts the cart entries into a map with Product code and quantity as key.
	 *
	 * @param cart
	 *           cart to be processed
	 * @return map of cart entries by Product code and quantity
	 */
	protected Map<String, List<String>> collectCartEntriesByProductCodeAndQuantity(final CartModel cart)
	{
		final Map<String, List<String>> map = new HashMap<>(cart.getEntries().size());

		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			final ProductModel product = entry.getProduct();
			final Boolean isConfigurable = product.getSapConfigurable();
			if (isConfigurable != null && isConfigurable.booleanValue())
			{
				final String key = createKeyForCartEntry(entry);
				List<String> configurablesEntries;
				if (map.containsKey(key))
				{
					configurablesEntries = addToConfigListForMultipleOccurences(map, entry.getExternalConfiguration(), key);
				}
				else
				{
					configurablesEntries = Collections.singletonList(entry.getExternalConfiguration());

				}

				map.put(key, configurablesEntries);
			}
		}
		return map;
	}

	protected void exchangeExternalConfigurationAndUpdateEntryBasePrice(final AbstractOrderEntryModel entryToChange,
			final Map<String, List<String>> extConfigsBeforeMerge) throws CommerceCartMergingException
	{
		final List<String> list = extConfigsBeforeMerge.get(createKeyForCartEntry(entryToChange));
		if (CollectionUtils.isEmpty(list))
		{
			throw new CommerceCartMergingException("Cannot exchange External configuration at restored cart entry");
		}

		getProductConfigurationService().updateCartEntryExternalConfiguration(list.get(0), entryToChange);
		getProductConfigurationService().updateCartEntryBasePrice(entryToChange);

		if (list.size() > 1)
		{
			list.remove(0);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Exchanged external configuration at cart entry " + entryToChange.getPk() + " (Product: "
					+ entryToChange.getProduct().getCode() + ", Quantity: " + entryToChange.getQuantity().toString() + ")");
		}
	}


	protected String createKeyForCartEntry(final AbstractOrderEntryModel entryToChange)
	{
		final StringBuilder key = new StringBuilder();
		key.append(entryToChange.getProduct().getCode());
		key.append(entryToChange.getQuantity());
		return key.toString();
	}

	/**
	 * @param fromCart
	 * @return map of collected configs before merge
	 * @deprecated
	 */
	@Deprecated
	protected Map<String, List<String>> collectConfigsBeforeMerge(final CartModel fromCart)
	{
		final Map<String, List<String>> collectedConfigs = new HashMap(fromCart.getEntries().size());
		for (final AbstractOrderEntryModel entry : fromCart.getEntries())
		{
			final ProductModel product = entry.getProduct();
			final Boolean isConfigurable = product.getSapConfigurable();
			if (isConfigurable != null && isConfigurable.booleanValue())
			{
				final String productCode = product.getCode();
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Storing external Configuration for product=" + productCode);
				}
				List<String> configList;
				if (collectedConfigs.containsKey(productCode))
				{
					configList = addToConfigListForMultipleOccurences(collectedConfigs, entry.getExternalConfiguration(), productCode);
				}
				else
				{
					// 99% case, a product is only once in cart, hence creating SingeltonList
					configList = Collections.singletonList(entry.getExternalConfiguration());
				}
				collectedConfigs.put(productCode, configList);
			}
		}
		if (LOG.isDebugEnabled())
		{
			final int numberCollectedConfigs = countCollectedConfigs(collectedConfigs);
			LOG.debug("Saved " + numberCollectedConfigs + " externalConfigs from cart " + fromCart.getGuid());
		}
		return collectedConfigs;
	}

	/**
	 * @param missingConfigs
	 * @param collectedConfigs
	 * @return number of changes
	 * @throws CommerceCartMergingException
	 * @deprecated
	 */
	@Deprecated
	protected int reApplyConfigsAfterMerge(final Map<String, List<AbstractOrderEntryModel>> missingConfigs,
			final Map<String, List<String>> collectedConfigs) throws CommerceCartMergingException
	{
		int changeCounter = 0;
		final int numberCollectedConfigs = countCollectedConfigs(collectedConfigs);
		final int numberMissingConfigs = countMissingConfigs(missingConfigs);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Restoring " + numberCollectedConfigs + " collected configs to " + numberMissingConfigs + " missing configs");
		}
		if (numberCollectedConfigs != numberMissingConfigs)
		{
			throw new CommerceCartMergingException("Merge created inconsitentc cart, configSaved=" + numberCollectedConfigs
					+ "; but missingConfigs=" + numberMissingConfigs);
		}

		for (final Entry<String, List<String>> collectedConfigEntry : collectedConfigs.entrySet())
		{
			final List<AbstractOrderEntryModel> missingConfigEntry = missingConfigs.get(collectedConfigEntry.getKey());
			int ii = 0;
			for (final String extnernalConfig : collectedConfigEntry.getValue())
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Restoring external config for cartItemPK=" + missingConfigEntry.get(ii).getPk() + "; which is item #"
							+ ii + " with productCode=" + collectedConfigEntry.getKey());
				}

				missingConfigEntry.get(ii).setExternalConfiguration(extnernalConfig);
				ii++;
				changeCounter++;
			}
		}
		return changeCounter;
	}


	/**
	 * @param toCart
	 * @return map of entities with missing config
	 * @deprecated
	 */
	@Deprecated
	protected Map<String, List<AbstractOrderEntryModel>> findEntiesWithMissingConfig(final CartModel toCart)
	{
		final Map<String, List<AbstractOrderEntryModel>> missingConfigs = new HashMap(toCart.getEntries().size());

		for (final AbstractOrderEntryModel entry : toCart.getEntries())
		{
			final String externalConfig = entry.getExternalConfiguration();
			final ProductModel product = entry.getProduct();
			final Boolean isConfigurable = product.getSapConfigurable();
			if (isConfigurable != null && isConfigurable.booleanValue() && (externalConfig == null || externalConfig.isEmpty()))
			{
				final String productCode = product.getCode();
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Missing external Configuration for cartEntry=" + entry.getPk() + "; product=" + productCode);
				}
				List<AbstractOrderEntryModel> entryList;
				if (missingConfigs.containsKey(productCode))
				{
					entryList = addToConfigListForMultipleOccurences(missingConfigs, entry, productCode);
				}
				else
				{
					// 99% case, a product is only once in cart, hence creating SingeltonList
					entryList = Collections.singletonList(entry);
				}
				missingConfigs.put(productCode, entryList);
			}
		}

		if (LOG.isDebugEnabled())
		{
			final int numberMissingConfigs = countMissingConfigs(missingConfigs);
			LOG.debug(numberMissingConfigs + " externalConfigs missing in cart " + toCart.getGuid());
		}
		return missingConfigs;
	}

	protected int reApplyExtConfigsAfterMerge(final CartModel toCart,
			final Map<PK, AbstractOrderEntryModel> toCartEntriesBeforeMerge, final Map<String, List<String>> extConfigsBeforeMerge)
			throws CommerceCartMergingException
	{

		int changeCounter = 0;

		for (final AbstractOrderEntryModel entry : toCart.getEntries())
		{
			final ProductModel product = entry.getProduct();
			final Boolean isConfigurable = product.getSapConfigurable();
			if (isConfigurable != null && isConfigurable.booleanValue() && !toCartEntriesBeforeMerge.containsKey(entry.getPk()))
			{
				exchangeExternalConfigurationAndUpdateEntryBasePrice(entry, extConfigsBeforeMerge);
				changeCounter++;
			}
		}
		return changeCounter;
	}


	protected <T> List<T> addToConfigListForMultipleOccurences(final Map<String, List<T>> missingConfigs, final T entry,
			final String key)
	{
		List<T> entryList;
		// 1% case - wrap the singletonList into a fully arraylist, if required
		entryList = missingConfigs.get(key);
		if (entryList.size() == 1)
		{
			final List<T> newConfigList = new ArrayList<>(entryList.size() + 1);
			newConfigList.addAll(entryList);
			entryList = newConfigList;
		}
		entryList.add(entry);
		return entryList;
	}

	/**
	 * @param collectedConfigs
	 * @return number of collected configs
	 * @deprecated
	 */
	@Deprecated
	protected int countCollectedConfigs(final Map<String, List<String>> collectedConfigs)
	{
		int counter = 0;
		for (final List<String> list : collectedConfigs.values())
		{
			counter += list.size();
		}
		return counter;
	}

	/**
	 * @param missingConfigs
	 * @return number of missing configs
	 * @deprecated
	 */
	@Deprecated
	protected int countMissingConfigs(final Map<String, List<AbstractOrderEntryModel>> missingConfigs)
	{
		int counter = 0;
		for (final List<AbstractOrderEntryModel> list : missingConfigs.values())
		{
			counter += list.size();
		}
		return counter;
	}

	protected CommerceCartParameter createCommerceCartParameterForCalculateCart(final CartModel sessionCart)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(sessionCart);
		return parameter;
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}
}
