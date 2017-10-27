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
    .module('OuterMocks', ['ngMockE2E', 'languageServiceModule', 'resourceLocationsModule'])
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(
        function($httpBackend, languageService) {
            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            $httpBackend.whenGET('/smarteditwebservices/v1/i18n/translations/' + languageService.getBrowserLocale()).respond({
                'se.label.page.name': 'Page Name',
                'se.label.page.display.conditions': 'Display Conditions',
                'se.label.page.primary': 'Primary',
                'page.displaycondition.primary': 'Primary',
                'page.displaycondition.primary.description': 'This is a primary page and it will be displayed if no other variation page exists',
                'page.displaycondition.variation': 'Variation',
                'page.displaycondition.variation.description': 'This is a variation page that supports restriction rules to control the page visibility',
                'se.cms.perspective.basic.name': 'Basic CMS',
                'se.cms.perspective.advanced.name': 'Advanced CMS',
                'page.restrictions.criteria': 'Criteria: ',
                'page.restrictions.criteria.all': 'ALL',
                'page.restrictions.criteria.any': 'ANY'
            });

            var map = [{
                "id": "2",
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "id": "3",
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "id": "4",
                "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
                "key": "applications.cmssmarteditContainer"
            }, {
                "id": "5",
                "value": "{\"smartEditLocation\":\"/web/webroot/cmssmartedit/js/cmssmartedit.js\"}",
                "key": "applications.cmssmartedit"
            }, {
                "id": "6",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/CommonSlotsMocks.js\"}",
                "key": "applications.commonSlotMocksModule"
            }, {
                "id": "7",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/PageContentComponentSlotsMocks.js\"}",
                "key": "applications.pagesContentSlotsComponentsMocks"
            }, {
                "id": "8",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/PageContentSlotsMocks.js\"}",
                "key": "applications.pagesContentSlotsMocks"
            }, {
                "id": "9",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/LanguagesMock.js\"}",
                "key": "applications.LanguageMockModule"
            }, {
                "id": "10",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/i18nMock.js\"}",
                "key": "applications.i18nMockModule"
            }];


            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);


            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond({
                    ticketId: 'dasdfasdfasdfa',
                    resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html'
                });

            $httpBackend.whenGET(/cmswebservices\/v1\/languages/).respond({
                languages: [{
                    language: 'en',
                    required: true
                }]
            });

            $httpBackend
                .whenGET("/cmswebservices/v1/i18n/languages/" + languageService.getBrowserLocale())
                .respond({});

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'French',
                    isocode: 'fr'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
                sites: [{
                    previewUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html',
                    name: {
                        type: "map",
                        value: {
                            en: "Electronics"
                        }
                    },
                    redirectUrl: 'redirecturlElectronics',
                    uid: 'electronics'
                }, {
                    previewUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html',
                    name: {
                        type: "map",
                        value: {
                            en: "Apparels"
                        }
                    },
                    redirectUrl: 'redirecturlApparels',
                    uid: 'apparel-uk'
                }, {
                    previewUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html',
                    name: {
                        type: "map",
                        value: {
                            en: "Apparels"
                        }
                    },
                    redirectUrl: 'redirecturlApparels',
                    uid: 'apparel-de'
                }, {
                    previewUrl: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html',
                    name: {
                        type: "map",
                        value: {
                            en: "Toys"
                        }
                    },
                    redirectUrl: 'redirectSomeOtherSite',
                    uid: 'toys'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/catalogversiondetails/).respond({
                name: {
                    type: "map",
                    value: {
                        en: "Electronics"
                    }
                },
                uid: 'electronics',
                catalogVersionDetails: [{
                    name: {
                        type: "map",
                        value: {
                            en: "Electronics Content Catalog"
                        }
                    },
                    catalogId: 'electronicsContentCatalog',
                    version: 'Online',
                    redirectUrl: null
                }, {
                    name: {
                        type: "map",
                        value: {
                            en: "Electronics Content Catalog"
                        }
                    },
                    catalogId: 'electronicsContentCatalog',
                    version: 'Staged',
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogversiondetails/).respond({
                name: {
                    type: "map",
                    value: {
                        en: "Apparels"
                    }
                },
                uid: 'apparel-uk',
                catalogVersionDetails: [{
                    name: {
                        type: "map",
                        value: {
                            en: "Apparel UK Content Catalog"
                        }
                    },
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Online',
                    redirectUrl: null
                }, {
                    name: {
                        type: "map",
                        value: {
                            en: "Apparel UK Content Catalog"
                        }
                    },
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Staged',
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-de\/catalogversiondetails/).respond({
                name: {
                    type: "map",
                    value: {
                        en: "Apparels"
                    }
                },
                uid: 'apparel-de',
                catalogVersionDetails: [{
                    name: {
                        type: "map",
                        value: {
                            en: "Apparel DE Content Catalog"
                        }
                    },
                    catalogId: 'apparel-deContentCatalog',
                    version: 'Online',
                    redirectUrl: null
                }, {
                    name: {
                        type: "map",
                        value: {
                            en: "Apparel DE Content Catalog"
                        }
                    },
                    catalogId: 'apparel-deContentCatalog',
                    version: 'Staged',
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET('/cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/pages\/homepage').respond({
                "name": "Homepage",
                "uid": "homepage"
            });

            $httpBackend.whenGET('/cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/pages\/secondpage').respond({
                "name": "Second Page",
                "uid": "secondpage"
            });

            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/somePrimaryPageUID/).respond({
                "name": "Some Primary Page",
                "uid": "somePrimaryPageUID"
            });

            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/fallbacks/).respond({
                uids: []
            });

            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/secondpage\/fallbacks/).respond({
                uids: ['somePrimaryPageUID']
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=homepage/).respond({
                pageRestrictionList: []
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=secondpage/).respond({
                pageRestrictionList: [{
                    pageId: 'secondpage',
                    restrictionId: 'restrictionA'
                }, {
                    pageId: 'secondpage',
                    restrictionId: 'restrictionB'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/restrictions/).respond({
                restrictions: [{
                    uid: 'restrictionA',
                    name: 'Restriction A',
                    description: 'Restriction A Description',
                    typeCode: 'CatalogRestriction'
                }, {
                    uid: 'restrictionB',
                    name: 'Restriction B',
                    description: 'Restriction B Description',
                    typeCode: 'CatalogRestriction'
                }, {
                    uid: 'restrictionC',
                    name: 'Restriction C',
                    description: 'Restriction C Description',
                    typeCode: 'TimeRestriction'
                }, {
                    uid: 'restrictionD',
                    name: 'Restriction D',
                    description: 'Restriction D Description',
                    typeCode: 'TimeRestriction'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/restrictiontypes/).respond({
                restrictionTypes: [{
                    code: "TimeRestriction",
                    name: {
                        "en": "Time Restriction",
                        "de": "....."
                    }
                }, {
                    code: "CatalogRestriction",
                    name: {
                        "en": "Catalog Restriction",
                        "de": "....."
                    }
                }]
            });
        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
