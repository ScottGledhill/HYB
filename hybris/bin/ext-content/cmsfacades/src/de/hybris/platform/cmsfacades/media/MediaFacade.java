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
package de.hybris.platform.cmsfacades.media;

import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.data.NamedQueryData;
import de.hybris.platform.cmswebservices.dto.MediaFileDto;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;


/**
 * Facade for managing Media.
 */
public interface MediaFacade
{
	/**
	 * Get a media DTO by code.
	 *
	 * @param code
	 *           - the code to search for
	 * @return the media
	 */
	MediaData getMediaByCode(String code) throws MediaNotFoundException;

	/**
	 * Search for a single page of media using a named query.
	 *
	 * @param namedQuery
	 *           - the named query
	 * @return the list of search results or empty collection
	 * @throws ValidationException
	 *            when the input contains validation errors
	 */
	List<MediaData> getMediaByNamedQuery(NamedQueryData namedQuery) throws ValidationException;

	/**
	 * Create a media item from an {@code InputStream}.
	 *
	 * @param media
	 *           - the attributes required to create a new media item
	 * @param mediaFile
	 *           - the actual file and an {@code InputStream} and its properties
	 * @return the newly created media
	 * @throws ValidationException
	 *            when the input contains validation errors
	 * @throws ConversionException
	 *            when unable to convert the DTOs to a hybris model
	 */
	MediaData addMedia(MediaData media, MediaFileDto mediaFile) throws ValidationException, ConversionException;
}
