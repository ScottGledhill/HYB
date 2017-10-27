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



/**
 * Retrieves an instance of the configuration provider according to the hybris application configuration.
 */
public interface ConfigurationProviderFactory
{

	/**
	 * Retrieves an instance of the configuration provider according to the hybris application configuration.
	 *
	 * @return instance of the configuration provider
	 */
	ConfigurationProvider getProvider();
}
