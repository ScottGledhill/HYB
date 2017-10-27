/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.sites.controller;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.POWER_TOOLS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_ONLINE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_STAGED;
import static de.hybris.platform.cmswebservices.sites.controller.ExpectedSiteBuilder.buildApparel;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.sites.populator.model.ComparableSiteData;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.SiteData;
import de.hybris.platform.cmswebservices.data.SiteListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SiteControllerIntegrationTest extends ApiBaseIntegrationTest
{

	private static final String SITES_ENDPOINT = "/v1/sites";

	private static final SiteData EXPECTED_APPAREL_SITE = buildApparel();

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	@Test
	public void getOnSiteWillReturnAnEmptyListOfSitesWhenNothingIsAvailable() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), empty());
	}

	@Test
	public void getOnSiteWillReturnAListOfSitesWithApparelWhenApparelIsAvailable() throws Exception
	{
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
				{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };
		
		final CatalogVersionModel[] catalogVersionModels = new CatalogVersionModel[]
				{ catalogVersionModelMother.createAppleOnlineCatalogVersionModel(),
						catalogVersionModelMother.createAppleStagedCatalogVersionModel() };

		contentPageModelMother.HomePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersionModels);
		cmsSiteModelMother.createSiteWithTemplate(POWER_TOOLS, catalogVersionModels);
		
		importCsv("/cmswebservices/test/impex/siteTestContentCatalogPermissions.csv", "UTF-8");

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		assertThat(entity.getSites(), hasSize(1));
		final Collection<SiteData> sites = makeSiteDataComparable(entity.getSites());
		assertThat(sites, hasItem(EXPECTED_APPAREL_SITE));
	}

	@Test
	public void theApparelSiteItemWillHaveUidAndBaseUrlAndSiteName() throws Exception
	{
		final CatalogVersionModel[] allowedCatalogVersionModels = new CatalogVersionModel[]
				{ catalogVersionModelMother.createOnlineCatalogVersionModelWithId(ID_ONLINE),
				catalogVersionModelMother.createStagedCatalogVersionModelWithId(ID_STAGED) };

		contentPageModelMother.HomePage(allowedCatalogVersionModels[0]);
		cmsSiteModelMother.createSiteWithTemplate(APPAREL, allowedCatalogVersionModels);
		
		importCsv("/cmswebservices/test/impex/siteTestContentCatalogPermissions.csv", "UTF-8");

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(SITES_ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SiteListData entity = response.readEntity(SiteListData.class);
		final SiteData siteData = new ComparableSiteData(entity.getSites().iterator().next());
		assertThat(siteData, is(EXPECTED_APPAREL_SITE));
	}

	protected static Collection<SiteData> makeSiteDataComparable(final Collection<SiteData> siteData) throws Exception
	{
		return siteData.stream().map(ComparableSiteData::new).collect(toList());
	}

}
