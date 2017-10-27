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
package de.hybris.platform.cmswebservices.catalogversions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static java.util.Locale.ENGLISH;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.DisplayConditionData;
import de.hybris.platform.cmswebservices.data.OptionData;
import de.hybris.platform.cmswebservices.page.DisplayCondition;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CatalogVersionControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}";

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ContentCatalogModelMother contentCatalogModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;

	@Test
	public void shouldFailGetCatalogVersion_VersionIdInvalid()
	{
		// create site with empty catalog
		siteModelMother.createElectronicsWithAppleCatalog();

		final Map<String, String> params = new HashMap<>();
		params.put(CmswebservicesConstants.URI_VERSION_ID, "invalid");

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, params)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.NOT_FOUND, response);
	}

	@Test
	public void shouldGetCatalogVersion()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getUid(), is(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name()));
		assertThat(entity.getName().get(ENGLISH.toString()),
				is(ContentCatalogModelMother.CatalogTemplate.ID_APPLE.getHumanName().get(ENGLISH.toString())));
		assertThat(entity.getVersion(), is(CatalogVersionModelMother.CatalogVersion.STAGED.getVersion()));
	}

	@Test
	public void shouldGetCatalogVersion_NoPagesExist_HasOnlyPrimaryConditionForAllSupportedTypes()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getPageDisplayConditions().size(), greaterThanOrEqualTo(3));

		final Optional<DisplayConditionData> contentPageCondition = getDisplayCondition(entity, ContentPageModel._TYPECODE);
		assertTrue(contentPageCondition.isPresent());
		assertPrimaryCondition(contentPageCondition.get().getOptions().get(0));

		final Optional<DisplayConditionData> productPageCondition = getDisplayCondition(entity, ProductPageModel._TYPECODE);
		assertTrue(productPageCondition.isPresent());
		assertPrimaryCondition(productPageCondition.get().getOptions().get(0));

		final Optional<DisplayConditionData> categoryPageCondition = getDisplayCondition(entity, CategoryPageModel._TYPECODE);
		assertTrue(categoryPageCondition.isPresent());
		assertPrimaryCondition(categoryPageCondition.get().getOptions().get(0));
	}

	@Test
	public void shouldGetCatalogVersion_WithPrimaryContentPage_HasPrimaryAndVariationConditions()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		final CatalogVersionModel catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		contentPageModelMother.DefaultEmailPageFromHomePageTemplate(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getPageDisplayConditions().size(), greaterThanOrEqualTo(3));

		final Optional<DisplayConditionData> contentPageCondition = getDisplayCondition(entity, ContentPageModel._TYPECODE);
		assertTrue(contentPageCondition.isPresent());
		assertPrimaryCondition(contentPageCondition.get().getOptions().get(0));
		assertVariationCondition(contentPageCondition.get().getOptions().get(1));
	}

	@Test
	public void shouldGetCatalogVersion_WithPrimaryProductPage_HasOnlyVariationCondition()
	{
		siteModelMother.createElectronicsWithAppleStagedAndOnlineCatalog();
		final CatalogVersionModel catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		productPageModelMother.DefaultProductPage(catalogVersion);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final CatalogVersionData entity = response.readEntity(CatalogVersionData.class);
		assertThat(entity.getPageDisplayConditions().size(), greaterThanOrEqualTo(3));

		final Optional<DisplayConditionData> productPageCondition = getDisplayCondition(entity, ProductPageModel._TYPECODE);
		assertTrue(productPageCondition.isPresent());
		assertVariationCondition(productPageCondition.get().getOptions().get(0));
	}

	protected Optional<DisplayConditionData> getDisplayCondition(final CatalogVersionData catalogVersion, final String typecode)
	{
		return catalogVersion.getPageDisplayConditions().stream().filter(data -> data.getTypecode().equals(typecode)).findFirst();
	}

	protected void assertPrimaryCondition(final OptionData option)
	{
		assertThat(option.getLabel(), is(CmsfacadesConstants.PAGE_DISPLAY_CONDITION_PRIMARY));
		assertThat(option.getValue(), is(DisplayCondition.PRIMARY.name()));
	}

	protected void assertVariationCondition(final OptionData option)
	{
		assertThat(option.getLabel(), is(CmsfacadesConstants.PAGE_DISPLAY_CONDITION_VARIATION));
		assertThat(option.getValue(), is(DisplayCondition.VARIATION.name()));
	}
}
