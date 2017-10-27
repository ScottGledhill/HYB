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
package com.hybris.yprofile.populators;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class OrderEventPopulator implements Populator<OrderModel, Order> {

    private static final String NEW_ORDER_EVENT_TYPE = "order";
    public static final String NEW_ORDER_STATUS = "new";
    public static final String NOT_DELIVERED_STATUS = "not delivered";

    private Converter<AddressModel, Address> profileAddressConverter;
    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter;

    @Override
    public void populate(OrderModel orderModel, Order order) throws ConversionException {

        order.setChannelRef(orderModel.getStore().getUid());
        order.setType(NEW_ORDER_EVENT_TYPE);
        order.setConsumer(getProfileConsumerConverter().convert(orderModel.getUser()));
        order.setBody(getOrderBody(orderModel));
    }

    private OrderBody getOrderBody(OrderModel orderModel){
        OrderBody orderBody = new OrderBody();
        orderBody.setOrderId(orderModel.getCode());
        orderBody.setCartId(orderModel.getCartIdReference());
        orderBody.setDate(Utils.formatDate(orderModel.getCreationtime()));
        orderBody.setOrderValue(Utils.formatDouble(orderModel.getTotalPrice()));
        orderBody.setCurrency(orderModel.getCurrency().getIsocode());

        orderBody.setStatus(orderModel.getStatusDisplay() != null ? orderModel.getStatusDisplay().toString(): NEW_ORDER_STATUS);

        List<Promotion> promotions = new ArrayList<>();
        orderModel.getAllPromotionResults().forEach(
                promotionResultModel ->
                        promotions.add(getPromotion(promotionResultModel.getPromotion()))
        );
        orderBody.setPromotionInfo(promotions);

        orderBody.setPaymentInfo(getPaymentInfo(orderModel));

        orderBody.setShipmentInfo(getShipmentInfo(orderModel));

        List<OrderLineItem> lineItems = new ArrayList<>();
        orderModel.getEntries().stream().forEach(
                (AbstractOrderEntryModel abstractOrderEntryModel)
                        -> lineItems.add(getProfileOrderLineItemConverter().convert(abstractOrderEntryModel))
        );

        orderBody.setLineItems(lineItems);

        return orderBody;
    }

    private Promotion getPromotion(AbstractPromotionModel promotionResultModel){
        Promotion promotion = new Promotion();
        promotion.setRef(promotionResultModel.getCode());
        promotion.setType(promotionResultModel.getPromotionType());

        return promotion;
    }

    private PaymentInfo getPaymentInfo(OrderModel orderModel){
        PaymentInfo paymentInfo = new PaymentInfo();

        PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();

        paymentInfo.setPaymentType(paymentInfoModel != null ? paymentInfoModel.getItemtype() : "");

        if (paymentInfoModel instanceof CreditCardPaymentInfoModel){
            paymentInfo.setPaymentType(((CreditCardPaymentInfoModel) paymentInfoModel).getType().toString());
        }

        paymentInfo.setStatus(orderModel.getPaymentStatus() != null ? orderModel.getPaymentStatus().toString() : "");
        paymentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getPaymentAddress()));

        return paymentInfo;
    }


    private ShipmentInfo getShipmentInfo(OrderModel orderModel){

        ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getDeliveryAddress()));
        shipmentInfo.setStatus(orderModel.getDeliveryStatus() != null ? orderModel.getDeliveryStatus().getCode() : NOT_DELIVERED_STATUS);

        return shipmentInfo;
    }


    public Converter<UserModel, Consumer> getProfileConsumerConverter() {
        return profileConsumerConverter;
    }

    @Required
    public void setProfileConsumerConverter(Converter<UserModel, Consumer> profileConsumerConverter) {
        this.profileConsumerConverter = profileConsumerConverter;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    @Required
    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }

    public Converter<AbstractOrderEntryModel, OrderLineItem> getProfileOrderLineItemConverter() {
        return profileOrderLineItemConverter;
    }

    @Required
    public void setProfileOrderLineItemConverter(Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter) {
        this.profileOrderLineItemConverter = profileOrderLineItemConverter;
    }
}
