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


import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.SESSION_YAAS_APPLICATIONID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultYaasSessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultYaasSessionServiceUnitTest extends YaasConfigurationTestUtils
{

	@Mock
	static SessionService mockSessionService;

	private DefaultYaasSessionService defaultYaasSessionService;

	@Before
	public void setup()
	{
		defaultYaasSessionService = new DefaultYaasSessionService();
		defaultYaasSessionService.setSessionService(mockSessionService);
	}


	@Test
	public void testSetCurrentYaasAppId_validateParameter()
	{
		errorMustBeReported("Parameter 'yaasAppId' must not be null!");
		defaultYaasSessionService.setCurrentYaasAppId(null);
	}

	@Test
	public void testSetCurrentYaasAppId()
	{
		defaultYaasSessionService.setCurrentYaasAppId("applicationId");
		when(mockSessionService.getAttribute(SESSION_YAAS_APPLICATIONID)).thenReturn("applicationId_set");

		assertEquals("applicationId_set", defaultYaasSessionService.getCurrentYaasAppId());
	}

	@Test
	public void testGetCurrentYaasAppId()
	{
		when(mockSessionService.getAttribute(SESSION_YAAS_APPLICATIONID)).thenReturn("applicationId");
		assertEquals("applicationId", defaultYaasSessionService.getCurrentYaasAppId());
	}

}
