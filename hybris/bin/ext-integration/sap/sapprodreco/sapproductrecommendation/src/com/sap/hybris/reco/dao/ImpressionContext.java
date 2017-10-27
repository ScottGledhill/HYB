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

/**
 *
 * data structure that holds the interaction data context
 *
 */
public class ImpressionContext
{
	private String scenarioId;
	private int impressionCount = 1; //defaulting to one
	private int itemCount;

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

	public int getImpressionCount()
	{
		return impressionCount;
	}

	public void setImpressionCount(int impressionCount)
	{
		this.impressionCount = impressionCount;
	}

	public int getItemCount()
	{
		return itemCount;
	}

	public void setItemCount(int itemsCount)
	{
		this.itemCount = itemsCount;
	}

}
