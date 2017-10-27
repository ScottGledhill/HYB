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
 * Exception used when there is an error while executing the search.
 */
public class SearchExecutionNamedQueryException extends RuntimeException
{
	private static final long serialVersionUID = -5513694400462128765L;

	public SearchExecutionNamedQueryException()
	{
	}

	public SearchExecutionNamedQueryException(final String message)
	{
		super(message);
	}

	public SearchExecutionNamedQueryException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
