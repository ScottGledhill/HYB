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
package de.hybris.platform.personalizationcms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultCxCustomizationServiceCmsIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String USER_ID = "customer1@hybris.com";

	@Resource
	private CxService cxService;
	@Resource
	private CxCustomizationService cxCustomizationService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationcms/test/testdata_personalizationcms.impex", "utf-8");

	}


	@Test
	public void findEnabledCustomizationsWithPageIdFromContentOnlyTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page1");
		params.put("statuses", CxItemStatus.ENABLED.getCode());
		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}


	@Test(expected = IllegalArgumentException.class)
	public void findCustomizationsWithPageIdFromContentOnlyForWrongStatusCodesTest()
	{
		//given
		final Set<String> statuses = Sets.newHashSet("WRONG_STATUS1", "WRONG_STATUS2");
		final String statusesParam = statuses.stream()
				.collect(Collectors.joining(", "));

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page1");
		params.put("statuses", statusesParam);

		//when
		cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

	}

	@Test
	public void findCustomizationsWithPageIdFromContentOnlyForEnabledAndDisabledStatusesTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1", "customization6");
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode(), CxItemStatus.DISABLED.getCode());
		final String statusesParam = statuses.stream()
				.collect(Collectors.joining(", "));

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page1");
		params.put("statuses", statusesParam);

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findCustomizationsForEnabledAndDisabledStatusesTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1", "customization2", "customization3", "customization4", "customization5", "customization6");
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode(), CxItemStatus.DISABLED.getCode());
		final String statusesParam = statuses.stream()
				.collect(Collectors.joining(", "));

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("statuses", statusesParam);

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findCustomizationsForEnabledStatusTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1", "customization2", "customization3", "customization4");
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.ENABLED.getCode());
		final String statusesParam = statuses.stream()
				.collect(Collectors.joining(", "));

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("statuses", statusesParam);

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findCustomizationsForDisabledStatusTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization5", "customization6");
		final Set<String> statuses = Sets.newHashSet(CxItemStatus.DISABLED.getCode());
		final String statusesParam = statuses.stream()
				.collect(Collectors.joining(", "));

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("statuses", statusesParam);

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findEnabledCustomizationsWithPageIdFromContentOnlyNegatedTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization2", "customization3", "customization4");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page1");
		params.put("statuses", CxItemStatus.ENABLED.getCode());
		params.put("negatePageId", "true");

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}



	@Test
	public void findEnabledCustomizationsWithPageIdFromTemplateOnlyTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization2", "customization3", "customization4");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page2");
		params.put("statuses", CxItemStatus.ENABLED.getCode());
		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findEnabledCustomizationsWithPageIdFromTemplateOnlyNegatedTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page2");
		params.put("statuses", CxItemStatus.ENABLED.getCode());
		params.put("negatePageId", "true");
		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findEnabledCustomizationsWithPageIdFromBothTemplateAndContentTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization1", "customization2", "customization3", "customization4");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page3");
		params.put("statuses", CxItemStatus.ENABLED.getCode());

		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}


	@Test
	public void findEnabledCustomizationsWithPageIdFromBothTemplateAndContentNegatedTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet();

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page3");
		params.put("statuses", CxItemStatus.ENABLED.getCode());
		params.put("negatePageId", "true");
		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}

	@Test
	public void findDisabledCustomizationsWithPageIdFromContentOnlyTest()
	{
		//given
		final Set<String> expectedCodes = Sets.newHashSet("customization5");

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("pageId", "page4");
		params.put("statuses", CxItemStatus.DISABLED.getCode());
		//when
		final SearchResult<CxCustomizationModel> customizations = cxCustomizationService.getCustomizations(catalogVersion, params,
				0, 100);

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getResult());
		assertEquals(expectedCodes,
				customizations.getResult().stream().map(CxCustomizationModel::getCode).collect(Collectors.toSet()));
	}


	public CxService getCxService()
	{
		return cxService;
	}

	public void setCxService(final CxService cxService)
	{
		this.cxService = cxService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
