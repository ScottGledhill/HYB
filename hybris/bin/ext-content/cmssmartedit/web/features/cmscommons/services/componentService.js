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
angular.module('componentServiceModule', ['restServiceFactoryModule', 'functionsModule', 'resourceLocationsModule'])
    /**
     * @ngdoc service
     * @name componentMenuModule.ComponentService
     *
     * @description
     * Service which manages component types and items
     */
    .service('ComponentService', function(restServiceFactory, hitch, $q, $log, parseQuery, TYPES_RESOURCE_URI, ITEMS_RESOURCE_URI, PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI) {

        var restServiceForTypes = restServiceFactory.get(TYPES_RESOURCE_URI);
        var restServiceForItems = restServiceFactory.get(ITEMS_RESOURCE_URI);
        var restServiceForAddExistingComponent = restServiceFactory.get(PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI);

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#createNewComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description given a component info and the component payload, a new componentItem is created and added to a slot
         *
         * @param {Object} componentInfo The basic information of the ComponentType to be created and added to the slot.
         * @param {String} componentInfo.componenCode componenCode of the ComponentType to be created and added to the slot.
         * @param {String} componentInfo.name name of the new component to be created.
         * @param {String} componentInfo.pageId pageId used to identify the current page template.
         * @param {String} componentInfo.slotId slotId used to identify the slot in the current template.
         * @param {String} componentInfo.position position used to identify the position in the slot in the current template.
         * @param {String} componentInfo.type type of the component being created.
         * @param {Object} componentPayload payload of the new component to be created.
         */
        this.createNewComponent = function(componentInfo, componentPayload) {

            var _payload = {};
            _payload.name = componentInfo.name;
            _payload.slotId = componentInfo.targetSlotId;
            _payload.pageId = componentInfo.pageId;
            _payload.position = componentInfo.position;
            _payload.typeCode = componentInfo.componentType;
            _payload.type = componentInfo.type;

            if (typeof componentPayload == "object") {
                for (var property in componentPayload) {
                    _payload[property] = componentPayload[property];
                }
            }

            return restServiceForItems.save(_payload);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#updateComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description Given a component info and the payload related to an existing component, the latter will be updated with the new supplied values.
         *
         * @param {Object} componentPayload of the new component to be created, including the info.
         * @param {String} componentPayload.componenCode of the ComponentType to be created and added to the slot.
         * @param {String} componentPayload.name of the new component to be created.
         * @param {String} componentPayload.pageId used to identify the current page template.
         * @param {String} componentPayload.slotId used to identify the slot in the current template.
         * @param {String} componentPayload.position used to identify the position in the slot in the current template.
         * @param {String} componentPayload.type of the component being created.
         */
        this.updateComponent = function(componentPayload) {
            return restServiceForItems.update(componentPayload);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#addExistingComponent
         * @methodOf componentMenuModule.ComponentService
         *
         * @description add an existing component item to a slot
         *
         * @param {String} pageId used to identify the page containing the slot in the current template.
         * @param {String} componentId used to identify the existing component which will be added to the slot.
         * @param {String} slotId used to identify the slot in the current template.
         * @param {String} position used to identify the position in the slot in the current template.
         */
        this.addExistingComponent = function(pageId, componentId, slotId, position) {

            var deferred = $q.defer();
            var _payload = {};
            _payload.pageId = pageId;
            _payload.slotId = slotId;
            _payload.componentId = componentId;
            _payload.position = position;

            return restServiceForAddExistingComponent.save(_payload);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadComponentTypes
         * @methodOf componentMenuModule.ComponentService
         *
         * @description all component types are retrieved
         */
        this.loadComponentTypes = function() {
            return restServiceForTypes.get({
                category: 'COMPONENT'
            });
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadComponentItem
         * @methodOf componentMenuModule.ComponentService
         *
         * @description load a component identified by its id
         */
        this.loadComponentItem = function(id) {
            return restServiceForItems.getById(id);
        };

        /**
         * @ngdoc method
         * @name componentMenuModule.ComponentService#loadPagedComponentItems
         * @methodOf componentMenuModule.ComponentService
         *
         * @description all existing component items for the current catalog are retrieved in the form of pages
         * used for pagination especially when the result set is very large.
         * 
         * @param {String} mask the search string to filter the results.
         * @param {String} pageSize the number of elements that a page can contain.
         * @param {String} page the current page number.
         */
        this.loadPagedComponentItems = function(mask, pageSize, page) {

            return restServiceForItems.get({
                pageSize: pageSize,
                currentPage: page,
                mask: mask,
                sort: 'name'
            });
        };
    });
