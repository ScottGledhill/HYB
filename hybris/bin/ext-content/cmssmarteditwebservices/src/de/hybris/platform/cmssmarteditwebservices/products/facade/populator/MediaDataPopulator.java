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

import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Basic class for populating {@link de.hybris.platform.cmssmarteditwebservices.data.MediaData} from {@link MediaData} data. 
 */
public class MediaDataPopulator implements Populator<MediaData, de.hybris.platform.cmssmarteditwebservices.data.MediaData>
{
	@Override
	public void populate(final MediaData source, final de.hybris.platform.cmssmarteditwebservices.data.MediaData target)
			throws ConversionException
	{
		target.setCode(source.getCode());
		target.setCatalogId(source.getCatalogId());
		target.setCatalogVersion(source.getCatalogVersion());
		target.setAltText(source.getAltText());
		target.setDescription(source.getDescription());
		target.setDownloadUrl(source.getDownloadUrl());
		target.setMime(source.getMime());
		target.setUrl(source.getUrl());
	}
}
