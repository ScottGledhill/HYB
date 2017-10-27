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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for conversion of {@link CMSItemModel}
 */
public class DefaultCmsItemModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CMSItemModel> 
{

	private Converter<CMSItemModel, ItemData> cmsItemModelItemDataConverter;
	private CMSAdminItemService cmsAdminItemService;
	
	@Override
	public String getItemType()
	{
		return CMSItemModel._TYPECODE;
	}

	@Override
	public ItemData convert(final CMSItemModel cmsItemModel) 
	{
		return getCmsItemModelItemDataConverter().convert(cmsItemModel);
	}

	@Override
	public CMSItemModel convert(final ItemData itemData)
	{
		checkArgument(itemData != null, "itemData must not be null");
		checkArgument(StringUtils.isNoneBlank(itemData.getItemId()), "itemData.itemId must not be null or empty");
		
		try
		{
			return getCmsAdminItemService().findByUid(itemData.getItemId());
		}
		catch (CMSItemNotFoundException e)
		{
			throw new UnknownIdentifierException(format("could not find ItemModel with uid %s", itemData.getItemId()));
		}
	}

	protected Converter<CMSItemModel, ItemData> getCmsItemModelItemDataConverter()
	{
		return cmsItemModelItemDataConverter;
	}

	@Required
	public void setCmsItemModelItemDataConverter(final Converter<CMSItemModel, ItemData> cmsItemModelItemDataConverter)
	{
		this.cmsItemModelItemDataConverter = cmsItemModelItemDataConverter;
	}
	
	@Required
	public void setCmsAdminItemService(CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}
	
	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}
}


