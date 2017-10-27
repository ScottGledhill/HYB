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
package com.sap.hybris.reco.addon.controller;

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sap.hybris.reco.addon.constants.SapprodrecoaddonConstants;
import com.sap.hybris.reco.addon.facade.ProductRecommendationManagerFacade;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;

/**
 * Controller for CMS CMSSAPRecommendationComponentController.
 */
@Controller("CMSSAPRecommendationComponentController")
@RequestMapping(value = "/view/CMSSAPRecommendationComponentController")
public class CMSSAPRecommendationComponentController
		extends AbstractCMSAddOnComponentController<CMSSAPRecommendationComponentModel>
{
	
	@Resource(name = "sapProductRecommendationManagerFacade")
	private ProductRecommendationManagerFacade productRecommendationManagerFacade;

	@Override
	protected String getAddonUiExtensionName(final CMSSAPRecommendationComponentModel component)
	{
		return SapprodrecoaddonConstants.EXTENSIONNAME;
	}

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CMSSAPRecommendationComponentModel component) {
		final RequestContextData requestContext = this.getRequestContextData(request);
		final ProductCategorySearchPageData<?, ?, ?> productCategorySearchPageData = (ProductCategorySearchPageData<?, ?, ?>) requestContext.getSearch();

		List<String> categoryCodes = new ArrayList<String>();
		if (productCategorySearchPageData != null)
		{
			for (BreadcrumbData<?> cat : productCategorySearchPageData.getBreadcrumbs())
			{
				if ("category".equals(cat.getFacetCode()))
				{
					categoryCodes.add(cat.getFacetValueCode());
				}
			}
		}

		String productCode = "";
		final ProductModel currentProduct = requestContext.getProduct();
		if (currentProduct != null)
		{
			// get leading product ID
			productCode = currentProduct.getCode();
			// get leading product parent category(ies) ID(s)
			Collection<CategoryModel> supercats = currentProduct.getSupercategories();
			for (CategoryModel supercat : supercats)
			{
				categoryCodes.add(supercat.getCode());
			}
		}

		final CategoryModel currentCategory = requestContext.getCategory();
		if (currentCategory != null)
		{
			categoryCodes.add(currentCategory.getCode());
		}

		productRecommendationManagerFacade.storeLeadingCategories(categoryCodes);
		model.addAttribute("productCode", productCode);
		model.addAttribute("componentId", component.getUid());
		model.addAttribute("categoryCode", categoryCodes);
	}
}
