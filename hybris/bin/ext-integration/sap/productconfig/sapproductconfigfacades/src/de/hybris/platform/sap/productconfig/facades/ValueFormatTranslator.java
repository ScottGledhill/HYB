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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper for formatting characteristic values. The external format for UI Display is derived from the web-shops I18N
 * locale, which in turn depends on the browser's locale.
 */
public interface ValueFormatTranslator
{

	/**
	 * Converts a characteristic value into an internal format, which is used by the underlying API's.
	 *
	 * @param uiType
	 *           UI type the characteristic value belongs to
	 * @param value
	 *           characteristic value in external format
	 * @return characteristic value in internal format
	 */
	String parse(UiType uiType, String value);

	/**
	 * @deprecated use {@link #format(CsticModel, String)} instead.
	 */
	@Deprecated
	String format(UiType uiType, String singleValue);

	/**
	 * Converts a characteristic value into an external format suitable for UI-Display.
	 *
	 * @param cstic
	 *           characteristic the value belongs to
	 * @param value
	 *           characteristic value in internal format
	 * @return characteristic value in external format
	 */
	String format(final CsticModel cstic, final String value);

	/**
	 * Checks the characteristic model whether it is either of type float or integer
	 *
	 * @param model
	 *           characteristic to be checked
	 * @return true if characteristic is numeric
	 */
	boolean isNumericCsticType(final CsticModel model);
}