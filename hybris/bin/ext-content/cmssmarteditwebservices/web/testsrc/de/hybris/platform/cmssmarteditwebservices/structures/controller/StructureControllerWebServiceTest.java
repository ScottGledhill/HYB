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
package de.hybris.platform.cmssmarteditwebservices.structures.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorcms.model.components.SimpleBannerComponentModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureAttributeWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureWsDTO;
import de.hybris.platform.cmssmarteditwebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmssmarteditwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class StructureControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String ACTIVE_FROM = "activeFrom";
	private static final String ACTIVE_UNTIL = "activeUntil";

	private static final String MODE = "mode";
	private static final String URI = "/v1/structures";

	@Test
	public void shouldFindTimeRestrictionStructureWithAllModes()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(URI) //
				.path(CMSTimeRestrictionModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final StructureListWsDTO entityList = response.readEntity(StructureListWsDTO.class);
		assertThat(entityList.getStructures(), hasSize(2));

		final StructureWsDTO defaultStructure = findStructureByMode(entityList, StructureTypeMode.DEFAULT.name());
		assertThat(defaultStructure.getAttributes(), hasSize(3));
		final StructureWsDTO addStructure = findStructureByMode(entityList, StructureTypeMode.ADD.name());
		assertThat(addStructure.getAttributes(), hasSize(3));
	}

	@Test
	public void shouldSimpleBannerComponentStructureWithDefaultMode()
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(URI) //
				.path(SimpleBannerComponentModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final StructureListWsDTO entityList = response.readEntity(StructureListWsDTO.class);
		assertThat(entityList.getStructures(), hasSize(1));

		final StructureWsDTO entity = findStructureByMode(entityList, StructureTypeMode.DEFAULT.name());
		assertThat(entity.getAttributes(), hasSize(3));
	}

	protected StructureWsDTO findStructureByMode(final StructureListWsDTO dtos, final String mode)
	{
		return dtos.getStructures().stream().filter(structure -> structure.getAttributes().stream()
				.filter(attribute -> attribute.getMode().equals(mode)).findFirst().isPresent()).findFirst().get();
	}

	@Test
	public void shouldFindTimeRestrictionStructureForAddModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(URI) //
				.path(CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.ADD.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final StructureListWsDTO entityList = response.readEntity(StructureListWsDTO.class);
		assertNotNull(entityList);

		final StructureWsDTO entity = entityList.getStructures().get(0);
		assertThat(entity.getAttributes(), hasSize(3));

		final List<StructureAttributeWsDTO> attributes = entity.getAttributes();

		final StructureAttributeWsDTO name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(false));
		assertThat(name.getMode(), equalTo(StructureTypeMode.ADD.name()));

		final StructureAttributeWsDTO activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(false));
		assertThat(activeFrom.getMode(), equalTo(StructureTypeMode.ADD.name()));

		final StructureAttributeWsDTO activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(false));
		assertThat(activeUntil.getMode(), equalTo(StructureTypeMode.ADD.name()));
	}

	@Test
	public void shouldFindTimeRestrictionStructureForDefaultModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(URI) //
				.path(CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.DEFAULT.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final StructureListWsDTO entityList = response.readEntity(StructureListWsDTO.class);
		assertNotNull(entityList);

		final StructureWsDTO entity = entityList.getStructures().get(0);
		assertTrue(entity.getAttributes().size() > 1);

		final List<StructureAttributeWsDTO> attributes = entity.getAttributes();

		final StructureAttributeWsDTO name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(true));
		assertThat(name.getMode(), equalTo(StructureTypeMode.DEFAULT.name()));

		final StructureAttributeWsDTO activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(true));
		assertThat(activeFrom.getMode(), equalTo(StructureTypeMode.DEFAULT.name()));

		final StructureAttributeWsDTO activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(true));
		assertThat(activeUntil.getMode(), equalTo(StructureTypeMode.DEFAULT.name()));
	}

	@Test
	public void shouldFindTimeRestrictionStructureForCreateModeAndAttributesAreOrdered() throws Exception
	{
		final Response response = getWsSecuredRequestBuilder() //
				.path(URI) //
				.path(CMSTimeRestrictionModel._TYPECODE) //
				.queryParam(MODE, StructureTypeMode.CREATE.name()).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final StructureListWsDTO entityList = response.readEntity(StructureListWsDTO.class);
		assertNotNull(entityList);

		final StructureWsDTO entity = entityList.getStructures().get(0);
		assertTrue(entity.getAttributes().size() > 1);

		final List<StructureAttributeWsDTO> attributes = entity.getAttributes();

		final StructureAttributeWsDTO name = attributes.get(0);
		assertThat(name.getQualifier(), equalTo(NAME));
		assertThat(name.isEditable(), is(true));
		assertThat(name.getMode(), equalTo(StructureTypeMode.CREATE.name()));

		final StructureAttributeWsDTO activeFrom = attributes.get(1);
		assertThat(activeFrom.getQualifier(), equalTo(ACTIVE_FROM));
		assertThat(activeFrom.isEditable(), is(true));
		assertThat(activeFrom.getMode(), equalTo(StructureTypeMode.CREATE.name()));

		final StructureAttributeWsDTO activeUntil = attributes.get(2);
		assertThat(activeUntil.getQualifier(), equalTo(ACTIVE_UNTIL));
		assertThat(activeUntil.isEditable(), is(true));
		assertThat(activeUntil.getMode(), equalTo(StructureTypeMode.CREATE.name()));
	}

}
