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
package de.hybris.platform.cmsfacades.catalogversions.populator;


import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default catalog version implementation of {@link Populator}.
 */
public class CatalogVersionModelPopulator implements Populator<CatalogVersionModel, CatalogVersionData>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CatalogVersionModel source, final CatalogVersionData target) throws ConversionException
	{
		final Map<String, String> catalogNameMap = Optional.ofNullable(target.getName()) //
				.orElseGet(() -> getNewCatalogNameMap(target));

		getLocalizedPopulator().populate( //
				(locale, value) -> catalogNameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getCatalog().getName(locale));

		target.setVersion(source.getVersion());
		target.setActive(source.getActive());
		target.setUid(source.getCatalog().getId());
	}

	protected Map<String, String> getNewCatalogNameMap(final CatalogVersionData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
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
