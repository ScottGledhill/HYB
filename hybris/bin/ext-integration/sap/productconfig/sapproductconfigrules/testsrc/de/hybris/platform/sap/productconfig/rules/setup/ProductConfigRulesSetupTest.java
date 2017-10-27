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
package de.hybris.platform.sap.productconfig.rules.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.nio.file.Path;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRulesSetupTest
{
	private ProductConfigRulesSetup classUnderTest;

	@Mock
	private SystemSetupContext context;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRulesSetup();

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetListOfLanguageFiles() throws Exception
	{
		final List<Path> localeFiles = classUnderTest.getListOfLanguageFiles(context);

		assertNotNull(localeFiles);
		assertTrue(localeFiles.size() > 0);

		assertTrue(localeFiles.parallelStream()
				.anyMatch(path -> path.toString().endsWith("sapproductconfigrules-impexsupport_xx.properties")));
	}

	@Test
	public void testExtractLocaleOutOfFileName()
	{
		String fileName = "/sapproductconfigrules/resources/localization/sapproductconfigrules-impexsupport_en.properties";

		String locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("en", locale);

		fileName = "sapproductconfigrules-impexsupport_EN_en.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("EN_en", locale);

		fileName = "sapproductconfigrules-impexsupport_de.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertEquals("de", locale);

		fileName = "sapproductconfigrules-impexsupport.properties";
		locale = classUnderTest.extractLocaleOutOfFileName(fileName);
		assertNull(locale);
	}
}
