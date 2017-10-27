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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxRecalculationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxRecalculationServiceTest
{
	private final DefaultCxRecalculationService service = new DefaultCxRecalculationService();

	@Mock
	private CxService cxService;

	@Mock
	private UserService userService;

	@Mock
	private CxSegmentService cxSegmentService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private UserModel user;
	@Mock
	private CatalogVersionModel catalogVersion;

	private Collection<CatalogVersionModel> catalogVersions;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setCxService(cxService);
		service.setUserService(userService);
		service.setCatalogVersionService(catalogVersionService);
		service.setSegmentService(cxSegmentService);
		catalogVersions = Collections.singleton(catalogVersion);
	}

	@Test
	public void recalculateTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.RECALCULATE));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(1)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(0)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(0)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(0)).updateUserSegments(user);
	}

	@Test
	public void loadActionTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.LOAD));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(0)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(0)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(1)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(0)).updateUserSegments(user);
	}

	@Test
	public void updateActionTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.UPDATE));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(0)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(0)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(0)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(1)).updateUserSegments(user);
	}

	@Test
	public void asyncProcessTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.ASYNC_PROCESS));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(0)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(1)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(0)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(0)).updateUserSegments(user);
	}

	@Test
	public void ignoreActionTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.IGNORE));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(0)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(0)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(0)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(0)).updateUserSegments(user);
	}

	@Test
	public void multipleActionTest()
	{
		//given
		BDDMockito.given(userService.getCurrentUser()).willReturn(user);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(catalogVersions);

		//when
		service.recalculate(user, Arrays.asList(RecalculateAction.ASYNC_PROCESS, RecalculateAction.LOAD,
				RecalculateAction.RECALCULATE, RecalculateAction.UPDATE));

		//then
		BDDMockito.verify(cxService, BDDMockito.times(1)).calculateAndLoadPersonalizationInSession(user, catalogVersion);
		BDDMockito.verify(cxService, BDDMockito.times(1)).startPersonalizationCalculationProcess(user, catalogVersions);
		BDDMockito.verify(cxService, BDDMockito.times(1)).loadPersonalizationInSession(user, catalogVersions);
		BDDMockito.verify(cxSegmentService, BDDMockito.times(1)).updateUserSegments(user);
	}
}
