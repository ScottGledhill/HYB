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
package de.hybris.platform.personalizationservices.process;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;

import java.util.Arrays;

import javax.annotation.Resource;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CalculatePersonalizationForUserActionTest extends ServicelayerTest
{
	@Resource
	private CalculatePersonalizationForUserAction calculatePersonalizationForUserAction;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UserService userService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationservices/test/testdata_cxsite.impex", "utf-8");
	}

	@Test
	public void shouldStoreActionResultsOnUser() throws RetryLaterException, Exception
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");

		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setCatalogVersions(Arrays.asList(catalogVersionService.getCatalogVersion("testCatalog", "Online")));
		process.setUser(user);

		final Transition result = calculatePersonalizationForUserAction.executeAction(process);

		Assert.assertThat(result, CoreMatchers.equalTo(Transition.OK));
		Assert.assertTrue(user.getCxResults().size() == 1);
		final CxResultsModel cxResult = user.getCxResults().iterator().next();
		Assert.assertThat(cxResult.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResult.getCatalogVersion().getCatalog().getId(), CoreMatchers.equalTo("testCatalog"));
		Assert.assertNotNull(cxResult.getResults());

	}

}
