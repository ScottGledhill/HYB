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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.recentvieweditemsservices.RecentViewedItemsService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.reco.common.util.HMCConfigurationReader;
import com.sap.hybris.reco.constants.SapproductrecommendationConstants;
import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;

/**
 * Utility Class For Product Recommendation
 *
 */
public class ProductRecommendationManagerUtil
{

	protected HMCConfigurationReader configuration;
	private SessionService sessionService;
	private CartService cartService;
	private ProductService productService;
	private RecentViewedItemsService recentViewedItemsService;
	
	@PostConstruct
	public void init()
	{

	}
	
	/**
	 * @param productId
	 * @return Product Recommendation Data
	 */
	public ProductRecommendationData createProductRecommedation(final String productId)
	{
		try
		{
			final ProductRecommendationData productRecommendationData = new ProductRecommendationData();
			productRecommendationData.setProductCode(productId);
			return productRecommendationData;
		}
		catch (final UnknownIdentifierException exception)
		{
			return null;
		}
	}

	/**
	 * @param context
	 * @return list of leading item ids
	 */
	public List<String> getLeadingItemId(final RecommendationContext context)
	{
		final List<String> leadingItems = new ArrayList<String>();

		if (context != null)
		{
			if (context.getLeadingItemType().equals(SapproductrecommendationConstants.CATEGORY))
			{
				for(String catId: context.getLeadingCategoryIds())
				{
					if(StringUtils.isNotEmpty(catId))
					{
						leadingItems.add(catId);
					}
				}
			}
			else if (context.getLeadingItemType().equals(SapproductrecommendationConstants.PRODUCT))
			{
				if(StringUtils.isNotEmpty(context.getLeadingProductId()))
				{
					leadingItems.add(context.getLeadingProductId());
				}
			}
		}
		return leadingItems;
	}

	/**
	 * Fetches the recent items from session
	 *
	 * @return cartItems
	 */
	public List<String> getRecentItemsFromSession(final String leadingItemType)
	{
		if(leadingItemType.equals(SapproductrecommendationConstants.PRODUCT)) 
		{
			return this.getRecentViewedItemsService().getRecentViewedProducts();
		}
		if (leadingItemType.equals(SapproductrecommendationConstants.CATEGORY))
		{
			return this.getRecentViewedItemsService().getRecentViewedCategories();
		}
		return null;
	}
	
	/**
	 * Fetches the cart items from session
	 *
	 * @return cartItems
	 */
	public List<String> getCartItemsFromSession()
	{
		return this.getCartService().getSessionCart().getEntries().stream() //
				.map(AbstractOrderEntryModel::getProduct) //
				.map(ProductModel::getCode) //
				.collect(Collectors.toList());
	}
	
	/**
	 * @return cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	/**
	 * @return productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return configuration
	 */
	public HMCConfigurationReader getConfiguration()
	{
		return configuration;
	}

	/**
	 * @param configuration
	 */
	public void setConfiguration(final HMCConfigurationReader configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * @return sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the recentViewedItemsService
	 */
	public RecentViewedItemsService getRecentViewedItemsService()
	{
		return recentViewedItemsService;
	}

	/**
	 * @param recentViewedItemsService the recentViewedItemsService to set
	 */
	public void setRecentViewedItemsService(RecentViewedItemsService recentViewedItemsService)
	{
		this.recentViewedItemsService = recentViewedItemsService;
	}
	
	/**
	 * @param compUid
	 * @return Key to store recommendation results in session for component
	 */
	public String getComponentSessionKey(final String compUid)
	{
		return getSessionService().getCurrentSession().getSessionId() + SapproductrecommendationConstants.UNDERSCORE + compUid;
	}

}