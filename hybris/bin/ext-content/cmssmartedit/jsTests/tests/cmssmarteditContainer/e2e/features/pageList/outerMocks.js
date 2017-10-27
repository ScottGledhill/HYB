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
angular
    .module('OuterMocks', ['ngMockE2E', 'languageServiceModule', 'synchronizationMocksModule', 'resourceLocationsModule'])
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(
        function($httpBackend, languageService, I18N_RESOURCE_URI) {
            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var map = [{
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/i18nMock.js\"}",
                "key": "applications.i18nMockModule"
            }, {
                "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
                "key": "applications.cmssmarteditContainer"
            }];

            var mockedPages = [{
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "homepage",
                typeCode: "ContentPage",
                uid: "homepage"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "ActionTemplate",
                name: "adz a synced page",
                typeCode: "ActionPage",
                uid: "syncedpageuid"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "ADVERTISE",
                typeCode: "MyCustomType",
                uid: "uid3"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "MyCustomPageTemplate",
                name: "page2TitleSuffix",
                typeCode: "HomePage",
                uid: "uid4"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "ZTemplate",
                name: "page3TitleSuffix",
                typeCode: "ProductPage",
                uid: "uid5"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page3TitleSuffix",
                typeCode: "ProductPage",
                uid: "uid6"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page4TitleSuffix",
                typeCode: "WallPage",
                uid: "uid7"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page5TitleSuffix",
                typeCode: "CheckoutPage",
                uid: "uid8"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page6TitleSuffix",
                typeCode: "PromoPage",
                uid: "uid9"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "WELCOMEPAGE",
                typeCode: "ProfilePage",
                uid: "uid10"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page3TitleSuffix",
                typeCode: "ProductPage",
                uid: "uid11"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page3TitleSuffix",
                typeCode: "ProductPage",
                uid: "zuid12"
            }];

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages$/).respond({
                pages: mockedPages
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages$/).respond({
                pages: mockedPages
            });


            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);


            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond(function(method, url, data, headers) {
                    var dataObject = angular.fromJson(data);
                    if (dataObject.pageId) {
                        return [200, {
                            ticketId: 'previewTicketForPageId',
                            resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefrontAlternatelayout.html'
                        }];
                    } else {
                        return [200, {
                            ticketId: 'dasdfasdfasdfa',
                            resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html'
                        }];
                    }
                });

            $httpBackend.whenGET(/fragments/).passThrough();


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
                    active: true,
                    redirectUrl: null
                }, {
                    name: {
                        en: "Apparel UK Content Catalog"
                    },
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Staged',
                    active: false,
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET(/pagesrestrictions/).respond({
                "pageRestrictionList": [{
                    "pageId": "uid3",
                    "restrictionId": "timeRestrictionIdA"
                }, {
                    "pageId": "uid3",
                    "restrictionId": "timeRestrictionIdB"
                }]
            });


        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
