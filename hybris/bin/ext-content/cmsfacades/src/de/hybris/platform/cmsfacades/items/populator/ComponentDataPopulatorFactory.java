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
package de.hybris.platform.cmsfacades.items.populator;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.converters.Populator;

import java.util.Optional;


/**
 * CMS Populator Factory will be responsible for retrieving the populator for a specific CMS Component Model.
 */
public interface ComponentDataPopulatorFactory
{
	/**
	 * Get Populator will receive a type Class and create the corresponding Populator.
	 *
	 * @param aClass
	 *           the CMS Component Model class
	 * @return the populator implementation
	 */
	Optional<Populator<AbstractCMSComponentData, AbstractCMSComponentModel>> getPopulator(
			Class<? extends AbstractCMSComponentModel> aClass);
}
