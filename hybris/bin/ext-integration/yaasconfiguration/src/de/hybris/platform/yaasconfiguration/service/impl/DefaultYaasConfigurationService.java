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

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_SCOPE;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTID;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTSECRET;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_TENANT;
import static de.hybris.platform.yaasconfiguration.model.YaasApplicationModel._TYPECODE;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link YaasConfigurationService}
 */
public class DefaultYaasConfigurationService implements YaasConfigurationService
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public YaasApplicationModel getYaasApplicationForId(final String applicationId)
	{
		checkArgument(applicationId != null, "appId must not be null");

		final YaasApplicationModel model = new YaasApplicationModel();

		model.setIdentifier(applicationId);
		return getFlexibleSearchService().getModelByExample(model);
	}


	@Override
	public Optional<YaasApplicationModel> takeFirstModel()
	{
		try
		{
			final FlexibleSearchQuery query = new FlexibleSearchQuery(format("select {pk} from {%s}", _TYPECODE));
			query.setCount(1);
			return ofNullable(getFlexibleSearchService().searchUnique(query));
		}
		catch (final ModelNotFoundException exception)
		{
			return empty();
		}
	}

	@Override
	public List<YaasApplicationModel> getYaaSApplications()
	{
		List<YaasApplicationModel> yaasApplcations = new ArrayList<>();
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {pk} from {" + YaasApplicationModel._TYPECODE + "}");
		yaasApplcations = getFlexibleSearchService().<YaasApplicationModel> search(query).getResult();
		return yaasApplcations;
	}

	@Override
	public YaasClientModel getYaasClientForId(final String clientId)
	{
		checkArgument(clientId != null, "clientId must not be null");

		final YaasClientModel model = new YaasClientModel();

		model.setIdentifier(clientId);
		return getFlexibleSearchService().getModelByExample(model);
	}

	@Override
	public <T> Map<String, String> buildYaasConfig(final String yaasAppId, final Class<T> clientType)
	{
		checkArgument(yaasAppId != null, "appId must not be null");
		checkArgument(clientType != null, "clientType must not be null");

		final Map<String, String> config = new HashMap();

		buildApplicationConfig(yaasAppId, config);

		buildClientConfig(clientType.getSimpleName(), config);

		return config;
	}

	/**
	 * Helper method to build the yass configuration from persisted YaasApplication for the given yaas applicationId
	 *
	 * @param yaasAppId
	 * @param config
	 */
	protected void buildApplicationConfig(final String yaasAppId, final Map<String, String> config)
	{
		final YaasApplicationModel yaasApplication = getYaasApplicationForId(yaasAppId);

		config.put(YAAS_OAUTH_URL, yaasApplication.getOauthURL());
		config.put(YAAS_OAUTH_CLIENTID, yaasApplication.getClientId());
		config.put(YAAS_OAUTH_CLIENTSECRET, yaasApplication.getClientSecret());
		config.put(YAAS_TENANT, yaasApplication.getYaasProject().getIdentifier());
	}

	/**
	 * Helper methd to build the yaas configuration from persisted YaasClient for the given yass client name.
	 *
	 * @param clientId
	 * @param config
	 */
	protected void buildClientConfig(final String clientId, final Map<String, String> config)
	{
		final YaasClientModel yaasClient = getYaasClientForId(clientId);

		config.put(YAAS_CLIENT_URL, yaasClient.getClientURL());
		config.put(YAAS_CLIENT_SCOPE, yaasClient.getClientScope());

		if (MapUtils.isNotEmpty(yaasClient.getAdditionalConfigurations()))
		{
			config.putAll(yaasClient.getAdditionalConfigurations());
		}
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
