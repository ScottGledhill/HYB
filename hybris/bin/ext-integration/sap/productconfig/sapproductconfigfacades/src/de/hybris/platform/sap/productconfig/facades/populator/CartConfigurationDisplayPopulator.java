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

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class CartConfigurationDisplayPopulator implements Populator<ConfigModel, List<ConfigurationInfoData>>
{
	private int maxNumberOfDisplayedCsticsInCart = 4;
	public static final String VALUE_SEPARATOR = "; ";
	private SessionAccessService sessionAccessService;
	private ClassificationSystemCPQAttributesProvider nameProvider;


	@Override
	public void populate(final ConfigModel source, final List<ConfigurationInfoData> target)
	{
		final InstanceModel rootInstance = source.getRootInstance();
		int numberOfDisplayedCstics = 0;
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = getSessionAccessService().getCachedNameMap();
		for (final CsticGroupModel currentGroup : rootInstance.getCsticGroups())
		{
			numberOfDisplayedCstics = processGroupForCartDisplay(target, rootInstance, numberOfDisplayedCstics, currentGroup,
					nameMap);
		}
		if (CollectionUtils.isEmpty(target))
		{
			addInitialConfigurationInfo(target);
		}
	}

	protected int processGroupForCartDisplay(final List<ConfigurationInfoData> target, final InstanceModel rootInstance,
			final int numberOfDisplayedCstics, final CsticGroupModel currentGroup,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		int currentNumberOfDisplayedCstics = numberOfDisplayedCstics;
		for (final String csticName : currentGroup.getCsticNames())
		{
			if (currentNumberOfDisplayedCstics >= maxNumberOfDisplayedCsticsInCart)
			{
				// stop as soon as maximum number of cstics to be displayed has been reached
				break;
			}
			final CsticModel cstic = rootInstance.getCstic(csticName);
			if (displayCstic(cstic))
			{
				addCsticForCartDisplay(target, cstic, nameMap);
				currentNumberOfDisplayedCstics++;
			}
		}
		return currentNumberOfDisplayedCstics;
	}

	protected void addCsticForCartDisplay(final List<ConfigurationInfoData> configurationInfoData, final CsticModel csticModel,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{

		final ClassificationSystemCPQAttributesProvider cpqNameProvider = getNameProvider();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = cpqNameProvider.getCPQAttributes(csticModel.getName(),
				nameMap);
		final boolean isNameProviderDebugEnabled = cpqNameProvider.isDebugEnabled();
		final String displayName = cpqNameProvider.getDisplayName(csticModel, cpqAttributes, isNameProviderDebugEnabled);
		final ConfigurationInfoData configInfoInline = new ConfigurationInfoData();
		configInfoInline.setConfigurationLabel(displayName);
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		configInfoInline.setConfigurationValue(generateConfigInfoInline(assignedValues, csticModel, cpqAttributes,
				isNameProviderDebugEnabled));
		configInfoInline.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configurationInfoData.add(configInfoInline);
	}

	protected String generateConfigInfoInline(final List<CsticValueModel> assignedValues, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes, final boolean isNameProviderDebugEnabled)
	{
		final StringBuilder builder = new StringBuilder();
		if (!CollectionUtils.isEmpty(assignedValues))
		{
			for (int i = 0; i < assignedValues.size(); i++)
			{
				if (i > 0)
				{
					builder.append(VALUE_SEPARATOR);
				}
				builder.append(getNameProvider().getOverviewValueName(assignedValues.get(i), csticModel, cpqAttributes,
						isNameProviderDebugEnabled));
			}
		}
		return builder.toString();
	}

	/**
	 * At least one ConfigurationInfo object has to be added to the target, otherwise no configuration link will be
	 * displayed.
	 *
	 * @param configurationInfoData
	 */
	protected void addInitialConfigurationInfo(final List<ConfigurationInfoData> configurationInfoData)
	{
		final ConfigurationInfoData configInfo = new ConfigurationInfoData();
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configurationInfoData.add(configInfo);
	}

	protected boolean displayCstic(final CsticModel cstic)
	{
		if (cstic.isVisible() && !CollectionUtils.isEmpty(cstic.getAssignedValues()))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param maxNumberOfDisplayedCsticsInCart
	 *           the maxNumberOfDisplayedCsticsInCart to set
	 */
	public void setMaxNumberOfDisplayedCsticsInCart(final int maxNumberOfDisplayedCsticsInCart)
	{
		this.maxNumberOfDisplayedCsticsInCart = maxNumberOfDisplayedCsticsInCart;
	}

	/**
	 * @return the sessionAccessService
	 */
	public SessionAccessService getSessionAccessService()
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

	/**
	 * @return the nameProvider
	 */
	public ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param nameProvider
	 *           the nameProvider to set
	 */
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

}
