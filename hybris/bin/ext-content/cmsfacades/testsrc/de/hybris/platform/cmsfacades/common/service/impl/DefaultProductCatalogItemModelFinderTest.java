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
package de.hybris.platform.cmsfacades.common.service.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductCatalogItemModelFinderTest
{
	@InjectMocks
	private DefaultProductCatalogItemModelFinder finder;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private ObjectFactory<ItemData> cmsItemDataDataFactory;

	@Mock
	private CategoryModel categoryModel;

	private final SessionService sessionService = new MockSessionService();
	private ItemData itemData;

	@Before
	public void setUp()
	{
		itemData = new ItemData();
		finder.setSessionService(sessionService);

		doNothing().when(searchRestrictionService).disableSearchRestrictions();
		doNothing().when(searchRestrictionService).enableSearchRestrictions();

		when(cmsItemDataDataFactory.getObject()).thenReturn(itemData);
		when(uniqueItemIdentifierService.getItemModel(itemData)).thenReturn(Optional.of(categoryModel));
	}

	@Test
	public void shouldFindCategoriesForValidKeys()
	{
		final String validKey1 = "pantsUK-staged-pants007";
		final String validKey2 = "pantsUK-staged-pants008";

		final List<CategoryModel> models = finder.getCategoriesForCompositeKeys(Arrays.asList(validKey1, validKey2));

		assertThat(models, hasSize(2));
		verify(cmsItemDataDataFactory, times(2)).getObject();
		verify(uniqueItemIdentifierService, times(2)).getItemModel(itemData);
	}

	@Test
	public void shouldFindCategoryForValidKey()
	{
		final String validKey = "pantsUK-staged-pants007";

		final CategoryModel model = finder.getCategoryForCompositeKey(validKey);

		assertThat(model, notNullValue());
		verify(cmsItemDataDataFactory).getObject();
		verify(uniqueItemIdentifierService).getItemModel(itemData);
	}

	@Test(expected = ConversionException.class)
	public void shouldFindCategoryForInvalidKey()
	{
		final String invalidKey = "invalid";
		doThrow(new ConversionException("invalid composite key")).when(uniqueItemIdentifierService).getItemModel(itemData);


		final CategoryModel model = finder.getCategoryForCompositeKey(invalidKey);
	}

}
