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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Takes care of populating product configuration relevant attributes
 */
public class CartConfigurationPopulator implements Populator<CartModel, CartData>
{

	private static final Logger LOG = Logger.getLogger(CartConfigurationPopulator.class);
	private SessionAccessService sessionAccessService;
	private ProductConfigurationService productConfigurationService;
	private CartConfigurationDisplayPopulator configurationDisplayPopulator;


	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @return sessionAccessService
	 */
	public SessionAccessService getSessionAccessService()
	{
		return this.sessionAccessService;
	}


	@Override
	public void populate(final CartModel source, final CartData target)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		for (final AbstractOrderEntryModel entry : source.getEntries())
		{
			populateCartEntry(target, entry);
		}

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Populating for cart took " + duration + " ms");
		}
	}

	/**
	 * Transfers configuration related attributes from order entry into its DTO representation
	 *
	 * @param target
	 *           Cart DTO, used to get the cart entry DTO via searching for key
	 * @param entry
	 *           Cart entry model
	 */
	protected void populateCartEntry(final CartData target, final AbstractOrderEntryModel entry)
	{
		final Boolean isConfigurable = entry.getProduct().getSapConfigurable();
		if (isConfigurable != null && isConfigurable.booleanValue())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is Configurable ==> populating DTO.");
			}

			final CartEntryConfigurationAttributes configurationAttributes = getProductConfigurationService()
					.calculateCartEntryConfigurationAttributes(entry);

			checkForExternalConfiguration(entry);
			writeToTargetEntry(target, entry, configurationAttributes);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is NOT Configurable ==> skipping population of DTO.");
			}
		}
	}

	/**
	 * Writes external configuration to cart entry if it is not present yet
	 *
	 * @param entry
	 *           Cart entry
	 */
	protected void checkForExternalConfiguration(final AbstractOrderEntryModel entry)
	{
		final String xml = entry.getExternalConfiguration();
		if (xml == null || xml.isEmpty())
		{
			final String configId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
			entry.setExternalConfiguration(getProductConfigurationService().retrieveExternalConfiguration(configId));
		}
	}

	/**
	 * Writes result to target entry DTO
	 *
	 * @param target
	 *           Cart DTO, used to get the cart entry DTO via searching for key
	 * @param entry
	 *           Cart entry model
	 * @param configurationAttributes
	 *           Configuration relevant attributes
	 */
	protected void writeToTargetEntry(final CartData target, final AbstractOrderEntryModel entry,
			final CartEntryConfigurationAttributes configurationAttributes)
	{
		final OrderEntryData targetEntry = findTargetEntry(target, entry.getEntryNumber());
		if (targetEntry == null)
		{
			throw new IllegalArgumentException("Target items do not match source items");
		}
		targetEntry.setItemPK(entry.getPk().toString());
		targetEntry.setConfigurationAttached(true);
		targetEntry.setConfigurationConsistent(configurationAttributes.getConfigurationConsistent().booleanValue());
		targetEntry.setConfigurationErrorCount(configurationAttributes.getNumberOfErrors().intValue());

		addNumberOfIssuesForCartDisplay(configurationAttributes, targetEntry);

		final List<ConfigurationInfoData> configInfoData = new ArrayList<>();
		final String configId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
		final ConfigModel configModel = getProductConfigurationService().retrieveConfigurationModel(configId);
		getConfigurationDisplayPopulator().populate(configModel, configInfoData);
		targetEntry.setConfigurationInfos(configInfoData);
	}

	protected void addNumberOfIssuesForCartDisplay(final CartEntryConfigurationAttributes configurationAttributes,
			final OrderEntryData targetEntry)
	{
		if (!configurationAttributes.getConfigurationConsistent().booleanValue())
		{
			final Map<ProductInfoStatus, Integer> statusSummaryMap = new HashMap<>();
			statusSummaryMap.put(ProductInfoStatus.ERROR, configurationAttributes.getNumberOfErrors());
			targetEntry.setStatusSummaryMap(statusSummaryMap);
		}
	}

	/**
	 * Finds an entry part of the cart
	 *
	 * @param target
	 *           Cart DTO representation
	 * @param entryNumber
	 *           Number of item we search for
	 * @return Target order entry DTO
	 */
	protected OrderEntryData findTargetEntry(final CartData target, final Integer entryNumber)
	{
		for (final OrderEntryData targetEntry : target.getEntries())
		{
			if (targetEntry.getEntryNumber().equals(entryNumber))
			{
				return targetEntry;
			}
		}
		return null;
	}



	/**
	 * @return the configurationDisplayPopulator
	 */
	public CartConfigurationDisplayPopulator getConfigurationDisplayPopulator()
	{
		return configurationDisplayPopulator;
	}

	/**
	 * @param configurationDisplayPopulator
	 *           the configurationDisplayPopulator to set
	 */
	public void setConfigurationDisplayPopulator(final CartConfigurationDisplayPopulator configurationDisplayPopulator)
	{
		this.configurationDisplayPopulator = configurationDisplayPopulator;
	}

}
