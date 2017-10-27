/*
* [y] hybris Platform
*
* Copyright (c) 2017 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/
package de.hybris.platform.yacceleratorfractusfulfilmentprocess.order;

import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Thrown when place fractus order is not possible.
 */
public class FractusOrderPlaceException extends BusinessException
{

	public FractusOrderPlaceException(final String message)
	{
		super(message);
	}

	public FractusOrderPlaceException(final Throwable cause)
	{
		super(cause);
	}

	public FractusOrderPlaceException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}