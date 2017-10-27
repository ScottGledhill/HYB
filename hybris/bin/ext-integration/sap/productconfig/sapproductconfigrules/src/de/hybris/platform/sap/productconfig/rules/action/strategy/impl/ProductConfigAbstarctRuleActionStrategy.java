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

import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Abstract base class for all CPQ Rule Engine Strategy implementations.<br>
 * Contains some boiler plate code required for every action strategy implementation, logging utilities and
 * setter/getter for common bean dependencies.
 */
public abstract class ProductConfigAbstarctRuleActionStrategy implements ProductConfigRuleActionStrategy
{

	private static final String EMPTY_STRING = "";
	private static final String RULE_UUID_END = "\\}";
	private static final String RULE_UUID_START = "\\{";

	private static final Logger LOG = Logger.getLogger(ProductConfigAbstarctRuleActionStrategy.class);

	private ProductConfigRuleActionStrategyChecker ruleActionChecker;
	private ProductConfigRuleFormatTranslator rulesFormator;
	private RuleEngineService ruleEngineService;
	private RuleParametersService ruleParametersService;
	private ConfigModelFactory configModelFactory;
	private I18NService i18NService;

	private static final Pattern paramPattern = Pattern.compile(".*\\{[\\-a-f0-9]+\\}.*");


	@Override
	public boolean apply(final ConfigModel model, final AbstractRuleActionRAO action)
	{


		if (LOG.isDebugEnabled())
		{
			LOG.debug("Checking if Action '" + action + "' execution is possible.");
		}
		boolean configChanged;
		if (!isActionPossible(model, action))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Discarding Action '" + action + "', because action execution is not possible.");
			}
			configChanged = false;
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Executing Action: " + action);
			}
			configChanged = executeAction(model, action);
			handleMessage(model, action);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Action Execution resulted in configChange='" + configChanged + "' for action: " + action);
			}
		}
		return configChanged;
	}

	protected void handleMessage(final ConfigModel model, final AbstractRuleActionRAO action)
	{
		final String code = action.getFiredRuleCode();
		final AbstractRuleEngineRuleModel rule = getRuleEngineService().getRuleForCode(code);
		String messageFired = rule.getMessageFired(i18NService.getCurrentLocale());
		final String params = rule.getRuleParameters();

		final ProductConfigRuleMessageSeverity ruleMessageSeverity = rule.getMessageSeverity();
		final ProductConfigMessageSeverity messageSeverity = mapSeverity(ruleMessageSeverity);

		if (LOG.isDebugEnabled())
		{
			String logMessage = "Rule with code='" + code;
			if (StringUtils.isNotEmpty(messageFired))
			{
				logMessage += "' has messageFired='" + messageFired + "'.";
				if (StringUtils.isNotEmpty(params))
				{
					logMessage += " With params='" + params + "'.";
				}
			}
			else
			{
				logMessage += "' has no message assigned.";
			}
			LOG.debug(logMessage);
			LOG.debug(ruleMessageSeverity);
		}

		if (StringUtils.isNotEmpty(messageFired))
		{
			if (messageContainsParameters(messageFired))
			{
				try
				{
					final List<RuleParameterData> paramList = getRuleParametersService().convertParametersFromString(params);
					messageFired = replaceMessageParameters(messageFired, paramList);
				}
				catch (final RuleConverterException ex)
				{
					LOG.error("Failed to parse rule message parmetrs. ", ex);
				}
				if (messageContainsParameters(messageFired))
				{
					LOG.error("Could not resolve all parameters of message. Please check in backoffice for rule '" + code
							+ "' whether the given UUID's are correct for message '" + messageFired + "'.");
				}
			}
			final ProductConfigMessage message = createMessage(code, messageFired, messageSeverity);
			model.getMessages().add(message);
		}
	}

	protected ProductConfigMessageSeverity mapSeverity(final ProductConfigRuleMessageSeverity ruleMessageSeverity)
	{
		ProductConfigMessageSeverity severity;
		if (ruleMessageSeverity != null)
		{
			switch (ruleMessageSeverity)
			{
				case WARNING:
					severity = ProductConfigMessageSeverity.WARNING;
					break;
				case INFO:
					severity = ProductConfigMessageSeverity.INFO;
					break;
				default:
					severity = ProductConfigMessageSeverity.INFO;
					break;
			}
		}
		else
		{
			severity = ProductConfigMessageSeverity.INFO;
		}

		return severity;
	}

	protected boolean messageContainsParameters(final String messageFired)
	{
		return paramPattern.matcher(messageFired).matches();
	}

	protected String replaceMessageParameters(final String messageFired, final List<RuleParameterData> paramList)
	{
		String replacedMassage = messageFired;
		for (final RuleParameterData ruleParam : paramList)
		{
			final Pattern pattern = Pattern.compile(RULE_UUID_START + ruleParam.getUuid() + RULE_UUID_END);
			final String valueString = ruleParam.getValue() == null ? EMPTY_STRING : ruleParam.getValue().toString();
			replacedMassage = pattern.matcher(replacedMassage).replaceAll(valueString);
		}
		return replacedMassage;
	}

	protected ProductConfigMessage createMessage(final String code, final String messageFired,
			final ProductConfigMessageSeverity severity)
	{
		return getConfigModelFactory().createInstanceOfProductConfigMessage(messageFired, code, severity,
				ProductConfigMessageSource.RULE);
	}

	protected CsticModel getCstic(final ConfigModel model, final AbstractRuleActionRAO action)
	{
		return getRuleActionChecker().getCstic(model, action);
	}

	protected ProductConfigRuleActionStrategyChecker getRuleActionChecker()
	{
		return ruleActionChecker;
	}

	@Required
	public void setRuleActionChecker(final ProductConfigRuleActionStrategyChecker ruleActionChecker)
	{
		this.ruleActionChecker = ruleActionChecker;
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

	protected RuleEngineService getRuleEngineService()
	{
		return ruleEngineService;
	}

	@Required
	public void setRuleEngineService(final RuleEngineService ruleEngineService)
	{
		this.ruleEngineService = ruleEngineService;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}

	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	protected RuleParametersService getRuleParametersService()
	{
		return ruleParametersService;
	}

	@Required
	public void setRuleParametersService(final RuleParametersService ruleParametersService)
	{
		this.ruleParametersService = ruleParametersService;
	}

	protected abstract boolean executeAction(ConfigModel model, AbstractRuleActionRAO action);

	protected abstract boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action);
}
