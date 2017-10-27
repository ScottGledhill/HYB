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
    angular.module('adminTabModule', ['genericEditorModule', 'resourceLocationsModule', 'componentEditorModule'])
        .controller('adminTabCtrl', function() {

            this.$onInit = function() {

                this.tabStructure = [{
                    cmsStructureType: "ShortString",
                    qualifier: "uid",
                    i18nKey: 'type.cmsitem.uid.name',
                    editable: false
                }, {
                    cmsStructureType: "ShortString",
                    qualifier: "pk",
                    i18nKey: 'type.item.pk.name',
                    editable: false
                }];

            };

        })
        .component('adminTab', {
            transclude: false,
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/componentEditor/componentEditorTemplate.html',
            controller: 'adminTabCtrl',
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
