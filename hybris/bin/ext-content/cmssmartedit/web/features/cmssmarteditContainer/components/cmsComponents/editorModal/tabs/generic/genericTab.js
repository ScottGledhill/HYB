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
(function() {
    angular.module('genericTabModule', ['genericEditorModule', 'resourceLocationsModule', 'componentEditorModule'])
        .controller('genericTabCtrl', function(STRUCTURES_RESOURCE_URI) {

            this.$onInit = function() {
                this.structureApi = STRUCTURES_RESOURCE_URI + '/:smarteditComponentType?mode=DEFAULT';
            };
        })
        .component('genericTab', {
            transclude: false,
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/componentEditor/componentEditorTemplate.html',
            controller: 'genericTabCtrl',
            bindings: {
                saveTab: '=',
                resetTab: '=',
                cancelTab: '=',
                isDirtyTab: '=',
                componentId: '<',
                componentType: '<',
                tabId: '<',
                componentInfo: '<'
            }
        });
})();
