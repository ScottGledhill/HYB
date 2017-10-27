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
package de.hybris.platform.cmsfacades.catalogversiondetails.populator;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.jalo.contents.ContentCatalog;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.CatalogVersionDetailData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@Link CatalogVersionData} DTO from a {@Link CMSSiteModel} with either {@link ContentCatalog} or ProductCatalog.
 */
public abstract class AbstractCatalogVersionModelPopulator implements Populator<CMSSiteModel, CatalogVersionData>
{

	private UserService userService;
	private CatalogVersionService catalogVersionService;
	private LocalizedPopulator localizedPopulator;
	private SiteThumbnailResolver siteThumbnailResolver;
	private Comparator<CatalogVersionDetailData> catalogVersionDetailDataComparator;
	
	@Override
	public void populate(final CMSSiteModel source, final CatalogVersionData target) throws ConversionException
	{
		
		Collection<CatalogVersionModel> allWritableCatalogVersions = getCatalogVersionService().getAllWritableCatalogVersions(getUserService().getCurrentUser());
		
		final Optional<String> optionalThumbnailUrl = getSiteThumbnailResolver().resolveHomepageThumbnailUrl(source);

		final Map<String, String> nameMap = Optional.ofNullable(target.getName()).orElseGet(() -> getNewNameMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getName(locale));
		
		target.setUid(source.getUid());
		final Collection<CatalogVersionDetailData> allVersions = new ArrayList<>();
		getCatalogs(source).forEach(catalog -> {
			final Collection<CatalogVersionDetailData> versions = catalog.getCatalogVersions().stream()
					.filter(catalogVersion -> allWritableCatalogVersions.contains(catalogVersion))
					.map((catalogVersion) -> populateCatalogVersionDetailData(source, catalog, catalogVersion, optionalThumbnailUrl))
					.sorted(getCatalogVersionDetailDataComparator()).collect(toList());
			allVersions.addAll(versions);
		});
		
		target.setCatalogVersionDetails(allVersions);
	}

	public abstract List<CatalogModel> getCatalogs(CMSSiteModel source);

	/**
	 * Used to populate the {@link CatalogVersionDetailData} bean.
	 *
	 * @param site
	 *           CMSSiteModel used to populate the DTO
	 * @param catalogModel
	 *           CatalogModel used to populate the DTO
	 * @param catalogVersion
	 * @param homepageThumbnailUrl
	 *           Optional with the homepageThumbnailUrl
	 * @return a CatalogVersionDetailData bean
	 */
	protected CatalogVersionDetailData populateCatalogVersionDetailData(final CMSSiteModel site, final CatalogModel catalogModel,
			final CatalogVersionModel catalogVersion, final Optional<String> homepageThumbnailUrl)
	{
		final CatalogVersionDetailData details = new CatalogVersionDetailData();

		final Map<String, String> nameMap = Optional.ofNullable(details.getName()).orElseGet(() -> getNewNameMap(details));
		getLocalizedPopulator().populate( //
				(locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> catalogModel.getName(locale));

		details.setRedirectUrl(site.getRedirectURL());
		details.setThumbnailUrl(homepageThumbnailUrl.orElse(null));
		details.setCatalogId(catalogModel.getId());
		details.setVersion(catalogVersion.getVersion());
		details.setActive(catalogVersion.getActive());
		return details;
	}

	protected Map<String, String> getNewNameMap(final CatalogVersionData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
	}

	protected Map<String, String> getNewNameMap(final CatalogVersionDetailData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
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

	protected Comparator<CatalogVersionDetailData> getCatalogVersionDetailDataComparator()
	{
		return catalogVersionDetailDataComparator;
	}

	@Required
	public void setCatalogVersionDetailDataComparator(
			final Comparator<CatalogVersionDetailData> catalogVersionDetailDataComparator)
	{
		this.catalogVersionDetailDataComparator = catalogVersionDetailDataComparator;
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
	
	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}
	
	@Required
	public void setCatalogVersionService(CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
	
	protected UserService getUserService()
	{
		return userService;
	}
	
	@Required
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
