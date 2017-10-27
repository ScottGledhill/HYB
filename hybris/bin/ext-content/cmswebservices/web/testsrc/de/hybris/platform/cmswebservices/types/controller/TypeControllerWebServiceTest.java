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
package de.hybris.platform.cmswebservices.types.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.ComponentTypeAttributeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.data.OptionData;
import de.hybris.platform.cmswebservices.data.StructureTypeCategory;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class TypeControllerWebServiceTest extends ApiBaseIntegrationTest
{

	private static final String WIDESCREEN = "widescreen";
	private static final String DESKTOP = "desktop";
	private static final String TABLET = "tablet";
	private static final String MOBILE = "mobile";

	private static final String URI = "/v1/types";

	@Resource
	private ModelService modelService;

	@Before
	public void setupMediaFormats()
	{
		createMediaFormat(MOBILE);
		createMediaFormat(TABLET);
		createMediaFormat(DESKTOP);
		createMediaFormat(WIDESCREEN);
	}

	@Test
	public void getAllTypesTest() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//check that we have a result
		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		// check that we have a body
		assertNotNull(entity);

		//check that we have a couple of entries
		assertTrue(entity.getComponentTypes().size() > 1);

		final List<ComponentTypeData> components = entity.getComponentTypes();
		ComponentTypeData paragraphComponent = new ComponentTypeData();

		for (final ComponentTypeData component : components)
		{
			if(component.getCode().equals("CMSParagraphComponent"))
			{
				paragraphComponent = component;
			}
		}

		// check that it contains the paragraph component
		assertNotNull(paragraphComponent);
		assertThat(paragraphComponent.getType(), is("cmsParagraphComponentData"));
	}

	@Test
	public void getAllTypesByCategoryTest() throws Exception
	{
		final String componentCategory = StructureTypeCategory.COMPONENT.name();
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI) //
				.queryParam("category", componentCategory) //
				.build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		//check that we have a result
		assertResponse(Status.OK, response);

		final ComponentTypeListData entity = response.readEntity(ComponentTypeListData.class);
		// check that we have a body
		assertNotNull(entity);

		//check that we have a couple of entries
		assertTrue(entity.getComponentTypes().size() > 1);

		final List<ComponentTypeData> components = entity.getComponentTypes();

		components.stream().forEach(componentTypeData -> assertThat(componentTypeData.getCategory(), is(componentCategory)));
	}


	@Test
	public void shouldGetSimpleResponsiveBannerComponentWithMediaFormats() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(URI).path(SimpleResponsiveBannerComponentModel._TYPECODE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final ComponentTypeData component = response.readEntity(ComponentTypeData.class);
		assertThat(component.getCode(), equalTo(SimpleResponsiveBannerComponentModel._TYPECODE));
		assertThat(component.getI18nKey(), equalTo("type.simpleresponsivebannercomponent.name"));

		final ComponentTypeAttributeData urlLink = getAttribute(component.getAttributes(), "urlLink");
		assertThat(urlLink.getCmsStructureType(), equalTo("ShortString"));
		assertThat(urlLink.getQualifier(), equalTo("urlLink"));
		assertThat(urlLink.getLocalized(), equalTo(Boolean.FALSE));
		assertThat(urlLink.getOptions(), nullValue());
		assertThat(urlLink.getI18nKey(), equalTo("type.simpleresponsivebannercomponent.urllink.name"));
		assertThat(urlLink.getCmsStructureEnumType(), nullValue());

		final ComponentTypeAttributeData media = getAttribute(component.getAttributes(), "media");
		assertThat(media.getCmsStructureType(), equalTo("MediaContainer"));
		assertThat(media.getQualifier(), equalTo("media"));
		assertThat(media.getLocalized(), equalTo(Boolean.TRUE));
		assertThat(media.getI18nKey(), equalTo("type.simpleresponsivebannercomponent.media.name"));
		assertThat(media.getCmsStructureEnumType(), nullValue());

		final OptionData mobile = getOption(media.getOptions(), MOBILE);
		assertThat(mobile.getValue(), equalTo(MOBILE));
		assertThat(mobile.getLabel(), equalTo("cms.media.format.mobile"));
		final OptionData tablet = getOption(media.getOptions(), TABLET);
		assertThat(tablet.getValue(), equalTo(TABLET));
		assertThat(tablet.getLabel(), equalTo("cms.media.format.tablet"));
		final OptionData desktop = getOption(media.getOptions(), DESKTOP);
		assertThat(desktop.getValue(), equalTo(DESKTOP));
		assertThat(desktop.getLabel(), equalTo("cms.media.format.desktop"));
		final OptionData widescreen = getOption(media.getOptions(), WIDESCREEN);
		assertThat(widescreen.getValue(), equalTo(WIDESCREEN));
		assertThat(widescreen.getLabel(), equalTo("cms.media.format.widescreen"));
	}

	protected void createMediaFormat(final String qualifier)
	{
		final MediaFormatModel mediaFormat = modelService.create(MediaFormatModel.class);
		mediaFormat.setQualifier(qualifier);
		modelService.save(mediaFormat);
	}

	protected ComponentTypeAttributeData getAttribute(final List<ComponentTypeAttributeData> attributes, final String qualifier)
	{
		return attributes.stream() //
				.filter(attribute -> qualifier.equals(attribute.getQualifier())) //
				.findFirst() //
				.orElseThrow(
						() -> new IllegalArgumentException("No attribute with qualifier [" + qualifier + "] in list of attributes."));
	}

	protected OptionData getOption(final List<OptionData> options, final String value)
	{
		return options.stream() //
				.filter(option -> value.equals(option.getValue())) //
				.findFirst() //
				.orElseThrow(() -> new IllegalArgumentException("No option with value [" + value + "] in list of options."));
	}
}
