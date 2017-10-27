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
package de.hybris.platform.personalizationyprofile.resolver.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.ServletException;

import jersey.repackaged.com.google.common.collect.Sets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxYaasAppIdResolverTest
{
	private static String FALLBACK_APP_ID = "fallbackAppId";
	private static String APP_ID = "appId";

	private final DefaultCxYaasAppIdResolver yaasAppIdResolver = new DefaultCxYaasAppIdResolver();
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private YaasSessionService yaasSessionService;
	@Mock
	private YaasConfigurationService yaasConfigurationService;
	@Mock
	private YaasProjectModel yaasProjectModel;
	@Mock
	private YaasApplicationModel yaasAppModel;
	@Mock
	private YaasClientModel yaasClientModel;
	@Mock
	private BaseSiteModel baseSiteModel;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		yaasAppIdResolver.setFallbackAppId(FALLBACK_APP_ID);
		yaasAppIdResolver.setBaseSiteService(baseSiteService);
		yaasAppIdResolver.setYaasSessionService(yaasSessionService);
		yaasAppIdResolver.setYaasConfigurationService(yaasConfigurationService);

		Mockito.when(yaasAppModel.getIdentifier()).thenReturn(APP_ID);
	}

	@Test
	public void getYaasAppIdFromSessionTest() throws ServletException, IOException
	{
		//given
		Mockito.when(yaasSessionService.getCurrentYaasAppId()).thenReturn(APP_ID);

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdForBaseSiteTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getYaasProjects()).thenReturn(Collections.singleton(yaasProjectModel));
		Mockito.when(yaasProjectModel.getYaasApplications()).thenReturn(Collections.singleton(yaasAppModel));

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenNoCurrentBaseSiteTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(null);

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(FALLBACK_APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenNoProjectsTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getYaasProjects()).thenReturn(Collections.emptySet());

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(FALLBACK_APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenBaseSiteHasMultipleProjectsTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getYaasProjects()).thenReturn(Sets.newHashSet(yaasProjectModel, new YaasProjectModel()));

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(FALLBACK_APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenNoApplicationTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getYaasProjects()).thenReturn(Collections.singleton(yaasProjectModel));
		Mockito.when(yaasProjectModel.getYaasApplications()).thenReturn(Collections.emptySet());

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(FALLBACK_APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenProjectHasMultipleApplicationsTest() throws ServletException, IOException
	{
		//given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseSiteModel.getYaasProjects()).thenReturn(Collections.singleton(yaasProjectModel));
		Mockito.when(yaasProjectModel.getYaasApplications()).thenReturn(Sets.newHashSet(yaasAppModel, new YaasApplicationModel()));

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(FALLBACK_APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenNoFallbakTest() throws ServletException, IOException
	{
		//given
		yaasAppIdResolver.setFallbackAppId(null);
		Mockito.when(yaasConfigurationService.takeFirstModel()).thenReturn(Optional.of(yaasAppModel));

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertEquals(APP_ID, yaasAppId);
	}

	@Test
	public void getYaasAppIdWhenNoAppInDatabaseTest() throws ServletException, IOException
	{
		//given
		yaasAppIdResolver.setFallbackAppId(null);
		Mockito.when(yaasConfigurationService.takeFirstModel()).thenReturn(Optional.empty());

		//when
		final String yaasAppId = yaasAppIdResolver.getYaasAppId();

		//then
		Assert.assertNull(yaasAppId);
	}
}
