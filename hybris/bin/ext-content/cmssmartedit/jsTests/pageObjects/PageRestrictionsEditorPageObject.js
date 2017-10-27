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

module.exports = (function() {

    var pageRestrictionsEditor = {};

    pageRestrictionsEditor.pageURI = 'jsTests/tests/cmssmarteditContainer/e2e/features/restrictionsEditor/restrictionsTest.html';

    pageRestrictionsEditor.elements = {

        getRestrictionListItem: function(index) {
            return element(by.css('.ySEVisbilityModalContainer-leftPane #restriction-' + index + ' .ySERestrictionsList-item'));
        },

        getRestrictionListItemName: function(index) {
            return element(by.css('.ySEVisbilityModalContainer-leftPane #restriction-' + index + ' .ySERestrictionsList-item .ySERestrictionsNameHeader'));
        },

        getRestrictionKebabMenu: function(index) {
            return element(by.css('.ySEVisbilityModalContainer-leftPane #restriction-' + index + ' y-drop-down-menu button'));
        },

        getCloseButtonForRestriction: function(index) {
            return element(by.css('.ySEVisbilityModalContainer-leftPane #restriction-' + index + ' y-drop-down-menu a'));
        },

        getDisplayedRestriction: function() {
            return element(by.css('.ySEVisbilityModalContainer-rightPane #restriction-1'));
        },

        getDisplayedRestrictionName: function() {
            return pageRestrictionsEditor.elements.getDisplayedRestriction().element(by.css('.ySERestrictionsNameHeader'));
        },

        getAddNewButton: function() {
            return element(by.css('page-restrictions-editor .y-add-btn'));
        },

        getRestrictionTypeSelect: function() {
            return element(by.css('.ySEPageRestr-picker--select .ui-select-toggle'));
        },

        getFirstRestrictionTypeInSelect: function() {
            return element(by.cssContainingText('.ui-select-choices-row-inner', 'Time Restriction'));
        },

        getSearchField: function() {
            return element(by.css('.ySEPageRestr-search__input'));
        },

        getSearchResultForText: function(text) {
            return element(by.cssContainingText('.ySERestrictionResultsList-item', text));
        },

        getRestrictionInListWithName: function(name) {
            return element(by.cssContainingText('.ySEVisbilityModalContainer-leftPane .ySERestrictionsNameHeader', name));
        },

        getPageRestrictionsPicker: function() {
            return element(by.tagName('page-restriction-picker'));
        },

        getPageRestrictionsEditor: function() {
            return element(by.tagName('page-restrictions-editor'));
        }
    };

    pageRestrictionsEditor.actions = {
        selectRestriction: function(index) {
            pageRestrictionsEditor.elements.getRestrictionListItem(index).click();
        },

        removeRestriction: function(index) {
            pageRestrictionsEditor.elements.getRestrictionKebabMenu(index).click();
            pageRestrictionsEditor.elements.getCloseButtonForRestriction(index).click();
        },

        clickAddNew: function() {
            return browser.click(pageRestrictionsEditor.elements.getAddNewButton());
        },

        openRestrictionTypesSelect: function() {
            return browser.click(pageRestrictionsEditor.elements.getRestrictionTypeSelect());
        },

        selectFirstRestrictionTypeFromSelect: function() {
            return browser.click(pageRestrictionsEditor.elements.getFirstRestrictionTypeInSelect());
        },

        enterSearchText: function(searchString) {
            return browser.sendKeys(pageRestrictionsEditor.elements.getSearchField(), searchString);
        },

        clickSearchResultWithText: function(text) {
            return browser.click(pageRestrictionsEditor.elements.getSearchResultForText(text));
        }
    };

    pageRestrictionsEditor.assertions = {
        assertRestrictionIsSelected: function(index) {
            expect(pageRestrictionsEditor.elements.getDisplayedRestrictionName().getText()).toBe(pageRestrictionsEditor.elements.getRestrictionListItemName(index).getText());
        },
        assertRestrictionDisplayedName: function(name) {
            expect(pageRestrictionsEditor.elements.getDisplayedRestrictionName().getText()).toBe(name);
        },
        assertAddNewNotDisplayed: function() {
            expect(pageRestrictionsEditor.elements.getAddNewButton().isDisplayed()).toBe(false);
        },
        assertAddNewIsDisplayed: function() {
            expect(pageRestrictionsEditor.elements.getAddNewButton().isDisplayed()).toBe(true);
        },

        assertRestrictionInListWithName: function(name) {
            expect(pageRestrictionsEditor.elements.getRestrictionInListWithName(name).isPresent()).toBe(true);
        },

        assertNoSelectedRestrictionIsDisplayed: function() {
            expect(pageRestrictionsEditor.elements.getDisplayedRestriction().isPresent()).toBe(false);
        },

        assertPickerIsDisplayed: function() {
            expect(pageRestrictionsEditor.elements.getPageRestrictionsPicker().isPresent()).toBe(true);
        }
    };

    return pageRestrictionsEditor;

}());
