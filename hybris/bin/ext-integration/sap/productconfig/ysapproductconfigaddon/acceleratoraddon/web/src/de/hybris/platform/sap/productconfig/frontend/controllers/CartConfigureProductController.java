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
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Cart-Configuration integration
 */
@Controller
@RequestMapping()
public class CartConfigureProductController extends AbstractProductConfigController
{

	@Resource
	private CartFacade cartFacade;
	private final UiStatusSync uiSync = new UiStatusSync();
	private static final Logger LOGGER = Logger.getLogger(CartConfigureProductController.class);

	/**
	 * Prepares a configuration session for the given cart item. This includes re-creation of the configuration sessions,
	 * in case it was already releases, as well as restoring of the UI-State.
	 *
	 * @param entryNumber
	 *           of the configurable cart item
	 * @param model
	 *           vie model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "cart/{entryNumber}/configuration/" + CONFIGURATOR_TYPE)
	public String configureCartEntry(@PathVariable("entryNumber")
	final int entryNumber, final Model model, final HttpServletRequest request) throws CommerceCartModificationException
	{
		final CartData sessionCart = getCartFacade().getSessionCart();
		final OrderEntryData currentEntry = getOrderEntry(entryNumber, sessionCart);
		final String cartItemHandle = currentEntry.getItemPK();
		final String productCode = currentEntry.getProduct().getCode();

		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		final UiStatus uiStatus = getUiStatusFromSession(cartItemHandle, kbKey, currentEntry.getProduct());
		if (uiStatus == null)
		{
			return REDIRECT_PREFIX + ROOT;
		}

		try
		{
			populateConfigurationModel(request, model, currentEntry, productCode);
		}
		catch (final CMSItemNotFoundException cnfe)
		{
			throw new CommerceCartModificationException("Root cause: CMSItemNotFoundException", cnfe);
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Retrieve content for cartEntry via GET ('" + cartItemHandle + "')");
			LOGGER.debug("Current Session: '" + getSessionService().getCurrentSession().getSessionId() + "'");
		}

		return ADDON_PREFIX + ROOT + SapproductconfigaddonConstants.EXTENSIONNAME + CONFIG_PAGE_ROOT;
	}

	protected void populateConfigurationModel(final HttpServletRequest request, final Model model,
			final OrderEntryData currentEntry, final String productCode) throws CMSItemNotFoundException
	{
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbBuilder().getBreadcrumbs(productCode));

		if (model.containsAttribute(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE))
		{
			return;
		}

		final ProductData productData = populateProductData(productCode, model, request);
		final KBKeyData kbKey = createKBKeyForProduct(productData);

		UiStatus uiStatus = getUiStatusFromSession(currentEntry.getItemPK(), kbKey, productData);
		final String configId = uiStatus.getConfigId();
		ifProductVariant(request, model, productData, kbKey);
		final ConfigurationData configData = reloadConfiguration(kbKey, configId, uiStatus);

		// assume currentEntry != null
		configData.setCartItemPK(currentEntry.getItemPK());
		configData.setQuantity(currentEntry.getQuantity().longValue());

		model.addAttribute(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE, configData);

		final BindingResult errors = getBindingResultForConfig(configData, uiStatus);

		configData.setAutoExpand(true);

		final UiGroupData expandedGroup = uiStateHandler.handleAutoExpand(configData, uiStatus);
		if (expandedGroup != null)
		{
			uiStatus.setGroupIdToDisplay(expandedGroup.getId());
			uiStateHandler.compileGroupForDisplay(configData, uiStatus);
		}
		uiStatus = uiSync.extractUiStatusFromConfiguration(configData);
		getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		getSessionAccessFacade().setCartEntryForProduct(configData.getKbKey().getProductCode(), currentEntry.getItemPK());

		getUiRecorder().recordUiAccessFromCart(configData, productCode);

		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigaddonConstants.CONFIG_ATTRIBUTE, errors);

		uiStateHandler.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);
		uiStateHandler.handleProductConfigMessages(configData, model);
	}

	/**
	 * Retrieves UI status based on a configuration attached to a cart entry
	 *
	 * @param cartItemHandle
	 * @param kbKey
	 * @return Null if no UI status could be created.
	 */
	protected UiStatus getUiStatusFromSession(final String cartItemHandle, final KBKeyData kbKey, final ProductData productData)
	{
		UiStatus uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(cartItemHandle);

		//this shall happen only when the cart is restored or if variant in cart is switched to KMAT (clicking change cfg for variant in cart)
		if (uiStatus == null)
		{
			final String configId = getSessionAccessFacade().getConfigIdForCartEntry(cartItemHandle);
			final ConfigurationData confData = getConfigDataForRestoredProduct(kbKey, productData, configId, cartItemHandle);
			if (confData == null)
			{
				return null;
			}

			logModelmetaData(confData);

			final UiStatusSync uiStatusSync = new UiStatusSync();
			uiStatusSync.setInitialStatus(confData);
			uiStatus = uiStatusSync.extractUiStatusFromConfiguration(confData);
			getSessionAccessFacade().setUiStatusForCartEntry(cartItemHandle, uiStatus);
		}
		return uiStatus;
	}

	/**
	 * Fetches a configuration which might already reside in the session (configId != null) or which needs to be created
	 * from the external configuration attached to a cart entry.
	 *
	 * @param kbKey
	 * @param configId
	 * @param cartItemHandle
	 * @return Null if no configuration could be created
	 */
	protected ConfigurationData getConfigDataForRestoredProduct(final KBKeyData kbKey, final ProductData productData,
			final String configId, final String cartItemHandle)
	{
		ConfigurationData confData;
		if (configId == null)
		{
			confData = this.loadNewConfiguration(kbKey, productData, cartItemHandle);
		}
		else
		{
			confData = this.getConfigData(kbKey, configId);
		}
		return confData;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @param cartFacade
	 *           injects the cart facade for interacting with the cart
	 */
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
