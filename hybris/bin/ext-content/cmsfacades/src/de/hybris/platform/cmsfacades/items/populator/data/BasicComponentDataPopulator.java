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

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * This populator will populate the {@link AbstractCMSComponentModel#setName(String)} and the
 * {@link AbstractCMSComponentModel#setUid(String)} with attributes uid and name from {@link AbstractCMSComponentData}.
 * The visible attribute is defaulted to true if not provided. </br>
 *
 */
public class BasicComponentDataPopulator implements Populator<AbstractCMSComponentData, AbstractCMSComponentModel>
{
	@Override
	public void populate(final AbstractCMSComponentData dto, final AbstractCMSComponentModel model) throws ConversionException
	{
		model.setUid(dto.getUid());
		model.setName(dto.getName());
		model.setVisible(dto.getVisible() != null ? dto.getVisible() : Boolean.TRUE);
	}
}
