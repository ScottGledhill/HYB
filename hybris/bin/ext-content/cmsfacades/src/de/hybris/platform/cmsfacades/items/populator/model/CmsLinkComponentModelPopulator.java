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
package de.hybris.platform.cmsfacades.items.populator.model;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmswebservices.data.CMSLinkComponentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default cms link component implementation of {@link Populator}.
 */
public class CmsLinkComponentModelPopulator implements Populator<CMSLinkComponentModel, CMSLinkComponentData>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CMSLinkComponentModel source, final CMSLinkComponentData target) throws ConversionException
	{
		final Map<String, String> linkNameMap = Optional.ofNullable(target.getLinkName())
				.orElseGet(() -> getNewLinkNameMap(target));

		getLocalizedPopulator().populate( //
				(locale, value) -> linkNameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getLinkName(locale));

		target.setExternal(source.isExternal());
		target.setUrl(source.getUrl());
	}

	protected Map<String, String> getNewLinkNameMap(final CMSLinkComponentData target)
	{
		target.setLinkName(new LinkedHashMap<>());
		return target.getLinkName();
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
