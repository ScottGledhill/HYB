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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts an {@link AbstractPageModel} page to a {@link AbstractPageData} dto
 */
public class BasicPageModelPopulator implements Populator<AbstractPageModel, AbstractPageData>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final AbstractPageModel source, final AbstractPageData target) throws ConversionException
	{
		target.setPk(source.getPk().toString());
		target.setCreationtime(source.getCreationtime());
		target.setModifiedtime(source.getModifiedtime());
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setTypeCode(source.getItemtype());
		target.setTemplate(source.getMasterTemplate().getUid());
		target.setDefaultPage(source.getDefaultPage());
		target.setOnlyOneRestrictionMustApply(source.isOnlyOneRestrictionMustApply());

		final Map<String, String> titleMap = Optional.ofNullable(target.getTitle()).orElseGet(() -> getNewTitleMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> titleMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getTitle(locale));
	}

	protected Map<String, String> getNewTitleMap(final AbstractPageData target)
	{
		target.setTitle(new LinkedHashMap<>());
		return target.getTitle();
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
