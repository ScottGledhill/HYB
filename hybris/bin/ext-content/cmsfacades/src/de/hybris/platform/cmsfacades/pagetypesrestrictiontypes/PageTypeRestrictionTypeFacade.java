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
package de.hybris.platform.cmsfacades.pagetypesrestrictiontypes;


import de.hybris.platform.cmswebservices.data.PageTypeRestrictionTypeData;

import java.util.List;


/**
 * Facade for managing pageTypes-restrictionTypes relations.
 */
public interface PageTypeRestrictionTypeFacade
{

	/**
	 * Get a list of restrictions types for all page types.
	 *
	 * @return list of page types - restriction types pairs; never <tt>null</tt>
	 */
	List<PageTypeRestrictionTypeData> getRestrictionTypesForAllPageTypes();

}
