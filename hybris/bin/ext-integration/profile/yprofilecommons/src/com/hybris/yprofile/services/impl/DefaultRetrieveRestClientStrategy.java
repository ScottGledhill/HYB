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

import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.yaasconfiguration.CharonFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultRetrieveRestClientStrategy implements RetrieveRestClientStrategy {

    private ProfileConfigurationService profileConfigurationService;
    private CharonFactory charonFactory;

    public ProfileClient getProfileRestClient() {
        return getCharonFactory().client(ProfileClient.class);
    }

    public CharonFactory getCharonFactory() {
        return charonFactory;
    }

    @Required
    public void setCharonFactory(CharonFactory charonFactory) {
        this.charonFactory = charonFactory;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }
}
