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
package de.hybris.platform.cmswebservices.synchronization.controller;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.SyncJobData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

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
public class CatalogVersionSynchronizationControllerGETWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String INVALID = "INVALID";
	private static final String TARGET_VERSION = "targetVersionId";
	private static final String SOURCE_VERSION = "sourceVersionId";
	private static final String CATALOG = "catalogId";
	private static final String ENDPOINT = "/v1/catalogs/{catalogId}/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	private CatalogVersionModel stagedCatalogVersionModel;
	private CatalogVersionModel onlineCatalogVersionModel;

	@Before
	public void setup()
	{
		stagedCatalogVersionModel = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		onlineCatalogVersionModel = catalogVersionModelMother.createAppleOnlineCatalogVersionModel();
	}

	@Test
	public void shouldGetSyncronizationStatus() throws Exception
	{
		catalogVersionModelMother.performCatalogSyncronization(stagedCatalogVersionModel, onlineCatalogVersionModel);

		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(CATALOG, ContentCatalogModelMother.CatalogTemplate.ID_APPLE.name());
		variables.put(SOURCE_VERSION, STAGED.getVersion());
		variables.put(TARGET_VERSION, ONLINE.getVersion());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SyncJobData entity = response.readEntity(SyncJobData.class);
		assertNotNull(entity.getSyncStatus());
		assertNotNull(entity.getLastModifiedDate());
		assertNotNull(entity.getSyncResult());
		assertNotNull(entity.getCreationDate());
		assertNotNull(entity.getEndDate());
		assertNotNull(entity.getStartDate());
	}

	@Test
	public void shouldStatusBeNull() throws Exception
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(CATALOG, ID_APPLE.name());
		variables.put(SOURCE_VERSION, STAGED.getVersion());
		variables.put(TARGET_VERSION, ONLINE.getVersion());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final SyncJobData entity = response.readEntity(SyncJobData.class);
		assertNull(entity.getSyncStatus());
		assertNull(entity.getLastModifiedDate());
		assertNull(entity.getSyncResult());
		assertNull(entity.getCreationDate());
		assertNull(entity.getEndDate());
		assertNull(entity.getStartDate());
	}

	@Test
	public void shouldReturnErrorValidation_catalogDoesNotExist() throws Exception
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(CATALOG, INVALID);
		variables.put(SOURCE_VERSION, STAGED.getVersion());
		variables.put(TARGET_VERSION, ONLINE.getVersion());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldReturnErrorValidation_sourceDoesNotExist() throws Exception
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(CATALOG, ID_APPLE.name());
		variables.put(SOURCE_VERSION, INVALID);
		variables.put(TARGET_VERSION, ONLINE.getVersion());

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

	@Test
	public void shouldReturnErrorValidation_targetDoesNotExist() throws Exception
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(CATALOG, ID_APPLE.name());
		variables.put(SOURCE_VERSION, STAGED.getVersion());
		variables.put(TARGET_VERSION, INVALID);

		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(ENDPOINT, variables)).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.BAD_REQUEST, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors().size(), is(1));
	}

}
