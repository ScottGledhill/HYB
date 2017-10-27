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
angular.module('componentTypeModule', []).directive('componentType', function($log, assetsService) {
    return {
        templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/componentMenu/componentTypeTemplate.html',
        restrict: 'E',
        transclude: false,
        replace: true,

        link: function(scope, element, attrs) {
            scope.imageRoot = assetsService.getAssetsRoot();
        }
    };
});
