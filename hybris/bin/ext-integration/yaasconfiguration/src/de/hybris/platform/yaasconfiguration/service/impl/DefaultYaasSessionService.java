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
package de.hybris.platform.yaasconfiguration.service.impl;

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.SESSION_YAAS_APPLICATIONID;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;


public class DefaultYaasSessionService extends AbstractBusinessService implements YaasSessionService
{

	@Override
	public void setCurrentYaasAppId(final String yaasAppId)
	{
		ServicesUtil.validateParameterNotNull(yaasAppId, "Parameter 'yaasAppId' must not be null!");

		getSessionService().setAttribute(SESSION_YAAS_APPLICATIONID, yaasAppId);

	}


	@Override
	public String getCurrentYaasAppId()
	{
		return getSessionService().getAttribute(SESSION_YAAS_APPLICATIONID);
	}

}
