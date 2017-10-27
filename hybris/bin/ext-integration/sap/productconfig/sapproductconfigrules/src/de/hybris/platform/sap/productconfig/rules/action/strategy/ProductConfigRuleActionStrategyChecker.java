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
package de.hybris.platform.sap.productconfig.rules.action.strategy;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * helper class for rule based action strategies. provides common used functionality
 */
public interface ProductConfigRuleActionStrategyChecker
{

	/**
	 * extracts the cstic from action raos
	 */
	CsticModel getCstic(ConfigModel model, AbstractRuleActionRAO action);

	/**
	 * checks whether the provided cstic is valid in context of the given model
	 */
	boolean checkCsticPartOfModel(ConfigModel model, AbstractRuleActionRAO action, String actionDescription);

	/**
	 * checks whether the given value is already assigned
	 */
	boolean checkValueUnassigned(ConfigModel model, AbstractRuleActionRAO action, String value, String actionDescription);

	/**
	 * checks whether the given value can be assigned
	 */
	boolean checkValueAssignable(ConfigModel model, AbstractRuleActionRAO action, String value, String actionDescription);

	/**
	 * checks whether the value can be parsed/formatted
	 */
	boolean checkValueForamtable(ConfigModel model, AbstractRuleActionRAO action, String valueToSet,
			ProductConfigRuleFormatTranslator rulesFormator, String actionDescription);

}
