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
package de.hybris.platform.cmsfacades.items;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmswebservices.data.AbstractCMSComponentData;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * Component facade interface which deals with methods related to component operations.
 */
public interface ComponentItemFacade
{

	/**
	 * Find all component items.
	 *
	 * @return list of {@link AbstractCMSComponentData}; never <tt>null</tt>
	 */
	List<AbstractCMSComponentData> getAllComponentItems();

	/**
	 * For a given mask used as filter and a {@link PageableData} query, will return a page object consisting of the
	 * content list of the requested page number and the total number of entities for the given mask
	 *
	 * @param mask
	 *           the string value on which components will be filtered, implementations may choose to filter on the
	 *           component name
	 * @param pageableData
	 *           the {@link PageableData} object containing the page request details. PageableData may contain a String
	 *           value for sort, this will be used by a sorting strategy to select the most appropriate query or resort
	 *           to a default one.
	 * @return list of component data fulfilling search query
	 */
	SearchResult<AbstractCMSComponentData> findComponentByMask(String mask, PageableData pageableData);

	/**
	 * Adds a new component instance and assigns it to a slot.
	 *
	 * @param component
	 *           the {@link AbstractCMSComponentData}
	 * @return the {@link AbstractCMSComponentData}
	 * @throws ValidationException
	 *            throws it in case of validation failures.
	 */
	AbstractCMSComponentData addComponentItem(final AbstractCMSComponentData component) throws ValidationException;

	/**
	 * Remove an existing component instance.
	 *
	 * @param componentUid
	 *           - the uid of the cms component
	 * @throws CMSItemNotFoundException
	 *            - when no component exists for the given UID
	 */
	void removeComponentItem(final String componentUid) throws CMSItemNotFoundException;

	/**
	 * Get a abstract component item by uid.
	 *
	 * @param uid
	 *           the uid of the cms component
	 * @return AbstractCMSComponentData the {@link AbstractCMSComponentData}
	 * @throws CMSItemNotFoundException
	 *            when the component is unable to be found from the id
	 */
	AbstractCMSComponentData getComponentItemByUid(final String uid) throws CMSItemNotFoundException;

	/**
	 * Update a component item.
	 *
	 * @param componentUid
	 *           - the uid of the cms component
	 * @param componentData
	 *           - the dto containing component attribute values
	 * @throws CMSItemNotFoundException
	 *            - when the component is unable to be found from the id
	 */
	void updateComponentItem(final String componentUid, final AbstractCMSComponentData componentData)
			throws CMSItemNotFoundException;
}
