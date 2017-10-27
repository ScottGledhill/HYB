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
package de.hybris.platform.cmswebservices.catalogversions.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.catalogversions.CatalogVersionFacade;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller that provides an API to retrieve a catalog version information.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 *
 */
@RestController
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions")
public class CatalogVersionController
{
	@Resource
	private CatalogVersionFacade catalogVersionFacade;

	/**
	 * Retrieve catalog version information for a given catalog and version name.
	 *
	 * @pathparam versionId Catalog version name
	 * @return a {@code CatalogVersionData}
	 * @throws CMSItemNotFoundException
	 *            when the catalog and/or version specified is not valid
	 */
	@RequestMapping(value = "/{versionId}", method = GET)
	public CatalogVersionData getCatalogVersion(@PathVariable("catalogId") final String catalogId,
			@PathVariable("versionId") final String versionId) throws CMSItemNotFoundException
	{
		return getCatalogVersionFacade().getCatalogVersion(catalogId, versionId);
	}

	protected CatalogVersionFacade getCatalogVersionFacade()
	{
		return catalogVersionFacade;
	}

	public void setCatalogVersionFacade(final CatalogVersionFacade catalogVersionFacade)
	{
		this.catalogVersionFacade = catalogVersionFacade;
	}

}
