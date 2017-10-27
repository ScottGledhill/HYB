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
package de.hybris.platform.cmsfacades.common.service;

import java.util.function.Supplier;


/**
 * Service to disable the search restrictions in order to retrieve all data for all catalogs and not only for the catalogs defined
 * in the current session.
 */
public interface SessionSearchRestrictionsDisabler
{
	/**
	 * Execute the supplier function while the search restrictions are disabled.
	 * @param supplier - the function to be executed
	 * @return data returned by the supplier
	 */
	<T> T execute(Supplier<T> supplier);
}
