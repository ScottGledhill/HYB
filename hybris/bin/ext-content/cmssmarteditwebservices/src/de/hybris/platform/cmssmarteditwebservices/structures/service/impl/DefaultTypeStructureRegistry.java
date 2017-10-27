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
package de.hybris.platform.cmssmarteditwebservices.structures.service.impl;

import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import jersey.repackaged.com.google.common.collect.Sets;


/**
 * Default implementation of the <code>TypeStructureRegistry</code>. This implementation uses autowire-by-type to inject
 * all beans implementing {@link ComponentTypeStructure}, {@link ComponentTypeAttributeStructure} and
 * {@link TypeAttributeStructure}.
 */
public class DefaultTypeStructureRegistry implements TypeStructureRegistry, InitializingBean
{
	@Autowired
	private Set<ComponentTypeStructure> allComponentTypeStructures;
	@Autowired
	private Set<ComponentTypeAttributeStructure> allComponentTypeAttributeStructures;
	@Autowired
	private Set<TypeAttributeStructure> allTypeAttributeStructures;

	private FacadeValidationService facadeValidationService;
	private Validator structureTypesPostCreationValidator;
	private TypeService typeService;
	private Set<String> cmsSupportedAbstractTypecodes;

	private final Map<String, TypeStructure> structureTypeMap = new HashMap<>();

	@Override
	public Set<TypeAttributeStructure> getStructureTypeAttributesByMode(final String typecode, final String mode)
	{
		return Optional
				.ofNullable(getStructureTypeMap().get(typecode).getAttributesByModeMap().get(StructureTypeMode.valueOf(mode)))
				.orElse(Collections.emptySet());
	}

	@Override
	public TypeStructure getTypeStructure(final String typecode)
	{
		return getStructureTypeMap().get(typecode);
	}

	@Override
	public TypeStructure getAbstractTypeStructure(final String itemtype)
	{
		final Optional<String> abstractTypecode = getCmsSupportedAbstractTypecodes().stream()
				.filter(typecode -> matchesComposedType(typecode, itemtype)).findFirst();
		return abstractTypecode.map(typecode -> getStructureTypeMap().get(typecode)).orElse(null);
	}

