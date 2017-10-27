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

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ReviewForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiDataStats;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictChecker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


/**
 * Abstract base class for all CPQ UI controllers.
 */
public abstract class AbstractProductConfigController extends AbstractPageController
{
	public static final String LOG_URL = "Redirect to: '";
	public static final String ADDON_PREFIX = "addon:";
	public static final String PRODUCT_ATTRIBUTE = "product";
	public static final String LOG_CONFIG_DATA = UiStateHandler.LOG_CONFIG_DATA;
	public static final String CONFIGURATOR_TYPE = "CPQCONFIGURATOR";
	public static final String CONFIG_PAGE_ROOT = "/pages/configuration/configurationPage";
	public static final String CONFIG_ERROR_ROOT = "/pages/configuration/errorForward";
	public static final String OVERVIEW_PAGE_ROOT = "/pages/configuration/configurationOverviewPage";
	public static final String AJAX_SUFFIX = "ForAJAXRequests";

	private static final Logger LOGGER = Logger.getLogger(AbstractProductConfigController.class.getName());

	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;
	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartFacade;
	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;
	@Resource(name = "sapProductConfigValidator")
	private Validator productConfigurationValidator;
	@Resource(name = "sapProductConfigConflictChecker")
	private ConflictChecker productConfigurationConflictChecker;
	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "sapProductConfigBreadcrumbBuilder")
	private ProductConfigureBreadcrumbBuilder breadcrumbBuilder;
	@Resource(name = "sapProductConfigUiTrackingRecorder")
	private UiTrackingRecorder uiRecorder;

	protected final UiStatusSync uiStatusSync = new UiStatusSync();
	protected final UiStateHandler uiStateHandler = new UiStateHandler();

	@InitBinder(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE)
	protected void initBinder(final WebDataBinder binder)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("INIT Binder at: " + System.currentTimeMillis());
		}
		binder.setValidator(getProductConfigurationValidator());
	}

	protected BindingResult getBindingResultForConfig(final ConfigurationData configData, final UiStatus uiStatus)
	{
		uiStateHandler.resetGroupStatus(configData);
		BindingResult errors = new BeanPropertyBindingResult(configData, SapproductconfigaddonConstants.CONFIG_ATTRIBUTE);
		// UI-Errors
		Map<String, FieldError> userInputToRestore = null;
		if (uiStatus != null)
		{
			userInputToRestore = uiStatus.getUserInputToRestore();
			final Map<String, FieldError> userInputToRemember = uiStatus.getUserInputToRemember();
			userInputToRestore = uiStateHandler.mergeUiErrors(userInputToRestore, userInputToRemember);
			errors = uiStateHandler.restoreValidationErrorsOnGetConfig(userInputToRestore, configData, errors);
		}

		productConfigurationConflictChecker.checkConflicts(configData, errors);
		if (configData.getCartItemPK() != null && !configData.getCartItemPK().isEmpty())
		{
			productConfigurationConflictChecker.checkMandatoryFields(configData, errors);
			logConfigurationCheckDeviation(errors, configData);
		}
		getProductConfigurationConflictChecker().checkCompletness(configData);
		uiStateHandler.countNumberOfUiErrorsPerGroup(configData.getGroups());

		if (userInputToRestore != null)
		{
			final Map<String, FieldError> userInputToRemeber = uiStateHandler.findCollapsedErrorCstics(userInputToRestore,
					configData);
			uiStatus.setUserInputToRemember(userInputToRemeber);
			getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		}

		return errors;
	}

	/**
	 * The ConflictChecker checks only visible characteristics for consistency and completeness as only those
	 * characteristics might be changed by the user.<br>
	 * <br>
	 * If the model is modeled in a way that a conflict appears for an invisible characteristic or an invisible
	 * characteristic is mandatory but not filled this would not be identified by those checks but the overall
	 * configuration status is not consistent/complete. This leads to a situation where the configuration cannot be sent
	 * to the backend system.<br>
	 * <br>
	 * In this case the modeler needs to improve the model to avoid such situations. The user cannot do anything about
	 * this so this is only logged as an error as a hint for the modeler.
	 */
	protected void logConfigurationCheckDeviation(final BindingResult errors, final ConfigurationData configData)
	{
		if (!(configData.isComplete() && configData.isConsistent()) && !errors.hasErrors())
		{
			// Configuration is incomplete/inconsistent: check whether this is reflected in the BindingResult
			// BindingResult does not contain errors -> log deviation
			LOGGER.warn("HINT: Configuration model of product "
					+ configData.getKbKey().getProductCode()
					+ " needs to be improved! Configuration status is [complete="
					+ configData.isComplete()
					+ "; consistent="
					+ configData.isConsistent()
					+ "] but the ConflictChecker signals no errors, i.e. the inconsistency/incompleteness exists at characteristics invisible for the user. Thus the user has no information what went wrong.");
		}
	}

	protected void setCartItemPk(final ConfigurationData configData)
	{
		final String cartItemKey = getSessionAccessFacade().getCartEntryForProduct(configData.getKbKey().getProductCode());
		if (cartItemKey != null)
		{
			final boolean isItemInCart = configCartFacade.isItemInCartByKey(cartItemKey);
			if (!isItemInCart)
			{
				getSessionAccessFacade().removeCartEntryForProduct(configData.getKbKey().getProductCode());
			}
			else
			{
				configData.setCartItemPK(cartItemKey);
			}
		}
	}

	protected Integer getCartEntryNumber(final KBKeyData kbKey)
	{
		final String cartItemKey = getSessionAccessFacade().getCartEntryForProduct(kbKey.getProductCode());
		if (cartItemKey != null)
		{
			final PK cartItemPK = PK.parse(cartItemKey);
			final AbstractOrderEntryModel item = configCartFacade.findItemInCartByPK(cartItemPK);
			return item != null ? item.getEntryNumber() : null;
		}
		return null;
	}

	/**
	 * Creates a new configuration session. Either returns a default configuration or creates a configuration from the
	 * external configuration attached to a cart entry. <br>
	 * Stores a new UIStatus based on the new configuration in the session (per product).
	 *
	 * @return Null if no configuration could be created
	 */
	protected ConfigurationData loadNewConfiguration(final KBKeyData kbKey, final ProductData productData,
			final String cartItemHandle)
	{
		final ConfigurationData configData;
		if (cartItemHandle != null && StringUtils.isEmpty(productData.getBaseProduct()))
		{
			configData = configCartFacade.restoreConfiguration(kbKey, cartItemHandle);
			if (configData == null)
			{
				return null;
			}
		}
		else
		{
			configData = configFacade.getConfiguration(productData);
			kbKey.setProductCode(configData.getKbKey().getProductCode());
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Load new " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}

		uiStatusSync.setInitialStatus(configData);
		final UiStatus uiStatus = uiStatusSync.extractUiStatusFromConfiguration(configData);
		getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		return configData;
	}

	protected ConfigurationData reloadConfiguration(final KBKeyData kbKey, final String configId, final UiStatus uiStatus)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Reload " + LOG_CONFIG_DATA + configId + "']");
		}
		final ConfigurationData configData = this.getConfigData(kbKey, configId);
		uiStatusSync.applyUiStatusToConfiguration(configData, uiStatus);
		uiStateHandler.compileGroupForDisplay(configData, uiStatus);
		return configData;
	}

	protected ConfigurationData getConfigData(final KBKeyData kbKey, final String configId)
	{
		final ConfigurationData configContent = new ConfigurationData();
		configContent.setConfigId(configId);
		configContent.setKbKey(kbKey);
		final ConfigurationData configData = configFacade.getConfiguration(configContent);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Retrieve " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}
		return configData;
	}

	protected ProductData populateProductData(final String productCode, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		handleRequestContext(request, productCode);
		updatePageTitle(productCode, model);
		final ProductData productData = populateProductDetailForDisplay(productCode, model);
		model.addAttribute(new ReviewForm());
		model.addAttribute("pageType", "productConfigPage");

		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(productData.getKeywords());
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productData.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return productData;
	}

	protected ProductData populateProductDetailForDisplay(final String productCode, final Model model)
			throws CMSItemNotFoundException
	{
		final ProductData productData = getProductDataForProductCode(productCode);
		final AbstractPageModel configPage = getPageForProduct();

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Using CMS page: '" + configPage.getName() + "' [ '" + configPage.getUid() + "']");
		}
		storeCmsPageInModel(model, configPage);
		populateProductData(productData, model);

		return productData;
	}

	protected ProductData getProductDataForProductCode(final String productCode)
	{
		final ProductData productData = getProductFacade().getProductForCodeAndOptions(
				productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.STOCK));
		return productData;
	}

	protected void populateProductData(final ProductData productData, final Model model)
	{
		model.addAttribute("galleryImages", getGalleryImages(productData));
		model.addAttribute(PRODUCT_ATTRIBUTE, productData);
	}

	protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData)
	{
		if (CollectionUtils.isEmpty(productData.getImages()))
		{
			return Collections.emptyList();
		}

		final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
		final List<ImageData> images = new ArrayList<>();
		for (final ImageData image : productData.getImages())
		{
			if (ImageDataType.GALLERY.equals(image.getImageType()))
			{
				images.add(image);
			}
		}

		if (CollectionUtils.isNotEmpty(images))
		{
			Collections.sort(images, (image1, image2) -> image1.getGalleryIndex().compareTo(image2.getGalleryIndex()));
			int currentIndex = images.get(0).getGalleryIndex().intValue();
			Map<String, ImageData> formats = new HashMap<>();
			for (final ImageData image : images)
			{
				if (currentIndex != image.getGalleryIndex().intValue())
				{
					galleryImages.add(formats);
					formats = new HashMap<>();
					currentIndex = image.getGalleryIndex().intValue();
				}
				formats.put(image.getFormat(), image);
			}
			if (!formats.isEmpty())
			{
				galleryImages.add(formats);
			}
		}
		return galleryImages;
	}

	protected boolean isProductVariant(final ProductData productData)
	{
		return StringUtils.isNotEmpty(productData.getBaseProduct());
	}

	protected void ifProductVariant(final HttpServletRequest request, final Model model, final ProductData productData,
			final KBKeyData kbKey) throws CMSItemNotFoundException
	{
		if (isProductVariant(productData))
		{
			cleanUpSessionAttribute(productData.getBaseProduct());
			populateProductData(productData.getBaseProduct(), model, request);
			model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbBuilder().getBreadcrumbs(productData.getBaseProduct()));
			kbKey.setProductCode(productData.getBaseProduct());
		}
	}

	protected void cleanUpSessionAttribute(final String baseProduct)
	{
		if (getSessionAccessFacade().getCartEntryForProduct(baseProduct) != null)
		{
			getSessionAccessFacade().removeCartEntryForProduct(baseProduct);
		}
	}

	protected AbstractPageModel getPageForProduct() throws CMSItemNotFoundException
	{
		return getCmsPageService().getPageForId("productConfig");
	}

	protected KBKeyData createKBKeyForProduct(final ProductData productData)
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productData.getCode());
		return kbKey;
	}

	protected void removeNullCsticsFromGroup(final List<CsticData> dirtyList)
	{
		if (dirtyList == null)
		{
			return;
		}

		final List<CsticData> cleanList = new ArrayList<>(dirtyList.size());

		for (final CsticData data : dirtyList)
		{
			if (data.getName() != null && (data.getType() != UiType.READ_ONLY || data.isRetractTriggered()))
			{
				cleanList.add(data);
			}
		}

		dirtyList.clear();
		dirtyList.addAll(cleanList);
	}

	protected void removeNullCstics(final List<UiGroupData> groups)
	{
		if (groups == null)
		{
			return;
		}

		for (final UiGroupData group : groups)
		{
			removeNullCsticsFromGroup(group.getCstics());

			final List<UiGroupData> subGroups = group.getSubGroups();
			removeNullCstics(subGroups);
		}
	}

	protected void handleRequestContext(final HttpServletRequest request, final String productCode)
	{
		final RequestContextData requestContext = getRequestContextData(request);
		if (requestContext != null)
		{
			requestContext.setProduct(getProductService().getProductForCode(productCode));
		}
	}

	protected void logModelmetaData(final ConfigurationData configData)
	{
		if (LOGGER.isDebugEnabled())
		{
			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug("Modelstats of product '" + configData.getKbKey().getProductCode() + "' after Update: '" + numbers + "'");
		}
	}

	protected void logRequestMetaData(final ConfigurationData configData, final HttpServletRequest request)
	{
		if (LOGGER.isDebugEnabled())
		{
			final NumberFormat decFormat = DecimalFormat.getInstance(Locale.ENGLISH);
			LOGGER.debug("Update Configuration of product '" + configData.getKbKey().getProductCode() + "'");
			LOGGER.debug("ContentLength=" + decFormat.format(request.getContentLength()) + " Bytes; numberParams="
					+ decFormat.format(request.getParameterMap().size()));

			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug(numbers);
		}
	}

	protected void updatePageTitle(final String productCode, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productCode));
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	protected SessionAccessFacade getSessionAccessFacade()
	{
		return sessionAccessFacade;
	}

	protected Validator getProductConfigurationValidator()
	{
		return productConfigurationValidator;
	}

	protected ConflictChecker getProductConfigurationConflictChecker()
	{
		return productConfigurationConflictChecker;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected ProductService getProductService()
	{
		return productService;
	}



	protected OrderEntryData getOrderEntry(final int entryNumber, final CartData cart) throws CommerceCartModificationException
	{
		final List<OrderEntryData> entries = cart.getEntries();
		if (entries == null)
		{
			throw new CommerceCartModificationException("Cart is empty");
		}
		try
		{
			return entries.stream().filter(e -> e != null).filter(e -> e.getEntryNumber().intValue() == entryNumber).findAny().get();
		}
		catch (final NoSuchElementException e)
		{
			throw new CommerceCartModificationException("Cart entry #" + entryNumber + " does not exist", e);
		}
	}

	protected ProductConfigureBreadcrumbBuilder getBreadcrumbBuilder()
	{
		return breadcrumbBuilder;
	}

	/**
	 * @param configFacade
	 *           CPQ facade
	 */
	public void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	/**
	 * @param configCartFacade
	 *           CPQ cart integration facade
	 */
	public void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
	}

	/**
	 * @param sessionAccessFacade
	 *           CPQ session cache access
	 */
	public void setSessionAccessFacade(final SessionAccessFacade sessionAccessFacade)
	{
		this.sessionAccessFacade = sessionAccessFacade;
	}

	/**
	 * @param productConfigurationValidator
	 *           CPQ validator
	 */
	public void setProductConfigurationValidator(final Validator productConfigurationValidator)
	{
		this.productConfigurationValidator = productConfigurationValidator;
	}

	/**
	 * @param productConfigurationConflictChecker
	 *           status and UI error handling&checking
	 */
	public void setProductConfigurationConflictChecker(final ConflictChecker productConfigurationConflictChecker)
	{
		this.productConfigurationConflictChecker = productConfigurationConflictChecker;
	}

	/**
	 * @param productFacade
	 *           for accessing product master data
	 */
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	/**
	 * @param productService
	 *           for accessing product related service
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @param productConfigurationBreadcrumbBuilder
	 *           for building UI breadcrumbs
	 */
	public void setBreadcrumbBuilder(final ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder)
	{
		this.breadcrumbBuilder = productConfigurationBreadcrumbBuilder;
	}


	protected UiTrackingRecorder getUiRecorder()
	{
		return uiRecorder;
	}

	/**
	 * @param uiRecorder
	 *           triggering CPQ tracking
	 */
	public void setUiRecorder(final UiTrackingRecorder uiRecorder)
	{
		this.uiRecorder = uiRecorder;
	}
}
