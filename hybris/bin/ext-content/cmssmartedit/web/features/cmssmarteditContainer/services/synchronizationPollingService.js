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
angular.module('synchronizationPollingServiceModule', ['renderServiceInterfaceModule', 'componentHandlerServiceModule', 'eventServiceModule', 'timerModule', 'gatewayProxyModule', 'crossFrameEventServiceModule', 'resourceModule', 'synchronizationConstantsModule', 'catalogServiceModule'])
    .run(function(syncPollingService) {
        syncPollingService.initSyncPolling();
    })
    .factory('syncPollingService', function($q, SYNCHRONIZATION_POLLING, OVERLAY_RERENDERED_EVENT, gatewayProxy, componentHandlerService, timerService, crossFrameEventService, systemEventService, synchronizationResource, catalogService) {

        var SyncPollingService = function(gatewayId) {
            this.gatewayId = gatewayId;

            gatewayProxy.initForService(this);
        };

        SyncPollingService.prototype.getSyncStatus = function(pageId, uriContext) {
            if (pageId === this.syncStatus.itemId) {
                return $q.when(this.syncStatus);
            } else {
                try {
                    if (componentHandlerService.getPageUID()) {
                        this.syncPollingTimer.restart(this.refreshInterval);
                        return this._fetchSyncStatus(componentHandlerService.getPageUID(), uriContext).then(function(syncStatus) {
                            this.syncStatus = syncStatus;
                            return syncStatus;
                        }.bind(this));
                    }
                } catch (e) {
                    if (e.name === "InvalidStorefrontPageError") {
                        this.syncPollingTimer.stop();
                        return this._fetchSyncStatus(pageId, uriContext);
                    } else {
                        throw e;
                    }
                }

            }
        };

        SyncPollingService.prototype._fetchSyncStatus = function(pageId, uriContext) {
            try {
                var pageUid = pageId || componentHandlerService.getPageUID();
                if (pageUid) {
                    return catalogService.isContentCatalogVersionNonActive().then(function(isContentCatalogVersionNonActive) {
                        if (isContentCatalogVersionNonActive) {

                            return catalogService.getContentCatalogActiveVersion(uriContext).then(function(activeVersion) {
                                return synchronizationResource.getPageSynchronizationGetRestService(uriContext).get({
                                    pageUid: pageUid,
                                    target: activeVersion
                                }).then(function(syncStatus) {
                                    if (JSON.stringify(syncStatus) !== JSON.stringify(this.syncStatus)) {
                                        crossFrameEventService.publish(SYNCHRONIZATION_POLLING.FAST_FETCH, syncStatus);
                                    }
                                    this.syncStatus = syncStatus;

                                    return syncStatus;
                                }.bind(this));
                            }.bind(this));

                        } else {
                            return $q.reject();
                        }
                    }.bind(this));
                }
            } catch (e) {
                if (e.name === "InvalidStorefrontPageError") {
                    this.syncPollingTimer.stop();
                    return $q.reject();
                } else {
                    throw e;
                }
            }
            return $q.when({});
        };

        SyncPollingService.prototype.changePollingSpeed = function(eventId, itemId) {

            if (eventId === SYNCHRONIZATION_POLLING.SPEED_UP) {
                this.syncStatus = {};
                if (itemId && this.triggers.indexOf(itemId) === -1) {
                    this.triggers.push(itemId);
                }

                this.refreshInterval = SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
            } else {
                if (itemId) {
                    this.triggers.splice(this.triggers.indexOf(itemId), 1);
                }
                if (this.triggers.length === 0) {
                    this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
                }
            }

            this.syncPollingTimer.restart(this.refreshInterval);

        };

        SyncPollingService.prototype.initSyncPolling = function() {

            this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
            this.triggers = [];
            this.syncStatus = {};

            var changePolling = this.changePollingSpeed.bind(this);

            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SPEED_UP, changePolling);
            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SLOW_DOWN, changePolling);

            crossFrameEventService.subscribe(OVERLAY_RERENDERED_EVENT, function() {
                this._fetchSyncStatus.bind(this)();
            }.bind(this));


            this.syncPollingTimer = timerService.createTimer(this._fetchSyncStatus.bind(this), this.refreshInterval);
            this.syncPollingTimer.start();
        };


        SyncPollingService.prototype.performSync = function(array, uriContext) {
            return catalogService.isContentCatalogVersionNonActive(uriContext).then(function(isNonActive) {
                if (isNonActive) {
                    return catalogService.getContentCatalogActiveVersion(uriContext).then(function(activeVersion) {
                        return synchronizationResource.getPageSynchronizationPostRestService(uriContext).save({
                            target: activeVersion,
                            items: array
                        });
                    });
                } else {
                    return $q.reject();
                }
            });
        };


        return new SyncPollingService('syncPollingService');

    });
