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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.FindPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;


/**
 * CPQ specific sub-class of the {@link FindPricingWithCurrentPriceFactoryStrategy}. This class ensures, that the base
 * price of any configurable cart entry is kept, in case the cart is reclaculated.
 */
public class FindPricingForConfigurableProductsStrategy extends FindPricingWithCurrentPriceFactoryStrategy
{

	private static final Logger LOG = Logger.getLogger(FindPricingForConfigurableProductsStrategy.class);


	@Override
	public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		final PriceValue basePrice;
		final Boolean isConfigurable = entry.getProduct().getSapConfigurable();
		if (isConfigurable != null && isConfigurable.booleanValue())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Keeping old base price for configurable product " + entry.getProduct().getCode());
			}

			final AbstractOrderModel order = entry.getOrder();
			basePrice = new PriceValue(order.getCurrency().getIsocode(), entry.getBasePrice().doubleValue(), order.getNet()
					.booleanValue());

		}
		else
		{
			basePrice = super.findBasePrice(entry);
		}
		return basePrice;
	}
}
