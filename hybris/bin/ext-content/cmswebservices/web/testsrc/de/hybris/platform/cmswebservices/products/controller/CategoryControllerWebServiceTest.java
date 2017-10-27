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
package de.hybris.platform.cmswebservices.products.controller;

import static de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother.ELECTRONICS;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductCategoryModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CategoryDataListWsDTO;
import de.hybris.platform.cmswebservices.dto.CategoryWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@NeedsEmbeddedServer(webExtensions =
		{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CategoryControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String PAGE_ENDPOINT = "/v1/productcatalogs/{catalogId}/versions/{versionId}/categories";
	
	@Resource
	private ProductCategoryModelMother productCategoryModelMother;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	
	@Resource
	private SiteModelMother siteModelMother;

	private CatalogVersionModel catalogVersion;
	@Before
	public void setup()
	{
		
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		catalogVersion = catalogVersionModelMother
				.createAppleStagedCatalogVersionModel();
		productCategoryModelMother.createDefaultCategory(catalogVersion);
	}
	
	@Test
	public void testGetCategoryByCode()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>()))
				.path(ELECTRONICS)
				.build()
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategoryWsDTO entity = response.readEntity(CategoryWsDTO.class);
		assertOnCategoryData(entity);
	}


	@Test
	public void testFindCategoryByText()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>())) //
				.queryParam("text", ELECTRONICS) //
				.queryParam("pageSize", 10) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final CategoryDataListWsDTO entity = response.readEntity(CategoryDataListWsDTO.class);
		assertThat(entity.getProductCategories(), notNullValue());
		assertThat(entity.getProductCategories().size(), is(1));
		assertOnCategoryData(entity.getProductCategories().get(0));
	}

	protected void assertOnCategoryData(final CategoryWsDTO categoryWsDTO)
	{
		assertThat(
				categoryWsDTO,
				allOf(hasProperty("name", is(ImmutableMap.<String, String>builder().put("en", ELECTRONICS).build())),
						hasProperty("code", is(ELECTRONICS)),
						hasProperty("catalogId", is(catalogVersion.getCatalog().getId())),
						hasProperty("catalogVersion", is(catalogVersion.getVersion()))
				));
	}
}
