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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 */
public class ProductAffinityPopulatorTest
{

	private final ProductAffinityPopulator productAffinityPopulator = new ProductAffinityPopulator();

	@Mock
	private ProductFacade productFacade;

	private List<NeighbourData> parsedNeighbours;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		productAffinityPopulator.setProductFacade(productFacade);

		final ObjectMapper jacksonObjectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);

		try
		{
			final InputStream in = getClass().getClassLoader()
					.getResourceAsStream("assistedserviceyprofilefacades/yprofileJSON/productAffinity.json");

			parsedNeighbours = jacksonObjectMapper.readValue(in, new TypeReference<List<NeighbourData>>()
			{
				//
			});

		}

		catch (final JsonParseException e)
		{
			e.printStackTrace();
		}
		catch (final JsonMappingException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void getSingleProductAffinityTest()
	{
		final List<NeighbourData> productAffinityList = parsedNeighbours.stream()
				.filter(n -> n.getSchema().contains(YaasyprofileconnectConstants.SCHEMA_COMMERCE_PRODUCT_AFFINITY))
				.collect(Collectors.toList());

		assertEquals(1, productAffinityList.size());
	}

	@Test
	public void verifyProductAffinityDataTest()
	{
		final List<NeighbourData> productAffinityList = parsedNeighbours.stream()
				.filter(n -> n.getSchema().contains(YaasyprofileconnectConstants.SCHEMA_COMMERCE_PRODUCT_AFFINITY))
				.collect(Collectors.toList());

		final ProductAffinityData productAffinityData = new ProductAffinityData();

		productAffinityPopulator.populate(productAffinityList.get(0), productAffinityData);

		assertEquals("7", productAffinityData.getViewCount());
	}
}
