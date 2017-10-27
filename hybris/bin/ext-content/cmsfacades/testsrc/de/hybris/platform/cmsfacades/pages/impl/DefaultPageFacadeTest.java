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
package de.hybris.platform.cmsfacades.pages.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.validator.CompositeValidator;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.exception.ValidationError;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pages.service.PageInitializer;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageTypeData;
import de.hybris.platform.cmswebservices.dto.UpdatePageValidationDto;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageFacadeTest
{
	private static final String PAGE_UID = "page-uid";
	private static final String UNIQUE_KEY = "uniqueKey";
	private static final String DEFAULT_PAGE_UID = "default-page-uid";
	private static final String INVALID = "invalid";

	@InjectMocks
	private DefaultPageFacade pageFacade;
	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private PageInitializer pageInitializer;
	@Mock
	private ModelService modelService;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private Map<Class<?>, CompositeValidator> cmsCreatePageValidatorFactory;
	@Mock
	private Map<Class<?>, CompositeValidator> cmsUpdatePageValidatorFactory;
	@Mock
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory;
	@Mock
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory;
	@Mock
	private Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ContentPageModel contentPageModel;
	@Mock
	private ProductPageModel productPageModel;
	@Mock
	private EmailPageModel emailPageModel;
	@Mock
	private PageVariationResolverTypeRegistry registry;
	@Mock
	private PageVariationResolverType resolverType;
	@Mock
	private PageVariationResolver<AbstractPageModel> pageVariationResolver;
	@Mock
	private Comparator<AbstractPageData> cmsPageComparator;

	private Collection<AbstractPageModel> pages;

	@Mock
	private AbstractPageData pageData1;
	@Mock
	private AbstractPageData pageData2;

	@Before
	public void setup()
	{
		when(keyGenerator.generate()).thenReturn(UNIQUE_KEY);
		pages = asList(contentPageModel, productPageModel, emailPageModel);

		final Set<Class<?>> supportedPages = new HashSet<>();
		supportedPages.add(contentPageModel.getClass());
		supportedPages.add(productPageModel.getClass());
		pageFacade.setCmsSupportedPages(supportedPages);
	}

	@Test
	public void findAllPagesReturnTransformedCollectionOfAllSupportedPagesOrderByNameAscending()
	{
		when(pageData1.getName()).thenReturn("Bname");
		when(pageData2.getName()).thenReturn("Aname");

		when(adminPageService.getAllPages()).thenReturn(pages);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);
		when(convertModel.convert(productPageModel)).thenReturn(pageData2);
		when(cmsPageComparator.compare(pageData2, pageData1)).thenReturn(-20);
		doReturn(convertModel).when(pageModelConverterFactory).get(contentPageModel.getClass());
		doReturn(convertModel).when(pageModelConverterFactory).get(productPageModel.getClass());
		when(pageModelConverterFactory.containsKey(any())).thenReturn(Boolean.TRUE).thenReturn(Boolean.TRUE);

		final List<AbstractPageData> results = pageFacade.findAllPages();
		assertThat(results.size(), is(equalTo(2)));
		assertThat(results, contains(pageData2, pageData1));
	}

	@Test
	public void findPagesReturnsEmptyList()
	{
		when(adminPageService.getAllPages()).thenReturn(Collections.<AbstractPageModel> emptyList());

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);
		when(convertModel.convert(productPageModel)).thenReturn(pageData2);
		doReturn(convertModel).when(pageModelConverterFactory).get(contentPageModel.getClass());

		final List<AbstractPageData> results = pageFacade.findAllPages();
		assertThat(results.size(), is(0));
		verify(convertModel, never()).convert(any(AbstractPageModel.class));
	}

	@Test
	public void shouldGetPageByUid() throws CMSItemNotFoundException
	{
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);
		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);
		doReturn(convertModel).when(pageModelConverterFactory).get(contentPageModel.getClass());
		final AbstractPageData pageByUid = pageFacade.getPageByUid(PAGE_UID);
		verify(adminPageService).getPageForIdFromActiveCatalogVersion(PAGE_UID);
		verify(convertModel).convert(contentPageModel);
		assertNotNull(pageByUid);
	}

	@Test
	public void shouldCreatePage()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsCreatePageValidatorFactory.get(any())).thenReturn(validator);
		doNothing().when(facadeValidationService).validate(validator, pageData1);

		final AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> convertData = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertData).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());
		when(convertData.convert(pageData1)).thenReturn(contentPageModel);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertModel).when(pageModelConverterFactory).computeIfAbsent(any(), any());
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);

		final AbstractPageData result = pageFacade.createPage(pageData1);
		assertThat(result, is(pageData1));
		verify(modelService, times(1)).save(contentPageModel);
		verify(pageInitializer, times(1)).initialize(contentPageModel);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailCreate_PageDataPopulatorNotFound()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsCreatePageValidatorFactory.get(any())).thenReturn(validator);
		doNothing().when(facadeValidationService).validate(validator, pageData1);

		doThrow(new ConversionException("no populator found")).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());

		pageFacade.createPage(pageData1);
		verifyZeroInteractions(modelService, pageInitializer);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailCreate_PageModelPopulatorNotFound()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsCreatePageValidatorFactory.get(any())).thenReturn(validator);
		doNothing().when(facadeValidationService).validate(validator, pageData1);

		final AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> convertData = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertData).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());
		when(convertData.convert(pageData1)).thenReturn(contentPageModel);

		doThrow(new ConversionException("no converter found")).when(pageModelConverterFactory).computeIfAbsent(any(), any());

		pageFacade.createPage(pageData1);

		verify(modelService).save(contentPageModel);
		verifyZeroInteractions(pageInitializer);
	}

	@Test
	public void shouldCreatePageUsingDefaultValidator()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsCreatePageValidatorFactory.get(pageData1.getClass())).thenReturn(null);
		when(cmsCreatePageValidatorFactory.get(AbstractPageData.class)).thenReturn(validator);
		doNothing().when(facadeValidationService).validate(validator, pageData1);

		final AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> convertData = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertData).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());
		when(convertData.convert(pageData1)).thenReturn(contentPageModel);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertModel).when(pageModelConverterFactory).computeIfAbsent(any(), any());
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);

		final AbstractPageData result = pageFacade.createPage(pageData1);

		assertThat(result, is(pageData1));
		verify(modelService).save(contentPageModel);
		verify(pageInitializer, times(1)).initialize(contentPageModel);
	}

	@Test
	public void shouldUpdatePage()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsUpdatePageValidatorFactory.get(pageData1.getClass())).thenReturn(validator);
		doNothing().when(facadeValidationService).validate(any(), any(), any());
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);

		final AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> convertData = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertData).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());
		when(convertData.convert(pageData1)).thenReturn(contentPageModel);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		doReturn(convertModel).when(pageModelConverterFactory).computeIfAbsent(any(), any());
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);

		pageFacade.updatePage(PAGE_UID, pageData1);

		verify(facadeValidationService, times(1)).validate(any(Validator.class), any(UpdatePageValidationDto.class),
				any(AbstractPageData.class));
		verify(modelService, times(1)).save(contentPageModel);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailUpdatePage_ValidationError()
	{
		final CompositeValidator validator = mock(CompositeValidator.class);
		when(cmsUpdatePageValidatorFactory.get(pageData1.getClass())).thenReturn(validator);
		doThrow(ValidationException.class).when(facadeValidationService).validate(any(), any(), any());
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);

		pageFacade.updatePage(PAGE_UID, pageData1);

		verify(facadeValidationService, times(1)).validate(any(Validator.class), any(UpdatePageValidationDto.class),
				any(AbstractPageData.class));
		verify(modelService, times(1)).save(contentPageModel);
	}

	@Test
	public void shouldGenerateUidWithPrefix()
	{
		when(keyGenerator.generate()).thenReturn(UNIQUE_KEY);
		final AbstractPageData pageData = new AbstractPageData();
		pageFacade.generateUID(pageData);
		assertThat(pageData.getUid(), Matchers.is(DefaultPageFacade.DEFAULT_UID_PREFIX + "-" + UNIQUE_KEY));
	}

	@Test
	public void shouldGenerateUidWithoutPrefix()
	{
		when(keyGenerator.generate()).thenReturn(UNIQUE_KEY);
		final AbstractPageData pageData = new AbstractPageData();
		pageFacade.setUidPrefix(null);
		pageFacade.generateUID(pageData);
		assertThat(pageData.getUid(), Matchers.is(UNIQUE_KEY));
	}

	@Test
	public void shouldNotGenerateUid()
	{
		final AbstractPageData pageData = new AbstractPageData();
		pageData.setUid(PAGE_UID);
		pageFacade.generateUID(pageData);
		assertThat(pageData.getUid(), Matchers.is(PAGE_UID));
	}

	@Test
	public void shouldFindAllSupportedPageTypes()
	{
		final Set<Class<?>> supportedTypes = new HashSet<>();
		supportedTypes.add(ProductPageModel.class);
		pageFacade.setCmsSupportedPages(supportedTypes);

		final CMSPageTypeModel emailPageType = new CMSPageTypeModel();
		final CMSPageTypeModel contentPageType = new CMSPageTypeModel();
		final CMSPageTypeModel productPageType = new CMSPageTypeModel();
		emailPageType.setCode(EmailPageModel._TYPECODE);
		contentPageType.setCode(ContentPageModel._TYPECODE);
		productPageType.setCode(ProductPageModel._TYPECODE);
		when(adminPageService.getAllPageTypes()).thenReturn(Arrays.asList(emailPageType, contentPageType, productPageType));

		final PageTypeData productType = new PageTypeData();
		productType.setCode(ProductPageModel._TYPECODE);
		when(pageTypeModelConverter.convert(productPageType)).thenReturn(productType);

		final List<PageTypeData> pageTypes = pageFacade.findAllPageTypes();

		assertThat(pageTypes.size(), is(1));
		assertThat(pageTypes.get(0).getCode(), is(ProductPageModel._TYPECODE));
	}

	@Test
	public void shouldNotFindAnySupportedPageTypes()
	{
		final Set<Class<?>> supportedTypes = new HashSet<>();
		supportedTypes.add(null);
		pageFacade.setCmsSupportedPages(supportedTypes);

		final CMSPageTypeModel emailPageType = new CMSPageTypeModel();
		final CMSPageTypeModel contentPageType = new CMSPageTypeModel();
		final CMSPageTypeModel productPageType = new CMSPageTypeModel();
		emailPageType.setCode(EmailPageModel._TYPECODE);
		contentPageType.setCode(ContentPageModel._TYPECODE);
		productPageType.setCode(ProductPageModel._TYPECODE);
		when(adminPageService.getAllPageTypes()).thenReturn(Arrays.asList(emailPageType, contentPageType, productPageType));

		final PageTypeData productType = new PageTypeData();
		productType.setCode(ProductPageModel._TYPECODE);
		when(pageTypeModelConverter.convert(productPageType)).thenReturn(productType);

		final List<PageTypeData> pageTypes = pageFacade.findAllPageTypes();

		assertThat(pageTypes, empty());
	}

	@Test
	public void shouldFindFallbackPages() throws CMSItemNotFoundException
	{
		final ContentPageModel defaultPage = new ContentPageModel();
		defaultPage.setUid(DEFAULT_PAGE_UID);

		when(contentPageModel.getItemtype()).thenReturn(ContentPageModel._TYPECODE);

		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);
		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findDefaultPages(contentPageModel)).thenReturn(Arrays.asList(defaultPage));

		final List<String> fallbacks = pageFacade.findFallbackPages(PAGE_UID);

		assertThat(fallbacks.size(), is(1));
		assertThat(fallbacks, contains(DEFAULT_PAGE_UID));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailFindAllFallbackPages_InvalidPageId() throws CMSItemNotFoundException
	{
		doThrow(new UnknownIdentifierException("invalid uid")).when(adminPageService).getPageForIdFromActiveCatalogVersion(INVALID);

		pageFacade.findFallbackPages(INVALID);
	}

	@Test
	public void shouldFindVariationPages() throws CMSItemNotFoundException
	{
		final ContentPageModel defaultPage = new ContentPageModel();
		defaultPage.setUid(DEFAULT_PAGE_UID);
		when(contentPageModel.getUid()).thenReturn(PAGE_UID);

		when(adminPageService.getPageForIdFromActiveCatalogVersion(DEFAULT_PAGE_UID)).thenReturn(defaultPage);
		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findVariationPages(defaultPage)).thenReturn(Arrays.asList(contentPageModel));

		final List<String> variations = pageFacade.findVariationPages(DEFAULT_PAGE_UID);

		assertThat(variations.size(), is(1));
		assertThat(variations, contains(PAGE_UID));
	}

	@Test
	public void shouldFindAllFallbackPagesForContentPageType()
	{
		final ContentPageModel defaultPage = mock(ContentPageModel.class);
		when(defaultPage.getUid()).thenReturn(PAGE_UID);
		when(contentPageModel.getUid()).thenReturn(PAGE_UID);

		when(pageData1.getName()).thenReturn("Bname");
		when(pageData2.getName()).thenReturn("Aname");

		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE))
				.thenReturn(Arrays.asList(contentPageModel, defaultPage));

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> convertModel = mock(
				AbstractPopulatingConverter.class);
		when(convertModel.convert(contentPageModel)).thenReturn(pageData1);
		when(convertModel.convert(defaultPage)).thenReturn(pageData2);
		when(cmsPageComparator.compare(pageData2, pageData1)).thenReturn(-20);
		doReturn(convertModel).when(pageModelConverterFactory).get(contentPageModel.getClass());

		final List<AbstractPageData> contentPages = pageFacade.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);

		assertThat(contentPages.size(), is(2));
		assertThat(contentPages, contains(pageData2, pageData1));
	}

	@Test(expected = ValidationException.class)
	public void shouldFailFindAllFallbackPagesForContentPageType_InvalidTypeCode()
	{
		doThrow(new ValidationException(new ValidationError("invalid typecode"))).when(facadeValidationService).validate(any(),
				any());

		pageFacade.findPagesByType(CMSParagraphComponentModel._TYPECODE, Boolean.TRUE);
	}

}
