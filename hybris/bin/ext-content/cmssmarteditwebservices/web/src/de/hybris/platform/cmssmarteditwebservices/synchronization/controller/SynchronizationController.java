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
package de.hybris.platform.cmssmarteditwebservices.synchronization.controller;

import static de.hybris.platform.cms2.model.pages.AbstractPageModel._TYPECODE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmssmarteditwebservices.data.ItemSynchronizationWsDTO;
import de.hybris.platform.cmsfacades.data.SyncItemStatusConfig;
import de.hybris.platform.cmssmarteditwebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmssmarteditwebservices.data.SynchronizationWsDTO;
import de.hybris.platform.cmsfacades.synchronization.ItemSynchronizationFacade;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to retrieve complex synchronization status for and to perform a synchronization on a given {@link AbstractPageModel}
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam sourceCatalogVersion identifier of the source catalog version
 * @pathparam sourceCatalogVersion identifier of the target catalog version
 */
@Controller
@RequestMapping(value = "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/synchronizations/versions/{targetCatalogVersion}")
public class SynchronizationController
{

	@Autowired
	private ItemSynchronizationFacade itemSynchronizationFacade;
	
	@Autowired
	private SyncItemStatusConfig cmsSyncItemStatusConfig;

	@Autowired
	private DataMapper dataMapper;

	/**
	 * Will build the synchronization status of a {@link AbstractPageModel} page including detailed status of its {@link ContentSlotModel} and their {@link AbstractCMSComponentModel}.
	 * @queryparam catalogId the catalog id
	 * @queryparam versionId the source catalog version from a synchronization perspective
	 * @queryparam targetCatalogVersion the source catalog version from a synchronization perspective
	 * @queryparam pageId the uid of the <pre>AbstractPageModel</pre> form which to retrieve the synchronization status
	 * @return a dto containing the complex synchronization status of the {@link AbstractPageModel} page
	 */
	@RequestMapping(value = "/pages/{pageId}", method = GET)
	@ResponseBody
	public SyncItemStatusWsDTO getSyncStatus(
			@PathVariable final String catalogId,
			@PathVariable final String versionId,
			@PathVariable final String targetCatalogVersion,
			@PathVariable final String pageId)
	{
		ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(pageId);
		itemSynchronizationData.setItemType(_TYPECODE);
		
		SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(versionId);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		
		return getDataMapper().map(getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData, getCmsSyncItemStatusConfig()), SyncItemStatusWsDTO.class);
	}

	/**
	 * Will perform synchronization status on a list of {@link ItemModel} identifier by their {@link ItemSynchronizationWsDTO}
	 * @queryparam catalogId the catalog id
	 * @queryparam sourceCatalogVersion the source catalog version from a synchronization perspective
	 * @queryparam targetCatalogVersion the target catalog version from a synchronization perspective
	 * @bodyparam synchronizationWsDTO the <pre>SynchronizationWsDTO</pre> containing the list of requested synchronizations
	 */
	@RequestMapping(method = POST)
	@ResponseBody
	public void performSync(
			@RequestBody SynchronizationWsDTO synchronizationWsDTO,
			@PathVariable final String catalogId,
			@PathVariable final String versionId,
			@PathVariable final String targetCatalogVersion)
	{
		
		SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(versionId);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		
		SynchronizationData synchronizationData = getDataMapper().map(synchronizationWsDTO, SynchronizationData.class);

		getItemSynchronizationFacade().performItemSynchronization(syncRequestData, synchronizationData);
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
	
	protected SyncItemStatusConfig getCmsSyncItemStatusConfig()
	{
		return cmsSyncItemStatusConfig;
	}

	protected ItemSynchronizationFacade getItemSynchronizationFacade()
	{
		return itemSynchronizationFacade;
	}
}
