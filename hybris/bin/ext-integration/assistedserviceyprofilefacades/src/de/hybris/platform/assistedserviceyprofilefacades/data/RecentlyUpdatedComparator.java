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
package de.hybris.platform.assistedserviceyprofilefacades.data;

import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.time.ZonedDateTime;
import java.util.Comparator;

import org.apache.log4j.Logger;


/**
 *
 * Used for NeighbourData sorting by their 'updated' field.
 */
public class RecentlyUpdatedComparator implements Comparator<NeighbourData>
{
	private final Logger LOG = Logger.getLogger(RecentlyUpdatedComparator.class);

	@Override
	public int compare(final NeighbourData neighbourData1, final NeighbourData neighbourData2)
	{
		if (neighbourData2.getProperties().containsKey(YaasyprofileconnectConstants.UPDATED_FIELD)
				&& neighbourData1.getProperties().containsKey(YaasyprofileconnectConstants.UPDATED_FIELD))
		{
			try
			{
				final ZonedDateTime firstNeighbourDate = ZonedDateTime
						.parse(neighbourData1.getProperties().get(YaasyprofileconnectConstants.UPDATED_FIELD));

				final ZonedDateTime secondNeighbourDate = ZonedDateTime
						.parse(neighbourData2.getProperties().get(YaasyprofileconnectConstants.UPDATED_FIELD));

				return secondNeighbourDate.compareTo(firstNeighbourDate);
			}
			catch (final Exception exp)
			{
				LOG.error("Problem happend during comparing reccently updated neighbours with invalid 'update field' value", exp);

			}
		}
		return 0;
	}
}