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
package de.hybris.platform.cmsfacades.common.validator.impl;


import de.hybris.platform.cmsfacades.common.validator.LocalizedTypeValidator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;


/**
 * Default validator to use for validating localized attributes of type media. This implementation uses the
 * {@link MediaService} and the {@link CMSAdminSiteService} to verify whether a given media is valid or not.
 */
public class LocalizedStringValidator implements LocalizedTypeValidator
{

	@Override
	public void validate(final String language, final String fieldName, final String value, final Errors errors)
	{
		if (StringUtils.isEmpty(value))
		{
			reject(language, fieldName, CmsfacadesConstants.FIELD_REQUIRED_L10N, errors);
		}
	}

	@Override
	public void reject(final String language, final String fieldName, final String errorCode, final Errors errors)
	{
		errors.rejectValue(fieldName, errorCode, new Object[]
		{ language }, null);
	}

}
