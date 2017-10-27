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
package com.hybris.yprofile.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hybris.yprofile.dto.*;
import com.hybris.yprofile.rest.clients.ProfileClient;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link ProfileTransactionService}. Communication service to send transactions to Profile
 */
public class DefaultProfileTransactionService implements ProfileTransactionService {
    private static final Logger LOG = Logger.getLogger(DefaultProfileTransactionService.class);
    private static final String NULL = "null";
    private static final String ACCOUNT_REGISTRATION_EVENT_TYPE = "account registration";
    private static final String LOGIN_EVENT_TYPE = "login";

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    private Converter<OrderModel, Order> profileOrderEventConverter;

    private Converter<ConsignmentModel, Order> profileConsignmentEventConverter;

    private Converter<ReturnRequestModel, Order> profileReturnEventConverter;

    private Converter<UserModel, User> profileUserEventConverter;

    /**
     * Send order to yprofile.
     * @param orderModel
     * @return
     */
    @Override
    public void sendSubmitOrderEvent(final OrderModel orderModel) {
        final Order order = getProfileOrderEventConverter().convert(orderModel);

        sendOrder(orderModel.getConsentReference(), order);
    }

    /**
     * Send consignment to yprofile.
     * @param consignmentModel
     * @return
     */
    @Override
    public void sendConsignmentEvent(final ConsignmentModel consignmentModel) {

        final OrderModel orderModel = (OrderModel) consignmentModel.getOrder();
        final Order order = getProfileConsignmentEventConverter().convert(consignmentModel);

        sendOrder(orderModel.getConsentReference(), order);
    }

    /**
     * Send return to yprofile.
     * @param returnRequestModel
     * @return
     */
    @Override
    public void sendReturnOrderEvent(final ReturnRequestModel returnRequestModel) {

        OrderModel orderModel = returnRequestModel.getOrder();

        final Order order = getProfileReturnEventConverter().convert(returnRequestModel);

        sendOrder(orderModel.getConsentReference(), order);
    }

    private void sendOrder(String consentReference, Order order) {
        if (getProfileConfigurationService().isYaaSConfigurationPresent() && isValidConsentReference(consentReference)) {
            getClient().sendOrderEvent(consentReference, order)
                    .subscribe(response -> logSuccess(order),
                            error -> logError(error, order.toString()),
                            () -> logSuccess(order));
        }
    }

    /**
     * Send user registration event to yprofile.
     * @param userModel
     * @param storeName
     * @return
     */
    @Override
    public void sendUserRegistrationEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName) {
        sendUserEvent(userModel, consentReferenceId, sessionId, storeName, ACCOUNT_REGISTRATION_EVENT_TYPE);
    }

    /**
     * Send user login event to yprofile.
     * @param userModel
     * @param storeName
     * @return
     */
    @Override
    public void sendLoginEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName) {
        sendUserEvent(userModel, consentReferenceId, sessionId, storeName, LOGIN_EVENT_TYPE);
    }

    private void sendUserEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName, final String eventType) {
        User user = getProfileUserEventConverter().convert(userModel);
        user.setType(eventType);
        user.setSessionId(sessionId);
        user.setChannelRef(storeName);

        if (getProfileConfigurationService().isYaaSConfigurationPresent() && isValidConsentReference(consentReferenceId)) {
            getClient().sendUserEvent(eventType, consentReferenceId, user)
                    .subscribe(response -> logSuccess(user),
                            error -> logError(error, user.toString()),
                            () -> logSuccess(user));
        }
    }

    private boolean isValidConsentReference(String consentReferenceId) {
        return StringUtils.isNotBlank(consentReferenceId) && !NULL.equals(consentReferenceId);
    }

    private void logSuccess(final Object obj) {
        if (LOG.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String event = obj.toString();
            try {
                event = mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                /*ignore*/
            }
            LOG.debug(event + " sent to yprofile ");
        }
    }

    private void logError(final Throwable error, final String obj) {
        LOG.error(obj.toString() + " sending to yprofile failed", error);
    }

    private ProfileClient getClient(){
        return getRetrieveRestClientStrategy().getProfileRestClient();
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public Converter<OrderModel, Order> getProfileOrderEventConverter() {
        return profileOrderEventConverter;
    }

    @Required
    public void setProfileOrderEventConverter(Converter<OrderModel, Order> profileOrderEventConverter) {
        this.profileOrderEventConverter = profileOrderEventConverter;
    }

    public Converter<ConsignmentModel, Order> getProfileConsignmentEventConverter() {
        return profileConsignmentEventConverter;
    }

    @Required
    public void setProfileConsignmentEventConverter(Converter<ConsignmentModel, Order> profileConsignmentEventConverter) {
        this.profileConsignmentEventConverter = profileConsignmentEventConverter;
    }

    public Converter<ReturnRequestModel, Order> getProfileReturnEventConverter() {
        return profileReturnEventConverter;
    }

    @Required
    public void setProfileReturnEventConverter(Converter<ReturnRequestModel, Order> profileReturnEventConverter) {
        this.profileReturnEventConverter = profileReturnEventConverter;
    }

    public Converter<UserModel, User> getProfileUserEventConverter() {
        return profileUserEventConverter;
    }

    @Required
    public void setProfileUserEventConverter(Converter<UserModel, User> profileUserEventConverter) {
        this.profileUserEventConverter = profileUserEventConverter;
    }
}
