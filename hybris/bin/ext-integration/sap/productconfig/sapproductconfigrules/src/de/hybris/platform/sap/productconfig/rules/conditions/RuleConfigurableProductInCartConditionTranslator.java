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
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;

import java.util.List;


/**
 * Creates the intermediate representation of the Configurable Product in the Cart condition
 */
public class RuleConfigurableProductInCartConditionTranslator extends RuleConfigurableProductBaseConditionTranslator
{

	public static final String CART_RAO_ENTRIES_ATTRIBUTE = "entries";
	public static final String ORDER_ENTRY_RAO_PRODUCT_CONFIGURATION_ATTRIBUTE = "productConfiguration";

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
				valueOperatorContains, Boolean.TRUE);

		// Variables
		final String productConfigurationRaoVariable = context.generateVariable(ProductConfigRAO.class);
		final String orderEntryRaoVariable = context.generateVariable(OrderEntryRAO.class);
		final String cartRaoVariable = context.generateVariable(CartRAO.class);

		// Order Entry
		final RuleIrAttributeRelCondition irOrderEntryProductConfigRel = new RuleIrAttributeRelCondition();
		irOrderEntryProductConfigRel.setVariable(orderEntryRaoVariable);
		irOrderEntryProductConfigRel.setAttribute(ORDER_ENTRY_RAO_PRODUCT_CONFIGURATION_ATTRIBUTE);
		irOrderEntryProductConfigRel.setOperator(RuleIrAttributeOperator.EQUAL);
		irOrderEntryProductConfigRel.setTargetVariable(productConfigurationRaoVariable);

		// Cart
		final RuleIrAttributeRelCondition irCartOrderEntryRel = new RuleIrAttributeRelCondition();
		irCartOrderEntryRel.setVariable(cartRaoVariable);
		irCartOrderEntryRel.setAttribute(CART_RAO_ENTRIES_ATTRIBUTE);
		irCartOrderEntryRel.setOperator(RuleIrAttributeOperator.CONTAINS);
		irCartOrderEntryRel.setTargetVariable(orderEntryRaoVariable);


		irConditions.add(irOrderEntryProductConfigRel);
		irConditions.add(irCartOrderEntryRel);

		// Group Condition
		final RuleIrGroupCondition irResultCondition = new RuleIrGroupCondition();
		irResultCondition.setOperator(RuleIrGroupOperator.AND);
		irResultCondition.setChildren(irConditions);

		return irResultCondition;
	}

}
