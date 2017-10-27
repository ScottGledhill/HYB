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
 * @name slotSharedServiceModule.slotSharedService
 * @description
 * SlotSharedService provides methods to interact with the backend for shared slot information. 
 */
angular.module('slotSharedServiceModule', ['resourceModule', 'componentHandlerServiceModule', 'resourceLocationsModule'])
    .service('slotSharedService', function(restServiceFactory, $q, componentHandlerService, PAGES_CONTENT_SLOT_RESOURCE_URI) {
        var pagesContentSlotsResource = restServiceFactory.get(PAGES_CONTENT_SLOT_RESOURCE_URI);
        var currentPageId = componentHandlerService.getPageUID();
        var sharedSlotsPromise;

        var getUniqueArray = function(id, position, ids) {
            return ids.indexOf(id) === position;
        };

        var getSlotIdsFromList = function(pageContentSlotComponent) {
            return pageContentSlotComponent.slotId;
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#reloadSharedSlotMap
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * This function fetches all the slots with the pageUID and then retrieves the slotShared property for each slot.
         *
         * @param {String} pageUID of the page
         */
        this.reloadSharedSlotMap = function() {
            sharedSlotsPromise = pagesContentSlotsResource.get({
                pageId: currentPageId
            }).then(function(pagesContentSlotsResponse) {
                return $q.when((pagesContentSlotsResponse.pageContentSlotList || [])
                    .filter(getUniqueArray)
                    .reduce(function(acc, slot) {
                        acc[slot.slotId] = slot.slotShared;
                        return acc;
                    }, {}));
            });

            return sharedSlotsPromise;
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#isSlotShared
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not. 
         * Based on this service method the slot shared button is shown or hidden for a particular slotId
         *
         * @param {String} slotId of the slot
         */
        this.isSlotShared = function(slotId) {
            return (sharedSlotsPromise ? $q.when(sharedSlotsPromise) : this.reloadSharedSlotMap()).then(function(response) {
                sharedSlotsPromise = response;
                return sharedSlotsPromise[slotId];
            });
        };
    });
