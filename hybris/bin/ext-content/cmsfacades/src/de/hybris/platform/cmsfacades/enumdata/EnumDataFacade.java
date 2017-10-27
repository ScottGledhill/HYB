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
package de.hybris.platform.cmsfacades.enumdata;

import de.hybris.platform.cmswebservices.data.EnumData;

import java.util.List;


/**
 * EnumDataFacade is a facade layer for exposing <code>EnumData</code> types.
 *
 * @deprecated since version 6.2
 */
@Deprecated
public interface EnumDataFacade
{
	/**
	 * EnumDataList retrieves a list of <code>EnumData</code> objects that represents the possible values that a given
	 * enumeration may have.
	 *
	 * @param classToRetrieve
	 *           the class we wish to retrieve the values for.
	 * @return a <code>EnumData</code> list.
	 * @deprecated since version 6.2
	 */
	@Deprecated
	List<EnumData> getEnumValues(final String classToRetrieve);
}
