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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;
import de.hybris.platform.cmsfacades.data.TimeRestrictionData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.StructureTypeCategory;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTypeStructureConverterTest
{
	private static final String QUALIFIER1 = "qualifier1";
	private static final String QUALIFIER2 = "qualifier2";
	private static final String QUALIFIER3 = "qualifier3";
	private static final String CODE = "code";

	@InjectMocks
	private DefaultTypeStructureConverter converter;
	@Mock
	private TypeStructureRegistry typeStructureRegistry;

	@Mock
	private ComponentTypeStructureRegistry componentTypeStructureRegistry;

	@Mock
	private StringDecapitalizer stringDecapitalizer;

	@Mock
	private ComposedTypeModel source;
	@Mock
	private AttributeDescriptorModel attribute1, attribute2, attribute3;
	@Mock
	private TypeStructure structureType;
	@Mock
	private TypeAttributeStructure structureAttribute1, structureAttribute2;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> popType1, popType2, defaultPopType;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> popAttribute1, popAttribute2;
	@Mock
	private ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory;

	@Before
	public void setUp()
	{
		when(source.getCode()).thenReturn(CODE);
		when(source.getDeclaredattributedescriptors()).thenReturn(Sets.newHashSet(attribute1));
		when(source.getInheritedattributedescriptors()).thenReturn(Sets.newHashSet(attribute2, attribute3));
		when(attribute1.getQualifier()).thenReturn(QUALIFIER1);
		when(attribute2.getQualifier()).thenReturn(QUALIFIER2);
		when(attribute3.getQualifier()).thenReturn(QUALIFIER3);

		when(structureType.getPopulators()).thenReturn(Sets.newHashSet(popType1, popType2));
		final Map<StructureTypeMode, Set<TypeAttributeStructure>> attributesByMode = new HashMap<>();
		attributesByMode.put(StructureTypeMode.DEFAULT, Sets.newHashSet(structureAttribute1, structureAttribute2));
		attributesByMode.put(StructureTypeMode.EDIT, Sets.newHashSet(structureAttribute2));
		when(structureType.getAttributesByModeMap()).thenReturn(attributesByMode);
		when(structureType.getCategory()).thenReturn(StructureTypeCategory.RESTRICTION);
		when(structureType.getTypeDataClass()).thenReturn(TimeRestrictionData.class);
		when(structureAttribute1.getQualifier()).thenReturn(QUALIFIER1);
		when(structureAttribute1.getPopulators()).thenReturn(Sets.newHashSet(popAttribute1, popAttribute2));
		when(structureAttribute2.getQualifier()).thenReturn(QUALIFIER2);
		when(structureAttribute2.getPopulators()).thenReturn(Sets.newHashSet(popAttribute1, popAttribute2));
		when(componentTypeAttributeDataFactory.getObject()).thenReturn(new ComponentTypeAttributeData());

		when(stringDecapitalizer.decapitalize(any())).thenReturn(Optional.of("timeRestrictionData"));
		when(typeStructureRegistry.getTypeStructure(CODE)).thenReturn(structureType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConvertMissingMode()
	{
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target, null);
	}

	@Test
	public void shouldPopulateComponentTypeProperties()
	{
		final ComponentTypeData target = new ComponentTypeData();
		converter.convert(source, target);

		verify(popType1).populate(source, target);
		verify(popType2).populate(source, target);
	}

	@Test
	public void shouldConvertComponentTypeAttributes()
	{
		final ComponentTypeData target = new ComponentTypeData();
		converter.convert(source, target);

		verify(popAttribute1).populate(eq(attribute1), any(ComponentTypeAttributeData.class));
		verify(popAttribute2).populate(eq(attribute1), any(ComponentTypeAttributeData.class));
		verify(popAttribute1).populate(eq(attribute2), any(ComponentTypeAttributeData.class));
		verify(popAttribute2).populate(eq(attribute2), any(ComponentTypeAttributeData.class));
	}

	@Test
	public void shouldNotConvertComponentTypeAttributesWithNoStructureAttributes()
	{
		when(structureType.getAttributesByModeMap()).thenReturn(new HashMap<>());
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target);

		verifyZeroInteractions(popAttribute1, popAttribute2);
	}

	@Test
	public void shouldConvertForDefinedDefaultMode()
	{
		when(structureType.getPopulators()).thenReturn(Sets.newHashSet(defaultPopType));
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target, StructureTypeMode.DEFAULT);

		assertThat(target.getAttributes(), hasSize(2));
		assertThat(target.getAttributes().get(0).getMode(), equalTo(StructureTypeMode.DEFAULT.name()));
	}

	@Test
	public void shouldConvertForDefinedEditMode()
	{
		when(structureType.getPopulators()).thenReturn(Sets.newHashSet(defaultPopType));
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target, StructureTypeMode.EDIT);

		assertThat(target.getAttributes(), hasSize(1));
		assertThat(target.getAttributes().get(0).getMode(), equalTo(StructureTypeMode.EDIT.name()));
	}

	@Test
	public void shouldUseFallbackStrategyToConvertForUndefinedAddMode()
	{
		when(structureType.getPopulators()).thenReturn(Sets.newHashSet(defaultPopType));
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target, StructureTypeMode.ADD);

		assertThat(target.getAttributes(), hasSize(2));
		assertThat(target.getAttributes().get(0).getMode(), equalTo(StructureTypeMode.ADD.name()));
	}

	@Test
	public void shouldPopulateTypAttributeWithCorrectName()
	{
		final ComponentTypeData target = new ComponentTypeData();

		converter.convert(source, target);

		verify(stringDecapitalizer).decapitalize(TimeRestrictionData.class);
	}

}
