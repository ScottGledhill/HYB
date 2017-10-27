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
angular.module('pageListSyncIconModule', ['pageSynchronizationServiceModule', 'catalogServiceModule', 'synchronizationConstantsModule', 'crossFrameEventServiceModule', 'translationServiceModule'])

.controller('pageListSyncIconController', function(pageSynchronizationService, catalogService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING, crossFrameEventService, $translate) {

    this.classes = {};
    this.classes[SYNCHRONIZATION_STATUSES.UNAVAILABLE] = "hyicon-sync se-sync-button__sync__sync-not";
    this.classes[SYNCHRONIZATION_STATUSES.IN_SYNC] = "hyicon-done se-sync-button__sync__done";
    this.classes[SYNCHRONIZATION_STATUSES.NOT_SYNC] = "hyicon-sync se-sync-button__sync__sync-not";
    this.classes[SYNCHRONIZATION_STATUSES.IN_PROGRESS] = "hyicon-sync se-sync-button__sync__sync-not";
    this.classes[SYNCHRONIZATION_STATUSES.SYNC_FAILED] = "hyicon-sync se-sync-button__sync__sync-not";

    this.fetchSyncStatus = function() {
        return pageSynchronizationService.getSyncStatus(this.pageId, this.uriContext).then(function(response) {
            this.syncStatus = response;
        }.bind(this), function() {
            this.syncStatus.status = SYNCHRONIZATION_STATUSES.UNAVAILABLE;
        }.bind(this));
    }.bind(this);


    this.triggerFetch = function(eventId, eventData) {
        if (eventData.itemId == this.pageId) {
            this.fetchSyncStatus();
        }
    };

    this.$onInit = function() {
        catalogService.isContentCatalogVersionNonActive(this.uriContext).then(function(isNonActive) {

            if (isNonActive) {
                // set initial sync status to unavailable
                this.syncStatus = {
                    status: SYNCHRONIZATION_STATUSES.UNAVAILABLE
                };

                crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.triggerFetch.bind(this));

                // the first sync fetch is done manually
                this.fetchSyncStatus();
            }
        }.bind(this));
    };

})

/**
 * @ngdoc directive
 * @name pageListSyncIconModule.directive:pageListSyncIcon
 * @restrict E
 * @element sync-icon
 *
 * @description
 * The Page Synchronization Icon component is used to display the icon that describes the synchronization status of a page.
 *
 * @param {string} pageId The identifier of the page for which the synchronzation status must be displayed.
 * 
 */
.component('pageListSyncIcon', {
    templateUrl: 'web/features/cmssmarteditContainer/components/synchronize/pages/pageListSyncIcon/pageListSyncIconTemplate.html',
    controller: 'pageListSyncIconController',
    controllerAs: '$ctrl',
    bindings: {
        pageId: '<',
        uriContext: '<'
    }
});
