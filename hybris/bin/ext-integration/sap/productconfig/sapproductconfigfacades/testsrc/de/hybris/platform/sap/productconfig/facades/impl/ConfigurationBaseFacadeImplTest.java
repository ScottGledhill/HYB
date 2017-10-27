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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageImpl;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationBaseFacadeImplTest
{
	private static final String NAME = "A";
	private static final String DESCRIPTION = "B";
	private static final String PRODUCT_CODE = "product_123";

	private final ConfigurationBaseFacadeImpl classUnderTest = new ConfigurationBaseFacadeImpl();
	private UiGroupData uiGroup;
	private List<UiGroupData> subGroups;
	private boolean oneSubGroupConfigurable;
	private final UiGroupData subGroup = new UiGroupData();

	@Mock
	private ProductService productServiceMock;

	@Mock
	private ProductModel productModelMock;

	@Mock
	private ConfigurationVariantUtilImpl configurationVariantUtil;
	private ConfigModel configModel;
	private ConfigurationData configData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);
		classUnderTest.setUiKeyGenerator(new UniqueUIKeyGeneratorImpl());

		given(productServiceMock.getProductForCode(PRODUCT_CODE)).willReturn(productModelMock);

		configModel = new ConfigModelImpl();
		configData = new ConfigurationData();
	}

	@Test
	public void testOneGroupConfigurableFalse()
	{
		oneSubGroupConfigurable = false;
		classUnderTest.checkAdoptSubGroup(uiGroup, subGroups, oneSubGroupConfigurable);
		assertNull(subGroup.getName());
		assertNull(subGroup.getDescription());
	}

	@Test
	public void testOneGroupConfigurableTrue()
	{
		oneSubGroupConfigurable = true;
		classUnderTest.checkAdoptSubGroup(uiGroup, subGroups, oneSubGroupConfigurable);
		assertNotNull(subGroup.getName());
		assertNotNull(subGroup.getDescription());
	}


	@Before
	public void createTestData()
	{
		uiGroup = new UiGroupData();
		subGroups = new ArrayList<UiGroupData>();
		uiGroup.setName(NAME);
		uiGroup.setDescription(DESCRIPTION);
		uiGroup.setSubGroups(subGroups);
		subGroups.add(subGroup);
	}

	@Test
	public void testShowVariants_disabled()
	{
		classUnderTest.setOfferVariantSearch(false);
		assertFalse("Do not show the variants, as varaint search is disbaled", classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledNoVariantsExistingNull()
	{
		classUnderTest.setOfferVariantSearch(true);
		assertFalse("Do not show the variants search, as no variants exist for thze given product",
				classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledNoVariantsEmptyList()
	{
		classUnderTest.setOfferVariantSearch(true);
		given(productModelMock.getVariants()).willReturn(Collections.emptyList());
		assertFalse("Do not show the variants search, as no variants exist for thze given product",
				classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledVariantsFound()
	{
		classUnderTest.setOfferVariantSearch(true);
		given(productModelMock.getVariants()).willReturn(Collections.singletonList(new VariantProductModel()));
		given(Boolean.valueOf(configurationVariantUtil.isCPQBaseProduct(productModelMock))).willReturn(Boolean.TRUE);
		assertTrue("At least one varaint exists, so show the variant search", classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testMapMessagesFromModelToDataEmpty()
	{
		classUnderTest.mapMessagesFromModelToData(configData, configModel);
		assertTrue(configData.getMessages().isEmpty());
	}

	@Test
	public void testMapMessagesFromModelToData_Info()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a test message", "messagekey123",
				ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a test message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToData_Warning()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a test message", "messagekey123",
				ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.ENGINE);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a test message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.INFO, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToData_Error()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a test message", "messagekey123",
				ProductConfigMessageSeverity.ERROR, ProductConfigMessageSource.ENGINE);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a test message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.ERROR, configData.getMessages().get(0).getSeverity());
	}
}
