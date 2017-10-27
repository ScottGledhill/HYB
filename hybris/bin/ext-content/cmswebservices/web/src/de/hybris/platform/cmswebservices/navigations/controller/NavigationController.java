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
package de.hybris.platform.cmswebservices.navigations.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.navigations.NavigationFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.NavigationNodeData;
import de.hybris.platform.cmswebservices.data.NavigationNodeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
 * Controller to deal with Navigation Nodes objects
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/navigations")
public class NavigationController
{
	private static final Logger LOGGER = Logger.getLogger(NavigationController.class.getName());

	@Resource
	private NavigationFacade navigationFacade;


	@Resource
	private LocationHeaderResource locationHeaderResource;

	/**
	 * Finds all navigation nodes filtering by parentUid
	 * @param parentUid the parent navigation node Uid
	 * @queryparam parentUid the parent navigation node's Uid
	 * @return a list of navigation nodes
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"parentUid"})
	@ResponseBody
	public NavigationNodeListData findAllNavigationNodesByParentUid(@RequestParam(value = "parentUid") final String parentUid)
	{
		final NavigationNodeListData navigationNodeListData = new NavigationNodeListData();
		navigationNodeListData.setNavigationNodes(getNavigationFacade().findAllNavigationNodes(parentUid));
		return navigationNodeListData;
	}

	/**
	 * Finds all navigation nodes
	 * @return a list of all navigation nodes, excluding the super root.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public NavigationNodeListData findAllNavigationNodes()
	{
		final NavigationNodeListData navigationNodeListData = new NavigationNodeListData();
		navigationNodeListData.setNavigationNodes(getNavigationFacade().findAllNavigationNodes());
		return navigationNodeListData;
	}

	/**
	 * Find the navigation node by uid.
	 * @pathparam uid the navigation node's unique identifier
	 * @return the navigation node identified by uid.
	 * @throws CMSItemNotFoundException when the Navigation Node has not been found.
	 */
	@RequestMapping(value="/{uid}", method = RequestMethod.GET)
	@ResponseBody
	public NavigationNodeData findNavigationNodeById(@PathVariable("uid") final String uid) throws CMSItemNotFoundException
	{
		return getNavigationFacade().findNavigationNodeById(uid);
	}

	/**
	 * Updates the navigation node for a given navigation node uid
	 * @pathparam uid the navigation node uid to be updated.
	 * @bodyparam navigationNode the navigation node data
	 * @throws CMSItemNotFoundException when the Navigation Node has not been found.
	 */
	@RequestMapping(value="/{uid}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public NavigationNodeData updateNavigationNodeById(@PathVariable("uid") final String uid, @RequestBody final NavigationNodeData navigationNode)
			throws CMSItemNotFoundException
	{
		try
		{
			return getNavigationFacade().updateNavigationNode(uid, navigationNode);
		}
		catch (final ValidationException e)
		{
			LOGGER.debug(e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Deletes the navigation node.
	 * @pathparam uid the navigation node's uid.
	 * @throws CMSItemNotFoundException when the Navigation Node has not been found.
	 */
	@RequestMapping(value="/{uid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteNavigationNodeById(@PathVariable("uid") final String uid) throws CMSItemNotFoundException
	{
		getNavigationFacade().deleteNavigationNode(uid);
	}

	/**
	 * Adds a new navigation node
	 * @bodyparam navigationNode the navigation node data to be added.
	 * @param request the {@link HttpServletRequest} object
	 * @param response the {@link HttpServletResponse} object
	 * @return the navigation node data created.
	 * @throws CMSItemNotFoundException if the parentUid does not exist
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public NavigationNodeData createNavigationNodeById( @RequestBody final NavigationNodeData navigationNode, final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException
	{
		try
		{
			final NavigationNodeData responseData = getNavigationFacade().addNavigationNode(navigationNode);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, responseData.getUid()));
			return responseData;
		}
		catch (final ValidationException e)
		{
			LOGGER.debug(e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Finds all navigation ancestors for a given node uid and the node itself.
	 * @queryparam ancestorTrailFrom the navigation node's Uid whose ancestors are to be extracted.
	 * @return the navigation node's ancestors and the node itself.
	 * @throws CMSItemNotFoundException when the navigation node uid does not exist.
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"ancestorTrailFrom"})
	@ResponseBody
	public NavigationNodeListData findAllNavigationAncestorsAndSelf(@RequestParam(value = "ancestorTrailFrom") final String navigationNodeUid)
			throws CMSItemNotFoundException
	{
		final NavigationNodeListData navigationAncestorListData = new NavigationNodeListData();
		navigationAncestorListData.setNavigationNodes(getNavigationFacade().getNavigationAncestorsAndSelf(navigationNodeUid));
		return navigationAncestorListData;
	}


	protected NavigationFacade getNavigationFacade()
	{
		return navigationFacade;
	}

	public void setNavigationFacade(final NavigationFacade navigationFacade)
	{
		this.navigationFacade = navigationFacade;
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
