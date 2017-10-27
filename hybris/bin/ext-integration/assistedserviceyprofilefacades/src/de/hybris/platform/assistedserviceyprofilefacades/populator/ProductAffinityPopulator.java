/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceyprofilefacades.populator;



import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ProductAffinityPopulator<SOURCE extends NeighbourData, TARGET extends ProductAffinityData>
		implements Populator<SOURCE, TARGET>
{

	private final Logger LOG = Logger.getLogger(ProductAffinityPopulator.class);
	private ProductFacade productFacade;


	@Override
	public void populate(final SOURCE neighbourData, final TARGET productAffinityData) throws ConversionException
	{
		final Map<String, String> neighbourProperties = neighbourData.getProperties();
		final String productProfileId = neighbourProperties.get(YaasyprofileconnectConstants.PRODUCT_ID_FIELD);

		if (StringUtils.isEmpty(productProfileId))
		{
			throw new ConversionException("Product Id not found for node " + neighbourData.getId());
		}

		try
		{
			ProductData productData = null;
			productData = getProductFacade().getProductForCodeAndOptions(productProfileId,
					Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

			productAffinityData.setProductData(productData);
		}
		catch (final UnknownIdentifierException e) // preventing errors for not-found products
		{
			LOG.error("Product with Id [" + productProfileId + "] on yProfile not found in hybris", e);

		}

		productAffinityData.setViewCount(neighbourProperties.get(YaasyprofileconnectConstants.PRODUCT_VIEW_COUNT_FIELD));
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}
}