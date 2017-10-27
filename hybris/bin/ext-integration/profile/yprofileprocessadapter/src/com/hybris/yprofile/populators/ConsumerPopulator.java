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

import com.hybris.yprofile.dto.Consumer;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ConsumerPopulator implements Populator<UserModel, Consumer> {

    public static final String TYPE = "email";

    @Override
    public void populate(UserModel userModel, Consumer consumer) throws ConversionException {
        consumer.setType(TYPE);
        consumer.setRef(userModel.getUid());

        if (userModel instanceof CustomerModel) {
            if (CustomerType.GUEST.equals(((CustomerModel) userModel).getType())) {
                consumer.setRef(((CustomerModel) userModel).getContactEmail());
            }
        }
    }
}
