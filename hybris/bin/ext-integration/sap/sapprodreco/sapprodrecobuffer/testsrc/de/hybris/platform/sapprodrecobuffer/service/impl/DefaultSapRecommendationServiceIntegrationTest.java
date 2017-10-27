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
package de.hybris.platform.sapprodrecobuffer.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sapprodrecobuffer.constants.SapprodrecobufferConstants;
import de.hybris.platform.sapprodrecobuffer.dao.SapRecommendationDao;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecoTypeMappingModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;
import de.hybris.platform.sapprodrecobuffer.service.SapRecommendationService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultSapRecommendationServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String SCENARIO_ID = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN";
	private static final String HASH_ID = "D33DD1F71615D50334FB2F10433653543";
	private static final String USER_ID = "test.guy@sap.com";
	private static final String RECO_LIST = "12345,67890";
	private static final String LEADING_ITEMS = "99999";
	private static final String RECO_TYPE = "G";

	private static final String SCENARIO_ID_RESTRICTED = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN_RESTRICTED";
	private static final String SCENARIO_ID_GENERIC = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN_GENERIC";
	private static final String HASH_ID_RESTRICTED = "A33DD1F71615D50334FB2F10433653543";
	private static final String HASH_ID_GENERIC = "B33DD1F71615D50334FB2F10433653543";
	private static final String RECO_TYPE_RESTRICTED = SapprodrecobufferConstants.RESTRICTED_RECO_TYPE;
	private static final String RECO_TYPE_GENERIC = SapprodrecobufferConstants.GENERIC_RECO_TYPE;
	private static final String RECO_LIST_RESTRICTED = "pr123,pr456";
	private static final String RECO_LIST_GENERIC = "pg123,pg456";
	private static final String LEADINGITEMS_RESTRICTED = "res123,res456";
	private static final String LEADINGITEMS_GENERIC = "gen123,gen456";

	// parameters used for testing saving recommendation
	private static final String USER_ID_SAVE = "test.savereco@sap.com";
	private static final String SCENARIO_ID_SAVE = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN_SAVE";
	private static final String HASH_ID_SAVE = "C33DD1F71615D50334FB2F10433653543";
	private static final String RECO_TYPE_SAVE = SapprodrecobufferConstants.GENERIC_RECO_TYPE;
	private static final String EMPTY_STRING = SapprodrecobufferConstants.EMPTY_STRING;

	private SAPRecommendationBufferModel recommendationModel;
	private SAPRecommendationMappingModel recommendationMappingModel;
	private SAPRecommendationBufferModel restrictedRecommendationModel;
	private SAPRecommendationBufferModel genericRecommendationModel;
	private SAPRecoTypeMappingModel restrictedRecoTypeMapping;
	private SAPRecoTypeMappingModel genericRecoTypeMapping;

	@Resource
	private ModelService modelService;

	@Resource
	private SapRecommendationDao recommendationDao;

	@Resource
	private SapRecommendationService recommendationBufferService;

	@Before
	public void setUp()
	{
		//Create a recommendation with empty leading items
		recommendationModel = modelService.create(SAPRecommendationBufferModel.class);
		recommendationModel.setScenarioId(SCENARIO_ID);
		recommendationModel.setHashId(HASH_ID);
		recommendationModel.setLeadingItems(LEADING_ITEMS);
		recommendationModel.setRecoList(RECO_LIST);
		recommendationModel.setExpiresOn(new Date());
		modelService.save(recommendationModel);

		//Create a recommendation mapping
		recommendationMappingModel = modelService.create(SAPRecommendationMappingModel.class);
		recommendationMappingModel.setScenarioId(SCENARIO_ID);
		recommendationMappingModel.setUserId(USER_ID);
		recommendationMappingModel.setHashId(HASH_ID);
		recommendationMappingModel.setExpiresOn(new Date());
		modelService.save(recommendationMappingModel);

		//Create a restricted recommendation type mapping
		restrictedRecoTypeMapping = modelService.create(SAPRecoTypeMappingModel.class);
		restrictedRecoTypeMapping.setScenarioId(SCENARIO_ID_RESTRICTED);
		restrictedRecoTypeMapping.setRecoType(RECO_TYPE_RESTRICTED);
		restrictedRecoTypeMapping.setHashId(HASH_ID_RESTRICTED);
		restrictedRecoTypeMapping.setExpiresOn(new Date());
		modelService.save(restrictedRecoTypeMapping);

		//Create a restricted recommendation
		restrictedRecommendationModel = modelService.create(SAPRecommendationBufferModel.class);
		restrictedRecommendationModel.setScenarioId(SCENARIO_ID_RESTRICTED);
		restrictedRecommendationModel.setLeadingItems(LEADINGITEMS_RESTRICTED);
		restrictedRecommendationModel.setHashId(HASH_ID_RESTRICTED);
		restrictedRecommendationModel.setRecoList(RECO_LIST_RESTRICTED);
		restrictedRecommendationModel.setExpiresOn(new Date());
		modelService.save(restrictedRecommendationModel);

		//Create a generic recommendation type mapping
		genericRecoTypeMapping = modelService.create(SAPRecoTypeMappingModel.class);
		genericRecoTypeMapping.setScenarioId(SCENARIO_ID_GENERIC);
		genericRecoTypeMapping.setRecoType(RECO_TYPE_GENERIC);
		genericRecoTypeMapping.setHashId(HASH_ID_GENERIC);
		genericRecoTypeMapping.setExpiresOn(new Date());
		modelService.save(genericRecoTypeMapping);

		//Create a generic recommendation
		genericRecommendationModel = modelService.create(SAPRecommendationBufferModel.class);
		genericRecommendationModel.setScenarioId(SCENARIO_ID_GENERIC);
		genericRecommendationModel.setLeadingItems(LEADINGITEMS_GENERIC);
		genericRecommendationModel.setHashId(HASH_ID_GENERIC);
		genericRecommendationModel.setRecoList(RECO_LIST_GENERIC);
		genericRecommendationModel.setExpiresOn(new Date());
		modelService.save(genericRecommendationModel);
	}

	@After
	public void tearDown()
	{
		modelService.remove(recommendationModel);
		modelService.remove(recommendationMappingModel);
		modelService.remove(restrictedRecommendationModel);
		modelService.remove(genericRecommendationModel);
		modelService.remove(restrictedRecoTypeMapping);
		modelService.remove(genericRecoTypeMapping);
	}

	@Test
	public void testGetRecommendation()
	{
		final SAPRecommendationBufferModel recoResult = //
				recommendationBufferService.getRecommendation(USER_ID, SCENARIO_ID, LEADING_ITEMS);
		assertTrue(RECO_LIST.equals(recoResult.getRecoList()));
	}

	@Test
	public void testGetRestrictedRecommendation()
	{
		final SAPRecommendationBufferModel recoResult = //
				recommendationBufferService.getGenericRecommendation(SCENARIO_ID_RESTRICTED, LEADINGITEMS_RESTRICTED);
		assertTrue(RECO_LIST_RESTRICTED.equals(recoResult.getRecoList()));
	}

	@Test
	public void testGetGenericRecommendation()
	{
		final SAPRecommendationBufferModel recoResult = //
				recommendationBufferService.getGenericRecommendation(SCENARIO_ID_GENERIC, LEADINGITEMS_GENERIC);
		assertTrue(RECO_LIST_GENERIC.equals(recoResult.getRecoList()));
	}

	@Test
	public void testSaveRecommendation()
	{
		//test save recommendation mapping model
		recommendationBufferService.saveRecommendation(USER_ID_SAVE, SCENARIO_ID_SAVE, HASH_ID_SAVE, EMPTY_STRING, EMPTY_STRING,
				EMPTY_STRING, new Date());
		final List<SAPRecommendationMappingModel> recoMappingResult = //
				recommendationDao.findRecommendationMapping(USER_ID_SAVE, SCENARIO_ID_SAVE);
		assertEquals(1, recoMappingResult.size());
		modelService.remove(recoMappingResult.get(0));

		// test save recommendation model
		final List<SAPRecommendationBufferModel> recoBufferResult = //
				recommendationDao.findRecommendation(SCENARIO_ID_SAVE, HASH_ID_SAVE, EMPTY_STRING);
		assertEquals(1, recoBufferResult.size());
		modelService.remove(recoBufferResult.get(0));

		//test save recommendation type model with  no userId
		recommendationBufferService.saveRecommendation(EMPTY_STRING, SCENARIO_ID_SAVE, HASH_ID_SAVE, EMPTY_STRING, EMPTY_STRING,
				RECO_TYPE_SAVE, new Date());
		final List<SAPRecoTypeMappingModel> recoTypeMappingResult = //
				recommendationDao.findRecoTypeMapping(RECO_TYPE_SAVE, SCENARIO_ID_SAVE);
		assertEquals(1, recoTypeMappingResult.size());
		modelService.remove(recoTypeMappingResult.get(0));
	}

	@Test
	public void testSaveExistingRecommendation()
	{
		//Save again
		recommendationBufferService.saveRecommendation(USER_ID, SCENARIO_ID, HASH_ID, LEADING_ITEMS, RECO_LIST, RECO_TYPE,
				new Date());

		//Check that only one recommendation exists
		final List<SAPRecommendationBufferModel> recoResult = //
				recommendationDao.findRecommendation(SCENARIO_ID, HASH_ID, LEADING_ITEMS);
		assertFalse(recoResult.isEmpty());
		assertEquals(1, recoResult.size());
	}

	@Test
	public void testUpdateExistingRecommendation()
	{
		//Update the recommendation by calling the save method
		final Date newDate = new Date();
		final String newRecoList = "33333,44444";
		recommendationBufferService.saveRecommendation(USER_ID, SCENARIO_ID, HASH_ID, LEADING_ITEMS, newRecoList, RECO_TYPE,
				newDate);

		//Check that the new fields were saved
		final List<SAPRecommendationBufferModel> recoResult = //
				recommendationDao.findRecommendation(SCENARIO_ID, HASH_ID, "99999");
		assertFalse(recoResult.isEmpty());
		assertTrue(newRecoList.equals(recoResult.get(0).getRecoList()));
		assertEquals(recoResult.get(0).getExpiresOn(), newDate);
	}

}
