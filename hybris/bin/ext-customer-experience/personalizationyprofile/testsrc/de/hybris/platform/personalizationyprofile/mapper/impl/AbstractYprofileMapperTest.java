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
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationyprofile.Neighbour;
import de.hybris.platform.personalizationyprofile.Relation;
import de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Contains utility methods for testing mappers
 */
public abstract class AbstractYprofileMapperTest
{
	public static final String PRODUCT_1 = "product%2Bcode";
	public static final String PRODUCT_2 = "other%2Bproduct%2Bcode";
	public static final String CATEGORY_1 = "catalog";
	public static final String CATEGORY_2 = "Mega%2BCamera";

	public static final String SEGMENT_1 = "PRODUCT product code";
	public static final String SEGMENT_2 = "PRODUCT other product code";
	public static final String SEGMENT_3 = "CATEGORY catalog";
	public static final String SEGMENT_4 = "CATEGORY Mega Camera";
	public static final String AFFINITY_1 = "0.888";
	public static final String AFFINITY_2 = "0.222";
	public static final String AFFINITY_3 = "0.444";
	public static final String AFFINITY_4 = "0.222";

	public CxCategoryYprofileMapper categoryMapper;
	public CxProductYprofileMapper productMapper;
	public MappingData target;

	public Neighbour product1, product2, category1, category2;


	@Before
	public void setup()
	{
		final ConfigurationService config = Mockito.mock(ConfigurationService.class, Mockito.RETURNS_DEEP_STUBS);

		Mockito.when(config.getConfiguration().getString(Mockito.anyString(), Mockito.anyString())).thenAnswer(new Answer<String>()
		{
			@Override
			public String answer(final InvocationOnMock invocation) throws Throwable
			{
				return (String) invocation.getArguments()[1];
			}
		});

		categoryMapper = new CxCategoryYprofileMapper();
		categoryMapper.setConfigurationService(config);

		productMapper = new CxProductYprofileMapper();
		productMapper.setConfigurationService(config);

		target = new MappingData();
		target.setSegments(new ArrayList<>());

		product1 = createProductAffinity("random", PRODUCT_1, AFFINITY_1);
		product2 = createProductAffinity("random2", PRODUCT_2, AFFINITY_2);

		category1 = createCategory(CATEGORY_1, AFFINITY_3);
		category2 = createCategory(CATEGORY_2, AFFINITY_4);
	}

	private static final String SCHEMA = "https://api.yaas.hybris.com/v1";
	private static final String AFFINITY = "affinity";

	protected List<Neighbour> createProfile(final Neighbour... neighbours)
	{
		final List<Neighbour> result = new ArrayList<>();
		final Neighbour profile = createNeighbour("profile", SCHEMA + PersonalizationyprofileConstants.PRODUCT_NODE);
		profile.getNeighbours().addAll(Arrays.asList(neighbours));
		result.add(profile);
		return result;
	}

	protected Neighbour createProductAffinity(final String aId, final String id, final String affinity)
	{
		final Neighbour neighbour = createNeighbour(aId, SCHEMA + PersonalizationyprofileConstants.PRODUCT_AFFINITY_NODE,
				createAffinityMap(affinity));
		neighbour.getNeighbours().add(createNeighbour(id, SCHEMA + PersonalizationyprofileConstants.PRODUCT_NODE));
		return neighbour;
	}

	protected Neighbour createCategory(final String id, final String affinity)
	{
		final Relation relation = createRelation(SCHEMA + PersonalizationyprofileConstants.PROFILE_CATEGORY_RELATION,
				createAffinityMap(affinity));

		return createNeighbour(id, SCHEMA + PersonalizationyprofileConstants.CATEGORY_NODE, Arrays.asList(relation));
	}

	protected Map<String, String> createAffinityMap(final String affinity)
	{
		final Map<String, String> map = new HashMap<>();
		map.put(AFFINITY, affinity);
		return map;
	}

	protected Neighbour createNeighbour(final String id, final String schema)
	{
		return createNeighbour(id, schema, new HashMap<>(), new ArrayList<>());
	}

	protected Neighbour createNeighbour(final String id, final String schema, final List<Relation> relations)
	{
		return createNeighbour(id, schema, new HashMap<>(), relations);
	}

	protected Neighbour createNeighbour(final String id, final String schema, final Map<String, String> properties)
	{
		return createNeighbour(id, schema, properties, new ArrayList<>());
	}

	protected Neighbour createNeighbour(final String id, final String schema, final Map<String, String> properties,
			final List<Relation> relations)
	{
		final Neighbour n = new Neighbour();
		n.setId(id);
		n.setProperties(properties);
		n.setRelations(relations);
		n.setSchema(schema);
		n.setNeighbours(new ArrayList<>());
		return n;
	}

	protected Relation createRelation(final String schema, final Map<String, String> properties)
	{
		final Relation r = new Relation();

		r.setSchema(schema);
		r.setProperties(properties);

		return r;
	}

	protected void assertCategoryIsEqual(final String id, final String affinity, final MappingData data)
	{
		final Optional<SegmentMappingData> optionalSegment = data.getSegments().stream().filter(s -> id.equals(s.getCode()))
				.findFirst();
		Assert.assertTrue("Missing segment with id " + id, optionalSegment.isPresent());
		Assert.assertTrue("Invalid affinity (" + affinity + "), expected " + optionalSegment.get().getAffinity(),
				new BigDecimal(affinity).compareTo(optionalSegment.get().getAffinity()) == 0);
	}
}
