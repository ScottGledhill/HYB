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
angular.module('seMediaSelectorModule', ['seMediaAdvancedPropertiesModule', 'searchMediaHandlerServiceModule', 'seMediaPrinterModule'])
    .controller('seMediaSelectorController', function(searchMediaHandlerService) {
        this.mediaTemplate = 'web/features/cmssmarteditContainer/components/genericEditor/media/components/mediaSelector/seMediaPrinterWrapperTemplate.html';

        this.fetchStrategy = {
            fetchEntity: function(id) {
                return searchMediaHandlerService.getItem(id, {});
            },
            fetchPage: function(mask, pageSize, currentPage) {
                return searchMediaHandlerService.getPage(mask, pageSize, currentPage, {});
            }
        };

    })
    .directive('seMediaSelector', function() {
        return {
            templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/media/components/mediaSelector/seMediaSelectorTemplate.html',
            restrict: 'E',
            scope: {},
            bindToController: {
                field: '=',
                model: '=',
                editor: '=',
                qualifier: '=',
                deleteIcon: '=',
                replaceIcon: '=',
                advInfoIcon: '='
            },
            controller: 'seMediaSelectorController',
            controllerAs: 'ctrl'
        };
    });
