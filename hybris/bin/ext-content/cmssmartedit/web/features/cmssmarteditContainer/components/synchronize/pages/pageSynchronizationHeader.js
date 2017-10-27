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
angular.module('pageSynchronizationHeaderModule', ['translationServiceModule'])
    .component('pageSynchronizationHeader', {
        templateUrl: 'web/features/cmssmarteditContainer/components/synchronize/pages/pageSynchronizationHeaderTemplate.html',
        bindings: {
            lastSyncTime: '<'
        },
        controller: ['$translate', function($translate) {
            $translate('se.cms.synchronization.page.header.help').then(function(translation) {
                this.helpTemplate = "<span>" + translation + "</span>";
            }.bind(this));
        }],
        controllerAs: 'pageSync'

    });
