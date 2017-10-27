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
package com.sap.hybris.reco.addon.facade;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;

import java.util.List;

import com.sap.hybris.reco.dao.InteractionContext;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;

/**
 * to do
 */
public interface ProductRecommendationManagerFacade
{
	/**
	 * Get recommended products based on recommendation context
	 * 
	 * @param context
	 * @return list of recommended products
	 */
	public List<ProductReferenceData> getProductRecommendation(RecommendationContext context);

	/**
	 * Get the recommendation components on the provided page
	 * 
	 * @param pageModel
	 * @return recommendation components on the page
	 */
	public List<CMSSAPRecommendationComponentModel> getRecommendationComponentForPage(AbstractPageModel pageModel);

	/**
	 * Post an interaction with a recommendation component
	 * 
	 * @param context
	 */
	public void postInteraction(InteractionContext context);

	/**
	 * Get the logged in User ID
	 *
	 * @return String
	 */
	public String getSessionUserId();

	/**
	 * Is the current Session User Anonymous?
	 * 
	 * @return boolean
	 */
	public boolean isSessionUserAnonymous();
	
	/**
	 * Provide the attributes of the recommendation context 
	 * 
	 * @param userId
	 * @param anonymousUser
	 * @param context
	 * @param component
	 * @param productCode
	 * @param categoryCodes
	 */
	public void populateContext(RecommendationContext context, CMSSAPRecommendationComponentModel component, String productCode, final String userId, final String cookieId);

	/**
	 * Get the anonymous user origin of contact ID
	 * 
	 * @return OriginOfContactId for anonymous user
	 */
	public String getAnonOriginOfContactId();

	/**
	 * Set the anonymous user origin of contact ID
	 * 
	 * @param anonOriginOfContactId
	 */
	public void setAnonOriginOfContactId(String anonOriginOfContactId);

	/**
	 * Store the leading categories in the facade
	 * 
	 * @param categoryCodes
	 */
	public void storeLeadingCategories(List<String> categoryCodes);
	
}
