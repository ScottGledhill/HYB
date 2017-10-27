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
package de.hybris.platform.cmssmarteditwebservices.structures.service.impl;

import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultComponentTypeStructure;
import de.hybris.platform.cmssmarteditwebservices.data.StructureTypeMode;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeAttributeStructure;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Default implementation of <code>StructureType</code>.
 * <p>
 * The attributes should be populated by {@link #getAttributesByMode()}, then using {@link Map#put(Object, Object)} and
 * {@link Set#add(Object)}.
 * </p>
 */
public class DefaultTypeStructure extends DefaultComponentTypeStructure implements TypeStructure
{
	private final Map<StructureTypeMode, Set<TypeAttributeStructure>> attributesByMode = new HashMap<>();

	public DefaultTypeStructure()
	{
		super();
	}

	public DefaultTypeStructure(final ComponentTypeStructure type)
	{
		super(type);
	}

	@Override
	public Map<StructureTypeMode, Set<TypeAttributeStructure>> getAttributesByModeMap()
	{
		return attributesByMode;
	}

}
