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
package de.hybris.platform.cmsfacades.restrictions.populator.model;

import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Converts an {@link AbstractRestrictionModel} Restriction to a {@link AbstractRestrictionData} dto
 */
public class BasicRestrictionModelPopulator implements Populator<AbstractRestrictionModel, AbstractRestrictionData>
{

	@Override
	public void populate(final AbstractRestrictionModel source, final AbstractRestrictionData target) throws ConversionException
	{
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setTypeCode(source.getItemtype());
	}

}
