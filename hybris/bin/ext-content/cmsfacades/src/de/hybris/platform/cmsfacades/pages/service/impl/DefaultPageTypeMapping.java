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
package de.hybris.platform.cmsfacades.pages.service.impl;

import de.hybris.platform.cmsfacades.pages.service.PageTypeMapping;
import de.hybris.platform.cmswebservices.data.AbstractPageData;


/**
 * Default implementation of <code>PageTypeMapping</code>.
 */
public class DefaultPageTypeMapping implements PageTypeMapping
{
	private String typecode;
	private Class<? extends AbstractPageData> typedata;

	@Override
	public String getTypecode()
	{
		return typecode;
	}

	@Override
	public void setTypecode(final String typecode)
	{
		this.typecode = typecode;
	}

	@Override
	public Class<? extends AbstractPageData> getTypedata()
	{
		return typedata;
	}

	@Override
	public void setTypedata(final Class<? extends AbstractPageData> typedata)
	{
		this.typedata = typedata;
	}

}
