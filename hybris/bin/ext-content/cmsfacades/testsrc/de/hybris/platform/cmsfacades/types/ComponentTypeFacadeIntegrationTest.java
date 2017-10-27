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
package de.hybris.platform.cmsfacades.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.StructureTypeCategory;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;



@IntegrationTest
public class ComponentTypeFacadeIntegrationTest extends ServicelayerTest
{
	@Resource
	private ComponentTypeFacade componentTypeFacade;

	@Test
	public void shouldGetParagraphComponentTypeFromAllTypes()
	{
		final List<ComponentTypeData> componentTypes = componentTypeFacade.getAllComponentTypes();
		Assert.assertTrue(componentTypes.size() > 1);

		// Get the CmsParagraphComponent type.
		final ComponentTypeData cmsParagraphComponentType = componentTypes.stream()
				.filter(componentType -> CMSParagraphComponentModel._TYPECODE.equals(componentType.getCode())).findFirst().get();

		assertParagraphComponent(cmsParagraphComponentType);
	}


	@Test
	public void shouldGetParagraphComponentTypeFromAllTypesByCategory()
	{
		final String componentCategory = StructureTypeCategory.COMPONENT.name();
		final List<ComponentTypeData> componentTypes = componentTypeFacade.getAllComponentTypes(componentCategory);
		Assert.assertTrue(componentTypes.size() > 1);


		final List<ComponentTypeData> collectedTypes = componentTypes //
				.stream() //
				.filter(componentType -> !componentCategory.equals(componentType.getCategory())) //
				.collect(Collectors.toList());

		assertThat(collectedTypes.size(), is(0));
	}


	@Test
	public void shouldGetParagraphComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData cmsParagraphComponentType = componentTypeFacade.getComponentTypeByCode("CMSParagraphComponent");

		assertParagraphComponent(cmsParagraphComponentType);
	}

	@Test
	public void shouldGetCategoryPageComponentType_FromAllTypes()
	{
		final List<ComponentTypeData> componentTypes = componentTypeFacade.getAllComponentTypes();
		Assert.assertTrue(componentTypes.size() > 1);

		final ComponentTypeData categoryPageType = componentTypes.stream()
				.filter(componentType -> CategoryPageModel._TYPECODE.equals(componentType.getCode())).findFirst().get();

		assertCategoryPage(categoryPageType);
	}

	@Test
	public void shouldGetCategoryPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData categoryPageType = componentTypeFacade.getComponentTypeByCode("CategoryPage");

		assertCategoryPage(categoryPageType);
	}

	@Test
	public void shouldGetContentPageComponentType_FromSingleType() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData categoryPageType = componentTypeFacade.getComponentTypeByCode("ContentPage");

		assertThat(categoryPageType.getCode(), equalTo(ContentPageModel._TYPECODE));
		assertThat(categoryPageType.getI18nKey(), equalTo("type.contentpage.name"));
		assertThat(categoryPageType.getCategory(), equalTo(StructureTypeCategory.PAGE.name()));
		assertThat(categoryPageType.getAttributes(), iterableWithSize(6));
	}

	protected void assertCategoryPage(final ComponentTypeData categoryPageType)
	{
		assertThat(categoryPageType.getCode(), equalTo(CategoryPageModel._TYPECODE));
		assertThat(categoryPageType.getI18nKey(), equalTo("type.categorypage.name"));
		assertThat(categoryPageType.getCategory(), equalTo(StructureTypeCategory.PAGE.name()));
		assertThat(categoryPageType.getAttributes(), iterableWithSize(5));
	}

	protected void assertParagraphComponent(final ComponentTypeData cmsParagraphComponentType)
	{
		Assert.assertEquals(CMSParagraphComponentModel._TYPECODE, cmsParagraphComponentType.getCode());
		Assert.assertNull(cmsParagraphComponentType.getName());
		Assert.assertEquals("type.cmsparagraphcomponent.name", cmsParagraphComponentType.getI18nKey());

		final List<ComponentTypeAttributeData> paragraphAttributes = cmsParagraphComponentType.getAttributes();
		Assert.assertEquals(1, paragraphAttributes.size());

		final ComponentTypeAttributeData contentAttribute = paragraphAttributes.get(0);
		Assert.assertEquals("content", contentAttribute.getQualifier());
		Assert.assertEquals(Boolean.TRUE, contentAttribute.getLocalized());
		Assert.assertEquals("RichText", contentAttribute.getCmsStructureType());
		Assert.assertEquals("type.cmsparagraphcomponent.content.name", contentAttribute.getI18nKey());
	}

}
