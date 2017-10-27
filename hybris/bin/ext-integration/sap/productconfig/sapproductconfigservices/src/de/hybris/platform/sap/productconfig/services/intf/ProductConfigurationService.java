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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;



/**
 * ProductConfigurationService provides access to the configuration engine implementation.
 *
 */
public interface ProductConfigurationService
{

	/**
	 * Based on the hybris product code, provided via the <code>KBKey.productCode</code>, the configuration engine will
	 * provide a default configuration for the requested product.
	 *
	 * @param kbKey
	 *           The product code for the configurable product
	 * @return The configurable product with default configuration
	 */
	ConfigModel createDefaultConfiguration(final KBKey kbKey);


	/**
	 * Based on the hybris product code, the configuration engine will provide a configuration for the requested product
	 * variant.
	 *
	 * @param baseProductCode
	 *           The product code for the configurable base product
	 * @param variantProductCode
	 *           The product code for the specific product variant
	 * @return The configurable product with default configuration
	 */
	ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode);

	/**
	 * Update the configuration model within the configuration engine.
	 *
	 * @param model
	 *           Updated model
	 */
	void updateConfiguration(final ConfigModel model);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in a <i>XML</i> format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	String retrieveExternalConfiguration(final String configId);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternal(final KBKey kbKey, String externalConfiguration);

	/**
	 * Create a <code>ConfigModel</code> based on a <code>Configuration</code> for the provided product code.
	 *
	 * @param extConfig
	 *           Configuration in a data structure
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(final Configuration extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 */
	void releaseSession(String configId);



	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param model
	 *           Cart Entry
	 * @return Attributes
	 */
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(AbstractOrderEntryModel model);

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param cartEntryKey
	 *           Key of cart entry, derived from {@link PK}
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           External configuration as XML
	 * @return Attributes
	 */
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(String cartEntryKey, String productCode,
			String externalConfiguration);


	/**
	 * Get the number of errors (conflict, not filled mandatory fields), as it is set at the cart item
	 *
	 * @param configId
	 * @return Total number of errors
	 */
	int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId);


	/**
	 * Updates cart entry's base price from configuration model if a price is available in configuration model. ConfigId
	 * has to be present in current session for given cart entry to retrieve configuration model. The caller hat to take
	 * care for triggering recalculate of cart afterwards.
	 *
	 * @param entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's external configuration from configuration model
	 *
	 * @param parameters
	 * @param entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's external configuration and creates configuration in current session from external string
	 * representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry);

	/**
	 * Update the product of the cartItem, if the product is different to the current cart item product
	 *
	 * @param entry
	 *           Entry to change, if necessary
	 * @param product
	 *           cart item product
	 * @param configId
	 *           ID of the current configuration
	 * @return true if the entry was updated
	 */
	boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId);
}
