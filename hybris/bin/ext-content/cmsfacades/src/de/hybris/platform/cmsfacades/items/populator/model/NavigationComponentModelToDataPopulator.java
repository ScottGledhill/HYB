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

import de.hybris.platform.acceleratorcms.model.components.NavigationComponentModel;
import de.hybris.platform.cmswebservices.data.NavigationComponentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Default navigation component implementation of {@link Populator}.
 */
public class NavigationComponentModelToDataPopulator implements Populator<NavigationComponentModel, NavigationComponentData>
{

	@Override
	public void populate(final NavigationComponentModel source, final NavigationComponentData target) throws ConversionException
	{
		if (source.getNavigationNode() != null)
		{
			target.setNavigationNode(source.getNavigationNode().getUid());
		}

		if (source.getWrapAfter() != null)
		{
			target.setWrapAfter(Integer.toString(source.getWrapAfter()));
		}
	}

}
