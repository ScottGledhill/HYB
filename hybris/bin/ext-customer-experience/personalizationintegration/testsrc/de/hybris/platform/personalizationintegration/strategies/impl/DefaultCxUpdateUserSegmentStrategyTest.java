/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationintegration.strategies.impl;


import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationintegration.segment.UserSegmentsProvider;
import de.hybris.platform.personalizationintegration.service.CxIntegrationMappingService;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



public class DefaultCxUpdateUserSegmentStrategyTest
{
	private final DefaultCxUpdateUserSegmentStrategy updateUserSegmentStrategy = new DefaultCxUpdateUserSegmentStrategy();
	@Mock
	private CxIntegrationMappingService cxIntegrationMappingService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	CxConfigurationService cxConfigurationService;
	@Mock
	Configuration configuration;
	@Mock
	UserSegmentsProvider provider;
	private List<UserSegmentsProvider> providers;
	@Mock
	UserModel user;
	List<SegmentMappingData> segmentMappingList;
	MappingData expectedMappingData;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		expectedMappingData = new MappingData();
		providers = Arrays.asList(provider);
		updateUserSegmentStrategy.setProviders(Optional.of(providers));
		updateUserSegmentStrategy.setConfigurationService(configurationService);
		updateUserSegmentStrategy.setCxIntegrationMappingService(cxIntegrationMappingService);
		updateUserSegmentStrategy.setCxConfigurationService(cxConfigurationService);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(cxConfigurationService.getConfiguration()).thenReturn(Optional.empty());
		Mockito.when(cxConfigurationService.getConfiguration(Mockito.any())).thenReturn(Optional.empty());
	}

	@Test
	public void updateUserSegmentTest()
	{
		//given
		segmentMappingList = createSegmentMappingList("segment1", "segment2");
		expectedMappingData.setSegments(segmentMappingList);
		Mockito.when(provider.getUserSegments(user)).thenReturn(segmentMappingList);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWithDuplicatedSegmensTest()
	{
		//given
		segmentMappingList = createSegmentMappingList("segment1", "segment1", "segment2", "segment1", "segment3", "segment2");
		Mockito.when(provider.getUserSegments(user)).thenReturn(segmentMappingList);
		final List<SegmentMappingData> expectedList = createSegmentMappingList("segment1", "segment2", "segment3");
		expectedMappingData.setSegments(expectedList);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void testReturnBiggestAffinity()
	{
		//given
		segmentMappingList = createSegmentMappingList("segment1", "segment1");
		segmentMappingList.get(0).setAffinity(BigDecimal.valueOf(0, 5));
		segmentMappingList.get(1).setAffinity(BigDecimal.ONE);
		Mockito.when(provider.getUserSegments(user)).thenReturn(segmentMappingList);
		final List<SegmentMappingData> expectedList = createSegmentMappingList("segment1");
		expectedMappingData.setSegments(expectedList);
		final ArgumentCaptor<MappingData> mappingDataArgument = ArgumentCaptor.forClass(MappingData.class);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				mappingDataArgument.capture(), Mockito.eq(false));
		final MappingData returnedMappingData = mappingDataArgument.getValue();
		Assert.assertNotNull(returnedMappingData);
		Assert.assertNotNull(returnedMappingData.getSegments());
		Assert.assertTrue(returnedMappingData.getSegments().size() == 1);
		Assert.assertEquals("segment1", returnedMappingData.getSegments().get(0).getCode());
		Assert.assertEquals(BigDecimal.ONE, returnedMappingData.getSegments().get(0).getAffinity());
	}

	protected List<SegmentMappingData> createSegmentMappingList(final String... segments)
	{
		final List<SegmentMappingData> segmentMappingList = new ArrayList();
		for (final String segment : segments)
		{
			segmentMappingList.add(createSegmentMapping(segment, BigDecimal.ONE));
		}
		return segmentMappingList;
	}

	protected SegmentMappingData createSegmentMapping(final String segmentCode, final BigDecimal affinity)
	{
		final SegmentMappingData segmentMapping = new SegmentMappingData();
		segmentMapping.setCode(segmentCode);
		segmentMapping.setAffinity(affinity);
		return segmentMapping;
	}

	protected class MappingDataMatcher extends ArgumentMatcher<MappingData>
	{
		MappingData expectedData;

		public MappingDataMatcher(final MappingData expectedData)
		{
			super();
			this.expectedData = expectedData;
		}

		@Override
		public boolean matches(final Object object)
		{
			if (object instanceof MappingData)
			{
				final MappingData mappingData = (MappingData) object;
				if (expectedData.getSegments() == mappingData.getSegments())
				{
					return true;
				}

				if (expectedData.getSegments() == null || expectedData.getSegments().size() != mappingData.getSegments().size())
				{
					return false;
				}
				return expectedData.getSegments().stream()//
						.map(s -> checkIfContains(mappingData, s))//
						.allMatch(contains -> contains == Boolean.TRUE);
			}

			return false;
		}

		private Boolean checkIfContains(final MappingData mappingData, final SegmentMappingData segmentMapping)
		{
			return Boolean.valueOf(mappingData.getSegments().stream()//
					.anyMatch(s -> StringUtils.equals(s.getCode(), segmentMapping.getCode())));
		}
	}
}