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
    getInSyncSlotDecorators: function() {
        return element.all(by.css('.sync-indicator-decorator.NOT_SYNC'));
    },

    getOutOfSyncSlotDecorators: function() {
        return element.all(by.css('.sync-indicator-decorator.IN_SYNC'));
    },
    getDecoratorBySlotId: function(slotId) {
        return element(by.css('[data-smartedit-component-id="' + slotId + '"] .sync-indicator-decorator'));
    },
    getDecoratorStatusBySlotId: function(slotId) {
        return this.getDecoratorBySlotId(slotId).getAttribute("data-sync-status");
    }
};
