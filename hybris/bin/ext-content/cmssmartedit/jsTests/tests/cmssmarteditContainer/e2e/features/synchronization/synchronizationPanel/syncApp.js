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
angular.module('syncApp', ['i18nMockModule', 'synchronizationPanelModule', 'cmssmarteditContainerTemplates', 'cmscommonsTemplates', 'synchronizationMocksModule', 'restServiceFactoryModule', 'pageSynchronizationHeaderModule'])
    .controller('defaultController', function(restServiceFactory, $q) {

        this.itemId = "homepage";
        this.getSyncStatus = function(itemId) {
            return restServiceFactory.get("/cmssmarteditwebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/synchronizations/versions/Online/pages").getById(itemId).then(function(results) {
                results.selectAll = 'se.cms.synchronization.page.select.all.slots';
                return results;
            }.bind(this));
        }.bind(this);
        this.performSync = function(array) {
            return restServiceFactory.get("/cmssmarteditwebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/synchronizations/versions/Online/items").save({
                items: array
            });
        };
        this.headerTemplateUrl = "web/features/cmssmarteditContainer/components/synchronize/pages/pageSynchronizationHeaderWrapperTemplate.html";
    });
