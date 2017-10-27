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
package de.hybris.platform.cmsfacades.restrictions.impl;


import de.hybris.platform.cmswebservices.data.AbstractRestrictionData;

import java.util.Comparator;


/**
 * Comparator that will allow to sort restrictions by name in ascending order.
 */
public class DefaultRestrictionComparator implements Comparator<AbstractRestrictionData>
{

	@Override
	public int compare(final AbstractRestrictionData restriction1, final AbstractRestrictionData restriction2)
	{
		return restriction1.getName().compareTo(restriction2.getName());
	}

}
