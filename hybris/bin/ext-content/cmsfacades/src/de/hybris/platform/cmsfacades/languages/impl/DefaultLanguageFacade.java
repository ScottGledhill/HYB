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
package de.hybris.platform.cmsfacades.languages.impl;

import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link LanguageFacade}.
 */
public class DefaultLanguageFacade implements LanguageFacade
{
	private StoreSessionFacade storeSessionFacade;

	@Override
	public List<LanguageData> getLanguages()
	{
		final List<LanguageData> languageDataList = new ArrayList<>();

		final LanguageData defaultLanguage = getStoreSessionFacade().getDefaultLanguage();

		for (final LanguageData language : getStoreSessionFacade().getAllLanguages())
		{
			if (defaultLanguage.getIsocode().equalsIgnoreCase(language.getIsocode()))
			{
				language.setRequired(true);
				languageDataList.add(0, language);
			}
			else
			{
				languageDataList.add(language);
			}
		}

		return languageDataList;
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

}
