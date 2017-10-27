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
package de.hybris.platform.cmssmarteditwebservices.structures.facade;

import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;

import java.util.List;


/**
 * Facade for getting structure type information about available CMS types and their attributes.
 */
public interface StructureFacade
{
	/**
	 * Get component type structures for all defined structure type modes.
	 *
	 * @param code
	 *           - the type code of the component type to retrieve
	 * @return the component type structures
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing types
	 */
	List<ComponentTypeData> getComponentTypesByCode(final String code) throws ComponentTypeNotFoundException;

	/**
	 * Get a single component type structure for a given structure type mode.
	 *
	 * @param code
	 *           - the type code of the component type to retrieve
	 * @param mode
	 *           - the mode of the structure type
	 * @return the component type structure
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing types
	 */
	ComponentTypeData getComponentTypeByCodeAndMode(final String code, String mode) throws ComponentTypeNotFoundException;
}