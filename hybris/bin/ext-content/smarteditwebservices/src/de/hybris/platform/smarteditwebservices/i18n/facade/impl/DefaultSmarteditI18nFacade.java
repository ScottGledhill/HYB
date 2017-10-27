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
package de.hybris.platform.smarteditwebservices.i18n.facade.impl;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.smarteditwebservices.configuration.facade.DefaultConfigurationKey.DEFAULT_LANGUAGE;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.forLanguageTag;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.remove;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.smarteditwebservices.configuration.facade.SmarteditConfigurationFacade;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
import de.hybris.platform.smarteditwebservices.data.SmarteditLanguageData;
import de.hybris.platform.smarteditwebservices.i18n.facade.SmarteditI18nFacade;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SmarteditI18nFacade} to retrieve cms management data
 */
public class DefaultSmarteditI18nFacade implements SmarteditI18nFacade
{
	private L10NService l10nService;
	private I18NService i18nService;
	private AbstractPopulatingConverter<Locale, SmarteditLanguageData> smarteditLanguageConverter;
	private SmarteditConfigurationFacade smarteditConfigurationFacade;
	private Comparator<SmarteditLanguageData> smarteditLanguageDataComparator;

	@Override
	public Map<String, String> getTranslationMap(final Locale locale)
	{
		final Optional<ConfigurationData> configurationData = getSmarteditConfigurationFacade()
				.tryAndFindByDefaultConfigurationKey(DEFAULT_LANGUAGE);
		final Locale defaultLanguage = configurationData.isPresent()
				? forLanguageTag(remove(configurationData.get().getValue(), '"')) : ENGLISH;
		final Map<String, String> defaultMap = resolveLanguageMap(defaultLanguage);

		if (!languageIsSupported(locale))
		{
			return defaultMap;
		}

		final Map<String, String> languageMap = resolveLanguageMap(locale);
		final Map<String, String> aggregatedLanguageMap = (defaultMap == null) ? newHashMap() : defaultMap;
		safePutAll(aggregatedLanguageMap, languageMap);
		return aggregatedLanguageMap;
	}

	/**
	 * Checks the supported {@link Locale} languages against the {@link Locale} language
	 *
	 * @param locale
	 *           {@link Locale} to check
	 * @return is the language of the {@link Locale} locale supported
	 */
	protected boolean languageIsSupported(final Locale locale)
	{
		return getI18nService().getSupportedLocales().parallelStream()
				.anyMatch(supportedLocale -> StringUtils.equals(supportedLocale.getLanguage(), locale.getLanguage()));
	}

	/**
	 * Will put the entries of the sourceMap into the targetMap if the {@link java.util.Map.Entry} of the sourceMap has a
	 * value that is not null, empty, ot blank
	 *
	 * @param targetMap
	 *           The {@link Map} to be populated
	 * @param sourceMap
	 *           The {@link Map} to be populated from
	 */
	protected void safePutAll(final Map<String, String> targetMap, final Map<String, String> sourceMap)
	{
		sourceMap.keySet().parallelStream().filter(key -> isNotBlank(sourceMap.get(key)))
				.forEach(key -> targetMap.put(key, sourceMap.get(key)));
	}

	@Override
	public List<SmarteditLanguageData> getSupportedLanguages()
	{
		return ofNullable(getI18nService().getSupportedLocales()).orElse(newHashSet()).stream()
				.map(locale -> getSmarteditLanguageConverter().convert(locale))
				.sorted((left, right) -> getSmarteditLanguageDataComparator().compare(left, right)).collect(Collectors.toList());
	}

	protected Map<String, String> resolveLanguageMap(final Locale locale)
	{
		final ResourceBundle resourceBundle = getL10nService().getResourceBundle(getI18nService().getAllLocales(locale));
		return resourceBundle.keySet().stream().collect(toMap(key -> key, resourceBundle::getString));
	}

	protected L10NService getL10nService()
	{
		return l10nService;
	}

	@Required
	public void setL10nService(final L10NService l10nService)
	{
		this.l10nService = l10nService;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected AbstractPopulatingConverter<Locale, SmarteditLanguageData> getSmarteditLanguageConverter()
	{
		return smarteditLanguageConverter;
	}

	@Required
	public void setSmarteditLanguageConverter(
			final AbstractPopulatingConverter<Locale, SmarteditLanguageData> smarteditLanguageConverter)
	{
		this.smarteditLanguageConverter = smarteditLanguageConverter;
	}

	protected SmarteditConfigurationFacade getSmarteditConfigurationFacade()
	{
		return smarteditConfigurationFacade;
	}

	@Required
	public void setSmarteditConfigurationFacade(final SmarteditConfigurationFacade smarteditConfigurationFacade)
	{
		this.smarteditConfigurationFacade = smarteditConfigurationFacade;
	}

	public Comparator<SmarteditLanguageData> getSmarteditLanguageDataComparator()
	{
		return smarteditLanguageDataComparator;
	}

	@Required
	public void setSmarteditLanguageDataComparator(final Comparator<SmarteditLanguageData> smarteditLanguageDataComparator)
	{
		this.smarteditLanguageDataComparator = smarteditLanguageDataComparator;
	}

}
