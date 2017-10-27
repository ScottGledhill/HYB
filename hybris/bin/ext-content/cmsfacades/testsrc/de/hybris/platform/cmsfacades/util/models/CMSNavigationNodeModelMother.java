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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.util.builder.NavigationNodeModelBuilder;

import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class CMSNavigationNodeModelMother extends AbstractModelMother<CMSNavigationNodeModel>
{


	private CMSNavigationDao navigationDao;

	private CMSNavigationService navigationService;

	public CMSNavigationNodeModel createNavigationNode(final String name, final String uid, final CMSNavigationNodeModel parent,
			final String englishTitle, final Collection<CatalogVersionModel> catalogVersions)
	{
		return getOrSaveAndReturn(
				() -> getNavigationDao().findNavigationNodesById(uid, catalogVersions).stream().findFirst().orElse(null),
				() -> NavigationNodeModelBuilder.aModel().withUid(uid).withName(name).withTitle(englishTitle, Locale.ENGLISH)
						.withParent(parent).build());
	}

	public CMSNavigationNodeModel createNavigationNodeWithEntry(final String name, final String uid,
			final CMSNavigationNodeModel parent, final String englishTitle, final Collection<CatalogVersionModel> catalogVersions,
			final CMSNavigationEntryModel entryModel)
	{
		return getOrSaveAndReturn(
				() -> getNavigationDao().findNavigationNodesById(uid, catalogVersions).stream().findFirst().orElse(null),
				() -> NavigationNodeModelBuilder.aModel().withUid(uid).withName(name).withTitle(englishTitle, Locale.ENGLISH)
						.withParent(parent).withEntry(entryModel).build());
	}


	public CMSNavigationDao getNavigationDao()
	{
		return navigationDao;
	}

	@Required
	public void setNavigationDao(final CMSNavigationDao navigationDao)
	{
		this.navigationDao = navigationDao;
	}

	public CMSNavigationNodeModel createNavigationRootNode(final CatalogVersionModel catalogVersion)
	{
		return navigationService.createSuperRootNavigationNode(catalogVersion);
	}

	public CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}
}
