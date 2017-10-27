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
angular.module('seMediaUploadFieldModule', ['assetsServiceModule'])
    .controller('seMediaUploadFieldController', function(assetsService) {
        this.displayImage = false;
        this.assetsRoot = assetsService.getAssetsRoot();
    })
    .directive('seMediaUploadField', function() {
        return {
            templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/media/components/mediaUploadForm/seMediaUploadFieldTemplate.html',
            restrict: 'E',
            scope: {},
            bindToController: {
                field: '=',
                model: '=',
                error: '='
            },
            controller: 'seMediaUploadFieldController',
            controllerAs: 'ctrl',
            link: function(scope, element, attrs, ctrl) {
                element.bind("mouseover", function(e) {
                    ctrl.displayImage = true;
                    scope.$digest();
                });
                element.bind("mouseout", function(e) {
                    ctrl.displayImage = false;
                    scope.$digest();
                });
            }
        };
    });
