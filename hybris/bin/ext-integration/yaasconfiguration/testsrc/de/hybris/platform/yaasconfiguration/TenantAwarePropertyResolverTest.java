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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class TenantAwarePropertyResolverTest extends ServicelayerBaseTest
{

	private static final String PROJECT_ID = "projectId";
	private static final String APP_ID = "appId";

	private static final String CLIENT_ID = "clientId";
	private static final String CLIENT_SECRET = "clientSecret";

	@Resource
	private ModelService modelService;
	@Resource
	private YaasConfigurationService yaasConfigurationService;

	private TenantAwarePropertyResolver propertyResolver;
	private ApplicationPropertyResolver resolver;

	protected void createConfiguration(final String projectId, final String appId, final String clientId,
			final String clientSecret)
	{
		final YaasProjectModel yaasProject = modelService.create(YaasProjectModel.class);
		yaasProject.setIdentifier(projectId);

		final YaasApplicationModel yaasApp = modelService.create(YaasApplicationModel.class);
		yaasApp.setClientId(clientId);
		yaasApp.setClientSecret(clientSecret);
		yaasApp.setIdentifier(appId);
		yaasApp.setYaasProject(yaasProject);
		modelService.saveAll(Arrays.asList(yaasApp, yaasProject));
	}

	@Test
	public void testResolverWithEmptyAppId() throws InterruptedException, ExecutionException
	{
		resolver = new ApplicationPropertyResolver(yaasConfigurationService);

		final String tenantId = Registry.getCurrentTenantNoFallback().getTenantID();

		propertyResolver = new TenantAwarePropertyResolver(tenantId, resolver);

		createConfiguration(PROJECT_ID, APP_ID, CLIENT_ID, CLIENT_SECRET);

		final String clientId = callInAnotherThread(() -> propertyResolver.lookup("oauth.clientId"));
		final String clientSecret = callInAnotherThread(() -> propertyResolver.lookup("oauth.clientSecret"));

		assertEquals(CLIENT_ID, clientId);
		assertEquals(CLIENT_SECRET, clientSecret);

	}

	@Test
	public void testResolverWithGivenAppId() throws InterruptedException, ExecutionException
	{
		resolver = new ApplicationPropertyResolver(yaasConfigurationService, APP_ID);

		final String tenantId = Registry.getCurrentTenantNoFallback().getTenantID();

		propertyResolver = new TenantAwarePropertyResolver(tenantId, resolver);

		createConfiguration(PROJECT_ID, APP_ID, CLIENT_ID, CLIENT_SECRET);

		final String clientId = callInAnotherThread(() -> propertyResolver.lookup("oauth.clientId"));
		final String clientSecret = callInAnotherThread(() -> propertyResolver.lookup("oauth.clientSecret"));

		assertEquals(CLIENT_ID, clientId);
		assertEquals(CLIENT_SECRET, clientSecret);
	}

	private static <T> T callInAnotherThread(final Callable<T> task) throws InterruptedException, ExecutionException
	{
		final ExecutorService executor = Executors.newFixedThreadPool(1);
		final Future<T> future = executor.submit(task);
		final T result = future.get();
		executor.shutdown();
		return result;
	}



}
