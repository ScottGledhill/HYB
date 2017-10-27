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
package de.hybris.platform.cmsfacades.common.validator;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmswebservices.dto.ComponentAndContentSlotValidationDto;
import de.hybris.platform.cmswebservices.dto.ComponentTypeAndContentSlotValidationDto;

import org.springframework.beans.factory.annotation.Required;


/**
 * Factory for creating beans used for validation.
 */
public class ValidationDtoFactory
{
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	private CMSAdminComponentService cmsAdminComponentService;
	private CMSAdminPageService cmsAdminPageService;

	/**
	 * Build a new DTO for use with predicates.
	 *
	 * @param componentUid
	 * @param contentSlotUid
	 * @return the new DTO
	 * @throws UnknownIdentifierException
	 *            when the component or the slot cannot be found
	 * @throws AmbiguousIdentifierException
	 *            when more than one component or slot is found
	 */
	public ComponentAndContentSlotValidationDto buildComponentAndContentSlotValidationDto(final String componentUid,
			final String contentSlotUid)
	{
		final ContentSlotModel contentSlot = getCmsAdminContentSlotService().getContentSlotForId(contentSlotUid);
		final AbstractCMSComponentModel component = getCmsAdminComponentService().getCMSComponentForId(componentUid);

		final ComponentAndContentSlotValidationDto dto = new ComponentAndContentSlotValidationDto();
		dto.setComponent(component);
		dto.setContentSlot(contentSlot);
		return dto;
	}

	/**
	 * Build a new DTO for use with predicates.
	 *
	 * @param typeCode
	 * @param contentSlotUid
	 *           the content slot id
	 * @param pageId
	 *           the page id
	 * @return the new DTO
	 * @throws UnknownIdentifierException
	 *            when the slot cannot be found
	 * @throws AmbiguousIdentifierException
	 *            when more than one slot is found
	 */
	public ComponentTypeAndContentSlotValidationDto buildComponentTypeAndContentSlotValidationDto(final String typeCode,
			final String contentSlotUid, final String pageId)
	{
		final ContentSlotModel contentSlot = getCmsAdminContentSlotService().getContentSlotForId(contentSlotUid);
		final AbstractPageModel page = getCmsAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		final ComponentTypeAndContentSlotValidationDto dto = new ComponentTypeAndContentSlotValidationDto();
		dto.setComponentType(typeCode);
		dto.setContentSlot(contentSlot);
		dto.setPage(page);
		return dto;
	}

	protected CMSAdminContentSlotService getCmsAdminContentSlotService()
	{
		return cmsAdminContentSlotService;
	}

	@Required
	public void setCmsAdminContentSlotService(final CMSAdminContentSlotService cmsAdminContentSlotService)
	{
		this.cmsAdminContentSlotService = cmsAdminContentSlotService;
	}

	protected CMSAdminComponentService getCmsAdminComponentService()
	{
		return cmsAdminComponentService;
	}

	@Required
	public void setCmsAdminComponentService(final CMSAdminComponentService cmsAdminComponentService)
	{
		this.cmsAdminComponentService = cmsAdminComponentService;
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
	}

}
