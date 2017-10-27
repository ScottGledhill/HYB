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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;


/**
 * CPQ AddToCart hook.<br>
 * This hook is executed before and after addToCart. Main purpose is to ensure that there is a runtime configuration for
 * each configurable product attached to the cart entry. This will already be the case if the add to cart action was
 * triggered from the configuration screen. However if a configurable product is added to cart directly from catalog,
 * without configuraing it beforehand, no runtime configuration exists, so the default configuration will be
 * instantiated and attached to the cart item.<br>
 * Additionally this hook will ensure that the add to cart will always produce a new cart entry in case of configurable
 * products. Merging two configurable products into one cart entry does not make sense, as they might have a different
 * runtime configuration.
 */
public class ProductConfigAddToCartHookImpl implements CommerceAddToCartMethodHook
{
	private static final Logger LOG = Logger.getLogger(ProductConfigAddToCartHookImpl.class);
	private ProductConfigurationService productConfigurationService;
	private SessionAccessService sessionAccessService;
	private ModelService modelService;
	private CommerceCartService commerceCartService;
	private TrackingRecorder recorder;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		LOG.debug("ProductConfig beforeAddToCart start");
		final CartModel toCart = parameters.getCart();
		final Boolean sapConfigurable = parameters.getProduct().getSapConfigurable();

		if (sapConfigurable != null && sapConfigurable.booleanValue())
		{
			LOG.debug("Changing cart entry and paramerers for CartGUID=" + toCart.getGuid());
			setCreateNewEntryForConfigurableProduct(parameters);

			if (parameters.getConfigId() == null)
			{
				createDefaultConfigForNotConfiguredCartEntries(parameters);
			}
		}
		LOG.debug("ProductConfig beforeAddToCart end");
	}

	protected void createDefaultConfigForNotConfiguredCartEntries(final CommerceCartParameter parameters)
	{
		final String productCode = parameters.getProduct().getCode();
		final KBKeyImpl kbKey = new KBKeyImpl(productCode);
		final ConfigModel configModel = productConfigurationService.createDefaultConfiguration(kbKey);
		parameters.setConfigId(configModel.getId());
		LOG.debug("Default configuration with config ID " + parameters.getConfigId() + " created for the product: " + productCode);
	}


	protected void setCreateNewEntryForConfigurableProduct(final CommerceCartParameter parameters)
	{
		if (!parameters.isCreateNewEntry() && LOG.isDebugEnabled())
		{
			LOG.debug("Changing 'createNewEntry' from 'false' to 'true'" + "; product=" + parameters.getProduct().getCode());
		}
		// configurable products should always fore a new cart item
		parameters.setCreateNewEntry(true);
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		LOG.debug("ProductConfig afterAddToCart start");
		if (parameters == null)
		{
			return;
		}

		final Boolean sapConfigurable = parameters.getProduct().getSapConfigurable();
		if (sapConfigurable != null && sapConfigurable.booleanValue())
		{
			final AbstractOrderEntryModel entry = result.getEntry();
			getSessionAccessService().setConfigIdForCartEntry(entry.getPk().toString(), parameters.getConfigId());
			getProductConfigurationService().updateCartEntryBasePrice(entry);
			//	save is needed to set calculated parameter on cart to false, this parameter triggered the recalculation on the cart
			modelService.save(entry);
			getCommerceCartService().calculateCart(parameters);
			getProductConfigurationService().updateCartEntryExternalConfiguration(parameters, entry);
			modelService.save(entry);
			recorder.recordAddToCart(entry, parameters);
		}

		LOG.debug("ProductConfig afterAddToCart end");
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           injects the hybris model service
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ProductConfigurationService getProductConfigurationService()
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

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           ibjects the cart sevice for interaction with the cart
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           cpq tracking recorder, for recording cart actions
	 */
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}


}
