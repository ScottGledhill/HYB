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
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmswebservices.data.CMSParagraphComponentData;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class DefaultStringDecapitalizerTest
{
	
	private DefaultStringDecapitalizer decapitalizer = new DefaultStringDecapitalizer();
	
	@Test
	public void testDecapitalizeNullClassShouldReturnOptionalEmpty()
	{
		final Optional<String> result = decapitalizer.decapitalize((Class) null);
		Assert.assertThat(result, Matchers.is(Optional.empty()));
	}
	
	@Test
	public void testDecapitalizeClassShouldReturnCorrectOptionalString()
	{
		final Optional<String> result = decapitalizer.decapitalize(CMSParagraphComponentData.class);
		Assert.assertThat(result, Matchers.is(Optional.of("cmsParagraphComponentData")));
	}
	
}
