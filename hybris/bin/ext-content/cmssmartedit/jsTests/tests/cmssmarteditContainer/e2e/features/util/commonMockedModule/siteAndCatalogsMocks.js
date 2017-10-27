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
angular.module('siteAndCatalogsMocks', ['ngMockE2E', 'functionsModule'])
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(function($httpBackend, parseQuery) {


        $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
            sites: [{
                previewUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html',
                name: {
                    en: "Apparels"
                },
                redirectUrl: 'redirecturlApparels',
                uid: 'apparel-uk'
            }]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogversiondetails/).respond({
            name: {
                en: "Apparels"
            },
            uid: 'apparel-uk',
            catalogVersionDetails: [{
                name: {
                    en: "Apparel UK Content Catalog"
                },
                catalogId: 'apparel-ukContentCatalog',
                version: 'Online',
                redirectUrl: null
            }, {
                name: {
                    en: "Apparel UK Content Catalog"
                },
                catalogId: 'apparel-ukContentCatalog',
                version: 'Staged',
                redirectUrl: null
            }]
        });

        $httpBackend.whenPOST(/thepreviewTicketURI/)
            .respond({
                ticketId: 'dasdfasdfasdfa',
                resourcePath: document.location.origin + '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html'
            });


        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();
    });
angular.module('smarteditloader').requires.push('siteAndCatalogsMocks');
angular.module('smarteditcontainer').requires.push('siteAndCatalogsMocks');
