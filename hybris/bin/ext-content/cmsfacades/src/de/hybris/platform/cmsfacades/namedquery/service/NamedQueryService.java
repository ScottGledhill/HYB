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
package de.hybris.platform.cmsfacades.namedquery.service;

import de.hybris.platform.cmsfacades.exception.InvalidNamedQueryException;
import de.hybris.platform.cmsfacades.exception.SearchExecutionNamedQueryException;
import de.hybris.platform.cmswebservices.namedquery.NamedQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * NamedQuery Service interface for search operations.
 */
public interface NamedQueryService
{

	/**
	 * Performs a search based on the given namedQuery attribute
	 *
	 * @param namedQuery
	 *           - object holding everything related to the current query.
	 * @param <T>
	 *           - The type to be returned
	 * @return the resulting list
	 * @throws InvalidNamedQueryException
	 *            when the queryName does not match any existing named queries
	 * @throws SearchExecutionNamedQueryException
	 *            when an error happens while executing the query on the search engine
	 */
	<T> List<T> search(NamedQuery namedQuery) throws InvalidNamedQueryException, SearchExecutionNamedQueryException;

	/**
	 * Performs a search based on the given namedQuery attribute
	 *
	 * @param namedQuery
	 *           - object holding everything related to the current query.
	 * @param <T>
	 *           - The type to be returned
	 * @return the resulting <code>SearchResult</code>
	 * @throws InvalidNamedQueryException
	 *            when the queryName does not match any existing named queries
	 * @throws SearchExecutionNamedQueryException
	 *            when an error happens while executing the query on the search engine
	 */
	<T> SearchResult<T> getSearchResult(NamedQuery namedQuery)
			throws InvalidNamedQueryException, SearchExecutionNamedQueryException;
}
