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
package de.hybris.platform.sap.productconfig.model.pricing;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.productconfig.model.constants.SapproductconfigmodelConstants;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import sap.hybris.integration.models.services.SalesAreaService;


/**
 * Default implementation of {@link PricingConfigurationParameter}
 */
public class DefaultPricingConfigurationParameter implements PricingConfigurationParameter
{
	private static final Logger LOG = Logger.getLogger(DefaultPricingConfigurationParameter.class);
	private ModuleConfigurationAccess moduleConfigurationAccess;

	private SalesAreaService commonSalesAreaService;

	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	@Required
	public void setCommonSalesAreaService(final SalesAreaService commonSalesAreaService)
	{
		this.commonSalesAreaService = commonSalesAreaService;
	}

	@Override
	public boolean isPricingSupported()
	{
		boolean isPricingSupported = false;
		try
		{
			if (moduleConfigurationAccess != null && moduleConfigurationAccess.getSAPConfigurationName() != null
					&& !moduleConfigurationAccess.getSAPConfigurationName().isEmpty())
			{
				final Object propertyValue = moduleConfigurationAccess
						.getProperty(SapproductconfigmodelConstants.CONFIGURATION_PRICING_SUPPORTED);

				if (propertyValue instanceof Boolean)
				{
					isPricingSupported = ((Boolean) propertyValue).booleanValue();
				}
			}
		}
		catch (final ConfigurationRuntimeException e)
		{
			LOG.warn("No configuration was set - Pricing will be disabled -  [" + e.getLocalizedMessage() + "]", e);
		}

		return isPricingSupported;
	}

	@Override
	public String getTargetForBasePrice()
	{
		String targetBasePrice = null;
		if (moduleConfigurationAccess != null)
		{
			final Object propertyValue = moduleConfigurationAccess
					.getProperty(SapproductconfigmodelConstants.CONFIGURATION_CONDITION_FUNCTION_BASE_PRICE);

			if (propertyValue instanceof String)
			{
				targetBasePrice = (String) propertyValue;
			}
		}

		return targetBasePrice;
	}

	@Override
	public String getTargetForSelectedOptions()
	{
		String targetSelectedOptions = null;
		if (moduleConfigurationAccess != null)
		{
			final Object propertyValue = moduleConfigurationAccess
					.getProperty(SapproductconfigmodelConstants.CONFIGURATION_CONDITION_FUNCTION_SECLECTED_OPTIONS);

			if (propertyValue instanceof String)
			{
				targetSelectedOptions = (String) propertyValue;
			}
		}

		return targetSelectedOptions;
	}

	@Override
	public String getPricingProcedure()
	{
		String pricingProcedure = null;
		if (moduleConfigurationAccess != null)
		{
			final Object propertyValue = moduleConfigurationAccess
					.getProperty(SapproductconfigmodelConstants.CONFIGURATION_PRICING_PROCEDURE);

			if (propertyValue instanceof String)
			{
				pricingProcedure = (String) propertyValue;
			}
		}

		return pricingProcedure;
	}

	@Override
	public String getSalesOrganization()
	{
		return commonSalesAreaService.getSalesOrganization();
	}

	@Override
	public String getDistributionChannelForConditions()
	{
		return commonSalesAreaService.getDistributionChannelForConditions();
	}

	@Override
	public String getDivisionForConditions()
	{
		return commonSalesAreaService.getDivisionForConditions();
	}

	@Override
	public String retrieveCurrencySapCode(final CurrencyModel currencyModel)
	{
		String currencySapCode = null;
		if (currencyModel != null)
		{
			currencySapCode = currencyModel.getSapCode();
		}
		return currencySapCode;
	}

	@Override
	public String retrieveUnitSapCode(final UnitModel unitModel)
	{
		String unitSapCode = null;
		if (unitModel != null)
		{
			unitSapCode = unitModel.getSapCode();
		}
		return unitSapCode;
	}

}