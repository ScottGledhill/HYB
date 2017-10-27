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
package de.hybris.platform.sap.productconfig.frontend.breadcrumb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistory;
import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistoryEntry;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigureBreadcrumbBuilderTest
{
	private static final String PRODUCT_CODE = "1234";
	private static final String PRODUCT_URL = "1234";
	private static final String PRODUCT_VARIANT_URL = "12345";
	private static final String CATEGORY_URL = "0815";
	private static final String CONFIG_URL = "/configuratorPage/CPQCONFIGURATOR";

	private ProductConfigureBreadcrumbBuilder breadCrumbBuilder;
	protected ProductData productData;
	protected ProductModel productModel;

	@Mock
	private ProductService productService;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		productData = createModel();
		final UrlResolver<ProductModel> productModelUrlResolver = new ProductUrlResolverTest();
		final UrlResolver<CategoryModel> categoryModelUrlResolver = new CategoryModelUrlResolverTest();
		final BrowseHistory browseHistory = new BrowseHistoryTest();

		breadCrumbBuilder = new ProductConfigureBreadcrumbBuilder()
		{
			@Override
			protected String getLinkText()
			{
				return "Product Configuration";
			}

			@Override
			protected String getOverviewLinkText()
			{
				return "Configuration Overview";
			}
		};
		breadCrumbBuilder.setProductModelUrlResolver(productModelUrlResolver);
		breadCrumbBuilder.setCategoryModelUrlResolver(categoryModelUrlResolver);
		breadCrumbBuilder.setBrowseHistory(browseHistory);
		breadCrumbBuilder.setProductService(productService);

		final Map<String, Object> catValues = new HashMap<>();

		final PK catPk = PK.fromLong(600);
		final CategoryModel catModel = new CategoryModel(
				ItemContextBuilder.createMockContext(CategoryModel.class, catPk, Locale.US, catValues));
		catModel.setName("Category");
		catModel.setCode("0815");


		final Map<String, Object> superCatValues = new HashMap<>();
		final PK superCatPk = PK.fromLong(600);
		final CategoryModel superCatModel = new ClassificationClassModel(
				ItemContextBuilder.createMockContext(ClassificationClassModel.class, superCatPk, Locale.US, superCatValues));
		superCatModel.setName("Super-Category");
		superCatModel.setCode("4711");

		final List<CategoryModel> superSuperCats = new ArrayList<>();
		superSuperCats.add(superCatModel);
		catModel.setSupercategories(superSuperCats);

		final Collection<CategoryModel> superCats = new Vector<>();
		superCats.add(catModel);
		final Map<String, Object> prodValues = new HashMap<>();
		final PK prodPk = PK.fromLong(500);
		final ProductModel productModel = new ProductModel(
				ItemContextBuilder.createMockContext(ProductModel.class, prodPk, Locale.US, prodValues));

		productModel.setSupercategories(superCats);
		productModel.setName("Test");
		productModel.setCode(PRODUCT_CODE);

		final ProductModel productModel2 = new ProductModel(
				ItemContextBuilder.createMockContext(ProductModel.class, prodPk, Locale.US, prodValues));

		productModel2.setSupercategories(superCats);
		productModel2.setName("Test");
		productModel2.setCode("12345");


		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel);
		given(productService.getProductForCode("12345")).willReturn(productModel2);

	}

	@Test
	public void getBreadCrumbTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE);

		assertEquals(3, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
	}

	@Test
	public void getBreadCrumbLinkClassTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE);

		assertEquals(3, breadCrumbs.size());

		assertNull("Link class must be null for second item", breadCrumbs.get(1).getLinkClass());
		assertNotNull("Link class must not be null for third item", breadCrumbs.get(2).getLinkClass());
	}

	@Test
	public void getOverviewBreadCrumbTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE);

		assertEquals(4, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
		assertEquals(PRODUCT_URL + "/configOverview", breadCrumbs.get(3).getUrl());
	}

	@Test
	public void getOverviewBreadCrumbLinkClassTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE);

		assertEquals(4, breadCrumbs.size());

		assertNull("Link class must be null for second item", breadCrumbs.get(1).getLinkClass());
		assertNull("Link class must be null for second item", breadCrumbs.get(2).getLinkClass());
		assertNotNull("Link class must not be null for third item", breadCrumbs.get(3).getLinkClass());
	}

	@Test
	public void getVariantOverviewBreadCrumbTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getVariantOverviewBreadcrumbs(createVariantModel());

		assertEquals(4, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
		assertEquals(PRODUCT_VARIANT_URL + "/variantOverview", breadCrumbs.get(3).getUrl());
	}

	@Test
	public void getVariantOverviewBreadCrumbLinkClassTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getVariantOverviewBreadcrumbs(createVariantModel());

		assertEquals(4, breadCrumbs.size());

		assertNull("Link class must be null for second item", breadCrumbs.get(1).getLinkClass());
		assertNull("Link class must be null for second item", breadCrumbs.get(2).getLinkClass());
		assertNotNull("Link class must not be null for third item", breadCrumbs.get(3).getLinkClass());
	}

	private ProductData createVariantModel()
	{
		final ProductData productData = new ProductData();

		updateModel(productData);

		productData.setName("TestVariant");
		productData.setCode("12345");
		productData.setBaseProduct(PRODUCT_CODE);

		return productData;
	}

	private ProductData createModel()
	{
		final ProductData productData = new ProductData();

		updateModel(productData);

		return productData;
	}

	private void updateModel(final ProductData productData)
	{
		productData.setName("Test");
		productData.setCode(PRODUCT_CODE);
	}

	class ProductUrlResolverTest implements UrlResolver<ProductModel>
	{

		@Override
		public String resolve(final ProductModel source)
		{
			return source.getCode();
		}
	}

	class ProductConverterTest implements Converter<ProductModel, ProductData>
	{
		@Override
		public ProductData convert(final ProductModel paramSOURCE, final ProductData paramTARGET) throws ConversionException
		{
			paramTARGET.setName(paramSOURCE.getName());
			paramTARGET.setCode(paramSOURCE.getCode());
			return paramTARGET;
		}


		@Override
		public ProductData convert(final ProductModel paramSOURCE) throws ConversionException
		{
			return convert(paramSOURCE, new ProductData());
		}



	}

	class CategoryModelUrlResolverTest implements UrlResolver<CategoryModel>
	{

		@Override
		public String resolve(final CategoryModel source)
		{
			return source.getCode();
		}
	}

	class BrowseHistoryTest implements BrowseHistory
	{

		private final Map<String, BrowseHistoryEntry> history = new HashMap<>();

		@Override
		public void addBrowseHistoryEntry(final BrowseHistoryEntry browseHistoryEntry)
		{
			history.put(browseHistoryEntry.getUrl(), browseHistoryEntry);
		}

		@Override
		public BrowseHistoryEntry findEntryMatchUrlEndsWith(final String url)
		{
			return history.get(url);
		}

	}
}
