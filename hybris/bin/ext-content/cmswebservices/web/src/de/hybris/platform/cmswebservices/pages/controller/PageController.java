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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Controller to deal with AbstractPageModel objects
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages")
public class PageController
{
	private static Logger LOG = LoggerFactory.getLogger(PageController.class);

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private PageFacade cmsPageFacade;

	/**
	 * Find all pages.
	 *
	 * @return all pages
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PageListData findAllPages()
	{
		final PageListData pageListData = new PageListData();
		pageListData.setPages(getCmsPageFacade().findAllPages());
		return pageListData;
	}

	/**
	 * Find specific pages.
	 *
	 * @queryparam uids a list of identifier of the pages that we are looking for
	 * @param uids
	 *           a list of identifier of the pages that we are looking for
	 * @return ({@link PageListData}
	 *
	 */
	@RequestMapping(method = RequestMethod.GET, params =
		{ "uids" })
	@ResponseBody
	public PageListData findPagesByIds(@RequestParam("uids") final List<String> uids)
	{
		final PageListData pageListData = new PageListData();
		pageListData.setPages(getCmsPageFacade().findAllPages().stream().filter(page -> uids.contains(page.getUid()))
				.collect(Collectors.toList()));
		return pageListData;
	}

	/**
	 * Get the page that matches the given page uid.
	 *
	 * @pathparam pageUid {@link AbstractPageData} identifier
	 * @return {@link AbstractPageData}
	 * @throws CMSItemNotFoundException
	 *            when the page cannot be found
	 */
	@RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
	@ResponseBody
	public AbstractPageData getPageByUid(@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return getCmsPageFacade().getPageByUid(pageId);
	}

	/**
	 * Get all default or variation pages that matches the given page type.
	 *
	 * @queryparam typeCode the type code of a page
	 * @queryparam defaultPage setting this to true will find all default pages; otherwise find all variation pages
	 * @return all default or variation pages for a given page type
	 */
	@RequestMapping(method = RequestMethod.GET, params =
		{ "typeCode", "defaultPage" })
	@ResponseBody
	public PageListData findPagesByType(@RequestParam("typeCode") final String typeCode,
			@RequestParam("defaultPage") final Boolean isDefaultPage)
	{
		final PageListData pageListData = new PageListData();
		pageListData.setPages(getCmsPageFacade().findPagesByType(typeCode, isDefaultPage));
		return pageListData;
	}

	/**
	 * Get all variation pages uid for a given page.
	 *
	 * @pathparam pageId the page identifier
	 * @return all variation pages uid for a given page; empty if the given page is already a variation page; never
	 *         <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the pageId is invalid
	 */
	@RequestMapping(value = "/{pageId}/variations", method = RequestMethod.GET)
	@ResponseBody
	public UidListData findVariationPages(@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findVariationPages(pageId));
	}

	/**
	 * Get all fallback pages uid for a given page.
	 *
	 * @pathparam pageId the page identifier
	 * @return all fallback pages uid for a given page; empty if the given page is already a fallback page; never
	 *         <tt>null</tt>
	 * @throws CMSItemNotFoundException
	 *            when the pageId is invalid
	 */
	@RequestMapping(value = "/{pageId}/fallbacks", method = RequestMethod.GET)
	@ResponseBody
	public UidListData findFallbackPages(@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findFallbackPages(pageId));
	}

	protected UidListData convertToUidListData(final List<String> pageIds)
	{
		final UidListData pageData = new UidListData();
		pageData.setUids(pageIds);
		return pageData;
	}

	/**
	 * Create a new page. <br>
	 *
	 * @bodyparam pageData {@link AbstractPageData}
	 * @param request
	 *           the {@link HttpServletRequest}
	 * @param response
	 *           the {@link HttpServletResponse}
	 * @return {@link AbstractPageData}
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public AbstractPageData createPage(@RequestBody final AbstractPageData pageData, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		try
		{
			final AbstractPageData createPage = getCmsPageFacade().createPage(pageData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, createPage.getUid()));
			return createPage;
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Update a page.
	 *
	 * @pathparam pageId Page identifier
	 * @bodyparam pageData the {@link AbstractPageData}
	 * @return {@link AbstractPageData}
	 */
	@RequestMapping(value = "/{pageId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public AbstractPageData updatePage(@PathVariable final String pageId, @RequestBody final AbstractPageData pageData)
	{
		try
		{
			return getCmsPageFacade().updatePage(pageId, pageData);
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
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
