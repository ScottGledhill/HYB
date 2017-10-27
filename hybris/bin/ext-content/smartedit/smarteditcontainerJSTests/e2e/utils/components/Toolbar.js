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
module.exports = {
    experienceSelectorToolbar: function() {
        return element(by.css('.ySmartEditExperienceSelectorToolbar'));
    },
    renderButton: function() {
        return this.experienceSelectorToolbar().element(by.cssContainingText('button', 'Render Component'));
    },
    renderSlotButton: function() {
        return this.experienceSelectorToolbar().element(by.cssContainingText('button', 'Render Slot'));
    }
};
