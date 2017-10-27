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

    waitForDropdownsToLoad: function() {
        return browser.wait(EC.visibilityOf(element(by.xpath("//item-printer[@id='dropdownA-selected']/div/span"))), 5000, 'cannot select dropdown');
    },

    getDropdownValues: function(dropdowns) {

        return protractor.promise.all(dropdowns.map(function(dropdown) {
            return element(by.id(dropdown + '-selector')).getText();
        }));
    },

    assertListOfOptions: function(dropdownId, expectedOptions) {
        var dropdownOptionCssSelector = '#' + dropdownId + ' .ui-select-choices-row-inner';
        browser.wait(function() {
            return element.all(by.css(dropdownOptionCssSelector)).map(function(element) {
                return element.getText().then(function(text) {
                    return text;
                }, function() {
                    return '';
                });
            }).then(function(actualOptions) {
                return actualOptions.join(',') === expectedOptions.join(',');
            });
        }, 5000, 'Expected dropdown options for ' + dropdownId + ' to be ' + expectedOptions);
    },

    _getDropdowOptionsSelector: function(dropdownId) {
        return '#' + dropdownId + ' .ui-select-choices-row-inner';
    },

    getListOfOptions: function(dropdownId) {
        return element.all(by.css(this._getDropdowOptionsSelector(dropdownId))).map(function(element) {
            browser.waitForPresence(element);
            return element.getText();
        });
    },

    clickDropdown: function(dropdown) {
        return browser.click(element(by.id(dropdown + '-selector')));
    },

    selectOption: function(dropdownId, optionLabel) {
        return browser.click(by.cssContainingText(this._getDropdowOptionsSelector(dropdownId), optionLabel));
    },

    selectDropdown: function(dropdown) {
        return browser.click(element(by.id(dropdown + '-selector')));
    },

    searchAndAssertInDropdown: function(dropdownId, searchTerm, expectedOptions) {
        element(by.css('#' + dropdownId + ' .ui-select-dropdown input')).clear().sendKeys(searchTerm);
        this.assertListOfOptions(dropdownId, expectedOptions);
    },

};
