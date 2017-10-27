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
package de.hybris.platform.cmsfacades.catalogversions.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.catalogversions.CatalogVersionFacade;
import de.hybris.platform.cmsfacades.common.service.ClassFieldFinder;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.DisplayConditionData;
import de.hybris.platform.cmswebservices.data.OptionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facade interface which deals with methods related to catalog version operations.
 */
public class DefaultCatalogVersionFacade implements CatalogVersionFacade
{
	private CatalogVersionService catalogVersionService;
	private Converter<CatalogVersionModel, CatalogVersionData> cmsCatalogVersionConverter;
	private PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry;
	private Set<Class<?>> cmsSupportedPages;

	@Override
	public CatalogVersionData getCatalogVersion(final String catalogId, final String versionId) throws CMSItemNotFoundException
	{
		final CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion(catalogId, versionId);
		// populate basic information : catalog id, name and version
		final CatalogVersionData catalogVersion = getCmsCatalogVersionConverter().convert(catalogVersionModel);

		if (Objects.isNull(catalogVersion))
		{
			throw new CMSItemNotFoundException("Cannot find catalog version");
		}

		// find all page display options per page type
		final Map<String, List<OptionData>> optionDataMap = getCmsSupportedPages().stream()
				.map(clazz -> ClassFieldFinder.getTypeCode(clazz))
				.collect(Collectors.toMap(Function.identity(), typecode -> getDisplayCondition(typecode)));
		final List<DisplayConditionData> displayConditions = optionDataMap.entrySet().stream()
				.map(entry -> convertToDisplayConditionData(entry)).collect(Collectors.toList());
		catalogVersion.setPageDisplayConditions(displayConditions);

		return catalogVersion;
	}

	protected List<OptionData> getDisplayCondition(final String typecode)
	{
		return getCmsPageVariationResolverTypeRegistry().getPageVariationResolverType(typecode).get().getResolver()
				.findDisplayConditions(typecode);
	}

	protected DisplayConditionData convertToDisplayConditionData(final Entry<String, List<OptionData>> optionEntry)
	{
		final DisplayConditionData displayCondition = new DisplayConditionData();
		displayCondition.setTypecode(optionEntry.getKey());
		displayCondition.setOptions(optionEntry.getValue());
		return displayCondition;
	}

	protected Converter<CatalogVersionModel, CatalogVersionData> getCmsCatalogVersionConverter()
	{
		return cmsCatalogVersionConverter;
	}

	@Required
	public void setCmsCatalogVersionConverter(final Converter<CatalogVersionModel, CatalogVersionData> cmsCatalogVersionConverter)
	{
		this.cmsCatalogVersionConverter = cmsCatalogVersionConverter;
	}

	protected PageVariationResolverTypeRegistry getCmsPageVariationResolverTypeRegistry()
	{
		return cmsPageVariationResolverTypeRegistry;
	}

	@Required
	public void setCmsPageVariationResolverTypeRegistry(
			final PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry)
	{
		this.cmsPageVariationResolverTypeRegistry = cmsPageVariationResolverTypeRegistry;
	}

	protected Set<Class<?>> getCmsSupportedPages()
	{
		return cmsSupportedPages;
	}

	@Required
	public void setCmsSupportedPages(final Set<Class<?>> cmsSupportedPages)
	{
		this.cmsSupportedPages = cmsSupportedPages;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
