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
package de.hybris.platform.cmswebservices.pagescontentslotstyperestrictions.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.PageContentSlotTypeRestrictionsFacade;
import de.hybris.platform.cmswebservices.data.ContentSlotTypeRestrictionsData;
import de.hybris.platform.cmswebservices.dto.ContentSlotTypeRestrictionsWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller that provides type restrictions for CMS content slots.
 *
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/catalogs/{catalogId}/versions/{versionId}/pages/{pageId}/contentslots/{slotId}/typerestrictions")
public class PageContentSlotTypeRestrictionController
{
	@Resource
	private PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade;

	@Resource
	private DataMapper dataMapper;

	/**
	 * Get type restriction for a given page id and content slot id.
	 *
	 * @pathparam catalogId Catalog name
	 * @pathparam versionId Catalog version identifier
	 * @pathparam pageId Page identifier
	 * @pathparam slotId Content slot identifier
	 * @return DTO providing the mapping
	 * @throws ContentSlotNotFoundException
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public ContentSlotTypeRestrictionsWsDTO getTypeRestrictionsForContentSlot(final @PathVariable String pageId,
			final @PathVariable String slotId) throws CMSItemNotFoundException
	{
		final ContentSlotTypeRestrictionsData data = getPageContentSlotTypeRestrictionsFacade()
				.getTypeRestrictionsForContentSlotUID(pageId, slotId);

		return getDataMapper().map(data, ContentSlotTypeRestrictionsWsDTO.class);
	}


	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}


	protected PageContentSlotTypeRestrictionsFacade getPageContentSlotTypeRestrictionsFacade()
	{
		return pageContentSlotTypeRestrictionsFacade;
	}

	public void setPageContentSlotTypeRestrictionsFacade(
			final PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade)
	{
		this.pageContentSlotTypeRestrictionsFacade = pageContentSlotTypeRestrictionsFacade;
	}
}
