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
angular.module('slotSyncButtonModule', ['catalogServiceModule', 'slotSynchronizationPanelModule', 'slotSynchronizationServiceModule', 'componentHandlerServiceModule', 'crossFrameEventServiceModule'])
    .controller('slotSyncButtonController', function($scope, SYNCHRONIZATION_POLLING, SYNCHRONIZATION_STATUSES, catalogService, slotSynchronizationService, componentHandlerService, crossFrameEventService) {

        catalogService.isContentCatalogVersionNonActive().then(function(isContentCatalogVersionNonActive) {
            this.isContentCatalogVersionNonActive = isContentCatalogVersionNonActive;
        }.bind(this));

        this.buttonName = 'slotSyncButton';
        this.isPopupOpened = false;

        $scope.$watch('ctrl.isPopupOpened', function(newValue, oldValue) {
            this.setRemainOpen({
                button: this.buttonName,
                remainOpen: this.isPopupOpened
            });
        }.bind(this));

        this.getSyncStatus = function() {
            var pageId = componentHandlerService.getPageUID();
            slotSynchronizationService.getSyncStatus(pageId, this.slotId).then(function(syncStatus) {
                this.isSlotInSync = syncStatus.status && syncStatus.status === SYNCHRONIZATION_STATUSES.IN_SYNC ? true : false;
            }.bind(this));
        }.bind(this);

        this.updateStatus = function(evenId, syncStatus) {
            var slotSyncStatus = (syncStatus.selectedDependencies || []).concat(syncStatus.sharedDependencies || []).find(function(slot) {
                return slot.itemId === this.slotId;
            }.bind(this)) || {};
            this.isSlotInSync = slotSyncStatus.status && slotSyncStatus.status === SYNCHRONIZATION_STATUSES.IN_SYNC ? true : false;
        };

        //var updateStatusCallback = this.updateStatus.bind(this);
        var updateStatusCallback = this.getSyncStatus;

        this.$onInit = function() {
            this.isSlotInSync = true;
            this.getSyncStatus();
            crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, updateStatusCallback);
        };

        this.$onDestroy = function() {
            crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, updateStatusCallback);
        };

    })
    .component('slotSyncButton', {
        templateUrl: 'web/features/cmssmartedit/components/synchronize/slots/slotSyncButtonTemplate.html',
        controller: 'slotSyncButtonController',
        controllerAs: 'ctrl',
        bindings: {
            setRemainOpen: '&',
            slotId: '@'
        }
    });
