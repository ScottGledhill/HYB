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

import de.hybris.platform.cmsfacades.exception.InvalidNamedQueryException;
import de.hybris.platform.cmsfacades.exception.SearchExecutionNamedQueryException;
import de.hybris.platform.cmsfacades.namedquery.service.NamedQueryFactory;
import de.hybris.platform.cmsfacades.namedquery.service.NamedQueryService;
import de.hybris.platform.cmswebservices.namedquery.NamedQuery;
import de.hybris.platform.cmswebservices.namedquery.NamedQueryConversionDto;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * NamedQueryService implemented as an adapter for Flexible Search Service. This implementation receives requests to
 * perform the search and uses the {@link FlexibleSearchService} service to leverage the final search.
 *
 */
public class FlexibleSearchNamedQueryService implements NamedQueryService
{

	private FlexibleSearchService flexibleSearchService;
	private FlexibleSearchNamedQueryConverter flexibleSearchNamedQueryConverter;
	private NamedQueryFactory namedQueryFactory;

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> search(final NamedQuery namedQuery)
	{
		return ((SearchResult<T>) getSearchResult(namedQuery)).getResult();
	}

	@Override
	public <T> SearchResult<T> getSearchResult(final NamedQuery namedQuery)
			throws InvalidNamedQueryException, SearchExecutionNamedQueryException
	{
		final String query = getNamedQueryFactory().getNamedQuery(namedQuery.getQueryName());

		final NamedQueryConversionDto internalNamedQuery = getInternalNamedQuery(namedQuery, query);

		final FlexibleSearchQuery flexibleSearchQuery = getFlexibleSearchNamedQueryConverter().convert(internalNamedQuery);

		try
		{
			return getFlexibleSearchService().search(flexibleSearchQuery);
		}
		catch (final Exception e)
		{
			throw new SearchExecutionNamedQueryException(
					String.format("Error while executing namedQuery [%s]", namedQuery.getQueryName()), e);
		}
	}

	/**
	 * Creates the internal named query object, which contains the query statement
	 *
	 * @param namedQuery
	 *           the valued object to be used in the assignment
	 * @param query
	 *           the query assigned to this namedQuery
	 * @return an internal named query object, internal to this component.
	 */
	protected NamedQueryConversionDto getInternalNamedQuery(final NamedQuery namedQuery, final String query)
	{
		final NamedQueryConversionDto internalNamedQuery = new NamedQueryConversionDto().withQuery(query) //
				.withNamedQuery(namedQuery);
		return internalNamedQuery;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	protected FlexibleSearchNamedQueryConverter getFlexibleSearchNamedQueryConverter()
	{
		return flexibleSearchNamedQueryConverter;
	}

	@Required
	public void setFlexibleSearchNamedQueryConverter(final FlexibleSearchNamedQueryConverter flexibleSearchNamedQueryConverter)
	{
		this.flexibleSearchNamedQueryConverter = flexibleSearchNamedQueryConverter;
	}

	protected NamedQueryFactory getNamedQueryFactory()
	{
		return namedQueryFactory;
	}

	@Required
	public void setNamedQueryFactory(final NamedQueryFactory namedQueryFactory)
	{
		this.namedQueryFactory = namedQueryFactory;
	}

}

