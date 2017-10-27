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
package de.hybris.platform.cmssmarteditwebservices.structures.converter;

import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Converter use to convert a <code>ComposedTypeModel</code> to a <code>ComponentTypeData</code>.
 */
public interface TypeStructureConverter<S extends ComposedTypeModel, T extends ComponentTypeData> extends Converter<S, T>
{
	/**
	 * Converts a model object into a data object for a given structure type mode.
	 *
	 * @param source
	 *           - the source model object
	 * @param mode
	 *           - the structure type mode
	 * @return a data object
	 * @throws ConversionException
	 *            when an error occurs during the conversion process
	 */
	T convert(final S source, final StructureTypeMode mode) throws ConversionException;

	/**
	 * Converts a model object into a data object for a given structure type mode.
	 *
	 * @param source
	 *           - the source model object
	 * @param target
	 *           - the target data object to be enhanced
	 * @param mode
	 *           - the structure type mode
	 * @return the target data object
	 * @throws ConversionException
	 *            when an error occurs during the conversion process
	 */
	T convert(final S source, final T target, StructureTypeMode mode) throws ConversionException;

}
