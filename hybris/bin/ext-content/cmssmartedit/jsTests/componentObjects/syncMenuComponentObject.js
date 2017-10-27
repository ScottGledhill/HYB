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

    BUTTON_SELECTOR: ".syncMenu button",

    click: function() {
        return browser.switchToParent().then(function() {
            return browser.click(this.BUTTON_SELECTOR);
        }.bind(this));
    },
    getPanel: function() {
        return element(by.css("synchronization-panel"));
    },
    getPanelHeader: function() {
        return element(by.css("page-synchronization-header > div")).getText();
    },
};
