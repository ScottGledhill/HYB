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
package de.hybris.platform.cmsfacades.pages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageTypeData;

import java.util.List;


/**
 * Component facade interface which deals with methods related to page operations.
 */
public interface PageFacade
{

	/**
	 * Find all pages.
	 *
	 * @return list of {@link AbstractPageData} ordered by title ascending; never <tt>null</tt>
	 */
	List<AbstractPageData> findAllPages();

	/**
	 * Find all page types.
	 *
	 * @return list of all {@link PageTypeData}; never <code>null</code>
	 */
	List<PageTypeData> findAllPageTypes();

	/**
	 * Find all default or variant pages for a given page type.
	 *
	 * @param typeCode
	 *           - the page typecode
	 * @param isDefaultPage
	 *           - set to true to find all default pages; set to false to find all variant pages
	 * @return list of default or variant {@link AbstractPageData} ordered by name ascending; never <tt>null</tt>
	 */
	List<AbstractPageData> findPagesByType(String typeCode, Boolean isDefaultPage);

	/**
	 * Find all variant pages for a given page.
	 *
	 * @param pageId
	 *           - the page identifier
	 * @return list of variation page uids; empty if the given page is already a variation page; never <tt>null</tt>
	 */
	List<String> findVariationPages(String pageId) throws CMSItemNotFoundException;

	/**
	 * Find all default pages for a given page.
	 *
	 * @param pageId
	 *           - the page identifier
	 * @return list of default page uids; empty if the given page is already a default page; never <tt>null</tt>
	 */
	List<String> findFallbackPages(String pageId) throws CMSItemNotFoundException;

	/**
	 * Find a single page by its uid.
	 *
	 * @param uid
	 *           - the uid of the page to retrieve.
	 * @return the page matching the given uid
	 * @throws CMSItemNotFoundException
	 *            when the page could not be found
	 */
	AbstractPageData getPageByUid(String uid) throws CMSItemNotFoundException;

	/**
	 * Adds a new page
	 *
	 * @param pageData
	 *           the {@link AbstractPageData}
	 *
	 * @return the updated {@link AbstractPageData}
	 * @throws ValidationException
	 *            if there are validations errors
	 */
	AbstractPageData createPage(AbstractPageData pageData) throws ValidationException;

	/**
	 * Update a page
	 *
	 * @param pageId
	 *           the pageId
	 *
	 * @param pageData
	 *           the {@link AbstractPageData}
	 * @return the updated {@link AbstractPageData}
	 * @throws ValidationException
	 *            if there are validations errors
	 */
	AbstractPageData updatePage(String pageId, AbstractPageData pageData);

}
