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
package de.hybris.platform.cmsfacades.namedquery.validator;

import de.hybris.platform.cmsfacades.exception.InvalidNamedQueryException;
import de.hybris.platform.cmsfacades.namedquery.service.NamedQueryFactory;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given query name maps to an existing named query.
 * <p>
 * Returns <tt>TRUE</tt> if the named query exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class NamedQueryExistsPredicate implements Predicate<String>
{
	private NamedQueryFactory namedQueryFactory;

	@Override
	public boolean test(final String target)
	{
		boolean result = true;
		try
		{
			getNamedQueryFactory().getNamedQuery(target);
		}
		catch (final InvalidNamedQueryException e)
		{
			result = false;
		}
		return result;
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
