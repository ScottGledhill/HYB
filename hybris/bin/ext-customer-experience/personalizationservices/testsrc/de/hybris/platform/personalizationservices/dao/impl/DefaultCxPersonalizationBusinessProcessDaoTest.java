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
/**
 *
 */
package de.hybris.platform.personalizationservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.dao.impl.DefaultCxPersonalizationBusinessProcessDao;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxPersonalizationBusinessProcessDaoTest extends ServicelayerTest
{
	@Resource
	private DefaultCxPersonalizationBusinessProcessDao defaultCxPersonalizationBusinessProcessDao;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void setupData() throws Exception
	{
		createCoreData();
		importCsv("/personalizationservices/test/testdata_cxsite.impex", "utf-8");
		importCsv("/personalizationservices/test/personalizationprocessdao_test.impex", "utf-8");

	}

	@Test
	public void shouldRetrieveRunningBusinessProcess()
	{
		final List<CxPersonalizationProcessModel> result = defaultCxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(
				"testdefinitionname", userService.getUserForUID("defaultcxcustomer"),
				Arrays.asList(catalogVersionService.getCatalogVersion("testCatalog1", "Online"),
						catalogVersionService.getCatalogVersion("testCatalog2", "Online")));

		Assert.assertEquals(1, CollectionUtils.size(result));
	}

}
