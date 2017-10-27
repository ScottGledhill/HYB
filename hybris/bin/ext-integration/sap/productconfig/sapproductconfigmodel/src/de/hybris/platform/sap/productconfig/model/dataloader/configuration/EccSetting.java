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
package de.hybris.platform.sap.productconfig.model.dataloader.configuration;

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IEccSetting;


public class EccSetting implements IEccSetting
{
	private final boolean loadBalanced;
	private final String sid;
	private final String messageServer;
	private final String group;

	public EccSetting(final boolean loadBalanced, final String instance, final String targetHost)
	{
		this(loadBalanced, instance, targetHost, null);
	}

	public EccSetting(final boolean loadBalanced, final String sid, final String messageServer, final String group)
	{
		super();
		this.loadBalanced = loadBalanced;
		this.sid = sid;
		this.messageServer = messageServer;
		this.group = group;
	}

	@Override
	public boolean isLoadBalanced()
	{
		return loadBalanced;
	}

	@Override
	public String getSid()
	{
		return sid;
	}

	@Override
	public String getMessageServer()
	{
		return messageServer;
	}

	@Override
	public String getGroup()
	{
		return group;
	}
}
