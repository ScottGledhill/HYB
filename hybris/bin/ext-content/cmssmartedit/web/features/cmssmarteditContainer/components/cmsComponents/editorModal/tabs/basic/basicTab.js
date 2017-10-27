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
    angular.module('basicTabModule', ['genericEditorModule', 'resourceLocationsModule', 'componentEditorModule'])
        .controller('basicTabCtrl', function() {

            this.$onInit = function() {

                this.tabStructure = [{
                    cmsStructureType: "ShortString",
                    qualifier: "name",
                    i18nKey: 'type.cmsitem.name.name',
                }, {
                    cmsStructureType: "Date",
                    qualifier: "creationtime",
                    i18nKey: 'type.item.creationtime.name',
                    editable: false
                }, {
                    cmsStructureType: "Date",
                    qualifier: "modifiedtime",
                    i18nKey: 'type.item.modifiedtime.name',
                    editable: false
                }];

            };

        })
        .component('basicTab', {
            transclude: false,
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/componentEditor/componentEditorTemplate.html',
            controller: 'basicTabCtrl',
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
