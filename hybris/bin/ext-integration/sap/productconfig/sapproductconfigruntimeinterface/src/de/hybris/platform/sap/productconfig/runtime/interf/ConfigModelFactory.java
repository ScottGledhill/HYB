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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;


/**
 * Create instances of all configuration model elements.
 */
public interface ConfigModelFactory
{
	/**
	 * Create an instance of the <code>ConfigModel</code>
	 *
	 * @return an instance of the configuration model
	 */
	ConfigModel createInstanceOfConfigModel();

	/**
	 * Create an instance of the <code>InstanceModel</code>
	 *
	 * @return an instance of the instance model
	 */
	InstanceModel createInstanceOfInstanceModel();

	/**
	 * Create an instance of the <code>CsticModel</code>
	 *
	 * @return an instance of the characteristic model
	 */
	CsticModel createInstanceOfCsticModel();

	/**
	 * Create an instance of the <code>CsticValueModel</code>
	 *
	 * @param valueType
	 *           The containing Cstic value type
	 * @return an instance of the characteristic value model
	 */
	CsticValueModel createInstanceOfCsticValueModel(int valueType);

	/**
	 * Create an instance of the <code>CsticGroupModel</code>
	 *
	 * @return an instance of the characteristic group model
	 */
	CsticGroupModel createInstanceOfCsticGroupModel();


	/**
	 * Create an instance of the <code>PriceModel</code>
	 *
	 * @return an instance of the price model
	 */
	PriceModel createInstanceOfPriceModel();


	/**
	 * Create an instance of the <code>PriceModel</code>
	 *
	 * @return an instance of the price model
	 */
	PriceModel getZeroPriceModel();

	/**
	 * Create an instance of the <code>ProductConfigMessage</code>
	 *
	 * @param message
	 *           localized message
	 * @param key
	 *           message key, should be unique together with message source
	 * @param severity
	 *           message severity
	 * @param source
	 *           message source, should be unique together with message key
	 * @return a message instance
	 */
	ProductConfigMessage createInstanceOfProductConfigMessage(String message, String key, ProductConfigMessageSeverity severity,
			ProductConfigMessageSource source);
}
