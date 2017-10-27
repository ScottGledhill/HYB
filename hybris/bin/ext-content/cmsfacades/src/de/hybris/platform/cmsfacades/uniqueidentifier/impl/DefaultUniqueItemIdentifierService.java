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
package de.hybris.platform.cmsfacades.uniqueidentifier.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

/**
 * Default implementation for {@link UniqueItemIdentifierService}
 */
public class DefaultUniqueItemIdentifierService implements UniqueItemIdentifierService, InitializingBean
{

	@Autowired
	private TypeService typeService;

	@Autowired
	private Set<UniqueIdentifierConverter> converters;

	private Map<String, UniqueIdentifierConverter> converterMap = new HashMap<>();


	@Override
	public Optional<ItemData> getItemData(final ItemModel itemModel)
	{
		Preconditions.checkNotNull(itemModel, "Item Model cannot be null.");

		final String itemType = itemModel.getItemtype();

		if (StringUtils.isEmpty(itemType))
		{
			return Optional.empty();
		}

		final Optional<ItemData> uniqueIdentifier = Optional.ofNullable(getConverterMap().get(itemType)).map(
				uniqueIdentifierConverter -> uniqueIdentifierConverter.convert(itemModel));

		if (uniqueIdentifier.isPresent())
		{
			return uniqueIdentifier;
		}
		else
		{
			final Set<String> supportedItemTypes = getConverterMap().keySet();
			try
			{
				getTypeService().getComposedTypeForCode(itemType);
			}
			catch (final UnknownIdentifierException e)
			{
				return Optional.empty();
			}

			return getTypeService() //
					.getComposedTypeForCode(itemType) //
					.getAllSuperTypes().stream() //
					.filter(composedType -> supportedItemTypes.contains(composedType.getCode())) //
					.map(composedType -> getConverterMap().get(composedType.getCode())) //
					.map(converter -> converter.convert(itemModel)) //
					.findFirst();
		}
	}

	@Override
	public Optional<ItemModel> getItemModel(final ItemData itemData)
	{
		checkArgument(itemData != null, "Item Data cannot be null.");
		checkArgument(isNotBlank(itemData.getItemId()), "itemId of Item can neither be null nor empty");
		checkArgument(isNotBlank(itemData.getItemType()), "itemType of Item can neither be null nor empty");

		final String itemType = itemData.getItemType();

		final Optional<ItemModel> uniqueItem = Optional.ofNullable(getConverterMap().get(itemType)).map(
				uniqueIdentifierConverter -> uniqueIdentifierConverter.convert(itemData));

		if (uniqueItem.isPresent())
		{
			return uniqueItem;
		}
		else
		{
			final Set<String> supportedItemTypes = getConverterMap().keySet();
			try
			{
				getTypeService().getComposedTypeForCode(itemType);
			}
			catch (final UnknownIdentifierException e)
			{
				return Optional.empty();
			}

			return getTypeService() //
					.getComposedTypeForCode(itemType) //
					.getAllSuperTypes().stream() //
					.filter(composedType -> supportedItemTypes.contains(composedType.getCode())) //
					.map(composedType -> getConverterMap().get(composedType.getCode())) //
					.map(converter -> converter.convert(itemData)) //
					.findFirst();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getConverters()
		.forEach(entry -> getConverterMap().put(entry.getItemType(), entry));
	}

	protected Set<UniqueIdentifierConverter> getConverters()
	{
		return converters;
	}

	public void setConverters(final Set<UniqueIdentifierConverter> converters)
	{
		this.converters = converters;
	}

	public Map<String, UniqueIdentifierConverter> getConverterMap()
	{
		return converterMap;
	}

	public void setConverterMap(final Map<String, UniqueIdentifierConverter> converterMap)
	{
		this.converterMap = converterMap;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

}
