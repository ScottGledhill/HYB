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
angular.module('waitDialogServiceModule', ['gatewayProxyModule', 'modalServiceModule'])
    .factory('waitDialogService', function(gatewayProxy, modalService) {

        var WaitDialogService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        WaitDialogService.prototype.showWaitModal = function(customLoadingMessageLocalizedKey) {};

        WaitDialogService.prototype.hideWaitModal = function() {};


        return new WaitDialogService('waitDialog');
    });
