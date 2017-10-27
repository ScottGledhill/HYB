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
package de.hybris.platform.sap.productconfig.frontend.util.impl;


/**
 * Utility class granting access to UI-Connstants directly to the xhtml UI-components (tags)
 */
@SuppressWarnings("squid:S1118")
public class ConstantHandler
{
	/**
	 * General group, used for all cstic, which are not assigned to another group
	 */
	public static final String GENERAL_GROUP_NAME = "_GEN";
	public static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";

	/**
	 * @return name of the general group
	 */
	public static String getGeneralGroupName()
	{
		return GENERAL_GROUP_NAME;
	}

	/**
	 * @return UI-type name for non implemented cstics
	 */
	public static String getNotImplemented()
	{
		return NOT_IMPLEMENTED;
	}
}
