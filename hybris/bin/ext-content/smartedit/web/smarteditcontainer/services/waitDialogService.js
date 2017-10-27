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

/**
 * @ngdoc overview
 * @name waitDialogServiceModule
 *
 * @description
 * A module that provides a service that can be used to display a loading overlay.
 */
angular.module('waitDialogServiceModule', ['gatewayProxyModule', 'modalServiceModule'])


/**
 * @ngdoc service
 * @name waitDialogServiceModule.service:waitDialogService
 *
 * @description
 * This service be used in order to display (or hide) a 'loading' overlay. The overlay should display on top of everything, preventing
 * the user from doing any action until the overlay gets hidden.
 */
.factory('waitDialogService', function(gatewayProxy, modalService) {

    var WaitDialogService = function(gatewayId) {
        this.gatewayId = gatewayId;
        gatewayProxy.initForService(this);
    };

    var modalArray = [];


    /**
     * @ngdoc method
     * @name waitDialogServiceModule.service:waitDialogService#showWaitModal
     * @methodOf waitDialogServiceModule.service:waitDialogService
     *
     * @description
     * This method can be called to display the loading overlay.
     *
     * @param {String} [customLoadingMessageLocalizedKey="wait.dialog.message"] The i18n key that corresponds to the message to be displayed.
     */
    WaitDialogService.prototype.showWaitModal = function(customLoadingMessageLocalizedKey) {
        if (modalArray.length === 0) {
            return modalService.open({
                templateUrl: 'web/common/core/services/waitDialog.html',
                cssClasses: "ySEWaitDialog",
                controller: ['modalManager', '$timeout', function(modalManager, $timeout) {
                    modalManager.loadingMessage = (customLoadingMessageLocalizedKey) ? customLoadingMessageLocalizedKey : "wait.dialog.message";
                    modalArray.push(modalManager);
                }]
            });
        }
    };

    /**
     * @ngdoc method
     * @name waitDialogServiceModule.service:waitDialogService#showWaitModal
     * @methodOf waitDialogServiceModule.service:waitDialogService
     *
     * @description
     * Removes the loading overlay.
     */
    WaitDialogService.prototype.hideWaitModal = function() {
        modalArray.forEach(function(modalManager) {
            modalManager.close();
        });
        modalArray.length = 0;
    };


    return new WaitDialogService('waitDialog');
});
