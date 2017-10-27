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
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleValueOperator;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implements common basic operations for Product Configuration translators
 */
public abstract class RuleConfigurableProductBaseConditionTranslator implements RuleConditionTranslator
{

	// params
	public static final String PRODUCT_PARAM = "product";
	public static final String CSTIC_PARAM = "cstic";
	public static final String CSTIC_VALUE_PARAM = "cstic_value";
	public static final String VALUE_OPERATOR_PARAM = "value_operator";

	public static final String PRODUCT_CONFIGURATION_RAO_CODE_ATTRIBUTE = "productCode";
	public static final String PRODUCT_CONFIGURATION_RAO_IN_CART_ATTRIBUTE = "inCart";
	public static final String PRODUCT_CONFIGURATION_RAO_CSTICS_ATTRIBUTE = "cstics";
	public static final String CSTIC_RAO_NAME_ATTRIBUTE = "csticName";
	public static final String CSTIC_RAO_ASSIGNED_VALUES_ATTRIBUTE = "assignedValues";
	public static final String CSTIC_VALUE_RAO_NAME_ATTRIBUTE = "csticValueName";

	private ProductConfigRuleFormatTranslator rulesFormator;

	protected List<RuleIrCondition> prepareProductConfigurationConditions(final RuleCompilerContext context, final String product,
			final String cstic, final String csticValue, final boolean valueOperatorContains, final Boolean inCart)
	{

		final List<RuleIrCondition> irConditions = new ArrayList<>();

		// Variables
		final String productConfigurationRaoVariable = context.generateVariable(ProductConfigRAO.class);

		if (cstic != null && csticValue != null)
		{
			// Variables
			final String csticValueRaoVariable = context.generateVariable(CsticValueRAO.class);
			final String csticRaoVariable = context.generateVariable(CsticRAO.class);

			// Cstic Value
			final RuleIrAttributeCondition irCsticValueCondition = new RuleIrAttributeCondition();
			irCsticValueCondition.setVariable(csticValueRaoVariable);
			irCsticValueCondition.setAttribute(CSTIC_VALUE_RAO_NAME_ATTRIBUTE);
			irCsticValueCondition.setOperator(RuleIrAttributeOperator.EQUAL);
			irCsticValueCondition.setValue(csticValue);

			// Cstic
			final RuleIrAttributeCondition irCsticCondition = new RuleIrAttributeCondition();
			irCsticCondition.setVariable(csticRaoVariable);
			irCsticCondition.setAttribute(CSTIC_RAO_NAME_ATTRIBUTE);
			irCsticCondition.setOperator(RuleIrAttributeOperator.EQUAL);
			irCsticCondition.setValue(cstic);

			final RuleIrAttributeRelCondition irCsticCsticValueCategoryRel = new RuleIrAttributeRelCondition();
			irCsticCsticValueCategoryRel.setVariable(csticRaoVariable);
			irCsticCsticValueCategoryRel.setAttribute(CSTIC_RAO_ASSIGNED_VALUES_ATTRIBUTE);
			irCsticCsticValueCategoryRel.setOperator(RuleIrAttributeOperator.CONTAINS);
			irCsticCsticValueCategoryRel.setTargetVariable(csticValueRaoVariable);

			if (valueOperatorContains)
			{
				irConditions.add(irCsticValueCondition);
				irConditions.add(irCsticCondition);
				irConditions.add(irCsticCsticValueCategoryRel);
			}
			else
			{
				final List<RuleIrCondition> irCsticWithoutValueConditions = new ArrayList<>();
				irCsticWithoutValueConditions.add(irCsticValueCondition);
				irCsticWithoutValueConditions.add(irCsticCsticValueCategoryRel);
				final RuleIrNotCondition irNotCondition = new RuleIrNotCondition();
				irNotCondition.setChildren(irCsticWithoutValueConditions);

				irConditions.add(irNotCondition);
				irConditions.add(irCsticCondition);
			}

			// Product Configuration
			final RuleIrAttributeRelCondition irProductConfigurationCsticCategoryRel = new RuleIrAttributeRelCondition();
			irProductConfigurationCsticCategoryRel.setVariable(productConfigurationRaoVariable);
			irProductConfigurationCsticCategoryRel.setAttribute(PRODUCT_CONFIGURATION_RAO_CSTICS_ATTRIBUTE);
			irProductConfigurationCsticCategoryRel.setOperator(RuleIrAttributeOperator.CONTAINS);
			irProductConfigurationCsticCategoryRel.setTargetVariable(csticRaoVariable);

			irConditions.add(irProductConfigurationCsticCategoryRel);
		}

		// Product Configuration
		final RuleIrAttributeCondition irProductConfigurationCondition = new RuleIrAttributeCondition();
		irProductConfigurationCondition.setVariable(productConfigurationRaoVariable);
		irProductConfigurationCondition.setAttribute(PRODUCT_CONFIGURATION_RAO_CODE_ATTRIBUTE);
		irProductConfigurationCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irProductConfigurationCondition.setValue(product);

		final RuleIrAttributeCondition irProductConfigurationInCartCondition = new RuleIrAttributeCondition();
		irProductConfigurationInCartCondition.setVariable(productConfigurationRaoVariable);
		irProductConfigurationInCartCondition.setAttribute(PRODUCT_CONFIGURATION_RAO_IN_CART_ATTRIBUTE);
		irProductConfigurationInCartCondition.setOperator(RuleIrAttributeOperator.EQUAL);
		irProductConfigurationInCartCondition.setValue(inCart);

		irConditions.add(irProductConfigurationCondition);
		irConditions.add(irProductConfigurationInCartCondition);

		return irConditions;
	}

	protected String getProduct(final RuleConditionData condition)
	{
		final RuleParameterData productParameter = condition.getParameters().get(PRODUCT_PARAM);
		return processParameter(productParameter);
	}

	protected boolean getValueOeratorContains(final RuleConditionData condition)
	{
		final RuleParameterData valueOperatorParameter = condition.getParameters().get(VALUE_OPERATOR_PARAM);
		return processValueOperatorParameter(valueOperatorParameter);
	}

	protected String getCsticValue(final RuleConditionData condition, final String cstic)
	{
		final RuleParameterData csticValueParameter = condition.getParameters().get(CSTIC_VALUE_PARAM);
		String csticValue = processParameter(csticValueParameter);
		if (cstic != null && csticValue == null)
		{
			csticValue = getRulesFormator().getNoValueIndicator();
		}
		return csticValue;
	}

	protected String getCstic(final RuleConditionData condition)
	{
		final RuleParameterData csticParameter = condition.getParameters().get(CSTIC_PARAM);
		return processParameter(csticParameter);
	}

	protected String processParameter(final RuleParameterData parameter)
	{
		return parameter == null ? null : (String) parameter.getValue();
	}

	protected boolean processValueOperatorParameter(final RuleParameterData valueOperatorParameter)
	{
		return valueOperatorParameter == null
				|| !ProductConfigRuleValueOperator.DOES_NOT_HAVE.equals(valueOperatorParameter.getValue());
	}

	protected ProductConfigRuleFormatTranslator getRulesFormator()
	{
		return rulesFormator;
	}

	@Required
	public void setRulesFormator(final ProductConfigRuleFormatTranslator rulesFormator)
	{
		this.rulesFormator = rulesFormator;
	}

}
