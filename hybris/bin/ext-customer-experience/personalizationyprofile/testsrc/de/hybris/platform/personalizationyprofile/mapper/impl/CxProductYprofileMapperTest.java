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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationyprofile.Neighbour;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class CxProductYprofileMapperTest extends AbstractYprofileMapperTest
{

	@Test
	public void testNullInput()
	{
		//given
		final List<Neighbour> source = null;

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testInvalidInput()
	{
		//given
		final List<Neighbour> source = createProfile(category1, category2);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testSingleValidInput()
	{
		//given
		final List<Neighbour> source = createProfile(category1, product2, category2);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertCategoryIsEqual(SEGMENT_2, "1", target);
	}

	@Test
	public void testMultipleValidInput()
	{
		//given
		final List<Neighbour> source = createProfile(product1, category2, product2, category1);

		//when
		productMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertCategoryIsEqual(SEGMENT_1, "1", target);
		assertCategoryIsEqual(SEGMENT_2, "0.25", target);
	}

}
