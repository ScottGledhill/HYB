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

import java.util.Map;
import java.util.Set;


/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
@SuppressWarnings("squid:S1214")
public interface SessionAccessService
{
	String PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER = "productconfigSessionAttributeContainer";

	/**
	 * returns the unique session id
	 *
	 * @return session id
	 *
	 */
	String getSessionId();

	/**
	 * Stores configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Retrieves config identifier from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 */
	String getConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return Object
	 */
	<T> T getUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param productKey
	 *           Product key
	 * @return Object
	 */
	<T> T getUiStatusForProduct(String productKey);

	/**
	 * Stores object for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param uiStatus
	 *           Object
	 */
	void setUiStatusForCartEntry(String cartEntryKey, Object uiStatus);

	/**
	 * Stores object for a product key into the session
	 *
	 * @param productKey
	 *           Product key
	 * @param uiStatus
	 *           Object
	 */
	void setUiStatusForProduct(String productKey, Object uiStatus);

	/**
	 * Removes object for a cart entry
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 */
	void removeUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Removes object for a product
	 *
	 * @param productKey
	 *           Product key
	 */
	void removeUiStatusForProduct(String productKey);

	/**
	 * Retrieves cart entry key belonging to a specific config ID
	 *
	 * @param configId
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForConfigId(String configId);

	/**
	 * Stores cart entry in session per product key
	 *
	 * @param productKey
	 * @param cartEntryId
	 *           String representation of the cart entry primary key
	 */
	void setCartEntryForProduct(String productKey, String cartEntryId);

	/**
	 * Retrieves cart entry key per product
	 *
	 * @param productKey
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForProduct(String productKey);

	/**
	 * Removes cart entry key for product
	 *
	 * @param productKey
	 */
	void removeCartEntryForProduct(String productKey);

	/**
	 * Removes config ID for cart entry
	 *
	 * @param cartEntryKey
	 */
	void removeConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Removes all session artifacts belonging to a cart entry
	 *
	 * @param cartEntryId
	 * @param productKey
	 */
	void removeSessionArtifactsForCartEntry(String cartEntryId, String productKey);

	/**
	 * @return Map of names from the hybris classification system
	 */
	Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap();

	/**
	 * @return Set of indexed properties
	 */
	Set<String> getSolrIndexedProperties();

	/**
	 * Stores set of indexed properties
	 *
	 * @param solrTypes
	 */
	void setSolrIndexedProperties(Set<String> solrTypes);

	/**
	 * get the configuration provider for this session
	 *
	 * @return Configuration provider
	 */
	ConfigurationProvider getConfigurationProvider();

	/**
	 * cache the configuration provider in this session
	 *
	 * @param provider
	 *           provider to cache
	 */
	void setConfigurationProvider(ConfigurationProvider provider);

	/**
	 * @param configId
	 * @return Configuration model
	 */
	ConfigModel getConfigurationModelEngineState(String configId);

	/**
	 * Puts the given config model into the engine state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param configModel
	 *           model to cache
	 */
	void setConfigurationModelEngineState(String configId, ConfigModel configModel);

	/**
	 * Removes the given configuration from read cache for engine state
	 *
	 * @param configId
	 *           unique config id
	 */
	void removeConfigurationModelEngineState(String configId);

	/**
	 * Clears the read cache for the configuration engine state for the whole user session
	 */
	void removeConfigurationModelEngineStates();

}
