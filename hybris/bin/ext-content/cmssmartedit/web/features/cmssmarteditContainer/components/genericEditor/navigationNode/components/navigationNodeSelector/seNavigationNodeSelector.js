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
angular.module('seNavigationNodeSelector', ['seBreadcrumbModule', 'seNavigationNodePickerModule', 'resourceLocationsModule'])
    .controller('NavigationNodeSelectorController', function(CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, sharedDataService) {

        this.ready = false;

        sharedDataService.get('experience').then(function(experience) {
            var uriContext = {};
            uriContext[CONTEXT_SITE_ID] = experience.siteDescriptor.uid;
            uriContext[CONTEXT_CATALOG] = experience.catalogDescriptor.catalogId;
            uriContext[CONTEXT_CATALOG_VERSION] = experience.catalogDescriptor.catalogVersion;
            this.uriContext = uriContext;
            this.ready = true;
        }.bind(this));

        this.isReady = function() {
            return this.ready;
        };

        this.remove = function($event) {
            delete this.model[this.qualifier];
        }.bind(this);

    })
    .component('seNavigationNodeSelector', {
        templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/navigationNode/components/navigationNodeSelector/seNavigationNodeSelectorTemplate.html',
        controller: 'NavigationNodeSelectorController',
        controllerAs: 'nav',
        bindings: {
            field: '<',
            model: '<',
            qualifier: '<'
        }
    });
