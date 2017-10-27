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
package de.hybris.platform.cmsfacades.catalogversiondetails;

import de.hybris.platform.cmswebservices.data.CatalogVersionData;


/**
 * simplification of CatalogVersionDetailData related interactions.
 */
public interface CatalogVersionDetailFacade
{
	/**
	 * Given a site uid this method will return a List of all the configured pairs of permitted content catalogs and versions.
	 * @deprecated since version 6.3, use {@link CatalogVersionDetailFacade#getContentCatalogVersionDetailDataForSite(String)}
	 * @param siteUid
	 *           the site uid
	 *
	 * @return All catalog and version pairs that are configured for a site; never <tt>null</tt>
	 */
	@Deprecated
	CatalogVersionData getCatalogVersionDetailDataForSite(String siteUid);
	
	/**
	 * Given a site uid this method will return a List of all the configured pairs of permitted content catalogs and versions.
	 * @param siteUid
	 *           the site uid
	 *
	 * @return All catalog and version pairs that are configured for a site; never <tt>null</tt>
	 */
	CatalogVersionData getContentCatalogVersionDetailDataForSite(String siteUid);

	/**
	 * Given a site uid this method will return a List of all the configured pairs of permitted product catalogs and versions.
	 * @param siteUid
	 *           the site uid
	 *
	 * @return All catalog and version pairs that are configured for a site; never <tt>null</tt>
	 */
	CatalogVersionData getProductCatalogVersionDetailDataForSite(String siteUid);

}
