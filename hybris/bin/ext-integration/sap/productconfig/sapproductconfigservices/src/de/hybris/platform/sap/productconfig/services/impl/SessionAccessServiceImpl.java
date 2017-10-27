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
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link SessionAccessService}
 */
public class SessionAccessServiceImpl implements SessionAccessService
{

	private static final String TRACE_MESSAGE_FOR_CART_ENTRY = "for cart entry: ";
	private static final String TRACE_MESSAGE_FOR_PRODUCT = "for product: ";
	private static final Logger LOG = Logger.getLogger(SessionAccessServiceImpl.class);
	private SessionService sessionService;


	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	@Override
	public String getSessionId()
	{
		return this.sessionService.getCurrentSession().getSessionId();
	}

	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}
		getCartEntryConfigCache().put(cartEntryKey, configId);
	}

	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		String configId = null;

		final Map<String, String> sessionConfigCartEntryCache = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		if (sessionConfigCartEntryCache != null)
		{
			configId = sessionConfigCartEntryCache.get(cartEntryKey);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;

	}


	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return getUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}


	/**
	 * Retrieves UiStatus from session
	 *
	 * @param key
	 *           Key of object in map
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 * @return UiStatus
	 */
	protected <T> T getUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		Object uiStatus = null;

		Map<String, Object> sessionUiStatusCache;
		if (forCart)
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}
		if (sessionUiStatusCache != null)
		{
			uiStatus = sessionUiStatusCache.get(key);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get UiStatus " + uiStatus + " from session " + traceMessage + key);
		}

		return (T) uiStatus;
	}


	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		setUiStatusIntoSession(cartEntryKey, uiStatus, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	@Override
	public Object getUiStatusForProduct(final String productKey)
	{
		return getUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}


	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		setUiStatusIntoSession(productKey, uiStatus, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	/**
	 * Puts UiStatus object into session
	 *
	 * @param key
	 *           Key for object
	 * @param uiStatus
	 *           The object we want to store in session
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void setUiStatusIntoSession(final String key, final Object uiStatus, final boolean forCart, final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put UiStatus " + uiStatus + " into session " + traceMessage + key);
		}

		Map<String, Object> sessionUiStatusEntryCache;
		if (forCart)
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		sessionUiStatusEntryCache.put(key, uiStatus);
	}


	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		removeUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	/**
	 * Removes UiStatus object from session
	 *
	 * @param key
	 *           Key for object
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void removeUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove UiStatus from session " + traceMessage + key);
		}

		Map<String, Object> uiStatusMap;
		if (forCart)
		{
			uiStatusMap = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			uiStatusMap = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		if (!MapUtils.isEmpty(uiStatusMap))
		{
			uiStatusMap.remove(key);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Map does not exist in session");
			}
		}
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		removeUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		String cartEntryKey = null;

		final Map<String, String> sessionCartEntryConfigurations = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		if (sessionCartEntryConfigurations != null)
		{
			for (final Map.Entry<String, String> entry : sessionCartEntryConfigurations.entrySet())
			{
				if (entry.getValue().equals(configId))
				{
					cartEntryKey = entry.getKey();
					break;
				}
			}
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get cart entry key " + cartEntryKey + " from session for config ID" + configId);
		}

		return cartEntryKey;
	}


	@Override
	public void setCartEntryForProduct(final String productKey, final String cartEntryId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put cartEntryId " + cartEntryId + " into session for product: " + productKey);
		}
		getProductCartEntryCache().put(productKey, cartEntryId);
	}


	@Override
	public String getCartEntryForProduct(final String productKey)
	{
		String cartEntryKey = null;

		final Map<String, String> sessionProductCartEntryCache = retrieveSessionAttributeContainer().getProductCartEntries();

		if (sessionProductCartEntryCache != null)
		{
			cartEntryKey = sessionProductCartEntryCache.get(productKey);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get cart entry key " + cartEntryKey + " from session for product: " + productKey);
		}

		return cartEntryKey;
	}


	@Override
	public void removeCartEntryForProduct(final String productKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove cartEntryId for product: " + productKey);
		}
		getProductCartEntryCache().remove(productKey);
	}

	/**
	 * @return Cache for cart entries per product
	 */
	protected Map<String, String> getProductCartEntryCache()
	{
		return retrieveSessionAttributeContainer().getProductCartEntries();
	}


	@Override
	public void removeSessionArtifactsForCartEntry(final String cartEntryId, final String productKey)
	{

		//remove configuration ID if needed
		removeConfigIdForCartEntry(cartEntryId);

		//remove UI status attached to cart entry
		removeUiStatusForCartEntry(cartEntryId);

		//check if this configuration is maintained at product level also
		final String currentCartEntryForProduct = getCartEntryForProduct(productKey);
		if (currentCartEntryForProduct != null && (currentCartEntryForProduct.equals(cartEntryId)))
		{
			//We need to clean up more storages
			removeCartEntryForProduct(productKey);
			removeUiStatusForProduct(productKey);
		}

	}


	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryConfigCache().remove(cartEntryKey);

	}

	/**
	 * @return Map: Configuration ID's for cart entry
	 */
	protected Map<String, String> getCartEntryConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryConfigurations();
	}

	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap()
	{
		return retrieveSessionAttributeContainer().getClassificationSystemCPQAttributes();
	}

	@Override
	public Set<String> getSolrIndexedProperties()
	{
		return retrieveSessionAttributeContainer().getIndexedProperties();
	}

	@Override
	public void setSolrIndexedProperties(final Set<String> solrTypes)
	{
		retrieveSessionAttributeContainer().setIndexedProperties(solrTypes);
	}


	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		return retrieveSessionAttributeContainer().getConfigurationProvider();
	}

	@Override
	public void setConfigurationProvider(final ConfigurationProvider provider)
	{
		retrieveSessionAttributeContainer().setConfigurationProvider(provider);
	}

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return retrieveSessionAttributeContainer().getConfigurationModelEngineStates().get(configId);
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		retrieveSessionAttributeContainer().getConfigurationModelEngineStates().put(configId, configModel);
	}

	@Override
	public void removeConfigurationModelEngineState(final String configId)
	{
		retrieveSessionAttributeContainer().getConfigurationModelEngineStates().remove(configId);
	}

	@Override
	public void removeConfigurationModelEngineStates()
	{
		final ProductConfigSessionAttributeContainer container = retrieveSessionAttributeContainer(false);
		if (container != null)
		{
			LOG.debug("Cleaning product config engine state read cache");
			container.getConfigurationModelEngineStates().clear();
		}
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer()
	{
		return retrieveSessionAttributeContainer(true);
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createLazy)
	{
		ProductConfigSessionAttributeContainer attributeContainer = sessionService
				.getAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
		if (attributeContainer == null && createLazy)
		{
			attributeContainer = new ProductConfigSessionAttributeContainer();
			sessionService.setAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, attributeContainer);
		}
		return attributeContainer;
	}
}
