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
var navigationNodeEditor = function() {
    this.yDropDownComponent = require('../componentObjects/yDropDownComponentObject.js');
    this.pageURI = 'jsTests/tests/cmssmarteditContainer/e2e/features/navigation/navigationNodeEditor/navigationNodeEditorTest.html';
    browser.get(this.pageURI);
};

navigationNodeEditor.prototype = {

    totalEntriesCount: function() {
        return element.all(by.css('.nav-node-editor-entry-item')).count();
    },
    clickMoreMenuOptionByIndex: function(entryPosition, text) {
        return this.yDropDownComponent.selectLabel('.nav-node-editor-entry-item:nth-child(' + (entryPosition + 2) + ')', text);
    },
    getEntryTitle: function(entryPosition) {
        return element(by.css('.nav-node-editor-entry-item:nth-child(' + (entryPosition + 2) + ') .nav-node-editor-entry-item__text .nav-node-editor-entry-item__name')).getText();
    },
    clickItemSuperTypeDropdown: function() {
        return browser.click(element(by.id('itemSuperType')));
    },
    clickItemIdDropdown: function() {
        return browser.click(element(by.id('itemId')));
    },
    getItemIdDropdown: function(entryPosition) {
        return element(by.id('itemId-selector'));
    },
    getItemIdDropdownOptions: function() {
        return element.all(by.css('[id=\'itemId\'] ul[role=\'listbox\'] li[role=\'option\']'));
    },
    getItemIdScrollElement: function() {
        return element(by.xpath("//ul[@id='itemId-list']//y-infinite-scrolling/div"));
    },
    getAddNewEntryButton: function() {
        return element(by.id('navigation-node-editor-add-entry'));
    },
    getSaveEntryButton: function() {
        return element(by.id('navigation-node-editor-save-entry'));
    },
    getCancelEntryButton: function() {
        return element(by.id('navigation-node-editor-cancel'));
    },
    getEntrySearchDropdown: function() {
        return element(by.xpath("//div[@id='itemId-selector']/a"));
    },
    getValidationErrorElements: function(qualifier) {
        return element(by.css('[id="' + qualifier + '"] se-generic-editor-field-errors span'));
    },
    selectOption: function(dropdown, index) {

        var option;
        if (dropdown === 'itemId') {
            option = element(by.xpath("//ul[@id = '" + dropdown + "-list']//y-infinite-scrolling//li[" + index + "]"));
        } else {
            option = element(by.xpath("//ul[@id = '" + dropdown + "-list']/li/ul/li[" + index + "]"));
        }

        browser.wait(function() {
            return option.click().then(function() {
                return true;
            }, function() {
                return false;
            }).then(function(clickable) {
                return clickable;
            });
        }, 5000, 'Option not clickable');
    },
    clickSaveButton: function() {
        return browser.click(element(by.id('save')));
    },
    clickCancelButton: function() {
        return browser.click(element(by.id('cancel')));
    },
    editNodeName: function(text) {
        return browser.sendKeys(by.css('[id="name-shortstring"]'), text);
    },
    editNodeTitle: function(text) {
        return browser.sendKeys(by.css('[id="title-shortstring"]'), text);
    },
    getErrorProneEntriesCount: function() {
        return element.all(by.xpath('//div[contains(@class, "nav-node-editor-entry-item")]//div[@class="nav-node-editor-entry-item__name"]/span[contains(@class, " error-input")]')).count();
    }

};

module.exports = navigationNodeEditor;
