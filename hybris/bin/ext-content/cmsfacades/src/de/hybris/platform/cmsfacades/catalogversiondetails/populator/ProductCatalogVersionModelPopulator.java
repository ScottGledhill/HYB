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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Populates a {@Link CatalogVersionData} DTO from a {@Link CMSSiteModel} with a ProductCatalog
 */
public class ProductCatalogVersionModelPopulator extends AbstractCatalogVersionModelPopulator
{

	private BaseSiteService baseSiteService;
	
	public List<CatalogModel> getCatalogs(CMSSiteModel source)
	{
		return getBaseSiteService().getProductCatalogs(source);
	}


	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}
	
	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
	
}
