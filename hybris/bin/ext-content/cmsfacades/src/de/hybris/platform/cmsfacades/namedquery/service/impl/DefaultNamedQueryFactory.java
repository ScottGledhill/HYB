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
import de.hybris.platform.cmsfacades.namedquery.service.NamedQueryFactory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation NamedQueryFactory interface, which holds a named query definition map. For each NamedQuery
 * search domain (e.g. Media), there should be a separate Spring bean with the named queries definitions.
 */
public class DefaultNamedQueryFactory implements NamedQueryFactory
{

	private Map<String, String> namedQueryMap;

	@Override
	public String getNamedQuery(final String queryName) throws InvalidNamedQueryException
	{
		if (!namedQueryMap.containsKey(queryName))
		{
			throw new InvalidNamedQueryException(queryName);
		}

		return namedQueryMap.get(queryName);
	}

	@Required
	public void setNamedQueryMap(final Map<String, String> namedQueryMap)
	{
		this.namedQueryMap = namedQueryMap;
	}

	protected Map<String, String> getNamedQueryMap()
	{
		return namedQueryMap;
	}
}
