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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Maps;


/**
 * This populator will populate the cmsStructureEnumType custom property. This is a custom property that is used to
 * denote the class that an enum type is. TODO
 */
public class CmsStructureEnumTypeComponentTypeAttributePopulator
		implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{

	private Map<String, Map<String, String>> componentEnumStructureTypes;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException

	{
		final String value = getComponentEnumStructureTypes().getOrDefault(source.getEnclosingType().getCode(), Maps.newHashMap())
				.get(source.getQualifier());

		target.setCmsStructureEnumType(value);
	}


	protected Map<String, Map<String, String>> getComponentEnumStructureTypes()
	{
		return componentEnumStructureTypes;
	}


	@Required
	public void setComponentEnumStructureTypes(final Map<String, Map<String, String>> componentEnumStructureTypes)
	{
		this.componentEnumStructureTypes = componentEnumStructureTypes;
	}
}
