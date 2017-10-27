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
package de.hybris.platform.cmswebservices.jaxb.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

/**
 *   This Adapter is used to convert multivalued maps {@code Map<String, Map<String, String>>} into XML/JSON objects, and vice-versa,
 *   where the entries are represented as
 *   Key/Value pairs, as opposed to an array representation.
 *   Example:
 *   {@code
 *   	public class Container {
 *   	  private Map<String, Map<String, String>> value;
 *   	}
 *   }
 *   would have the following JSON representation
 *   {@code
 *      {
 *          "value": {
 *              "key1": {"subkey1": "value1", "subkey2": "value2"},
 *              "key2": {"subkey3": "value3", "subkey4": "value4"}
 *          }
 *      }
 *   }
 *
 */
public class MultiValuedObjectMapAdapter extends XmlAdapter<MultiValuedObjectMapAdapter.KeyValueListAdaptedMap, Map<String, Map<String, String>>>
{

	public static class KeyValueListAdaptedMap
	{
		@XmlVariableNode("key")
		List<MultiValuedParentAdaptedEntry> entries = new ArrayList<>();
	}

	public static class MultiValuedParentAdaptedEntry
	{
		@XmlTransient
		String key;

		@XmlVariableNode("key")
		List<MultiValuedAdaptedEntry> entries = new ArrayList<>();
	}

	
	public static class MultiValuedAdaptedEntry
	{
		@XmlTransient
		String key;

		@XmlValue
		String value;
	}
	
	@Override
	public KeyValueListAdaptedMap marshal(Map<String, Map<String, String>> map) throws Exception
	{
		if (map == null)
		{
			return null;
		}
		KeyValueListAdaptedMap adaptedMap = new KeyValueListAdaptedMap();
		map.entrySet().stream()
				.filter(entry -> entry.getValue() != null) //
				.forEach(entry -> { //
					MultiValuedParentAdaptedEntry adaptedEntry = new MultiValuedParentAdaptedEntry(); 
					adaptedEntry.key = entry.getKey();
					
					entry.getValue().entrySet().stream()
							.forEach(o ->  {
								MultiValuedAdaptedEntry e = new MultiValuedAdaptedEntry();
								e.key = o.getKey();
								e.value = o.getValue();
								adaptedEntry.entries.add(e);
							});
					adaptedMap.entries.add(adaptedEntry);
				});
		return adaptedMap;
	}

	@Override
	public Map<String, Map<String, String>> unmarshal(KeyValueListAdaptedMap adaptedMap) throws Exception
	{
		if (adaptedMap == null)
		{
			return null;
		}
		List<MultiValuedParentAdaptedEntry> adaptedEntries = adaptedMap.entries;
		Map<String, Map<String, String>> map = new HashMap<>(adaptedEntries.size());
		for (MultiValuedParentAdaptedEntry adaptedEntry : adaptedEntries)
		{
			Map<String, String> entryMap = new HashMap<>();
			if (adaptedEntry.entries != null)
			{
				adaptedEntry.entries.stream().forEach(e -> entryMap.put(e.key, e.value));
			}
			map.put(adaptedEntry.key, entryMap);
		}
		return map;
	}
}
