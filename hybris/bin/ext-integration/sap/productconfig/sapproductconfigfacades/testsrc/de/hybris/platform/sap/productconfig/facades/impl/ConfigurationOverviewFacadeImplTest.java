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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOverviewPopulator;
import de.hybris.platform.sap.productconfig.facades.populator.VariantOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationOverviewFacadeImplTest
{
	public ConfigurationOverviewFacadeImpl classUnderTest;

	private static final String CONFIG_ID = "config_id";
	private static final String PRODUCT_CODE = "4711";

	@Mock
	private ConfigurationOverviewPopulator configurationOverviewPopulator;

	@Mock
	private VariantOverviewPopulator variantOverviewPopulator;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private ProductService productService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationOverviewFacadeImpl();
		classUnderTest.setConfigurationOverviewPopulator(configurationOverviewPopulator);
		classUnderTest.setVariantOverviewPopulator(variantOverviewPopulator);
		classUnderTest.setConfigurationService(productConfigurationService);
		classUnderTest.setProductService(productService);
		Mockito.when(productConfigurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(new ConfigModelImpl());
		Mockito.when(productService.getProduct(PRODUCT_CODE)).thenReturn(new ProductModel());
	}

	@Test
	public void testGetOverviewForConfigurationNull()
	{
		ConfigurationOverviewData configOverviewData = null;
		configOverviewData = classUnderTest.getOverviewForConfiguration(CONFIG_ID, configOverviewData);
		assertNotNull(configOverviewData);
	}

	@Test
	public void testGetVaraintForProductVariantNull()
	{
		ConfigurationOverviewData configOverviewData = null;
		configOverviewData = classUnderTest.getOverviewForProductVariant(PRODUCT_CODE, configOverviewData);
		assertNotNull(configOverviewData);
	}
}