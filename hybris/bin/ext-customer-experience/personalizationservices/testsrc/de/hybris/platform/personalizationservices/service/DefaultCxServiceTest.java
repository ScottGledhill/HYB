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
package de.hybris.platform.personalizationservices.service;

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.action.CxActionResultService;
import de.hybris.platform.personalizationservices.action.CxActionService;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.dao.CxPersonalizationBusinessProcessDao;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxService;
import de.hybris.platform.personalizationservices.strategies.ProcessSelectionStrategy;
import de.hybris.platform.personalizationservices.variation.CxVariationService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.action.ActionService;

import java.math.BigDecimal;
import java.util.*;

import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.NoMoreInteractions;


@UnitTest
public class DefaultCxServiceTest
{
	private static final String PROCESS_DEFINITION_NAME = "proces_definition_name";

	private final DefaultCxService service = new DefaultCxService();

	@Mock
	private ActionService actionService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CxVariationService cxVariationService;

	@Mock
	private CxActionResultService cxActionResultService;

	@Mock
	private CxActionService cxActionService;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private ProcessSelectionStrategy processSelectionStrategy;

	@Mock
	private CxPersonalizationBusinessProcessDao cxPersonalizationBusinessProcessDao;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setActionService(actionService);
		service.setBaseSiteService(baseSiteService);
		service.setCxVariationService(cxVariationService);
		service.setCxActionResultService(cxActionResultService);
		service.setCxActionService(cxActionService);
		service.setBusinessProcessService(businessProcessService);
		service.setProcessSelectionStrategy(processSelectionStrategy);
		service.setCxPersonalizationBusinessProcessDao(cxPersonalizationBusinessProcessDao);
	}

	@Test
	public void shouldCalculateAndStorePersonalization()
	{
		final UserModel user = new UserModel();
		final ArrayList<CxSegmentModel> segments = new ArrayList<>();
		setSegments(user, segments);

		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxVariationService.getActiveVariations(user, catalogVersion)).willReturn(variations);
		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);

		service.calculateAndStorePersonalization(user, catalogVersion);

		BDDMockito.verify(actionService, BDDMockito.times(1)).prepareAndTriggerAction(BDDMockito.eq(action),
				BDDMockito.any(Map.class));
		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).storeActionResultsOnUser(BDDMockito.eq(user),
				BDDMockito.eq(catalogVersion), BDDMockito.any());

	}

	@Test
	public void shouldClearPersonalizationInSession()
	{
		final UserModel user = new UserModel();

		service.clearPersonalizationInSession(user, catalogVersion);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).clearActionResultsInSession(user, catalogVersion);
	}

	@Test
	public void shouldLoadPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = Arrays.asList(catalogVersion);

		service.loadPersonalizationInSession(user, cvs);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).loadActionResultsInSession(user, cvs);
	}

	@Test
	public void shouldPreviewPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);

		service.calculateAndLoadPersonalizationInSession(user, catalogVersion, variations);

		BDDMockito.verify(cxActionService).getActionsForVariations(variations);
		BDDMockito.verify(actionService).prepareAndTriggerAction(BDDMockito.eq(action), BDDMockito.any());
		BDDMockito.verify(cxActionResultService).setActionResultsInSession(BDDMockito.eq(user), BDDMockito.eq(catalogVersion),
				BDDMockito.anyList());
	}

	@Test
	public void shouldGetActionResults()
	{
		final UserModel user = new UserModel();
		final List<CxAbstractActionResult> results = new ArrayList<>();

		BDDMockito.given(cxActionResultService.getActionResults(user, catalogVersion)).willReturn(results);

		final List<CxAbstractActionResult> actionResultsFromSession = service.getActionResultsFromSession(user, catalogVersion);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).getActionResults(user, catalogVersion);
		Assert.assertSame(results, actionResultsFromSession);
	}

	@Test
	public void shouldCreateNewProcessForUpdateCustomerExperience()
	{
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = new ArrayList<>();
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();

		BDDMockito.given(processSelectionStrategy.retrieveProcessDefinitionName(user, cvs)).willReturn(PROCESS_DEFINITION_NAME);
		BDDMockito.given(cxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(PROCESS_DEFINITION_NAME, user, cvs)).willReturn(Lists.newArrayList());
		BDDMockito.given(businessProcessService.createProcess(BDDMockito.any(), BDDMockito.eq(PROCESS_DEFINITION_NAME)))
				.willReturn(process);

		final CxPersonalizationProcessModel returnedProcess = service.startPersonalizationCalculationProcess(user, cvs);

		BDDMockito.verify(processSelectionStrategy).retrieveProcessDefinitionName(user, cvs);
		BDDMockito.verify(businessProcessService).createProcess(BDDMockito.anyString(), BDDMockito.eq(PROCESS_DEFINITION_NAME));
		Assert.assertSame(process, returnedProcess);
		Assert.assertSame(cvs, returnedProcess.getCatalogVersions());
		Assert.assertSame(user, returnedProcess.getUser());
	}

	@Test
	public void shouldReturnNullWithoutUpdateCustomerExperience()
	{
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = new ArrayList<>();
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setUser(user);
		process.setCatalogVersions(cvs);

		BDDMockito.given(processSelectionStrategy.retrieveProcessDefinitionName(user, cvs)).willReturn(PROCESS_DEFINITION_NAME);
		BDDMockito.given(cxPersonalizationBusinessProcessDao.findActiveBusinessProcesses(PROCESS_DEFINITION_NAME, user, cvs)).willReturn(Lists.newArrayList(process));

		final CxPersonalizationProcessModel returnedProcess = service.startPersonalizationCalculationProcess(user, cvs);

		BDDMockito.verify(processSelectionStrategy).retrieveProcessDefinitionName(user, cvs);
		BDDMockito.verifyZeroInteractions(businessProcessService);
		Assert.assertNull(returnedProcess);
	}

	@Test
	public void shouldCalculateAndLoadPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);
		BDDMockito.given(cxVariationService.getActiveVariations(user, catalogVersion)).willReturn(variations);

		service.calculateAndLoadPersonalizationInSession(user, catalogVersion);

		BDDMockito.verify(cxActionService).getActionsForVariations(variations);
		BDDMockito.verify(actionService).prepareAndTriggerAction(BDDMockito.eq(action), BDDMockito.any());
		BDDMockito.verify(cxActionResultService).setActionResultsInSession(BDDMockito.eq(user), BDDMockito.eq(catalogVersion),
				BDDMockito.anyList());

	}


	private void setSegments(final UserModel user, final List<CxSegmentModel> segments)
	{
		user.setUserToSegments(new ArrayList<CxUserToSegmentModel>());

		for (final CxSegmentModel segment : segments)
		{
			final CxUserToSegmentModel uts = new CxUserToSegmentModel();
			uts.setSegment(segment);
			uts.setUser(user);
			uts.setAffinity(BigDecimal.ONE);
			segment.setUserToSegments(new ArrayList<CxUserToSegmentModel>());
			segment.getUserToSegments().add(uts);
			user.getUserToSegments().add(uts);
		}
	}
}
