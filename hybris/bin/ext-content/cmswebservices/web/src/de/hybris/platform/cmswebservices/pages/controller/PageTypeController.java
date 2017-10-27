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
package de.hybris.platform.cmswebservices.pages.controller;

import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmswebservices.data.PageTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to get page types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/pagetypes")
public class PageTypeController
{
	@Resource
	private PageFacade cmsPageFacade;

	/**
	 * Find all page types.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PageTypeListData findAllPageTypes()
	{
		final PageTypeListData pageTypes = new PageTypeListData();
		pageTypes.setPageTypes(getCmsPageFacade().findAllPageTypes());
		return pageTypes;
	}

	protected PageFacade getCmsPageFacade()
	{
		return cmsPageFacade;
	}

	public void setCmsPageFacade(final PageFacade pageFacade)
	{
		this.cmsPageFacade = pageFacade;
	}
}
