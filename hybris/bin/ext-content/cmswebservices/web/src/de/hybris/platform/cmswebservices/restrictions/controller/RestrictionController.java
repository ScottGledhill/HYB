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
package de.hybris.platform.cmswebservices.restrictions.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.restrictions.RestrictionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;
import de.hybris.platform.cmswebservices.data.NamedQueryData;
import de.hybris.platform.cmswebservices.data.RestrictionListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Controller to manage restrictions.
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/restrictions")
public class RestrictionController
{
	private static final Logger LOG = LoggerFactory.getLogger(RestrictionController.class);

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private RestrictionFacade restritionFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	/**
	 * Find all restrictions.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public RestrictionListData findAllRestrictions()
	{
		final RestrictionListData restrictionListData = new RestrictionListData();
		restrictionListData.setRestrictions(getRestritionFacade().findAllRestrictions());
		return restrictionListData;
	}

	/**
	 * Find a page of restrictions.
	 *
	 * @queryparam mask the string value on which restrictions will be filtered, business logic may choose to filter on
	 *             the restriction name
	 * @queryparam params The query parameter values containing the restriction type code.
	 * @queryparam currentpage The index of the requested page (index 0 means page 1).
	 * @queryparam pagesize The number of results per page.
	 * @queryparam sort The requested ordering for the search results.
	 *
	 * @return a dto which serves as a wrapper object that contains a list of {@link RestrictionListData} as well as
	 *         pagination and sorting pertaining to the request; never <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params = { "pageSize", "currentPage"})
	@ResponseBody
	public RestrictionListData findRestrictionsByPage(@RequestParam(required = false) final String mask,
			@ModelAttribute final NamedQueryData namedQuery) throws WebserviceValidationException
	{
		try
		{
			final SearchResult<AbstractRestrictionData> pagedData = getRestritionFacade().findRestrictionsByMask(mask, namedQuery);
			final RestrictionListData restrictionListData = new RestrictionListData();
			restrictionListData.setRestrictions(pagedData.getResult());
			restrictionListData.setPagination(getWebPaginationUtils().buildPagination(pagedData));
			return restrictionListData;
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Create a new restriction
	 *
	 * @bodyparam restrictionData {@link AbstractRestrictionData}
	 * @param request
	 *           the {@link HttpServletRequest}
	 * @param response
	 *           the {@link HttpServletResponse}
	 * @return {@link AbstractRestrictionData}
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public AbstractRestrictionData createRestriction(@RequestBody final AbstractRestrictionData restrictionData, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		try
		{
			final AbstractRestrictionData restriction = getRestritionFacade().createRestriction(restrictionData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, restriction.getUid()));
			return restriction;
		}
		catch (final ValidationException e)
		{
			LOG.info("valiation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}
	
	/**
	 * Get a restriction that corresponds to a given restriction id
	 *
	 * @pathparam restrictionId the restriction identifier
	 * @return {@link AbstractRestrictionData}
	 * @throws CMSItemNotFoundException
	 *            when the corresponding restriction to the id does not exist
	 */
	@RequestMapping(value = "/{restrictionId}", method = RequestMethod.GET)
	@ResponseBody
	public AbstractRestrictionData findRestrictionById(@PathVariable final String restrictionId) throws CMSItemNotFoundException
	{
		return getRestritionFacade().findRestrictionById(restrictionId);
	}
	
	/**
	 * Update a restriction.
	 *
	 * @pathparam restrictionId Restriction identifier
	 * @bodyparam restrictionData the {@link AbstractRestrictionData}
	 * @return {@link AbstractRestrictionData}
	 * @throws WebserviceValidationException
	 *            if there validation errors.   
	 * @throws CMSItemNotFoundException
	 *            when the corresponding restriction to the id does not exist    
	 */
	
	@RequestMapping(value = "/{restrictionId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public AbstractRestrictionData updateRestriction(@PathVariable final String restrictionId, @RequestBody final AbstractRestrictionData restrictionData)
			throws WebserviceValidationException,CMSItemNotFoundException
	{
		try
		{
			return getRestritionFacade().updateRestriction(restrictionId, restrictionData);
		}
		catch (final ValidationException e)
		{
			LOG.info("validation exception", e);
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

	public RestrictionFacade getRestritionFacade()
	{
		return restritionFacade;
	}

	public void setRestritionFacade(final RestrictionFacade restritionFacade)
	{
		this.restritionFacade = restritionFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

}
