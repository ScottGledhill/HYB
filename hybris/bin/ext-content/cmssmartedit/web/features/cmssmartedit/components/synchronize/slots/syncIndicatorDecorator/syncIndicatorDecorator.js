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
angular.module('syncIndicatorDecoratorModule', ['catalogServiceModule', 'slotSynchronizationServiceModule', 'componentHandlerServiceModule', 'synchronizationConstantsModule', 'crossFrameEventServiceModule'])
    .controller('syncIndicatorController', function($q, catalogService, slotSynchronizationService, crossFrameEventService, componentHandlerService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING) {

        this.isVersionNonActive = false;


        this.$onInit = function() {
            // initial sync status is set to unavailable until the first fetch
            this.syncStatus = {
                status: SYNCHRONIZATION_STATUSES.UNAVAILABLE
            };
            this.pageId = componentHandlerService.getPageUID();

            crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus.bind(this));

            catalogService.isContentCatalogVersionNonActive().then(function(isNonActive) {
                this.isVersionNonActive = isNonActive;
                if (this.isVersionNonActive) {
                    this.fetchSyncStatus();
                }
            }.bind(this));
        };

        this.fetchSyncStatus = function() {
            return this.isVersionNonActive ? slotSynchronizationService.getSyncStatus(this.pageId, JSON.parse(this.smarteditProperties).smarteditComponentId).then(function(response) {
                this.syncStatus = response;
            }.bind(this), function() {
                this.syncStatus.status = SYNCHRONIZATION_STATUSES.UNAVAILABLE;
            }.bind(this)) : $q.when();
        }.bind(this);

    })
    .directive('syncIndicator', [function() {
        return {
            templateUrl: 'web/features/cmssmartedit/components/synchronize/slots/syncIndicatorDecorator/syncIndicatorDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'syncIndicatorController',
            controllerAs: 'ctrl',
            bindToController: {
                smarteditProperties: '@',
                active: '='
            }
        };
    }]);
