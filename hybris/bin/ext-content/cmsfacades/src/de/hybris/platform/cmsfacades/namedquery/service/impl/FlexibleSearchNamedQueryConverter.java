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
package de.hybris.platform.cmsfacades.namedquery.service.impl;

import de.hybris.platform.cmswebservices.namedquery.NamedQueryConversionDto;
import de.hybris.platform.cmswebservices.namedquery.Sort;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;


/**
 * Default Converter used by {@link FlexibleSearchNamedQueryService} to build the final FlexibleSearchQuery object.
 */
public class FlexibleSearchNamedQueryConverter implements Converter<NamedQueryConversionDto, FlexibleSearchQuery>
{
	protected static final String ORDER_BY = " ORDER BY ";
	protected static final Integer DEFAULT_INITIAL_PAGE = 0;
	protected static final String COMMA = ", ";
	protected static final String SPACE = " ";
	protected static final String EMPTY_STRING = "";
	protected static final String OPEN_BRACKET = "{";
	protected static final String CLOSE_BRACKET = "}";

	private Integer defaultPageSize;


	@Override
	public FlexibleSearchQuery convert(final NamedQueryConversionDto namedQueryConversion) throws ConversionException
	{
		final String query = namedQueryConversion.getQuery() + appendSort(namedQueryConversion.getNamedQuery().getSort());
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query,
				namedQueryConversion.getNamedQuery().getParameters());

		setFlexibleSearchStartAndCount(flexibleSearchQuery, namedQueryConversion.getNamedQuery().getCurrentPage(),
				namedQueryConversion.getNamedQuery().getPageSize());

		return flexibleSearchQuery;
	}

	/**
	 * Append the order by clause if the Sort list is not empty. If sort list is empty, return an empty String.
	 *
	 * @param sort
	 *           - a list of {@link Sort} objects
	 * @return the order by query or empty, if no sort clause is provided
	 */
	protected String appendSort(final List<Sort> sort)
	{
		if (!CollectionUtils.isEmpty(sort))
		{
			return ORDER_BY + StringUtils.join(
					sort.stream() //
							.map(e -> OPEN_BRACKET + e.getParameter() + CLOSE_BRACKET + SPACE + e.getDirection().name()) //
							.collect(Collectors.toList()) //
					, COMMA);
		}
		return EMPTY_STRING;
	}

	/**
	 * Set the requested currentPage and pageSize or use the default values.
	 *
	 * @param flexibleSearchQuery
	 * @param requestedCurrentPage
	 * @param requestedPageSize
	 */
	protected void setFlexibleSearchStartAndCount(final FlexibleSearchQuery flexibleSearchQuery,
			final Integer requestedCurrentPage, final Integer requestedPageSize)
	{
		Integer page = DEFAULT_INITIAL_PAGE;
		Integer pageSize = defaultPageSize;

		if (requestedCurrentPage != null && requestedCurrentPage > 0)
		{
			page = requestedCurrentPage;
		}
		if (requestedPageSize != null)
		{
			pageSize = requestedPageSize;
		}
		// assuming that the currentPage is always greater or equals than zero, i.e. pageSize >= 0
		final int start = page * pageSize;
		flexibleSearchQuery.setStart(start);
		flexibleSearchQuery.setCount(pageSize);
		flexibleSearchQuery.setNeedTotal(true);
	}

	@Required
	public void setDefaultPageSize(final Integer defaultPageSize)
	{
		this.defaultPageSize = defaultPageSize;
	}

	protected Integer getDefaultPageSize()
	{
		return defaultPageSize;
	}
}
