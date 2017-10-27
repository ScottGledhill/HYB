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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for consuming the item Id, name and itemType from reading {@link MediaModel} class
 */
public class DefaultMediaModelUniqueIdentifierConverter implements UniqueIdentifierConverter<MediaModel>
{

	private Converter<MediaModel, ItemData> mediaModelItemDataConverter;
	
	@Override
	public String getItemType()
	{
		return MediaModel._TYPECODE;
	}

	@Override
	public ItemData convert(final MediaModel mediaModel) 
	{
		return getMediaModelItemDataConverter().convert(mediaModel);
	}

	@Override
	public MediaModel convert(final ItemData itemData)
	{
		throw new ConversionException("Conversion implementation to MediaModel is not available.");
	}

	protected Converter<MediaModel, ItemData> getMediaModelItemDataConverter()
	{
		return mediaModelItemDataConverter;
	}

	@Required
	public void setMediaModelItemDataConverter(final Converter<MediaModel, ItemData> mediaModelItemDataConverter)
	{
		this.mediaModelItemDataConverter = mediaModelItemDataConverter;
	}
}
