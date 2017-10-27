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
package de.hybris.platform.cmswebservices.pagescontentslotscomponents.controller;

import static java.util.Collections.emptyList;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ComponentNotAllowedInSlotException;
import de.hybris.platform.cmsfacades.exception.ComponentNotFoundInSlotException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pagescontentslotscomponents.PageContentSlotComponentFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentData;
import de.hybris.platform.cmswebservices.data.PageContentSlotComponentListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller that provides an API to update components either between slots, or within a single slot.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscomponents")
public class PageContentSlotComponentController
{
	@Resource
	private PageContentSlotComponentFacade pageContentSlotComponentFacade;
	@Resource
	private LocationHeaderResource locationHeaderResource;

	/**
	 * Fetches all components on a given page.
	 *
	 * @queryparam pageId Identifier of the page
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody PageContentSlotComponentListData getComponentsByPage(@RequestParam("pageId") final String pageId)
	{
		final PageContentSlotComponentListData pageContentSlotComponentList = new PageContentSlotComponentListData();

		try
		{
			pageContentSlotComponentList
			.setPageContentSlotComponentList(getPageContentSlotComponentFacade().getPageContentSlotComponentsByPageId(pageId));
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotComponentList.setPageContentSlotComponentList(emptyList());
		}
		return pageContentSlotComponentList;
	}

	/**
	 * This resource is deprecated since version 6.2, please use
	 * <code>PUT /pagescontentslotscomponents/pages/{pageId}/contentslots/{slotId}/components/{componentId}</code>
	 * instead. <br>
	 * <br>
	 *
	 * Update a component by changing the content slot where it is placed inside a page.
	 *
	 * @queryparam pageId Identifier of the page which contains the component
	 * @queryparam slotId Identifier of the initial content slot containing the component to be moved
	 * @queryparam componentId Identifier of the component to be moved
	 * @bodyparam pageContentSlotComponent the {@link PageContentSlotComponentData}
	 * @throws CMSItemNotFoundException
	 * @throws ComponentNotAllowedInSlotException
	 * @throws WebserviceValidationException
	 *            if there validation errors.
	 *
	 * @deprecated since version 6.2, please use
	 *             {@link #moveComponent(pageId, slotId, componentId, pageContentSlotComponent)}
	 */
	@Deprecated
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody PageContentSlotComponentData updatePageContentSlotComponent(@RequestParam("pageId") final String pageId,
			@RequestParam("slotId") final String slotId, @RequestParam("componentId") final String componentId,
			@RequestBody final PageContentSlotComponentData pageContentSlotComponent)
					throws CMSItemNotFoundException, ComponentNotAllowedInSlotException, WebserviceValidationException
	{
		return moveComponent(pageId, slotId, componentId, pageContentSlotComponent);
	}

	/**
	 * Move a component to a different content slot or by changing its position within the content slot on a given page.
	 *
	 * @pathparam pageId Page identifier
	 * @pathparam slotId Content slot identifier
	 * @pathparam componentId Component identifier
	 * @bodyparam pageContentSlotComponent the {@link PageContentSlotComponentData}
	 *
	 * @throws CMSItemNotFoundException
	 * @throws ComponentNotAllowedInSlotException
	 * @throws WebserviceValidationException
	 *            if there validation errors.
	 */
	@RequestMapping(value = "/pages/{pageId}/contentslots/{slotId}/components/{componentId}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public PageContentSlotComponentData moveComponent(@PathVariable("pageId") final String pageId,
			@PathVariable("slotId") final String slotId, @PathVariable("componentId") final String componentId,
			@RequestBody final PageContentSlotComponentData pageContentSlotComponent)
					throws CMSItemNotFoundException, ComponentNotAllowedInSlotException, WebserviceValidationException
	{
		try
		{
			return getPageContentSlotComponentFacade().moveComponent(pageId, componentId, slotId, pageContentSlotComponent);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Add a component to a slot.
	 *
	 * @bodyparam pageContentSlotComponentData the {@link PageContentSlotComponentData}
	 * @return the {@link PageContentSlotComponentData}
	 * @throws CMSItemNotFoundException
	 *            when component item does not exist
	 * @throws WebserviceValidationException
	 *            when validation errors are found
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody PageContentSlotComponentData addComponentToSlot(@RequestBody
			final PageContentSlotComponentData pageContentSlotComponentData, final HttpServletRequest request,
			final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			final PageContentSlotComponentData resultData = getPageContentSlotComponentFacade()
					.addComponentToContentSlot(pageContentSlotComponentData);
			// passing concatenation of pageId, slotId and componentId as identifier to the location header
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request,
							resultData.getPageId() + "-" + resultData.getSlotId() + "-" + resultData.getComponentId()));
			return resultData;
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * This resource is deprecated since version 6.2, please use
	 * <code>DELETE /pagescontentslotscomponents/contentslots/{slotId}/components/{componentId}</code> instead. <br>
	 * <br>
	 *
	 * Remove a component from a slot
	 *
	 * @queryparam slotId Content slot id containing the component to be removed
	 * @queryparam componentId Identifier of the component to be removed
	 * @throws CMSItemNotFoundException
	 *            when the component or slot cannot be found
	 * @throws ComponentNotFoundInSlotException
	 *            when the component slot does not contain the component
	 *
	 * @deprecated since version 6.2, please use {@link #removeComponent(slotId, componentId)}
	 */
	@Deprecated
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeComponentFromSlot(@RequestParam("slotId") final String slotId,
			@RequestParam("componentId") final String componentId) throws CMSItemNotFoundException, ComponentNotFoundInSlotException
	{
		removeComponent(slotId, componentId);
	}

	/**
	 * Remove a component from a content slot
	 *
	 * @pathparam slotId Content slot identifier containing the component to be removed
	 * @pathparam componentId Component identifier of the component to be removed
	 *
	 * @throws CMSItemNotFoundException
	 *            when the component or slot cannot be found
	 * @throws ComponentNotFoundInSlotException
	 *            when the component slot does not contain the component
	 */
	@RequestMapping(value = "/contentslots/{slotId}/components/{componentId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeComponent(@PathVariable("slotId") final String slotId, @PathVariable("componentId") final String componentId)
			throws CMSItemNotFoundException, ComponentNotFoundInSlotException
	{
		getPageContentSlotComponentFacade().removeComponentFromContentSlot(slotId, componentId);
	}

	protected PageContentSlotComponentFacade getPageContentSlotComponentFacade()
	{
		return pageContentSlotComponentFacade;
	}

	public void setPageContentSlotComponentFacade(final PageContentSlotComponentFacade contentSlotFacade)
	{
		this.pageContentSlotComponentFacade = contentSlotFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

}
