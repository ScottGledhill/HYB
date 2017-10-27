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

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessor;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;


/**
 * {@link RuleIrProcessor} for product configuration. Ensures that the {@link ProcessStep} is propagated to each product
 * config rule implicitly.
 */
public class ProductConfigRuleIrProcessor implements RuleIrProcessor
{

	@Override
	public void process(final RuleCompilerContext context, final RuleIr ruleIr) throws RuleCompilerException
	{
		final AbstractRuleModel sourceRule = context.getRule();

		if (sourceRule instanceof ProductConfigSourceRuleModel)
		{
			// add condition for rule engine result
			final String resultRaoVariable = context.generateVariable(RuleEngineResultRAO.class);

			final RuleIrTypeCondition irResultCondition = new RuleIrTypeCondition();
			irResultCondition.setVariable(resultRaoVariable);

			ruleIr.getConditions().add(irResultCondition);

			// add condition for ProductConfigProcessStep
			final String processStepRaoVariable = context.generateVariable(ProductConfigProcessStepRAO.class);

			final RuleIrTypeCondition irProcessStepCondition = new RuleIrTypeCondition();
			irProcessStepCondition.setVariable(processStepRaoVariable);

			ruleIr.getConditions().add(irProcessStepCondition);
		}
	}
}
