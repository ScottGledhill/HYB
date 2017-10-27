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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigurationRuleAwareService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ProductConfigurationRuleAwareServiceImpl extends ProductConfigurationServiceImpl implements
		ProductConfigurationRuleAwareService
{
	private FactContextFactory factContextFactory;
	private CartService cartService;
	private String defaultRuleEngineContextName;
	private RuleEngineContextDao ruleEngineContextDao;
	private RuleEngineService commerceRuleEngineService;

	private Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationRuleAwareServiceImpl.class);


	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		final ConfigModel config = getConfigurationProvider().createDefaultConfiguration(kbKey);
		this.getRecorder().recordCreateConfiguration(config, kbKey);
		return checkRulesAndCacheConfig(config);
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create variant configuration for base product " + baseProductCode + " of product variant "
					+ variantProductCode);
		}

		final ConfigModel config = getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode, variantProductCode);
		this.getRecorder().recordCreateConfigurationForVariant(config, baseProductCode, variantProductCode);
		return checkRulesAndCacheConfig(config);
	}

	protected ConfigModel checkRulesAndCacheConfig(final ConfigModel config)
	{
		ConfigModel checkedConfig = config;
		final boolean adjusted = adjustConfigurationRuleBased(checkedConfig, ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		if (adjusted)
		{
			final Set<ProductConfigMessage> oldMessages = checkedConfig.getMessages();
			updateConfiguration(checkedConfig);
			checkedConfig = retrieveConfigurationModel(config.getId());
			checkedConfig.getMessages().addAll(oldMessages);
		}
		else
		{
			adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION);
			this.getRecorder().recordConfigurationStatus(checkedConfig);
		}

		cacheConfig(checkedConfig);
		return checkedConfig;
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = getSessionAccessService().getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{
				cachedModel = provideConfigurationModel(configId, true);
				cacheConfig(configId, cachedModel);
				this.getRecorder().recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	protected ConfigModel provideConfigurationModel(final String configId, final boolean useRuleEngine)
	{
		final ConfigModel config = super.retrieveConfigurationModelFromConfigurationEngine(configId);

		if (useRuleEngine)
		{
			adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION);
		}
		return config;
	}


	@Override
	public ConfigModel retrieveConfigurationModelBypassRules(final String configId)
	{

		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = getSessionAccessService().getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{
				cachedModel = provideConfigurationModel(configId, false);
				cacheConfig(configId, cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}


	protected boolean adjustConfigurationRuleBased(final ConfigModel currentConfigModel, final ProcessStep processStep)
	{
		boolean adjusted = false;

		final ProductConfigProcessStepModel processStepModel = new ProductConfigProcessStepModel();
		processStepModel.setProcessStep(processStep);

		final CartModel cartModel = getCartService().getSessionCart();

		final FactContext factContext = createFactContext(cartModel, currentConfigModel, processStepModel);
		final RuleEvaluationContext ruleEvaluationContext = prepareRuleEvaluationContext(factContext);

		LOG.debug("Triggering rule engine for process Step " + processStep + " and config id " + currentConfigModel.getId());

		final RuleEvaluationResult rulesResult = getCommerceRuleEngineService().evaluate(ruleEvaluationContext);
		adjusted = applyRulesResult(rulesResult, currentConfigModel);

		LOG.debug("Rule engine processing done for process Step " + processStep + " and config id " + currentConfigModel.getId());


		return adjusted;
	}

	protected FactContext createFactContext(final CartModel cartModel, final ConfigModel currentConfigModel,
			final ProductConfigProcessStepModel processStepModel)
	{
		final List<Object> facts = new ArrayList<Object>();

		facts.add(cartModel);
		facts.add(currentConfigModel);
		facts.add(processStepModel);

		return getFactContextFactory().createFactContext(FactContextType.PRODUCTCONFIG_DEFAULT_CONFIGURATION, facts);
	}


	protected RuleEvaluationContext prepareRuleEvaluationContext(final FactContext factContext)
	{
		final Set<Object> convertedFacts = provideRAOs(factContext);
		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		final AbstractRuleEngineContextModel engineContext = getRuleEngineContextDao().getRuleEngineContextByName(
				getDefaultRuleEngineContextName());
		evaluationContext.setRuleEngineContext(engineContext);
		evaluationContext.setFacts(convertedFacts);
		return evaluationContext;
	}

	protected Set<Object> provideRAOs(final FactContext factContext)
	{
		final Set<Object> result = new HashSet<>();
		if (factContext != null)
		{
			for (final Object fact : factContext.getFacts())
			{
				for (final RAOProvider raoProvider : factContext.getProviders(fact))
				{
					result.addAll(raoProvider.expandFactModel(fact));
				}
			}
		}
		return result;
	}


	protected FactContextFactory getFactContextFactory()
	{
		return factContextFactory;
	}

	/**
	 * @param factContextFactory
	 *           injects the rule engine fact context factory, which is required to trigger the rule engine
	 */
	@Required
	public void setFactContextFactory(final FactContextFactory factContextFactory)
	{
		this.factContextFactory = factContextFactory;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cart service to access the session cart contents, which may influence the rule engine result
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected String getDefaultRuleEngineContextName()
	{
		return defaultRuleEngineContextName;
	}

	/**
	 * @param defaultRuleEngineContextName
	 *           injects the rule engine context, which is required to trigger the rule engine
	 */
	@Required
	public void setDefaultRuleEngineContextName(final String defaultRuleEngineContextName)
	{
		this.defaultRuleEngineContextName = defaultRuleEngineContextName;
	}

	protected RuleEngineContextDao getRuleEngineContextDao()
	{
		return ruleEngineContextDao;
	}

	/**
	 * @param ruleEngineContextDao
	 *           injects the rule engine context DAO, which is required to trigger the rule engine
	 */
	@Required
	public void setRuleEngineContextDao(final RuleEngineContextDao ruleEngineContextDao)
	{
		this.ruleEngineContextDao = ruleEngineContextDao;
	}

	protected RuleEngineService getCommerceRuleEngineService()
	{
		return commerceRuleEngineService;
	}

	/**
	 * @param ruleEngineService
	 *           injects the rule engine service, which is required to trigger the rule engine
	 */
	@Required
	public void setCommerceRuleEngineService(final RuleEngineService ruleEngineService)
	{
		this.commerceRuleEngineService = ruleEngineService;
	}


	protected boolean applyRulesResult(final RuleEvaluationResult rulesResult, final ConfigModel configModel)
	{
		boolean configChanged = false;

		if (rulesResult == null || rulesResult.getResult() == null)
		{
			return false;
		}

		final Set<AbstractRuleActionRAO> actions = rulesResult.getResult().getActions();
		if (CollectionUtils.isEmpty(actions))
		{
			return false;
		}

		for (final AbstractRuleActionRAO action : actions)
		{
			final ProductConfigRuleActionStrategy stategy = getRuleActionStrategy(action.getActionStrategyKey());
			configChanged |= stategy.apply(configModel, action);
		}

		return configChanged;
	}

	protected Map<String, ProductConfigRuleActionStrategy> getActionStrategiesMapping()
	{
		return actionStrategiesMapping;
	}

	/**
	 * @param actionStrategiesMapping
	 *           injects the mapping between actionStrategyKey, which is defined by the rule action and a concreate
	 *           actionStrategy class, which will apply the defined action
	 */
	@Required
	public void setActionStrategiesMapping(final Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping)
	{
		this.actionStrategiesMapping = actionStrategiesMapping;
	}

	/**
	 * returns the {@code ProductConfigRuleActionStrategy} defined in the {@code actionStrategiesMapping} attribute of
	 * this service by looking up it's hey.
	 *
	 * @param strategyKey
	 *           the key of the RuleActionStrategy to look up
	 * @return the found bean id
	 * @throws IllegalArgumentException
	 *            if the requested strategy cannot be found
	 * @throws IllegalStateException
	 *            if this method is called but no strategies are configured
	 */
	protected ProductConfigRuleActionStrategy getRuleActionStrategy(final String strategyKey)
	{
		if (MapUtils.isNotEmpty(getActionStrategiesMapping()))
		{
			final ProductConfigRuleActionStrategy strategy = getActionStrategiesMapping().get(strategyKey);
			if (strategy != null)
			{
				return strategy;
			}
			throw new IllegalArgumentException("cannot find ProductConfigRuleActionStrategy for given action: " + strategyKey);
		}
		throw new IllegalStateException("no strategy mapping defined");
	}
}
