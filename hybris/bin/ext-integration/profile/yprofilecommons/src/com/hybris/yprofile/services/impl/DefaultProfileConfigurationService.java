/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.hybris.yprofile.services.impl;

import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.yaasconfiguration.model.YaasApplicationModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasSessionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class DefaultProfileConfigurationService implements ProfileConfigurationService {

    private final String appId;
    private YaasConfigurationService yaasConfigurationService;
    private YaasSessionService yaasSessionService;


    public DefaultProfileConfigurationService(String appId) {
        this.appId = appId;
    }

    @Override
    public boolean isYaaSConfigurationPresent(){
        return getYaasApplicationModel().isPresent();
    }

    @Override
    public String getYaaSTenant(){

        if (isYaaSConfigurationPresent() && getYaasConfig().getYaasProject() != null) {
            return getYaasConfig().getYaasProject().getIdentifier();
        }

        return StringUtils.EMPTY;
    }

    @Override
    public String getBaseSiteId(){

        if (isYaaSConfigurationPresent() && getYaasConfig().getYaasProject() != null) {
            return getYaasConfig().getYaasProject().getBaseSite().getUid();
        }

        return StringUtils.EMPTY;
    }

    protected YaasApplicationModel getYaasConfig() {

        Optional<YaasApplicationModel> model = getYaasApplicationModel();

        if (model.isPresent()) {
            return model.get();
        }

        return null;
    }

    protected Optional<YaasApplicationModel> getYaasApplicationModel()
    {
        if (isNotBlank(getYaasSessionService().getCurrentYaasAppId()))
        {
            return ofNullable(getYaasConfigurationService().getYaasApplicationForId(getYaasSessionService().getCurrentYaasAppId()));
        }

        if (isNotBlank(appId))
        {
            getYaasSessionService().setCurrentYaasAppId(appId);
            return ofNullable(getYaasConfigurationService().getYaasApplicationForId(appId));
        }

        Optional<YaasApplicationModel> yaasApplicationModel = getYaasConfigurationService().takeFirstModel();

        if (yaasApplicationModel.isPresent())
        {
            getYaasSessionService().setCurrentYaasAppId(yaasApplicationModel.get().getIdentifier());
        }

        return yaasApplicationModel;
    }

    public YaasConfigurationService getYaasConfigurationService() {
        return yaasConfigurationService;
    }

    @Required
    public void setYaasConfigurationService(YaasConfigurationService yaasConfigurationService) {
        this.yaasConfigurationService = yaasConfigurationService;
    }

    public YaasSessionService getYaasSessionService() {
        return yaasSessionService;
    }

    @Required
    public void setYaasSessionService(YaasSessionService yaasSessionService) {
        this.yaasSessionService = yaasSessionService;
    }
}
