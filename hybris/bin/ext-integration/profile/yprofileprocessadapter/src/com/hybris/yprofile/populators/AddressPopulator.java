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

import com.hybris.yprofile.dto.Address;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class AddressPopulator implements Populator<AddressModel, Address> {

    @Override
    public void populate(AddressModel addressModel, Address address) throws ConversionException {

        if (addressModel == null) {
            return;
        }

        address.setFirstName(addressModel.getFirstname() != null ? addressModel.getFirstname() : "");
        address.setLastName(addressModel.getLastname() != null? addressModel.getLastname() : "");

        address.setStreet(addressModel.getStreetname());
        address.setNumber("0");

        if (addressModel.getStreetname() != null) {
            int lastIndexOf = addressModel.getStreetname().lastIndexOf(" ");
            if (lastIndexOf > 0) {
                address.setStreet(addressModel.getStreetname().substring(0, lastIndexOf));
                address.setNumber(addressModel.getStreetname().substring(lastIndexOf));
            }
        }

        address.setAddition(addressModel.getLine2() != null ? addressModel.getLine2() : "");

        address.setZip(addressModel.getPostalcode() != null ? addressModel.getPostalcode() : "");
        address.setCity(addressModel.getTown() != null ? addressModel.getTown() : "");
        address.setCountry((addressModel.getCountry() != null && addressModel.getCountry().getIsocode() != null) ? addressModel.getCountry().getIsocode() : "");
    }
}
