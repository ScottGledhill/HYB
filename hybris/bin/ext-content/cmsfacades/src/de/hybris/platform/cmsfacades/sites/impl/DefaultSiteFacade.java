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
package de.hybris.platform.cmsfacades.sites.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.sites.SiteFacade;
import de.hybris.platform.cmswebservices.data.SiteData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SiteFacade}.
 */
public class DefaultSiteFacade implements SiteFacade
{

	private UserService userService;
	private CatalogVersionService catalogVersionService;
	private CMSAdminSiteService cmsAdminSiteService;
	private Converter<CMSSiteModel, SiteData> cmsSiteModelConverter;
	private Comparator<SiteData> siteDataComparator;

	@Override
	public List<SiteData> getAllSiteData()
	{
		Collection<CatalogVersionModel> allWritableCatalogVersions = getCatalogVersionService().getAllWritableCatalogVersions(getUserService().getCurrentUser());
		Set<CatalogModel> allowedCatalogs = allWritableCatalogVersions.stream().map(version -> version.getCatalog()).collect(toSet());
		
		
		final Collection<CMSSiteModel> sites = getCmsAdminSiteService().getSites();
		return sites.stream()
				//will only keep sites containing at least one catalog for which the user has write permissions on the catalog version
				.filter(site -> site.getContentCatalogs().stream().filter(catalog -> allowedCatalogs.contains(catalog)).findAny().isPresent())
				.map((site) -> getCmsSiteModelConverter().convert(site)).sorted(getSiteDataComparator())
				.collect(toList());
	}


	@Required
	public void setSiteDataComparator(final Comparator<SiteData> siteDataComparator)
	{
		this.siteDataComparator = siteDataComparator;
	}

	protected Comparator<SiteData> getSiteDataComparator()
	{
		return siteDataComparator;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsSiteModelConverter(final Converter<CMSSiteModel, SiteData> cmsSiteModelConverter)
	{
		this.cmsSiteModelConverter = cmsSiteModelConverter;
	}

	protected Converter<CMSSiteModel, SiteData> getCmsSiteModelConverter()
	{
		return cmsSiteModelConverter;
	}

	@Required
   public void setCatalogVersionService(CatalogVersionService catalogVersionService)
   {
   	this.catalogVersionService = catalogVersionService;
   }
   
   protected CatalogVersionService getCatalogVersionService()
   {
   	return catalogVersionService;
   }
   
	@Required
   public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
   
   public UserService getUserService()
	{
		return userService;
	}
}
