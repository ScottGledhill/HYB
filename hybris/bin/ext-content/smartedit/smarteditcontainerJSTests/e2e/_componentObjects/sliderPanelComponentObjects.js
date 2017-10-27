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

    var componentObjects = {

        elements: {

            getModalSliderPanel: function() {
                return browser.findElement("#y-modal-dialog y-slider-panel");
            },

            getModalSliderPanelSaveButton: function() {
                return browser.findElement("#y-modal-dialog y-slider-panel .sliderpanel-footer .btn-default", true);
            },

            getModalSliderPanelCancelButton: function() {
                return browser.findElement("#y-modal-dialog y-slider-panel .sliderpanel-footer .btn-subordinate", true);
            },

            getModalSliderPanelBody: function() {
                return browser.findElement("#y-modal-dialog y-slider-panel .sliderpanel-body", true);
            },

            getModalSliderPanelTitle: function() {
                return browser.findElement("#y-modal-dialog y-slider-panel .sliderpanel-header", true);
            }

        },

        actions: {

            checkPresenceOfModalSliderPanel: function() {
                return element(by.css("#y-modal-dialog y-slider-panel"));
            },

            clickOnModalSliderPanelCancelButton: function() {
                return componentObjects.elements.getModalSliderPanelCancelButton().click();
            },

            clickOnModalSliderPanelSaveButton: function() {
                return componentObjects.elements.getModalSliderPanelSaveButton().click();
            },

            waitForNonPresenceOfModalSliderPanel: function() {
                return browser.waitForNonPresence("#y-modal-dialog y-slider-panel");
            }

        }

    };

    return componentObjects;

})();