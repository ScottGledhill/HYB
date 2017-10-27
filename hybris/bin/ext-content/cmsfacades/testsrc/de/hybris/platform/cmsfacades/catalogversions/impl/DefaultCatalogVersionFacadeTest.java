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
package de.hybris.platform.cmsfacades.catalogversions.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.DisplayConditionData;
import de.hybris.platform.cmswebservices.data.OptionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCatalogVersionFacadeTest
{
	private static final String TYPECODE = "test-typecode";
	private static final String CATALOG_ID = "test-catalog-id";
	private static final String VERSION_ID = "test-version-id";

	@InjectMocks
	@Spy
	private final DefaultCatalogVersionFacade catalogVersionFacade = new DefaultCatalogVersionFacade();

	@Mock
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionConverter;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private DisplayConditionData displayConditionData1;
	@Mock
	private PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry;
	@Mock
	private PageVariationResolverType resolverType;
	@Mock
	private PageVariationResolver<AbstractPageModel> resolver;
	@Mock
	private OptionData optionData1;
	@Mock
	private OptionData optionData2;
	@Mock
	private CatalogVersionModel catalogVersionModel;

	private Set<Class<?>> cmsSupportedPages;
	private CatalogVersionData catalogVersionData;

	@Before
	public void setUp()
	{
		cmsSupportedPages = new HashSet<>();
		cmsSupportedPages.add(ContentPageModel.class);
		cmsSupportedPages.add(ProductPageModel.class);

		catalogVersionData = new CatalogVersionData();
		when(catalogVersionConverter.convert(catalogVersionModel)).thenReturn(catalogVersionData);
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ID)).thenReturn(catalogVersionModel);

		catalogVersionFacade.setCmsSupportedPages(cmsSupportedPages);
	}

	@Test
	public void shouldGetCatalogVersion() throws CMSItemNotFoundException
	{
		doReturn(Arrays.asList(optionData1, optionData2)).when(catalogVersionFacade)
				.getDisplayCondition(ContentPageModel._TYPECODE);
		doReturn(Collections.emptyList()).when(catalogVersionFacade).getDisplayCondition(ProductPageModel._TYPECODE);

		catalogVersionData = catalogVersionFacade.getCatalogVersion(CATALOG_ID, VERSION_ID);

		assertThat(catalogVersionData.getPageDisplayConditions(), iterableWithSize(2));

		final Optional<DisplayConditionData> contentPageCondition = catalogVersionData.getPageDisplayConditions().stream()
				.filter(data -> data.getTypecode().equals(ContentPageModel._TYPECODE)).findFirst();
		assertTrue(contentPageCondition.isPresent());
		assertThat(contentPageCondition.get().getOptions(), contains(optionData1, optionData2));

		final Optional<DisplayConditionData> productPageCondition = catalogVersionData.getPageDisplayConditions().stream()
				.filter(data -> data.getTypecode().equals(ProductPageModel._TYPECODE)).findFirst();
		assertTrue(productPageCondition.isPresent());
		assertThat(productPageCondition.get().getOptions(), empty());
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailGetCatalogVersion_CatalogVersionNotFound() throws CMSItemNotFoundException
	{
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ID)).thenReturn(null);
		catalogVersionFacade.getCatalogVersion(CATALOG_ID, VERSION_ID);
	}

	@Test
	public void shouldGetCatalogVersion_NoSupportedPageTypes() throws CMSItemNotFoundException
	{
		cmsSupportedPages.clear();

		catalogVersionData = catalogVersionFacade.getCatalogVersion(CATALOG_ID, VERSION_ID);

		assertThat(catalogVersionData.getPageDisplayConditions(), empty());
	}

	@Test
	public void shouldGetDisplayCondition()
	{
		when(cmsPageVariationResolverTypeRegistry.getPageVariationResolverType(TYPECODE))
				.thenReturn(Optional.<PageVariationResolverType> of(resolverType));
		when(resolverType.getResolver()).thenReturn(resolver);
		when(resolver.findDisplayConditions(TYPECODE)).thenReturn(Arrays.asList(optionData1, optionData2));

		final List<OptionData> options = catalogVersionFacade.getDisplayCondition(TYPECODE);

		assertThat(options, iterableWithSize(2));
		assertThat(options, contains(optionData1, optionData2));
	}

}
