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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ZeroPriceModelImpl;

import java.math.BigDecimal;


/**
 * Represents the price model including currency.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
@SuppressWarnings("squid:S1214")
public interface PriceModel extends BaseModel
{

	/**
	 * Value-Object to model the case, when no price information is available
	 */
	PriceModel NO_PRICE = new ZeroPriceModelImpl();

	/**
	 * @param currency
	 *           price currency
	 */
	void setCurrency(String currency);

	/**
	 * @return price currency
	 */
	String getCurrency();

	/**
	 * @return price value
	 */
	BigDecimal getPriceValue();

	/**
	 * @param priceValue
	 *           price value
	 */
	void setPriceValue(BigDecimal priceValue);

	/**
	 * Clone is needed as the mock configurator implementation relies on clone to create new instances
	 *
	 * @return cloned <code>PriceModel</code>
	 */
	@Override
	PriceModel clone(); //NOSONAR

	/**
	 * Checks whether this is a valid price
	 *
	 * @return <code>true</code> only if a NON-Zero price value and a currency are assigned
	 */
	boolean hasValidPrice();

}
