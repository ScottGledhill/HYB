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
package de.hybris.platform.cmsfacades.catalogversiondetails.populator;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.jalo.contents.ContentCatalog;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import java.util.List;


/**
 * Populates a {@Link CatalogVersionData} DTO from a {@Link CMSSiteModel} with {@link ContentCatalog}.
 */
public class ContentCatalogVersionModelPopulator extends AbstractCatalogVersionModelPopulator
{

	public List<CatalogModel> getCatalogs(CMSSiteModel source)
	{
		return source.getContentCatalogs().stream().map(e -> (CatalogModel) e).collect(toList());
	}

}
