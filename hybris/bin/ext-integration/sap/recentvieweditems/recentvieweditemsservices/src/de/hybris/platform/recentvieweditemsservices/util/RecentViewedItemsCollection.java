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
package de.hybris.platform.recentvieweditemsservices.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Object containing queues of recently viewed products and categories *
 */
public class RecentViewedItemsCollection
{

	private final List<String> productCodes;
	private final List<String> categoryCodes;
	private static final int DEFAULT_MAX_ENTRIES = 3;
	private int maxEntries = 0;

	public RecentViewedItemsCollection(final int maxEntries)
	{
		if (maxEntries < 1)
		{
			this.maxEntries = DEFAULT_MAX_ENTRIES;
			throw new IllegalArgumentException(
					"Maximum size not configured properly in backoffice." + " Using default value " + DEFAULT_MAX_ENTRIES);
		}
		this.maxEntries = maxEntries;
		productCodes = new ArrayList<String>(maxEntries);
		categoryCodes = new ArrayList<String>(maxEntries);
	}

	public synchronized void addProductCode(final String code)
	{
		if (!productCodes.contains(code) & StringUtils.isNotBlank(code))
		{
			productCodes.add(0, code);
			while (productCodes.size() > maxEntries)
			{
				productCodes.remove(maxEntries);
			}
		}
	}

	public synchronized void addCategoryCode(final String code)
	{
		if (!categoryCodes.contains(code) & StringUtils.isNotBlank(code))
		{
			categoryCodes.add(0, code);
			while (categoryCodes.size() > maxEntries)
			{
				categoryCodes.remove(maxEntries);
			}
		}
	}

	public List<String> getProductCodes()
	{
		return Collections.unmodifiableList(productCodes);
	}


	public List<String> getCategoryCodes()
	{
		return Collections.unmodifiableList(categoryCodes);
	}
}
