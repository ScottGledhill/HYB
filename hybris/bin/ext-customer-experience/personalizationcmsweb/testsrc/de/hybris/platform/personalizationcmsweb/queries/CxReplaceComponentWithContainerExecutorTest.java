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
package de.hybris.platform.personalizationcmsweb.queries;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.personalizationcmsweb.data.CxCmsComponentContainerData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;


@UnitTest
public class CxReplaceComponentWithContainerExecutorTest
{
	private static String COMPONENT_ID = "componentId";
	private static String SLOT_ID = "slotId";
	private static String CATALOG_ID = "catalogId";
	private static String CATALOG_VERSION_ID = "catalogVersionId";

	private final CxReplaceComponentWithContainerExecutor replaceComponentWithContainerExecutor = new CxReplaceComponentWithContainerExecutor();
	@Mock
	private SessionService sessionService;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSContentSlotService cmsContentSlotService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<CxCmsComponentContainerModel, CxCmsComponentContainerData> converter;
	@Mock
	private CxCmsComponentContainerModel container;
	@Mock
	private SimpleCMSComponentModel component;
	@Mock
	private ContentSlotModel slot;
	@Mock
	private CxCmsComponentContainerData returnData;


	@Before
	public void setup() throws CMSItemNotFoundException
	{
		MockitoAnnotations.initMocks(this);
		replaceComponentWithContainerExecutor.setCmsComponentService(cmsComponentService);
		replaceComponentWithContainerExecutor.setCatalogVersionService(catalogVersionService);
		replaceComponentWithContainerExecutor.setCmsContentSlotService(cmsContentSlotService);
		replaceComponentWithContainerExecutor.setModelService(modelService);
		replaceComponentWithContainerExecutor.setConverter(converter);

		Mockito.when(component.getUid()).thenReturn(COMPONENT_ID);
		Mockito.when(
				cmsComponentService.getAbstractCMSComponent(Mockito.eq(COMPONENT_ID), Mockito.eq(SLOT_ID), Mockito.anyCollection()))
				.thenReturn(component);
		Mockito.when(slot.getUid()).thenReturn(SLOT_ID);
		Mockito.when(cmsContentSlotService.getContentSlotForId(SLOT_ID)).thenReturn(slot);
		Mockito.when(modelService.create(CxCmsComponentContainerModel.class)).thenReturn(container);
		Mockito.when(converter.convert(container)).thenReturn(returnData);
	}

	@Test
	public void validateInputParamsTest()
	{
		//given
		final Map<String, String> params = new HashMap<String, String>();
		params.put(CxReplaceComponentWithContainerExecutor.OLD_ID, COMPONENT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.SLOT_ID, SLOT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG, CATALOG_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG_VERSION, CATALOG_VERSION_ID);
		final Errors errors = new MapBindingResult(params, "params");

		//when
		replaceComponentWithContainerExecutor.validateInputParams(params, errors);

		//then
		Assert.assertEquals(errors.getErrorCount(), 0);
	}

	@Test
	public void validateInputParamsWhenNoParamsTest()
	{
		//given
		final Map<String, String> params = new HashMap<String, String>();
		final Errors errors = new MapBindingResult(params, "params");

		//when
		replaceComponentWithContainerExecutor.validateInputParams(params, errors);

		//then
		Assert.assertEquals(errors.getErrorCount(), 4);
		assertFieldErrorsContains(errors.getFieldErrors(), CxReplaceComponentWithContainerExecutor.OLD_ID);
		assertFieldErrorsContains(errors.getFieldErrors(), CxReplaceComponentWithContainerExecutor.SLOT_ID);
		assertFieldErrorsContains(errors.getFieldErrors(), CxReplaceComponentWithContainerExecutor.CATALOG);
		assertFieldErrorsContains(errors.getFieldErrors(), CxReplaceComponentWithContainerExecutor.CATALOG_VERSION);
	}

	private void assertFieldErrorsContains(final List<FieldError> fieldErrors, final String fieldName)
	{
		Assert.assertTrue(fieldErrors.stream().anyMatch(e -> e.getField().equals(fieldName)));
	}

	@Test
	public void executeAfterValidationTest()
	{
		//given
		final Map<String, String> params = new HashMap<String, String>();
		params.put(CxReplaceComponentWithContainerExecutor.OLD_ID, COMPONENT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.SLOT_ID, SLOT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG, CATALOG_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG_VERSION, CATALOG_VERSION_ID);

		//when
		final Object result = replaceComponentWithContainerExecutor.executeAfterValidation(params);

		//then
		Assert.assertEquals(returnData, result);
	}

	@Test(expected = NotFoundException.class)
	public void executeAfterValidationWhenComponentNotExistTest() throws CMSItemNotFoundException
	{
		//given
		final Map<String, String> params = new HashMap<String, String>();
		params.put(CxReplaceComponentWithContainerExecutor.OLD_ID, COMPONENT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.SLOT_ID, SLOT_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG, CATALOG_ID);
		params.put(CxReplaceComponentWithContainerExecutor.CATALOG_VERSION, CATALOG_VERSION_ID);
		Mockito.when(
				cmsComponentService.getAbstractCMSComponent(Mockito.eq(COMPONENT_ID), Mockito.eq(SLOT_ID), Mockito.anyCollection()))
				.thenThrow(new CMSItemNotFoundException("Component not found"));

		//when
		replaceComponentWithContainerExecutor.executeAfterValidation(params);
	}
}
