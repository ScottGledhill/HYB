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
package de.hybris.platform.sap.productconfig.frontend.constants;

import de.hybris.platform.catalog.enums.ConfiguratorType;


/**
 * Global class for all ysapproductconfigaddon web constants. You can add global constants for your extension into this
 * class.
 */
public final class SapproductconfigfrontendWebConstants
{
	public static final String CONFIG_OVERVIEW_URL = "/configOverview";
	public static final String VARIANT_OVERVIEW_URL = "/variantOverview";
	public static final String CONFIG_URL = "/configuratorPage/" + ConfiguratorType.CPQCONFIGURATOR.toString();

	private SapproductconfigfrontendWebConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
