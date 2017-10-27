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
angular.module('pageSynchronizationPanelModule', ['functionsModule', 'synchronizationPanelModule', 'pageSynchronizationServiceModule', 'pageSynchronizationHeaderModule', 'componentHandlerServiceModule'])
    .constant("SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL", "se.cms.synchronization.page.select.all.slots")
    .controller('PageSynchronizationPanelController', function($attrs, SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL, isBlank, pageSynchronizationService, componentHandlerService) {


        this.getSyncStatus = function(itemId) {
            return pageSynchronizationService.getSyncStatus(this.itemId, this.uriContext).then(function(syncStatus) {
                syncStatus.selectAll = SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL;
                return syncStatus;
            });
        }.bind(this);

        this.performSync = function(array) {
            return pageSynchronizationService.performSync(array, this.uriContext);
        }.bind(this);

        this.headerTemplateUrl = "web/features/cmssmarteditContainer/components/synchronize/pages/pageSynchronizationHeaderWrapperTemplate.html";

        this.$postLink = function() {
            this.showSyncButton = isBlank($attrs.syncItems);
            this.itemId = this.itemId || componentHandlerService.getPageUID();
        };

    })
    .component('pageSynchronizationPanel', {

        templateUrl: 'web/features/cmssmarteditContainer/components/synchronize/pages/pageSynchronizationPanelTemplate.html',
        controller: 'PageSynchronizationPanelController',
        controllerAs: 'pageSync',
        bindings: {
            syncItems: '=?',
            itemId: '=?',
            uriContext: '=?',
            onSelectedItemsUpdate: '<?'
        }
    });
