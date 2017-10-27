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
import de.hybris.platform.sap.core.odata.util.ODataClientService;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hybris.reco.common.util.HMCConfigurationReader;


/**
 * Recommendation Entity Manager Interface
 */
public interface RecommendationEntityManager extends BackendBusinessObject
{
	/**
	 * Retrieve the list of model types from a remote server
	 *
	 * @param entityName
	 * @param expand
	 * @param select
	 * @param filter
	 * @param orderby
	 *
	 * @return Returns an ODataFeed with the list of model types
	 * @throws ODataException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public ODataFeed getTypes(String entityName, String expand, String select, String filter, String orderby)
			throws ODataException, URISyntaxException, IOException;

	/**
	 * @return clientService
	 */
	public ODataClientService getClientService();

	/**
	 * @param clientService
	 */
	public void setClientService(ODataClientService clientService);

	/**
	 * @return HMCConfiguration
	 */
	public HMCConfigurationReader getConfiguration();

	/**
	 * @param configuration
	 */
	public void setConfiguration(HMCConfigurationReader configuration);
}