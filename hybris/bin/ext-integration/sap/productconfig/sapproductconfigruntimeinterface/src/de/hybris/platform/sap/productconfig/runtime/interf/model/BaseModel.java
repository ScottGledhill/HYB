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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.util.Map;


/**
 * Common base interface for all configuration related models includes the extension map.
 */
public interface BaseModel extends Cloneable
{
	/**
	 * @return the extensionMap
	 */
	Map<String, String> getExtensionMap();

	/**
	 * @param extensionMap
	 *           the extensionMap to set
	 */
	void setExtensionMap(final Map<String, String> extensionMap);

	/**
	 * @param key
	 *           the key of the extension data
	 * @param value
	 *           the value of the extension data
	 */
	void putExtensionData(final String key, final String value);

	/**
	 * @param key
	 *           the key of the extension data
	 * @return the value of the extension data
	 */
	String getExtensionData(final String key);

	/**
	 * We need our models to be cloneable since the configurator mock implementation uses clone for creating new
	 * entities.
	 *
	 * @return cloned BaseModel
	 */
	BaseModel clone(); //NOSONAR
}