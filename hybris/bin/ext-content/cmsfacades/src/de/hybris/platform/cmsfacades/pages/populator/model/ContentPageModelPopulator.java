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
package de.hybris.platform.cmsfacades.pages.populator.model;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmswebservices.data.ContentPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Converts an {@link ContentPageModel} page to a {@link ContentPageData} dto
 */
public class ContentPageModelPopulator implements Populator<ContentPageModel, ContentPageData>
{
	@Override
	public void populate(final ContentPageModel source, final ContentPageData target) throws ConversionException
	{
		target.setLabel(source.getLabel());
	}
}
