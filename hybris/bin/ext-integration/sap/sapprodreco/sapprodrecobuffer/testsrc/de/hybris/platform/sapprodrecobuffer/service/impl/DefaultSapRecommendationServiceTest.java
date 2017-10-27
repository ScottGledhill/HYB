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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sapprodrecobuffer.dao.SapRecommendationDao;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRecommendationServiceTest
{
	private static final String SCENARIO_ID = "SAP_TOP_SELLERS_EMAIL_CAMPAIGN";
	private static final String HASH_ID = "D33DD1F71615D50334FB2F1043365430";
	private static final String LEADING_ITEMS = "23191";
	private static final Date EXPIRY_DATE = new Date(1000); //some old date

	@InjectMocks
	private DefaultSapRecommendationService recommendationService;

	@Mock
	private SapRecommendationDao recommendationDao;

	@Mock
	private SAPRecommendationBufferModel recommendationModel;

	@Mock
	private SAPRecommendationMappingModel recommendationMappingModel;

	@Mock
	private SAPRecommendationMappingModel recommendationMappingModel2;

	@Before
	public void setUp()
	{
		recommendationService.setEnableRecommendationBuffer(true);
	}

	@Test
	public void testListToCSVString()
	{
		when(recommendationMappingModel.getHashId()).thenReturn("11111");
		when(recommendationMappingModel2.getHashId()).thenReturn("22222");
		final List<SAPRecommendationMappingModel> mappings = Arrays.asList(recommendationMappingModel, recommendationMappingModel2);

		String csvList = "";

		for (final SAPRecommendationMappingModel mapping : mappings)
		{
			csvList += mapping.getHashId() + ",";
		}

		csvList = csvList.replaceAll(",$", "");

		assertEquals("11111,22222", csvList);
	}

	@Test
	public void testExpiredRecommendation()
	{
		final List<SAPRecommendationBufferModel> recoList = Arrays.asList(recommendationModel);
		recommendationService.setExpiryOffset(30);

		when(recommendationDao.findRecommendation(SCENARIO_ID, HASH_ID, LEADING_ITEMS)).thenReturn(recoList);
		when(recommendationModel.getExpiresOn()).thenReturn(EXPIRY_DATE);

		assertTrue(recommendationService.isRecommendationExpired(SCENARIO_ID, HASH_ID, LEADING_ITEMS));
	}

	@Test
	public void testExpiredRecommendationByDate()
	{
		when(recommendationModel.getExpiresOn()).thenReturn(EXPIRY_DATE);
		assertTrue(recommendationService.isRecommendationExpired(recommendationModel));
	}

}
