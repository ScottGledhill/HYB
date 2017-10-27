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
var storefront = require('./Storefront.js');
var perspectives = require('./Perspectives.js');

module.exports = {
    getDottedSlotBorderForNonEmptySlot: function() {
        return perspectives.getElementInOverlay(storefront.TOP_HEADER_SLOT_ID)
            .element(by.css('.decorator-basic-slot-border'));
    },
    getDottedSlotBorderForEmptySlot: function() {
        return perspectives.getElementInOverlay(storefront.OTHER_SLOT_ID)
            .element(by.css('.decorator-basic-slot-border'));
    }
};
