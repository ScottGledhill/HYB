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
package de.hybris.platform.cmsfacades.sites.populator.model;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;
import static org.apache.commons.collections.SetUtils.isEqualSet;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import de.hybris.platform.cmswebservices.data.SiteData;

import java.util.Map;


public class ComparableSiteData extends SiteData
{

	private static final long serialVersionUID = -4009491095049364817L;

	public ComparableSiteData()
	{
		super();
	}

	public ComparableSiteData(final SiteData siteData)
	{
		try
		{
			copyProperties(this, siteData);
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode()
	{
		return reflectionHashCode(this, excludeFields());
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object that)
	{
		return reflectionEquals(this, that, excludeFields())
				&& (that != null && namesEqual(getName(), ((ComparableSiteData) that).getName()));
	}

	public boolean namesEqual(final Map<String, String> thisName, final Map<String, String> thatName)
	{
		return isEqualSet(thatName.entrySet(), thisName.entrySet());
	}

	@Override
	public String toString()
	{
		return reflectionToString(this, MULTI_LINE_STYLE);
	}

	public String[] excludeFields()
	{
		return new String[]
		{ "name" };
	}
}