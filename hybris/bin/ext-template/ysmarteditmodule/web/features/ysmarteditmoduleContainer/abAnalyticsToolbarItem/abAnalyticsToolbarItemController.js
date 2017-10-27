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
angular.module('abAnalyticsToolbarItemControllerModule', ['iframeClickDetectionServiceModule'])
    .controller('abAnalyticsToolbarItemController', function($scope, iframeClickDetectionService) {
        this.$onInit = function() {
            this.isOpen = false;
            this.autoClose = 'outsideClick';

            this.openIcon = '/ysmarteditmodule/icons/icon_ab_analytics_white.png';
            this.closedIcon = '/ysmarteditmodule/icons/icon_ab_analytics_blue.png';
            this.label = 'AB Analytics';

            iframeClickDetectionService.registerCallback('closeAbAnalytics', function() {
                this.isOpen = false;
                $scope.$apply();
            }.bind(this));
        };

        this.preventDefault = function(event) {
            event.stopPropagation();
            event.preventDefault();
        };
    });
