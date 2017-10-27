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
 * @name slotRestrictionsServiceModule
 * @description
 * # The slotRestrictionsServiceModule
 *
 * The slotRestrictionsServiceModule contains a service that caches and returns the restrictions of a slot in a page. This restrictions determine
 * whether a component of a certain type is allowed or forbidden in a particular slot.
 *
 */
angular.module('slotRestrictionsServiceModule', ['yLoDashModule', 'componentHandlerServiceModule', 'restServiceFactoryModule'])
    /**
     * @ngdoc service
     * @name slotRestrictionsServiceModule.service:slotRestrictionsService
     *
     * @description
     * This service provides methods that cache and return the restrictions of a slot in a page. This restrictions determine
     * whether a component of a certain type is allowed or forbidden in a particular slot.
     */
    .service('slotRestrictionsService', function($q, lodash, componentHandlerService, restServiceFactory, CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI) {
        var _slotRestrictions = {};
        var _currentPageId = null;
        var _slotRestrictionsRestService;


        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#getSlotRestrictions
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This methods retrieves the list of restrictions applied to the slot identified by the provided ID.
         *
         * @param {String} slotId The ID of the slot whose restrictions to retrieve.
         * @returns {Promise} A promise containing an array with the restrictions applied to the slot.
         */
        this.getSlotRestrictions = function(slotId) {
            _slotRestrictionsRestService = _slotRestrictionsRestService || restServiceFactory.get(CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI);
            _currentPageId = _currentPageId || componentHandlerService.getPageUID();

            var restrictionId = this._getEntryId(_currentPageId, slotId);
            if (_slotRestrictions[restrictionId]) {
                return $q.when(_slotRestrictions[restrictionId]);
            }

            return _slotRestrictionsRestService.get({
                pageUid: _currentPageId,
                slotUid: slotId
            }).then(function(response) {
                _slotRestrictions[restrictionId] = response.validComponentTypes;
                return _slotRestrictions[restrictionId];
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#isComponentAllowedInSlot
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This methods determines whether a component of the provided type is allowed in the slot.
         *
         * @param {Object} slot the slot for which to verify if it allows a component of the provided type.
         * @param {String} slot.id The ID of the slot.
         * @param {Array} slot.components the list of components contained in the slot, they must contain an "id" property.
         * @param {Object} dragInfo contains the dragged object information
         * @param {String} dragInfo.componentType The smartedit type of the component being checked.
         * @param {String} dragInfo.componentId The smartedit id of the component being checked.
         * @param {String} dragInfo.slotId The smartedit id of the slot from which the component originates
         * @returns {Promise} A promise containing a boolean flag that determines whether a component of the provided type is allowed in the slot.
         */
        this.isComponentAllowedInSlot = function(slot, dragInfo) {
            return this.getSlotRestrictions(slot.id).then(function(currentSlotRestrictions) {

                var isComponentIdAllowed = slot.id === dragInfo.slotId || !slot.components.some(function(component) {
                    return component.id == dragInfo.componentId;
                });
                return isComponentIdAllowed && lodash.includes(currentSlotRestrictions, dragInfo.componentType);
            });
        };

        this.emptyCache = function() {
            _slotRestrictions = {};
            _currentPageId = null;
        };

        this._getEntryId = function(pageId, slotId) {
            return pageId + '_' + slotId;
        };
    });
