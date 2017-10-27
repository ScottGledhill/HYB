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
package com.sap.hybris.reco.test.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.service.SapRecommendationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.reco.be.impl.OdataProductRecommendationManagerCEI;
import com.sap.hybris.reco.bo.impl.ProductRecommendationManagerBOImpl;
import com.sap.hybris.reco.constants.SapproductrecommendationConstants;
import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.util.ProductRecommendationManagerUtil;


/**
 *
 */
@UnitTest
public class ProductRecommendationManagerBOTest {

	@Mock
	private OdataProductRecommendationManagerCEI oDataBackendObj;
	@Mock
	private ProductRecommendationManagerUtil util;
	
	@Mock
	private SapRecommendationService recoBufferService;
	@Mock
	private SapRecommendationService recommendationService;
	
	private ProductRecommendationManagerBOImpl recoBO;
	private RecommendationContext context;
	private List<ProductRecommendationData> data;

	/**
	 *
	 */
	@Before
	public void setup( )
	{
		MockitoAnnotations.initMocks(this);
		SAPRecommendationBufferModel recommendation = new SAPRecommendationBufferModel();
		recommendation.setRecoList("M-01,M-09,M-13");
		context = new RecommendationContext();
		context.setUserId("test@sap.com");
		context.setRecotype("SAP_TOP_SELLERS");
		context.setLeadingProductId("123456");
		context.setLeadingItemType(SapproductrecommendationConstants.PRODUCT);
		context.setIncludeCart(false);
		context.setIncludeRecent(false);
		data = new ArrayList<ProductRecommendationData>();
		buildDataList();
		doReturn(data).when(oDataBackendObj).getProductRecommendation(context, new HashMap<String, Object>());
		doReturn(recommendation).when(recoBufferService).getRecommendation(context.getUserId(), context.getRecotype(), context.getLeadingProductId());
	}

	private void buildDataList() {
		final ProductRecommendationData datum = new ProductRecommendationData();
		datum.setProductCode("M-01");
		data.add(datum);
		datum.setProductCode("M-09");
		data.add(datum);
		datum.setProductCode("M-13");
		data.add(datum);
	}

	/**
	 *
	 */
	@Test
	public void testODataGetRecommendations() 
	{
		recoBO = new ProductRecommendationManagerBOImpl();
		recoBO.setBackendObject(oDataBackendObj);
		recoBO.setRecoBufferService(recoBufferService);
		final List<ProductRecommendationData> recos = recoBO.getProductRecommendation(context);
		assertNotNull(recos);
		assertEquals(recos.size(), data.size());
	}

}
