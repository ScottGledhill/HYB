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
    DEFAULT_PERSPECTIVES: {
        ALL: 'perspective.all.name',
        NONE: 'perspective.none.name'
    },
    selectPerspective: function(perspectiveName) {
        return browser.switchToParent().then(function() {
            return element(by.css('.ySEPerspectiveSelector button')).getText().then(function(perspectiveSelected) {
                if (perspectiveSelected.toUpperCase() !== perspectiveName.toUpperCase()) {
                    return browser.click(by.css('.ySEPerspectiveSelector')).then(function() {
                        return browser.click(by.cssContainingText('.ySEPerspectiveSelector ul li ', perspectiveName), "perspective " + perspectiveName + " is not clickable");
                    });
                } else {
                    return;
                }
            });
        });
    },
    perspectiveIsSelected: function(perspectiveName) {
        expect(element(by.css('.ySEPerspectiveSelector button span')).getText()).toBe(perspectiveName.toUpperCase());
    },
    getElementInOverlay: function(componentID, componentType) {
        var selector = '#smarteditoverlay .smartEditComponentX[data-smartedit-component-id="' + componentID + '"]';
        if (componentType) {
            selector += '[data-smartedit-component-type="' + componentType + '"]';
        }
        return element(by.css(selector));
    }

};
