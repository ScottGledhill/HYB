/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.personalizationyprofile.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationyprofile.resolver.CxYaasAppIdResolver;
import de.hybris.platform.personalizationyprofile.yaas.client.CxSecuredGraphClient;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class YaasConfigurationUtilTest
{
	private static String APP_ID = "appId";
	private static String KEY1 = "key1";
	private static String VALUE1 = "value1";

	private final CxYaasConfigurationUtil yaasConfigurationUtil = new CxYaasConfigurationUtil();
	@Mock
	private YaasConfigurationService yaasConfigurationService;
	@Mock
	private CxYaasAppIdResolver cxYaasAppIdResolver;
	@Mock
	private YaasApplicationModel yaasAppModel;
	@Mock
	private YaasClientModel yaasClientModel;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		yaasConfigurationUtil.setYaasConfigurationService(yaasConfigurationService);
		yaasConfigurationUtil.setCxYaasAppIdResolver(cxYaasAppIdResolver);

		Mockito.when(cxYaasAppIdResolver.getYaasAppId()).thenReturn(APP_ID);
		Mockito.when(yaasAppModel.getIdentifier()).thenReturn(APP_ID);
	}

	@Test
	public void buildYaasConfigTest()
	{
		//given
		final Map<String, String> yaasConfig = new HashMap<>();
		yaasConfig.put(KEY1, VALUE1);
		Mockito.when(yaasConfigurationService.buildYaasConfig(APP_ID, CxSecuredGraphClient.class)).thenReturn(yaasConfig);

		//when
		final Map<String, String> config = yaasConfigurationUtil.buildYaasConfig(CxSecuredGraphClient.class);

		//then
		Assert.assertNotNull(config);
		Assert.assertEquals(1, config.size());
		Assert.assertTrue(config.containsKey(KEY1));
	}


	@Test
	public void buildYaasConfigWhenNoModelInDatabaseTest()
	{
		//given
		Mockito.when(yaasConfigurationService.buildYaasConfig(APP_ID, CxSecuredGraphClient.class)).thenThrow(
				new ModelNotFoundException("not found"));

		//when
		final Map<String, String> config = yaasConfigurationUtil.buildYaasConfig(CxSecuredGraphClient.class);

		//then
		Assert.assertNotNull(config);
		Assert.assertTrue(config.isEmpty());
	}

	@Test
	public void isConfigurationPresentTest()
	{
		//given
		Mockito.when(yaasConfigurationService.getYaasApplicationForId(APP_ID)).thenReturn(yaasAppModel);
		Mockito.when(yaasConfigurationService.getYaasClientForId(CxSecuredGraphClient.class.getSimpleName())).thenReturn(
				yaasClientModel);

		//when
		final boolean confExist = yaasConfigurationUtil.isConfigurationPresent(CxSecuredGraphClient.class);

		//then
		Assert.assertTrue(confExist);
	}

	@Test
	public void isConfigurationPresentWhenNoAppIdTest()
	{
		//given
		Mockito.when(cxYaasAppIdResolver.getYaasAppId()).thenReturn(null);

		//when
		final boolean confExist = yaasConfigurationUtil.isConfigurationPresent(CxSecuredGraphClient.class);

		//then
		Assert.assertFalse(confExist);
	}
}
