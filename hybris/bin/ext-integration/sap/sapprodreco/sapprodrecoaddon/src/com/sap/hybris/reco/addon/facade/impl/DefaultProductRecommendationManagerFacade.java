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
package com.sap.hybris.reco.addon.facade.impl;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.product.data.ReferenceData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.reco.addon.facade.ProductRecommendationManagerFacade;
import com.sap.hybris.reco.bo.ProductRecommendationManagerBO;
import com.sap.hybris.reco.common.util.HMCConfigurationReader;
import com.sap.hybris.reco.common.util.UserIdProvider;
import com.sap.hybris.reco.dao.InteractionContext;
import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.model.CMSSAPRecommendationComponentModel;


/**
 *
 */
public class DefaultProductRecommendationManagerFacade implements ProductRecommendationManagerFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProductRecommendationManagerFacade.class);
	private Converter<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData> referenceDataProductReferenceConverter;
	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> referenceProductConfiguredPopulator;

	private GenericFactory genericFactory;
	private UserService userService;
	private CartService cartService;
	private UserIdProvider userIDProvider;
	private String anonOriginOfContactId;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "hmcConfigurationReader")
	private HMCConfigurationReader configuration;

	@Resource(name = "sessionService")
	private SessionService sessionService;
	private List<String> leadingCategoriesIds;

	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	/**
	 * Get product recommendations based on current context
	 *
	 */
	@Override
	public List<ProductReferenceData> getProductRecommendation(RecommendationContext context)
	{
		final List<ProductReferenceData> result = new ArrayList<ProductReferenceData>();

		for (final ReferenceData<ProductReferenceTypeEnum, ProductModel> reference : createReferenzList(context))
		{
			final ProductReferenceData productReferenceData = getReferenceDataProductReferenceConverter().convert(
					reference);
			getReferenceProductConfiguredPopulator().populate(reference.getTarget(), productReferenceData.getTarget(),
					PRODUCT_OPTIONS);
			result.add(productReferenceData);
		}

		return result;
	}

	@Override
	public List<CMSSAPRecommendationComponentModel> getRecommendationComponentForPage(final AbstractPageModel pageModel)
	{

		final List<CMSSAPRecommendationComponentModel> prodRecoComponents = new ArrayList<CMSSAPRecommendationComponentModel>();
		for (final ContentSlotForPageModel contentSlot : pageModel.getContentSlots())
		{
			for (final AbstractCMSComponentModel component : contentSlot.getContentSlot().getCmsComponents())
			{
				if (component.getItemtype().equals(CMSSAPRecommendationComponentModel._TYPECODE) && component.getVisible())
				{
					prodRecoComponents.add((CMSSAPRecommendationComponentModel) component);
				}
			}
		}
		return prodRecoComponents;
	}

	@Override
	public void postInteraction(final InteractionContext context)
	{
		getRecommendationManager().postInteraction(context);
	}

	private List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> createReferenzList(final RecommendationContext context)
	{
		final List<ProductRecommendationData> recommendations = getRecommendationManager().getProductRecommendation(context);

		if (recommendations == null)
		{
			return Collections.emptyList();
		}
		
		final List<ReferenceData<ProductReferenceTypeEnum, ProductModel>> ref = new ArrayList<>();
		
		for (final ProductRecommendationData recommendation : recommendations)
		{
			try
			{
				ref.add(createReferenceData(productService.getProductForCode(recommendation.getProductCode())));
			}
			catch (IllegalArgumentException | UnknownIdentifierException | AmbiguousIdentifierException e)
			{
				LOGGER.warn("Product='{}' not found in session's catalog, recotype='{}', userType='{}'", recommendation.getProductCode(), context.getRecotype(), context.getUserType());
			}
		}
		return ref;
	}

	private ReferenceData<ProductReferenceTypeEnum, ProductModel> createReferenceData(final ProductModel product)
	{
		final ReferenceData<ProductReferenceTypeEnum, ProductModel> referenceData = new ReferenceData<ProductReferenceTypeEnum, ProductModel>();
		referenceData.setQuantity(1);
		referenceData.setReferenceType(ProductReferenceTypeEnum.OTHERS);
		referenceData.setTarget(product);
		return referenceData;
	}

	@Override
	public String getSessionUserId()
	{
		final UserModel currentUser = userService.getCurrentUser();
		return userIDProvider.getUserId(currentUser);
	}
	
	@Override
	public boolean isSessionUserAnonymous()
	{
		final UserModel currentUser = userService.getCurrentUser();
		return userService.isAnonymousUser(currentUser);
	}

	@Override
	public void populateContext(final RecommendationContext context, final CMSSAPRecommendationComponentModel component, final String leadingProductId, final String userId, final String cookieId)
	{
		context.setLeadingProductId(leadingProductId);
		context.setLeadingCategoryIds(this.leadingCategoriesIds);
		context.setRecotype(component.getRecotype());
		context.setIncludeCart(BooleanUtils.isTrue(component.isIncludecart()));
		context.setIncludeRecent(BooleanUtils.isTrue(component.isIncluderecent()));
		context.setLeadingItemDSType(component.getLeadingitemdstype());
		context.setLeadingItemType(component.getLeadingitemtype());
		context.setComponentModel(component);
		if (this.isSessionUserAnonymous())
		{
			context.setUserType(this.getAnonOriginOfContactId());
			context.setUserId(cookieId);
		}
		else
		{
			context.setUserType(configuration.getUserType());
			context.setUserId(userId);
		}
		context.setCartItemDSType(component.getCartitemdstype());
	}

	protected ProductRecommendationManagerBO getRecommendationManager()
	{
		return genericFactory.getBean("sapProductRecommendationManagerBO");
	}

	/**
	 * @return the referenceDataProductReferenceConverter
	 */
	public Converter<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData> getReferenceDataProductReferenceConverter()
	{
		return referenceDataProductReferenceConverter;
	}

	/**
	 * @param referenceDataProductReferenceConverter
	 *           the referenceDataProductReferenceConverter to set
	 */
	public void setReferenceDataProductReferenceConverter(
			final Converter<ReferenceData<ProductReferenceTypeEnum, ProductModel>, ProductReferenceData> referenceDataProductReferenceConverter)
	{
		this.referenceDataProductReferenceConverter = referenceDataProductReferenceConverter;
	}

	/**
	 * @return the referenceProductConfiguredPopulator
	 */
	public ConfigurablePopulator<ProductModel, ProductData, ProductOption> getReferenceProductConfiguredPopulator()
	{
		return referenceProductConfiguredPopulator;
	}

	/**
	 * @param referenceProductConfiguredPopulator
	 *           the referenceProductConfiguredPopulator to set
	 */
	public void setReferenceProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ProductData, ProductOption> referenceProductConfiguredPopulator)
	{
		this.referenceProductConfiguredPopulator = referenceProductConfiguredPopulator;
	}

	/**
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 *           the genericFactory to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the userIDProvider
	 */
	public UserIdProvider getUserIDProvider()
	{
		return userIDProvider;
	}

	/**
	 * @param userIDProvider
	 *           the userIDProvider to set
	 */
	public void setUserIDProvider(final UserIdProvider userIDProvider)
	{
		this.userIDProvider = userIDProvider;
	}

	@Override
	public String getAnonOriginOfContactId()
	{
		return anonOriginOfContactId;
	}

	@Override
	public void setAnonOriginOfContactId(final String anonOriginOfContactId)
	{
		this.anonOriginOfContactId = anonOriginOfContactId.intern();
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the configuration
	 */
	public HMCConfigurationReader getConfiguration()
	{
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(final HMCConfigurationReader configuration)
	{
		this.configuration = configuration;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public void storeLeadingCategories(List<String> categoryCodes) 
	{
		this.leadingCategoriesIds = categoryCodes;
	}

}
