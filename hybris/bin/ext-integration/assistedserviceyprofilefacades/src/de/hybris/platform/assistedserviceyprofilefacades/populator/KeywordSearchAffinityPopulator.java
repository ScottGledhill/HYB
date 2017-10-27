/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.util.Map;


/**
 * Keyword search affinity that is used for populating keyword search affinity data
 */
public class KeywordSearchAffinityPopulator<SOURCE extends NeighbourData, TARGET extends KeywordSearchAffinityData>
		implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE neighbourData, final TARGET keywordData) throws ConversionException
	{
		final Map<String, String> neighbourProperties = neighbourData.getProperties();

		keywordData.setSearchText(neighbourProperties.get(YaasyprofileconnectConstants.KEYWORD_SEARCHID_FIELD));
		keywordData.setSearchCount(neighbourProperties.get(YaasyprofileconnectConstants.KEYWORD_SEARCH_COUNT_FIELD));
	}
}