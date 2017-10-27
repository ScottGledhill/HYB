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
package de.hybris.platform.cmswebservices.pagesrestrictions.controller;

import de.hybris.platform.cmsfacades.pagetypesrestrictiontypes.PageTypeRestrictionTypeFacade;
import de.hybris.platform.cmswebservices.data.PageTypeRestrictionTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller that provides an API to retrieve all pages types and their restrictions types.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/pagetypesrestrictiontypes")
public class PageTypesRestrictionTypesController
{

	@Resource
	private PageTypeRestrictionTypeFacade pageTypesRestrictionTypesFacade;

	/**
	 * Retrieve all restrictions types for all page types.
	 *
	 * @return a dto which serves as a wrapper object that contains a list of {@link PageRestrictionListData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public PageTypeRestrictionTypeListData getRestrictionTypesByPageType()
	{
		final PageTypeRestrictionTypeListData pageTypesRestrictionTypesList = new PageTypeRestrictionTypeListData();
		pageTypesRestrictionTypesList
		.setPageTypeRestrictionTypeList(getPageTypesRestrictionTypesFacade().getRestrictionTypesForAllPageTypes());
		return pageTypesRestrictionTypesList;
	}

	protected PageTypeRestrictionTypeFacade getPageTypesRestrictionTypesFacade()
	{
		return pageTypesRestrictionTypesFacade;
	}

	public void setPageTypesRestrictionTypesFacade(final PageTypeRestrictionTypeFacade pageTypesRestrictionTypesFacade)
	{
		this.pageTypesRestrictionTypesFacade = pageTypesRestrictionTypesFacade;
	}

}
