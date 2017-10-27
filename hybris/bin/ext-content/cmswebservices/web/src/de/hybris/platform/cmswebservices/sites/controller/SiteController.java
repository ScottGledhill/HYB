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
package de.hybris.platform.cmswebservices.sites.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import de.hybris.platform.cmsfacades.sites.SiteFacade;
import de.hybris.platform.cmswebservices.data.SiteListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to support the sites end point.
 */
@RestController
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites")
public class SiteController {

	@Resource
	private SiteFacade siteFacade;

	/**
	 * Get all sites.
	 *
	 * @return A list of all sites configured; never <tt>null</tt>
	 */
	@RequestMapping(method = GET)
	public SiteListData getAllSites() {
		final SiteListData siteListData = new SiteListData();
		siteListData.setSites(getSiteFacade().getAllSiteData());
		return siteListData;
	}

	public SiteFacade getSiteFacade() {
		return siteFacade;
	}

	public void setSiteFacade(final SiteFacade siteFacade) {
		this.siteFacade = siteFacade;
	}
}
