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
package de.hybris.platform.cmswebservices.pages.service;

/**
 * performs the necessary initialization that a newly created {@code AbstractPageModel} may require before saving.
 * 
 * @deprecated since version 6.3. Please use {@link de.hybris.platform.cmsfacades.pages.service.PageInitializer} in the
 *             cmsfacades extension instead.
 */
@Deprecated
public interface PageInitializer extends de.hybris.platform.cmsfacades.pages.service.PageInitializer
{

}
