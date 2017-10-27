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
package de.hybris.platform.cmsfacades.uniqueidentifier;


import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;

import java.util.Optional;

/**
 * Interface definition for getting unique identifiers of Item Models. 
 */
public interface UniqueItemIdentifierService
{

	/**
	 * Method to get the unique identifier and other information (not the PK) about an Item Model.
	 * For CMS Items, that means getting the {@link CMSItemModel#getUid()} and name, but for other Items, 
	 * this could be different.  
	 * @param itemModel the item model used to get the unique identifier. 
	 */
	Optional<ItemData> getItemData(final ItemModel itemModel);

	/**
	 * Method to get the {@link ItemModel} identifier by the given {@link ItemData}
	 * @param itemData the container of item unique identifier and its type
	 */
	Optional<ItemModel> getItemModel(ItemData itemData);
	
}
