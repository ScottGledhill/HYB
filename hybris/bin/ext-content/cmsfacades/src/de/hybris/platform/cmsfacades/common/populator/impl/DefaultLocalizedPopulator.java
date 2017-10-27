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
package de.hybris.platform.cmsfacades.common.populator.impl;

import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for the <code>LocalizedPopulator</code> interface.
 */
public class DefaultLocalizedPopulator implements LocalizedPopulator
{
	private LanguageFacade languageFacade;

	@Override
	public <T> void populate(final BiConsumer<Locale, T> consumer, final Function<Locale, T> function)
	{
		getAllLanguages() //
				.map(language -> language.getIsocode().replace("_", "-")).map(Locale::forLanguageTag) //
				.forEach(locale -> consumer.accept(locale, function.apply(locale)));
	}

	@Override
	public String getLanguage(final Locale locale)
	{
		if (Objects.isNull(locale))
		{
			throw new IllegalArgumentException("Locale cannot be null");
		}
		return locale.toLanguageTag();
	}

	/**
	 * Gets all the languages supported.
	 *
	 * @return - a Map containing all the required locales where the keys maps the {@code Locale.getLanguage()} to a
	 *         boolean value, indicating if the language is the default language.
	 */
	protected Stream<LanguageData> getAllLanguages()
	{
		return getLanguageFacade().getLanguages().stream();
	}



	protected LanguageFacade getLanguageFacade()
	{
		return languageFacade;
	}

	@Required
	public void setLanguageFacade(final LanguageFacade languageFacade)
	{
		this.languageFacade = languageFacade;
	}
}
