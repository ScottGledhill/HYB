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

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


/**
 * Default implementtaion of the {@link ConfigurationProviderFactory}.
 */
public class ConfigurationProviderFactoryImpl implements ConfigurationProviderFactory
{

	private static final Logger LOG = Logger.getLogger(ConfigurationProviderFactoryImpl.class);

	private SessionAccessService sessionAccessService;
	private ApplicationContext applicationContext;

	protected static final String SESSION_CACHE_KEY = ConfigurationProvider.class.getName();

	@Override
	public ConfigurationProvider getProvider()
	{
		ConfigurationProvider provider = sessionAccessService.getConfigurationProvider();
		if (provider == null)
		{
			provider = createProviderInstance();
			sessionAccessService.setConfigurationProvider(provider);
		}
		return provider;
	}

	protected ConfigurationProvider createProviderInstance()
	{
		ConfigurationProvider provider;

		ApplicationContext applCtxt = getApplicationContext();

		if (applCtxt == null)
		{
			applCtxt = ServicelayerUtils.getApplicationContext();
			setApplicationContext(applCtxt);
		}

		if (applCtxt == null)
		{
			throw new IllegalStateException("Application Context not available");
		}

		final String providerBeanName = "sapProductConfigConfigurationProvider";

		provider = (ConfigurationProvider) applicationContext.getBean(providerBeanName);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("created a new configuration provider instance");
		}

		return provider;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * used for tests
	 */
	void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}
}
