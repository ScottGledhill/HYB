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
package com.sap.hybris.reco.be;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;

import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.dao.ImpressionContext;
import com.sap.hybris.reco.dao.InteractionContext;
import com.sap.hybris.reco.dao.ProductRecommendationData;

import java.util.List;
import java.util.Map;


/**
 * Product Recommendation Manager Backend
 */
public interface ProductRecommendationManagerBackend extends BackendBusinessObject
{
	/**
	 * Get Product Recommendations from hybris Marketing based on current context
	 *
	 * @param context
	 * @return List<ProductRecommendation>
	 */
	public List<ProductRecommendationData> getProductRecommendation(final RecommendationContext context, final Map<String, Object> recommendationScenario);
	
	/**
	 * Post a recommendation impression, when a buffered recommendation is done.
	 * 
	 * @param context
	 */
	public void postImpression(ImpressionContext context);

	/**
	 * Post the user interactions to the hybris Marketing.
	 *
	 * @param context
	 */
	public void postInteraction(InteractionContext context);

	/**
	 * Check if the backend system is responding
	 * 
	 * @return boolean
	 */
	public boolean pingBackendSystem();
}
