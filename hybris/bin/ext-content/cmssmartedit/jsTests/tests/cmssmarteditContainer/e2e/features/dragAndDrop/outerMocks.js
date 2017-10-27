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
    .constant('testAssets', true)
    .service('dragAndDropMocksService', function($httpBackend) {

        this.mockPagesContentSlotsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?pageId=homepage/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'topHeaderSlot'
                }, {
                    pageId: 'homepage',
                    position: 'bottomHeader',
                    slotId: 'bottomHeaderSlot'
                }, {
                    pageId: 'homepage',
                    position: 'footer',
                    slotId: 'footerSlot'
                }, {
                    pageId: 'homepage',
                    position: 'other',
                    slotId: 'otherSlot'
                }]
            });
        };

        this.mockPagesContentSlotsComponentsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents\?pageId=homepage/).respond({
                'pageContentSlotComponentList': [{
                    'componentId': 'component1',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component2',
                    'pageId': 'homepage',
                    'position': 1,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component3',
                    'pageId': 'homepage',
                    'position': 2,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component4',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'bottomHeaderSlot'
                }, {
                    'componentId': 'component5',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'footerSlot'
                }]
            });
        };

        this.mockTopHeaderSlotTypeRestrictionsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/topHeaderSlot\/typerestrictions/).respond({
                contentSlotName: 'topHeaderSlot',
                validComponentTypes: [
                    'componentType1',
                    'componentType2',
                    'componentType3',
                    'CMSParagraphComponent'
                ]
            });
        };

        this.mockBottomHeaderSlotTypeRestricionsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/bottomHeaderSlot\/typerestrictions/).respond({
                contentSlotName: 'bottomHeaderSlot',
                validComponentTypes: [
                    'componentType4',
                    'CMSParagraphComponent',
                    'SimpleBannerComponent'
                ]
            });
        };

        this.mockFooterSlotTypeRestrictionsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/footerSlot\/typerestrictions/).respond({
                contentSlotName: 'footerSlot',
                validComponentTypes: [
                    'componentType0',
                    'componentType2',
                    'componentType3',
                    'componentType4',
                    'componentType5',
                    'SimpleResponsiveBannerComponent',
                    'CMSParagraphComponent'
                ]
            });
        };

        this.mockOtherSlotTypeRestrictionsResource = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/otherSlot\/typerestrictions/).respond({
                contentSlotName: 'otherSlot',
                validComponentTypes: [
                    'componentType0',
                    'componentType2',
                    'componentType3',
                    'componentType4',
                    'componentType5',
                    'SimpleResponsiveBannerComponent'
                ]
            });
        };



    })
    .run(
        function($httpBackend, languageService, dragAndDropMocksService) {
            dragAndDropMocksService.mockPagesContentSlotsResource();
            dragAndDropMocksService.mockPagesContentSlotsComponentsResource();
            dragAndDropMocksService.mockTopHeaderSlotTypeRestrictionsResource();
            dragAndDropMocksService.mockBottomHeaderSlotTypeRestricionsResource();
            dragAndDropMocksService.mockFooterSlotTypeRestrictionsResource();
            dragAndDropMocksService.mockOtherSlotTypeRestrictionsResource();


            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var configurations = [{
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
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/dragAndDrop/innerMocks.js\"}",
                "key": "applications.innerMocksModule"
            }];


            $httpBackend.whenGET(/configuration/).respond(configurations);
            $httpBackend.whenPUT(/configuration/).respond(404);


            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond({
                    ticketId: 'dasdfasdfasdfa',
                    resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefrontAlternatelayout.html'
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

            $httpBackend.whenGET(/cmswebservices\/v1\/types/).respond({
                "componentTypes": [{
                    "attributes": [{
                        "cmsStructureType": "RichText",
                        "i18nKey": "type.cmsparagraphcomponent.content.name",
                        "localized": true,
                        "qualifier": "content"
                    }],
                    "category": "COMPONENT",
                    "code": "CMSParagraphComponent",
                    "i18nKey": "type.cmsparagraphcomponent.name",
                    "name": "Paragraph"
                }, {
                    "attributes": [{
                        "cmsStructureType": "NavigationNodeSelector",
                        "i18nKey": "type.footernavigationcomponent.navigationnode.name",
                        "localized": false,
                        "qualifier": "navigationNode"
                    }],
                    "category": "COMPONENT",
                    "code": "FooterNavigationComponent",
                    "i18nKey": "type.footernavigationcomponent.name",
                    "name": "Footer Navigation Component"
                }, {
                    "attributes": [{
                        "cmsStructureType": "Media",
                        "i18nKey": "type.simplebannercomponent.media.name",
                        "localized": true,
                        "qualifier": "media"
                    }, {
                        "cmsStructureType": "ShortString",
                        "i18nKey": "type.simplebannercomponent.urllink.name",
                        "localized": false,
                        "qualifier": "urlLink"
                    }, {
                        "cmsStructureType": "Boolean",
                        "i18nKey": "type.simplebannercomponent.external.name",
                        "localized": false,
                        "qualifier": "external"
                    }],
                    "category": "COMPONENT",
                    "code": "SimpleBannerComponent",
                    "i18nKey": "type.simplebannercomponent.name",
                    "name": "Simple Banner Component"
                }]
            });

            // mock for customize components
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/items\?currentPage=0&mask=&pageSize=10&sort=name/).respond({
                'componentItems': [{
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 1',
                    'pk': '1',
                    'typeCode': 'CMSParagraphComponent',
                    'uid': 'component1',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 2',
                    'pk': '2',
                    'typeCode': 'componentType2',
                    'uid': 'component2',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 3',
                    'pk': '3',
                    'typeCode': 'componentType3',
                    'uid': 'component3',
                    "visible": true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 4',
                    'pk': '4',
                    'typeCode': 'componentType4',
                    'uid': 'component4',
                    "visible": true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 5',
                    'pk': '5',
                    'typeCode': 'componentType5',
                    'uid': 'component5',
                    "visible": true
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

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
