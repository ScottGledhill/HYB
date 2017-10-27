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
package de.hybris.platform.cmsfacades.exception;

/**
 * Exception used when the {@link NamedQueryFactory}  implementation does not match any existing named queries
 */
public class InvalidNamedQueryException extends RuntimeException
{
	private static final long serialVersionUID = -6615030091084809142L;

	private final String queryName;

	public InvalidNamedQueryException(final String queryName) {
		super("Named Query is not present in the configuration.");
		this.queryName = queryName;
	}

	public String getQueryName()
	{
		return queryName;
	}
}
