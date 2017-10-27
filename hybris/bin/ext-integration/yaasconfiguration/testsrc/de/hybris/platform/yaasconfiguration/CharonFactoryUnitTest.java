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

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.client.TargetProductClient;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.charon.CharonBuilder;


@RunWith(MockitoJUnitRunner.class)
public class CharonFactoryUnitTest extends YaasConfigurationTestUtils
{

	@Mock
	private YaasConfigurationService yaasConfigurationService;

	@Mock
	private YaasSessionService yaasSessionService;

	@Mock
	private CharonBuilder charonBuilder;

	private CharonFactory charonFactory;

	@Before
	public void setup()
	{
		charonFactory = new CharonFactory();
		charonFactory.setYaasSessionService(yaasSessionService);
		charonFactory.setYaasConfigurationService(yaasConfigurationService);
	}


	@Test
	public void testClient_validateParameter()
	{
		errorMustBeReported("clientType must not be null");
		charonFactory.client(null);
	}

	@Test
	public void testClient_noApplicationIdInSession()
	{
		errorMustBeReported("Failed to get current YaaS applicationId");
		charonFactory.client(ProductClient.class);
	}

	@Test
	public void testClient()
	{
		final Map<String, String> yaasConfig = new HashMap();

		yaasConfig.put(YAAS_OAUTH_URL, "https://api.yaas.io/hybris/oauth2/v1");

		when(yaasSessionService.getCurrentYaasAppId()).thenReturn("applicationId");
		when(yaasConfigurationService.buildYaasConfig("applicationId", ProductClient.class)).thenReturn(yaasConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		assertNotNull(charonFactory.client(ProductClient.class));

	}


	@Test
	public void testInValidateCache_validateParameter()
	{
		errorMustBeReported("key must not be null");
		charonFactory.inValidateCache(null);

	}

	@Test
	public void testInValidateCache_withDifferentApplicationId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(ProductClient.class, "applicationId2");

		//Expected to create 2 client configuration, because of 2 different applicationId
		assertEquals(getCharonChache().size(), 2);

		charonFactory.inValidateCache("applicationId");

		//Expected to remove the cache corresponding to given applicationId.
		assertEquals(getCharonChache().size(), 1);

	}

	@Test
	public void testInValidateCache_sameApplicationIdWithDifferentClient()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(TargetProductClient.class, "applicationId");

		//Expected to create 2 client configuration, because of 2 different client
		assertEquals(getCharonChache().size(), 2);

		charonFactory.inValidateCache("applicationId");

		//Expected to remove all, if it request to invalidate all the client for the given appId
		assertEquals(getCharonChache().size(), 0);

	}

	@Test
	public void testInValidateCache_sameApplicationId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(ProductClient.class, "applicationId");

		//Expected to create only one configuration, because of same client and applicationId
		assertEquals(getCharonChache().size(), 1);

		charonFactory.inValidateCache("applicationId");

		assertEquals(getCharonChache().size(), 0);

	}

	@Test
	public void testInValidateCache_clientId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(TargetProductClient.class, "applicationId");

		//Expected to create 2 client configuration, because of 2 different client
		assertEquals(getCharonChache().size(), 2);

		charonFactory.inValidateCache("applicationId#ProductClient");

		//Expected to remove only for the given client : it should not behave similar to testInValidateCache_sameApplicationIdWithDifferentClient
		assertEquals(getCharonChache().size(), 1);

	}

	@Test
	public void testBuildKey()
	{
		assertEquals("applicationId#ProductClient", charonFactory.buildCacheKey("applicationId", ProductClient.class.getName()));
	}

	protected void prepareClient(final Class client, final String appId)
	{

		final Map<String, String> yaasConfig = new HashMap();

		yaasConfig.put(YAAS_OAUTH_URL, "https://api.yaas.io/hybris/oauth2/v1");

		when(yaasSessionService.getCurrentYaasAppId()).thenReturn(appId);
		when(yaasConfigurationService.buildYaasConfig(appId, client)).thenReturn(yaasConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		charonFactory.client(client);

	}


	protected ConcurrentHashMap<String, Object> getCharonChache()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{

		final Field field = charonFactory.getClass().getDeclaredField("cache"); //NoSuchFieldException
		field.setAccessible(true);

		return (ConcurrentHashMap) field.get(charonFactory);
	}



}
