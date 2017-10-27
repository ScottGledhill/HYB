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
package de.hybris.platform.cmswebservices.navigations.service;

/**
 * Registry that stores a collection of {@link NavigationEntryItemModelConverter}.
 * 
 * @deprecated since version 6.3. Please use
 *             {@link de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry} in the
 *             cmsfacades extension instead.
 */
@Deprecated
public interface NavigationEntryConverterRegistry
		extends de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry
{

}
