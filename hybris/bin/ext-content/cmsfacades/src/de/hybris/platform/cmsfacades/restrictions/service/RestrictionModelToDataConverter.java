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
package de.hybris.platform.cmsfacades.restrictions.service;

import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;


/**
 * Represents meta-information about a <code>AbstractRestrictionModel</code> class and the associated converter.
 */
public interface RestrictionModelToDataConverter
{
	/**
	 * Get the typecode identifying the <code>AbstractRestrictionModel</code>.
	 *
	 * @return the typecode
	 */
	String getTypecode();

	/**
	 * Get the converter to be applied.
	 *
	 * @return the converter to be applied for the type {@code RestrictionDataToModelConverter#getTypecode}
	 */
	AbstractPopulatingConverter<AbstractRestrictionModel, AbstractRestrictionData> getConverter();
}
