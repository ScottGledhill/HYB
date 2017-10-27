/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 *
 */
package de.hybris.platform.personalizationpromotionsweb.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationpromotions.constants.PersonalizationpromotionsConstants;
import de.hybris.platform.personalizationpromotionsweb.data.PromotionRuleListWsDTO;
import de.hybris.platform.personalizationpromotionsweb.data.PromotionRuleWsDTO;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class CxPromotionsQueryIntegrationTest extends BaseWebServiceTest
{
	private static final String PROMOTIONS_PATH = "v1/query/cxpromotionsforcatalog";

	@Resource
	ConfigurationService configurationService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importCsv("/personalizationpromotionsweb/test/querycontext_testdata.impex", "utf-8");
	}


	@Test
	public void getAllPromotionsForValidCatalog() throws IOException
	{
		//given
		modifyPromotions(true);
		final Params params = new Params().addParam("catalog", "testContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(4, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule2", "rule2", "description2", "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", "rule3", "description3", "MODIFIED");
		assertPromotionRule(promotionList, "rule4", "rule4", "description4", "ARCHIVED");
	}

	@Test
	public void getAllPromotionsForInvalidCatalog() throws IOException
	{
		//given
		modifyPromotions(true);
		final Params params = new Params().addParam("catalog", "testOtherContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertTrue((promotionList.getPromotions() == null)
				|| (promotionList.getPromotions() != null && promotionList.getPromotions().size() == 0));
	}

	@Test
	public void getPublishedPromotions() throws IOException
	{
		//given
		modifyPromotions(true);
		final Params params = new Params().addParam("catalog", "testContentCatalog").addParam("status", "PUBLISHED");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(2, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule2", "rule2", "description2", "PUBLISHED");
	}

	@Test
	public void getPublishedAndModifiedPromotions() throws IOException
	{
		//given
		modifyPromotions(true);
		final Params params = new Params().addParam("catalog", "testContentCatalog").addParam("status", "PUBLISHED,MODIFIED");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(3, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule2", "rule2", "description2", "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", "rule3", "description3", "MODIFIED");
	}

	@Test
	public void getAllPromotionsInGerman() throws IOException
	{
		//given
		modifyPromotions(true);
		final Params params = new Params().addParam("catalog", "testContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.header("Accept-Language", "de").post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(4, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "de-rule1", "de-description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule2", "de-rule2", null, "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", null, "de-description3", "MODIFIED");
		assertPromotionRule(promotionList, "rule4", null, null, "ARCHIVED");
	}

	@Test
	public void getCxOnlyPromotionsForValidCatalog() throws IOException
	{
		//given
		modifyPromotions(false);
		final Params params = new Params().addParam("catalog", "testContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(2, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", "rule3", "description3", "MODIFIED");
	}

	@Test
	public void getCxOnlyPromotionsForInvalidCatalog() throws IOException
	{
		//given
		modifyPromotions(false);
		final Params params = new Params().addParam("catalog", "testOtherContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertTrue((promotionList.getPromotions() == null)
				|| (promotionList.getPromotions() != null && promotionList.getPromotions().size() == 0));
	}

	@Test
	public void getCxOnlyPublishedPromotions() throws IOException
	{
		//given
		modifyPromotions(false);
		final Params params = new Params().addParam("catalog", "testContentCatalog").addParam("status", "PUBLISHED");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(1, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
	}

	@Test
	public void getCxOnlyPublishedAndModifiedPromotions() throws IOException
	{
		//given
		modifyPromotions(false);
		final Params params = new Params().addParam("catalog", "testContentCatalog").addParam("status", "PUBLISHED,MODIFIED");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(2, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "rule1", "description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", "rule3", "description3", "MODIFIED");
	}

	@Test
	public void getCxOnlyPromotionsInGerman() throws IOException
	{
		//given
		modifyPromotions(false);
		final Params params = new Params().addParam("catalog", "testContentCatalog");

		//when
		final Response response = getWsSecuredRequestBuilderForCmsManager()//
				.path(PROMOTIONS_PATH)//
				.build()//
				.header("Accept-Language", "de").post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);

		final PromotionRuleListWsDTO promotionList = response.readEntity(PromotionRuleListWsDTO.class);
		assertNotNull(promotionList.getPromotions());
		assertEquals(2, promotionList.getPromotions().size());

		assertPromotionRule(promotionList, "rule1", "de-rule1", "de-description1", "PUBLISHED");
		assertPromotionRule(promotionList, "rule3", null, "de-description3", "MODIFIED");
	}

	private void assertPromotionRule(final PromotionRuleListWsDTO promotionList, final String code, final String name,
			final String description, final String status)
	{
		final PromotionRuleWsDTO promotionRule = getPromotionRule(promotionList, code);

		Assert.assertNotNull("Not found ", promotionRule);
		Assert.assertEquals(code, promotionRule.getCode());
		Assert.assertEquals(name, promotionRule.getName());
		Assert.assertEquals(description, promotionRule.getDescription());
		Assert.assertEquals(status, promotionRule.getStatus());
	}

	private PromotionRuleWsDTO getPromotionRule(final PromotionRuleListWsDTO promotionList, final String code)
	{
		return promotionList.getPromotions().stream().filter(p -> p.getCode().equals(code)).findFirst().orElse(null);
	}

	private class Params
	{
		private final List<Entry> params = new ArrayList<>();

		public Params addParam(final String key, final String value)
		{
			final Entry entry = new Entry();
			entry.key = key;
			entry.value = value;

			params.add(entry);

			return this;
		}

		public List<Entry> getParams()
		{
			return params;
		}
	}

	//its used by marshallers
	@SuppressWarnings("unused")
	private class Entry
	{
		private String key;
		private String value;

		public void setKey(final String key)
		{
			this.key = key;
		}

		public String getKey()
		{
			return key;
		}

		public void setValue(final String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
	}

	private void modifyPromotions(final boolean modify)
	{
		configurationService.getConfiguration().setProperty(PersonalizationpromotionsConstants.MODIFY_PROMOTIONS,
				Boolean.toString(modify));
	}
}
