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
package de.hybris.platform.sap.productconfig.rules.interceptor;

import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContextFactory;
import de.hybris.platform.ruleengineservices.compiler.impl.DefaultRuleCompilerContext;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.springframework.beans.factory.annotation.Required;



public class ProductConfigRulesPrepareInterceptor implements PrepareInterceptor<AbstractRuleEngineRuleModel>
{
	private RuleCompilerContextFactory<DefaultRuleCompilerContext> ruleCompilerContextFactory;

	@Override
	public void onPrepare(final AbstractRuleEngineRuleModel model, final InterceptorContext context) throws InterceptorException
	{
		if (!RuleType.PRODUCTCONFIG.equals(model.getRuleType()))
		{
			return;
		}

		final DefaultRuleCompilerContext ruleCompileContext = getRuleCompilerContextFactory().getContext();
		if (null != ruleCompileContext)
		{
			final ProductConfigSourceRuleModel rule = (ProductConfigSourceRuleModel) ruleCompileContext.getRule();
			mapSeverity(model, rule);
		}
	}

	protected void mapSeverity(final AbstractRuleEngineRuleModel runtimeRule, final ProductConfigSourceRuleModel sourceRule)
	{
		runtimeRule.setMessageSeverity(sourceRule.getMessageSeverity());
	}

	protected RuleCompilerContextFactory<DefaultRuleCompilerContext> getRuleCompilerContextFactory()
	{
		return ruleCompilerContextFactory;
	}

	@Required
	public void setRuleCompilerContextFactory(
			final RuleCompilerContextFactory<DefaultRuleCompilerContext> ruleCompilerContextFactory)
	{
		this.ruleCompilerContextFactory = ruleCompilerContextFactory;
	}
}
