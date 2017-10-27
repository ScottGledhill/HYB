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
package de.hybris.platform.cmsfacades.catalogversions.populator;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionModelPopulatorTest
{
	private static final String CATALOG_NAME_EN = "catalog-name-EN";
	private static final String CATALOG_NAME_FR = "catalog-name-FR";
	private static final String CATALOG_ID = "test-catalog-id";
	private static final String CATALOG_VERSION = "test-version";
	private static final Boolean CATALOG_ACTIVE = true;

	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private LanguageFacade languageFacade;

	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;
	@InjectMocks
	private CatalogVersionModelPopulator populator;

	private CatalogVersionData versionDto;

	@Before
	public void setup()
	{
		versionDto = new CatalogVersionData();

		when(catalogModel.getId()).thenReturn(CATALOG_ID);
		when(catalogModel.getName(ENGLISH)).thenReturn(CATALOG_NAME_EN);
		when(catalogModel.getName(FRENCH)).thenReturn(CATALOG_NAME_FR);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogVersionModel.getVersion()).thenReturn(CATALOG_VERSION);
		when(catalogVersionModel.getActive()).thenReturn(CATALOG_ACTIVE);

		final LanguageData languageEN = new LanguageData();
		final LanguageData languageFR = new LanguageData();
		languageEN.setIsocode(ENGLISH.getLanguage());
		languageFR.setIsocode(FRENCH.getLanguage());
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));

		populator.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void shouldPopulateNonLocalizedAttributes() throws Exception
	{
		populator.populate(catalogVersionModel, versionDto);

		assertThat(versionDto.getVersion(), equalTo(CATALOG_VERSION));
		assertThat(versionDto.getActive(), equalTo(CATALOG_ACTIVE));
		assertThat(versionDto.getUid(), equalTo(CATALOG_ID));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_AllLanguages()
	{
		populator.populate(catalogVersionModel, versionDto);

		assertThat(versionDto.getName().get(ENGLISH.getLanguage()), equalTo(CATALOG_NAME_EN));
		assertThat(versionDto.getName().get(FRENCH.getLanguage()), equalTo(CATALOG_NAME_FR));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_NullMaps()
	{
		when(catalogModel.getName(ENGLISH)).thenReturn(null);
		when(catalogModel.getName(FRENCH)).thenReturn(null);

		populator.populate(catalogVersionModel, versionDto);

		assertThat(versionDto.getName().get(ENGLISH.getLanguage()), nullValue());
		assertThat(versionDto.getName().get(FRENCH.getLanguage()), nullValue());
	}

	@Test
	public void shouldPopulateLocalizedAttributes_SingleLanguages()
	{
		when(catalogModel.getName(FRENCH)).thenReturn(null);

		populator.populate(catalogVersionModel, versionDto);

		assertThat(versionDto.getName().get(ENGLISH.getLanguage()), equalTo(CATALOG_NAME_EN));
		assertThat(versionDto.getName().get(FRENCH.getLanguage()), nullValue());
	}
}