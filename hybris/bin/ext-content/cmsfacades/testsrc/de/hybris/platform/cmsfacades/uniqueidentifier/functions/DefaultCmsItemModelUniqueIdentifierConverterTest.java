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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCmsItemModelUniqueIdentifierConverterTest
{

	private static final java.lang.String UID = "uid";

	@Mock
	private CMSAdminItemService cmsAdminItemService;

	@Mock
	private Converter<CMSItemModel, ItemData> cmsItemModelItemDataConverter;

	@InjectMocks
	private DefaultCmsItemModelUniqueIdentifierConverter conversionFunction;
	
	@Mock
	private ItemData itemData;
	@Mock
	private CMSItemModel itemModel;

	@Before
	public void setup() throws CMSItemNotFoundException 
	{
		when(cmsItemModelItemDataConverter.convert(itemModel)).thenReturn(itemData);
		when(cmsAdminItemService.findByUid(UID)).thenReturn(itemModel);
	}
	
	@Test
	public void testConversionWithCMSItemModelClass()
	{
		final ItemData result = conversionFunction.convert(itemModel);
		assertThat(result, is(itemData));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void whenItemDataIsNullconvertWithCMSItemDataWillThrowException()
	{
		conversionFunction.convert((ItemData)null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void whenItemDataIdIsEmptyconvertWithCMSItemDataWillThrowException()
	{
		conversionFunction.convert(itemData);
	}

	@Test
	public void convertWithCMSItemData()
	{
		when(itemData.getItemId()).thenReturn(UID);
		final ItemModel result = conversionFunction.convert(itemData);
		assertThat(result, is(itemModel));
	}


}
