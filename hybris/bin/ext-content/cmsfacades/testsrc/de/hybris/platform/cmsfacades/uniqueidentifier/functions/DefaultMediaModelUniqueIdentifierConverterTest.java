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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMediaModelUniqueIdentifierConverterTest
{

	private static final String MEDIA_CODE = "media-code";

	@Mock	
	private Converter<MediaModel, ItemData> mediaModelItemDataConverter;

	@InjectMocks
	private DefaultMediaModelUniqueIdentifierConverter conversionFunction;

	private ItemData itemData = new ItemData();
	
	@Before
	public void setup()
	{
		itemData.setItemId(MEDIA_CODE);
		itemData.setItemType(MediaModel._TYPECODE);
		when(mediaModelItemDataConverter.convert(Mockito.any())).thenReturn(itemData);
	}
	@Test
	public void testConverValidMediaModel()
	{
		final ItemData itemData = conversionFunction.convert(new MediaModel());
		assertThat(itemData.getItemId(), is(MEDIA_CODE));
		assertThat(itemData.getItemType(), is(MediaModel._TYPECODE));
	}

}
