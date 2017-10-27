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
package de.hybris.platform.cmsfacades.types.converter;

import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter use to convert a <code>ComposedTypeModel</code> to a <code>ComponentTypeData</code>.
 */
public class ComponentTypeStructureConverter implements Converter<ComposedTypeModel, ComponentTypeData>
{

	private ComponentTypeStructureRegistry componentTypeStructureRegistry;

	private StringDecapitalizer stringDecapitalizer;
	private ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory;

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source) throws ConversionException
	{
		return convert(source, new ComponentTypeData());
	}

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		// Get structure type
		final ComponentTypeStructure structureType = Optional
				.ofNullable(getComponentTypeStructureRegistry().getComponentTypeStructure(source.getCode()))
				.orElse(getComponentTypeStructureRegistry().getAbstractComponentTypeStructure(source.getItemtype()));

		// Populate component type properties
		structureType.getPopulators().forEach(populator -> populator.populate(source, target));

		// Convert attributes
		target.setAttributes(structureType.getAttributes().stream() //
				.map(attribute -> convertAttribute(attribute, getAttributeDescriptor(source, attribute.getQualifier()))) //
				.filter(optional -> optional.isPresent()) //
				.map(optional -> optional.get()) //
				.collect(Collectors.toList()));

		target.setCategory(structureType.getCategory().name());

		getStringDecapitalizer() //
		.decapitalize(structureType.getTypeDataClass()) //
		.ifPresent(typeData -> target.setType(typeData));
		return target;
	}

	/**
	 * Get <code>AttributeDescriptor</code> matching with the given qualifier for the type provided.
	 * @param type
	 *           - the composed type model in which to search for the descriptor
	 * @param qualifier
	 *           - the name of the descriptor to search for
	 * @return the attribute descriptor matching the given criteria
	 */
	protected Optional<AttributeDescriptorModel> getAttributeDescriptor(final ComposedTypeModel type, final String qualifier)
	{
		return Stream.of(type.getDeclaredattributedescriptors(), type.getInheritedattributedescriptors())
				.flatMap(Collection::stream).filter(attribute -> attribute.getQualifier().equals(qualifier)).findAny();
	}

	/**
	 * Convert the attribute descriptor to a POJO using the structure attribute's populators.
	 * <p>
	 * NOTE: If the attribute descriptor is empty, then no conversion occurs and the method returns
	 * {@link Optional#empty()}.
	 * </p>
	 * @param attribute
	 *           - the structure type attribute
	 * @param attributeDescriptor
	 *           - the attribute descriptor
	 * @return the component type attribute
	 */
	protected Optional<ComponentTypeAttributeData> convertAttribute(final ComponentTypeAttributeStructure attribute,
			final Optional<AttributeDescriptorModel> attributeDescriptor)
	{
		return attributeDescriptor.map(element -> convertAttribute(attribute, element));
	}

	/**
	 * Convert the attribute descriptor to a POJO using the structure attribute's populators.
	 * @param attribute
	 *           - the structure type attribute
	 * @param attributeDescriptor
	 *           - the attribute descriptor
	 * @return the component type attribute POJO
	 */
	protected ComponentTypeAttributeData convertAttribute(final ComponentTypeAttributeStructure attribute,
			final AttributeDescriptorModel attributeDescriptor)
	{
		final ComponentTypeAttributeData target = getComponentTypeAttributeDataFactory().getObject();
		attribute.getPopulators().forEach(populator -> populator.populate(attributeDescriptor, target));
		return target;
	}

	protected ComponentTypeStructureRegistry getComponentTypeStructureRegistry()
	{
		return componentTypeStructureRegistry;
	}

	@Required
	public void setComponentTypeStructureRegistry(final ComponentTypeStructureRegistry componentTypeStructureRegistry)
	{
		this.componentTypeStructureRegistry = componentTypeStructureRegistry;
	}

	protected StringDecapitalizer getStringDecapitalizer()
	{
		return stringDecapitalizer;
	}

	@Required
	public void setStringDecapitalizer(final StringDecapitalizer stringDecapitalizer)
	{
		this.stringDecapitalizer = stringDecapitalizer;
	}

	protected ObjectFactory<ComponentTypeAttributeData> getComponentTypeAttributeDataFactory()
	{
		return componentTypeAttributeDataFactory;
	}

	@Required
	public void setComponentTypeAttributeDataFactory(
			final ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory)
	{
		this.componentTypeAttributeDataFactory = componentTypeAttributeDataFactory;
	}
}
