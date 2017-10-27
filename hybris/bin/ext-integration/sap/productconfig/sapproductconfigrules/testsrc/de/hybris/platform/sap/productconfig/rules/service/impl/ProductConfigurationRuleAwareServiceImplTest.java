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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.impl.DefaultRuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.RemoveAssignableValueRuleActionStrategyImpl;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.SetCsticValueRuleActionStrategyImpl;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.SetCsticValueRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationRuleAwareServiceImplTest
{

	private static final String CSTIC_VALUE = "VALUE_1";
	private static final String CSTIC_VALUE_1 = "VAL1";
	private static final String CSTIC_VALUE_2 = "VAL2";
	private static final String CSTIC_WITHOUT_VALUE = "CSTIC_1.1";
	private static final String CSTIC_WITH_VALUE = "CSTIC_1.2";
	private static final String CSTIC_WITH_MULTI_VALUE = "CSTIC_1.4";
	private ProductConfigurationRuleAwareServiceImpl classUnderTest;
	@Mock
	private FactContext factContext;
	@Mock
	private RAOProvider provider;

	@Mock
	private DefaultRuleEngineContextDao ruleEngineContextDao;


	private RuleEvaluationResult rulesResult;
	private LinkedHashSet<AbstractRuleActionRAO> actions;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationRuleAwareServiceImpl();


		classUnderTest.setRuleEngineContextDao(ruleEngineContextDao);
		classUnderTest.setDefaultRuleEngineContextName("productconfig-context");

		final Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping = new HashMap<String, ProductConfigRuleActionStrategy>();
		final SetCsticValueRuleActionStrategyImpl strategy = new SetCsticValueRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(strategy);
		actionStrategiesMapping.put("sapProductConfigDefaultSetCsticValueRAOAction", strategy);
		final RemoveAssignableValueRuleActionStrategyImpl strategy2 = new RemoveAssignableValueRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(strategy2);
		actionStrategiesMapping.put("sapProductConfigDefaultRemoveAssignableValueRAOAction", strategy2);

		classUnderTest.setActionStrategiesMapping(actionStrategiesMapping);

		final List<ConfigModelImpl> factList = new ArrayList<>();
		factList.add(new ConfigModelImpl());
		doReturn(factList).when(factContext).getFacts();

		final Set<RAOProvider> providers = new HashSet<>();
		providers.add(provider);
		doReturn(providers).when(factContext).getProviders(Mockito.any(ConfigModelImpl.class));

		final List<ProductConfigRAO> productRaos = new ArrayList<>();
		final ProductConfigRAO productConfigRao = new ProductConfigRAO();
		productRaos.add(productConfigRao);
		when(provider.expandFactModel(Mockito.any(Object.class))).thenReturn(Collections.singleton(productConfigRao));

		final AbstractRuleEngineContextModel engineContext = new DroolsRuleEngineContextModel();
		when(ruleEngineContextDao.getRuleEngineContextByName("productconfig-context")).thenReturn(engineContext);

		rulesResult = ConfigurationRulesTestData.createEmptyRulesResult();
		actions = rulesResult.getResult().getActions();
	}

	@Test
	public void testProvideRAOsWithNullFactContext()
	{
		factContext = null;
		final Set<Object> raos = classUnderTest.provideRAOs(factContext);
		assertTrue("Set of 'raos' not empty: ", raos.isEmpty());
	}

	@Test
	public void testProvideRAOsWithNotNullFactContext()
	{
		final Set<Object> raos = classUnderTest.provideRAOs(factContext);
		assertTrue("Set of 'raos' empty:", !raos.isEmpty());
	}

	@Test
	public void testPrepareRuleEvaluationContext()
	{
		final RuleEvaluationContext ruleEvaluationContext = classUnderTest.prepareRuleEvaluationContext(factContext);

		assertTrue("Rule engine context is missing", ruleEvaluationContext.getRuleEngineContext() != null);
		assertTrue("Facts (RAOs)are missing", ruleEvaluationContext.getFacts() != null);
		assertTrue("Wrong number of facts (RAOs)", ruleEvaluationContext.getFacts().size() == 1);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testPrepareRuleEvaluationContextNoContext()
	{
		when(ruleEngineContextDao.getRuleEngineContextByName("productconfig-context"))
				.thenThrow(new ModelNotFoundException("TEST"));
		classUnderTest.prepareRuleEvaluationContext(factContext);
	}

	@Test
	public void testApplyRulesEmptyResult()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createEmptyConfigModel();
		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesSetDefaultValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assumeTrue(!CSTIC_VALUE.equals(cstic.getSingleValue()));
		createSetCsticValueAction(CSTIC_WITHOUT_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}

	@Test
	public void testApplyRulesChangeDefaultValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assumeTrue(!CSTIC_VALUE.equals(cstic.getSingleValue()));
		createSetCsticValueAction(CSTIC_WITH_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}


	@Test
	public void testApplyRulesTryToSetNonAssignableValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		final String oldValue = cstic.getSingleValue();

		createSetCsticValueAction(CSTIC_WITH_VALUE, "valueNotExisting");

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(oldValue.equals(cstic.getSingleValue()));
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetValueForReadOnlyCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		cstic.setReadonly(true);
		final String oldValue = cstic.getSingleValue();

		createSetCsticValueAction(CSTIC_WITH_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(oldValue.equals(cstic.getSingleValue()));
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetValueUnconstrainedCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		cstic.setConstrained(false);
		cstic.setAssignableValues(Collections.EMPTY_LIST);
		assumeFalse(CSTIC_VALUE.equals(cstic.getSingleValue()));

		createSetCsticValueAction(CSTIC_WITHOUT_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetNonExistingCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		createSetCsticValueAction("doesNotExist", CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesAddMultiValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);

		boolean valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_1);
		assumeFalse(valueAssigned);
		final int beforeListSize = cstic.getAssignedValues().size();
		createSetCsticValueAction(CSTIC_WITH_MULTI_VALUE, CSTIC_VALUE_1);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);
		assertEquals(beforeListSize + 1, cstic.getAssignedValues().size());
		valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_1);
		assertTrue(CSTIC_VALUE_1 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);
		assertTrue(isChanged);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyRulesOtherActionRAO()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		final AbstractRuleActionRAO otherAction = new AbstractRuleActionRAO();
		actions.add(otherAction);

		/* final boolean isChanged = */classUnderTest.applyRulesResult(rulesResult, configModel);
	}

	private boolean isValueAssigned(final CsticModel cstic, final String valueToCheck)
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName(valueToCheck);
		final boolean valueAssigned = cstic.getAssignedValues().contains(value);
		return valueAssigned;
	}

	@Test
	public void testApplyRulesAddExistingMultiValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);

		boolean valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_2);
		assumeTrue(CSTIC_VALUE_2 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);
		final int beforeListSize = cstic.getAssignedValues().size();
		createSetCsticValueAction(CSTIC_WITH_MULTI_VALUE, CSTIC_VALUE_2);

		classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);
		assertEquals(beforeListSize, cstic.getAssignedValues().size());
		valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_2);
		assertTrue(CSTIC_VALUE_2 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);

	}

	private void createSetCsticValueAction(final String csticName, final String valueName)
	{
		final SetCsticValueRAO csticSetAction = new SetCsticValueRAO();
		csticSetAction.setActionStrategyKey("sapProductConfigDefaultSetCsticValueRAOAction");
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(valueName);
		csticSetAction.setValueNameToSet(valueRao);
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName(csticName);
		csticSetAction.setAppliedToObject(csticRao);
		actions.add(csticSetAction);
	}

	private void createRemoveAssignableValueAction(final String csticName, final String valueName)
	{
		final RemoveAssignableValueRAO csticRemoveAssignableValueAction = new RemoveAssignableValueRAO();
		csticRemoveAssignableValueAction.setActionStrategyKey("sapProductConfigDefaultRemoveAssignableValueRAOAction");
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(valueName);
		csticRemoveAssignableValueAction.setValueNameToRemoveFromAssignable(valueRao);
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName(csticName);
		csticRemoveAssignableValueAction.setAppliedToObject(csticRao);
		actions.add(csticRemoveAssignableValueAction);
	}

	@Test
	public void testApplyRulesRemoveAssignableValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();

		CsticModel cstic = configModel.getRootInstance().getCstic(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES);
		assertEquals(2, cstic.getAssignableValues().size());

		createRemoveAssignableValueAction(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES);

		assertFalse(isChanged);
		assertEquals(1, cstic.getAssignableValues().size());
	}

	@Test
	public void testGetRuleActionStrategy()
	{
		final ProductConfigRuleActionStrategy strategy = classUnderTest
				.getRuleActionStrategy("sapProductConfigDefaultRemoveAssignableValueRAOAction");
		assertNotNull(strategy);
	}



	@Test(expected = IllegalStateException.class)
	public void testGetRuleActionStrategyEmptyMap()
	{
		classUnderTest.setActionStrategiesMapping(new HashMap<String, ProductConfigRuleActionStrategy>());
		classUnderTest.getRuleActionStrategy("sapProductConfigDefaultRemoveAssignableValueRAOAction");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleActionStrategyWrongStrategy()
	{
		classUnderTest.getRuleActionStrategy("wrongStrategy");
	}
}
