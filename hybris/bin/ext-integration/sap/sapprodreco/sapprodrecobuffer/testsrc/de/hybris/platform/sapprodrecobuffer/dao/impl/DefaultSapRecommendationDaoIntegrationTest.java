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
package de.hybris.platform.sapprodrecobuffer.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sapprodrecobuffer.constants.SapprodrecobufferConstants;
import de.hybris.platform.sapprodrecobuffer.dao.SapRecommendationDao;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecoTypeMappingModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test the flexible search queries
 */
@IntegrationTest
public class DefaultSapRecommendationDaoIntegrationTest extends ServicelayerTransactionalTest
{
	
	
	private static final String SCENARIO_ID = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN";
	private static final String HASH_ID = "D33DD1F71615D50334FB2F1043365430";
	private static final String HASH_ID2 = "D33DD1F71615D50334FB2F104331234";
	private static final String USER_ID = "test@sap.com";
	private static final String LEADING_ITEMS = "23191";
	private static final String RECO_LIST = "23210,12345";
	private static final String RECO_TYPE = SapprodrecobufferConstants.RESTRICTED_RECO_TYPE;
	private static Calendar old_test_cal;
	private static Calendar current_cal;
	private static Date old_test_date;
	private static Date current_date;

	@Resource
	private ModelService modelService;
	

	@Resource
	private SapRecommendationDao recommendationDao;

	private SAPRecommendationBufferModel recommendationModel;
	private SAPRecommendationMappingModel recommendationMappingModel1;
	private SAPRecommendationMappingModel recommendationMappingModel2;
	private SAPRecoTypeMappingModel recommendationTypeMappingModel;

	@Before
	public void setUp()
	{
		old_test_cal = Calendar.getInstance();
		old_test_cal.set(2011, Calendar.JANUARY, 22);
		old_test_date = old_test_cal.getTime();
		current_cal = Calendar.getInstance();
		current_date = current_cal.getTime();
		
		//Create recommendation test data
		recommendationModel = modelService.create(SAPRecommendationBufferModel.class);
		recommendationModel.setScenarioId(SCENARIO_ID);
		recommendationModel.setHashId(HASH_ID);
		recommendationModel.setLeadingItems(LEADING_ITEMS);
		recommendationModel.setRecoList("23210,12345");
		recommendationModel.setExpiresOn(old_test_date);
		modelService.save(recommendationModel);

		//Create recommendation mapping test data
		recommendationMappingModel1 = modelService.create(SAPRecommendationMappingModel.class);
		recommendationMappingModel1.setUserId(USER_ID);
		recommendationMappingModel1.setScenarioId(SCENARIO_ID);
		recommendationMappingModel1.setHashId(HASH_ID);
		recommendationMappingModel1.setExpiresOn(old_test_date);
		modelService.save(recommendationMappingModel1);

		recommendationMappingModel2 = modelService.create(SAPRecommendationMappingModel.class);
		recommendationMappingModel2.setUserId(USER_ID);
		recommendationMappingModel2.setScenarioId(SCENARIO_ID);
		recommendationMappingModel2.setHashId(HASH_ID2);
		recommendationMappingModel2.setExpiresOn(old_test_date);
		modelService.save(recommendationMappingModel2);
		
		//Create recommendation type mapping test data
		recommendationTypeMappingModel = modelService.create(SAPRecoTypeMappingModel.class);
		recommendationTypeMappingModel.setRecoType(RECO_TYPE);
		recommendationTypeMappingModel.setScenarioId(SCENARIO_ID);
		recommendationTypeMappingModel.setHashId(HASH_ID);
		recommendationTypeMappingModel.setExpiresOn(old_test_date);
		modelService.save(recommendationTypeMappingModel);
		
	}

	@After
	public void tearDown()
	{
		modelService.remove(recommendationModel);
		modelService.remove(recommendationMappingModel1);
		modelService.remove(recommendationMappingModel2);
		modelService.remove(recommendationTypeMappingModel);
	}

	@Test
	public void testFindRecommendation()
	{
		final List<SAPRecommendationBufferModel> recoResult = //
				recommendationDao.findRecommendation(SCENARIO_ID, HASH_ID, LEADING_ITEMS);
		assertFalse(recoResult.isEmpty());
		assertTrue(RECO_LIST.equals(recoResult.get(0).getRecoList()));
	}

	@Test
	public void testFindRecommendationMapping()
	{
		final List<SAPRecommendationMappingModel> recoResult = recommendationDao.findRecommendationMapping(USER_ID, SCENARIO_ID);
		assertFalse(recoResult.isEmpty());
		assertTrue(HASH_ID.equals(recoResult.get(0).getHashId()));
		assertTrue(HASH_ID2.equals(recoResult.get(1).getHashId()));
		
		final List<SAPRecommendationMappingModel> recoResult2 = recommendationDao.findRecommendationMapping(USER_ID, SCENARIO_ID, HASH_ID);
		assertFalse(recoResult2.isEmpty());
		assertTrue(HASH_ID.equals(recoResult2.get(0).getHashId()));
	}

	@Test
	public void testFindRecoTypeMapping()
	{
		final List<SAPRecoTypeMappingModel> recoResult = recommendationDao.findRecoTypeMapping(RECO_TYPE, SCENARIO_ID);
		assertFalse(recoResult.isEmpty());
		assertTrue(HASH_ID.equals(recoResult.get(0).getHashId()));
		
	}
	
	@Test
	public void testFindExpiredRecommendations()
	{
		final List<SAPRecommendationBufferModel> recoResult = recommendationDao.findExpiredRecommendations(current_date);
		assertEquals(1,recoResult.size());
	} 
	
	@Test
	public void testfindExpiredRecommendationMappings()
	{
		final List<SAPRecommendationMappingModel> recoResult = recommendationDao.findExpiredRecommendationMappings(current_date);
		assertEquals(2,recoResult.size());
	}
	
	@Test
	public void testFindExpiredRecoTypeMappings()
	{
		final List<SAPRecoTypeMappingModel> recoResult = recommendationDao.findExpiredRecoTypeMappings(current_date);
		assertEquals(1,recoResult.size());
	}
	
	@Test
	public void testUpdateRecommendation()
	{
		final Date date = new Date();

		//Set new values in existing model
		recommendationModel.setRecoList("22222,99999");
		recommendationModel.setExpiresOn(date);
		modelService.save(recommendationModel);

		//Retrieve the recommendation again
		final List<SAPRecommendationBufferModel> recoResult = //
				recommendationDao.findRecommendation(SCENARIO_ID, HASH_ID, LEADING_ITEMS);

		//Check that new values were saved
		assertFalse(recoResult.isEmpty());
		assertEquals("22222,99999", recoResult.get(0).getRecoList());
		assertEquals(date, recoResult.get(0).getExpiresOn());
	}
}

