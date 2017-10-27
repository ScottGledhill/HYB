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
package com.sap.hybris.reco.bo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hybris.reco.be.RecommendationEntityManager;
import com.sap.hybris.reco.dao.SAPRecommendationItemDataSourceType;


/**
 * To fetch a list of model types from PRI using OData service
 */
public class SAPRecommendationItemDSTypeReader
{
	private static final String ENTITY_NAME = "ItemSourceTypes";
	private static final String ITEMSOURCE_TYPE_DESCRIPTION = "ItemSourceTypeDescription";
	private static final String ITEMSOURCE_OBJECT_TYPE = "ItemSourceObjectType";
	protected RecommendationEntityManager accessBE;


	/**
	 * @return a list of SAPRecommendationItemDataSourceType
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public List<SAPRecommendationItemDataSourceType> getAllItemDSTypes() throws ODataException, URISyntaxException, IOException
	{
		final ODataFeed feed = accessBE.getTypes(ENTITY_NAME, null, null, null, null);
		final List<ODataEntry> foundEntries = feed.getEntries();
		return extractItemDSTypes(foundEntries);
	}

	/**
	 * extracts the DS Types for items
	 */
	private List<SAPRecommendationItemDataSourceType> extractItemDSTypes(final List<ODataEntry> foundEnties)
	{
		final List<SAPRecommendationItemDataSourceType> itemDSTypes = new ArrayList<SAPRecommendationItemDataSourceType>();
		if (foundEnties != null)
		{
			for(ODataEntry entry : foundEnties)
			{
				itemDSTypes.add(extractItemDSType(entry));
			}
		}
		return itemDSTypes;
	}

	/**
	 * extracts the DS Type for an item
	 */
	private SAPRecommendationItemDataSourceType extractItemDSType(final ODataEntry entry)
	{
		final SAPRecommendationItemDataSourceType itemDSType = new SAPRecommendationItemDataSourceType();
		final Map<String, Object> props = entry.getProperties();
		if (props != null && props.size() > 0)
		{
			itemDSType.setId(props.get(ITEMSOURCE_OBJECT_TYPE).toString());
			itemDSType.setDescription(props.get(ITEMSOURCE_TYPE_DESCRIPTION).toString());
		}
		return itemDSType;
	}

	/**
	 * @return accessBE
	 */
	public RecommendationEntityManager getAccessBE()
	{
		return accessBE;
	}

	/**
	 * @param accessBE
	 */
	public void setAccessBE(final RecommendationEntityManager accessBE)
	{
		this.accessBE = accessBE;
	}

}