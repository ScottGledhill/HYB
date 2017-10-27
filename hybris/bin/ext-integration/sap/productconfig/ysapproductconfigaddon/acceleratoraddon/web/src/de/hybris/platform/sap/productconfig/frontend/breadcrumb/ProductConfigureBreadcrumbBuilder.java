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
package de.hybris.platform.sap.productconfig.frontend.breadcrumb;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.util.localization.Localization;

import java.util.List;


public class ProductConfigureBreadcrumbBuilder extends ProductBreadcrumbBuilder
{

	private static final String LAST_LINK_CLASS = "active";

	@Override
	public List<Breadcrumb> getBreadcrumbs(final String productCode)
	{
		final List<Breadcrumb> breadcrumbs = super.getBreadcrumbs(productCode);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getUrl(productCode, SapproductconfigfrontendWebConstants.CONFIG_URL), getLinkText(),
				LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	public List<Breadcrumb> getOverviewBreadcrumbs(final String productCode)
	{
		final List<Breadcrumb> breadcrumbs = getBreadcrumbs(productCode);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getUrl(productCode, SapproductconfigfrontendWebConstants.CONFIG_OVERVIEW_URL),
				getOverviewLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	public List<Breadcrumb> getVariantOverviewBreadcrumbs(final ProductData productData)
	{
		final List<Breadcrumb> breadcrumbs = getBreadcrumbs(productData.getBaseProduct());

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(
				getUrl(productData.getCode(), SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL), getOverviewLinkText(),
				LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	protected String getLinkText()
	{
		if (Registry.isStandaloneMode())
		{
			return "TEST-STANDALONE-MODE";
		}
		return Localization.getLocalizedString("sapproductconfig.config.breadcrumb");
	}

	protected String getOverviewLinkText()
	{
		if (Registry.isStandaloneMode())
		{
			return "TEST-STANDALONE-MODE";
		}
		return Localization.getLocalizedString("sapproductconfig.config.overview.breadcrumb");
	}

	protected String getUrl(final String productCode, final String appendUrl)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final String productUrl = super.getProductModelUrlResolver().resolve(productModel);
		return productUrl + appendUrl;
	}
}
