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
/**
 * @ngdoc overview
 * @name componentEditingFacadeModule
 * @description
 * # The componentEditingFacadeModule
 *
 * The componentEditingFacadeModule contains a service with methods that allow adding or removing components in the page.
 *
 */
angular.module('componentEditingFacadeModule', ['componentServiceModule', 'restServiceFactoryModule', 'renderServiceModule', 'translationServiceModule', 'componentServiceModule', 'editorModalServiceModule', 'alertServiceModule'])
    /**
     * @ngdoc service
     * @name componentEditingFacadeModule.service:componentEditingFacade
     *
     * @description
     * This service provides methods that allow adding or removing components in the page.
     */
    .service('componentEditingFacade', function($q, $translate, ComponentService, componentHandlerService, restServiceFactory, editorModalService, removeComponentService, renderService, alertService, PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI) {
        var _contentSlotComponentsRestService;

        function _generateSuccessMessage(sourceComponentId, targetSlotId, isVisible) {
            var i18nkey = isVisible ? 'cmsdraganddrop.success' : 'cmsdraganddrop.success.but.hidden';

            return $translate.instant(i18nkey, {
                sourceComponentId: sourceComponentId,
                targetSlotId: targetSlotId
            });
        }

        function _generateErrorMessage(sourceComponentId, targetSlotId, requestResponse) {
            var detailedError = (requestResponse.data && requestResponse.data.errors && requestResponse.data.errors.length > 0) ?
                requestResponse.data.errors[0].message : "";

            return $translate.instant('cmsdraganddrop.error', {
                sourceComponentId: sourceComponentId,
                targetSlotId: targetSlotId,
                detailedError: detailedError
            });
        }

        /**
         * @ngdoc method
         * @name componentEditingFacadeModule.service:componentEditingFacade#addNewComponentToSlot
         * @methodOf componentEditingFacadeModule.service:componentEditingFacade
         *
         * @description
         * This methods adds a new component to the slot and opens a component modal to edit its properties.
         *
         * @param {String} targetSlotId The ID of the slot where to drop the new component.
         * @param {String} componentType The type of the new component to add.
         * @param {Number} position The position in the slot where to add the new component.
         *
         */
        this.addNewComponentToSlot = function(targetSlotId, componentType, position) {
            var pageId = componentHandlerService.getPageUID();
            var componentId;

            return editorModalService.open(componentType, 0, 0, pageId, targetSlotId, position).then(function() {
                renderService.renderSlots([targetSlotId]);
            }, function() {
                // user cancelled

            }).catch(function(response) {
                var errorMessage = _generateErrorMessage(componentId, targetSlotId, response);
                alertService.pushAlerts([{
                    successful: false,
                    message: errorMessage,
                    closeable: true
                }]);
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name componentEditingFacadeModule.service:componentEditingFacade#addExistingComponentToSlot
         * @methodOf componentEditingFacadeModule.service:componentEditingFacade
         *
         * @description
         * This methods adds an existing component to the slot.
         *
         * @param {String} targetSlotId The ID of the slot where to drop the component.
         * @param {String} componentId The ID of the component to add into the slot.
         * @param {Number} position The position in the slot where to add the component.
         *
         */
        this.addExistingComponentToSlot = function(targetSlotId, componentId, position) {
            var pageId = componentHandlerService.getPageUID();

            return ComponentService.addExistingComponent(pageId, componentId, targetSlotId, position).then(function() {

                return ComponentService.loadComponentItem(componentId).then(function(response) {

                    var message = _generateSuccessMessage(componentId, targetSlotId, response.visible);
                    alertService.pushAlerts([{
                        successful: true,
                        message: message,
                        closeable: true
                    }]);

                    renderService.renderSlots(targetSlotId);
                });

            }, function(response) {
                var errorMessage = _generateErrorMessage(componentId, targetSlotId, response);
                alertService.pushAlerts([{
                    successful: false,
                    message: errorMessage,
                    closeable: true
                }]);

                return $q.reject();
            }.bind(this));
        };


        /**
         * @ngdoc method
         * @name componentEditingFacadeModule.service:componentEditingFacade#moveComponent
         * @methodOf componentEditingFacadeModule.service:componentEditingFacade
         *
         * @description
         * This methods moves a component from two slots in a page.
         *
         * @param {String} sourceSlotId The ID of the slot where the component is initially located.
         * @param {String} targetSlotId The ID of the slot where to drop the component.
         * @param {String} componentId The ID of the component to add into the slot.
         * @param {Number} position The position in the slot where to add the component.
         *
         */
        this.moveComponent = function(sourceSlotId, targetSlotId, componentId, position) {
            var contentSlotComponentsResourceLocation = PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI + '/pages/:pageId/contentslots/:currentSlotId/components/:componentId';
            _contentSlotComponentsRestService = _contentSlotComponentsRestService || restServiceFactory.get(contentSlotComponentsResourceLocation, 'componentId');
            return _contentSlotComponentsRestService.update({
                pageId: componentHandlerService.getPageUID(),
                currentSlotId: sourceSlotId,
                componentId: componentId,
                slotId: targetSlotId,
                position: position
            }).then(function() {
                renderService.renderSlots([sourceSlotId, targetSlotId]);
            }, function() {
                var errorMessage = (response === undefined) ? $translate.instant("cmsdraganddrop.move.failed", {
                    slotID: targetSlotId,
                    componentID: componentId
                }) : _generateErrorMessage(componentId, targetSlotId, response);
                alertService.pushAlerts([{
                    successful: false,
                    message: errorMessage,
                    closeable: true
                }]);
            }.bind(this));
        };
    });
