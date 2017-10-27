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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticNumericValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageImpl;


public class ConfigModelFactoryImpl implements ConfigModelFactory
{

	private static ConfigModelFactory factoryInstance;

	public static ConfigModelFactory getInstance()
	{
		if (factoryInstance == null)
		{
			factoryInstance = new ConfigModelFactoryImpl();
		}

		return factoryInstance;
	}

	@Override
	public ConfigModel createInstanceOfConfigModel()
	{
		return new ConfigModelImpl();
	}

	@Override
	public InstanceModel createInstanceOfInstanceModel()
	{
		return new InstanceModelImpl();
	}

	@Override
	public CsticModel createInstanceOfCsticModel()
	{
		return new CsticModelImpl();
	}

	@Override
	public CsticValueModel createInstanceOfCsticValueModel(final int valueType)
	{
		if (valueType == CsticModel.TYPE_FLOAT || valueType == CsticModel.TYPE_INTEGER)
		{
			return new CsticNumericValueModelImpl();
		}
		return new CsticValueModelImpl();
	}

	@Override
	public CsticGroupModel createInstanceOfCsticGroupModel()
	{
		return new CsticGroupModelImpl();
	}

	@Override
	public PriceModel createInstanceOfPriceModel()
	{
		return new PriceModelImpl();
	}


	@Override
	public PriceModel getZeroPriceModel()
	{
		return PriceModel.NO_PRICE;
	}


	@Override
	public ProductConfigMessage createInstanceOfProductConfigMessage(final String message, final String key,
			final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source)
	{
		return new ProductConfigMessageImpl(message, key, severity, source);
	}

}
