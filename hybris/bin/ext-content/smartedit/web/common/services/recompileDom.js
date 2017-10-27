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
angular.module('recompileDomModule', [])

.directive("recompileDom", function($timeout) {
    return {
        restrict: "A",
        replace: false,
        transclude: true,
        template: "<div data-ng-if='showContent' data-ng-transclude></div>",
        scope: {
            trigger: '=recompileDom'
        },
        link: function(scope) {
            scope.showContent = true;
            scope.trigger = function() {
                scope.showContent = false;
                $timeout(function() {
                    scope.showContent = true;
                }, 0);
            };
        }
    };
});
