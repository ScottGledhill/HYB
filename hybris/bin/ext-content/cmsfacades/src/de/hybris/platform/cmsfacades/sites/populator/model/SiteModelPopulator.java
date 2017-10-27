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
package de.hybris.platform.cmsfacades.sites.populator.model;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.cmswebservices.data.SiteData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@Link SiteData} DTO from a {@Link CMSSiteModel}
 */
public class SiteModelPopulator implements Populator<CMSSiteModel, SiteData>
{

	private LocalizedPopulator localizedPopulator;
	private SiteThumbnailResolver siteThumbnailResolver;

	@Override
	public void populate(final CMSSiteModel source, final SiteData target) throws ConversionException
	{
		target.setUid(source.getUid());
		target.setThumbnailUrl(getSiteThumbnailResolver().resolveHomepageThumbnailUrl(source).orElse(null));
		target.setPreviewUrl(source.getPreviewURL());
		target.setRedirectUrl(source.getRedirectURL());

		getLocalizedPopulator().populate(getSiteDataNameSetter(target), getSiteModelNameGetter(source));
	}

	protected Function<Locale, String> getSiteModelNameGetter(final CMSSiteModel source)
	{
		return (locale) -> source.getName(locale);
	}

	protected BiConsumer<Locale, String> getSiteDataNameSetter(final SiteData target)
	{
		return (locale, value) -> {
			if (Objects.isNull(target.getName()))
			{
				target.setName(new LinkedHashMap<>());
			}
			target.getName().put(localizedPopulator.getLanguage(locale), value);
		};
	}


	protected SiteThumbnailResolver getSiteThumbnailResolver()
	{
		return siteThumbnailResolver;
	}

	@Required
	public void setSiteThumbnailResolver(final SiteThumbnailResolver siteThumbnailResolver)
	{
		this.siteThumbnailResolver = siteThumbnailResolver;
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}

}
