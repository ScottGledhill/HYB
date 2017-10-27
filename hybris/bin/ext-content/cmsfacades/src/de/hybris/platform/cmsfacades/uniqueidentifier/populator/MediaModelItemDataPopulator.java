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
package de.hybris.platform.cmsfacades.uniqueidentifier.populator;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates the ItemData object with the Media model as the source.
 */
public class MediaModelItemDataPopulator implements Populator<MediaModel, ItemData>
{
	@Override
	public void populate(final MediaModel source, final ItemData target) throws ConversionException
	{
		target.setItemId(source.getCode());
		target.setItemType(source.getItemtype());
	}
}
