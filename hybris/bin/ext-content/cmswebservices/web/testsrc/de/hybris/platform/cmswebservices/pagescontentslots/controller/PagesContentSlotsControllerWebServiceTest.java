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
package de.hybris.platform.cmswebservices.pagescontentslots.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageContentSlotListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PagesContentSlotsControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String PAGE_CONTENT_SLOT_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslots";

	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;
	@Resource
	private ContentSlotForTemplateModelMother contentSlotForTemplateModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private ContentSlotModelMother contentSlotModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;

	private static final String INVALID_ID = "invalid-id";

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(TemplateSite.ELECTRONICS, catalogVersion);
	}

	@Test
	public void shouldGetPagesForFooterSlotFromPageRelations() throws Exception
	{
		// footer slot is present in 2 pages
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);
		contentSlotForPageModelMother.FooterEmailPage_Empty(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_SLOT_ID, ContentSlotModelMother.UID_FOOTER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(2));
	}

	@Test
	public void shouldGetSinglePageForFooterSlot() throws Exception
	{
		// footer slot is present in the page template and a single page
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);
		contentSlotForTemplateModelMother.FooterHomepage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_SLOT_ID, ContentSlotModelMother.UID_FOOTER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(1));
	}

	@Test
	public void shouldGetEmptyForHeaderSlot() throws Exception
	{
		contentSlotModelMother.createHeaderSlotWithParagraph(catalogVersion);

		// No CMS relation was created for the header content slot
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_SLOT_ID, ContentSlotModelMother.UID_HEADER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList(), empty());
	}

	@Test
	public void shouldGetEmptyForInvalidSlot() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_SLOT_ID, INVALID_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList(), empty());
	}

	@Test
	public void shouldGetPagesForFooterSlotInTemplates() throws Exception
	{
		// footer slot is present in 3 templates
		contentPageModelMother.HomePage(catalogVersion);
		contentPageModelMother.SearchPage(catalogVersion);
		contentPageModelMother.EmailPageFromEmailPageTemplate(catalogVersion);
		contentSlotForTemplateModelMother.FooterHomepage(catalogVersion);
		contentSlotForTemplateModelMother.FooterEmailPage(catalogVersion);
		contentSlotForTemplateModelMother.FooterSearchPage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_SLOT_ID, ContentSlotModelMother.UID_FOOTER).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(3));
	}

	@Test
	public void shouldGetContentSlotsInPage() throws Exception
	{
		contentSlotForPageModelMother.LogoHomepage(catalogVersion);
		contentSlotForPageModelMother.FooterHomepage_Empty(catalogVersion);
		contentSlotForPageModelMother.HeaderHomepage_FlashComponentOnly(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, ContentPageModelMother.UID_HOMEPAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList().size(), is(3));
	}

	@Test
	public void shouldGetEmptyForInvalidPage() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_ENDPOINT, new HashMap<>()))
				.queryParam(CmswebservicesConstants.URI_PAGE_ID, INVALID_ID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageContentSlotListData entity = response.readEntity(PageContentSlotListData.class);
		assertThat(entity.getPageContentSlotList(), empty());
	}
}
