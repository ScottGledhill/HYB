/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.impl.ClassificationSystemCPQAttributesProviderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.impl.SessionAccessServiceImpl;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CartConfigurationDisplayPopulatorTest
{
	CartConfigurationDisplayPopulator classUnderTest;
	private CsticModel csticModel;
	@Mock
	public SessionService sessionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CartConfigurationDisplayPopulator();
		csticModel = new CsticModelImpl();
		csticModel.setValueType(CsticModel.TYPE_STRING);
		final ClassificationSystemCPQAttributesProviderImpl nameProvider = Mockito
				.spy(new ClassificationSystemCPQAttributesProviderImpl());
		nameProvider.setUiTypeFinder(new UiTypeFinderImpl());
		nameProvider.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		classUnderTest.setNameProvider(nameProvider);

		final SessionAccessServiceImpl sessionAccessService = new SessionAccessServiceImpl();
		sessionAccessService.setSessionService(sessionService);
		given(sessionService.getAttribute(ClassificationSystemCPQAttributesContainer.class.getName())).willReturn(null);
		classUnderTest.setSessionAccessService(sessionAccessService);

		Mockito.doReturn(ClassificationSystemCPQAttributesContainer.NULL_OBJ).when(nameProvider)
				.getCPQAttributes(Mockito.anyString(), Mockito.anyMap());
	}

	@Test
	public void testConfigInfoInlineEmpty()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertEquals("'configInfoInline' not equal empty String: ", StringUtils.EMPTY, configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotEmpty()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertEquals("'configInfoInline' equal empty String: ", "Value 0", configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotContainsSemicolon()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertFalse("'configInfoInline' not contains ';': ", configInfoInline.toString().contains(classUnderTest.VALUE_SEPARATOR));
	}

	@Test
	public void testConfigInfoInlineContainsSemicolon()
	{
		final int numberOfAssignedValues = 4;
		final List<CsticValueModel> assignedValues = createAssignedValues(numberOfAssignedValues);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertTrue("'configInfoInline' not contains ';': ", configInfoInline.toString().contains(classUnderTest.VALUE_SEPARATOR));
		final int expectedNumberOfSemicolons = numberOfAssignedValues - 1;
		final int counter = countSemicolons(configInfoInline.toString());
		assertTrue("There should be " + expectedNumberOfSemicolons + "';' in 'configInfoInline': ",
				counter == expectedNumberOfSemicolons);
	}

	@Test
	public void test1Group3ValuesMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("3 ConfigurationInfoData entries should be returned", 3, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", configInfoDataList.get(1).getConfigurationLabel());
	}

	@Test
	public void test1Group4ValuesWithMultiValuedCsticMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValuesMultiValued();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("4 ConfigurationInfoData entries should be returned", 4, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", ConfigurationTestData.CHBOX_LIST_LD_NAME,
				configInfoDataList.get(1).getConfigurationLabel());
		assertEquals("Second entry's cstic's values: ", "VALUE 2; VALUE 3", configInfoDataList.get(1).getConfigurationValue());
	}

	@Test
	public void testNoAssignedValues()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModel();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("1 initial ConfigurationInfoData entry should be returned", 1, configInfoDataList.size());
		assertEquals("Configurator Type should be set to CPQCONFIGURATOR ", ConfiguratorType.CPQCONFIGURATOR,
				configInfoDataList.get(0).getConfiguratorType());
		assertNull("configuration label should be null: ", configInfoDataList.get(0).getConfigurationLabel());
	}

	@Test
	public void test1Group3ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("2 ConfigurationInfoData entries should be returned", 2, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", configInfoDataList.get(1).getConfigurationLabel());
		assertEquals("Second entry's cstic's value: ", "defaultValue", configInfoDataList.get(1).getConfigurationValue());
	}

	@Test
	public void test2Groups6ValuesMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith2GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("5 ConfigurationInfoData entries should be returned", 5, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", configInfoDataList.get(1).getConfigurationLabel());
		assertEquals("Fifth entry's cstic: ", "CSTIC 2.3", configInfoDataList.get(4).getConfigurationLabel());
		assertEquals("Fifth entry's cstic's value: ", "VALUE_2", configInfoDataList.get(4).getConfigurationValue());
	}

	@Test
	public void test2Groups6ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith2GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("2 ConfigurationInfoData entries should be returned", 2, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", configInfoDataList.get(1).getConfigurationLabel());
	}


	@Test
	public void test3Groups6ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith3GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoDataList);
		assertEquals("2 ConfigurationInfoData entries should be returned", 2, configInfoDataList.size());
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", configInfoDataList.get(1).getConfigurationLabel());
	}


	protected List<CsticValueModel> createAssignedValues(final int size)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			final CsticValueModel value = new CsticValueModelImpl();
			final String langDepName = "Value " + i;
			value.setLanguageDependentName(langDepName);
			assignedValues.add(value);
		}
		return assignedValues;
	}

	protected int countSemicolons(final String configInfoInline)
	{
		int counter = 0;
		for (int i = 0; i < configInfoInline.length(); i++)
		{
			if (configInfoInline.charAt(i) == classUnderTest.VALUE_SEPARATOR.toCharArray()[0])
			{
				counter++;
			}
		}
		return counter;
	}


}
