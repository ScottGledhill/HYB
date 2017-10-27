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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for visiting {@link AbstractPageModel} models for the cms synchronization service to work properly. 
 * In this implementation, it is responsible for collecting all content slots and the page's restrictions. 
 * 
 * @param <PAGETYPE> the page type that extends {@link AbstractPageModel}
 */
public abstract class AbstractPageModelVisitor<PAGETYPE extends AbstractPageModel> implements ItemVisitor<PAGETYPE>
{

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemModel> visit(PAGETYPE source, List<ItemModel> path, Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = newLinkedList();
		collectedItems.addAll(source.getContentSlots().stream().map(contentSlotForPage -> contentSlotForPage.getContentSlot()).collect(toList()));
		collectedItems.addAll(source.getRestrictions());
		return collectedItems;
	}
}
