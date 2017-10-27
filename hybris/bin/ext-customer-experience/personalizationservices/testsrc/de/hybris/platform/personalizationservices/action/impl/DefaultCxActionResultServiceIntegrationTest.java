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
package de.hybris.platform.personalizationservices.action.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxActionResultServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultCxActionResultService defaultCxActionResultService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private SessionService sessionService;

	@Resource
	private TimeService timeService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationservices/test/testdata_cxsite.impex", "utf-8");
	}

	@Test
	public void shouldSerializeDeserializeActionResults()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final TestCxActionResultModel result1 = new TestCxActionResultModel();
		result1.setActionCode("test1");
		result1.setCustomizationCode("test1CustCode");
		result1.setVariationCode("test1VarCode");
		final TestCxActionResultModel result2 = new TestCxActionResultModel();
		result2.setActionCode("test2");
		result2.setCustomizationCode("test2CustCode");
		result2.setVariationCode("test2VarCode");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1, result2);

		defaultCxActionResultService.storeActionResultsOnUser(user, cv, inputActionResults);
		Assert.assertNotNull(user.getCxResults());

		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(2)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test1VarCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getCustomizationCode(), CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getVariationCode(), CoreMatchers.equalTo("test2VarCode"));
	}

	@Test
	public void shouldLoadMoreRecentResultsInSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final TestCxActionResultModel result1 = new TestCxActionResultModel();
		result1.setActionCode("test1");
		result1.setCustomizationCode("test1CustCode");
		result1.setVariationCode("test1VarCode");
		final TestCxActionResultModel result2 = new TestCxActionResultModel();
		result2.setActionCode("test2");
		result2.setCustomizationCode("test2CustCode");
		result2.setVariationCode("test2VarCode");
		final List<CxAbstractActionResult> results1 = Collections.singletonList(result1);
		final List<CxAbstractActionResult> results2 = Collections.singletonList(result2);

		//results1 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResultsOnUser(user, cv, results1);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test1VarCode"));

		//results2 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResultsOnUser(user, cv, results2);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test2VarCode"));
	}

	@Test
	public void shouldNotLoadOlderResultsInSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final TestCxActionResultModel result1 = new TestCxActionResultModel();
		result1.setActionCode("test1");
		result1.setCustomizationCode("test1CustCode");
		result1.setVariationCode("test1VarCode");
		final TestCxActionResultModel result2 = new TestCxActionResultModel();
		result2.setActionCode("test2");
		result2.setCustomizationCode("test2CustCode");
		result2.setVariationCode("test2VarCode");
		final List<CxAbstractActionResult> results1 = Collections.singletonList(result1);
		final List<CxAbstractActionResult> results2 = Collections.singletonList(result2);

		//results1 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResultsOnUser(user, cv, results1);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test1VarCode"));

		//results2 are stored in db...
		defaultCxActionResultService.storeActionResultsOnUser(user, cv, results2);
		final CxResultsModel cxResult = user.getCxResults().iterator().next();
		//...but we make them older...
		cxResult.setCalculationTime(DateUtils.addDays(cxResult.getCalculationTime(), -1));

		//...so they should not be loaded in the session
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test1VarCode"));
	}


	@Test
	public void shouldSetAndClearResultsFromSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final TestCxActionResultModel result1 = new TestCxActionResultModel();
		result1.setActionCode("test1");
		result1.setCustomizationCode("test1CustCode");
		result1.setVariationCode("test1VarCode");
		final TestCxActionResultModel result2 = new TestCxActionResultModel();
		result2.setActionCode("test2");
		result2.setCustomizationCode("test2CustCode");
		result2.setVariationCode("test2VarCode");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1, result2);

		defaultCxActionResultService.setActionResultsInSession(user, cv, inputActionResults);
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);

		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(2)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(), CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(), CoreMatchers.equalTo("test1VarCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getCustomizationCode(), CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getVariationCode(), CoreMatchers.equalTo("test2VarCode"));

		defaultCxActionResultService.clearActionResultsInSession(user, cv);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultCxActionResultService.getActionResults(user, cv)));
	}



}

class TestCxActionResultModel extends CxAbstractActionResult
{
	//empty
}
