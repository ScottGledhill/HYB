/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.catalogversiondetails;


import de.hybris.platform.cmswebservices.data.CatalogVersionDetailData;

import java.util.Comparator;


/**
 * Implementation of a {@link Comparator} which uses the natural ordering of uid in a {@link CatalogVersionDetailData}
 * dto.
 */
public class CatalogVersionDetailDataCatalogIdComparator implements Comparator<CatalogVersionDetailData>
{

	@Override
	public int compare(final CatalogVersionDetailData that, final CatalogVersionDetailData other)
	{
		return that.getCatalogId().compareTo(other.getCatalogId());
	}
}
