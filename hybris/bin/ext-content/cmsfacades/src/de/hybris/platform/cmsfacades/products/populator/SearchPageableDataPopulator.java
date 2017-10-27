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
package de.hybris.platform.cmsfacades.products.populator;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates a {@link de.hybris.platform.commerceservices.search.pagedata.PageableData} instance from the {@link PageableData} source data model. 
 */
public class SearchPageableDataPopulator implements Populator<PageableData, de.hybris.platform.commerceservices.search.pagedata.PageableData>
{
	@Override
	public void populate(final PageableData source, final de.hybris.platform.commerceservices.search.pagedata.PageableData target) throws ConversionException
	{
		target.setCurrentPage(source.getCurrentPage());
		target.setPageSize(source.getPageSize());
		target.setSort(source.getSort());
	}

}
