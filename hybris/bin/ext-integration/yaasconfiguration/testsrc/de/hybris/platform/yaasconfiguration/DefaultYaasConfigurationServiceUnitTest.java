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

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_SCOPE;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTID;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTSECRET;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_TENANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultYaasConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultYaasConfigurationServiceUnitTest extends YaasConfigurationTestUtils
{

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private YaasApplicationModel yaasApplicationModel;

	@Mock
	private YaasClientModel yaasClientModel;

	private DefaultYaasConfigurationService configurationService;

	@Before
	public void setup()
	{
		configurationService = new DefaultYaasConfigurationService();
		configurationService.setFlexibleSearchService(flexibleSearchService);
		when(yaasApplicationModel.getIdentifier()).thenReturn("applicationId");
		when(yaasClientModel.getIdentifier()).thenReturn("clientId");
	}


	@Test
	public void testGetYaasApplicationForId()
	{
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(yaasApplicationModel);
		when(yaasApplicationModel.getIdentifier()).thenReturn("applicationId");

		assertEquals("applicationId", configurationService.getYaasApplicationForId("applicationId").getIdentifier());
	}

	@Test
	public void testGetYaasApplicationForId_validation()
	{
		errorMustBeReported("appId must not be null");
		configurationService.getYaasApplicationForId(null);

	}

	@Test
	public void testGetYaasClientForId()
	{
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(yaasClientModel);

		assertEquals("clientId", configurationService.getYaasClientForId("clientId").getIdentifier());
	}

	@Test
	public void testGetYaasClientForId_validation()
	{
		errorMustBeReported("clientId must not be null");
		configurationService.getYaasClientForId(null);
	}

	@Test
	public void testBuildYaasConfig()
	{

		final YaasApplicationModel yaasApp = createYaasApplication();
		final YaasClientModel yaasClient = createYaasClient();

		configurationService.setFlexibleSearchService(flexibleSearchService);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasApplicationModel.class))).thenReturn(yaasApp);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasClientModel.class))).thenReturn(yaasClient);

		final Map<String, String> configValue = configurationService.buildYaasConfig("applicationId", ProductClient.class);

		assertNotNull(configValue);

		assertEquals(configValue.get(YAAS_OAUTH_URL), yaasApp.getOauthURL());
		assertEquals(configValue.get(YAAS_OAUTH_CLIENTID), yaasApp.getClientId());
		assertEquals(configValue.get(YAAS_OAUTH_CLIENTSECRET), yaasApp.getClientSecret());
		assertEquals(configValue.get(YAAS_TENANT), yaasApp.getYaasProject().getIdentifier());
		assertEquals(configValue.get(YAAS_CLIENT_URL), yaasClient.getClientURL());
		assertEquals(configValue.get(YAAS_CLIENT_SCOPE), yaasClient.getClientScope());

	}

	@Test
	public void testBuildYaasConfig_additionalConfiguration()
	{

		final YaasApplicationModel yaasApp = createYaasApplication();
		final YaasClientModel yaasClient = createYaasClient();
		updateYaasClientWithAdditionalConfig(yaasClient);

		configurationService.setFlexibleSearchService(flexibleSearchService);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasApplicationModel.class))).thenReturn(yaasApp);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasClientModel.class))).thenReturn(yaasClient);

		final Map<String, String> configValue = configurationService.buildYaasConfig("applicationId", ProductClient.class);

		assertEquals(configValue.get("retries"), yaasClient.getAdditionalConfigurations().get("retries"));
		assertEquals(configValue.get("retriesInterval"), yaasClient.getAdditionalConfigurations().get("retriesInterval"));
		assertEquals(configValue.get("timeout"), yaasClient.getAdditionalConfigurations().get("timeout"));

	}

	@Test
	public void testBuildYaasConfig_validationAppId()
	{
		errorMustBeReported("appId must not be null");
		configurationService.buildYaasConfig(null, null);
	}

	@Test
	public void testBuildYaasConfig_validationClientType()
	{
		errorMustBeReported("clientType must not be null");
		configurationService.buildYaasConfig("appId", null);
	}

	private YaasApplicationModel createYaasApplication()
	{

		final YaasProjectModel yaasProject = new YaasProjectModel();
		yaasProject.setIdentifier("devproject");

		final YaasApplicationModel yaasApp = new YaasApplicationModel();

		yaasApp.setIdentifier("devapplication");
		yaasApp.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		yaasApp.setClientSecret("4MAKkTecaOFewdlH");
		yaasApp.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		yaasApp.setYaasProject(yaasProject);

		return yaasApp;
	}

	private YaasClientModel createYaasClient()
	{

		final YaasClientModel yaasClient = new YaasClientModel();

		yaasClient.setIdentifier("ProductClient");
		yaasClient.setClientURL("https://api.yaas.io/hybris/product/v2");
		yaasClient.setClientScope("hybris.product_read_unpublished");

		return yaasClient;
	}

	protected void updateYaasClientWithAdditionalConfig(final YaasClientModel yaasClient)
	{
		final Map<String, String> additionalConfig = new HashMap();
		additionalConfig.put("retries", "0");
		additionalConfig.put("retriesInterval", "0");
		additionalConfig.put("timeout", "1");

		yaasClient.setAdditionalConfigurations(additionalConfig);
	}


}
