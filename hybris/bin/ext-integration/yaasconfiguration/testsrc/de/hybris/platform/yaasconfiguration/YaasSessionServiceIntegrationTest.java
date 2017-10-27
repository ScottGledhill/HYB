/*
* [y] hybris Platform
*
* Copyright (c) 2017 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/
package de.hybris.platform.yaasconfiguration;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class YaasSessionServiceIntegrationTest extends ServicelayerBaseTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private YaasSessionService yaasSessionService;


	@Test
	public void testEmptyParameter() throws Exception
	{

		errorMustBeReported("Parameter 'yaasAppId' must not be null!");
		yaasSessionService.setCurrentYaasAppId(null);
	}

	@Test
	public void testGetYaasAppIdParameterBeforeSetting() throws Exception
	{
		assertNull(yaasSessionService.getCurrentYaasAppId());

	}

	@Test
	public void testSetYaasAppIdParameter() throws Exception
	{

		yaasSessionService.setCurrentYaasAppId("devapplication");

		assertEquals(yaasSessionService.getCurrentYaasAppId(), "devapplication");
	}


	private void errorMustBeReported(final String msg)
	{
		expectedException.expectMessage(containsString(msg));
	}



}
