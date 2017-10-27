/*
* [y] hybris Platform
*
* Copyright (c) 2017 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/
package de.hybris.platform.yaasconfiguration;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.STRING_CONSTANT_DOT;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CACHE_DELIMITER;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.Charon;
import com.hybris.charon.CharonBuilder;
import com.hybris.charon.conf.CombinePropertyResolver;
import com.hybris.charon.conf.PropertyResolver;


public class CharonFactory
{
	private static final Logger LOG = Logger.getLogger(CharonFactory.class.getName());

	private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
	private PropertyResolver resolver;
	private YaasConfigurationService yaasConfigurationService;

	private YaasSessionService yaasSessionService;


	/**
	 * returns a cached instance of http client. The given clientType must be an interface which represents the Charon
	 * client.
	 *
	 * @param clientType
	 * @return
	 */
	public <T> T client(final Class<T> clientType)
	{
		checkArgument(clientType != null, "clientType must not be null");

		final String yaasAppId = yaasSessionService.getCurrentYaasAppId();

		if (StringUtils.isNoneEmpty(yaasAppId))
		{
			// if yaas application id exist in the session, then build the yaas configuration
			// to create the yaas client proxy via charon
			final Map<String, String> yaasConfig = yaasConfigurationService.buildYaasConfig(yaasAppId, clientType);

			return client(yaasAppId, clientType, yaasConfig, builder -> builder.build());
		}

		LOG.error("No current YaaS applicationId in the hybris session");
		throw new SystemException("Failed to get current YaaS applicationId");

	}

	public <T> T client(final String appId, final Class<T> clientType, final Map<String, String> yaasConfig,
			final Function<CharonBuilder<T>, T> builder)
	{
		checkArgument(appId != null, "appId must not be null");
		checkArgument(clientType != null, "clientType must not be null");
		checkArgument(yaasConfig != null && !yaasConfig.isEmpty(), "yaasConfig must not be empty");
		checkArgument(builder != null, "builder must not be null");

		// cache key consists of Yaas appId + YAAS_CACHE_DELIMITER + clientType
		// it might have different client for the same yaas appid : example : yaasApp1#productClient , yaasApp1#category
		return (T) cache.computeIfAbsent(buildCacheKey(appId, clientType.getName()),
				k -> builder.apply(Charon.from(clientType).config(yaasConfig)));

	}


	/**
	 * returns a cached instance of http client. the clientType must be an interface which represents the charon client.
	 * the related configuration will lookup to YassConfigurationModel the only properties that are loaded from the model
	 * are clientId and clientSecret
	 *
	 * @param appId
	 * @param clientType
	 * @param <T>
	 * @return yaas client proxy
	 *
	 * @deprecated use {@link #client()} instead.
	 */
	@Deprecated
	public <T> T client(final String appId, final Class<T> clientType)
	{
		return client(appId, clientType, builder -> builder.build());
	}

	/**
	 * returns a cached instance of http client. the clientType must be an interface which represents the charon client.
	 * the related configuration will lookup to YassConfigurationModel the only properties that are loaded from the model
	 * are clientId and clientSecret
	 *
	 * @param appId
	 * @param clientType
	 * @param builder
	 *           client builder modifier
	 * @param <T>
	 * @return yaas client proxy
	 *
	 * @deprecated use {@link #client()} instead.
	 */
	@Deprecated
	public <T> T client(final String appId, final Class<T> clientType, final Function<CharonBuilder<T>, T> builder)
	{
		checkArgument(appId != null, "appId must not be null");
		checkArgument(clientType != null, "clientType must not be null");
		checkArgument(builder != null, "builder must not be null");

		final Tenant tenant = Registry.getCurrentTenantNoFallback();

		if (tenant != null)
		{
			return (T) cache.computeIfAbsent(buildCacheKey(appId, clientType.getName()), //
					k -> builder.apply(Charon.from(clientType).config(new TenantAwarePropertyResolver(tenant.getTenantID(),
							new CombinePropertyResolver(new ApplicationPropertyResolver(yaasConfigurationService, appId), resolver)))));
		}

		LOG.error("No current tenant active when Registry.getCurrentTenantNoFallback().getTenantID() is called");
		throw new SystemException("Failed to get current active tenantId");


	}

	/**
	 * Invalidate the cache for the given key
	 *
	 * @param key
	 */
	public void inValidateCache(final String key)
	{
		checkArgument(key != null, "key must not be null");

		// Remove the cache if it exactly matches the key
		if (cache.containsKey(key))
		{
			cache.remove(key);
		}
		// If the caller notify to invalidate all the client corresponding to the
		// given yaas appid , then it should invalidate all the client associated with
		// the given yass appid.
		// Cache might have different client for the same yaas appid : example : yaasApp1#productClient , yaasApp1#category
		else
		{
			for (final String keyValue : cache.keySet())
			{
				if (StringUtils.indexOf(keyValue, key + YAAS_CACHE_DELIMITER) >= 0)
				{
					cache.remove(keyValue);
				}
			}
		}
	}

	/**
	 * Helper method to build cache key which holds yaas client proxy.
	 *
	 * @param appId
	 * @param clientFileName
	 *
	 * @return yaas appId + # + yaas client name
	 */
	protected String buildCacheKey(final String appId, final String clientFileName)
	{
		return appId + YAAS_CACHE_DELIMITER + StringUtils.substringAfterLast(clientFileName, STRING_CONSTANT_DOT);
	}

	/**
	 * remove all clients from the cache
	 */
	public void clearCache()
	{
		cache.clear();
	}

	public void setResolver(final PropertyResolver resolver)
	{
		this.resolver = resolver;
	}

	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

	@Required
	public void setYaasSessionService(final YaasSessionService yaasSessionService)
	{
		this.yaasSessionService = yaasSessionService;
	}
}
