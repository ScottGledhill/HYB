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
package de.hybris.platform.cmswebservices.populator;

/**
 * CMS Populator Factory will be responsible for retrieving the populator for a specific CMS Component Model.
 *
 * @deprecated since version 6.3. Please use
 *             {@link de.hybris.platform.cmsfacades.items.populator.ComponentDataPopulatorFactory} in the cmsfacades
 *             extension instead.
 */
@Deprecated
public interface ComponentDataPopulatorFactory extends de.hybris.platform.cmsfacades.items.populator.ComponentDataPopulatorFactory
{

}
