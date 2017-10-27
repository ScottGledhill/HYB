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
package de.hybris.platform.cmsfacades.synchronization.itemcollector;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Collects the direct content slots of a given {@link AbstractPageModel}.  
 */
public class BasicAbstractPageItemCollector implements ItemCollector<AbstractPageModel>
{
	
	@Override
	public List<? extends ItemModel> collect(final AbstractPageModel item)
	{
		return item.getContentSlots().stream().map(ContentSlotForPageModel::getContentSlot).collect(Collectors.toList());
	}
}
