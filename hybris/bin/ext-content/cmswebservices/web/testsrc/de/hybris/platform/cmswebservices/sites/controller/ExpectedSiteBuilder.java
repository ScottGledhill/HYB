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

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;

import de.hybris.platform.cmsfacades.sites.populator.model.ComparableSiteData;
import de.hybris.platform.cmswebservices.data.SiteData;

public class ExpectedSiteBuilder {

	public static ComparableSiteData buildApparel() {
		final SiteData sitedata = new SiteData();
		sitedata.setName(APPAREL.getNames());
		sitedata.setUid(APPAREL.getUid());
		sitedata.setThumbnailUrl(APPAREL.getThumbnailUri());
		sitedata.setRedirectUrl(APPAREL.getBaseUrl());
		return new ComparableSiteData(sitedata);
	}
}
