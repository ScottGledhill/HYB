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
package de.hybris.platform.sapprodrecobuffer.dao;

import de.hybris.platform.sapprodrecobuffer.model.SAPRecoTypeMappingModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;

import java.util.Date;
import java.util.List;


/**
 * An interface for SAP Recommendation DAO
 */
public interface SapRecommendationDao
{
	/**
	 * Find recommendation for given keys
	 *
	 * @param scenarioId
	 * @param hashIds
	 * @param leadingItems
	 * @return a list of recommendation models
	 */
	List<SAPRecommendationBufferModel> findRecommendation(final String scenarioId, final String hashIds,
			final String leadingItems);

	/**
	 * Find hashId for given keys
	 *
	 * @param userId
	 * @param scenarioId
	 *
	 * @return a list of recommnedationMapping models
	 */
	List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId);

	/**
	 * Find recommendation for given keys
	 *
	 * @param userId
	 * @param scenarioId
	 * @param hashId
	 *
	 * @return a list of recommnedationMapping models
	 */
	List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId,
			final String hashId);

	/**
	 *
	 * Find hashId for given keys
	 *
	 * @param recoType
	 * @param scenarioId
	 * @return a list of recoTypeMapping models
	 */
	List<SAPRecoTypeMappingModel> findRecoTypeMapping(String recoType, String scenarioId);


	/**
	 * Find all expired more than 30 days recommendations
	 *
	 * @param expiredDate
	 *
	 * @return a list of expired recommendations
	 */
	List<SAPRecommendationBufferModel> findExpiredRecommendations(Date expiredDate);

	/**
	 * Find all expired more than 30 days recommendation mappings
	 *
	 * @param expiredDate
	 *
	 * @return a list of expired recommendation mappings
	 */
	List<SAPRecommendationMappingModel> findExpiredRecommendationMappings(Date expiredDate);

	/**
	 * Find all expired more than 30 days recommendation type mappings
	 *
	 * @param expiredDate
	 *
	 * @return a list of expired recommendation type mappings
	 */
	List<SAPRecoTypeMappingModel> findExpiredRecoTypeMappings(Date expiredDate);
}
