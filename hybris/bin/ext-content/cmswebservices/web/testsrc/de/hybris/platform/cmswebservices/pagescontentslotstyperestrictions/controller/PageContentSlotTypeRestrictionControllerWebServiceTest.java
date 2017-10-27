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
package de.hybris.platform.cmswebservices.pagescontentslotstyperestrictions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.ContentSlotTypeRestrictionsWsDTO;
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

import com.google.common.collect.Maps;


/**
 * Integration test for {@link PageContentSlotTypeRestrictionController}
 */
@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageContentSlotTypeRestrictionControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String SLOT_ID = "slotId";

	private static final String PAGE_UID = "pageUid";

	private static final String ENDPOINT = "/v1/catalogs/{catalogId}/versions/{versionId}/pages/{pageUid}/contentslots/{slotId}/typerestrictions";

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private PageTemplateModelMother pageTemplateModelMother;

	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;

	@Resource
	private ContentSlotNameModelMother contentSlotNameModelMother;

	@Resource
	private ParagraphComponentModelMother paragraphComponentModelMother;

	@Test
	public void shouldGetRestrictions() throws Exception
	{
		final HashMap<String, String> variables = Maps.newHashMap();
		variables.put(PAGE_UID, ContentPageModelMother.UID_HOMEPAGE);
		variables.put(SLOT_ID, ContentSlotModelMother.UID_LOGO);

		final String enpoint = replaceUriVariablesWithDefaults(ENDPOINT, variables);
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(enpoint).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ContentSlotTypeRestrictionsWsDTO entity = response.readEntity(ContentSlotTypeRestrictionsWsDTO.class);
		assertThat(entity.getValidComponentTypes(), not(empty()));
	}

	@Before
	public void setup()
	{
		final CatalogVersionModel catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		final PageTemplateModel pageTemplate = pageTemplateModelMother.HomePage_Template(catalogVersion);
		contentSlotForPageModelMother.LogoHomepage(catalogVersion);
		contentSlotNameModelMother.Logo(pageTemplate);
	}
}
