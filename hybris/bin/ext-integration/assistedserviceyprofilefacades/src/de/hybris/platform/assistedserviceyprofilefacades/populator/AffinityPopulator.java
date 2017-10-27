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

import de.hybris.platform.assistedserviceyprofilefacades.data.AffinityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default affinity populator that populates common affinity attributes e.g. update and affinity fields
 */
public class AffinityPopulator<SOURCE extends NeighbourData, TARGET extends AffinityData> implements Populator<SOURCE, TARGET>
{

	private final Logger LOG = Logger.getLogger(AffinityPopulator.class);

	@Override
	public void populate(final SOURCE neighbourData, final TARGET affinityData) throws ConversionException
	{
		final Map<String, String> neighbourProperties = neighbourData.getProperties();

		affinityData.setAffinity(neighbourProperties.get(YaasyprofileconnectConstants.AFFINITY_FIELD));

		try
		{
			affinityData.setUpdated(
					Date.from(ZonedDateTime.parse(neighbourProperties.get(YaasyprofileconnectConstants.UPDATED_FIELD)).toInstant()));
		}
		catch (final Exception exp)
		{
			LOG.error("Affinity with Id [" + neighbourData.getId() + "] on yProfile has invalid 'update field' value ["
					+ neighbourProperties.get(YaasyprofileconnectConstants.UPDATED_FIELD) + "]", exp);
		}
	}
}
