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

    selectLabel: function(selectorOfTheDropDownparent, optionLabel) {
        return browser.click(by.css(selectorOfTheDropDownparent + ' [data-uib-dropdown-toggle]')).then(function(label) {
            return browser.click(by.cssContainingText(selectorOfTheDropDownparent + ' ul[data-uib-dropdown-menu] li > a', label));
        }.bind(undefined, optionLabel));
    },



};
