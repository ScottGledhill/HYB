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

import java.util.HashMap;


/**
 *
 * data structure that holds the interaction data context
 *
 */
public class InteractionContext
{
	private String scenarioId;
	private String userId;
	private String userType;
	private String timestamp;
	private String interactionType;
	private String productId;
	private String productType;
	private String productNavURL;
	private String productImageURL;
	private String sourceObjectId;

	private HashMap<String, String> products;


	/**
	 * @return userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId
	 */
	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return timeStamp
	 */
	public String getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setTimestamp(final String timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @return interactionType
	 */
	public String getInteractionType()
	{
		return interactionType;
	}

	/**
	 * @param interactionType
	 */
	public void setInteractionType(final String interactionType)
	{
		this.interactionType = interactionType;
	}

	/**
	 * @return productId
	 */
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @param productId
	 */
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @return products
	 */
	public HashMap<String, String> getProducts()
	{
		return products;
	}

	/**
	 * @param products
	 */
	public void setProducts(final HashMap<String, String> products)
	{
		this.products = products;
	}

	/**
	 * @return scenarioId
	 */
	public String getScenarioId()
	{
		return scenarioId;
	}

	/**
	 * @param scenarioId
	 */
	public void setScenarioId(final String scenarioId)
	{
		this.scenarioId = scenarioId;
	}

	/**
	 * @return productType
	 */
	public String getProductType()
	{
		return productType;
	}

	/**
	 * @param productType
	 */
	public void setProductType(final String productType)
	{
		this.productType = productType;
	}

	/**
	 * @return URL for product image
	 */
	public String getProductImageURL()
	{
		return productImageURL;
	}

	/**
	 * @param productImageURL
	 */
	public void setProductImageURL(final String productImageURL)
	{
		this.productImageURL = productImageURL;
	}

	/**
	 * @return URL for product page
	 */
	public String getProductNavURL()
	{
		return productNavURL;
	}

	/**
	 * @param productNavURL
	 */
	public void setProductNavURL(final String productNavURL)
	{
		this.productNavURL = productNavURL;
	}

	/**
	 * @param userType
	 */
	public void setUserType(final String userType)
	{
		this.userType = userType;

	}

	/**
	 * @return userType
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * @return the sourceObjectId
	 */
	public String getSourceObjectId()
	{
		return sourceObjectId;
	}

	/**
	 * @param sourceObjectId
	 *           the sourceObjectId to set
	 */
	public void setSourceObjectId(final String sourceObjectId)
	{
		this.sourceObjectId = sourceObjectId;
	}

}