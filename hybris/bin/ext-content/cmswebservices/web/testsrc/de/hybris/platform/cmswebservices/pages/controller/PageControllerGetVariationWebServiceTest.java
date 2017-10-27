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
package de.hybris.platform.cmswebservices.pages.controller;


import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.LanguageModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageControllerGetVariationWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String BASE_PAGE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages";
	private static final String FALLBACKS = "fallbacks";
	private static final String VARIATIONS = "variations";
	private static final String DEFAULT_PAGE = "defaultPage";
	private static final String TYPECODE = "typeCode";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	@Resource
	private LanguageModelMother languageModelMother;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp() {
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
	}

	@Test
	public void shouldGetFallbackContentPages()
	{
		contentPageModelMother.DefaultEmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.DefaultHomePage(catalogVersion);
		contentPageModelMother.HomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, Boolean.TRUE) //
				.queryParam(TYPECODE, ContentPageModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(2));

		final AbstractPageData emailPageData = entity.getPages().get(0);
		assertThat(emailPageData.getUid(), is(ContentPageModelMother.UID_DEFAULT_EMAILPAGE));
		assertThat(emailPageData.getDefaultPage(), is(Boolean.TRUE));

		final AbstractPageData homePageData = entity.getPages().get(1);
		assertThat(homePageData.getUid(), is(ContentPageModelMother.UID_DEFAULT_HOMEPAGE));
		assertThat(homePageData.getDefaultPage(), is(Boolean.TRUE));
	}

	@Test
	public void shouldGetVariationContentPages()
	{
		contentPageModelMother.DefaultEmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.EmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.HomePage(catalogVersion);
		contentPageModelMother.DefaultHomePage(catalogVersion);
		productPageModelMother.ProductPage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, Boolean.FALSE) //
				.queryParam(TYPECODE, ContentPageModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(2));

		final AbstractPageData emailPageData = entity.getPages().get(0);
		assertThat(emailPageData.getUid(), is(ContentPageModelMother.UID_EMAILPAGE));
		assertThat(emailPageData.getDefaultPage(), is(Boolean.FALSE));

		final AbstractPageData homePageData = entity.getPages().get(1);
		assertThat(homePageData.getUid(), is(ContentPageModelMother.UID_HOMEPAGE));
		assertThat(homePageData.getDefaultPage(), is(Boolean.FALSE));
	}

	@Test
	public void shouldGetVariationProductPages()
	{
		contentPageModelMother.EmailPageFromHomePageTemplate(catalogVersion);
		productPageModelMother.ProductPage(catalogVersion);
		productPageModelMother.DefaultProductPage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, Boolean.FALSE) //
				.queryParam(TYPECODE, ProductPageModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageListData entity = response.readEntity(PageListData.class);
		assertThat(entity.getPages().size(), is(1));

		final AbstractPageData productPageData = entity.getPages().get(0);
		assertThat(productPageData.getUid(), is(ProductPageModelMother.UID_PRODUCT_PAGE));
		assertThat(productPageData.getDefaultPage(), is(Boolean.FALSE));
	}

	@Test
	public void shouldGetVariationContentPagesForEmailPage()
	{
		contentPageModelMother.DefaultEmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.EmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.HomePage(catalogVersion);
		contentPageModelMother.DefaultHomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_DEFAULT_EMAILPAGE).path(VARIATIONS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final UidListData entity = response.readEntity(UidListData.class);
		assertThat(entity.getUids().size(), is(1));
		assertThat(entity.getUids().get(0), is(ContentPageModelMother.UID_EMAILPAGE));
	}

	@Test
	public void shouldGetFallbackContentPagesForEmailPage()
	{
		contentPageModelMother.EmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.DefaultEmailPageFromHomePageTemplate(catalogVersion);
		contentPageModelMother.DefaultHomePage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.path(ContentPageModelMother.UID_EMAILPAGE).path(FALLBACKS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final UidListData entity = response.readEntity(UidListData.class);

		assertThat(entity.getUids().size(), is(1));
		assertThat(entity.getUids().get(0), is(ContentPageModelMother.UID_DEFAULT_EMAILPAGE));
	}

	@Test
	public void shouldFailGetFallbackPagesInvalidTypeCode()
	{
		// not passing a typeCode from a supported page model
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, Boolean.TRUE) //
				.queryParam(TYPECODE, CMSParagraphComponentModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldFailGetFallbackPagesMissingTypeCodeParam()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, Boolean.TRUE) //
				.queryParam(TYPECODE, " ").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldFailGetFallbackPagesMissingPrimaryParam()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(BASE_PAGE_ENDPOINT, Maps.newHashMap()))
				.queryParam(DEFAULT_PAGE, " ") //
				.queryParam(TYPECODE, ContentPageModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}
}
