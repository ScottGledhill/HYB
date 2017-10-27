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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSAdminContentSlotServiceTest
{

	@Captor
	private ArgumentCaptor<ContentSlotModel> savedContentSlotCaptor;

	@Captor
	private ArgumentCaptor<ContentSlotForPageModel> savedContentSlotForPageCaptor;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultCMSAdminContentSlotService cmsAdminContentSlotService;

	@Mock
	private AbstractPageModel page;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private Date from;

	@Mock
	private Date to;

	@Before
	public void setUp()
	{
		when(keyGenerator.generate()).thenReturn("generatedKey");
		when(page.getUid()).thenReturn("mypage$uid");
		when(page.getCatalogVersion()).thenReturn(catalogVersion);
	}

	@Test
	public void willCreateAndAssignSlotsToThePositionsNotBusyWithSharedSlots()
	{
		ContentSlotModel returnValue = cmsAdminContentSlotService.createContentSlot(page, null, "name1", "position1", true, from,
				to);

		verify(modelService, times(1)).saveAll(savedContentSlotCaptor.capture(), savedContentSlotForPageCaptor.capture());

		ContentSlotModel contentSlotModel = savedContentSlotCaptor.getValue();
		ContentSlotForPageModel contentSlotForPageModel = savedContentSlotForPageCaptor.getValue();

		assertThat(returnValue, is(contentSlotModel));

		assertThat(contentSlotModel, allOf(
				hasProperty("uid", is("position1Slot-mypage-uid")),
				hasProperty("name", is("name1")),
				hasProperty("active", is(true)),
				hasProperty("activeFrom", is(from)),
				hasProperty("activeUntil", is(to)),
				hasProperty("catalogVersion", is(catalogVersion))));

		assertThat(contentSlotForPageModel, allOf(
				hasProperty("uid", is("contentSlotForPage-generatedKey")),
				hasProperty("catalogVersion", is(catalogVersion)),
				hasProperty("position", is("position1")),
				hasProperty("page", is(page)),
				hasProperty("contentSlot", is(contentSlotModel))
				));

	}
}
