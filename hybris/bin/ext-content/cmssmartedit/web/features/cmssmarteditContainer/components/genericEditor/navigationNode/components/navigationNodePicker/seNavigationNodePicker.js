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
angular.module('seNavigationNodePickerModule', ['functionsModule', 'resourceLocationsModule'])
    .controller('NavigationNodePickerController', function(URIBuilder, NAVIGATION_MANAGEMENT_RESOURCE_URI, NAVIGATION_NODE_ROOT_NODE_UID) {

        this.nodeURI = new URIBuilder(NAVIGATION_MANAGEMENT_RESOURCE_URI).replaceParams(this.uriContext).build();
        this.nodeTemplateUrl = 'web/features/cmssmarteditContainer/components/genericEditor/navigationNode/components/navigationNodePicker/navigationNodePickerRenderTemplate.html';
        this.rootNodeUid = NAVIGATION_NODE_ROOT_NODE_UID;

        this.showPicker = {};

        this.actions = {
            pick: function(treeService, handle) {
                this.model[this.qualifier] = handle.$modelValue.uid;
            }.bind(this),
            mouseEnter: function(treeService, $event, handle) {
                this.showPicker[handle.$modelValue.uid] = true;
            }.bind(this),
            mouseLeave: function(treeService, $event, handle) {
                delete this.showPicker[handle.$modelValue.uid];
            }.bind(this),
            show: function(treeService, handle) {
                return this.showPicker[handle.$modelValue.uid] === true;
            }.bind(this)
        };

    })
    /**
     * @ngdoc directive
     * @name seNavigationNodePickerModule.directive:seNavigationPicker
     * @scope
     * @restrict E
     * @element ANY
     *
     * @description
     * Directive that will build a navigation node picker and assign the uid of the selected node to model[qualifier].
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations.
     * @param {Object} model the model a property a property of which will be set to the selected node uid.
     * @param {String} qualifier the name of the model property that will be set to the selected node uid.
     */
    .component('seNavigationPicker', {
        templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/navigationNode/components/navigationNodePicker/seNavigationNodePickerTemplate.html',
        bindings: {
            uriContext: '<',
            model: '=',
            qualifier: '='
        },
        controller: 'NavigationNodePickerController',
        controllerAs: 'ctrl',
    });
