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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import org.apache.commons.lang.NotImplementedException;


/**
 * ConfigurationProvider provides access to all required interactions with SSC configuration an pricing engine.
 */
public interface ConfigurationProvider
{

	/**
	 * Creates a default configuration for the required knowledge base. The knowledge base (KB) can be identified e.g.
	 * via the product code or via the KB name, version and logical system.
	 *
	 * @param kbKey
	 *           Information needed to identify a knowledge base
	 * @return The configurable product with default configuration
	 */
	ConfigModel createDefaultConfiguration(KBKey kbKey);

	/**
	 * Update the configuration model within the configuration engine.
	 *
	 * @param model
	 *           Updated model
	 */
	boolean updateConfiguration(ConfigModel model);

	/**
	 * Retrieve the current state of the configuration model for the requested <code>configId</code>.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the current state of the configuration for the requested <code>configId</code> as an XML string
	 * containing the configuration in external format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	String retrieveExternalConfiguration(String configId);

	/**
	 * Creates a configuration from the configuration in external format which can be provided from outside, e.g. from
	 * the configuration prepared in the back end
	 *
	 * @param extConfig
	 *           External configuration in external format
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(Configuration extConfig);

	/**
	 * Creates a configuration from an XML string containing the configuration in external format
	 *
	 * @param kbKey
	 *           Information needed to create a knowledge base
	 * @param extConfig
	 *           External configuration as XML string
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(KBKey kbKey, String extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 */
	void releaseSession(String configId);

	/**
	 * Get configuration for the base product, initialized with given configuration of the variant
	 *
	 * @param baseProductCode
	 * @param variantProductCode
	 * @return The pre-filled configuration model
	 */
	default ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		throw new NotImplementedException("This function is not supported by this configuration provider");
	}
}
