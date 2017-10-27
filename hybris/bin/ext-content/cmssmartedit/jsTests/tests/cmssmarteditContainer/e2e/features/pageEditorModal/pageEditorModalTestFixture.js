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
angular.module('pageEditorTestFixtureModule', ['ngMockE2E', 'pageEditorModalServiceModule', 'sharedDataServiceModule', 'renderServiceModule', 'genericEditorModule', 'eventServiceModule', 'resourceLocationsModule'])
    .run(function($httpBackend, sharedDataService, restServiceFactory) {
        var PAGE_BY_UID = {
            primaryContentPage: {
                type: 'contentPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Content Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Content Page'
                },
                typeCode: 'ContentPage',
                uid: 'primaryContentPage',
                label: 'primary-content-page'
            },
            variationContentPage: {
                type: 'contentPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Content Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Content Page'
                },
                typeCode: 'ContentPage',
                uid: 'variationContentPage',
                label: 'variation-content-page'
            },
            primaryCategoryPage: {
                type: 'categoryPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Category Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Category Page'
                },
                typeCode: 'CategoryPage',
                uid: 'primaryCategoryPage',
                label: 'primary-category-page'
            },
            variationCategoryPage: {
                type: 'categoryPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Category Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Category Page'
                },
                typeCode: 'CategoryPage',
                uid: 'variationCategoryPage',
                label: 'variation-category-page'
            },
            primaryProductPage: {
                type: 'productPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Product Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Product Page'
                },
                typeCode: 'ProductPage',
                uid: 'primaryProductPage',
                label: 'primary-product-page'
            },
            variationProductPage: {
                type: 'productPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Product Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Product Page'
                },
                typeCode: 'ProductPage',
                uid: 'variationProductPage',
                label: 'variation-product-page'
            }
        };

        function prepareCommonMocks() {
            restServiceFactory.setDomain('thedomain');

            sharedDataService.set('experience', {
                siteDescriptor: {
                    uid: 'someSiteUid'
                },
                catalogDescriptor: {
                    catalogId: 'electronics',
                    catalogVersion: 'staged'
                }
            });

            $httpBackend
                .whenGET(/\/smarteditwebservices\/v1\/i18n\/translations\//)
                .respond({
                    'pageeditormodal.editpagetab.title': 'Manage Page',
                    'pageeditormodal.editpagetab.basic': 'INFORMATION',
                    'pageeditormodal.editpagetab.basic.template': 'Search results grid',
                    'editortabset.visibilitytab.title': 'Visibility',
                    'editor.title.suffix': 'Editor',
                    'compoment.confirmation.modal.cancel': 'Cancel',
                    'component.confirmation.modal.save': 'Save',
                    'se.cms.pageinfoform.name': 'Page Name',
                    'se.cms.pageinfoform.title': 'Page Title',
                    'se.cms.pageinfoform.id': 'Page ID',
                    'se.cms.pageinfoform.label': 'Page Label',
                    'se.cms.pageinfoform.fromemail': 'From E-mail',
                    'se.cms.pageinfoform.fromname': 'From Name',
                    'se.cms.pageinfoform.datecreated': 'Date Created',
                    'se.cms.pageinfoform.datemodified': 'Date Modified',
                    'page.restrictions.editor.tab': 'Restrictions',
                    'se.pageinfo.information.title': 'Information',
                    'se.pageinfo.display.conditions.title': 'Display Condition',
                    'se.cms.display.conditions.label': 'Display Condition',
                    'se.cms.display.conditions.primary.value': 'Primary',
                    'se.cms.display.conditions.primary.description': 'This is a primary page and it will be displayed if no other variation page exists',
                    'se.cms.display.conditions.variation.value': 'Variation',
                    'se.cms.display.conditions.variation.description': 'This is a variation page that supports restriction rules to control the page visibility',
                    'se.cms.display.conditions.header.page.name': 'Page Name',
                    'se.cms.display.conditions.header.creation.date': 'Creation Date',
                    'se.cms.display.conditions.header.restrictions': 'Restrictions',
                    'se.cms.display.conditions.variation.pages.title': 'Page Variations Associated to this Primary',
                    'se.cms.display.conditions.variations.description': 'The variation page with the most applicable restrictions is displayed. If more than one variation page applies, the oldest variation page is displayed',
                    'se.cms.display.conditions.no.variations': 'There are no page variations for this primary.',
                    'se.cms.display.conditions.primary.page.label': 'Primary page associated to the variation'
                });

            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/languages/)
                .respond({
                    languages: [{
                        nativeName: 'English',
                        isocode: 'en',
                        name: 'English',
                        required: true
                    }]
                });



            $httpBackend.whenGET(/cmswebservices\/v1\/types\/CategoryPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\/ContentPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.contentpage.label.name",
                    "localized": false,
                    "qualifier": "label"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\/ProductPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });



        }

        function prepareBackendMocksForPageInfo() {
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/([a-zA-Z0-9_]*)$/, undefined, ['uid'])
                .respond(function(method, url, data, headers, params) {
                    return [200, PAGE_BY_UID[params.uid]];
                });

            $httpBackend
                .whenPUT(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/([a-zA-Z0-9_]*)$/, undefined, undefined, ['uid'])
                .respond(function(method, url, data, headers, params) {
                    if (params.uid === 'variationContentPage') {
                        return [400, {
                            "errors": [{
                                "message": "This field is required.",
                                "reason": "invalid",
                                "subject": "name",
                                "subjectType": "parameter",
                                "type": "ValidationError"
                            }]
                        }];
                    } else {
                        return [200, PAGE_BY_UID[params.uid]];
                    }
                });
        }

        function prepareBackendMocksForPrimaryContentPage() {
            // Content primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryContentPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Content primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryContentPage\/variations$/)
                .respond({
                    uids: ['someVariationContentPageUid', 'someOtherVariationContentPageUid']
                });

            // Each variation has a name and creation time
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\?uids=someVariationContentPageUid&uids=someOtherVariationContentPageUid/)
                .respond({
                    pages: [{
                        uid: 'someVariationContentPageUid',
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Variation Content Page"
                    }, {
                        uid: 'someOtherVariationContentPageUid',
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Other Variation Content Page"
                    }]
                });

            // Each variation has a number of restrictions
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pagesrestrictions\?pageId=someVariationContentPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid1"
                    }, {
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid2"
                    }]
                });

            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pagesrestrictions\?pageId=someOtherVariationContentPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid3"
                    }]
                });
        }

        function prepareBackendMocksForVariationContentPage() {
            // Content variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/variationContentPage\/fallbacks/)
                .respond({
                    uids: ['primaryContentPage']
                });

            // Content variation pages can be re-associated to a different primary
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\?defaultPage=true&typeCode=ContentPage/)
                .respond({
                    pages: [{
                        uid: 'primaryContentPage',
                        name: 'My Little Primary Content Page'
                    }, {
                        uid: 'someOtherPrimaryPageContent',
                        name: 'Some Other Primary Content Page'
                    }, {
                        uid: 'anotherPrimaryPageContent',
                        name: 'Another Primary Content Page'
                    }]
                });
        }

        function prepareBackendMocksForPrimaryCategoryPage() {
            // Category primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryCategoryPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Category primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryCategoryPage\/variations$/)
                .respond({
                    uids: []
                });
        }

        function prepareBackendMocksForVariationCategoryPage() {
            // Category variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/variationCategoryPage\/fallbacks/)
                .respond({
                    uids: ['primaryCategoryPage']
                });
        }

        function prepareBackendMocksForPrimaryProductPage() {
            // Product primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryProductPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Product primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/primaryProductPage\/variations$/)
                .respond({
                    uids: ['someVariationProductPageUid', 'someOtherVariationProductPageUid']
                });

            // Each variation has a name and creation time
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\?uids=someVariationProductPageUid&uids=someOtherVariationProductPageUid/)
                .respond({
                    pages: [{
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Variation Product Page",
                        uid: 'someVariationProductPageUid'
                    }, {
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Other Variation Product Page",
                        uid: 'someOtherVariationProductPageUid'
                    }]
                });

            // Each variation has a number of restrictions
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pagesrestrictions\?pageId=someVariationProductPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid1"
                    }, {
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid2"
                    }]
                });

            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pagesrestrictions\?pageId=someOtherVariationProductPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid3"
                    }]
                });
        }

        function prepareBackendMocksForVariationProductPage() {
            // Product variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/someSiteUid\/catalogs\/electronics\/versions\/staged\/pages\/variationProductPage\/fallbacks/)
                .respond({
                    uids: ['primaryProductPage']
                });
        }

        prepareCommonMocks();
        prepareBackendMocksForPageInfo();
        prepareBackendMocksForPrimaryContentPage();
        prepareBackendMocksForVariationContentPage();
        prepareBackendMocksForPrimaryCategoryPage();
        prepareBackendMocksForVariationCategoryPage();
        prepareBackendMocksForPrimaryProductPage();
        prepareBackendMocksForVariationProductPage();
    })
    .controller('pageEditorTestFixtureController', function(LANGUAGE_RESOURCE_URI, sharedDataService, restServiceFactory, $httpBackend, pageEditorModalService, languageService, CONTEXTUAL_PAGES_RESOURCE_URI, I18N_RESOURCE_URI) {
        this.openModal = function(uid) {

            var page = {
                uid: uid,
                typeCode: 'ContentPage',
                template: 'gridAccountPageTemplate',
                name: "Brands Category Page",
                numberOfRestrictions: 1,
                onlyOneRestrictionMustApply: true,
                uriContext: {
                    CURRENT_CONTEXT_CATALOG: "electronics",
                    CURRENT_CONTEXT_CATALOG_VERSION: "staged",
                    CURRENT_CONTEXT_SITE_ID: "someSiteUid"
                }
            };

            pageEditorModalService.open(page);
        };
    });
