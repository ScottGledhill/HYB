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

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


public class HideCsticRuleActionStrategyImpl extends ProductConfigAbstarctRuleActionStrategy
{

	private static final String ACTION_DESCRIPTION = "Hide characteristic";

	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action)
	{
		getCstic(model, action).setVisible(false);
		return false;
	}

	@Override
	protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action)
	{
		return getRuleActionChecker().checkCsticPartOfModel(model, action, ACTION_DESCRIPTION);
	}
}