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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.common.service.ClassFieldFinder;
import de.hybris.platform.cmsfacades.common.validator.CompositeValidator;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsfacades.pages.service.PageInitializer;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageTypeData;
import de.hybris.platform.cmswebservices.dto.UpdatePageValidationDto;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;


/**
 * Default implementation of {@link PageFacade}.
 */
public class DefaultPageFacade implements PageFacade
{
	public static final String DEFAULT_UID_PREFIX = "page";

	private String uidPrefix = DEFAULT_UID_PREFIX;
	private CMSAdminPageService adminPageService;
	private CMSAdminRestrictionService adminRestrictionService;
	private PageInitializer pageInitializer;
	private FacadeValidationService facadeValidationService;
	private ModelService modelService;
	private TypeService typeService;

	private Map<Class<?>, CompositeValidator> cmsCreatePageValidatorFactory;
	private Map<Class<?>, CompositeValidator> cmsUpdatePageValidatorFactory;
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory;
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory;
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	private Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter;
	private Comparator<AbstractPageData> cmsPageComparator;

	private Validator cmsFindVariationPageValidator;

	private KeyGenerator keyGenerator;

	private Set<Class<?>> cmsSupportedPages;

	@Override
	public List<AbstractPageData> findAllPages()
	{
		return getAdminPageService().getAllPages().stream().filter(model -> getCmsSupportedPages().contains(model.getClass()))
				.filter(model -> getPageModelConverterFactory().containsKey(model.getClass())).map(model -> convertPage(model))
				.sorted(getCmsPageComparator()).collect(Collectors.toList());
	}

	@Override
	public AbstractPageData getPageByUid(final String uid) throws CMSItemNotFoundException
	{
		final AbstractPageModel pageModel = getPageModelById(uid);
		return getPageModelConverterFactory().get(pageModel.getClass()).convert(pageModel);
	}

	protected AbstractPageModel getPageModelById(final String pageId) throws CMSItemNotFoundException
	{
		try
		{
			return getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new CMSItemNotFoundException("Cannot find page with uid [" + pageId + "].", e);
		}
	}

	@Override
	public List<AbstractPageData> findPagesByType(final String typeCode, final Boolean isDefaultPage)
	{
		final AbstractPageData pageData = new AbstractPageData();
		pageData.setTypeCode(typeCode);
		pageData.setDefaultPage(isDefaultPage);

		getFacadeValidationService().validate(getCmsFindVariationPageValidator(), pageData);

		return getPageVariationResolver(typeCode).findPagesByType(typeCode, isDefaultPage).stream() //
				.map(model -> convertPage(model)).sorted(getCmsPageComparator()).collect(Collectors.toList());
	}

	@Override
	public List<String> findVariationPages(final String pageId) throws CMSItemNotFoundException
	{
		final AbstractPageModel page = getPageModelById(pageId);
		return getPageVariationResolver(page.getItemtype()).findVariationPages(page).stream().map(pageData -> pageData.getUid())
				.collect(Collectors.toList());
	}

	@Override
	public List<String> findFallbackPages(final String pageId) throws CMSItemNotFoundException
	{
		final AbstractPageModel page = getPageModelById(pageId);
		return getPageVariationResolver(page.getItemtype()).findDefaultPages(page).stream().map(pageData -> pageData.getUid())
				.collect(Collectors.toList());
	}

	protected PageVariationResolver<AbstractPageModel> getPageVariationResolver(final String typeCode)
	{
		return getPageVariationResolverTypeRegistry().getPageVariationResolverType(typeCode).get().getResolver();
	}

	@Override
	@Transactional
	public AbstractPageData createPage(final AbstractPageData pageData) throws ConversionException
	{
		generateUID(pageData);

		final Class<? extends AbstractPageData> pageDataClass = pageData.getClass();

		final Validator validator = Optional
				.ofNullable(Optional.ofNullable(getCmsCreatePageValidatorFactory().get(pageDataClass))
						.orElseGet(() -> getCmsCreatePageValidatorFactory().get(AbstractPageData.class)))
				.orElseThrow(() -> new IllegalArgumentException("The validator is required and must not be null."));

		getFacadeValidationService().validate(validator, pageData);

		final AbstractPageModel model = getConverter(pageDataClass).convert(pageData);
		getModelService().save(model);
		getPageInitializer().initialize(model);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> modelConverter = getPageModelConverterFactory()
				.computeIfAbsent(model.getClass(), k -> {
					throw new ConversionException(String.format("Converter not found for CMS Page Model [%s]", model.getName()));
				});
		return modelConverter.convert(model);
	}

	@Override
	public AbstractPageData updatePage(final String pageId, final AbstractPageData pageData)
	{
		final Validator validator = Optional
				.ofNullable(Optional.ofNullable(getCmsUpdatePageValidatorFactory().get(pageData.getClass()))
						.orElseGet(() -> getCmsUpdatePageValidatorFactory().get(AbstractPageData.class)))
				.orElseThrow(() -> new IllegalArgumentException("The validator is required and must not be null."));

		getFacadeValidationService().validate(validator, buildUpdateComponentValidationDto(pageId, pageData), pageData);

		final AbstractPageModel pageModel = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		getConverter(pageData.getClass()).populate(pageData, pageModel);
		getModelService().save(pageModel);

		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> modelConverter = getPageModelConverterFactory()
				.computeIfAbsent(pageModel.getClass(), k -> {
					throw new ConversionException(String.format("Converter not found for CMS Page Model [%s]", pageModel.getName()));
				});

		return modelConverter.convert(pageModel);

	}

