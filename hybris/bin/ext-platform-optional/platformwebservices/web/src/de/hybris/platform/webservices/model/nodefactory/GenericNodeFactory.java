/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.webservices.model.nodefactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.webservices.objectgraphtransformer.GenericYNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.YNodeFactory;
import de.hybris.platform.webservices.objectgraphtransformer.YObjectGraphContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeContext;


/**
 * GenericNodeFactory - delegates to the appropriate nodeFactory.
 */
public class GenericNodeFactory extends YNodeFactory
{
	private final YNodeFactory genericFac = new GenericYNodeFactory();

	@Override
	protected ItemModel getModel(final YObjectGraphContext ctx, final NodeContext nodeCtx, final Object dto)
	{
		YNodeFactory nodeFactory = (YNodeFactory) ctx.getServices().getWsUtilService().findNodeFactory(dto.getClass());
		if (nodeFactory == null)
		{
			nodeFactory = genericFac;
		}
		final ItemModel result = nodeFactory.getValue(nodeCtx, dto);
		return result;
	}
}