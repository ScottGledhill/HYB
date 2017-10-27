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

import org.springframework.validation.Errors;


/**
 * Exception thrown when there is any problem when validating request data.
 */
public class ValidationException extends RuntimeException
{
	private static final long serialVersionUID = 5922002536003254842L;

	protected Errors validationObject;

	public ValidationException(final Errors validationObject)
	{
		super("Validation error");
		this.validationObject = validationObject;
	}

	public Errors getValidationObject()
	{
		return validationObject;
	}
}
