/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService.ACTIVECATALOGVERSION;
import static de.hybris.platform.core.PK.fromLong;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSItemDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSAdminItemServiceTest
{

	@Mock
	private ModelService modelService;

	@Mock
	private CMSItemDao cmsItemDao;

	@Mock
	private SessionService sessionService;

	@InjectMocks
	private DefaultCMSAdminItemService defaultCMSAdminItemService;

	private final String uid = "uid";

	@Mock
	private CatalogVersionModel catalogVersionInSession;

	private final PK versionPK = fromLong(123);

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CMSItemModel item;

	@Before
	public void setUp()
	{
		when(sessionService.getAttribute(ACTIVECATALOGVERSION)).thenReturn(versionPK);
		when(modelService.get(versionPK)).thenReturn(catalogVersionInSession);

		when(cmsItemDao.findByUid(uid, catalogVersion)).thenReturn(item);
		when(cmsItemDao.findByUid(uid, catalogVersionInSession)).thenReturn(item);
	}

	@Test
	public void findByUidWillDelegateToTheDaoWithPassedVersion() throws CMSItemNotFoundException
	{
		//execute
		final CMSItemModel fetchedItem = defaultCMSAdminItemService.findByUid(uid, catalogVersion);

		//verify
		assertThat(fetchedItem, is(item));
		verify(cmsItemDao).findByUid(uid, catalogVersion);
	}

	@Test
	public void findByUidWillDelegateToTheDaoWithVersionInSession() throws CMSItemNotFoundException
	{
		//execute
		final CMSItemModel fetchedItem = defaultCMSAdminItemService.findByUid(uid);

		//verify
		assertThat(fetchedItem, is(item));
		verify(cmsItemDao).findByUid(uid, catalogVersionInSession);

	}

	@Test(expected = CMSItemNotFoundException.class)
	public void findByUidWillThrowExceptionWhenNotFound() throws CMSItemNotFoundException
	{
		//setup
		when(cmsItemDao.findByUid(uid, catalogVersionInSession)).thenReturn(null);

		//execute
		defaultCMSAdminItemService.findByUid(uid);

		verify(cmsItemDao).findByUid(uid, catalogVersionInSession);

	}

}
