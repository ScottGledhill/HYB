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
package de.hybris.platform.sap.productconfig.services.event.impl;

import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigUserChangedEventListenerTest
{
	private DummySessionAccessService dummySessionAccessService;
	private ProductConfigUserChangedEventListener classUnderTest;
	private AfterSessionUserChangeEvent evt;


	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigUserChangedEventListenerForTest();
		dummySessionAccessService = new DummySessionAccessService();
		evt = new AfterSessionUserChangeEvent();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetterNotImplemented()
	{
		classUnderTest = new ProductConfigUserChangedEventListener();
		classUnderTest.getSessionAccessService();
	}

	@Test
	public void testOnEventNoCPQSession()
	{
		classUnderTest.onEvent(evt);
		assertNull(dummySessionAccessService.getAttributeContainer());
	}

	@Test
	public void testOnEventWithCPQSession()
	{
		dummySessionAccessService.setConfigurationModelEngineState("123", new ConfigModelImpl());
		classUnderTest.onEvent(evt);
		assertNull(dummySessionAccessService.getConfigurationModelEngineState("123"));
	}

	public class ProductConfigUserChangedEventListenerForTest extends ProductConfigUserChangedEventListener
	{

		@Override
		protected SessionAccessService getSessionAccessService()
		{
			return dummySessionAccessService;
		}
	}
}
