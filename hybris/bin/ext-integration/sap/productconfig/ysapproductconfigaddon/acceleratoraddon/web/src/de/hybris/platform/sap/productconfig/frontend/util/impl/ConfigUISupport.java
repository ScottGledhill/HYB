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

import de.hybris.platform.sap.productconfig.facades.CsticData;

import java.util.List;


/**
 * Utility class to be called directly from the xhtml UI-components (tags)
 */
@SuppressWarnings("squid:S1118")
public class ConfigUISupport
{
	/**
	 * @param cstics
	 *           list of cstics
	 * @return <code>true</code>, only if the list of cstics has at least one required cstic
	 */
	public static boolean hasRequiredCstic(final List<CsticData> cstics)
	{
		if (cstics == null)
		{
			return false;
		}

		return cstics.parallelStream().anyMatch(cstic -> cstic.isRequired());
	}
}
