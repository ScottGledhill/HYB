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
package de.hybris.platform.cmsfacades.items.validator;

import de.hybris.platform.cmsfacades.common.validator.LocalizedTypeValidator;
import de.hybris.platform.cmsfacades.common.validator.LocalizedValidator;
import de.hybris.platform.cmswebservices.data.CMSLinkComponentData;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates fields of {@link CMSLinkComponentData}
 */
public class LinkComponentValidator implements Validator
{

	private static final String LINK_NAME = "linkName";

	private LocalizedValidator localizedValidator;
	private LocalizedTypeValidator localizedStringValidator;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CMSLinkComponentData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final CMSLinkComponentData target = (CMSLinkComponentData) obj;

		final Function<String, String> linkNameGetter = (language) -> Optional.ofNullable(target.getLinkName())
				.orElse(Collections.emptyMap()).get(language);
		getLocalizedValidator().validateRequiredLanguages(//
				(language, value) -> getLocalizedStringValidator().validate(language, LINK_NAME, value, errors), //
				linkNameGetter, errors);

	}

	protected LocalizedTypeValidator getLocalizedStringValidator()
	{
		return localizedStringValidator;
	}

	@Required
	public void setLocalizedStringValidator(final LocalizedTypeValidator localizedStringValidator)
	{
		this.localizedStringValidator = localizedStringValidator;
	}

	protected LocalizedValidator getLocalizedValidator()
	{
		return localizedValidator;
	}

	@Required
	public void setLocalizedValidator(final LocalizedValidator localizedValidator)
	{
		this.localizedValidator = localizedValidator;
	}



}
