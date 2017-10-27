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

import de.hybris.platform.cmsfacades.enumdata.EnumDataFacade;
import de.hybris.platform.cmswebservices.data.EnumData;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of <code>EnumDataFacade</code>
 *
 * @deprecated since version 6.2
 */
@Deprecated
public class DefaultEnumDataFacade implements EnumDataFacade
{

	private EnumerationService enumerationService;
	private EnumDataConverter enumDataConverter;

	@Deprecated
	@Override
	public List<EnumData> getEnumValues(final String classToRetrieve)
	{
		try
		{
			@SuppressWarnings("rawtypes")
			final Class classToReturn = Class.forName(classToRetrieve);

			@SuppressWarnings("unchecked")
			final List<HybrisEnumValue> rawEnumList = getEnumerationService().getEnumerationValues(classToReturn);

			final List<EnumData> enums = new ArrayList<>(rawEnumList.size());
			rawEnumList.stream().forEach((rawEnum) -> {
				enums.add(enumDataConverter.convert((rawEnum)));
			});

			return enums;
		}
		catch (final ClassNotFoundException cnfe)
		{
			throw new IllegalArgumentException("Can not instantiate class " + classToRetrieve, cnfe);
		}
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected EnumDataConverter getEnumDataConverter()
	{
		return enumDataConverter;
	}

	@Required
	public void setEnumDataConverter(final EnumDataConverter enumDataConverter)
	{
		this.enumDataConverter = enumDataConverter;
	}

}
