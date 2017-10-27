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
angular.module('pageSyncMenuToolbarItemModule', ['crossFrameEventServiceModule', 'catalogServiceModule', 'pageSynchronizationPanelModule'])
    .controller('PageSyncMenuToolbarItemController', function(crossFrameEventService, catalogService, assetsService, systemEventService, iframeClickDetectionService, componentHandlerService, pageSynchronizationService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING, $scope, $element) {

        this.fetchSyncStatus = function() {
            pageSynchronizationService.getSyncStatus(componentHandlerService.getPageUID()).then(function(syncStatus) {
                this.isNotInSync = syncStatus.status != SYNCHRONIZATION_STATUSES.IN_SYNC;
            }.bind(this));
        }.bind(this);


        this.$onInit = function() {

            this.isContentCatalogVersionNonActive = false;

            catalogService.isContentCatalogVersionNonActive().then(function(isNonActive) {
                if (isNonActive) {
                    this.isContentCatalogVersionNonActive = true;
                    this.isOpen = false;
                    this.icons = {
                        open: assetsService.getAssetsRoot() + "/images/icon_info_white.png",
                        closed: assetsService.getAssetsRoot() + "/images/icon_info_blue.png"
                    };
                    this.menuIcon = this.icons.closed;

                    $scope.$watch('$ctrl.isOpen', function() {
                        if (this.isOpen) {
                            this.menuIcon = this.icons && this.icons.open;
                            $element.closest('.ySEHybridAction').addClass("ySEOpenComponent");
                        } else {
                            this.menuIcon = this.icons && this.icons.closed;
                            $element.closest('.ySEHybridAction').removeClass("ySEOpenComponent");
                        }
                    }.bind(this));

                    iframeClickDetectionService.registerCallback('closeToolbarMenu', function() {
                        this.isOpen = false;
                        $scope.$apply();
                    }.bind(this));

                    this.isNotInSync = false;

                    crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus);

                    this.fetchSyncStatus();

                }
            }.bind(this));
        };

    })
    .component('pageSyncMenuToolbarItem', {
        templateUrl: 'web/features/cmssmarteditContainer/components/synchronize/pages/syncMenu/pageSyncMenuToolbarItemTemplate.html',
        controller: 'PageSyncMenuToolbarItemController',
        controllerAs: '$ctrl',
        bindings: {
            toolbarItem: '<item'
        }
    });
