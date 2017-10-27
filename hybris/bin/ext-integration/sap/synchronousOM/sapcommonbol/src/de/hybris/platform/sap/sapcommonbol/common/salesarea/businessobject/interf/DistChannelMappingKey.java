/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf;


/**
 * Interface builds the key for the sales organisation and distribution channel mapping .<br>
 *
 * @version 1.0
 */
public interface DistChannelMappingKey {

    /**
     * @return sales organisation
     */
    public String getSalesOrg();

    /**
     * @param salesOrg sales organisation
     */
    public void setSalesOrg(String salesOrg);

    /**
     * @return distribution channel
     */
    public String getDistChannel();

    /**
     * @param distChannel distribution channel
     */
    public void setDistChannel(String distChannel);

}