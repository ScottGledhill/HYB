/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.print.comet.utils;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.print.jalo.Page;
import de.hybris.platform.print.jalo.PrintPublication;
import de.hybris.platform.print.jalo.Publication;
import de.hybris.platform.print.model.PageModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


public class CometFileHandlerTest
{
	public static final String ID = "testId1234";
	@Mock
	private PrintPublication printPublication;

	@Mock
	private Item item;

	@Mock
	private Page page;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldGetNullWhileGettingNullPublication()
	{
		// given
		final Publication publication = null;
		// when
		final PageModel pageModel = CometFileHandler.getPageById(ID, publication, modelService);
		// then
		assertThat(pageModel).isNull();
	}

	@Test
	public void shouldGetNullWhileGettingConcretePublication()
	{
		// given
		final PageModel pageModel = CometFileHandler.getPageById(ID, printPublication, modelService);
		// when
		when(printPublication.getPublicationElementById(eq(ID))).thenReturn(null);
		// then
		assertThat(pageModel).isNull();
	}

	@Test
	public void shouldGetNullWhileGettingPublicationWithSpecifiedIdAndPublicationHasAnItem()
	{
		// given
		final PageModel pageModel = CometFileHandler.getPageById(ID, printPublication, modelService);
		// when
		when(printPublication.getPublicationElementById(eq(ID))).thenReturn(item);
		// then
		assertThat(pageModel).isNull();
	}

	@Test
	public void shouldGetPageModel()
	{
		// given
		final PageModel givenPageModel = new PageModel();
		// when
		when(printPublication.getPublicationElementById(eq(ID))).thenReturn(page);
		when(modelService.toModelLayer(page)).thenReturn(givenPageModel);
		// then
		assertThat(CometFileHandler.getPageById(ID, printPublication, modelService)).isEqualTo(givenPageModel);
	}
}
