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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAbstractPageModelVisitorTest
{

	@Mock
	private ContentPageModel page;
	@Mock
	private ContentSlotForPageModel csfp1;
	@Mock
	private ContentSlotForPageModel csfp2;
	@Mock
	private ContentSlotModel slot1;
	@Mock
	private ContentSlotModel slot2;
	@Mock
	private ContentSlotForTemplateModel csft;
	@Mock
	private ContentSlotModel sharedSlot;
	@InjectMocks
	private DefaultAbstractPageModelVisitor visitor;
	
	
	@Before
	public void setUp()
	{
		when(page.getContentSlots()).thenReturn(asList(csfp1, csfp2));
		when(csfp1.getContentSlot()).thenReturn(slot1);
		when(csfp2.getContentSlot()).thenReturn(slot2);
	
		
		//when(page.get).thenReturn(asList(csfp1, csfp2));
		when(csft.getContentSlot()).thenReturn(sharedSlot);

	}

	@Test
	public void willCollectNonSharedSlots()
	{
		
		List<ItemModel> visit = visitor.visit(page, null, null);
		
		assertThat(visit, containsInAnyOrder(slot1, slot2));
		
		
	}

}
