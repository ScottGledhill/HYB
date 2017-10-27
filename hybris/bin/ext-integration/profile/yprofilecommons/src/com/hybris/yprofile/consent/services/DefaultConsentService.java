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
package com.hybris.yprofile.consent.services;


import com.hybris.yprofile.consent.cookie.ConsentReferenceCookieGenerator;
import com.hybris.yprofile.rest.clients.ConsentResponse;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import rx.Observable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

/**
 * Implementation for {@link ConsentService}. Service is responsible to generate and provide the consent reference.
 */
public class DefaultConsentService implements ConsentService {
    private static final Logger LOG = Logger.getLogger(DefaultConsentService.class);

    private static final String CONSENT_REFERENCE_SESSION_ATTR_KEY = "consent-reference";

    private ConsentReferenceCookieGenerator cookieGenerator;

    private SessionService sessionService;

    private UserService userService;

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    @Override
    public void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response)
    {
        generateConsentReference(request, response, true);
    }

    @Override
    public void generateConsentReference(final HttpServletRequest request, final HttpServletResponse response, final boolean shouldGenerateConsentReference) {
        String consentReferenceId = getConsentReferenceFromCookie(request);

        if (consentReferenceId == null && shouldGenerateConsentReference) {

            try {
                ConsentResponse consentResponse = generateConsentReferenceForUser(getUserId()).toBlocking().first();

                if (consentResponse != null) {
                    consentReferenceId = consentResponse.getId();
                    addConsentReferenceCookie(response, consentReferenceId);
                }
            } catch (Throwable e){
                if (e instanceof TimeoutException) {
                    LOG.error(e.getMessage());
                }
                LOG.error("Error requesting consent ref", e);
            }

        }

        storeConsentReferenceInSession(consentReferenceId);
    }

    private String getUserId(){

        String userId = null;

        UserModel user = getUserService().getCurrentUser();

        if (!getUserService().isAnonymousUser(user)){
            return user.getUid();
        }
        return StringUtils.trimToEmpty(userId);
    }

    @Override
    public String getConsentReferenceFromCookie(final HttpServletRequest request) {
        String consentReferenceId = null;

        if (request.getCookies() != null)
        {
            final String consentReferenceCookieName = getCookieGenerator().getCookieName();

            for (final Cookie cookie : request.getCookies())
            {
                if (consentReferenceCookieName.equals(cookie.getName()))
                {
                    consentReferenceId = cookie.getValue();
                    break;
                }
            }
        }
        return consentReferenceId;
    }

    protected void addConsentReferenceCookie(final HttpServletResponse response, final  String consentReferenceId) {
        if (consentReferenceId == null){
            return;
        }

        try {
            getCookieGenerator().addCookie(response, consentReferenceId);
        } catch (Exception e) {
            LOG.error("Error setting consent reference id cookie", e);
        }
    }

    protected void storeConsentReferenceInSession(final String consentReferenceId) {
        if (consentReferenceId == null){
            return;
        }

        try {

            if (getSessionService().getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY) == null) {
                getSessionService().setAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY, consentReferenceId);
            }
        } catch (Exception e) {
            LOG.error("Error setting consent reference id in session", e);
        }
    }

    @Override
    public String getConsentReferenceFromSession(){
        return getSessionService().getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY);
    }

    @Override
    public Observable<ConsentResponse> generateConsentReferenceForUser(final String userId) {

        if(getProfileConfigurationService().isYaaSConfigurationPresent()) {

            return getClient().getConsentReference(userId).map(
                    consentResponse -> {
                        logSuccess(consentResponse);
                        return consentResponse;
                    })
                    .doOnError(error -> logError(error));

        } else {
            LOG.warn("YaaS Configuration not found");
            return Observable.just(null);
        }
    }

    private void logSuccess(ConsentResponse consentResponse){
        if (LOG.isDebugEnabled()) {
            LOG.debug("Consent reference retrieved successfully. consent-reference: " + consentResponse);
        }
    }

    private void logError(Throwable error){
        LOG.error("Error requesting consent reference", error);
    }

    private ProfileClient getClient() {
        return getRetrieveRestClientStrategy().getProfileRestClient();
    }

    protected ConsentReferenceCookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(ConsentReferenceCookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }
}

