/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyProvider;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class ConfigurationProviderFactoryImplTest
{


	private ConfigurationProviderFactoryImpl classUnderTest;
	private SessionAccessServiceImpl sessionAccessService;

	@Mock
	ApplicationContext mockApplicationContext;

	final ConfigurationProvider dummyProvider = new DummyProvider();
	final ConfigurationProvider dummyLocalProvider = new DummyProvider();


	@Before
	public void setUp()
	{

		classUnderTest = new ConfigurationProviderFactoryImpl();
		classUnderTest = Mockito.spy(classUnderTest);
		MockitoAnnotations.initMocks(this);


		sessionAccessService = new DummySessionAccessService();

		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setApplicationContext(mockApplicationContext);

		Mockito.when(mockApplicationContext.getBean("sapProductConfigConfigurationProvider")).thenReturn(dummyProvider);
		Mockito.when(mockApplicationContext.getBean("sapProductConfigLocalConfigurationProvider")).thenReturn(dummyLocalProvider);

	}

	@Test
	public void testGetProvider_newSession()
	{
		sessionAccessService.setConfigurationProvider(null);
		final ConfigurationProvider provider = classUnderTest.getProvider();
		assertNotNull(provider);
		assertSame(dummyProvider, provider);
	}

	@Test
	public void testGetProvider_existingSession()
	{
		sessionAccessService.setConfigurationProvider(dummyProvider);
		final ConfigurationProvider provider = classUnderTest.getProvider();
		assertNotNull(provider);
		assertSame(dummyProvider, provider);
	}

}
