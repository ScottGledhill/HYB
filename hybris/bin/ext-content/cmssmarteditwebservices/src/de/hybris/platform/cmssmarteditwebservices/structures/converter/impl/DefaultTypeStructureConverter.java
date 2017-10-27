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
package de.hybris.platform.cmssmarteditwebservices.structures.converter.impl;

import de.hybris.platform.cmsfacades.types.converter.ComponentTypeStructureConverter;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.converter.TypeStructureConverter;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link TypeStructureConverter}. The converter's strategy when retrieving the attributes is
 * to return all attributes defined for the <code>DEFAULT</code> structure type mode if no attributes were found for the
 * specified mode.
 */
public class DefaultTypeStructureConverter extends ComponentTypeStructureConverter
implements TypeStructureConverter<ComposedTypeModel, ComponentTypeData>
{
	private TypeStructureRegistry typeStructureRegistry;
	private ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory;

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source) throws ConversionException
	{
		return convert(source, new ComponentTypeData(), StructureTypeMode.DEFAULT);
	}

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		return convert(source, target, StructureTypeMode.DEFAULT);
	}

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source, final StructureTypeMode mode) throws ConversionException
	{
		return convert(source, new ComponentTypeData(), mode);
	}

	@Override
	public ComponentTypeData convert(final ComposedTypeModel source, final ComponentTypeData target, final StructureTypeMode mode)
			throws ConversionException
	{
		Preconditions.checkArgument(Objects.nonNull(mode), "Mode cannot be null.");

		// Get structure type
		final TypeStructure structureType = Optional.ofNullable(getTypeStructureRegistry().getTypeStructure(source.getCode()))
				.orElse(getTypeStructureRegistry().getAbstractTypeStructure(source.getItemtype()));

		// Populate component type properties
		structureType.getPopulators().forEach(populator -> populator.populate(source, target));

		// Convert attributes
		final Set<TypeAttributeStructure> attributes = findAttributesOrGetDefault(structureType, mode);
		if (Objects.nonNull(attributes))
		{
			target.setAttributes(attributes.stream() //
					.map(attribute -> convertAttribute(attribute, getAttributeDescriptor(source, attribute.getQualifier()), mode)) //
					.filter(optional -> optional.isPresent()) //
					.map(optional -> optional.get()) //
					.collect(Collectors.toList()));
		}
		target.setCategory(structureType.getCategory().name());

		getStringDecapitalizer().decapitalize(structureType.getTypeDataClass()) //
		.ifPresent(typeData -> target.setType(typeData));
		return target;
	}

	/**
	 * Find all structure type attributes defined for the given mode. If none were found, it returns the attributes
	 * defined for the <code>DEFAULT</code>.
	 *
	 * @param structureType
	 *           - the structure type containing the attributes (classified by mode)
	 * @param mode
	 *           - the structure type mode
	 * @return the structure type attributes
	 */
	protected Set<TypeAttributeStructure> findAttributesOrGetDefault(final TypeStructure structureType,
			final StructureTypeMode mode)
	{
		Set<TypeAttributeStructure> attributes = structureType.getAttributesByModeMap().get(mode);
		if (Objects.isNull(attributes) && mode != StructureTypeMode.DEFAULT)
		{
			attributes = structureType.getAttributesByModeMap().get(StructureTypeMode.DEFAULT);
		}
		return attributes;
	}

	/**
	 * Convert the attribute descriptor to a POJO using the structure attribute's populators.
	 * <p>
	 * NOTE: If the attribute descriptor is empty, then no conversion occurs and the method returns
	 * {@link Optional#empty()}.
	 * </p>
	 *
	 * @param attribute
	 *           - the structure type attribute
	 * @param attributeDescriptor
	 *           - the attribute descriptor
	 * @param mode
	 *           - the mode of the context
	 * @return the component type attribute
	 */
	protected Optional<ComponentTypeAttributeData> convertAttribute(final ComponentTypeAttributeStructure attribute,
			final Optional<AttributeDescriptorModel> attributeDescriptor, final StructureTypeMode mode)
	{
		return attributeDescriptor.map(element -> convertAttribute(attribute, element, mode));
	}

	/**
	 * Convert the attribute descriptor to a POJO using the structure attribute's populators.
	 *
	 * @param attribute
	 *           - the structure type attribute
	 * @param attributeDescriptor
	 *           - the attribute descriptor
	 * @param mode
	 *           - the mode of the context
	 * @return the component type attribute POJO
	 */
	protected ComponentTypeAttributeData convertAttribute(final ComponentTypeAttributeStructure attribute,
			final AttributeDescriptorModel attributeDescriptor, final StructureTypeMode mode)
	{
		final ComponentTypeAttributeData target = getComponentTypeAttributeDataFactory().getObject();
		attribute.getPopulators().forEach(populator -> populator.populate(attributeDescriptor, target));
		target.setMode(mode.name());
		return target;
	}

	protected TypeStructureRegistry getTypeStructureRegistry()
	{
		return typeStructureRegistry;
	}

	@Required
	public void setTypeStructureRegistry(final TypeStructureRegistry typeStructureRegistry)
	{
		this.typeStructureRegistry = typeStructureRegistry;
	}

	protected ObjectFactory<ComponentTypeAttributeData> getComponentTypeAttributeDataFactory()
	{
		return componentTypeAttributeDataFactory;
	}

	@Required
	public void setComponentTypeAttributeDataFactory(final ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory)
	{
		this.componentTypeAttributeDataFactory = componentTypeAttributeDataFactory;
	}

}
