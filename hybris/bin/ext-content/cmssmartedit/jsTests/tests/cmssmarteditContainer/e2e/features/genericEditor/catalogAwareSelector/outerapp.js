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
angular.module('outerapp', ['ui.bootstrap', 'ngRoute', 'treeModule', 'seProductSelectorModule', 'seCategorySelectorModule'])
    .config(function($routeProvider) {
        $routeProvider.when('/test', {
            templateUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/catalogAwareSelector/view.html',
            controller: 'testController',
            controllerAs: 'cont'
        });
    })
    .controller('testController', function(restServiceFactory, sharedDataService, ITEMS_RESOURCE_URI) {
        restServiceFactory.setDomain('thedomain');
        sharedDataService.set('experience', {
            siteDescriptor: {
                uid: 'apparel-uk'
            },
            catalogDescriptor: {
                catalogId: 'apparel-ukContentCatalog',
                catalogVersion: 'Online'
            }
        });

        this.model = {
            products: [],
            //products: ["300738116-apparelProductCatalog-Online"],
            categories: []
        };

        //this.thesmarteditComponentType = 'thesmarteditComponentType';
        //this.thesmarteditComponentId = 'thesmarteditComponentId';
        //this.structureApi = "structureApi";
        //this.displaySubmit = true;
        //this.displayCancel = true;
        //
        //this.contentApi = ITEMS_RESOURCE_URI;

    });
