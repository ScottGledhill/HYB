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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractPageModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Map;


/**
 * Concrete implementation of {@link AbstractPageModelVisitor} to visit items of the {@link ContentPageModel} types.
 *
 * Collects the items from {@link AbstractPageModelVisitor#visit(AbstractPageModel, List, Map)}.
 *
 */
public class ContentPageModelVisitor extends AbstractPageModelVisitor<ContentPageModel>
{

	@Override
	public List<ItemModel> visit(ContentPageModel source, List<ItemModel> path, Map<String, Object> ctx)
	{
		return super.visit(source, path, ctx);
	}

}
