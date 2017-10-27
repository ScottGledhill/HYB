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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCMSAdminPageServiceTest
{
	private static final String INVALID = "invalid";
	private static final String TEST_PAGE_TYPE = "testPageType";

	@InjectMocks
	private DefaultCMSAdminPageService pageService;

	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private CMSPageTypeModel pageType1;
	@Mock
	private CMSPageTypeModel pageType2;

	@Before
	public void setUp()
	{
		when(typeService.getComposedTypeForClass(AbstractPageModel.class)).thenReturn(composedTypeModel);
		when(composedTypeModel.getAllSubTypes()).thenReturn(Arrays.asList(pageType1, pageType2));
		when(pageType1.getCode()).thenReturn(TEST_PAGE_TYPE);
		when(pageType2.getCode()).thenReturn(TEST_PAGE_TYPE);
	}

	@Test
	public void shouldGetAllPageTypes()
	{
		final Collection<CMSPageTypeModel> pageTypes = pageService.getAllPageTypes();
		assertThat(pageTypes, containsInAnyOrder(pageType1, pageType2));
	}

	@Test
	public void shouldGetPageTypeByCode()
	{
		final Optional<CMSPageTypeModel> pageType = pageService.getPageTypeByCode(TEST_PAGE_TYPE);
		assertThat(pageType.isPresent(), is(true));
		assertThat(pageType.get(), is(pageType1));
	}

	@Test
	public void shouldNotGetPageTypeByInvalidCode()
	{
		final Optional<CMSPageTypeModel> pageType = pageService.getPageTypeByCode(INVALID);
		assertThat(pageType.isPresent(), is(false));
	}
}
