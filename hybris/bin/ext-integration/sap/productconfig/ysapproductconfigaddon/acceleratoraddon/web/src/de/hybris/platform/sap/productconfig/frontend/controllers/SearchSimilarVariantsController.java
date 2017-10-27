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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantFacade;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller for variant search
 */
@Controller
@RequestMapping()
public class SearchSimilarVariantsController
{

	private static final Logger LOG = Logger.getLogger(SearchSimilarVariantsController.class);
	private static final String SEARCH_VARIANTS = "/pages/configuration/searchVariants";
	private static final String AJAX_VIEW_NAME = AbstractProductConfigController.ADDON_PREFIX
			+ AbstractProductConfigController.ROOT + SapproductconfigaddonConstants.EXTENSIONNAME + SEARCH_VARIANTS
			+ AbstractProductConfigController.AJAX_SUFFIX;

	@Resource(name = "sapProductConfigVariantFacade")
	private ConfigurationVariantFacade variantFacade;

	/**
	 * Searches similar varaints considering the value assigments of the given runtime configuration. Renders the results
	 * as variant caraousel.
	 *
	 * @param configId
	 *           configuration session id
	 * @param productCode
	 *           code of the configurable product
	 * @param model
	 *           ciew model
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/configur*/**/searchConfigVariant", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView searchVariant(@RequestParam
	final String configId, @RequestParam
	final String productCode, final Model model) throws CMSItemNotFoundException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Recieved search request for similar variants with configId=" + configId + " and productCode=" + productCode);
		}

		final List<ConfigurationVariantData> variants = getVariantFacade().searchForSimilarVariants(configId, productCode);
		model.addAttribute(SapproductconfigaddonConstants.VARIANT_SEARCH_RESULT, variants);
		final ModelAndView view = new ModelAndView(getViewName());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Rendering " + variants.size() + " matched variants");
		}
		return view;
	}

	protected String getViewName()
	{
		return AJAX_VIEW_NAME;
	}


	protected ConfigurationVariantFacade getVariantFacade()
	{
		return variantFacade;
	}


	/**
	 * @param variantFacade
	 *           injects the facade object used by this controller for variant search
	 */
	public void setVariantFacade(final ConfigurationVariantFacade variantFacade)
	{
		this.variantFacade = variantFacade;
	}
}
