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
package de.hybris.platform.cmsfacades.catalogversiondetails.populator.model;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.catalogversiondetails.populator.AbstractCatalogVersionModelPopulator;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.cmswebservices.data.CatalogVersionData;
import de.hybris.platform.cmswebservices.data.CatalogVersionDetailData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractCatalogVersionModelPopulatorTest
{
	private static final String SITE_UID = "test-site-id";
	private static final String CONTENT_CATALOG_ID = "test-content-catalog-id";
	private static final Boolean CATALOG_ACTIVE = true;
	private static final String CATALOG_VERSION = "test-version";
	private static final String SITE_NAME_EN = "site-name-EN";
	private static final String SITE_NAME_FR = "site-name-FR";
	private static final String CONTENT_CATALOG_NAME_EN = "content-catalog-name-EN";
	private static final String CONTENT_CATALOG_NAME_FR = "content-catalog-name-FR";
	private static final String REDIRECT_URL = "some-test-url";
	private static final String THUMBNAIL_URL = "some-thumbnail-url";
	private static final String EN = Locale.ENGLISH.getLanguage();
	private static final String FR = Locale.FRENCH.getLanguage();

	@Mock
	private CMSSiteModel siteModel;
	@Mock
	private ContentCatalogModel catalogModel;
	@Mock
	private CatalogVersionModel allowedVersionModel;
	@Mock
	private CatalogVersionModel notAllowedVersionModel;
	@Mock
	private SiteThumbnailResolver siteThumbnailResolver;
	@Mock
	private Comparator<CatalogVersionDetailData> catalogVersionDetailDataComparator;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	@Mock
	private UserModel currentUser;

	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;
	@InjectMocks
	private TestCatalogVersionModelPopulator populator = new TestCatalogVersionModelPopulator();

	private CatalogVersionData versionDto;

	@Before
	public void setup()
	{
		versionDto = new CatalogVersionData();

		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(catalogVersionService.getAllWritableCatalogVersions(currentUser)).thenReturn(Arrays.asList(allowedVersionModel));
		
		
		when(catalogModel.getCatalogVersions()).thenReturn(Arrays.asList(allowedVersionModel, notAllowedVersionModel).stream().collect(toSet()));

		when(siteModel.getUid()).thenReturn(SITE_UID);
		when(siteModel.getName(Locale.ENGLISH)).thenReturn(SITE_NAME_EN);
		when(siteModel.getName(Locale.FRENCH)).thenReturn(SITE_NAME_FR);
		when(siteModel.getRedirectURL()).thenReturn(REDIRECT_URL);
		when(catalogModel.getName(Locale.ENGLISH)).thenReturn(CONTENT_CATALOG_NAME_EN);
		when(catalogModel.getName(Locale.FRENCH)).thenReturn(CONTENT_CATALOG_NAME_FR);
		when(catalogModel.getId()).thenReturn(CONTENT_CATALOG_ID);
		when(allowedVersionModel.getVersion()).thenReturn(CATALOG_VERSION);
		when(allowedVersionModel.getActive()).thenReturn(CATALOG_ACTIVE);
		when(siteThumbnailResolver.resolveHomepageThumbnailUrl(any(CMSSiteModel.class))).thenReturn(Optional.of(THUMBNAIL_URL));

		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));

		populator.setCatalogVersionDetailDataComparator(catalogVersionDetailDataComparator);
		populator.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void shouldPopulateNonLocalizedAttributes() throws Exception
	{
		populator.populate(siteModel, versionDto);

		assertThat(versionDto.getUid(), equalTo(SITE_UID));
		assertThat(versionDto.getCatalogVersionDetails().size(), is(1));
		final CatalogVersionDetailData detailDto = versionDto.getCatalogVersionDetails().stream().findFirst().get();
		assertThat(detailDto.getRedirectUrl(), equalTo(REDIRECT_URL));
		assertThat(detailDto.getThumbnailUrl(), equalTo(THUMBNAIL_URL));
		assertThat(detailDto.getCatalogId(), equalTo(CONTENT_CATALOG_ID));
		assertThat(detailDto.getVersion(), equalTo(CATALOG_VERSION));
		assertThat(detailDto.getActive(), equalTo(CATALOG_ACTIVE));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_AllLanguages()
	{
		populator.populate(siteModel, versionDto);

		assertThat(versionDto.getName().get(EN), equalTo(SITE_NAME_EN));
		assertThat(versionDto.getName().get(FR), equalTo(SITE_NAME_FR));

		final CatalogVersionDetailData detailDto = versionDto.getCatalogVersionDetails().stream().findFirst().get();
		assertThat(detailDto.getName().get(EN), equalTo(CONTENT_CATALOG_NAME_EN));
		assertThat(detailDto.getName().get(FR), equalTo(CONTENT_CATALOG_NAME_FR));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_NullMaps()
	{
		versionDto.setName(null);
		versionDto.setCatalogVersionDetails(null);

		populator.populate(siteModel, versionDto);

		assertThat(versionDto.getName().get(EN), equalTo(SITE_NAME_EN));
		assertThat(versionDto.getName().get(FR), equalTo(SITE_NAME_FR));

		final CatalogVersionDetailData detailDto = versionDto.getCatalogVersionDetails().stream().findFirst().get();
		assertThat(detailDto.getName().get(EN), equalTo(CONTENT_CATALOG_NAME_EN));
		assertThat(detailDto.getName().get(FR), equalTo(CONTENT_CATALOG_NAME_FR));
	}

	@Test
	public void shouldPopulateLocalizedAttributes_SingleLanguages()
	{
		when(siteModel.getName(Locale.FRENCH)).thenReturn(null);
		when(catalogModel.getName(Locale.FRENCH)).thenReturn(null);

		populator.populate(siteModel, versionDto);

		assertThat(versionDto.getName().get(EN), equalTo(SITE_NAME_EN));
		assertThat(versionDto.getName().get(FR), nullValue());

		final CatalogVersionDetailData detailDto = versionDto.getCatalogVersionDetails().stream().findFirst().get();
		assertThat(detailDto.getName().get(EN), equalTo(CONTENT_CATALOG_NAME_EN));
		assertThat(detailDto.getName().get(FR), nullValue());
	}

	private class TestCatalogVersionModelPopulator extends AbstractCatalogVersionModelPopulator{

		@Override
		public List<CatalogModel> getCatalogs(CMSSiteModel source)
		{
			return Arrays.asList(catalogModel);
		}
		
		
	}
}
