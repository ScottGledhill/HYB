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
package com.sap.hybris.reco.bo.impl;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.service.SapRecommendationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.reco.be.ProductRecommendationManagerBackend;
import com.sap.hybris.reco.bo.ProductRecommendationManagerBO;
import com.sap.hybris.reco.constants.SapproductrecommendationConstants;
import com.sap.hybris.reco.dao.ImpressionContext;
import com.sap.hybris.reco.dao.InteractionContext;
import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.util.ProductRecommendationManagerUtil;


/**
 * Product Recommendation Manager BO Impl
 */
@BackendInterface(ProductRecommendationManagerBackend.class)
public class ProductRecommendationManagerBOImpl extends BusinessObjectBase implements ProductRecommendationManagerBO
{
	private static final Logger LOG = Logger.getLogger(ProductRecommendationManagerBOImpl.class);
	private ProductRecommendationManagerUtil recommendationService;
	private SapRecommendationService recoBufferService;
	private boolean enableCartRemoval;

	/**
	 * Get Product Recommendations from hybris Marketing based on current context
	 *
	 * @param context
	 * @return List<ProductRecommendation>
	 */
	@Override
	public List<ProductRecommendationData> getProductRecommendation(final RecommendationContext context)
	{
		List<ProductRecommendationData> recommendations = new ArrayList<ProductRecommendationData>();

		SAPRecommendationBufferModel bufferedRecommendations;
		//Check if there are buffered recommendations
		if (StringUtils.isNotEmpty(context.getUserId()))
		{
			//If a user ID or a cookie ID is available, attempt to get personalized recommendations
			bufferedRecommendations = recoBufferService.getRecommendation(context.getUserId(), context.getRecotype(),
					this.getLeadingItemsAsString(context));
		}
		else
		{
			//If there is no user ID or cookie ID, get generic recommendations
			bufferedRecommendations = recoBufferService.getGenericRecommendation(context.getRecotype(),
					this.getLeadingItemsAsString(context));
		}

		if (bufferedRecommendations != null)
		{
			//If there are buffered recommendations, check if they are expired
			if (recoBufferService.isRecommendationExpired(bufferedRecommendations))
			{
				//If the buffered recommendations are expired, try getting fresh recommendations from backend (ODATA)
				if (pingRecommendationBackend())
				{
					//if the backend system is available, retrieve the recommendations (ODATA)
					Map<String, Object> recommendationScenario = new HashMap<String, Object>();
					recommendations = getRecommendationsFromBackend(context, recommendationScenario);
					addRecommendationsToBuffer(recommendations, context, recommendationScenario);
				}
				else
				{
					//if backend system is not available, return the expired recommendations anyway
					List<ProductRecommendationData> results = convertFlatListRecommendations(bufferedRecommendations, context);
					postImpression(context, results.size());
					return results;
				}
			}
			else
			{
				//The buffered recommendations are not expired
				List<ProductRecommendationData> results = convertFlatListRecommendations(bufferedRecommendations, context);
				postImpression(context, results.size());
				return results;
			}
		}
		else
		{
			//If there are no buffered recommendations, try getting fresh recommendations from backend (ODATA)
			if (pingRecommendationBackend())
			{
				//if the backend system is available, retrieve the recommendations (ODATA)
				Map<String, Object> recommendationScenario = new HashMap<String, Object>();
				recommendations = getRecommendationsFromBackend(context, recommendationScenario);
				//if recommendations are returned by marketing, update the expired buffered recommendations
				addRecommendationsToBuffer(recommendations, context, recommendationScenario);
			}
			else
			{
				//if backend system is not available, return the generic recommendations
				SAPRecommendationBufferModel genericRecommendations = recoBufferService
						.getGenericRecommendation(context.getRecotype(), this.getLeadingItemsAsString(context));
				if (genericRecommendations != null)
				{
					List<ProductRecommendationData> results = convertFlatListRecommendations(genericRecommendations, context);
					postImpression(context, results.size());
					return results;
				}
				//If the backend is down and there are no generic recommendations, don't return any recommendations
				return null;
			}
		}
		return recommendations;
	}

	private void performCartRemoval(SAPRecommendationBufferModel bufferedRecommendations)
	{
		final List<String> cartList = recommendationService.getCartItemsFromSession();
		
		if (!cartList.isEmpty())
		{
   		final List<String> recoList = new ArrayList<>(Arrays.asList(bufferedRecommendations.getRecoList().split("\\s*,\\s*")));
   		if (recoList.removeAll(cartList))
   		{
   			final String recoCSV = recoList.stream().map(Object::toString).collect(Collectors.joining(","));
   			bufferedRecommendations.setRecoList(recoCSV);
   		}
		}
	}

	private void postImpression(RecommendationContext context, int itemCount)
	{
		ImpressionContext impressionContext = new ImpressionContext();
		impressionContext.setScenarioId(context.getRecotype());
		impressionContext.setItemCount(itemCount);

		try
		{
			getBackendBusinessObject().postImpression(impressionContext);
		}
		catch (final BackendException e)
		{
			LOG.error("Getting Recommendation failed due to BackendException", e);
		}
	}

