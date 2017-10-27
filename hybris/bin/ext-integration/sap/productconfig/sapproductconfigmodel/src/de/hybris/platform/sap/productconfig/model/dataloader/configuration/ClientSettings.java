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

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IClientSetting;


public class ClientSettings implements IClientSetting
{
	private final String client;
	private final String password;
	private final String user;

	public ClientSettings(final String client, final String user, final String password)
	{
		this.client = client;
		this.password = password;
		this.user = user;
	}

	@Override
	public String getClient()
	{
		return client;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String getUser()
	{
		return user;
	}

}