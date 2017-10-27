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

import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.ADD;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.BASE;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.EDIT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultComponentTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTypeStructureRegistryTest
{
	private static final String INVALID = "INVALID";
	private static final String TYPECODE_A = "A";
	private static final String TYPECODE_B = "B";
	private static final String TYPECODE_C = "C";

	private static final String QUALIFIER_1 = "1";
	private static final String QUALIFIER_2 = "2";
	private static final String QUALIFIER_3 = "3";

	private TypeStructure defaultType;
	private TypeStructure typeA;
	private TypeStructure typeB;
	private TypeStructure typeC;

	private ComponentTypeAttributeStructure attributeA1;
	private ComponentTypeAttributeStructure attributeA2;
	private TypeAttributeStructure attributeA1Default;
	private TypeAttributeStructure attributeA2Default;
	private TypeAttributeStructure attributeA2Base;
	private TypeAttributeStructure attributeA2Add;
	private TypeAttributeStructure attributeA3Add;
	private TypeAttributeStructure attributeB1Default;
	private TypeAttributeStructure attributeB2Default;
	private TypeAttributeStructure attributeC1Default;

	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> pop1;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> pop2;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> pop3;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> pop4;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> basePop;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> popA;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> popB;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> popC;

	@InjectMocks
	private DefaultTypeStructureRegistry registry;

	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private Validator structureTypesPostCreationValidator;

	@Before
	public void setUp()
	{
		defaultType = new DefaultTypeStructure();
		defaultType.setTypecode(AbstractRestrictionModel._TYPECODE);
		defaultType.setPopulators(Sets.newHashSet(pop1, pop2));

		typeA = new DefaultTypeStructure();
		typeA.setTypecode(TYPECODE_A);
		typeA.setPopulators(Sets.newHashSet(pop1, pop2));

		typeB = new DefaultTypeStructure();
		typeB.setTypecode(TYPECODE_B);
		typeB.setPopulators(Sets.newHashSet(pop2, pop3, pop4));

		typeC = new DefaultTypeStructure();
		typeC.setTypecode(TYPECODE_C);
		typeC.setPopulators(Sets.newHashSet(pop1, pop2));

		attributeA1 = new DefaultComponentTypeAttributeStructure();
		attributeA1.setTypecode(TYPECODE_A);
		attributeA1.setQualifier(QUALIFIER_1);
		attributeA1.setPopulators(Sets.newHashSet(basePop));

		attributeA2 = new DefaultComponentTypeAttributeStructure();
		attributeA2.setTypecode(TYPECODE_A);
		attributeA2.setQualifier(QUALIFIER_2);
		attributeA2.setPopulators(Sets.newHashSet(basePop));

		attributeA1Default = new DefaultTypeAttributeStructure();
		attributeA1Default.setTypecode(TYPECODE_A);
		attributeA1Default.setQualifier(QUALIFIER_1);
		attributeA1Default.setMode(DEFAULT);
		attributeA1Default.setPopulators(Sets.newHashSet(popA, popB));

		attributeA2Default = new DefaultTypeAttributeStructure();
		attributeA2Default.setTypecode(TYPECODE_A);
		attributeA2Default.setQualifier(QUALIFIER_2);
		attributeA2Default.setMode(DEFAULT);
		attributeA2Default.setPopulators(Sets.newHashSet(popA, popB));

		attributeA2Base = new DefaultTypeAttributeStructure();
		attributeA2Base.setTypecode(TYPECODE_A);
		attributeA2Base.setQualifier(QUALIFIER_2);
		attributeA2Base.setMode(BASE);
		attributeA2Base.setPopulators(Sets.newHashSet(popC));

		attributeA2Add = new DefaultTypeAttributeStructure();
		attributeA2Add.setTypecode(TYPECODE_A);
		attributeA2Add.setQualifier(QUALIFIER_2);
		attributeA2Add.setMode(ADD);
		attributeA2Add.setPopulators(Sets.newHashSet(popC));

		attributeA3Add = new DefaultTypeAttributeStructure();
		attributeA3Add.setTypecode(TYPECODE_A);
		attributeA3Add.setQualifier(QUALIFIER_3);
		attributeA3Add.setMode(ADD);
		attributeA3Add.setPopulators(Sets.newHashSet(popA, popB, popC));

		attributeB1Default = new DefaultTypeAttributeStructure();
		attributeB1Default.setTypecode(TYPECODE_B);
		attributeB1Default.setQualifier(QUALIFIER_1);
		attributeB1Default.setMode(DEFAULT);
		attributeB1Default.setPopulators(Sets.newHashSet(popA, popB));

		attributeB2Default = new DefaultTypeAttributeStructure();
		attributeB2Default.setTypecode(TYPECODE_B);
		attributeB2Default.setQualifier(QUALIFIER_2);
		attributeB2Default.setMode(DEFAULT);
		attributeB2Default.setPopulators(Sets.newHashSet(popA, popB));

		attributeC1Default = new DefaultTypeAttributeStructure();
		attributeC1Default.setTypecode(TYPECODE_C);
		attributeC1Default.setQualifier(QUALIFIER_1);
		attributeC1Default.setMode(DEFAULT);
		attributeC1Default.setPopulators(Sets.newHashSet(popC));
	}

	@Test
	public void shouldBuildElementsCorrectly_AllUseCasesCovered() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA, typeB, typeC));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA1Default, attributeA2Default, attributeA2Add,
				attributeA3Add, attributeB1Default, attributeB2Default, attributeC1Default));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA1, attributeA2));
		registry.afterPropertiesSet();

		final Map<String, TypeStructure> types = registry.getStructureTypeMap();

		assertThat(types.size(), equalTo(3));

		final TypeStructure resultTypeA = getType(TYPECODE_A);
		final TypeStructure resultTypeB = getType(TYPECODE_B);
		final TypeStructure resultTypeC = getType(TYPECODE_C);

		assertThat(resultTypeA.getTypecode(), equalTo(TYPECODE_A));
		assertThat(resultTypeB.getTypecode(), equalTo(TYPECODE_B));
		assertThat(resultTypeC.getTypecode(), equalTo(TYPECODE_C));

		assertThat(resultTypeA.getPopulators(), hasSize(2));
		assertThat(resultTypeA.getPopulators(), containsInAnyOrder(pop1, pop2));

		assertThat(resultTypeB.getPopulators(), hasSize(3));
		assertThat(resultTypeB.getPopulators(), containsInAnyOrder(pop2, pop3, pop4));

		assertThat(resultTypeC.getPopulators(), hasSize(2));
		assertThat(resultTypeC.getPopulators(), containsInAnyOrder(pop1, pop2));

		assertThat(resultTypeA.getAttributesByModeMap().values(), hasSize(3)); // has 2 modes : BASE, DEFAULT and ADD
		final TypeAttributeStructure resultAttrA1Default = getAttribute(TYPECODE_A, QUALIFIER_1, DEFAULT.name());
		assertThat(resultAttrA1Default.getTypecode(), equalTo(TYPECODE_A));
		assertThat(resultAttrA1Default.getQualifier(), equalTo(QUALIFIER_1));
		assertThat(resultAttrA1Default.getMode(), equalTo(DEFAULT));
		assertThat(resultAttrA1Default.getPopulators(), hasSize(3));
		assertThat(resultAttrA1Default.getPopulators(), containsInAnyOrder(basePop, popA, popB));

		final TypeAttributeStructure resultAttrA2Default = getAttribute(TYPECODE_A, QUALIFIER_2, DEFAULT.name());
		assertThat(resultAttrA2Default.getTypecode(), equalTo(TYPECODE_A));
		assertThat(resultAttrA2Default.getQualifier(), equalTo(QUALIFIER_2));
		assertThat(resultAttrA2Default.getMode(), equalTo(DEFAULT));
		assertThat(resultAttrA2Default.getPopulators(), hasSize(3));
		assertThat(resultAttrA2Default.getPopulators(), containsInAnyOrder(basePop, popA, popB));

		final TypeAttributeStructure resultAttrA2Add = getAttribute(TYPECODE_A, QUALIFIER_2, ADD.name());
		assertThat(resultAttrA2Add.getTypecode(), equalTo(TYPECODE_A));
		assertThat(resultAttrA2Add.getQualifier(), equalTo(QUALIFIER_2));
		assertThat(resultAttrA2Add.getMode(), equalTo(ADD));
		assertThat(resultAttrA2Add.getPopulators(), hasSize(2));
		assertThat(resultAttrA2Add.getPopulators(), containsInAnyOrder(basePop, popC));

		final TypeAttributeStructure resultAttr3Default = getAttribute(TYPECODE_A, QUALIFIER_3, DEFAULT.name());
		assertThat(resultAttr3Default, nullValue());
		final TypeAttributeStructure resultAttrA3Add = getAttribute(TYPECODE_A, QUALIFIER_3, ADD.name());
		assertThat(resultAttrA3Add.getTypecode(), equalTo(TYPECODE_A));
		assertThat(resultAttrA3Add.getQualifier(), equalTo(QUALIFIER_3));
		assertThat(resultAttrA3Add.getMode(), equalTo(ADD));
		assertThat(resultAttrA3Add.getPopulators(), hasSize(3));
		assertThat(resultAttrA3Add.getPopulators(), containsInAnyOrder(popA, popB, popC));

		assertThat(resultTypeB.getAttributesByModeMap().values(), hasSize(1)); // has 1 mode : DEFAULT
		final TypeAttributeStructure resultAttrB1Default = getAttribute(TYPECODE_B, QUALIFIER_1, DEFAULT.name());
		assertThat(resultAttrB1Default.getTypecode(), equalTo(TYPECODE_B));
		assertThat(resultAttrB1Default.getQualifier(), equalTo(QUALIFIER_1));
		assertThat(resultAttrB1Default.getMode(), equalTo(DEFAULT));
		assertThat(resultAttrB1Default.getPopulators().size(), equalTo(2));
		assertThat(resultAttrB1Default.getPopulators(), containsInAnyOrder(popA, popB));

		final TypeAttributeStructure resultAttrB2Default = getAttribute(TYPECODE_B, QUALIFIER_2, DEFAULT.name());
		assertThat(resultAttrB2Default.getTypecode(), equalTo(TYPECODE_B));
		assertThat(resultAttrB2Default.getQualifier(), equalTo(QUALIFIER_2));
		assertThat(resultAttrB2Default.getMode(), equalTo(DEFAULT));
		assertThat(resultAttrB2Default.getPopulators(), hasSize(2));
		assertThat(resultAttrB2Default.getPopulators(), containsInAnyOrder(popA, popB));

		assertThat(resultTypeC.getAttributesByModeMap().values(), hasSize(1)); // has 1 mode : DEFAULT
		final TypeAttributeStructure resultAttrC1Default = getAttribute(TYPECODE_C, QUALIFIER_1, DEFAULT.name());
		assertThat(resultAttrC1Default.getTypecode(), equalTo(TYPECODE_C));
		assertThat(resultAttrC1Default.getQualifier(), equalTo(QUALIFIER_1));
		assertThat(resultAttrC1Default.getMode(), equalTo(DEFAULT));
		assertThat(resultAttrC1Default.getPopulators(), hasSize(1));
		assertThat(resultAttrC1Default.getPopulators(), contains(popC));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentException_typecodeNotFoundInAttribute() throws Exception
	{
		final TypeAttributeStructure attributeInvalid = new DefaultTypeAttributeStructure();
		attributeInvalid.setTypecode(INVALID);
		attributeInvalid.setQualifier(QUALIFIER_1);
		attributeInvalid.setPopulators(Collections.emptySet());

		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA, defaultType));
		registry.setAllComponentTypeAttributeStructures(new HashSet<>());
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeInvalid));
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldGetAttributesByTypecodeAndDefaultMode() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA1));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA1Default));
		registry.afterPropertiesSet();

		final Set<TypeAttributeStructure> attributes = registry.getStructureTypeAttributesByMode(TYPECODE_A, DEFAULT.name());
		assertThat(attributes, hasSize(1));

		final TypeAttributeStructure attribute = attributes.iterator().next();
		assertThat(attribute.getTypecode(), equalTo(TYPECODE_A));
		assertThat(attribute.getQualifier(), equalTo(QUALIFIER_1));
		assertThat(attribute.getMode(), equalTo(DEFAULT));
		assertThat(attribute.getPopulators(), hasSize(3));
		assertThat(attribute.getPopulators(), containsInAnyOrder(basePop, popA, popB));
	}

	@Test
	public void shouldGetEmptyAttributesByTypecodeAndMode_NoModeMatch() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA2));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA2Add, attributeA2Default));
		registry.afterPropertiesSet();

		final Set<TypeAttributeStructure> attributes = registry.getStructureTypeAttributesByMode(TYPECODE_A, EDIT.name());
		assertThat(attributes, empty());
	}

	@Test(expected = ValidationException.class)
	public void testAttributeValidationError() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA2));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA2Add, attributeA2Default));
		doThrow(new ValidationException(null)).when(facadeValidationService).validate(any(), any());
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldFindAllModesExceptBase() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA2));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA2Add, attributeA2Default, attributeA2Base));
		registry.afterPropertiesSet();

		final Set<StructureTypeMode> modes = registry.getStructureModes(TYPECODE_A);
		assertThat(modes, hasSize(2));
		assertThat(modes, containsInAnyOrder(ADD, DEFAULT));
	}

	@Test
	public void shouldCreateDefaultStructureForBaseOnlyStructure() throws Exception
	{
		registry.setAllComponentTypeStructures(Sets.newHashSet(typeA));
		registry.setAllComponentTypeAttributeStructures(Sets.newHashSet(attributeA2));
		registry.setAllTypeAttributeStructures(Sets.newHashSet(attributeA2Base));
		registry.afterPropertiesSet();

		registry.createDefaultModeTypeStructure();

		final Set<StructureTypeMode> modes = registry.getStructureModes(TYPECODE_A);
		assertThat(modes, hasSize(1));
		assertThat(modes, contains(DEFAULT));
	}

	protected TypeStructure getType(final String typecode)
	{
		return registry.getTypeStructure(typecode);
	}

	protected TypeAttributeStructure getAttribute(final String typecode, final String qualifier, final String mode)
	{
		return getType(typecode).getAttributesByModeMap().get(StructureTypeMode.valueOf(mode)).stream() //
				.filter(attribute -> attribute.getQualifier().equals(qualifier)) //
				.findAny().orElse(null);
	}
}
