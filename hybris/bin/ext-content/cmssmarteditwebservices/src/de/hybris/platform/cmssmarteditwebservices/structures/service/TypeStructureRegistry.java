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
package de.hybris.platform.cmssmarteditwebservices.structures.service;

import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;

import java.util.Collection;
import java.util.Set;


/**
 * Registry that stores a collection of <code>StructureType</code> elements.
 */
public interface TypeStructureRegistry
{

	/**
	 * Get a specific <code>StructureTypeAttribute</code> by typecode and mode.
	 *
	 * @param typecode
	 *           - the typecode of the element to retrieve from the registry.
	 * @param mode
	 *           - the mode of the element to retrieve from the registry.
	 * @return the element matching the typecode and qualifier; never <tt>null</tt>
	 */
	Set<TypeAttributeStructure> getStructureTypeAttributesByMode(String typecode, String mode);

	/**
	 * Get a specific <code>StructureType</code> by its typecode.
	 *
	 * @param typecode
	 *           - the typecode of the element to retrieve from the registry.
	 * @return the element matching the typecode
	 */
	TypeStructure getTypeStructure(String typecode);

	/**
	 * Get all elements in the registry.
	 *
	 * @return all items in the registry or an empty collection if no elements are found.
	 */
	Collection<TypeStructure> getStructureTypes();

	/**
	 * Get all structure type modes defined for a given type code.
	 *
	 * @param typecode
	 *           - the type code
	 * @return all structure type modes
	 */
	Set<StructureTypeMode> getStructureModes(String typecode);

	/**
	 * Get a specific <code>TypeStructure</code> of an Abstract type by its itemtype.
	 *
	 * @param itemtype
	 *           - the itemtype of the element to retrieve from the registry.
	 * @return the element matching the itemtype
	 */
	TypeStructure getAbstractTypeStructure(String itemtype);

	/**
	 * Get all structure type modes defined for the Abstract type matching the given item type.
	 *
	 * @param itemtype
	 *           - the <code>ComposedTypeModel</code> item type
	 * @return all structure type modes
	 */
	Set<StructureTypeMode> getAbstractStructureModes(String itemtype);

}
