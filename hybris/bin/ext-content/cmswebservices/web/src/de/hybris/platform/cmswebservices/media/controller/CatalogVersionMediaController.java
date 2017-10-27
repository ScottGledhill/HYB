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
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.dto.MediaFileDto;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;


/**
 * Controller that provides media.
 *
 * @pathparam catalogId The unique code of the Catalog item.
 * @pathparam versionId The version of the catalog
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/catalogs/{catalogId}/versions/{versionId}" + CatalogVersionMediaController.MEDIA_URI_PATH)
public class CatalogVersionMediaController
{
	private static final Logger LOG = LoggerFactory.getLogger(CatalogVersionMediaController.class);

	private static final String DESCRIPTION = "description";
	private static final String ALT_TEXT = "altText";
	private static final String CODE = "code";
	public static final String MEDIA_URI_PATH = "/media";
	private static final String UTF_8 = "UTF-8";

	@Resource
	private MediaFacade mediaFacade;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	/**
	 * Upload a media.
	 *
	 * @queryparam code The code to use for the newly created media.
	 * @queryparam altText The alternative text to use for the newly created media.
	 * @queryparam description The description to use for the newly created media.
	 * @queryparam file The file representing the actual binary contents of the media to be created.
	 *
	 * @param catalogId
	 *           the unique identifier of the catalog for which to link the new media.
	 * @param versionId
	 *           the specific catalog version to which the new media will be associated to.
	 * @param media
	 *           the {@link MediaData} containing the data for the associated media item to be created.
	 * @param multiPart
	 *           the file that was uploaded for the new media.
	 * @param request
	 *           the {@link HttpServletRequest}
	 * @param response
	 *           the {@link HttpServletResponse}
	 *
	 * @return the newly created Media item
	 * @throws IOException
	 *            when an error occurs parsing the {@code MultipartFile}
	 * @throws WebserviceValidationException
	 *            when the media query parameters provided contain validation errors
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public MediaData uploadMultipartMedia(@PathVariable("catalogId") final String catalogId,
			@PathVariable("versionId") final String versionId, @ModelAttribute("media") final MediaData media,
			@RequestParam("file") final MultipartFile multiPart, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, WebserviceValidationException
	{
		media.setCatalogId(catalogId);
		media.setCatalogVersion(versionId);

		try
		{
			try (InputStream inputStream = multiPart.getInputStream())
			{
				final MediaData newMedia = getMediaFacade().addMedia(media, getFile(multiPart, inputStream));

				response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
						getLocationHeaderResource().createLocationForChildResource(request, newMedia.getCode()));
				return newMedia;
			}
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Get a media by code. The resource will accept any GET ../media/**, which includes paths and image extensions at
	 * the end. To allow this resource to accept any image extension like .jpg, .gif, .png, Spring MVC needs to be
	 * configured accordingly.
	 *
	 * See spring mvc configuration {@code
	 * <mvc:annotation-driven content-negotiation-manager="contentNegotiationManager">} where
	 * {@code org.springframework.web.accept.ContentNegotiationManagerFactoryBean.favorPathExtension = false}
	 *
	 * @pathparam code The unique code of the Media item.
	 *
	 * @return the Media item matching the code
	 * @throws MediaNotFoundException
	 *            When the media code requested cannot be found.
	 */
	@RequestMapping(value = "/**", method = RequestMethod.GET)
	@ResponseBody
	public MediaData getMediaByCode(final HttpServletRequest request)
			throws MediaNotFoundException
	{
		final Optional<String> optionalMediaCode = parseMediaCode(request);

		return getMediaFacade() //
				.getMediaByCode(optionalMediaCode.orElseThrow(() -> new MediaNotFoundException("Media code cannot be empty")));
	}

	/**
	 * Parses the Request URI after the Media code, which is defined by everything after the /media less the first '/', if
	 * present.
	 * @param request the http servlet request
	 * @return an optional object to hold the parsed media code
	 */
	protected Optional<String> parseMediaCode(final HttpServletRequest request)
	{
		// we know that the URI will always have the MEDIA_URI_PATH at the end
		final String uri = StringUtils.substringAfter(request.getRequestURI(), MEDIA_URI_PATH);
		if (uri.trim().length() > 0)
		{
			// returns the remaining path without the first '/'
			try
			{
				return Optional.of(java.net.URLDecoder.decode(uri.substring(1), UTF_8));
			}
			catch (final UnsupportedEncodingException e)
			{
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	/**
	 * Create a new media DTO from request parameters.
	 *
	 * @param request
	 *           - the http servlet request
	 * @return a media DTO
	 */
	public MediaData getMedia(final HttpServletRequest request)
	{
		final MediaData media = new MediaData();
		media.setCode(request.getParameter(CODE));
		media.setAltText(request.getParameter(ALT_TEXT));
		media.setDescription(request.getParameter(DESCRIPTION));
		return media;
	}

	/**
	 * Create a new media file DTO from the {@code MultipartFile}
	 *
	 * @param file
	 *           - a Spring {@code MultipartFile}
	 * @return a media file DTO
	 * @throws IOException
	 *            when an error occurs retrieving the input stream from the multi part file
	 */
	public MediaFileDto getFile(final MultipartFile file, final InputStream inputStream)
	{
		final MediaFileDto mediaFile = new MediaFileDto();
		mediaFile.setInputStream(inputStream);
		mediaFile.setName(file.getOriginalFilename());
		mediaFile.setSize(file.getSize());
		mediaFile.setMime(file.getContentType());
		return mediaFile;
	}

	public MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
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
