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
angular.module('synchronizationPanelModule', ['yHelpModule', 'translationServiceModule', 'alertServiceModule', 'timerModule', 'alertsBoxModule', 'eventServiceModule', 'synchronizationPollingServiceModule', 'crossFrameEventServiceModule', 'waitDialogServiceModule', 'synchronizationConstantsModule'])
    .controller('SynchronizationPanelController', function($attrs, $translate, isBlank, SYNCHRONIZATION_POLLING, SYNCHRONIZATION_STATUSES, alertService, systemEventService, timerService, crossFrameEventService, waitDialogService) {

        this.syncPendingItems = [];
        this.selectedItems = [];
        this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
        this.getRows = function() {
            return this.syncStatus ? [this.syncStatus].concat(this.syncStatus.selectedDependencies) : [];
        };
        this.selectionChange = function(index) {

            this.syncStatus.selectedDependencies.forEach(function(item) {
                item.selected = (index === 0 && item.status !== SYNCHRONIZATION_STATUSES.IN_SYNC) ? true : item.selected;
            }.bind(this));

            this.selectedItems = this.getRows().filter(function(item) {
                return item.selected;
            });

            if (this.onSelectedItemsUpdate) {
                this.onSelectedItemsUpdate(this.selectedItems);
            }

        };
        this.preserveSelection = function(selectedItems) {

            var itemIds = (selectedItems || []).map(function(item) {
                return item.itemId;
            }, []);

            this.getRows().forEach(function(item) {
                item.selected = itemIds.indexOf(item.itemId) > -1 && item.status !== SYNCHRONIZATION_STATUSES.IN_SYNC;
            });
        };
        this.isDisabled = function(item) {
            return (item !== this.syncStatus && this.syncStatus.selected) || item.status === SYNCHRONIZATION_STATUSES.IN_SYNC;
        };

        this.isSyncButtonDisabled = function() {
            return (this.selectedItems.length === 0 || this.syncPendingItems.length !== 0);
        };
        this.isInSync = function(dependency) {
            return dependency.status === SYNCHRONIZATION_STATUSES.IN_SYNC;
        };
        this.isOutOfSync = function(dependency) {
            return dependency.status === SYNCHRONIZATION_STATUSES.NOT_IN_SYNC;
        };
        this.isInProgress = function(dependency) {
            return dependency.status === SYNCHRONIZATION_STATUSES.IN_PROGRESS;
        };
        this.isSyncFailed = function(dependency) {
            return dependency.status === SYNCHRONIZATION_STATUSES.SYNC_FAILED;
        };
        this.hasHelp = function(dependency) {
            return dependency.dependentItemTypesOutOfSync && dependency.dependentItemTypesOutOfSync.length > 0;
        };
        this.buildInfoTemplate = function(dependency) {
            if (dependency && dependency.dependentItemTypesOutOfSync) {
                return dependency.dependentItemTypesOutOfSync.reduce(function(accumulator, item) {
                    accumulator += ("<br/>" + $translate.instant(item.i18nKey));
                    return accumulator;
                }, "");
            }
        };
        this.infoTitle = "se.cms.synchronization.panel.update.title";

        this.syncItems = function() {

            var syncPayload = this.getRows().filter(function(item) {
                return item.selected;
            }).map(function(item) {
                return {
                    itemId: item.itemId,
                    itemType: item.itemType
                };
            });
            Array.prototype.push.apply(this.syncPendingItems, syncPayload.map(function(el) {
                return el.itemId;
            }));

            if (this.selectedItems.length > 0) {
                waitDialogService.showWaitModal("sync.synchronizing");
                this.performSync(syncPayload).then(function() {
                    this.refreshInterval = SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
                    this.resynchTimer.restart(this.refreshInterval);
                    systemEventService.sendAsynchEvent(SYNCHRONIZATION_POLLING.SPEED_UP, this.itemId);
                }.bind(this));
            }
        }.bind(this);


        this.fetchSyncStatus = function() {
            return this.getSyncStatus(this.itemId).then(function(syncStatus) {

                this.syncStatus = syncStatus;
                this.preserveSelection(this.selectedItems);

                this.selectionChange();
                this._updateStatus();
                if (this.syncPendingItems.length === 0) {
                    this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
                    this.resynchTimer.restart(this.refreshInterval);
                    systemEventService.sendAsynchEvent(SYNCHRONIZATION_POLLING.SLOW_DOWN, this.itemId);
                }
            }.bind(this));
        };

        this._updateStatus = function() {
            var anyFailures = false;

            var itemsInErrors = '';
            var preNbSyncPendingItems = this.syncPendingItems.length;
            this.getRows().forEach(function(item) {
                if (this.syncPendingItems.indexOf(item.itemId) > -1 && item.status === SYNCHRONIZATION_STATUSES.SYNC_FAILED) {
                    itemsInErrors = ' ' + itemsInErrors + item.itemId;
                    anyFailures = true;
                }
                if (this.syncPendingItems.indexOf(item.itemId) > -1 && item.status != SYNCHRONIZATION_STATUSES.IN_PROGRESS) {
                    this.syncPendingItems.splice(this.syncPendingItems.indexOf(item.itemId), 1);
                }

                // if there was at least one item in the sync queue, and the last item has been resolved
                // or if there wasn't any item in the sync queue
                if ((preNbSyncPendingItems && this.syncPendingItems.length === 0) || preNbSyncPendingItems === 0) {
                    preNbSyncPendingItems = 0;
                    waitDialogService.hideWaitModal();
                }
                if (item.status === SYNCHRONIZATION_STATUSES.IN_PROGRESS && this.syncPendingItems.indexOf(item.itemId) === -1) {
                    this.syncPendingItems.push(item.itemId);
                }
            }.bind(this));

            if (anyFailures) {
                alertService.pushAlerts([{
                    successful: false,
                    message: $translate.instant('se.cms.synchronization.panel.failure.message', {
                        items: itemsInErrors
                    }),
                    closeable: true
                }]);
            }

        };

        this.$onInit = function() {
            this.unSubscribeFastFetch = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus.bind(this));
            this.fetchSyncStatus();

            //start timer polling
            this.resynchTimer = timerService.createTimer(this.fetchSyncStatus.bind(this), this.refreshInterval);
            this.resynchTimer.start();

        };

        this.$onDestroy = function() {
            this.resynchTimer.teardown();
            this.unSubscribeFastFetch();
            systemEventService.sendAsynchEvent(SYNCHRONIZATION_POLLING.SLOW_DOWN, this.itemId);
        };

        this.$postLink = function() {
            this.showSyncButton = isBlank($attrs.syncItems);
        };
    })
    /**
     * @ngdoc directive
     * @name synchronizationPanelModule.component:synchronizationPanel
     * @scope
     * @restrict E
     * @element synchronization-panel
     * 
     * @description
     * This component reads and performs synchronization for a main object and its dependencies (from a synchronization perspective).
     * The component will list statuses of backend-configured dependent types.
     * The main object and the listed dependencies will display as well lists of logical grouping of dependencies (in a popover)
     * that cause the main object or the listed dependency to be out of sync.
     * @param {String} itemId the unique identifier of the main object of the synchronization panel
     * @param {Function} getSyncStatus the callback, invoked with itemId that returns a promise of the aggregated sync status required for the component to initialize.
     * the expected format of the aggregated status is the following:
     * ````
     *{
     *  itemId:'someUid',
     *  itemType:'AbstractPage',
     *  name:'Page1',
     *  lastSyncTime: long,
     *  selectAll: 'some.key.for.first.row.item' // Some i18nKey to display the title for the first item
     *  status:{name: 'IN_SYNC', i18nKey: 'some.key.for.in.sync'}  //'IN_SYNC'|'NOT_IN_SYNC'|'NOT_AVAILABLE'|'IN_PROGRESS'|'SYNC_FAILED'
     *  dependentItemTypesOutOfSync:[
     *                          {code: 'MetaData', i18nKey: 'some.key.for.MetaData'}, 
     *                          {code: 'Restrictions', i18nKey: 'some.key.for.Restrictions'}, 
     *                          {code: 'Slot', i18nKey: 'some.key.for.Slot'}, 
     *                          {code: 'Component', i18nKey: 'some.key.for.Component'},  
     *                          {code: 'Navigation', i18nKey: 'some.key.for.Navigation'}, 
     *                          {code: 'Customization', i18nKey: 'some.key.for.Customization'}
     *                          ],
     *  selectedDependencies:[
     *  {
     *      itemId:'someUid',
     *      itemType:'ContentSlot',
     *      name:'Slot1',
     *      lastSyncTime: long,
     *      selectAll: 'some.key.for.first.row.item' // Some i18nKey to display the title for the first item
     *      status:{name: 'IN_SYNC', i18nKey: 'some.key.for.in.sync'}  //'IN_SYNC'|'NOT_IN_SYNC'|'NOT_AVAILABLE'|'IN_PROGRESS'|'SYNC_FAILED'
     *      dependentItemTypesOutOfSync:[
     *                              {code: 'Component', i18nKey: 'some.key.for.Component'}, 
     *                              {code: 'Navigation', i18nKey: 'some.key.for.Navigation'}, 
     *                              {code: 'Customization', i18nKey: 'some.key.for.Customization'}
     *                              ] 
     *  }
     *  ]
     *}
     * ````
     *  @param {Function} performSync the function invoked with the list of item unique identifiers to perform synchronization. It returns a promise of the resulting sync status with the following values : 'IN_SYNC'|'NOT_IN_SYNC'|'NOT_AVAILABLE'|'IN_PROGRESS'|'SYNC_FAILED'
     *  @param {String} headerTemplateUrl the path to an HTML template to customize the header of the synchronization panel. Optional.
     *  @param {Function} syncItems enables parent directive/component to invoke the internal synchronization. Optional, if not set, the panel will display a sync button for manual synchronization.
     *  @param {Function} [onSelectedItemsUpdate] callback function invoked when the the list of selected items change
     */
    .component('synchronizationPanel', {

        templateUrl: 'web/features/cmscommons/components/synchronize/synchronizationPanelTemplate.html',
        controller: 'SynchronizationPanelController',
        controllerAs: 'sync',
        bindings: {
            itemId: '<',
            getSyncStatus: '<',
            performSync: '<',
            headerTemplateUrl: '<?',
            syncItems: '=?',
            onSelectedItemsUpdate: '<?'
        }
    });
