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
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmsfacades.util.models.MediaModelMother.MediaTemplate.LOGO;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class MediaControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String GET_ALL_ENDPOINT = "/v1/media";

	private static final String CODE_PHONE_1 = "some.awesome.phone.from.Apple";
	private static final String CODE_PHONE_2 = "another-PHONE-from-Apple";
	private static final String CODE_PHONE_3 = "standard iPhone";
	private static final String CODE_PHONE_4 = "phone... by Apple";
	private static final String CODE_CAMERA_1 = "that-red-camera";
	private static final String CODE_CAMERA_2 = "that.phony/fake camera";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private MediaModelMother mediaModelMother;
	@Resource
	private ModelService modelService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setupFixtures()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		mediaModelMother.createLogoMediaModel(catalogVersion);
	}

	@Test
	public void shouldGetMediaByQuery() throws Exception
	{
		createMediaModels();

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:phone,catalogId:ID_APPLE,catalogVersion:staged") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia().size(), is(4));
	}

	@Test
	public void shouldFailGetMediaByQuery_ValidationErrors() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "") //
				.queryParam("params", "code:banner") //
				.queryParam("pageSize", "invalid") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:banner").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(3));
		assertThat(errors.getErrors().get(0).getSubject(), is("namedQuery"));
		assertThat(errors.getErrors().get(1).getSubject(), is("pageSize"));
		assertThat(errors.getErrors().get(2).getSubject(), is("sort"));
	}

	@Test
	public void shouldGetMediaByQueryWithEmptyResults_CodeNoMatch() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:invalid,catalogId:ID_APPLE,catalogVersion:staged") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia(), empty());
	}

	@Test
	public void shouldGetMediaByQueryWithEmptyResults_CatalogVersionNoMatch() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam("namedQuery", "namedQueryMediaSearchByCodeCatalogVersion") //
				.queryParam("params", "code:" + LOGO.getCode() + ",catalogId:apple,catalogVersion:invalid") //
				.queryParam("pageSize", "25") //
				.queryParam("currentPage", "0") //
				.queryParam("sort", "code:ASC").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final MediaListData entity = response.readEntity(MediaListData.class);
		assertThat(entity.getMedia(), empty());
	}

	protected void createMediaModels()
	{
		createMediaModel(CODE_PHONE_1);
		createMediaModel(CODE_PHONE_2);
		createMediaModel(CODE_PHONE_3);
		createMediaModel(CODE_PHONE_4);
		createMediaModel(CODE_CAMERA_1);
		createMediaModel(CODE_CAMERA_2);
	}

	protected void createMediaModel(final String code)
	{
		final MediaModel media = modelService.create(MediaModel.class);
		media.setCode(code);
		media.setCatalogVersion(catalogVersion);
		modelService.save(media);
	}

}
