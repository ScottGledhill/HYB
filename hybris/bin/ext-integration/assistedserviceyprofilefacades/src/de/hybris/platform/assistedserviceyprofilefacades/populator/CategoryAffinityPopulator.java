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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Populator for category affinity data
 *
 * @param <SOURCE>
 *           NeighbourData
 * @param <TARGET>
 *           CategoryAffinityData
 */
public class CategoryAffinityPopulator<SOURCE extends NeighbourData, TARGET extends CategoryAffinityData>
		implements Populator<SOURCE, TARGET>
{

	private final Logger LOG = Logger.getLogger(CategoryAffinityPopulator.class);
	private CategoryService categoryService;
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Override
	public void populate(final SOURCE neighbourData, final TARGET categoryAffinityData) throws ConversionException
	{
		final Map<String, String> neighbourProperties = neighbourData.getProperties();

		final String categoryProfileId = neighbourProperties.get(YaasyprofileconnectConstants.CATEGORY_ID_FIELD);

		if (StringUtils.isEmpty(categoryProfileId))
		{
			throw new ConversionException("Category Id ["+categoryProfileId+"] not found for node " + neighbourData.getId());
		}
		try
		{
			final CategoryModel category = getCategoryService().getCategoryForCode(categoryProfileId);
			final CategoryData categoryData = getCategoryUrlConverter().convert(category);
			categoryAffinityData.setCategoryData(categoryData);
			categoryAffinityData.setImage(getImageDataForCategory(categoryData));
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.error("Category with Id [" + categoryProfileId + "] on yProfile not found in hybris", e);
		}
		categoryAffinityData.setViewCount(neighbourProperties.get(YaasyprofileconnectConstants.CATEGORY_VIEW_COUNT_FIELD));
	}

	protected ImageData getImageDataForCategory(final CategoryData categoryData)
	{
		return categoryData.getImage() != null ? categoryData.getImage() : getImageDataByProductInCategory(categoryData);
	}

	protected ImageData getImageDataByProductInCategory(final CategoryData categoryData)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);
		final ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData> searchPageData = getProductSearchFacade()
				.categorySearch(categoryData.getCode(), new SearchStateData(), pageableData);
		for (final ProductData p : searchPageData.getResults())
		{
			if (CollectionUtils.isNotEmpty(p.getImages()))
			{
				return p.getImages().toArray(new ImageData[1])[0];
			}
		}

		LOG.info("No image found for categorty [" + categoryData.getName() + "]!");
		return null;
	}

	public ProductSearchFacade<ProductData> getProductSearchFacade()
	{
		return productSearchFacade;
	}

	@Required
	public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade)
	{
		this.productSearchFacade = productSearchFacade;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected Converter<CategoryModel, CategoryData> getCategoryUrlConverter()
	{
		return categoryUrlConverter;
	}

	@Required
	public void setCategoryUrlConverter(final Converter<CategoryModel, CategoryData> categoryUrlConverter)
	{
		this.categoryUrlConverter = categoryUrlConverter;
	}


}