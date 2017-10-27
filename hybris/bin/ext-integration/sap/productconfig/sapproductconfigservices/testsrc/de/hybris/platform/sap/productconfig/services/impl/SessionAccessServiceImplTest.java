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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
/**
 * Unit tests for {@link SessionAccessServiceImpl}
 */
public class SessionAccessServiceImplTest
{
	SessionAccessServiceImpl classUnderTest;

	@Before
	public void setup()
	{
		// dummy service extends real service, just bypassing the session servic
		classUnderTest = new DummySessionAccessService();
	}

	@Test
	public void testSessionService()
	{
		assertNotNull(classUnderTest.getSessionId());
	}

	@Test
	public void testCartEntryConfigId()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		final Map<String, String> configs = new HashMap<>();
		configs.put(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatus()
	{
		final String cartEntryKey = "X";
		final Object status = "S";
		final Map<String, Object> statuses = new HashMap<>();
		statuses.put(cartEntryKey, status);
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
		statuses.remove(cartEntryKey);
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}


	@Test
	public void testUIStatusProduct()
	{
		final String productKey = "X";
		final Object status = "S";
		final Map<String, Object> statuses = new HashMap<>();
		statuses.put(productKey, status);
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
		statuses.remove(productKey);
		classUnderTest.removeUiStatusForProduct(productKey);
		assertNull(classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testConfigIdForCartEntry()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		final Map<String, String> configs = new HashMap<>();
		configs.put(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(cartEntryKey, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		final Map<String, String> configs = new HashMap<>();
		configs.put(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		configs.remove(cartEntryKey);
		classUnderTest.removeConfigIdForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testCartEntryForProduct()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		final Map<String, String> cartEntryIds = new HashMap<>();
		cartEntryIds.put(productKey, cartEntryId);
		classUnderTest.setCartEntryForProduct(productKey, cartEntryId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeCartEntryForProduct(productKey);
		cartEntryIds.remove(productKey);
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryCartEntryMap()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		final Map<String, String> cartEntryIds = new HashMap<>();
		cartEntryIds.put(productKey, cartEntryId);
		classUnderTest.setCartEntryForProduct(productKey, cartEntryId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryId, productKey);
		//We expect that the corresponding product/cartEntry entry is gone!
		cartEntryIds.remove(productKey);
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigMap()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		final Map<String, String> configs = new HashMap<>();
		configs.put(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		configs.remove(cartEntryKey);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey, "");
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testGetSolrProperties()
	{
		final Set<String> solrProperties = new HashSet<>();
		classUnderTest.setSolrIndexedProperties(solrProperties);
		assertEquals(solrProperties, classUnderTest.getSolrIndexedProperties());
	}

	@Test
	public void testConfigurationProvider()
	{
		final ConfigurationProvider provider = new DummyProvider();
		classUnderTest.setConfigurationProvider(provider);
		assertEquals(provider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testConfigurationModelEngineState()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final String configId = "ID";
		classUnderTest.setConfigurationModelEngineState(configId, configModel);
		assertEquals(configModel, classUnderTest.getConfigurationModelEngineState(configId));
		classUnderTest.removeConfigurationModelEngineState(configId);
		assertNull(classUnderTest.getConfigurationModelEngineState(configId));
	}
}
