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
package de.hybris.platform.cmswebservices.constants;

/**
 * Global class for all Cmswebservices constants. You can add global constants for your extension into this class.
 */
public final class CmswebservicesConstants extends GeneratedCmswebservicesConstants
{
	public static final String EXTENSIONNAME = "cmswebservices";

	private CmswebservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String URI_CATALOG_ID = "catalogId";
	public static final String URI_VERSION_ID = "versionId";
	public static final String URI_SITE_ID = "siteId";
	public static final String URI_PAGE_ID = "pageId";
	public static final String URI_PAGE_IDS = "pageIds";
	public static final String URI_SLOT_ID = "slotId";

	public static final String HEADER_LOCATION = "Location";

	/**
	 * This is not used.
	 *
	 * @deprecated since version 6.3
	 */
	@Deprecated
	public static final String INVALID_POSITION = "position.invalid";

	/**
	 * This is not used.
	 *
	 * @deprecated since version 6.3
	 */
	@Deprecated
	public static final String HEADER_AUTHORIZATION = "Authorization";

}
