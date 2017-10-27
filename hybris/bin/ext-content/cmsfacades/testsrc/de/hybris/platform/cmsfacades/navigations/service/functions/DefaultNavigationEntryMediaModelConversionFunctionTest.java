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
package de.hybris.platform.cmsfacades.navigations.service.functions;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmswebservices.data.NavigationEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryMediaModelConversionFunctionTest
{

	@Mock
	private MediaService mediaService;

	@InjectMocks
	private DefaultNavigationEntryMediaModelConversionFunction conversionFunction;

	@Test
	public void testApplyNavigationEntryConversion()
	{
		final NavigationEntryData navigationEntry = mock(NavigationEntryData.class);
		conversionFunction.apply(navigationEntry);
		verify(mediaService).getMedia(anyString());
	}

	@Test(expected = ConversionException.class)
	public void testApplyNavigationEntryConversionWithInvalidMedia()
	{
		when(mediaService.getMedia(anyString())).thenThrow(new UnknownIdentifierException(""));
		final NavigationEntryData navigationEntry = mock(NavigationEntryData.class);
		conversionFunction.apply(navigationEntry);
		verify(mediaService).getMedia(anyString());
		fail();
	}
}
