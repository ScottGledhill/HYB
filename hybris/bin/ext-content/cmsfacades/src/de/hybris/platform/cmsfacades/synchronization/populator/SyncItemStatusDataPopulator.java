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
package de.hybris.platform.cmsfacades.synchronization.populator;

import de.hybris.platform.cmsfacades.data.ItemTypeData;
import de.hybris.platform.cmsfacades.data.SyncItemInfoJobStatusData;
import de.hybris.platform.cmsfacades.data.SyncItemStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;

/**
 * Basic populator for the {@link SyncItemStatusData} from {@link SynchronizationItemDetailsData}. 
 * The {@link SynchronizationItemDetailsData} class is the base class returned from 
 * {@link de.hybris.platform.cmsfacades.synchronization.service.ItemSynchronizationService#getSynchronizationItemStatus(SyncRequestData, ItemModel)}
 * whereas {@link SyncItemStatusData} is the base class returned from the execution of 
 * {@link de.hybris.platform.cmsfacades.synchronization.service.ItemSynchronizationService#getSynchronizationItemStatus(SyncRequestData, ItemModel)}.
 * 
 */
public class SyncItemStatusDataPopulator implements Populator<SynchronizationItemDetailsData, SyncItemStatusData>
{
	private Converter<SyncItemInfoJobStatusData, ItemTypeData> itemTypeConverter;
	
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	
	@Override
	public void populate(final SynchronizationItemDetailsData source,
			final SyncItemStatusData target) throws ConversionException
	{
		getUniqueItemIdentifierService().getItemData(source.getItem()).ifPresent(itemData -> {
			target.setItemId(itemData.getItemId());
			target.setItemType(itemData.getItemType());
			target.setName(itemData.getName());
		});
		target.setStatus(source.getSyncStatus());


		target.setDependentItemTypesOutOfSync(
				Lists.newArrayList(
						Optional.ofNullable(source.getRelatedItemStatuses()) //
						.orElse(new ArrayList<>()) //
						.stream() //
						.map(syncItemInfoJobStatusData -> getItemTypeConverter().convert(syncItemInfoJobStatusData)) //
						.collect(Collectors.toMap(o -> o.getItemType(), o -> o, (itemType1, itemType2) -> itemType1)) //
						.values()));
		

		Optional.ofNullable(source.getLastSyncStatusDate()) //
				.ifPresent(date -> target.setLastSyncStatus(date.getTime()));
	}

	protected Converter<SyncItemInfoJobStatusData, ItemTypeData> getItemTypeConverter()
	{
		return itemTypeConverter;
	}

	@Required
	public void setItemTypeConverter(final Converter<SyncItemInfoJobStatusData, ItemTypeData> itemTypeConverter)
	{
		this.itemTypeConverter = itemTypeConverter;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