	@Override
	public List<PageTypeData> findAllPageTypes()
	{
		final List<String> supportedPageTypes = getCmsSupportedPages().stream() //
				.filter(modelClass -> !Objects.isNull(modelClass)) //
				.map(modelClass -> ClassFieldFinder.getTypeCode(modelClass)) //
				.filter(typeCode -> !Strings.isNullOrEmpty(typeCode)).collect(Collectors.toList());

		return getAdminPageService().getAllPageTypes().stream().filter(model -> supportedPageTypes.contains(model.getCode())) //
				.map(pageType -> getPageTypeModelConverter().convert(pageType)) //
				.collect(Collectors.toList());
	}

	protected AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> getConverter(
			final Class<? extends AbstractPageData> pageDataClass)
	{
		return getPageDataPopulatorFactory().computeIfAbsent(pageDataClass, k -> {
			throw new ConversionException(String.format("Converter not found for CMS Page Data [%s]", pageDataClass.getName()));
		});
	}

	protected void generateUID(final AbstractPageData pageData)
	{
		if (StringUtils.isBlank(pageData.getUid()))
		{
			final String uid = StringUtils.isNotBlank(getUidPrefix())
					? String.format("%s-%s", getUidPrefix(), getKeyGenerator().generate().toString())
					: getKeyGenerator().generate().toString();

			pageData.setUid(uid);
		}
	}

	protected AbstractPageData convertPage(final AbstractPageModel pageModel)
	{
		final AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> converter = getPageModelConverterFactory()
				.get(pageModel.getClass());
		return converter.convert(pageModel);
	}

	protected UpdatePageValidationDto buildUpdateComponentValidationDto(final String originalUid, final AbstractPageData page)
	{
		final UpdatePageValidationDto dto = new UpdatePageValidationDto();
		dto.setOriginalUid(originalUid);
		dto.setPage(page);
		return dto;
	}

	public CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> getPageDataPopulatorFactory()
	{
		return pageDataPopulatorFactory;
	}

	@Required
	public void setPageDataPopulatorFactory(
			final Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory)
	{
		this.pageDataPopulatorFactory = pageDataPopulatorFactory;
	}

	protected Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> getPageModelConverterFactory()
	{
		return pageModelConverterFactory;
	}

	@Required
	public void setPageModelConverterFactory(
			final Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory)
	{
		this.pageModelConverterFactory = pageModelConverterFactory;
	}

	protected String getUidPrefix()
	{
		return uidPrefix;
	}

	public void setUidPrefix(final String uidPrefix)
	{
		this.uidPrefix = uidPrefix;
	}

	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected Set<Class<?>> getCmsSupportedPages()
	{
		return cmsSupportedPages;
	}

	@Required
	public void setCmsSupportedPages(final Set<Class<?>> cmsSupportedPages)
	{
		this.cmsSupportedPages = cmsSupportedPages;
	}

	protected Converter<CMSPageTypeModel, PageTypeData> getPageTypeModelConverter()
	{
		return pageTypeModelConverter;
	}

	@Required
	public void setPageTypeModelConverter(final Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter)
	{
		this.pageTypeModelConverter = pageTypeModelConverter;
	}

	protected Validator getCmsFindVariationPageValidator()
	{
		return cmsFindVariationPageValidator;
	}

	@Required
	public void setCmsFindVariationPageValidator(final Validator cmsFindVariationPageValidator)
	{
		this.cmsFindVariationPageValidator = cmsFindVariationPageValidator;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected CMSAdminRestrictionService getAdminRestrictionService()
	{
		return adminRestrictionService;
	}

	@Required
	public void setAdminRestrictionService(final CMSAdminRestrictionService adminRestrictionService)
	{
		this.adminRestrictionService = adminRestrictionService;
	}


	protected PageVariationResolverTypeRegistry getPageVariationResolverTypeRegistry()
	{
		return pageVariationResolverTypeRegistry;
	}

	@Required
	public void setPageVariationResolverTypeRegistry(final PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry)
	{
		this.pageVariationResolverTypeRegistry = pageVariationResolverTypeRegistry;
	}

	protected Comparator<AbstractPageData> getCmsPageComparator()
	{
		return cmsPageComparator;
	}

	@Required
	public void setCmsPageComparator(final Comparator<AbstractPageData> cmsPageComparator)
	{
		this.cmsPageComparator = cmsPageComparator;
	}

	protected Map<Class<?>, CompositeValidator> getCmsCreatePageValidatorFactory()
	{
		return cmsCreatePageValidatorFactory;
	}

	@Required
	public void setCmsCreatePageValidatorFactory(final Map<Class<?>, CompositeValidator> cmsCreatePageValidatorFactory)
	{
		this.cmsCreatePageValidatorFactory = cmsCreatePageValidatorFactory;
	}

	protected Map<Class<?>, CompositeValidator> getCmsUpdatePageValidatorFactory()
	{
		return cmsUpdatePageValidatorFactory;
	}

	@Required
	public void setCmsUpdatePageValidatorFactory(final Map<Class<?>, CompositeValidator> cmsUpdatePageValidatorFactory)
	{
		this.cmsUpdatePageValidatorFactory = cmsUpdatePageValidatorFactory;
	}

	public PageInitializer getPageInitializer()
	{
		return pageInitializer;
	}

	@Required
	public void setPageInitializer(final PageInitializer pageInitializer)
	{
		this.pageInitializer = pageInitializer;
	}
}
