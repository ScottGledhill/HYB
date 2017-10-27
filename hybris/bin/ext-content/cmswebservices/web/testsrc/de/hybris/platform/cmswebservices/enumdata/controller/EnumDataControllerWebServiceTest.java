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
package de.hybris.platform.cmswebservices.enumdata.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.EnumListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


/**
 * Test for <code>EnumDataController</code>.
 * 
 * @deprecated since version 6.2
 */
@Deprecated
@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class EnumDataControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENUM_CLASS = "enumClass";
	private static final String GET_ALL_ENDPOINT = "/v1/enums";
	private static final String AB_TEST_SCOPE_CLASS = "de.hybris.platform.cms2.enums.ABTestScopes";

	@Test
	public void getEnumerationValues() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(GET_ALL_ENDPOINT) //
				.queryParam(ENUM_CLASS, AB_TEST_SCOPE_CLASS).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final EnumListData entity = response.readEntity(EnumListData.class);
		assertNotNull("Expected values back for call to get enums but was null", entity);
	}

}
