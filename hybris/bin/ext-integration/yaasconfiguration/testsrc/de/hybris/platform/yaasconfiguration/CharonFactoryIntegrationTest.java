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
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class CharonFactoryIntegrationTest<T> extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private YaasConfigurationService yaasConfigurationService;

	@Resource
	private CharonFactory charonFactory;

	@Resource
	private SessionService sessionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before()
	{

		final YaasProjectModel yaasProject = modelService.create(YaasProjectModel.class);
		yaasProject.setIdentifier("devproject");

		final YaasApplicationModel yaasApp = modelService.create(YaasApplicationModel.class);
		yaasApp.setIdentifier("devapplication");
		yaasApp.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		yaasApp.setClientSecret("4MAKkTecaOFewdlH");
		yaasApp.setPubsubClient("Order");
		yaasApp.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		yaasApp.setYaasProject(yaasProject);

		final YaasClientModel yaasClient = (YaasClientModel) modelService.create(YaasClientModel.class);
		yaasClient.setIdentifier("ProductClient");
		yaasClient.setClientURL("https://api.yaas.io/hybris/product/v2");
		yaasClient.setClientScope("hybris.product_read_unpublished");
		yaasClient.setYaasApplication(yaasApp);

		modelService.saveAll(Arrays.asList(yaasApp, yaasProject, yaasClient));

	}

	@Test
	public void testEmptyClientType() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");

		errorMustBeReported("clientType must not be null");
		charonFactory.client(null);

	}

	@Test
	public void testYaasClientCreation() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");

		final ProductClient productClient = charonFactory.client(ProductClient.class);

		assertNotNull(productClient);

		assertEquals(getCharonChache().size(), 1);
	}

	@Test
	public void testYaasClient_timeOut() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");

		updateYaasClientModelWithAdditionalConfig();

		final ProductClient productClient = charonFactory.client(ProductClient.class);

		errorMustBeReported("java.util.concurrent.TimeoutException");

		productClient.getProductByCode("anyproduct");
	}

	@Test
	public void testCharonReturnsFromCache() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");


		final ProductClient productClient = charonFactory.client(ProductClient.class);


		final ProductClient productClient2 = charonFactory.client(ProductClient.class);

		assertEquals(productClient, productClient2);

	}

	@Test
	public void testInvalidatesCacheFor_YaasClientModelUpdates() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");


		final ProductClient productClient = charonFactory.client(ProductClient.class);

		updateYaasClientModel();

		final ProductClient productClient2 = charonFactory.client(ProductClient.class);

		assertNotEquals(productClient, productClient2);

	}

	@Test
	public void testInvalidatesCacheFor_YaasApplicationModelUpdates() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");


		final ProductClient productClient = charonFactory.client(ProductClient.class);

		updateYaasApplicationModel();

		final ProductClient productClient2 = charonFactory.client(ProductClient.class);

		assertNotEquals(productClient, productClient2);

	}

	@Test
	public void testInvalidatesCacheFor_YaasClientModelDelete() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");

		final ProductClient productClient = charonFactory.client(ProductClient.class);

		deleteYaasClientModel();

		assertNotNull(productClient);

		assertEquals(getCharonChache().size(), 0);

	}

	@Test
	public void testInvalidatesCacheFor_YaasApplicationModelDelete() throws Exception
	{
		sessionService.setAttribute(SESSION_YAAS_APPLICATIONID, "devapplication");

		final ProductClient productClient = charonFactory.client(ProductClient.class);

		deleteYaasApplicationModel();

		assertNotNull(productClient);

		assertEquals(getCharonChache().size(), 0);

	}


	private void errorMustBeReported(final String msg)
	{
		expectedException.expectMessage(containsString(msg));
	}


	protected ConcurrentHashMap<String, Object> getCharonChache()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{

		final Field field = charonFactory.getClass().getDeclaredField("cache"); //NoSuchFieldException
		field.setAccessible(true);

		return (ConcurrentHashMap) field.get(charonFactory);
	}

	protected void updateYaasClientModel()
	{
		YaasClientModel yaasClient = new YaasClientModel();

		yaasClient.setIdentifier("ProductClient");

		yaasClient = flexibleSearchService.getModelByExample(yaasClient);

		yaasClient.setClientURL("https://api.yaas.io/hybris/product/v3");
		yaasClient.setClientScope("hybris.product_read_unpublished");

		modelService.save(yaasClient);
	}

	protected void updateYaasClientModelWithAdditionalConfig()
	{
		YaasClientModel yaasClient = new YaasClientModel();

		yaasClient.setIdentifier("ProductClient");

		yaasClient = flexibleSearchService.getModelByExample(yaasClient);

		final Map<String, String> additionalConfig = new HashMap();
		additionalConfig.put("retries", "0");
		additionalConfig.put("retriesInterval", "0");
		additionalConfig.put("timeout", "1");

		yaasClient.setAdditionalConfigurations(additionalConfig);

		modelService.save(yaasClient);
	}


	protected void deleteYaasClientModel()
	{
		YaasClientModel yaasClient = new YaasClientModel();

		yaasClient.setIdentifier("ProductClient");

		yaasClient = flexibleSearchService.getModelByExample(yaasClient);

		modelService.remove(yaasClient);
	}

	protected void updateYaasApplicationModel()
	{

		YaasApplicationModel yaasApp = new YaasApplicationModel();

		yaasApp.setIdentifier("devapplication");

		yaasApp = flexibleSearchService.getModelByExample(yaasApp);

		yaasApp.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");

		modelService.save(yaasApp);
	}

	protected void deleteYaasApplicationModel()
	{
		YaasApplicationModel yaasApp = new YaasApplicationModel();

		yaasApp.setIdentifier("devapplication");

		yaasApp = flexibleSearchService.getModelByExample(yaasApp);

		modelService.remove(yaasApp);
	}

}
