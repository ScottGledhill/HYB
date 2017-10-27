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

import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.ADD;
import static de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode.DEFAULT;
import static de.hybris.platform.cmswebservices.data.StructureTypeCategory.RESTRICTION;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.comparator.ComponentTypeAttributeDataComparator;
import de.hybris.platform.cmssmarteditwebservices.structures.converter.TypeStructureConverter;
import de.hybris.platform.cmssmarteditwebservices.structures.service.ComponentTypeAttributeDataComparatorRegistry;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureRegistry;
import de.hybris.platform.cmssmarteditwebservices.structures.service.impl.DefaultTypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.impl.DefaultTypeStructure;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.StructureTypeCategory;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStructureFacadeTest
{
	private static final String ABSTRACT_TYPECODE = "abstractType";
	private static final String ITEM_TYPECODE = "itemType";
	private static final String INVALID = "invalid";
	private static final String QUALIFIER = "qualifier";

	@InjectMocks
	@Spy
	private DefaultStructureFacade facade;
	@Mock
	private TypeService typeService;
	@Mock
	private TypeStructureConverter<ComposedTypeModel, ComponentTypeData> typeStructureConverter;
	@Mock
	private ComponentTypeAttributeDataComparatorRegistry comparatorRegistry;
	@Mock
	private TypeStructureRegistry typeStructureRegistry;

	@Mock
	private ComposedTypeModel componentTypeModel;
	@Mock
	private ComponentTypeData componentTypeData;
	@Mock
	private ComponentTypeAttributeDataComparator comparator;

	private final Set<String> cmsSupportedAbstractTypecodes = Sets.newHashSet(ABSTRACT_TYPECODE);

	@Before
	public void setUp()
	{
		facade.setCmsSupportedAbstractTypecodes(cmsSupportedAbstractTypecodes);
	}

	@Test
	public void shouldFindAbstractTypeAttributes()
	{
		final ComponentTypeData componentType = new ComponentTypeData();
		componentType.setCategory(RESTRICTION.name());
		componentType.setType(ITEM_TYPECODE);
		componentType.setAttributes(new ArrayList<>());

		final TypeAttributeStructure attribute = new DefaultTypeAttributeStructure();
		attribute.setMode(DEFAULT);
		attribute.setQualifier(QUALIFIER);
		attribute.setTypecode(ABSTRACT_TYPECODE);

		final TypeStructure abstractTypeStructure = new DefaultTypeStructure();
		abstractTypeStructure.setCategory(RESTRICTION);
		abstractTypeStructure.setTypecode(ABSTRACT_TYPECODE);
		abstractTypeStructure.getAttributesByModeMap().put(DEFAULT, Sets.newHashSet(attribute));

		when(typeStructureRegistry.getStructureTypes()).thenReturn(Sets.newHashSet(abstractTypeStructure));
		doReturn(Optional.ofNullable(componentTypeData)).when(facade)
		.getOptionalComponentTypeByCodeAndModeUnordered(ABSTRACT_TYPECODE, ADD);

		facade.getAbstractTypesForComponentAndMode(componentType, ADD);

		verify(facade).getOptionalComponentTypeByCodeAndModeUnordered(ABSTRACT_TYPECODE, ADD);
	}

	@Test
	public void shouldNotFindAbstractTypeAttributes_noAttributesDefined()
	{
		final ComponentTypeData componentType = new ComponentTypeData();
		componentType.setCategory(StructureTypeCategory.RESTRICTION.name());
		componentType.setType(ITEM_TYPECODE);
		componentType.setAttributes(new ArrayList<>());

		final TypeStructure abstractTypeStructure = new DefaultTypeStructure();
		abstractTypeStructure.setCategory(StructureTypeCategory.RESTRICTION);
		abstractTypeStructure.setTypecode(ABSTRACT_TYPECODE);

		when(typeStructureRegistry.getStructureTypes()).thenReturn(Sets.newHashSet(abstractTypeStructure));

		final List<ComponentTypeData> abstractData = facade.getAbstractTypesForComponentAndMode(componentType,
				StructureTypeMode.ADD);

		assertThat(abstractData, empty());
		verify(facade, times(0)).getOptionalComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD);
	}

	@Test
	public void shouldNotAbstractTypeAttributesForAbstractTypecode()
	{
		final ComponentTypeData componentType = new ComponentTypeData();
		componentType.setCategory(StructureTypeCategory.RESTRICTION.name());
		componentType.setType(ABSTRACT_TYPECODE);
		componentType.setAttributes(new ArrayList<>());

		final List<ComponentTypeData> abstractData = facade.getAbstractTypesForComponentAndMode(componentType,
				StructureTypeMode.ADD);

		assertThat(abstractData, empty());
		verify(facade, times(0)).getOptionalComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD);
	}

	@Test
	public void shouldFindComposedTypeModel() throws ComponentTypeNotFoundException
	{
		when(typeService.getComposedTypeForCode(ITEM_TYPECODE)).thenReturn(componentTypeModel);

		final ComposedTypeModel componentType = facade.getComponentType(ITEM_TYPECODE);

		assertThat(componentType, not(nullValue()));
	}

	@Test(expected = ComponentTypeNotFoundException.class)
	public void shouldNotFindComposedTypeModelForUnsupportedTypecode() throws ComponentTypeNotFoundException
	{
		when(typeService.getComposedTypeForCode(INVALID)).thenReturn(null);

		facade.getComponentType(INVALID);
	}

	@Test
	public void shouldGetComponentByCodeAndMode() throws ComponentTypeNotFoundException
	{
		doReturn(componentTypeModel).when(facade).getComponentType(ITEM_TYPECODE);
		when(typeStructureConverter.convert(componentTypeModel, ADD)).thenReturn(componentTypeData);
		when(facade.getAbstractTypesForComponentAndMode(componentTypeData, ADD)).thenReturn(Collections.emptyList());
		doReturn(Optional.of(comparator)).when(comparatorRegistry).getComparatorForTypecode(ITEM_TYPECODE, ADD);
		doReturn(Optional.empty()).when(comparatorRegistry).getComparatorForTypecode(ITEM_TYPECODE, DEFAULT);
		when(componentTypeData.getAttributes()).thenReturn(Collections.emptyList());

		final ComponentTypeData componentTypeData = facade.getComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD.name());

		verify(facade, times(2)).getAbstractTypesForComponentAndMode(componentTypeData, ADD);
		verify(comparatorRegistry).getComparatorForTypecode(ITEM_TYPECODE, ADD);
		verify(comparatorRegistry, times(0)).getComparatorForTypecode(ITEM_TYPECODE, DEFAULT);
	}

	@Test
	public void shouldGetComponentsByCode() throws ComponentTypeNotFoundException
	{
		doReturn(componentTypeModel).when(facade).getComponentType(ITEM_TYPECODE);
		when(typeStructureRegistry.getStructureModes(ITEM_TYPECODE)).thenReturn(Sets.newHashSet(DEFAULT, ADD));
		when(typeStructureConverter.convert(any(ComposedTypeModel.class), any(StructureTypeMode.class)))
		.thenReturn(componentTypeData);
		doReturn(Optional.of(componentTypeData)).when(facade).getOptionalComponentTypeByCodeAndMode(anyString(), any());

		facade.getComponentTypesByCode(ITEM_TYPECODE);

		verify(typeStructureRegistry).getStructureModes(ITEM_TYPECODE);
		verify(facade, times(2)).getOptionalComponentTypeByCodeAndMode(eq(ITEM_TYPECODE), any());
	}

}
