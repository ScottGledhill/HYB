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
package de.hybris.platform.sap.productconfig.rules.compiler.processors;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRuleIrProcessorTest
{
	public static final String RESULT_RAO_VARIABLE_ = "resultRaoVariable";
	public static final String PROCESS_STEP_RAO_VARIABLE_ = "processStepRaoVariable";

	private ProductConfigRuleIrProcessor classUnderTest;
	private RuleIr ruleIr;
	private ProductConfigSourceRuleModel sourceRule;

	@Mock
	private RuleCompilerContext context;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleIr = new RuleIr();
		ruleIr.setConditions(new ArrayList<RuleIrCondition>());
		classUnderTest = new ProductConfigRuleIrProcessor();

		sourceRule = new ProductConfigSourceRuleModel();
		given(context.getRule()).willReturn(sourceRule);
		given(context.generateVariable(RuleEngineResultRAO.class)).willReturn(RESULT_RAO_VARIABLE_);
		given(context.generateVariable(ProductConfigProcessStepRAO.class)).willReturn(PROCESS_STEP_RAO_VARIABLE_);
	}

	@Test
	public void testProcess() throws Exception
	{
		classUnderTest.process(context, ruleIr);

		final RuleIrTypeCondition resultCondition = (RuleIrTypeCondition) ruleIr.getConditions().get(0);
		final RuleIrTypeCondition processStepCondition = (RuleIrTypeCondition) ruleIr.getConditions().get(1);

		assertEquals(RESULT_RAO_VARIABLE_, resultCondition.getVariable());
		assertEquals(PROCESS_STEP_RAO_VARIABLE_, processStepCondition.getVariable());
	}
}
