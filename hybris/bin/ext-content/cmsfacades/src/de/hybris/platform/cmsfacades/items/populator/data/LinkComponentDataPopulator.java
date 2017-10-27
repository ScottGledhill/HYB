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
package de.hybris.platform.cmsfacades.items.populator.data;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmswebservices.data.CMSLinkComponentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link CMSLinkComponentModel#setLinkName(String, Locale)} with attribute linkName
 * {@link CMSLinkComponentData}. <br>
 * The external attribute is defaulted to false if not provided.
 */
public class LinkComponentDataPopulator implements Populator<CMSLinkComponentData, CMSLinkComponentModel>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CMSLinkComponentData dto, final CMSLinkComponentModel model) throws ConversionException
	{
		Optional.ofNullable(dto.getLinkName()) //
				.ifPresent(linkName -> getLocalizedPopulator().populate( //
						(locale, value) -> model.setLinkName(value, locale),
						(locale) -> linkName.get(getLocalizedPopulator().getLanguage(locale))));

		model.setExternal(dto.getExternal() != null ? dto.getExternal() : Boolean.FALSE);
		model.setUrl(dto.getUrl());
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}

}
