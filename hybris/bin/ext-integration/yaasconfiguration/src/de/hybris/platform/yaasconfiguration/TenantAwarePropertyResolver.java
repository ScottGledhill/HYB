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

import static com.google.common.base.Preconditions.checkNotNull;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import org.apache.log4j.Logger;

import com.hybris.charon.conf.PropertyResolver;


public class TenantAwarePropertyResolver implements PropertyResolver
{
	private static final Logger LOG = Logger.getLogger(TenantAwarePropertyResolver.class.getName());

	final private String tenantId;
	final private PropertyResolver resolver;

	public TenantAwarePropertyResolver(final String tenantID, final PropertyResolver resolver)
	{
		checkNotNull(tenantID, "tenantID must not be null");
		checkNotNull(resolver, "resoPropertyResolverlver must not be null");

		this.resolver = resolver;
		this.tenantId = tenantID;
	}


	@Override
	public boolean contains(final String key)
	{
		final Tenant tenant = Registry.getTenantByID(tenantId);
		try
		{
			return Registry.runAsTenant(tenant, () -> resolver.contains(key));
		}
		catch (final Exception e)
		{
			LOG.error("Run method in tenant failed", e);
			throw new SystemException(e);
		}

	}

	@Override
	public String lookup(final String key)
	{
		final Tenant tenant = Registry.getTenantByID(tenantId);

		try
		{
			return Registry.runAsTenant(tenant, () -> resolver.lookup(key));
		}
		catch (final Exception e)
		{
			LOG.error("Run method in tenant failed", e);
			throw new SystemException(e);
		}

	}

}