	/**
	 * Verifies that the given typecode has the same <code>ComposedType</code> as the provided itemtype.
	 * @param typecode
	 *           - the typecode which <code>ComposedType</code> will be compared
	 * @param itemtype
	 *           - the itemtype used for comparison
	 * @return <tt>TRUE</tt> if the <code>ComposedType</code> of the given typecode matches the itemtype specified;
	 *         <tt>FALSE</tt> otherwise
	 */
	protected boolean matchesComposedType(final String typecode, final String itemtype)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typecode);
		return composedType.getItemtype().equals(itemtype);
	}

	@Override
	public Collection<TypeStructure> getStructureTypes()
	{
		return getStructureTypeMap().values();
	}

	/**
	 * {@inheritDoc} (except the {@link StructureTypeMode#BASE} mode)
	 */
	@Override
	public Set<StructureTypeMode> getStructureModes(final String typecode)
	{
		final Set<StructureTypeMode> modes = Optional.ofNullable(getTypeStructure(typecode)) //
				.map(typeStructure -> typeStructure.getAttributesByModeMap().keySet())
				.orElse(Collections.emptySet());
		return modes.stream().filter(mode -> mode != StructureTypeMode.BASE).collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc} (except the {@link StructureTypeMode#BASE} mode)
	 */
	@Override
	public Set<StructureTypeMode> getAbstractStructureModes(final String itemtype)
	{
		final Set<StructureTypeMode> modes = Optional.ofNullable(getAbstractTypeStructure(itemtype)) //
				.map(typeStructure -> typeStructure.getAttributesByModeMap().keySet())
				.orElse(Collections.emptySet());
		return modes.stream().filter(mode -> mode != StructureTypeMode.BASE).collect(Collectors.toSet());
	}

	/**
	 * Suppress sonar warning (squid:S2095 | Resources should be closed ) : Stream.of() does not hold a resource.
	 */
	@SuppressWarnings("squid:S2095")
	@Override
	public void afterPropertiesSet() throws Exception
	{
		getAllComponentTypeStructures().stream().map(type -> new DefaultTypeStructure(type))
		.forEach(type -> putOrUpdateStructureType(type));

		// merge the populators from base componentTypeStructures (cmsfacades)
		final Set<DefaultTypeAttributeStructure> baseAttributes = getAllComponentTypeAttributeStructures().stream() //
				.filter(attribute -> attribute.getClass().equals(DefaultComponentTypeAttributeStructure.class))
				.map(attribute -> new DefaultTypeAttributeStructure(attribute)).collect(Collectors.toSet()); //

		Stream.of(baseAttributes, getAllTypeAttributeStructures()).flatMap(attributes -> attributes.stream())
		.forEach(attribute -> updateAttributes(attribute));

		// create DEFAULT mode structure if only BASE mode exists
		createDefaultModeTypeStructure();

		// post-validates the structure type
		getAllComponentTypeStructures().stream().forEach(
				componentType -> getFacadeValidationService().validate(getStructureTypesPostCreationValidator(), componentType));
	}

	/**
	 * Create a type structure for DEFAULT mode from the BASE mode type structure when only the BASE mode type structure exists.
	 */
	protected void createDefaultModeTypeStructure()
	{
		getStructureTypeMap().values().stream() //
		.filter(typeStructure -> typeStructure.getAttributesByModeMap().keySet().size() == 1
		&& typeStructure.getAttributesByModeMap().keySet().contains(StructureTypeMode.BASE)) //
		.forEach(typeStructure ->
		{
			final Set<TypeAttributeStructure> defaultAttributes = typeStructure.getAttributesByModeMap()
					.get(StructureTypeMode.BASE).stream()
					.map(attribute -> new DefaultTypeAttributeStructure(attribute, StructureTypeMode.DEFAULT))
					.collect(Collectors.toSet());

			typeStructure.getAttributesByModeMap().put(StructureTypeMode.DEFAULT, defaultAttributes);
		});
	}


	/**
	 * If the map of component type structures is empty, then add this element to the map. Otherwise, update the set of
	 * populators for the element found in the map by adding the populators from the given element.
	 * @param type
	 *           - the component type structure to process
	 */
	protected void putOrUpdateStructureType(final TypeStructure type)
	{
		final Optional<TypeStructure> typeStructure = Optional.ofNullable(getStructureTypeMap().get(type.getTypecode()));
		typeStructure.ifPresent(internal -> internal.getPopulators().addAll(type.getPopulators()));

		if (!typeStructure.isPresent())
		{
			getStructureTypeMap().put(type.getTypecode(), type);
		}
	}


	/**
	 * If the component type structure matching the typecode given in the attribute does not have any attributes with the
	 * same qualifier as the given attribute, then add the attribute to the collection of attributes of the component
	 * type structure. Otherwise, if there is an attribute with the same qualifier already in the collection of
	 * attributes, then we add the populators of the given attribute to that attribute.
	 * @param attribute
	 *           - the attribute to process
	 * @throws IllegalArgumentException
	 *            when the typecode provided in the attributes does not match any component type structure in the map.
	 */
	protected void updateAttributes(final TypeAttributeStructure attribute) throws IllegalArgumentException
	{
		final TypeStructure type = getStructureTypeMap().get(attribute.getTypecode());
		if (type == null)
		{
			throw new IllegalArgumentException("No StructureType with code \"" + attribute.getTypecode() + "\".");
		}

		Optional<Set<TypeAttributeStructure>> allAttributes = Optional
				.ofNullable(type.getAttributesByModeMap().get(attribute.getMode()));

		final Optional<TypeAttributeStructure> attributeInType;
		if (allAttributes.isPresent())
		{
			attributeInType = allAttributes.get().stream() //
					.filter(attr -> attr.getQualifier().equals(attribute.getQualifier())) //
					.findFirst();
		}
		else
		{
			// get base attribute (defined in cmsfacades) and its populators
			final Optional<TypeAttributeStructure> baseAttribute = Optional
					.ofNullable(type.getAttributesByModeMap().get(StructureTypeMode.BASE)).orElse(Collections.emptySet()).stream() //
					.filter(attr -> attr.getTypecode().equals(attribute.getTypecode())) //
					.filter(attr -> attr.getQualifier().equals(attribute.getQualifier())).findFirst();
			// if base attributes exists for the given typecode and qualifier, add the attribute for the given mode to the ModeMap
			baseAttribute.ifPresent(attr ->
			{
				final TypeAttributeStructure copyAttribute = new DefaultTypeAttributeStructure(attr, attribute.getMode());
				type.getAttributesByModeMap().put(attribute.getMode(), Sets.newHashSet(copyAttribute));
			});

			allAttributes = Optional.ofNullable(type.getAttributesByModeMap().get(attribute.getMode()));
			if (allAttributes.isPresent())
			{
				attributeInType = allAttributes.get().stream() //
						.filter(attr -> attr.getQualifier().equals(attribute.getQualifier())) //
						.findFirst();
			}
			else
			{
				attributeInType = Optional.empty();
			}
		}

		if (attributeInType.isPresent())
		{
			attributeInType.get().getPopulators().addAll(attribute.getPopulators());
		}
		else
		{
			final Set<TypeAttributeStructure> attributeSet = (type.getAttributesByModeMap().containsKey(attribute.getMode())
					? type.getAttributesByModeMap().get(attribute.getMode())
							: new HashSet<>());

			// get base attribute (defined in cmsfacades) and its populators
			final Optional<TypeAttributeStructure> baseAttribute = Optional
					.ofNullable(type.getAttributesByModeMap().get(StructureTypeMode.BASE)).orElse(Collections.emptySet()).stream() //
					.filter(attr -> attr.getTypecode().equals(attribute.getTypecode())) //
					.filter(attr -> attr.getQualifier().equals(attribute.getQualifier())).findFirst();
			// if base attributes exists for the given typecode and qualifier, add the attribute for the given mode to the ModeMap
			baseAttribute.ifPresent(attr ->
			{
				final TypeAttributeStructure copyAttribute = new DefaultTypeAttributeStructure(attr, attribute.getMode());
				copyAttribute.getPopulators().addAll(attribute.getPopulators());
				attributeSet.add(copyAttribute);
				type.getAttributesByModeMap().put(attribute.getMode(), attributeSet);
			});

			if (!baseAttribute.isPresent())
			{
				attributeSet.add(attribute);
				type.getAttributesByModeMap().put(attribute.getMode(), attributeSet);
			}
		}
	}

	protected Set<ComponentTypeStructure> getAllComponentTypeStructures()
	{
		return allComponentTypeStructures;
	}

	public void setAllComponentTypeStructures(final Set<ComponentTypeStructure> allComponentTypeStructures)
	{
		this.allComponentTypeStructures = allComponentTypeStructures;
	}

	protected Set<ComponentTypeAttributeStructure> getAllComponentTypeAttributeStructures()
	{
		return allComponentTypeAttributeStructures;
	}

	public void setAllComponentTypeAttributeStructures(
			final Set<ComponentTypeAttributeStructure> allComponentTypeAttributeStructures)
	{
		this.allComponentTypeAttributeStructures = allComponentTypeAttributeStructures;
	}

	protected Set<TypeAttributeStructure> getAllTypeAttributeStructures()
	{
		return allTypeAttributeStructures;
	}

	public void setAllTypeAttributeStructures(final Set<TypeAttributeStructure> allTypeAttributeStructures)
	{
		this.allTypeAttributeStructures = allTypeAttributeStructures;
	}

	protected Map<String, TypeStructure> getStructureTypeMap()
	{
		return structureTypeMap;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Validator getStructureTypesPostCreationValidator()
	{
		return structureTypesPostCreationValidator;
	}

	@Required
	public void setStructureTypesPostCreationValidator(final Validator structureTypesPostCreationValidator)
	{
		this.structureTypesPostCreationValidator = structureTypesPostCreationValidator;
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

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
