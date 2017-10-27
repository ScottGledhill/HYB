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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller implementation to be used, when a <b>variant</b> shall be displayed on the configuration overview page.
 * 
 * @see ConfigurationOverviewController
 */
@Controller
@RequestMapping()
public class VariantOverviewController extends AbstractProductConfigController
{
	private static final String CLOSING_QUOTE = "'";
	private static final String CONFIG_GET_RECEIVED_FOR = "Config GET received for '";
	private static final String CONFIG_POST_RECEIVED_FOR = "Config POST received for '";
	private static final String CURRENT_SESSION = "' - Current Session: '";

	private static final String AJAX_VIEW_NAME = ADDON_PREFIX + ROOT + SapproductconfigaddonConstants.EXTENSIONNAME
			+ OVERVIEW_PAGE_ROOT + AJAX_SUFFIX;

	private static final Logger LOGGER = Logger.getLogger(VariantOverviewController.class.getName());

	@Resource(name = "sapProductConfigOverviewFacade")
	private ConfigurationOverviewFacade configurationOverviewFacade;


	@Resource(name = "sapProductConfigBreadcrumbBuilder")
	private ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder;

	/**
	 * Renders the product config overview page, using a product configuration variant as input.
	 *
	 * @param productCode
	 *           product code of the variant
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}" + SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL, method = RequestMethod.GET)
	public String getVariantOverview(@PathVariable("productCode")
	final String productCode, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CONFIG_GET_RECEIVED_FOR + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		ConfigurationOverviewData configOverviewData = null;
		configOverviewData = populateConfigurationModel(productCode, configOverviewData);
		prepareUiModel(request, model, productCode, configOverviewData);
		getUiRecorder().recordUiAccessVariantOverview(productCode);

		return ADDON_PREFIX + ROOT + SapproductconfigaddonConstants.EXTENSIONNAME + OVERVIEW_PAGE_ROOT;
	}


	/**
	 * Updates the product config overview page for variants. For example if a filter value was changed.
	 *
	 * @param productCode
	 *           product code of the variant
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @param overviewUIData
	 *           data currently displayed on overview page
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}" + SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL, method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateVariantOverview(@PathVariable("productCode")
	final String productCode, final OverviewUiData overviewUIData, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CONFIG_POST_RECEIVED_FOR + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		overviewUIData.setConfigurationOverviewData(populateConfigurationModel(productCode,
				overviewUIData.getConfigurationOverviewData()));
		prepareUiModel(request, model, productCode, overviewUIData.getConfigurationOverviewData());

		return new ModelAndView(AJAX_VIEW_NAME);
	}

	/**
	 * Cleans up the UI-State, after a varaint has been added to the cart
	 *
	 * @param productCode
	 *           product code of variant
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}/addVariantToCartCleanUp", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void addVariantToCartCleanUp(@PathVariable("productCode")
	final String productCode) throws CMSItemNotFoundException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("AddVariantToCart GET received for '" + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		final ProductData productData = getProductFacade().getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC));

		final String baseProduct = productData.getBaseProduct();
		resetUiCartConfigurationForProduct(baseProduct);
	}

	protected void resetUiCartConfigurationForProduct(final String baseProduct)
	{
		final UiStatus uiStatus = getSessionAccessFacade().getUiStatusForProduct(baseProduct);
		if (uiStatus != null)
		{
			final String configId = uiStatus.getConfigId();
			final boolean configIsAttachedToCartEntry = null != getSessionAccessFacade().getCartEntryForConfigId(configId);
			if (!configIsAttachedToCartEntry)
			{
				getConfigCartFacade().resetConfiguration(configId);
			}
			getSessionAccessFacade().removeUiStatusForProduct(baseProduct);
		}
		getSessionAccessFacade().removeCartEntryForProduct(baseProduct);
	}

	protected ConfigurationOverviewData populateConfigurationModel(final String productCode,
			final ConfigurationOverviewData updatedOverview)
	{
		return getConfigurationOverviewFacade().getOverviewForProductVariant(productCode, updatedOverview);
	}

	protected void prepareUiModel(final HttpServletRequest request, final Model model, final String productCode,
			final ConfigurationOverviewData overview) throws CMSItemNotFoundException
	{
		final ProductData productData = populateProductData(productCode, model, request);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				productConfigurationBreadcrumbBuilder.getVariantOverviewBreadcrumbs(productData));

		final OverviewUiData overviewUIData = prepareOverviewUiData(overview, productData);

		model.addAttribute("overviewUiData", overviewUIData);
		model.addAttribute("pageType", "productConfigOverviewPage");
	}

	protected OverviewUiData prepareOverviewUiData(final ConfigurationOverviewData overview, final ProductData productData)
	{
		final OverviewUiData overviewUIData = new OverviewUiData();

		overviewUIData.setProductCode(productData.getCode());
		overviewUIData.setConfigurationOverviewData(overview);
		overviewUIData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW);
		overviewUIData.setQuantity(getQuantity(productData.getBaseProduct()));

		return overviewUIData;
	}

	protected long getQuantity(final String baseProduct)
	{
		final UiStatus uiStatus = getSessionAccessFacade().getUiStatusForProduct(baseProduct);
		if (uiStatus != null)
		{
			return uiStatus.getQuantity();
		}

		return 1;
	}

	@Override
	protected AbstractPageModel getPageForProduct() throws CMSItemNotFoundException
	{
		return getCmsPageService().getPageForId("productConfigOverview");
	}

	/**
	 * @param configurationOverviewFacade
	 *           injects the facade for the variant overview
	 */
	public void setConfigurationOverviewFacade(final ConfigurationOverviewFacade configurationOverviewFacade)
	{
		this.configurationOverviewFacade = configurationOverviewFacade;
	}

	protected ConfigurationOverviewFacade getConfigurationOverviewFacade()
	{
		return configurationOverviewFacade;
	}
}
