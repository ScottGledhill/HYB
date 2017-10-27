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
package de.hybris.platform.cmswebservices.items.controller;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.cmswebservices.data.ComponentItemListData;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller to deal with component items
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/items")
public class ItemController
{
	static final String DEFAULT_FIELD_SET = "DEFAULT";

	@Resource
	private ComponentItemFacade componentFacade;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	/**
	 * Find all components. <br>
	 * By default, the result is ordered by modified time; most recently modified items first.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return a dto which serves as a wrapper object that contains a list of {@link ComponentItemListData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params = {})
	@ResponseBody
	public ComponentItemListData getAllComponentItems(
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ComponentItemListData componentDataList = new ComponentItemListData();
		componentDataList.setComponentItems(getComponentFacade().getAllComponentItems());
		return getDataMapper().map(componentDataList, ComponentItemListData.class, fields);
	}

	/**
	 * Find a page of components. <br>
	 * By default, the result is ordered by modified time; most recently modified items first.
	 * @queryparam mask the string value on which components will be filtered, business logic may choose to filter on the component name
	 * @queryparam pageableData the {@link PageableData} object containing the page request details
	 * @return a dto which serves as a wrapper object that contains a list of {@link ComponentItemListData} as well as pagination and sorting pertaining to the request; never <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"pageSize"})
	@ResponseBody
	public ComponentItemListData getComponentItemsByPage(@RequestParam(required = false) final String mask,
			@ModelAttribute final PageableWsDTO pageableDto)
	{
		final PageableData pageableData = getDataMapper().map(pageableDto, PageableData.class);
		final SearchResult<AbstractCMSComponentData> pagedData = getComponentFacade().findComponentByMask(mask, pageableData);

		final ComponentItemListData componentDataList = new ComponentItemListData();
		componentDataList.setComponentItems(pagedData.getResult());
		componentDataList.setPagination(getWebPaginationUtils().buildPagination(pagedData));
		return componentDataList;
	}

	/**
	 * Find components matching the given uids. <br>
	 *
	 * @queryparam uids list of uids representing the components to retrieve.
	 * @return a dto which serves as a wrapper object that contains a list of {@link ComponentItemListData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params =
		{ "uids" })
	@ResponseBody
	public ComponentItemListData getAllComponentItemsForUids(@RequestParam("uids") final String[] uids)
	{
		final List<String> listUids = Arrays.asList(uids);
		final ComponentItemListData componentDataList = new ComponentItemListData();

		componentDataList.setComponentItems(getComponentFacade().getAllComponentItems().stream() //
				.filter(component -> listUids.contains(component.getUid())) //
				.collect(Collectors.toList()));
		return componentDataList;
	}


	/**
	 * Get the component that matches the given component id.
	 *
	 * @pathparam componentId Component identifier
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @return AbstractCMSComponentData
	 * @throws CMSItemNotFoundException
	 *            when the item has not been found
	 */
	@RequestMapping(value = "/{componentId}", method = RequestMethod.GET)
	@ResponseBody
	public AbstractCMSComponentData getComponentItemByUid(@PathVariable final String componentId,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields) throws CMSItemNotFoundException,
	ConversionException
	{
		final AbstractCMSComponentData componentData = getComponentFacade().getComponentItemByUid(componentId);
		return componentData;
	}

	/**
	 * Create a new component. <br>
	 * When the <tt>&nbsp;slotId&nbsp;</tt> and the <tt>&nbsp;positionId&nbsp;</tt> are specified in the request, the
	 * newly created component will be assigned to a content slot at the stated position.
	 *
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @bodyparam componentData {@link AbstractCMSComponentData}
	 * @bodyparams componentData,uid,name,typeCode,pageId,slotId,position
	 * @param request
	 *           the {@link HttpServletRequest}
	 * @param response
	 *           the {@link HttpServletResponse}
	 * @return AbstractCMSComponentData
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public AbstractCMSComponentData createAndAddNewComponent(@RequestBody final AbstractCMSComponentData componentData,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final AbstractCMSComponentData componentItem = getComponentFacade().addComponentItem(componentData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, componentItem.getUid()));
			return componentItem;
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Update a component.
	 *
	 * @pathparam componentId Component identifier
	 * @queryparam fields Response configuration (list of fields, which should be returned in the response)
	 * @bodyparam componentData {@link AbstractCMSComponentData}
	 *            if it cannot convert the DTO.
	 * @throws WebserviceValidationException
	 *            if there is any validation error
	 * @throws CMSItemNotFoundException
	 *            if it cannot find the component
	 */
	@RequestMapping(value = "/{componentId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateComponent(@PathVariable final String componentId, //
			@RequestBody final AbstractCMSComponentData componentData) throws WebserviceValidationException,
	CMSItemNotFoundException
	{
		try
		{
			getComponentFacade().updateComponentItem(componentId, componentData);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Remove a component from the system.
	 *
	 * @pathparam componentId Component identifier
	 *
	 * @throws CMSItemNotFoundException
	 *            if it cannot find the component
	 */
	@RequestMapping(value = "/{componentId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeComponent(@PathVariable final String componentId)
			throws CMSItemNotFoundException
	{
		getComponentFacade().removeComponentItem(componentId);
	}

	protected ComponentItemFacade getComponentFacade()
	{
		return componentFacade;
	}

	public void setComponentFacade(final ComponentItemFacade componentFacade)
	{
		this.componentFacade = componentFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}