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
package de.hybris.platform.cmsfacades.navigations.validator.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.NavigationComponentModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ValidEntryItemModelPredicateTest
{
	private final CMSParagraphComponentModel paragraphComponentModel = new CMSParagraphComponentModel();

	private final NavigationComponentModel navigationComponentModel = new NavigationComponentModel();
	
	@Mock
	private TypeService typeService;
	
	@InjectMocks
	private ValidEntryItemModelPredicate predicate;

	@Before
	public void setup() 
	{
		final ComposedTypeModel paragraphComposedType = new ComposedTypeModel();
		final ComposedTypeModel navigationComposedType = new ComposedTypeModel();
		
		when(typeService.getComposedTypeForCode(CMSParagraphComponentModel._TYPECODE)).thenReturn(paragraphComposedType);
		when(typeService.getComposedTypeForCode(NavigationComponentModel._TYPECODE)).thenReturn(navigationComposedType);
		
		final AttributeDescriptorModel paragraphAttribute = new AttributeDescriptorModel();
		final ComposedTypeModel paragraphEnclosingType = new ComposedTypeModel();
		paragraphEnclosingType.setCode(CMSItemModel._TYPECODE);
		paragraphAttribute.setAttributeType(paragraphEnclosingType);
		final Set<AttributeDescriptorModel> paragraphAttributes = Sets.newHashSet(paragraphAttribute);
		when(typeService.getAttributeDescriptorsForType(paragraphComposedType)).thenReturn(paragraphAttributes);


		final AttributeDescriptorModel navigationAttribute = new AttributeDescriptorModel();
		final ComposedTypeModel navigationEnclosingType = new ComposedTypeModel();
		navigationEnclosingType.setCode(CMSNavigationNodeModel._TYPECODE);
		navigationAttribute.setAttributeType(navigationEnclosingType);
		final Set<AttributeDescriptorModel> navigationAttributes = Sets.newHashSet(navigationAttribute);
		when(typeService.getAttributeDescriptorsForType(navigationComposedType)).thenReturn(navigationAttributes);
	}
	
	@Test
	public void testParagraphComponentModel_shouldBeValidComponent()
	{
		final boolean test = predicate.test(paragraphComponentModel);
		assertThat(test, is(true));
	}

	@Test
	public void testNavigationComponentModel_shouldBeInvalidComponent()
	{
		final boolean test = predicate.test(navigationComponentModel);
		assertThat(test, is(false));
	}
}