	private String getLeadingItemsAsString(RecommendationContext context)
	{
		if (context == null)
		{
			return "";
		}
		
		HashSet<String> leadingItems = new HashSet<String>();
		if (context.getLeadingItemType().equals(SapproductrecommendationConstants.PRODUCT))
		{
			if (StringUtils.isNotEmpty(context.getLeadingProductId()))
			{
				leadingItems.add(context.getLeadingProductId());
			}
			if(context.isIncludeCart() && StringUtils.isNotEmpty(context.getCartItemDSType()))
			{
				leadingItems.addAll(recommendationService.getCartItemsFromSession());
			}
		}
		if (context.getLeadingItemType().equals(SapproductrecommendationConstants.CATEGORY))
		{
			if(context.getLeadingCategoryIds().size() > 0)
			{
				leadingItems.addAll(context.getLeadingCategoryIds());
			}
		}
		
		if(context.isIncludeRecent())
		{
			if(recommendationService.getRecentItemsFromSession(context.getLeadingItemType()).size() > 0)
			{
				leadingItems.addAll(recommendationService.getRecentItemsFromSession(context.getLeadingItemType()));
			}
		}
		
		return String.join(",", leadingItems);
	}

	private List<ProductRecommendationData> convertFlatListRecommendations(SAPRecommendationBufferModel bufferedRecommendations,
			RecommendationContext context)
	{
		if (enableCartRemoval)
		{
			performCartRemoval(bufferedRecommendations);
		}
		if (StringUtils.isNotEmpty(bufferedRecommendations.getRecoList()))
		{
			List<ProductRecommendationData> recommendationsArray = new ArrayList<ProductRecommendationData>();
			for (String item : bufferedRecommendations.getRecoList().split("\\s*,\\s*"))
			{
				ProductRecommendationData prod = new ProductRecommendationData();
				prod.setProductCode(item);
				recommendationsArray.add(prod);
			}
			return recommendationsArray;
		}
		return null;
	}

	private void addRecommendationsToBuffer(List<ProductRecommendationData> recommendations, RecommendationContext context,
			final Map<String, Object> recommendationScenario)
	{
		if (recommendations.isEmpty())
		{
			return;
		}

		String hashId = (String) recommendationScenario.get(SapproductrecommendationConstants.HASH_ID);
		if (hashId == null)
		{
			LOG.debug("Recommendation can not be buffered, no hash ID available");
			return;
		}

		String resultScope = recommendationScenario.get(SapproductrecommendationConstants.RESULT_SCOPE).toString();
		GregorianCalendar expiresOn = (GregorianCalendar) recommendationScenario.get(SapproductrecommendationConstants.EXPIRES_ON);

		String recoList = recommendations.stream().map(ProductRecommendationData::getProductCode).collect(Collectors.joining(","));
		recoBufferService.saveRecommendation(context.getUserId(), context.getRecotype(), hashId, this.getLeadingItemsAsString(context),
				recoList, resultScope, expiresOn.getTime());
	}

	private boolean pingRecommendationBackend()
	{
		try
		{
			return getBackendBusinessObject().pingBackendSystem();
		}
		catch (BackendException e)
		{
			LOG.error("Pinging the backend system has failed", e);
			return false;
		}
	}

	private List<ProductRecommendationData> getRecommendationsFromBackend(RecommendationContext context,
			final Map<String, Object> recommendationScenario)
	{
		try
		{
			LOG.info("Retrieveing SAP Recommendation from Backend System");
			return getBackendBusinessObject().getProductRecommendation(context, recommendationScenario);
		}
		catch (final BackendException e)
		{
			LOG.error("Getting Recommendation failed due to BackendException", e);
		}
		return null;
	}

	/**
	 * Posts the user interactions to the hybris Marketing.
	 *
	 * @param context
	 */
	@Override
	public void postInteraction(final InteractionContext context)
	{
		try
		{
			getBackendBusinessObject().postInteraction(context);
		}
		catch (final BackendException e)
		{
			LOG.error("Posting Interaction failed due to BackendException", e);
		}
	}

	@Override
	public ProductRecommendationManagerBackend getBackendBusinessObject() throws BackendException
	{
		return (ProductRecommendationManagerBackend) super.getBackendBusinessObject();
	}

	public ProductRecommendationManagerUtil getRecommendationService()
	{
		return recommendationService;
	}

	public void setRecommendationService(ProductRecommendationManagerUtil recommendationService)
	{
		this.recommendationService = recommendationService;
	}

	public SapRecommendationService getRecoBufferService()
	{
		return recoBufferService;
	}

	public void setRecoBufferService(SapRecommendationService recoBufferService)
	{
		this.recoBufferService = recoBufferService;
	}

	public boolean getEnableCartRemoval()
	{
		return enableCartRemoval;
	}

	public void setEnableCartRemoval(boolean enableCartRemoval)
	{
		this.enableCartRemoval = enableCartRemoval;
	}

}
