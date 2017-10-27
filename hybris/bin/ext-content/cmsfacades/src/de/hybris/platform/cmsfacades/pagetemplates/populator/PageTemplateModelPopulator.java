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
package de.hybris.platform.cmsfacades.pagetemplates.populator;

import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmswebservices.data.PageTemplateData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * This populator will populate the {@link PageTemplateData} from the {@link PageTemplateModel}
 */
public class PageTemplateModelPopulator implements Populator<PageTemplateModel, PageTemplateData>
{

	@Override
	public void populate(final PageTemplateModel source, final PageTemplateData target) throws ConversionException
	{

		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setFrontEndName(source.getFrontendTemplateName());
		target.setPreviewIcon((source.getPreviewIcon() != null) ? source.getPreviewIcon().getURL() : null);
	}

}
