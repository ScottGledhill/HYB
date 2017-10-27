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
package de.hybris.platform.cmswebservices.media.controller;

import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.controller.AbstractSearchableController;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.data.NamedQueryData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller that handles searching for media.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/media")
public class MediaController extends AbstractSearchableController
{
	@Resource
	private MediaFacade mediaFacade;

	/**
	 * Get media by named query.
	 *
	 * @queryparam namedquery The name of the named query to use for the search.
	 * @queryparam params The query parameter values to inject into the named query.
	 * @queryparam currentpage The index of the requested page (index 0 means page 1).
	 * @queryparam pagesize The number of results per page.
	 * @queryparam sort The requested ordering for the search results.
	 *
	 * @return A single page of query results as a list of media or an empty list.
	 * @throws WebserviceValidationException
	 *            when the named query parameters provide contain validation errors
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public MediaListData getMediaByQuery(@ModelAttribute("namedQuery") final NamedQueryData namedQuery)
			throws WebserviceValidationException
	{
		final MediaListData mediaList = new MediaListData();

		try
		{
			mediaList.setMedia(getMediaFacade().getMediaByNamedQuery(namedQuery));
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
		return mediaList;
	}

	public MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}
}
