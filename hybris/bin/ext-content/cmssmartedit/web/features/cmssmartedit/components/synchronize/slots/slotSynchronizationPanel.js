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
angular.module('slotSynchronizationPanelModule', ['synchronizationPanelModule', 'slotSynchronizationServiceModule', 'componentHandlerServiceModule'])
    .controller('slotSynchronizationPanelController', function(slotSynchronizationService, componentHandlerService) {

        this.getSyncStatus = function() {
            var pageId = componentHandlerService.getPageUID();
            return slotSynchronizationService.getSyncStatus(pageId, this.slotId);
        }.bind(this);

        this.performSync = function(array) {
            return slotSynchronizationService.performSync(array);
        };

    })
    .component('slotSynchronizationPanel', {
        templateUrl: 'web/features/cmssmartedit/components/synchronize/slots/slotSynchronizationPanelTemplate.html',
        controller: 'slotSynchronizationPanelController',
        controllerAs: 'slotSync',
        bindings: {
            slotId: '<'
        }
    });
