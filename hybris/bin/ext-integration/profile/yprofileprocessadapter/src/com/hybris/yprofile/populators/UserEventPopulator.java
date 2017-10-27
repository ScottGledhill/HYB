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
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

public class UserEventPopulator implements Populator<UserModel, User> {

    public static final String TYPE = "YaaS account";
    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<AddressModel, Address> profileAddressConverter;

    @Override
    public void populate(UserModel userModel, User user) throws ConversionException {

        user.setDate(Utils.formatDate(userModel.getCreationtime()));
        user.setBody(getUserBody(userModel));

    }

    private UserBody getUserBody(UserModel userModel){
        UserBody userBody = new UserBody();

        userBody.setType(TYPE);
        userBody.setDate(Utils.formatDate(userModel.getCreationtime()));
        userBody.setIdentity(getProfileConsumerConverter().convert(userModel));
        userBody.setMasterData(getUserMasterData(userModel));

        return userBody;
    }


    private UserMasterData getUserMasterData(UserModel userModel){
        UserMasterData userMasterData = new UserMasterData();
        userMasterData.setAddress(getProfileAddressConverter().convert(userModel.getDefaultPaymentAddress()));

        return userMasterData;
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
}
