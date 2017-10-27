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
package de.hybris.platform.sap.productconfig.services;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.impl.ClassificationSystemCPQAttributesContainer;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Container for session attibutes used in product configuration
 */
public class ProductConfigSessionAttributeContainer implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Map<String, String> cartEntryConfigurations = Collections.synchronizedMap(new HashMap<String, String>());

	private final Map<String, String> productCartEntries = Collections.synchronizedMap(new HashMap<String, String>());

	private final transient Map<String, Object> cartEntryUiStatuses = Collections.synchronizedMap(new HashMap<String, Object>());

	private final transient Map<String, Object> productUiStatuses = Collections.synchronizedMap(new HashMap<String, Object>());

	private final Set<String> indexedProperties = Collections.synchronizedSet(new HashSet<String>());

	private transient Map<String, ClassificationSystemCPQAttributesContainer> classificationSystemCPQAttributes = new ConcurrentHashMap<String, ClassificationSystemCPQAttributesContainer>(
			512);

	private transient ConfigurationProvider configurationProvider;

	private transient Map<String, ConfigModel> configurationModelEngineStates = Collections
			.synchronizedMap(new HashMap<String, ConfigModel>());

	/**
	 * @return the cartEntryConfigurations
	 */
	public Map<String, String> getCartEntryConfigurations()
	{
		return cartEntryConfigurations;
	}

	/**
	 * @param cartEntryConfigurations
	 *           the cartEntryConfigurations to set
	 */
	public void setCartEntryConfigurations(final Map<String, String> cartEntryConfigurations)
	{
		this.cartEntryConfigurations.clear();
		this.cartEntryConfigurations.putAll(cartEntryConfigurations);
	}

	/**
	 * @return the productCartEntries
	 */
	public Map<String, String> getProductCartEntries()
	{
		return productCartEntries;
	}

	/**
	 * @param productCartEntries
	 *           the productCartEntries to set
	 */
	public void setProductCartEntries(final Map<String, String> productCartEntries)
	{
		this.productCartEntries.clear();
		this.productCartEntries.putAll(productCartEntries);
	}

	/**
	 * @return the cartEntryUiStatuses
	 */
	public Map<String, Object> getCartEntryUiStatuses()
	{
		return cartEntryUiStatuses;
	}

	/**
	 * @param cartEntryUiStatuses
	 *           the cartEntryUiStatuses to set
	 */
	public void setCartEntryUiStatuses(final Map<String, Object> cartEntryUiStatuses)
	{
		this.cartEntryUiStatuses.clear();
		this.cartEntryUiStatuses.putAll(cartEntryUiStatuses);
	}

	/**
	 * @return the productUiStatuses
	 */
	public Map<String, Object> getProductUiStatuses()
	{
		return productUiStatuses;
	}

	/**
	 * @param productUiStatuses
	 *           the productUiStatuses to set
	 */
	public void setProductUiStatuses(final Map<String, Object> productUiStatuses)
	{
		this.productUiStatuses.clear();
		this.productUiStatuses.putAll(productUiStatuses);
	}

	/**
	 * @return the indexedProperties
	 */
	public Set<String> getIndexedProperties()
	{
		return indexedProperties;
	}

	/**
	 * @param indexedProperties
	 *           the indexedProperties to set
	 */
	public void setIndexedProperties(final Set<String> indexedProperties)
	{
		this.indexedProperties.clear();
		this.indexedProperties.addAll(indexedProperties);
	}

	/**
	 * @return the classificationSystemCPQAttributes
	 */
	public Map<String, ClassificationSystemCPQAttributesContainer> getClassificationSystemCPQAttributes()
	{
		return classificationSystemCPQAttributes;
	}

	/**
	 * @return the configurationProvider
	 */
	public ConfigurationProvider getConfigurationProvider()
	{
		return configurationProvider;
	}

	/**
	 * @param configurationProvider
	 *           the configurationProvider to set
	 */
	public void setConfigurationProvider(final ConfigurationProvider configurationProvider)
	{
		this.configurationProvider = configurationProvider;
	}

	/**
	 * @return the configurationModelEngineStates
	 */
	public Map<String, ConfigModel> getConfigurationModelEngineStates()
	{
		return configurationModelEngineStates;
	}

}
