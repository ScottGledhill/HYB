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
package de.hybris.platform.sap.productconfig.rules.conditions;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;

import java.util.List;


/**
 * Creates the intermediate representation of the current Configurable Product condition
 */
public class RuleConfigurableProductConditionTranslator extends RuleConfigurableProductBaseConditionTranslator
{
	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition) throws RuleCompilerException
	{
		// Parameters
		final String product = getProduct(condition);
		if (product == null || product.trim().isEmpty())
		{
			return new RuleIrFalseCondition();
		}

		final String cstic = getCstic(condition);
		final String csticValue = getCsticValue(condition, cstic);
		final boolean valueOperatorContains = getValueOeratorContains(condition);

		// Prepare Product Configuration relevant conditions
		final List<RuleIrCondition> irConditions = prepareProductConfigurationConditions(context, product, cstic, csticValue,
				valueOperatorContains, Boolean.FALSE);

		// Group Condition
		final RuleIrGroupCondition irResultCondition = new RuleIrGroupCondition();
		irResultCondition.setOperator(RuleIrGroupOperator.AND);
		irResultCondition.setChildren(irConditions);

		return irResultCondition;
	}

}
