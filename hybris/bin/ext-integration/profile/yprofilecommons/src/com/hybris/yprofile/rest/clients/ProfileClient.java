/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.yprofile.rest.clients;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Header;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.TrackingEvent;
import com.hybris.yprofile.dto.User;
import org.springframework.http.HttpHeaders;
import rx.Observable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@OAuth
@Http
public interface ProfileClient {

    /**
     * Fetch consent reference for user
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/profile-consent/v1/${tenant}/consentReferences")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<ConsentResponse> getConsentReference(
            @HeaderParam("hybris-user") String userId);


    /**
     * Send events to yProfile
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/profile-edge/v1/events")
    @Header(name = "event-type", val = "piwik")
    @Header(name = "hybris-tenant", val = "${tenant}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<TrackingResponse> sendTrackingEvent(
            @HeaderParam("consent-reference") String consentReferenceId,
            @HeaderParam(HttpHeaders.USER_AGENT) String userAgent,
            @HeaderParam(HttpHeaders.ACCEPT) String accept,
            @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) String acceptLanguage,
            @HeaderParam(HttpHeaders.REFERER) String referer,
            TrackingEvent trackingEvent);

    /**
     * Send orders to yProfile.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/profile-ecp-ctx-adapter/v1/${tenant}/events")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<ProfileResponse> sendOrderEvent(
            @HeaderParam("consent-reference") String consentReferenceId,
            Order order);

    /**
     * Send users to yProfile.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/profile-edge/v1/events")
    @Header(name = "hybris-tenant", val = "${tenant}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<ProfileResponse> sendUserEvent(
            @HeaderParam("event-type") String eventType,
            @HeaderParam("consent-reference") String consentReferenceId,
            User user);
}
