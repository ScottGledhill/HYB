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
package de.hybris.platform.cmssmarteditwebservices.structures.facade.impl;

import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.converter.TypeStructureConverter;
import de.hybris.platform.cmssmarteditwebservices.structures.facade.StructureFacade;
import de.hybris.platform.cmssmarteditwebservices.structures.service.ComponentTypeAttributeDataComparatorRegistry;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default SmartEdit implementation of {@link ComponentTypeFacade} by providing field ordering, marking a field as
 * editable or not, and listing only the fields needed for a given mode.
 */
public class DefaultStructureFacade implements StructureFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultStructureFacade.class);

	private static final String ABSTRACT = "Abstract";

	private TypeService typeService;
	private TypeStructureRegistry typeStructureRegistry;
	private Set<String> cmsSupportedAbstractTypecodes;

	private TypeStructureConverter<ComposedTypeModel, ComponentTypeData> typeStructureConverter;
	private ComponentTypeAttributeDataComparatorRegistry componentTypeAttributeDataComparatorRegistry;

	@Override
	public List<ComponentTypeData> getComponentTypesByCode(final String code) throws ComponentTypeNotFoundException
	{
		final ComposedTypeModel componentType = getComponentType(code);

		Set<StructureTypeMode> structureModes = getTypeStructureRegistry().getStructureModes(code);
		if (structureModes.isEmpty())
		{
			structureModes = getTypeStructureRegistry().getAbstractStructureModes(componentType.getItemtype());
		}

		final List<ComponentTypeData> componentTypes = structureModes.stream().filter(mode -> mode != StructureTypeMode.BASE)
				.map(mode -> getOptionalComponentTypeByCodeAndMode(code, mode)).map(optional -> optional.get())
				.collect(Collectors.toList());

		return componentTypes;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The type attributes are ordered in the order specified by the associated {@link ComponentTypeAttributeDataComparator}.
	 */
	@Override
	public ComponentTypeData getComponentTypeByCodeAndMode(final String code, final String mode)
			throws ComponentTypeNotFoundException
	{
		final StructureTypeMode modeEnum = StructureTypeMode.valueOf(mode);
		final ComponentTypeData componentTypeData = getComponentTypeByCodeAndModeUnordered(code, modeEnum);
		// order the list of attributes
		getAttributeComparator(code, modeEnum, componentTypeData)
				.ifPresent(comparator -> componentTypeData.getAttributes().sort(comparator));

		return componentTypeData;
	}

	/**
	 * Get a single component type structure for a given structure type mode. The type attributes are not ordered using the
	 * {@link ComponentTypeAttributeDataComparator}.
	 * @param code
	 *           - the type code of the component type to retrieve
	 * @param mode
	 *           - the mode of the structure type
	 * @return the component type structure
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing types
	 */
	protected ComponentTypeData getComponentTypeByCodeAndModeUnordered(final String code, final StructureTypeMode mode)
			throws ComponentTypeNotFoundException
	{
		final ComposedTypeModel componentType = getComponentType(code);
		// if no type structure was defined for the given mode, the structure for mode DEFAULT of the given type will be returned
		final ComponentTypeData componentTypeData = getTypeStructureConverter().convert(componentType, mode);

		// include abstract type attributes
		getAbstractTypesForComponentAndMode(componentTypeData, mode)
				.forEach(abstractType -> augmentTypeAttributes(componentTypeData, abstractType));

		return componentTypeData;
	}

	/**
	 * Gets the {@link ComponentTypeAttributeDataComparator} for the given type code and mode. If no comparator is found
	 * matching the mode for the specified type code, return the comparator for the {@link StructureTypeMode#DEFAULT}
	 * mode instead. If no comparator is found matching the type code, return {@link Optional#empty()}.
	 * @param code
	 *           - the type code
	 * @param mode
	 *           - the structure type mode
	 * @return the comparator for the given type code and mode; never <tt>null</tt>
	 */
	protected Optional<ComponentTypeAttributeDataComparator> getAttributeComparator(final String code,
			final StructureTypeMode mode, final ComponentTypeData componentTypeData)
	{
		Optional<ComponentTypeAttributeDataComparator> comparator = getComponentTypeAttributeDataComparatorRegistry()
				.getComparatorForTypecode(code, mode);
		if (!comparator.isPresent())
		{
			comparator = getComponentTypeAttributeDataComparatorRegistry().getComparatorForTypecode(code, StructureTypeMode.DEFAULT);
		}

		if (!comparator.isPresent() && !code.startsWith(ABSTRACT))
		{
			final Set<TypeStructure> abstractStructures = getAbstractTypeStructureForCategory(componentTypeData.getCategory());
			final Optional<TypeStructure> abstractStructure = abstractStructures.stream().findFirst();
			if (abstractStructure.isPresent())
			{
				comparator = getAttributeComparator(abstractStructure.get().getTypecode(), mode, componentTypeData);
			}
		}

		return comparator;
	}

	/**
	 * Gets the {@link ComposedTypeModel} for a given type code.
	 * @param code
	 *           - the type code
	 * @return a composed type model
	 * @throws ComponentTypeNotFoundException
	 *            when the provided type code is not valid
	 */
	protected ComposedTypeModel getComponentType(final String code) throws ComponentTypeNotFoundException
	{
		final ComposedTypeModel componentType = getTypeService().getComposedTypeForCode(code);
		if (componentType == null)
		{
			throw new ComponentTypeNotFoundException("Component type with code \"" + code + "\" was not found.");
		}
		return componentType;
	}

	/**
	 * Merges the type attributes from the abstract type to the child type
	 * @param componentType
	 *           - the child type which attributes will be augmented
	 * @param abstractType
	 *           - the abstract type which attributes will be added to the child type attributes
	 */
	protected void augmentTypeAttributes(final ComponentTypeData componentType, final ComponentTypeData abstractType)
	{
		final List<ComponentTypeAttributeData> attributes = new ArrayList<>(componentType.getAttributes());
		attributes.addAll(abstractType.getAttributes().stream()
				.filter(attribute -> !containsAttribute(attribute, componentType.getAttributes())).collect(Collectors.toList()));
		componentType.setAttributes(attributes);
	}

	/**
	 * Verifies that an attribute exists in the given list of attributes.
	 * @param attribute
	 *           - the attribute to check if its presence in the given list of attributes
	 * @param attributes
	 *           - the list of attributes against which the attribute is checked
	 * @return <tt>TRUE</tt> if the attribute is already defined in the list of attributes; <tt>FALSE</tt> otherwise
	 */
	protected boolean containsAttribute(final ComponentTypeAttributeData attribute,
			final List<ComponentTypeAttributeData> attributes)
	{
		return attributes.stream().filter(attr -> attr.getQualifier().equals(attribute.getQualifier())).findAny().isPresent();
	}

	/**
	 * Find all abstract types structure for the category defined in the given component type. If the componentType of an
	 * <code>abstract</code> type, an empty list is returned.
	 * @param componentType
	 *           - the component type specifying the category used for filtering
	 * @param mode
	 *           - the structure type mode
	 * @return all abstract types defined for a given category; never <tt>null</tt>
	 */
	protected List<ComponentTypeData> getAbstractTypesForComponentAndMode(final ComponentTypeData componentType,
			final StructureTypeMode mode)
	{
		if (getCmsSupportedAbstractTypecodes().contains(componentType.getCode()))
		{
			return Collections.emptyList();
		}
		else
		{
			final Set<TypeStructure> abstractTypeStructures = getAbstractTypeStructureForCategory(componentType.getCategory());

			// find structure for the given mode of the given abstract type
			Set<TypeStructure> abstractStructures = abstractTypeStructures.stream() //
					.filter(structure -> structure.getAttributesByModeMap().containsKey(mode)).collect(Collectors.toSet());
			// if none found, return the default mode type structure of the given abstract type
			if (abstractStructures.isEmpty())
			{
				abstractStructures = abstractTypeStructures.stream()
						.filter(structure -> structure.getAttributesByModeMap().containsKey(StructureTypeMode.DEFAULT))
						.collect(Collectors.toSet());
			}
			return abstractStructures.stream()
					.map(structure -> getOptionalComponentTypeByCodeAndModeUnordered(structure.getTypecode(), mode))
					.map(optional -> optional.get()).collect(Collectors.toList());
		}
	}

	/**
	 * Find all type structures representing an Abstract type in a given category
	 * @param category
	 *           - the {@link StructureTypeCategory}
	 * @return type structures
	 */
	protected Set<TypeStructure> getAbstractTypeStructureForCategory(final String category)
	{
		return getTypeStructureRegistry().getStructureTypes().stream() //
				.filter(structure -> getCmsSupportedAbstractTypecodes().contains(structure.getTypecode())) //
				.filter(structure -> structure.getCategory().name().equals(category)) //
				.collect(Collectors.toSet());
	}

	/**
	 * Get component type POJO from a typecode with ordered attributes; without the type attributes from the abstract type
	 * @param code
	 *           - the typecode to search for
	 * @param mode
	 *           - the mode of the context
	 * @return the component type POJO or {@link Optional#empty()} if none could be found
	 */
	protected Optional<ComponentTypeData> getOptionalComponentTypeByCodeAndMode(final String code, final StructureTypeMode mode)
	{
		try
		{
			return Optional.of(getComponentTypeByCodeAndMode(code, mode.name()));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			LOG.info(e.getMessage(), e);
			return Optional.empty();
		}
	}

	/**
	 * Get component type POJO from a typecode with unordered attributes; without the type attributes from the abstract type
	 * @param code
	 *           - the typecode to search for
	 * @param mode
	 *           - the mode of the context
	 * @return the component type POJO or {@link Optional#empty()} if none could be found
	 */
	protected Optional<ComponentTypeData> getOptionalComponentTypeByCodeAndModeUnordered(final String code,
			final StructureTypeMode mode)
	{
		try
		{
			return Optional.of(getComponentTypeByCodeAndModeUnordered(code, mode));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			LOG.info(e.getMessage(), e);
			return Optional.empty();
		}
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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

	protected Set<String> getCmsSupportedAbstractTypecodes()
	{
		return cmsSupportedAbstractTypecodes;
	}

	@Required
	public void setCmsSupportedAbstractTypecodes(final Set<String> cmsSupportedAbstractTypecodes)
	{
		this.cmsSupportedAbstractTypecodes = cmsSupportedAbstractTypecodes;
	}

	protected TypeStructureConverter<ComposedTypeModel, ComponentTypeData> getTypeStructureConverter()
	{
		return typeStructureConverter;
	}

	@Required
	public void setTypeStructureConverter(
			final TypeStructureConverter<ComposedTypeModel, ComponentTypeData> typeStructureConverter)
	{
		this.typeStructureConverter = typeStructureConverter;
	}

	protected ComponentTypeAttributeDataComparatorRegistry getComponentTypeAttributeDataComparatorRegistry()
	{
		return componentTypeAttributeDataComparatorRegistry;
	}

	@Required
	public void setComponentTypeAttributeDataComparatorRegistry(
			final ComponentTypeAttributeDataComparatorRegistry componentTypeAttributeDataComparatorRegistry)
	{
		this.componentTypeAttributeDataComparatorRegistry = componentTypeAttributeDataComparatorRegistry;
	}

}
