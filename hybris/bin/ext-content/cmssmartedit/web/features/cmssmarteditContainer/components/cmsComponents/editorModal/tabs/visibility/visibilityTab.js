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
    angular.module('visibilityTabModule', ['genericEditorModule', 'resourceLocationsModule', 'componentEditorModule'])
        .controller('visibilityTabCtrl', function() {

            this.$onInit = function() {

                this.tabStructure = [{
                    cmsStructureType: "Boolean",
                    qualifier: "visible",
                    prefixText: 'visible.prefix.text',
                    labelText: 'visible.postfix.text'
                }];
            };

        })
        .component('visibilityTab', {
            transclude: false,
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/componentEditor/componentEditorTemplate.html',
            controller: 'visibilityTabCtrl',
            bindings: {
                saveTab: '=',
                resetTab: '=',
                cancelTab: '=',
                isDirtyTab: '=',
                componentId: '<',
                componentType: '<',
                tabId: '<',
                componentInfo: '<'
            },
        });
})();
