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
package de.hybris.platform.cmsfacades.catalogversiondetails.populator.model;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.catalogversiondetails.populator.ContentCatalogVersionModelPopulator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentCatalogVersionModelPopulatorTest
{

	@Mock
	private CMSSiteModel siteModel;
	@Mock
	private ContentCatalogModel productCatalogModel;
	@InjectMocks
	private ContentCatalogVersionModelPopulator populator;

	@Before
	public void setup()
	{
		when(siteModel.getContentCatalogs()).thenReturn(asList(productCatalogModel));
	
	}

	@Test
	public void getCatalogsRetrievesFrombaseSiteService() throws Exception
	{
		assertThat(populator.getCatalogs(siteModel), equalTo(asList(productCatalogModel)));
	}



}
