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
package de.hybris.platform.cmswebservices.pagescontentslots.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslots.PageContentSlotFacade;
import de.hybris.platform.cmswebservices.data.PageContentSlotListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import java.util.Collections;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller that provides an API to retrieve all pages where a given content slot is present.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslots")
public class PagesContentSlotsController
{
	@Resource
	private PageContentSlotFacade pageContentSlotFacade;

	/**
	 * This resource is deprecated since version 6.3, please use <code>GET /pagescontentslots?pageId={pageId}</code> to retrieve
	 * shared content slot information.
	 * <p>
	 * Retrieve all pages containing the content slot specified by the slot id.
	 * @queryparam slotId Identifier of the slot which the page contains
	 * @return a dto which serves as a wrapper object that contains a list of {@link PageContentSlotListData}; never
	 *         <tt>null</tt>
	 * @deprecated since version 6.3
	 */
	@Deprecated
	@RequestMapping(method = RequestMethod.GET, params = { "slotId" })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody PageContentSlotListData getPagesByContentSlot(@RequestParam("slotId") final String slotId)
	{
		final PageContentSlotListData pageContentSlotList = new PageContentSlotListData();

		try
		{
			pageContentSlotList.setPageContentSlotList(getPageContentSlotFacade().getPagesBySlotId(slotId));
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotList.setPageContentSlotList(Collections.emptyList());
		}
		return pageContentSlotList;
	}

	/**
	 * Retrieve all content slots defined on the page specified by the page id.
	 *
	 * @queryparam pageId Identifier of the page
	 *
	 * @return a dto which serves as a wrapper object that contains a list of {@link PageContentSlotListData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params =
		{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody PageContentSlotListData getContentSlotsByPage(@RequestParam("pageId") final String pageId)
	{
		final PageContentSlotListData pageContentSlotList = new PageContentSlotListData();

		try
		{
			pageContentSlotList.setPageContentSlotList(getPageContentSlotFacade().getContentSlotsByPage(pageId));
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotList.setPageContentSlotList(Collections.emptyList());
		}
		return pageContentSlotList;
	}

	protected PageContentSlotFacade getPageContentSlotFacade()
	{
		return pageContentSlotFacade;
	}

	public void setPageContentSlotFacade(final PageContentSlotFacade pageContentSlotFacade)
	{
		this.pageContentSlotFacade = pageContentSlotFacade;
	}

}
