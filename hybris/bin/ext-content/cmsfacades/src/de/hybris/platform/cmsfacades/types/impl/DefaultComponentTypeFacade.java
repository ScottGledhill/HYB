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
package de.hybris.platform.cmsfacades.types.impl;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This implementation of the {@link ComponentTypeFacade} will get the {@link ComposedTypeModel} items and convert them
 * to DTOs.
 *
 * <p>
 * The types available are determined by using the {@link ComponentTypeStructureRegistry} to get all registered
 * component types.
 * </p>
 */
public class DefaultComponentTypeFacade implements ComponentTypeFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultComponentTypeFacade.class);

	private ComponentTypeStructureRegistry componentTypeStructureRegistry;
	private Converter<ComposedTypeModel, ComponentTypeData> componentTypeStructureConverter;
	private TypeService typeService;
	private Set<String> cmsSupportedAbstractTypecodes;

	@Override
	public List<ComponentTypeData> getAllComponentTypes()
	{
		return getComponentTypeStructureRegistry().getComponentTypeStructures().stream() //
				.filter(structure -> !structure.getTypecode().equals(AbstractCMSComponentModel._TYPECODE))
				.map(structure -> getOptionalComponentTypeByCode(structure.getTypecode())) //
				.filter(optional -> optional.isPresent()) //
				.map(optional -> optional.get()) //
				.collect(Collectors.toList());
	}

	@Override
	public List<ComponentTypeData> getAllComponentTypes(final String category)
	{
		return getComponentTypeStructureRegistry().getComponentTypeStructures().stream() //
				.filter(structure -> !structure.getTypecode().equals(AbstractCMSComponentModel._TYPECODE))
				.map(structure -> getOptionalComponentTypeByCode(structure.getTypecode())) //
				.filter(optional -> optional.isPresent()) //
				.map(optional -> optional.get()) //
				.filter(componentTypeData -> StringUtils.equals(category, componentTypeData.getCategory()))
				.collect(Collectors.toList());
	}

	@Override
	public ComponentTypeData getComponentTypeByCode(final String code) throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = getComponentType(code);
		// include abstract type attributes
		getAbstractTypesForComponent(componentTypeData)
				.forEach(abstractType -> augmentTypeAttributes(componentTypeData, abstractType));
		return componentTypeData;
	}

	/**
	 * Merges the type attributes from the abstract type to the child type
	 *
	 * @param componentType
	 *           - the child type which attributes will be augmented
	 * @param abstractType
	 *           - the abstract type which attributes will be added to the child type attributes
	 */
	protected void augmentTypeAttributes(final ComponentTypeData componentType, final ComponentTypeData abstractType)
	{
		final List<ComponentTypeAttributeData> attributes = new ArrayList<>(componentType.getAttributes());
		attributes.addAll(abstractType.getAttributes().stream()
				.filter(attribute -> !containsAttribute(attribute, componentType.getAttributes()))
				.collect(Collectors.toList()));
		componentType.setAttributes(attributes);
	}

	/**
	 * Verifies that an attribute exists in the given list of attributes.
	 *
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

	protected ComponentTypeData getComponentType(final String code) throws ComponentTypeNotFoundException
	{
		final ComposedTypeModel componentType = getTypeService().getComposedTypeForCode(code);
		if (componentType == null)
		{
			throw new ComponentTypeNotFoundException("Component type with code \"" + code + "\" was not found.");
		}
		return getComponentTypeStructureConverter().convert(componentType);
	}

	/**
	 * Get component type POJO from a typecode; without the type attributes from the abstract type
	 *
	 * @param code
	 *           - the typecode to search for
	 * @return the component type POJO or {@link Optional#empty()} if none could be found
	 */
	protected Optional<ComponentTypeData> getOptionalComponentType(final String code)
	{
		try
		{
			return Optional.of(getComponentType(code));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			LOG.info(e.getMessage(), e);
			return Optional.empty();
		}
	}

	/**
	 * Find all abstract types structure for the category defined in the given component type
	 *
	 * @param componentType
	 *           - the component type specifying the category used for filtering
	 * @return all abstract types defined for a given category
	 */
	protected List<ComponentTypeData> getAbstractTypesForComponent(final ComponentTypeData componentType)
	{
		if (getCmsSupportedAbstractTypecodes().contains(componentType.getCode()))
		{
			return Collections.emptyList();
		}
		else
		{
			return getComponentTypeStructureRegistry().getComponentTypeStructures().stream() //
					.filter(structure -> getCmsSupportedAbstractTypecodes().contains(structure.getTypecode())
							&& structure.getCategory().name().equals(componentType.getCategory())) //
					.map(structure -> getOptionalComponentType(structure.getTypecode())) //
					.map(optional -> optional.get()).collect(Collectors.toList());
		}
	}

	/**
	 * Get component type POJO from a typecode; including the type attributes from the abstract type
	 *
	 * @param code
	 *           - the typecode to search for
	 * @return the component type POJO or {@link Optional#empty()} if none could be found
	 */
	protected Optional<ComponentTypeData> getOptionalComponentTypeByCode(final String code)
	{
		try
		{
			return Optional.of(getComponentTypeByCode(code));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			return Optional.empty();
		}
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

	protected Converter<ComposedTypeModel, ComponentTypeData> getComponentTypeStructureConverter()
	{
		return componentTypeStructureConverter;
	}

	@Required
	public void setComponentTypeStructureConverter(
			final Converter<ComposedTypeModel, ComponentTypeData> componentTypeStructureConverter)
	{
		this.componentTypeStructureConverter = componentTypeStructureConverter;
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

	protected Set<String> getCmsSupportedAbstractTypecodes()
	{
		return cmsSupportedAbstractTypecodes;
	}

	@Required
	public void setCmsSupportedAbstractTypecodes(final Set<String> cmsSupportedAbstractTypecodes)
	{
		this.cmsSupportedAbstractTypecodes = cmsSupportedAbstractTypecodes;
	}

}
