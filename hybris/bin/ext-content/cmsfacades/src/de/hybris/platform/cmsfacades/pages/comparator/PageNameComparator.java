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
package de.hybris.platform.cmsfacades.pages.comparator;


import de.hybris.platform.cmswebservices.data.AbstractPageData;

import java.util.Comparator;


/**
 * Compare {@link AbstractPageData} by name and sort by ascending (alphabetical) order.
 */
public class PageNameComparator implements Comparator<AbstractPageData>
{

	@Override
	public int compare(final AbstractPageData page1, final AbstractPageData page2)
	{
		return page1.getName().compareTo(page2.getName());
	}

}
