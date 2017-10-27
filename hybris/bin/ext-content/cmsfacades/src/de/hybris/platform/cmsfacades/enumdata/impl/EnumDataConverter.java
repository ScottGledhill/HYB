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
package de.hybris.platform.cmsfacades.enumdata.impl;


import de.hybris.platform.cmswebservices.data.EnumData;
import de.hybris.platform.core.HybrisEnumValue;

import org.springframework.core.convert.converter.Converter;


/**
 * EnumDataConverter is a converter for adapting from a <code>HybrisEnumValue</code> to a <code>EnumData</code>
 * representation.
 *
 * @deprecated since version 6.2
 */
@Deprecated
public class EnumDataConverter implements Converter<HybrisEnumValue, EnumData>
{

	@Deprecated
	@Override
	public EnumData convert(final HybrisEnumValue enumToConvert)
	{
		final EnumData convertedEnum = new EnumData();
		convertedEnum.setCode(enumToConvert.getCode());
		convertedEnum.setLabel(enumToConvert.getCode());
		return convertedEnum;
	}
}
