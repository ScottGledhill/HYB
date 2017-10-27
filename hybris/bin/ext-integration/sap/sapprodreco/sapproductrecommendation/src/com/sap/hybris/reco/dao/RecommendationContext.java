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
package com.sap.hybris.reco.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;


/**
 * Recommendation Context
 */
public class RecommendationContext
{
	private String leadingProductId;
	private List<String> leadingCategoryIds;
	private String leadingItemType;
	private String leadingItemDSType;
	private String cartItemDSType;
	private boolean includeCart;
	private boolean includeRecent;
	private String userId;
	private String userType;
	private String recotype;
	private String cookieId;

	private final List<String> cartItems = new ArrayList<String>();
	private CMSSAPRecommendationComponentModel componentModel;

	/**
	 * @return leadingProductId
	 */
	public String getLeadingProductId()
	{
		return leadingProductId;
	}
	
	/**
	 * @return the leadingCategoryId
	 */
	public List<String> getLeadingCategoryIds()
	{
		return leadingCategoryIds;
	}

	/**
	 * @param leadingCategoryId the leadingCategoryId to set
	 */
	public void setLeadingCategoryIds(List<String> leadingCategoryIds)
	{
		this.leadingCategoryIds = leadingCategoryIds;
	}
	
	/**
	 * @param leadingCategoryId - the category ID to be added
	 * 
	 */
	public void addLeadingCategoryId(String leadingCategoryId)
	{
		this.leadingCategoryIds.add(leadingCategoryId);
	}

	/**
	 * @return userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @return cartItems
	 */
	public List<String> getCartItems()
	{
		return cartItems;
	}

	/**
	 * @param cartItem
	 */
	public void addCartItem(final String cartItem)
	{
		cartItems.add(cartItem);
	}

	/**
	 * @param leadingProductId
	 */
	public void setLeadingProductId(final String leadingProductId)
	{
		this.leadingProductId = leadingProductId;
	}

	/**
	 * @param userId
	 */
	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return componentModel
	 */
	public CMSSAPRecommendationComponentModel getComponentModel()
	{
		return componentModel;
	}

	/**
	 * @param componentModel
	 */
	public void setComponentModel(final CMSSAPRecommendationComponentModel componentModel)
	{
		this.componentModel = componentModel;
	}

	/**
	 * @return leadingItemDSType
	 */
	public String getLeadingItemDSType()
	{
		return leadingItemDSType;
	}

	/**
	 * @param leadingItemDSType
	 */
	public void setLeadingItemDSType(final String leadingItemDSType)
	{
		this.leadingItemDSType = leadingItemDSType;
	}

	/**
	 * @return includeCart
	 */
	public boolean isIncludeCart()
	{
		return includeCart;
	}

	/**
	 * @param includeCart
	 */
	public void setIncludeCart(final boolean includeCart)
	{
		this.includeCart = includeCart;
	}

	/**
	 * @return the includeRecent
	 */
	public boolean isIncludeRecent()
	{
		return includeRecent;
	}

	/**
	 * @param includeRecent the includeRecent to set
	 */
	public void setIncludeRecent(boolean includeRecent)
	{
		this.includeRecent = includeRecent;
	}

	/**
	 * @return recotype
	 */
	public String getRecotype()
	{
		return recotype;
	}

	/**
	 * @param recotype
	 */
	public void setRecotype(final String recotype)
	{
		this.recotype = recotype;
	}

	/**
	 * @return userType
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * @param userType
	 */
	public void setUserType(final String userType)
	{
		this.userType = userType;
	}

	/**
	 * @return String
	 */
	public String getSessionKey()
	{
		if (this.getComponentModel() != null)
		{
			return this.getComponentModel().getUid();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @return cookieId
	 */
	public String getCookieId()
	{
		return cookieId;
	}

	/**
	 * @param cookieId
	 */
	public void setCookieId(final String cookieId)
	{
		this.cookieId = cookieId;
	}

	/**
	 * @return cartItemDSType
	 */
	public String getCartItemDSType()
	{
		return cartItemDSType;
	}

	/**
	 * @param cartItemDSType
	 */
	public void setCartItemDSType(final String cartItemDSType)
	{
		this.cartItemDSType = cartItemDSType;
	}

	/**
	 * @return leadingItemType
	 */
	public String getLeadingItemType()
	{
		return leadingItemType;
	}

	/**
	 * @param leadingItemType
	 */
	public void setLeadingItemType(final String leadingItemType)
	{
		this.leadingItemType = leadingItemType;
	}
}
