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
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class YaasConfigurationServiceIntegrationTest extends ServicelayerTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private YaasConfigurationService yaasConfigurationService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testEmptyApplicationId() throws Exception
	{
		errorMustBeReported("appId must not be null");
		yaasConfigurationService.getYaasApplicationForId(null);
	}

	@Test
	public void testEmptyClientType() throws Exception
	{
		errorMustBeReported("clientId must not be null");
		yaasConfigurationService.getYaasClientForId(null);
	}

	@Test
	public void testGetYaasClientForId() throws Exception
	{
		createYaasClient();

		final YaasClientModel clientModel = yaasConfigurationService.getYaasClientForId("ProductClient");

		assertEquals(clientModel.getIdentifier(), "ProductClient");
		assertEquals(clientModel.getClientURL(), "https://api.yaas.io/hybris/product/v2");
		assertEquals(clientModel.getClientScope(), "hybris.product_read_unpublished");
	}

	@Test
	public void testGetYaasApplicationForId() throws Exception
	{
		createYaasApplication();

		final YaasApplicationModel applicationModel = yaasConfigurationService.getYaasApplicationForId("devapplication");

		assertEquals(applicationModel.getIdentifier(), "devapplication");
		assertEquals(applicationModel.getClientId(), "5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		assertEquals(applicationModel.getClientSecret(), "4MAKkTecaOFewdlH");
		assertEquals(applicationModel.getOauthURL(), "https://api.yaas.io/hybris/oauth2/v1");
	}

	@Test
	public void testBuildYaasConfig_invalidIds() throws Exception
	{
		createYaasApplication();
		createYaasClient();
		errorMustBeReported("No result for the given example [YaasApplicationModel (<unsaved>)] was found.");

		yaasConfigurationService.buildYaasConfig("Invalid", "invalid".getClass());

		//assertEquals(map.size(), 0);

	}


	@Test
	public void testBuildYaasConfig_validIds() throws Exception
	{
		createYaasApplication();
		createYaasClient();

		final Map<String, String> map = yaasConfigurationService.buildYaasConfig("devapplication", ProductClient.class);

		assertEquals(map.get(YAAS_OAUTH_CLIENTID), "5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		assertEquals(map.get(YAAS_OAUTH_CLIENTSECRET), "4MAKkTecaOFewdlH");
		assertEquals(map.get(YAAS_OAUTH_URL), "https://api.yaas.io/hybris/oauth2/v1");


		assertEquals(map.get(YAAS_CLIENT_URL), "https://api.yaas.io/hybris/product/v2");
		assertEquals(map.get(YAAS_CLIENT_SCOPE), "hybris.product_read_unpublished");

	}


	private void createYaasApplication()
	{
		final YaasProjectModel yaasProject = modelService.create(YaasProjectModel.class);
		yaasProject.setIdentifier("devproject");

		final YaasApplicationModel yaasApp = modelService.create(YaasApplicationModel.class);

		yaasApp.setIdentifier("devapplication");
		yaasApp.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		yaasApp.setClientSecret("4MAKkTecaOFewdlH");
		yaasApp.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		yaasApp.setYaasProject(yaasProject);

		modelService.save(yaasApp);
	}

	private void createYaasClient()
	{

		final YaasClientModel yaasClient = (YaasClientModel) modelService.create(YaasClientModel.class);

		yaasClient.setIdentifier("ProductClient");
		yaasClient.setClientURL("https://api.yaas.io/hybris/product/v2");
		yaasClient.setClientScope("hybris.product_read_unpublished");

		modelService.save(yaasClient);
	}

	private void errorMustBeReported(final String msg)
	{
		expectedException.expectMessage(containsString(msg));
	}

}
