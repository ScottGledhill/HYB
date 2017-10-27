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

import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;


@SuppressWarnings("squid:S1118")
public class ConfigOverviewFilterEvaluator
{

	public static boolean hasAppliedFilters(final OverviewUiData overviewData)
	{
		return overviewData.getCsticFilterList().stream().anyMatch(filter -> filter.isSelected())
				|| overviewData.getGroupFilterList().stream().anyMatch(filter -> filter.isSelected());
	}
}