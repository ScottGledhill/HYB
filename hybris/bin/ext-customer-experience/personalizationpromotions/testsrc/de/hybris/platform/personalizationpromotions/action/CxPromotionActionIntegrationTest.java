/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 *
 */
package de.hybris.platform.personalizationpromotions.action;

import de.hybris.platform.personalizationpromotions.constants.PersonalizationpromotionsConstants;
import de.hybris.platform.personalizationpromotions.model.CxPromotionActionModel;
import de.hybris.platform.personalizationservices.action.CxActionService;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.enums.ActionType;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CxPromotionActionIntegrationTest extends ServicelayerTest
{
	private static final String PUBLISHED_EMPTY_PROMOTION = "promotionRule1";
	private static final String PUBLISHED_AWARE_PROMOTION = "promotionRule2";
	private static final String PUBLISHED_COMPLEX_PROMOTION = "promotionRule3";
	private static final String PUBLISHED_COMPLEX_AWARE_PROMOTION = "promotionRule4";
	private static final String MODIFIED_EMPTY_PROMOTION = "promotionRule5";
	private static final String MODIFIED_AWARE_PROMOTION = "promotionRule6";
	private static final String MODIFIED_COMPLEX_PROMOTION = "promotionRule7";
	private static final String MODIFIED_COMPLEX_AWARE_PROMOTION = "promotionRule8";

	@Resource
	CxActionService cxActionService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Resource
	RuleMaintenanceService ruleMaintenanceService;

	@Resource
	ConfigurationService configurationService;

	@Resource
	ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationpromotions/test/testdata_personalizationpromotions.impex", "utf-8");

		publishPromotions(PUBLISHED_EMPTY_PROMOTION, PUBLISHED_AWARE_PROMOTION, PUBLISHED_COMPLEX_PROMOTION,
				PUBLISHED_COMPLEX_AWARE_PROMOTION);
	}

	protected void publishPromotions(final String... codes) throws RuleEngineServiceException
	{
		final List<PromotionSourceRuleModel> rules = new ArrayList<>();

		for (final String code : codes)
		{
			rules.add(getPromotionRule(code));
		}
		ruleMaintenanceService.compileAndPublishRules(rules);

	}

	protected CxVariationModel getVariation()
	{
		final CxVariationModel sample = new CxVariationModel();
		sample.setCode("variation1");
		return flexibleSearchService.getModelByExample(sample);
	}

	protected PromotionSourceRuleModel getPromotionRule(final String code)
	{
		final PromotionSourceRuleModel sample = new PromotionSourceRuleModel();
		sample.setCode(code);
		final PromotionSourceRuleModel model = flexibleSearchService.getModelByExample(sample);
		modelService.refresh(model);
		return model;
	}

	protected CxPromotionActionModel createAction(final String code, final String promotionId)
	{
		final CxPromotionActionModel model = new CxPromotionActionModel();

		model.setCode(code);
		model.setPromotionId(promotionId);
		model.setTarget("cxPromotionActionPerformable");
		model.setType(ActionType.PLAIN);

		return model;
	}

	private void modifyPromotions(final boolean modify)
	{
		configurationService.getConfiguration().setProperty(PersonalizationpromotionsConstants.MODIFY_PROMOTIONS,
				Boolean.toString(modify));
	}

	public void executeTest(final String promotionId, final boolean modifyPromotion, final RuleStatus expectedStatus)
	{
		//given
		modifyPromotions(modifyPromotion);
		final CxVariationModel variation = getVariation();
		final CxPromotionActionModel action = createAction("promotionAction", promotionId);
		final PromotionSourceRuleModel sourceRuleModel = getPromotionRule(promotionId);
		final String conditions = sourceRuleModel.getConditions();
		//when
		cxActionService.createAction(action, variation);

		//then
		final PromotionSourceRuleModel promotionRule = getPromotionRule(promotionId);
		if (modifyPromotion)
		{
			Assert.assertThat(promotionRule.getConditions(), CoreMatchers.containsString("cx_aware_promotion"));
		}
		else
		{
			Assert.assertEquals(conditions, promotionRule.getConditions());
		}
		Assert.assertEquals(expectedStatus, promotionRule.getStatus());
	}

	@Test
	public void testModifyPublishedEmpty()
	{
		executeTest(PUBLISHED_EMPTY_PROMOTION, true, RuleStatus.PUBLISHED);
	}

	@Test
	public void testModifyPublishedAware()
	{
		executeTest(PUBLISHED_AWARE_PROMOTION, true, RuleStatus.PUBLISHED);
	}

	@Test
	public void testModifyPublishedComplex()
	{
		executeTest(PUBLISHED_COMPLEX_PROMOTION, true, RuleStatus.PUBLISHED);
	}

	@Test
	public void testModifyPublishedComplexAware()
	{
		executeTest(PUBLISHED_COMPLEX_AWARE_PROMOTION, true, RuleStatus.PUBLISHED);
	}

	@Test
	public void testModifyUnpublishedEmpty()
	{
		executeTest(MODIFIED_EMPTY_PROMOTION, true, RuleStatus.MODIFIED);
	}

	@Test
	public void testModifyUnpublishedAware()
	{
		executeTest(MODIFIED_AWARE_PROMOTION, true, RuleStatus.MODIFIED);
	}

	@Test
	public void testModifyUnpublishedComplex()
	{
		executeTest(MODIFIED_COMPLEX_PROMOTION, true, RuleStatus.MODIFIED);
	}

	@Test
	public void testModifyUnpublishedComplexAware()
	{
		executeTest(MODIFIED_COMPLEX_AWARE_PROMOTION, true, RuleStatus.MODIFIED);
	}


	@Test
	public void testPublishedEmpty()
	{
		executeTest(PUBLISHED_EMPTY_PROMOTION, false, RuleStatus.PUBLISHED);
	}

	@Test
	public void testPublishedAware()
	{
		executeTest(PUBLISHED_AWARE_PROMOTION, false, RuleStatus.PUBLISHED);
	}

	@Test
	public void testPublishedComplex()
	{
		executeTest(PUBLISHED_COMPLEX_PROMOTION, false, RuleStatus.PUBLISHED);
	}

	@Test
	public void testPublishedComplexAware()
	{
		executeTest(PUBLISHED_COMPLEX_AWARE_PROMOTION, false, RuleStatus.PUBLISHED);
	}

	@Test
	public void testUnpublishedEmpty()
	{
		executeTest(MODIFIED_EMPTY_PROMOTION, false, RuleStatus.MODIFIED);
	}

	@Test
	public void testUnpublishedAware()
	{
		executeTest(MODIFIED_AWARE_PROMOTION, false, RuleStatus.MODIFIED);
	}

	@Test
	public void testUnpublishedComplex()
	{
		executeTest(MODIFIED_COMPLEX_PROMOTION, false, RuleStatus.MODIFIED);
	}

	@Test
	public void testUnpublishedComplexAware()
	{
		executeTest(MODIFIED_COMPLEX_AWARE_PROMOTION, false, RuleStatus.MODIFIED);
	}

	@Test(expected = ModelSavingException.class)
	public void testInvalidPromotion()
	{
		//given
		final CxVariationModel variation = getVariation();
		final CxPromotionActionModel action = createAction("promotionAction", PUBLISHED_EMPTY_PROMOTION + "x");

		//when
		cxActionService.createAction(action, variation);
	}
}
