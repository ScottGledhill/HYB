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
angular.module('editorModalServiceModule', ['genericEditorModalServiceModule', 'gatewayProxyModule', 'typeStructureRestServiceModule', 'adminTabModule', 'basicTabModule', 'visibilityTabModule', 'genericTabModule', 'renderServiceModule', 'componentEditorModule'])
    .factory('editorModalService', function(genericEditorModalService, gatewayProxy, renderService, typeStructureRestService) {

        function EditorModalService() {
            this.gatewayId = 'EditorModal';
            gatewayProxy.initForService(this, ["open", "openAndRerenderSlot"]);
        }

        var tabs = [{
            id: 'genericTab',
            title: 'editortabset.generictab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/genericTabTemplate.html'
        }, {
            id: 'basicTab',
            title: 'editortabset.basictab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/basicTabTemplate.html'
        }, {
            id: 'adminTab',
            title: 'editortabset.admintab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/adminTabTemplate.html',
        }, {
            id: 'visibilityTab',
            title: 'editortabset.visibilitytab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/visibilityTabTemplate.html'
        }];

        var _createComponentData = function(componentType, componentId, pageId, targetSlotId, position, type) {
            return {
                componentId: componentId,
                componentType: componentType,
                title: 'type.' + componentType.toLowerCase() + '.name',
                pageId: pageId,
                targetSlotId: targetSlotId,
                position: position,
                type: type
            };
        };

        var _filterTabs = function(componentId, structure) {
            // Will only display a genericTab if backend returns a structure for it
            // If it's a new component, only the relevant tabs will be displayed (displayOnComponentCreation)
            return tabs.filter(function(tab) {
                if (!componentId && tab.id == 'adminTab') {
                    return false;
                } else {
                    // the tab will be displayed if the structure is present (given the whole tab structure was returned with getWholeStructure:true)
                    // or if it's not the generic tab
                    // or if the tab structure is present
                    return (structure.attributes && structure.attributes.length > 0 || tab.id !== "genericTab" || !structure.attributes && structure.length > 0);
                }
            });
        };

        EditorModalService.prototype.openAndRerenderSlot = function(componentType, componentId, slotId) {
            return typeStructureRestService.getStructureByType(componentType, {
                getWholeStructure: true
            }).then(function(structure) {
                var componentData = _createComponentData(componentType, componentId);
                var filteredTabs = _filterTabs(componentId, structure);

                return genericEditorModalService.open(componentData, filteredTabs, function() {
                    renderService.renderSlots(slotId);
                });
            });

        };

        EditorModalService.prototype.open = function(componentType, componentId, options, pageId, targetSlotId, position) {
            // opts holds the default behavior
            var opts = options || {
                render: true
            };

            return typeStructureRestService.getStructureByType(componentType, {
                getWholeStructure: true
            }).then(function(structure) {
                var componentData = _createComponentData(componentType, componentId, pageId, targetSlotId, position, structure.type);
                var filteredTabs = _filterTabs(componentId, structure);

                return genericEditorModalService.open(componentData, filteredTabs, function() {
                    if (opts.render && componentId) {
                        renderService.renderComponent(componentData.componentId, componentData.componentType);
                    }
                });
            });

        };

        return new EditorModalService();
    });
