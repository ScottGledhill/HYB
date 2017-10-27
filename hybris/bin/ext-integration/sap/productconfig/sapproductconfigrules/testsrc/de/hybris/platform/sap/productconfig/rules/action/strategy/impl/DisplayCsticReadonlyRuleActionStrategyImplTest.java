/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayCsticReadonlyRuleActionStrategyImplTest
{

	private DisplayCsticReadonlyRuleActionStrategyImpl classUnderTest;
	private AbstractRuleActionRAO action;
	private ConfigModel model;
	private CsticModel cstic;

	@Before
	public void setUp()
	{
		classUnderTest = new DisplayCsticReadonlyRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		model = ConfigurationRulesTestData.createConfigModelWithCstic();
		cstic = model.getRootInstance().getCstics().get(0);

		action = new AbstractRuleActionRAO();
		ConfigurationRulesTestData.setCsticAsActionTarget(action, cstic.getName());

		cstic.setReadonly(false);
	}

	@Test
	public void testEsxecuteAction()
	{
		final boolean configChanged = classUnderTest.executeAction(model, action);
		assertFalse(configChanged);
		assertTrue(cstic.isReadonly());
	}

	@Test
	public void testEsxecuteAction_alreadyReadOnly()
	{
		cstic.setReadonly(true);
		final boolean configChanged = classUnderTest.executeAction(model, action);
		assertFalse(configChanged);
		assertTrue(cstic.isReadonly());
	}

	@Test
	public void testIsActionPossible()
	{
		final boolean actionPossible = classUnderTest.isActionPossible(model, action);
		assertTrue(actionPossible);
	}

	@Test
	public void testIsActionPossible_csticDoesNotExist()
	{
		cstic.setName("anotherName");
		final boolean actionPossible = classUnderTest.isActionPossible(model, action);
		assertFalse(actionPossible);
	}
}
