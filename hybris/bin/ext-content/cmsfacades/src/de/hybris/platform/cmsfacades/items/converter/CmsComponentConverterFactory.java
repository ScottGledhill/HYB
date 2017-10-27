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
package de.hybris.platform.cmsfacades.items.converter;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Optional;


/**
 * CMS Converter Factory will be responsible for retrieving the converter for a specific CMS Component Model.
 */
public interface CmsComponentConverterFactory
{
	/**
	 * Get Converter will receive a type Class and create the corresponding Converter.
	 *
	 * @param classType
	 *           the CMS Component Model class
	 * @return the converter implementation
	 */
	Optional<AbstractPopulatingConverter<AbstractCMSComponentModel, AbstractCMSComponentData>> getConverter(
			Class<? extends AbstractCMSComponentModel> classType);
}
