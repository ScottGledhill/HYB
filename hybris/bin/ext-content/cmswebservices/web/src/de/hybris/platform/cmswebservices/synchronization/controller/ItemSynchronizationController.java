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
package de.hybris.platform.cmswebservices.synchronization.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmswebservices.data.ItemSynchronizationWsDTO;
import de.hybris.platform.cmswebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmswebservices.data.SynchronizationWsDTO;
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
public class ItemSynchronizationController
{

	@Autowired
	private ItemSynchronizationFacade itemSynchronizationFacade;

	@Autowired
	private DataMapper dataMapper;

	/**
	 * Builds the synchronization status of a {@link AbstractPageModel} page.
	 * @queryparam catalogId the catalog id
	 * @queryparam versionId the source catalog version from a synchronization perspective
	 * @queryparam targetCatalogVersion the source catalog version from a synchronization perspective
	 * @queryparam pageId the uid of the <pre>AbstractPageModel</pre> form which to retrieve the synchronization status
	 * @return a dto containing the complex synchronization status of the {@link AbstractPageModel} page
	 */
	@RequestMapping(value = "/pages/{pageId}", method = GET)
	@ResponseBody
	public SyncItemStatusWsDTO getPageSynchronizationStatus(
			@PathVariable final String catalogId,
			@PathVariable final String versionId,
			@PathVariable final String targetCatalogVersion,
			@PathVariable final String pageId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(pageId, AbstractPageModel._TYPECODE);

		SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);
		
		return getDataMapper().map(getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData), SyncItemStatusWsDTO.class);
	}


	/**
	 * Builds the synchronization status of a {@link de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel} slot.
	 * @queryparam catalogId the catalog id
	 * @queryparam versionId the source catalog version from a synchronization perspective
	 * @queryparam targetCatalogVersion the source catalog version from a synchronization perspective
	 * @queryparam slotId the uid of the <pre>ContentSlotModel</pre> form which to retrieve the synchronization status
	 * @return a dto containing the complex synchronization status of the {@link ContentSlotModel} page
	 */
	@RequestMapping(value = "/slots/{slotId}", method = GET)
	@ResponseBody
	public SyncItemStatusWsDTO getSlotSynchronizationStatus(
			@PathVariable final String catalogId,
			@PathVariable final String versionId,
			@PathVariable final String targetCatalogVersion,
			@PathVariable final String slotId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(slotId, ContentSlotModel._TYPECODE);

		SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

		return getDataMapper().map(getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData), SyncItemStatusWsDTO.class);
	}


	/**
	 * Builds the synchronization status of a {@link de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel} component.
	 * @queryparam catalogId the catalog id
	 * @queryparam versionId the source catalog version from a synchronization perspective
	 * @queryparam targetCatalogVersion the source catalog version from a synchronization perspective
	 * @queryparam pageId the uid of the <pre>AbstractCMSComponentModel</pre> form which to retrieve the synchronization status
	 * @return a dto containing the complex synchronization status of the {@link AbstractCMSComponentModel} page
	 */
	@RequestMapping(value = "/items/{componentId}", method = GET)
	@ResponseBody
	public SyncItemStatusWsDTO getComponentSynchronizationStatus(
			@PathVariable final String catalogId,
			@PathVariable final String versionId,
			@PathVariable final String targetCatalogVersion,
			@PathVariable final String componentId)
	{
		final ItemSynchronizationData itemSynchronizationData = getItemSynchronizationData(componentId, AbstractCMSComponentModel._TYPECODE);

		SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);

		return getDataMapper().map(getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData, itemSynchronizationData), SyncItemStatusWsDTO.class);
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

		final SyncRequestData syncRequestData = getSyncRequestData(catalogId, versionId, targetCatalogVersion);
		
		final SynchronizationData synchronizationData = getDataMapper().map(synchronizationWsDTO, SynchronizationData.class);

		getItemSynchronizationFacade().performItemSynchronization(syncRequestData, synchronizationData);
	}

	/**
	 * Returns the synchronization request data 
	 * @param catalogId the content catalog to be synchronized
	 * @param sourceCatalogVersion the source catalog version
	 * @param targetCatalogVersion the target catalog version
	 * @return the synch request data
	 */

	protected SyncRequestData getSyncRequestData(final @PathVariable String catalogId, final @PathVariable String sourceCatalogVersion,
			final @PathVariable String targetCatalogVersion)
	{
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(sourceCatalogVersion);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		return syncRequestData;
	}

	/**
	 * Returns the Item Synchronization Data for a given itemId and item type
	 * @param itemId the item id that will be used as a root for synchronization
	 * @param itemType the item type
	 * @return the item synch data
	 */
	protected ItemSynchronizationData getItemSynchronizationData(final String itemId, final String itemType)
	{
		final ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(itemId);
		itemSynchronizationData.setItemType(itemType);
		return itemSynchronizationData;
	}

	protected ItemSynchronizationFacade getItemSynchronizationFacade()
	{
		return itemSynchronizationFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}
}
