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
package de.hybris.platform.cmswebservices.catalogversiondetails.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED1;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_LAPTOPS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_PHONES;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.CatalogVersionDetailData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionDetailsControllerIntegrationTest extends ApiBaseIntegrationTest
{
	private static final String CONTENT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/catalogversiondetails";
	private static final String PRODUCT_CATALOG_ENDPOINT = "/v1/sites/{siteId}/productcatalogversiondetails";

	@Resource
	private SiteModelMother siteModelMother;

	@Test
	public void getContentCatalogVersionDetailsWillReturnAnEmptyList() throws Exception
	{
		// create site with empty catalog
		siteModelMother.createElectronicsWithAppleCatalog();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getCatalogVersionDetails(), empty());
	}

	@Test
	public void getContentCatalogVersionDetailsWillReturnTheAllowedContentCatalog() throws Exception
	{
		// create site with staged and online catalog
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		importCsv("/cmswebservices/test/impex/stagedContentCatalogPermissions.csv", "UTF-8");

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(CONTENT_CATALOG_ENDPOINT, new HashMap<>())).build() //
				.accept(APPLICATION_JSON) //
				.get();

		assertResponse(OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getCatalogVersionDetails(), hasSize(1));

		List<TestCatalogVersionDetailData> actual = entity.getCatalogVersionDetails().stream().map(e -> new TestCatalogVersionDetailData(e)).collect(Collectors.toList());
		assertThat(actual, containsInAnyOrder(
				new TestCatalogVersionDetailData(ID_APPLE, STAGED)
				));

	}

	
	@Test
	public void getProductCatalogVersionDetailsWillReturnAListOfOnlineAndStaged() throws Exception
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		importCsv("/cmswebservices/test/impex/stagedContentCatalogPermissions.csv", "UTF-8");
		importCsv("/cmswebservices/test/impex/productCatalogPermissions.csv", "UTF-8");
		
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PRODUCT_CATALOG_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getCatalogVersionDetails(), hasSize(4));

		List<TestCatalogVersionDetailData> actual = entity.getCatalogVersionDetails().stream().map(e -> new TestCatalogVersionDetailData(e)).collect(Collectors.toList());
		assertThat(actual, containsInAnyOrder(
				new TestCatalogVersionDetailData(ID_LAPTOPS, STAGED1),
				new TestCatalogVersionDetailData(ID_LAPTOPS, ONLINE),
				new TestCatalogVersionDetailData(ID_PHONES, STAGED1),
				new TestCatalogVersionDetailData(ID_PHONES, ONLINE)
				));
	}
	
	public static class TestCatalogVersionDetailData extends CatalogVersionDetailData{
		
		private static final long serialVersionUID = -5788222831721250158L;
		
		public TestCatalogVersionDetailData(CatalogVersionDetailData source){
			this.setCatalogId(source.getCatalogId());
			this.setVersion(source.getVersion());
			this.setName(source.getName());
		}
		public TestCatalogVersionDetailData(CatalogTemplate catalogTemplate, CatalogVersion catalogVersion){
			this.setCatalogId(catalogTemplate.name());
			this.setVersion(catalogVersion.name().toLowerCase());
			Map<String, String> name = new HashMap<>();
			name.put("en", catalogTemplate.getFirstInstanceOfHumanName());
			this.setName(name);
			
		}
		
		@Override
		public String toString()
		{
			return format("[catalogId: %s, name:  %s, version: %s]", this.getCatalogId(), this.getName().get("en"), this.getVersion());
		}
		@Override
		public boolean equals(Object obj)
		{
			CatalogVersionDetailData compared = (CatalogVersionDetailData) obj;
			boolean match = this.getCatalogId().equals(compared.getCatalogId())
					&& this.getName().get("en").equals(compared.getName().get("en"))
					&& this.getVersion().equals(compared.getVersion());
			return match;
		}
		
	}

}
