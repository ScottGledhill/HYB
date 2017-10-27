/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class CxApplicationPropertyResolverTest extends ServicelayerTransactionalBaseTest
{
	private static final String PROJECT_ID = "projectId";
	private static final String APP_ID = "appId";
	private static final String PROJECT_ID_1 = "projectId_1";
	private static final String APP_ID_1 = "appId_1";
	private static final String CLIENT_ID = "clientId";
	private static final String CLIENT_SECRET = "clientSecret";

	@Resource
	private ModelService modelService;
	@Resource
	private YaasConfigurationService yaasConfigurationService;
	private CxApplicationPropertyResolver propertyResolver;

	protected void createConfiguration(final String projectId, final String appId, final String clientId, final String clientSecret)
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
	public void testResolverWithSingleConfiguration()
	{
		//given
		propertyResolver = new CxApplicationPropertyResolver(yaasConfigurationService);
		createConfiguration(PROJECT_ID, APP_ID, CLIENT_ID, CLIENT_SECRET);

		//when
		final boolean contains = propertyResolver.contains(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);
		final String projectId = propertyResolver.lookup(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);

		//then
		assertTrue(contains);
		assertEquals(PROJECT_ID, projectId);
	}

	@Test
	public void testResolverWithGivenAppId()
	{
		//given
		propertyResolver = new CxApplicationPropertyResolver(yaasConfigurationService, APP_ID);
		createConfiguration(PROJECT_ID, APP_ID, CLIENT_ID, CLIENT_SECRET);

		//when
		final boolean contains = propertyResolver.contains(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);
		final String projectId = propertyResolver.lookup(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);

		//then
		assertTrue(contains);
		assertEquals(PROJECT_ID, projectId);
	}

	@Test
	public void testResolverWithoutConfiguration()
	{
		//given
		propertyResolver = new CxApplicationPropertyResolver(yaasConfigurationService);

		//when
		final boolean contains = propertyResolver.contains(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);
		final String projectId = propertyResolver.lookup(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);

		//then
		assertFalse(contains);
		assertNull(projectId);
	}

	@Test
	public void testResolverWithMultipleConfiguration()
	{
		//given
		propertyResolver = new CxApplicationPropertyResolver(yaasConfigurationService, APP_ID);
		createConfiguration(PROJECT_ID_1, APP_ID_1, CLIENT_ID, CLIENT_SECRET);
		createConfiguration(PROJECT_ID, APP_ID, CLIENT_ID, CLIENT_SECRET);

		//when
		final boolean contains = propertyResolver.contains(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);
		final String projectId = propertyResolver.lookup(PersonalizationyprofileConstants.YAAS_CONFIGURATION_PROJECT_ID_PROPERTY);

		//then
		assertTrue(contains);
		assertEquals(PROJECT_ID, projectId);
	}
}
