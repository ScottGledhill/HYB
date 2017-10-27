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

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationService}.<br>
 * This implementation will synchronize access to the {@link ConfigurationProvider}, so that it is guaranteed that only
 * exactly one thread will access the configuration provider for a given configuration session. Furthermore a simple
 * session based read cache ensures that subsequent calls to read the same configuration result only into exactly one
 * read request to the configuration engine.
 *
 * @see ProductConfigurationServiceImpl#setMaxLocksPerMap(int)
 * @see ProductConfigurationServiceImpl#setMaxCachedConfigsInSession(int)
 */
public class ProductConfigurationServiceImpl implements ProductConfigurationService
{

	protected static final String DEBUG_CONFIG_WITH_ID = "Config with id '";
	static final Object PROVIDER_LOCK = new Object();
	static final Object CACHE_LOCK = new Object();

	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImpl.class);

	private int maxCachedConfigMapSize = 5;
	private Set<String> cachedConfigIds = new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1));
	private Set<String> oldCachedConfigIds = new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1));

	private static int maxLocksPerMap = 1024;
	private static Map<String, Object> locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
	private static Map<String, Object> oldLocks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));

	private ConfigurationProviderFactory configurationProviderFactory;

	private SessionAccessService sessionAccessService;
	private TrackingRecorder recorder;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// no need to synchronize create, because config session (identified by
		// the config ID)
		// is only exposed once the object has been created
		final ConfigModel config = getConfigurationProvider().createDefaultConfiguration(kbKey);
		cacheConfig(config);
		recorder.recordCreateConfiguration(config, kbKey);
		return config;

	}

	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		final String id = model.getId();
		final Object lock = ProductConfigurationServiceImpl.getLock(id);
		synchronized (lock)
		{

			final boolean updateExecuted = getConfigurationProvider().updateConfiguration(model);
			if (updateExecuted)
			{
				recorder.recordUpdateConfiguration(model);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(DEBUG_CONFIG_WITH_ID + model.getId() + "' updated, removing it from cache");
				}
				removeConfigFromCache(id);
			}
		}
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = sessionAccessService.getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{
				cachedModel = retrieveConfigurationModelFromConfigurationEngine(configId);
				cacheConfig(configId, cachedModel);
				recorder.recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngine(final String configId)
	{
		return getConfigurationProvider().retrieveConfigurationModel(configId);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		final Object lock = getLock(configId);
		synchronized (lock)
		{
			return getConfigurationProvider().retrieveExternalConfiguration(configId);
		}
	}

	/**
	 * @param configurationProviderFactory
	 *           inject factory to access the configuration provider
	 */
	@Required
	public void setConfigurationProviderFactory(final ConfigurationProviderFactory configurationProviderFactory)
	{
		this.configurationProviderFactory = configurationProviderFactory;
	}

	/**
	 * A configuration provider lock ensures, that there are no concurrent requests send to the configuration engine for
	 * the same configuration session.<br>
	 * We might not always get informed when a configuration session is released, hence we do not rely on this. Instead
	 * we just keep a maximum number of locks and release the oldest locks, when there are to many. The maximum number
	 * can be configured by this setter. <br>
	 * A look can be re-created in case it had already been deleted. The number should be high enough, so that locks do
	 * not get deleted while some concurrent threads are still using the lock, as this could cause concurrency issue.
	 * <b>The maximum number heavily depends on the number of concurrent threads expected.</b> Default is 1024.
	 *
	 * @param maxLocksPerMap
	 *           sets the maximum number of Configuration Provider Locks kept.
	 */
	public static void setMaxLocksPerMap(final int maxLocksPerMap)
	{
		ProductConfigurationServiceImpl.maxLocksPerMap = maxLocksPerMap;
	}

	protected static int getMaxLocksPerMap()
	{
		return ProductConfigurationServiceImpl.maxLocksPerMap;
	}

	protected int getMaxCachedConfigsInSession()
	{
		return maxCachedConfigMapSize * 2;
	}

	/**
	 * Re-reading a configuration from the configuration engine can be expensive, especially for large configurations.
	 * This is only necessary when the configuration was updated since the last time being read. To make life for callers
	 * easier, this implementation features a simple read-cache for configurations based on the user session. So any
	 * calls to read configuration will always result in a cache hit until the configuration is updated.
	 *
	 * @param maxCachedConfigsInSession
	 *           set the maximum number of configs to be cached in the session. Default is 10.
	 */
	public void setMaxCachedConfigsInSession(final int maxCachedConfigsInSession)
	{
		this.maxCachedConfigMapSize = maxCachedConfigsInSession / 2;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return configurationProviderFactory.getProvider();
	}

	protected static Object getLock(final String configId)
	{
		synchronized (PROVIDER_LOCK)
		{

			Object lock = locks.get(configId);
			if (lock == null)
			{
				lock = oldLocks.get(configId);
				if (lock == null)
				{
					ensureThatLockMapIsNotTooBig();
					lock = new Object();
					locks.put(configId, lock);
				}
			}
			return lock;
		}
	}

	protected static void ensureThatLockMapIsNotTooBig()
	{
		if (locks.size() >= maxLocksPerMap)
		{
			oldLocks.clear();
			oldLocks = locks;
			// avoid rehashing, create with sufficient capacity
			locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
		}
	}

	protected void ensureThatNotToManyConfigsAreCachedInSession()
	{
		if (cachedConfigIds.size() >= maxCachedConfigMapSize)
		{
			for (final String configId : oldCachedConfigIds)
			{
				// clear old configs from session cache
				removeConfigFromSessionCache(configId);
			}
			oldCachedConfigIds = cachedConfigIds;
			// avoid rehashing, create with sufficient capacity
			cachedConfigIds = new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1));
		}
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		recorder.recordCreateConfigurationFromExternalSource(config);
		cacheConfig(config);

		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
		recorder.recordCreateConfigurationFromExternalSource(config);
		cacheConfig(config);

		return config;
	}

	@Override
	public void releaseSession(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Releasing config session with id " + configId);
		}

		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			getConfigurationProvider().releaseSession(configId);
			removeConfigFromCache(configId);

			synchronized (PROVIDER_LOCK)
			{
				locks.remove(configId);
				oldLocks.remove(configId);
			}
		}

	}

	protected void removeConfigFromCache(final String configId)
	{
		removeConfigFromSessionCache(configId);
		synchronized (CACHE_LOCK)
		{
			cachedConfigIds.remove(configId);
		}
	}

	protected void removeConfigFromSessionCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		sessionAccessService.removeConfigurationModelEngineState(configId);
	}

	protected void cacheConfig(final ConfigModel config)
	{
		cacheConfig(config.getId(), config);
	}

	protected void cacheConfig(final String configId, final ConfigModel config)
	{
		synchronized (CACHE_LOCK)
		{
			ensureThatNotToManyConfigsAreCachedInSession();
			cachedConfigIds.add(configId);
		}
		sessionAccessService.setConfigurationModelEngineState(configId, config);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' read frist time, caching it for further access");
		}
	}

	protected ConfigurationProviderFactory getConfigurationProviderFactory()
	{
		return configurationProviderFactory;
	}


	/**
	 * Sets session access service (Accessing mappings which we store in the hybris session)
	 *
	 * @param sessionAccessService
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;

	}


	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	@Override
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final AbstractOrderEntryModel entryModel)
	{
		final String cartEntryKey = entryModel.getPk().toString();
		final String productCode = entryModel.getProduct().getCode();
		final String externalConfiguration = entryModel.getExternalConfiguration();

		return calculateCartEntryConfigurationAttributes(cartEntryKey, productCode, externalConfiguration);

	}

	@Override
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final String cartEntryKey,
			final String productCode, final String externalConfiguration)
	{
		final CartEntryConfigurationAttributes attributes = new CartEntryConfigurationAttributes();

		final String configId = getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ConfigID=" + configId + " is mapped to cartentry with PK=" + cartEntryKey);
		}
		ConfigModel configurationModel = null;
		if (configId != null)
		{
			configurationModel = retrieveConfigurationModel(configId);
		}

		if (configurationModel == null)
		{
			final KBKeyImpl kbKey = new KBKeyImpl(productCode);
			if (externalConfiguration == null)
			{
				// this means the item was put into the cart without touching
				// CPQ, e.g. through order forms
				// as this is not the standard process, log this in info level
				LOG.info("No external configuration provided for cart entry key: " + cartEntryKey
						+ ". Creating default configuration");
				configurationModel = createDefaultConfiguration(kbKey);
			}
			else
			{
				LOG.debug("Creating config model form external XML");
				configurationModel = createConfigurationFromExternal(kbKey, externalConfiguration);
			}
			getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, configurationModel.getId());
		}
		final boolean isConfigurationConsistent = configurationModel.isConsistent() && configurationModel.isComplete();

		attributes.setConfigurationConsistent(Boolean.valueOf(isConfigurationConsistent));
		final int numberOfIssues = countNumberOfIncompleteCstics(configurationModel.getRootInstance())
				+ countNumberOfSolvableConflicts(configurationModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Number of issues: " + numberOfIssues);
		}
		attributes.setNumberOfErrors(Integer.valueOf(numberOfIssues));

		return attributes;
	}

	@Override
	public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
	{
		final ConfigModel configurationModel = retrieveConfigurationModel(configId);

		return countNumberOfIncompleteCstics(configurationModel.getRootInstance())
				+ countNumberOfSolvableConflicts(configurationModel);

	}

	protected int countNumberOfIncompleteCstics(final InstanceModel rootInstance)
	{

		int numberOfErrors = 0;
		for (final InstanceModel subInstace : rootInstance.getSubInstances())
		{
			numberOfErrors += countNumberOfIncompleteCstics(subInstace);
		}
		for (final CsticModel cstic : rootInstance.getCstics())
		{
			if (cstic.isRequired() && !cstic.isComplete())
			{
				numberOfErrors++;
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Mandatory Cstic missing: " + cstic.getName());
				}
			}
		}
		return numberOfErrors;

	}

	protected int countNumberOfSolvableConflicts(final ConfigModel configModel)
	{
		final int result = 0;
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		if (solvableConflicts != null)
		{
			return solvableConflicts.size();
		}
		return result;
	}

	@Override
	public boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry)
	{
		final String configId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
		final ConfigModel configModel = retrieveConfigurationModel(configId);
		final PriceModel currentTotalPrice = configModel.getCurrentTotalPrice();
		boolean cartEntryUpdated = false;
		if (currentTotalPrice != null && currentTotalPrice.hasValidPrice())
		{
			entry.setBasePrice(Double.valueOf(currentTotalPrice.getPriceValue().doubleValue()));
			LOG.debug("Base price: " + entry.getBasePrice() + " is set for the cart entry with pk: " + entry.getPk());
			cartEntryUpdated = true;
		}
		return cartEntryUpdated;
	}

	@Override
	public boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry)
	{
		final String xml = retrieveExternalConfiguration(parameters.getConfigId());
		entry.setExternalConfiguration(xml);
		recorder.recordUpdateCartEntry(entry, parameters);
		LOG.debug("Configuration with config ID " + parameters.getConfigId() + " set at cart entry " + entry.getPk().toString()
				+ ": " + xml);
		return true;
	}

	@Override
	public boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry)
	{
		if (LOG.isDebugEnabled())
		{
			final String oldConfigId = getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
			LOG.debug("Removed old configId " + oldConfigId + " for cart entry " + entry.getPk().toString());
		}
		getSessionAccessService().removeConfigIdForCartEntry(entry.getPk().toString());
		final KBKey kbKey = new KBKeyImpl(entry.getProduct().getCode());
		final ConfigModel configurationModel = createConfigurationFromExternal(kbKey, externalConfiguration);
		getSessionAccessService().setConfigIdForCartEntry(entry.getPk().toString(), configurationModel.getId());
		final String newExternalConfiguration = retrieveExternalConfiguration(configurationModel.getId());
		entry.setExternalConfiguration(newExternalConfiguration);
		LOG.debug("Configuration with config ID " + configurationModel.getId() + " set at cart entry " + entry.getPk().toString()
				+ ": " + newExternalConfiguration);
		return true;
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create variant configuration for base product " + baseProductCode + " of product variant "
					+ variantProductCode);
		}
		final ConfigModel configModel = getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode,
				variantProductCode);
		recorder.recordCreateConfigurationForVariant(configModel, baseProductCode, variantProductCode);

		return configModel;
	}

	@Override
	public boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId)
	{
		if (hasProductChangedForCartItem(product, entry))
		{
			getSessionAccessService().setConfigIdForCartEntry(entry.getPk().toString(), configId);
			entry.setProduct(product);
			return true;
		}
		return false;
	}

	protected boolean hasProductChangedForCartItem(final ProductModel product, final AbstractOrderEntryModel cartItem)
	{
		return !cartItem.getProduct().getCode().equals(product.getCode());
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

}
