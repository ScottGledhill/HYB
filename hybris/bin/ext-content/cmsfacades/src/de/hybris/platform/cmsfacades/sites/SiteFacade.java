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
package de.hybris.platform.cmsfacades.sites;


import de.hybris.platform.cmswebservices.data.SiteData;

import java.util.List;


/**
 * simplification of site related interactions.
 */
public interface SiteFacade
{


	/**
	 * Lists all sites that have been configured.
	 *
	 * @return All sites that are configured; never <tt>null</tt>
	 */
	List<SiteData> getAllSiteData();

}
