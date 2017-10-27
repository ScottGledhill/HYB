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
package de.hybris.platform.cmssmarteditwebservices.products.facade.populator;

import de.hybris.platform.cmsfacades.data.ProductData;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmssmarteditwebservices.data.MediaData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

/**
 * Basic class for populating media data for {@link de.hybris.platform.cmssmarteditwebservices.data.ProductData} from {@link ProductData} data.  
 */
public class ProductMediaDataPopulator implements Populator<ProductData, de.hybris.platform.cmssmarteditwebservices.data.ProductData>
{
	
	private MediaFacade mediaFacade;
	
	private Converter<de.hybris.platform.cmswebservices.data.MediaData, MediaData> cmsSeMediaDataConverter;
	
	@Override
	public void populate(final ProductData source,
			final de.hybris.platform.cmssmarteditwebservices.data.ProductData target) throws ConversionException
	{
		Optional.ofNullable(source.getThumbnailMediaCode()).ifPresent(thumbnailMediaCode -> target.setThumbnail(getMedia(thumbnailMediaCode)));
		
	}

	protected MediaData getMedia(final String mediaCode)
	{
		return getCmsSeMediaDataConverter().convert(getMediaFacade().getMediaByCode(mediaCode));
	}

	protected MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	@Required
	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}

	protected Converter<de.hybris.platform.cmswebservices.data.MediaData, MediaData> getCmsSeMediaDataConverter()
	{
		return cmsSeMediaDataConverter;
	}

	@Required
	public void setCmsSeMediaDataConverter(
			final Converter<de.hybris.platform.cmswebservices.data.MediaData, MediaData> cmsSeMediaDataConverter)
	{
		this.cmsSeMediaDataConverter = cmsSeMediaDataConverter;
	}
}
