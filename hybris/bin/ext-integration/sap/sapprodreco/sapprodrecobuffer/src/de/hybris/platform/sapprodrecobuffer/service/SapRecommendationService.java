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
package de.hybris.platform.sapprodrecobuffer.service;

import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;

import java.util.Date;


/**
 *
 */
public interface SapRecommendationService
{
	/**
	 * Get a recommendation
	 *
	 * @param userId
	 * @param scenarioId
	 * @param leadingItems
	 * @return SAPRecommendationBufferModel
	 */
	public SAPRecommendationBufferModel getRecommendation(String userId, String scenarioId, String leadingItems);

	/**
	 * Get a generic recommendation
	 *
	 * @param scenarioId
	 * @param leadingItems
	 * @return SAPRecommendationBufferModel
	 */
	public SAPRecommendationBufferModel getGenericRecommendation(String scenarioId, String leadingItems);

	/**
	 * Retrieve and check if a recommendation is expired
	 *
	 * @param scenarioId
	 * @param hashId
	 * @param leadingItems
	 *
	 * @return true/false
	 */
	public boolean isRecommendationExpired(String scenarioId, String hashId, String leadingItems);

	/**
	 * Check if a recommendation is expired
	 *
	 * @param recommendation
	 *
	 * @return true/false
	 */
	public boolean isRecommendationExpired(SAPRecommendationBufferModel recommendation);

	/**
	 * Remove expired recommendations based on the expiry offset
	 *
	 */
	public void removeExpiredRecommendations();

	/**
	 * Remove expired mappings based on the expiry offset
	 *
	 */
	public void removeExpiredMappings();

	/**
	 * Remove expired mappings based on the expiry offset
	 *
	 */
	public void removeExpiredTypeMappings();

	/**
	 * Add a new recommendation entry
	 *
	 * @param userId
	 * @param scenarioId
	 * @param hashId
	 * @param leadingItems
	 * @param recoList
	 * @param recoType
	 * @param expiresOn
	 */
	public void saveRecommendation(String userId, String scenarioId, String hashId, String leadingItems, String recoList,
			String recoType, Date expiresOn);
}
