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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.CsticData;

import org.springframework.validation.Errors;


/**
 * UI validator for numeric values.
 */
public interface NumericChecker
{


	/**
	 * Validates the value of the cstic.
	 *
	 * @param cstic
	 *           to check
	 * @param errorObj
	 *           to add errors if found
	 */
	void validate(final CsticData cstic, final Errors errorObj);

	/**
	 * Validates the additional Value of the cstic
	 *
	 * @param cstic
	 * @param errorObj
	 */
	void validateAdditionalValue(CsticData cstic, Errors errorObj);

}
