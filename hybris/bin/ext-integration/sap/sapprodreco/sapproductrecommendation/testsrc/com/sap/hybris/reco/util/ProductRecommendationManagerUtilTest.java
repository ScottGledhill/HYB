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
package com.sap.hybris.reco.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;


@UnitTest
public class ProductRecommendationManagerUtilTest {

	ProductRecommendationManagerUtil productRecommendationManagerUtil;
	List<String> productuids;
	List<ProductRecommendationData> prodRecoData;
	@Mock
	private SessionService sessionService;
	@Mock
	private RecommendationContext context;
	@Mock
	CMSSAPRecommendationComponentModel model;

	@Before
	public void setup(){
		productRecommendationManagerUtil = new ProductRecommendationManagerUtil();
		ProductRecommendationData prd = new ProductRecommendationData();
		MockitoAnnotations.initMocks(this);
		productRecommendationManagerUtil.setSessionService(sessionService);
		context = Mockito.mock(RecommendationContext.class);
		model = Mockito.mock(CMSSAPRecommendationComponentModel.class);
		Mockito.when(context.getUserId()).thenReturn("12345");
		//context.setUserId("12345");
		productuids = new ArrayList<String>();
		prodRecoData = new ArrayList<ProductRecommendationData>();
		productuids.add("111");
		prd.setProductCode("111");
		prodRecoData.add(prd);
		productuids.add("222");
		prd.setProductCode("222");
		prodRecoData.add(prd);
		productuids.add("333");
		prd.setProductCode("333");
		prodRecoData.add(prd);
		Session session = Mockito.mock(Session.class);
		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		Mockito.when(session.getSessionId()).thenReturn("session1"); 

		Mockito.when(context.getComponentModel()).thenReturn(model);
		//context.setComponentModel(model);
		Mockito.when(model.getUid()).thenReturn("12345");
		//context.getComponentModel().setUid("12345");
		
		buildDataList();
	}
	
	private void buildDataList() {
		
	
	}
		
	@Test
	public void testCreateProductRecommedation(){
		ProductRecommendationData prodRecoData = new ProductRecommendationData();
		String prodCode = "12345";
		prodRecoData = productRecommendationManagerUtil.createProductRecommedation(prodCode);
		assertNotNull(prodRecoData);
		assertEquals(prodCode, prodRecoData.getProductCode());
		
	}
	
	@Test
	public void testGetLeadingItemIdCategory(){
		
		List<String> leadItems = new ArrayList<String>();
		
		Mockito.when(context.getLeadingItemType()).thenReturn("C");
		List<String> cats = new ArrayList<String>();
		cats.add("1112");
		Mockito.when(context.getLeadingCategoryIds()).thenReturn(cats);
		ProductService productService = Mockito.mock(ProductService.class);
		productRecommendationManagerUtil.setProductService(productService);
		ProductModel model = Mockito.mock(ProductModel.class);
		Mockito.when(productService.getProductForCode("1234")).thenReturn(model);
		Collection<CategoryModel> catList = new ArrayList<CategoryModel>();
		CategoryModel catModel = new CategoryModel();
		catModel.setCode("1112");
		catList.add(catModel);
		CategoryModel catModel2 = new CategoryModel();
		catModel2.setCode("2223");
		catList.add(catModel2);
		CategoryModel catModel3 = new CategoryModel();
		catModel3.setCode("3334");
		catList.add(catModel3);
		CategoryModel catModel4 = new CategoryModel();
		catModel4.setCode("4445");
		catList.add(catModel4);
		Mockito.when(model.getSupercategories()).thenReturn(catList);
		leadItems = productRecommendationManagerUtil.getLeadingItemId(context);
		assertNotNull(leadItems);
		CategoryModel cmodel = new CategoryModel();
		cmodel = (CategoryModel) catList.iterator().next();
		assertEquals(((cmodel).getCode()), (leadItems.get(0)));
		
	}
	
	@Test
	public void testGetLeadingItemIdProduct(){
		
		List<String> leadItems = new ArrayList<String>();
		
		Mockito.when(context.getLeadingItemType()).thenReturn("P");
		Mockito.when(context.getLeadingProductId()).thenReturn("1234");
		ProductService productService = Mockito.mock(ProductService.class);
		productRecommendationManagerUtil.setProductService(productService);
		ProductModel model = Mockito.mock(ProductModel.class);
		Mockito.when(productService.getProductForCode("1234")).thenReturn(model);
		
		leadItems = productRecommendationManagerUtil.getLeadingItemId(context);
		assertNotNull(leadItems);
		assertEquals("1234", leadItems.get(0));
		
	}
	

	@Test
	public void testGetLeadingItemIdInvalidLeadType(){
		
		List<String> leadItems = new ArrayList<String>();
		
		Mockito.when(context.getLeadingItemType()).thenReturn("D");
		Mockito.when(context.getLeadingProductId()).thenReturn("1234");
		ProductService productService = Mockito.mock(ProductService.class);
		productRecommendationManagerUtil.setProductService(productService);
		ProductModel model = Mockito.mock(ProductModel.class);
		Mockito.when(productService.getProductForCode("1234")).thenReturn(model);
		
		leadItems = productRecommendationManagerUtil.getLeadingItemId(context);
		assertNotNull(leadItems);
		assertEquals(0, leadItems.size());
		
	}
}
