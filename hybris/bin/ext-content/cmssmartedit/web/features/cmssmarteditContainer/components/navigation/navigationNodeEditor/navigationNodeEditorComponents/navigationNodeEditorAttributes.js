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
angular.module("navigationNodeEditorAttributesModule", ['functionsModule', 'resourceLocationsModule', 'eventServiceModule', 'resourceModule', 'yLoDashModule'])
    .constant('NODE_CREATION_EVENT', 'nodeCreationEvent')
    .controller("navigationNodeEditorAttributesController", function(lodash, NAVIGATION_MANAGEMENT_RESOURCE_URI, URIBuilder, systemEventService, navigationNodeRestService, NODE_CREATION_EVENT) {

        var MODIFY_ENTRY_SET = 'modifyEntrySet';

        this.isContentLoaded = false;
        this.contentApi = new URIBuilder(NAVIGATION_MANAGEMENT_RESOURCE_URI).replaceParams(this.uriContext).build();

        this.tabId = 'ge-nav-node-editor';

        this.typeCode = "CMSNavigationNode";

        this.tabStructure = [{
            cmsStructureType: "ShortString",
            qualifier: "name",
            i18nKey: 'navigationmanagement.navnode.node.name',
            required: true
        }, {
            cmsStructureType: "ShortString",
            qualifier: "title",
            i18nKey: 'navigationmanagement.navnode.node.title',
            localized: true
        }];

        var _updateEntrySet = function(key, entries) {
            var clone = lodash.cloneDeep(entries);
            var updatedClone = clone.map(function(entry) {
                delete entry.id;
                return entry;
            });
            this.content.entries = updatedClone;
        }.bind(this);


        this.$onInit = function() {

            if (this.nodeUid) {
                var payload = angular.extend({
                    identifier: this.nodeUid
                }, this.uriContext);

                navigationNodeRestService.get(payload).then(function(response) {
                    this.content = response;
                    this.isContentLoaded = true;
                }.bind(this));
            } else if (this.parentUid) {
                this.content = {
                    parentUid: this.parentUid
                };
                this.isContentLoaded = true;
            } else {
                throw "navigationNodeEditorAttributes directive was provided with neither nodeUid nore parentUid";
            }

        };

        this.updateCallback = function(originalData, newData) {
            if (!this.nodeUid) {
                systemEventService.sendAsynchEvent(NODE_CREATION_EVENT, newData);
            }
        }.bind(this);

        // event listener 
        systemEventService.registerEventHandler(MODIFY_ENTRY_SET, _updateEntrySet);

        // destroy listener on destroy controller scope
        this.$onDestroy = function() {
            systemEventService.unRegisterEventHandler(MODIFY_ENTRY_SET, _updateEntrySet);
        };

    })
    .directive('navigationNodeEditorAttributes', function() {

        return {
            restrict: 'E',
            transclude: false,
            replace: false,
            templateUrl: 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/navigationNodeEditorComponents/navigationNodeEditorAttributesTemplate.html',
            controller: 'navigationNodeEditorAttributesController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                nodeUid: '=?',
                parentUid: '<?',
                uriContext: '=',
                reset: '=',
                submit: '=',
                isDirty: '=',
                navigationNodeEntryData: '='
            }
        };
    });
