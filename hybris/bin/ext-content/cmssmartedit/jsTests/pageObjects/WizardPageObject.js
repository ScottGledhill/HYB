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

    // Elements
    window: function() {
        return element(by.css('.modal-dialog'));
    },
    nextButton: function() {
        return element(by.css('.modal-footer button#ACTION_NEXT'));
    },
    backButton: function() {
        return element(by.css('.modal-footer button#ACTION_BACK'));
    },
    closeButton: function() {
        return element(by.css('.modal-header button.close'));
    },
    stepHeaderByIndex: function(index) {
        return element.all(by.css('.modal-body button')).get(index - 1);
    },
    submitButton: function() {
        return element(by.css('.modal-footer button#ACTION_DONE'));
    },
    getCurrentStep: function() {
        // This might break if the CSS changes. It'd be a good idea to define another class specific to the
        // tab headers.
        return element.all(by.css('.modal-wizard-template-step__action__enabled')).count();
    },
    getCurrentStepText: function() {
        return element(by.css('.modal-wizard-template-step__action__current')).getText().then(function(text) {
            return text.trim();
        });
    },
    // Actions
    isWindowsOpen: function() {
        return this.window().isPresent();
    },
    moveNext: function() {
        var button = this.nextButton();
        return browser.actions().mouseMove(button).click().perform();
    },
    moveBack: function() {
        var button = this.backButton();
        return browser.actions().mouseMove(button).click().perform();
    },
    moveToStepByIndex: function(index) {
        var tabHeader = this.stepHeaderByIndex(index);
        return browser.actions().mouseMove(tabHeader).click().perform();
    },
    scrollIntoView: function(item) {
        return browser.executeScript('arguments[0].scrollIntoView()', item.getWebElement());
    },
    submit: function() {
        var button = this.submitButton();
        return this.scrollIntoView(button).then(function() {
            return browser.actions().mouseMove(button).click().perform();
        });
    }
};