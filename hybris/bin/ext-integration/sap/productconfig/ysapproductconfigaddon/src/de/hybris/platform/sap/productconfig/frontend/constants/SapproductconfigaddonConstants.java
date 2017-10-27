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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;


@SuppressWarnings("PMD")
public class SapproductconfigaddonConstants extends GeneratedSapproductconfigaddonConstants
{
	@SuppressWarnings("squid:S2387")
	public static final String EXTENSIONNAME = "ysapproductconfigaddon";
	public static final String CONFIG_ATTRIBUTE = "config";
	public static final String OVERVIEW_ATTRIBUTE = "overview";
	public static final String OVERVIEWUIDATA_ATTRIBUTE = "overviewUiData";

	/**
	 * If this method is available at the OrderEntryDTO, we assume that the UI is prepared to render the configuration
	 * link
	 *
	 * @see OrderEntryData
	 */
	public static final String CONFIGURABLE_SOM_DTO_METHOD = "isConfigurable";

	public static final String VARIANT_SEARCH_RESULT = "variantSearchResult";

	private SapproductconfigaddonConstants()
	{
		//empty
	}


}
