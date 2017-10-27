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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.util.builder.ProductCategoryModelBuilder;

import java.util.Locale;


public class ProductCategoryModelMother extends AbstractModelMother<CategoryModel>
{
	public static final String ELECTRONICS = "electronics";

	private CategoryDao categoryDao;
	
	public CategoryModel createDefaultCategory(final CatalogVersionModel catalogVersion)
	{
		return getFromCollectionOrSaveAndReturn(() -> getCategoryDao().findCategoriesByCode(ELECTRONICS),
				() -> ProductCategoryModelBuilder.aModel() //
						.withName(ELECTRONICS, Locale.ENGLISH) //
						.withCatalogVersion(catalogVersion) //
						.withCode(ELECTRONICS)
						.build());
	}

	public CategoryDao getCategoryDao()
	{
		return categoryDao;
	}

	public void setCategoryDao(final CategoryDao categoryDao)
	{
		this.categoryDao = categoryDao;
	}
}
