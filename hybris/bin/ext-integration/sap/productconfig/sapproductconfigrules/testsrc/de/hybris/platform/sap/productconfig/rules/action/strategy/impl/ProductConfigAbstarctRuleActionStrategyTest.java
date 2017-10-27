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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageImpl;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigAbstarctRuleActionStrategyTest
{

	private ProductConfigAbstarctRuleActionStrategy classUnderTest;
	private AbstractRuleActionRAO action;
	private ConfigModel model;
	private AbstractRuleEngineRuleModel rule;

	@Mock
	private RuleParametersService mockedRuleParamService;
	@Mock
	private RuleEngineService mockedRuleEngineService;
	@Mock
	private I18NService mockedI18nService;
	private List<RuleParameterData> paramList;

	@Before
	public void setUp() throws RuleConverterException
	{
		classUnderTest = new ProductConfigAbstarctRuleActionStrategy()
		{
			@Override
			protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action)
			{
				return false;
			}

			@Override
			protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action)
			{
				return false;
			}
		};
		MockitoAnnotations.initMocks(this);
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest, mockedRuleEngineService, mockedI18nService,
				mockedRuleParamService);

		model = ConfigurationRulesTestData.createEmptyConfigModel();
		action = new AbstractRuleActionRAO();
		action.setFiredRuleCode("rule123");

		rule = new AbstractRuleEngineRuleModel();

		given(mockedRuleEngineService.getRuleForCode("rule123")).willReturn(rule);
		given(mockedI18nService.getCurrentLocale()).willReturn(Locale.ENGLISH);

		paramList = new ArrayList<>();
		prepareRuleParam("0226a9c4-f20c-4111-b578-d6a318e33100",
				"Enum(de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleMessageSeverity)", "WARNING");
		given(mockedRuleParamService.convertParametersFromString(any(String.class))).willReturn(paramList);
	}

	@Test
	public void testHandleMessage_noMessage()
	{
		classUnderTest.handleMessage(model, action);
		assertTrue(model.getMessages().isEmpty());
	}


	@Test
	public void testHandleMessage_newMessage()
	{
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action);

		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_newMessage_de()
	{
		given(mockedI18nService.getCurrentLocale()).willReturn(Locale.GERMAN);
		rule.setMessageFired("eine test meldung", Locale.GERMAN);
		classUnderTest.handleMessage(model, action);

		assertOneMessage("eine test meldung", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_sameRuleWith2Actions()
	{
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action);
		classUnderTest.handleMessage(model, action);

		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_doNotOverwriteMessageWithHigherSeverity()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a test message", "rule123",
				ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.RULE);
		model.getMessages().add(message);
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action);

		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.RULE);
	}


	protected void assertOneMessage(final String message, final String key, final ProductConfigMessageSeverity severity,
			final ProductConfigMessageSource source)
	{
		assertTrue(model.getMessages().size() == 1);
		final ProductConfigMessage firstMessage = model.getMessages().iterator().next();
		assertEquals(message, firstMessage.getMessage());
		assertEquals(key, firstMessage.getKey());
		assertEquals(severity, firstMessage.getSeverity());
		assertEquals(source, firstMessage.getSource());
	}

	@Test
	public void testMessageContainsParameters_validParam()
	{
		final String messageWithValidParam = "a message with a param {1dc12f4e-c48a-4a48-bc5b-43f606b61bbc}";
		assertTrue(classUnderTest.messageContainsParameters(messageWithValidParam));
	}

	@Test
	public void testMessageContainsParameters_noParam()
	{
		final String messageWithoutParam = "a message without a param";
		assertFalse(classUnderTest.messageContainsParameters(messageWithoutParam));
	}

	@Test
	public void testMessageContainsParameters_invalidParam()
	{
		final String messageWithInvalidParam = "a message with invalid param. params mut be in format '{hex|-}'";
		assertFalse(classUnderTest.messageContainsParameters(messageWithInvalidParam));
	}

	@Test
	public void testMessageReplace_noParam()
	{
		final String message = "message without any param";
		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);
		assertEquals(message, replacedMessage);
	}

	@Test
	public void testMessageReplace_replaceSingleParam()
	{
		prepareRuleParam("aa-111-bb", "java.lang.String", "WCEM_RELEASE");
		final String message = "Characteristic '{aa-111-bb}' has been prefilled, based on your current cart contents.";

		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);

		final String expectedMessage = "Characteristic 'WCEM_RELEASE' has been prefilled, based on your current cart contents.";
		assertEquals(expectedMessage, replacedMessage);
	}

	@Test
	public void testMessageReplace_replaceSingleParam_nullValue()
	{
		prepareRuleParam("aa-111-bb", "java.lang.String", null);
		final String message = "Characteristic '{aa-111-bb}' has been prefilled, based on your current cart contents.";

		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);

		final String expectedMessage = "Characteristic '' has been prefilled, based on your current cart contents.";
		assertEquals(expectedMessage, replacedMessage);
	}

	protected void prepareRuleParam(final String uuid, final String type, final String value)
	{
		final RuleParameterData ruleParam = new RuleParameterData();
		ruleParam.setUuid(uuid);
		ruleParam.setType(type);
		ruleParam.setValue(value);
		paramList.add(ruleParam);
	}

	@Test
	public void testHandleMessage_withParams()
	{

		prepareRuleParam("111-ff-22", "java.lang.String", "p1");
		prepareRuleParam("222-aa-11", "java.lang.String", "p2");
		rule.setMessageFired("a test message withe params {111-ff-22} and {222-aa-11}", Locale.ENGLISH);

		classUnderTest.handleMessage(model, action);

		assertOneMessage("a test message withe params p1 and p2", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_withParamsIncomplete()
	{

		prepareRuleParam("aaaa-ff-22", "java.lang.String", "p1");
		prepareRuleParam("222-aa-11", "java.lang.String", "p2");
		rule.setMessageFired("a test message withe params {111-ff-22} and {222-aa-11}", Locale.ENGLISH);

		classUnderTest.handleMessage(model, action);

		assertOneMessage("a test message withe params {111-ff-22} and p2", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testMapSeverity_warning()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(ProductConfigRuleMessageSeverity.WARNING);
		assertEquals(ProductConfigMessageSeverity.WARNING, severity);
	}

	@Test
	public void testMapSeverity_info()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(ProductConfigRuleMessageSeverity.INFO);
		assertEquals(ProductConfigMessageSeverity.INFO, severity);
	}

	@Test
	public void testMapSeverity_null()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(null);
		assertEquals(ProductConfigMessageSeverity.INFO, severity);
	}
}
