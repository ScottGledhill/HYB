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
angular.module('restrictionsPageListIconModule', ['translationServiceModule'])
    .controller('restrictionsPageListIconController', function($scope) {
        this.tooltipI18nKey = 'icon.tooltip.visibility';

        this.getIconUrl = function() {
            return this.numberOfRestrictions > 0 ? '/cmssmartedit/images/icon_restriction_small_blue.png' :
                '/cmssmartedit/images/icon_restriction_small_grey.png';
        };

        this.getInterpolationParameters = function() {
            return {
                numberOfRestrictions: this.numberOfRestrictions
            };
        };
    })
    .directive('restrictionsPageListIcon', function() {
        return {
            templateUrl: 'web/features/cmssmarteditContainer/components/pageRestrictions/restrictionsPageListIcon/restrictionsPageListIconTemplate.html',
            restrict: 'E',
            controller: 'restrictionsPageListIconController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                numberOfRestrictions: '='
            }
        };
    });
