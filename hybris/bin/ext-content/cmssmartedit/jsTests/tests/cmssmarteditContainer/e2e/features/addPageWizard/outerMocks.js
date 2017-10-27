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
        function($httpBackend, languageService, I18N_RESOURCE_URI) {
            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var map = [{
                "id": "2",
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "id": "8",
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "id": "9",
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/pageList/innerMocks.js\"}",
                "key": "applications.InnerMocks"
            }, {
                "id": "11",
                "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
                "key": "applications.cmssmarteditContainer"
            }];

            $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
                'se.cms.addpagewizard.addpage': 'add new page',
                'se.cms.addpagewizard.pagetype.title': 'Create Page',
                'se.cms.addpagewizard.pagetype.description': 'SmartEdit supports page creation for the following page types',
                'se.cms.addpagewizard.pagetype.tabname': 'Type',
                'se.cms.addpagewizard.pagetemplate.tabname': 'Template',
                'se.cms.addpagewizard.pageconditions.tabname': 'Display Condition',
                'se.cms.addpagewizard.pageinfo.tabname': 'Info',
                'page.condition.selection.label': 'Condition',
                'page.condition.primary.association.label': 'Primary page associated to the variation',
                'page.displaycondition.primary': 'Primary',
                'page.displaycondition.primary.description': 'This is a primary page and it will be displayed if no other variant page exists.',
                'page.displaycondition.variation': 'Variation',
                'page.displaycondition.variation.description': 'This is a variant page that supports restriction rules to control the page visibility.',
                'page.label.label': 'Label',
                'page.restrictions.list.empty': 'This page has no restrictions',
                'page.restrictions.list.title': 'List of restrictions',
                'page.restrictions.editor.button.add.new': 'ADD NEW',
                'page.restrictions.editor.tab': 'Restrictions',
                'page.restrictions.picker.type.label': 'Restriction Type',
                'page.restrictions.picker.type.placeholder': 'Select restriction type',
                'page.restrictions.picker.search.label': 'Restriction Name',
                'page.restrictions.picker.search.placeholder': 'Search',
                'page.restrictions.criteria': 'Criteria:',
                'page.restrictions.criteria.all': 'Match all',
                'page.restrictions.criteria.any': 'Match any',
                'page.restrictions.criteria.select.all': 'Apply all restrictions',
                'page.restrictions.criteria.select.any': 'Apply any restriction',
                'page.restrictions.toolbar.menu': 'Restrictions',
                'page.restrictions.item.remove': 'Remove',
                'confirmation.modal.title': 'Confirm',
                'confirmation.modal.ok': 'OK',
                'confirmation.modal.cancel': 'Cancel',
                'confirmation.modal.missing.description': 'Confirmation modal description is required',
                'editor.cancel.confirm': 'You have unsaved changes. Are you sure you want to cancel?'
            });

            $httpBackend.whenGET(/\/restrictiontypes/).respond({
                restrictionTypes: [{
                    code: 'CMSTimeRestriction',
                    name: {
                        de: 'DAS blabla',
                        en: 'Time Restriction'
                    }
                }, {
                    code: 'CMSCatalogRestriction',
                    name: {
                        en: 'Catalog Restriction'
                    }
                }, {
                    code: 'CMSCategoryRestriction',
                    name: {
                        en: 'category Restriction'
                    }
                }, {
                    code: 'CMSUserRestriction',
                    name: {
                        en: 'User Restriction'
                    }
                }]
            });

            $httpBackend.whenGET(/restrictions/).respond({
                restrictions: [{
                    uid: "timeRestrictionIdA",
                    name: "Some Time restriction A",
                    typeCode: "CMSTimeRestriction",
                    description: "some description"
                }, {
                    uid: "timeRestrictionIdB",
                    name: "another time B",
                    typeCode: "CMSTimeRestriction",
                    description: "some description"
                }, {
                    uid: "timeRestrictionIdC",
                    name: "yet another",
                    typeCode: "CMSTimeRestriction",
                    description: "some description"
                }, {
                    uid: "catalogRestrictionIdD",
                    name: "some cat restriction",
                    typeCode: "CMSCatalogRestriction",
                    description: "some description"
                }, {
                    uid: "catalogRestrictionIdE",
                    name: "I'm a skatman",
                    typeCode: "CMSCatalogRestriction",
                    description: "some description"
                }, {
                    uid: "catalogRestrictionIdF",
                    name: "Cat restriction E",
                    typeCode: "CMSCatalogRestriction",
                    description: "some description"
                }, {
                    uid: "catalogRestrictionIdG",
                    name: "catalogRestrictionNameG",
                    typeCode: "CMSCatalogRestriction",
                    description: "some description"
                }, {
                    uid: "catalogRestrictionIdH",
                    name: "Some User restriciton 1",
                    typeCode: "CMSCatalogRestriction",
                    description: "some description"
                }, {
                    uid: "userRestrictionIdI",
                    name: "User restriction 2",
                    typeCode: "CMSUserRestriction",
                    description: "some description"
                }]
            });

            $httpBackend.whenGET(/pagetypesrestrictiontypes/).respond({
                "pageTypeRestrictionTypeList": [{
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSCatalogRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "CatalogPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSCategoryRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "CategoryPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "ContentPage",
                    "restrictionType": "CMSUiExperienceRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSCategoryRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSProductRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSTimeRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUserRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUserGroupRestriction"
                }, {
                    "pageType": "ProductPage",
                    "restrictionType": "CMSUiExperienceRestriction"
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

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);

            $httpBackend.whenGET(/smarteditwebservices\/v1\/i18n\/languages/).respond({
                languages: [{
                    language: 'en',
                    isoCode: 'en',
                    required: true
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

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online$/).respond({
                "name": {
                    "en": "Apparel UK Content Catalog"
                },
                "pageDisplayConditions": [{
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "value": "VARIATION"
                    }],
                    "typecode": "ProductPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.primary",
                        "value": "PRIMARY"
                    }],
                    "typecode": "CategoryPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.primary",
                        "value": "PRIMARY"
                    }, {
                        "label": "page.displaycondition.variation",
                        "value": "VARIATION"
                    }],
                    "typecode": "ContentPage"
                }],
                "uid": "apparel-ukContentCatalog",
                "version": "Online"
            });

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
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages$/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "PageTemplate",
                    name: "page1TitleSuffix",
                    label: 'page1TitleSuffix',
                    typeCode: "ContentPage",
                    uid: "auid1"
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


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=ContentPage/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "PageTemplate",
                    name: "page1TitleSuffix",
                    label: 'page1TitleSuffix',
                    typeCode: "ContentPage",
                    uid: "auid1"
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=CategoryPage/).respond({
                pages: []
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=ProductPage/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968058",
                    template: "PageTemplate",
                    name: "productPage1",
                    label: 'productPage1',
                    typeCode: "CategoryPage",
                    uid: "auid2"
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pagetemplates*/).respond({
                "templates": [{
                    "frontEndName": "pageTemplate1",
                    "name": "Page Template 1",
                    "uid": "pageTemplate1"
                }, {
                    "frontEndName": "pageTemplate2",
                    "name": "Page Template 2",
                    "uid": "pageTemplate2"
                }]
            });

            $httpBackend.whenPOST(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages$/)
                .respond(function(method, url, data, headers) {
                    var dataObject = angular.fromJson(data);
                    if (dataObject.uid === 'bla') {
                        return [400, {
                            "errors": [{
                                "message": "Some error msg.",
                                "reason": "invalid",
                                "subject": "uid",
                                "subjectType": "parameter",
                                "type": "ValidationError"
                            }]
                        }];
                    } else {
                        return [200, {
                            uid: 'valid'
                        }];
                    }
                });

            $httpBackend.whenGET(/\/synchronizations\//).
            respond({
                creationDate: "2015-01-29T16:25:44",
                syncStatus: "FINISHED",
                endDate: '2016-01-29T16:25:28',
                lastModifiedDate: '2016-01-29T16:25:28',
                syncResult: 'SUCCESS',
                startDate: '2016-01-29T16:25:28'
            });

            $httpBackend
                .whenGET("/cmswebservices\/v1\/pagetypes")
                .respond({
                    pageTypes: [{
                        code: 'ContentPage',
                        name: {
                            "en": 'Content Page',
                            "fr": 'Content Page in French'
                        },
                        description: {
                            "en": 'Description for content page',
                            "fr": 'Description for content page in French'
                        }
                    }, {
                        code: 'ProductPage',
                        name: {
                            "en": 'Product Page',
                            "fr": 'Product Page in French'
                        },
                        description: {
                            "en": 'Description for product page',
                            "fr": 'Description for product page in French'
                        }
                    }, {
                        code: 'CategoryPage',
                        name: {
                            "en": 'Category Page',
                            "fr": 'Category Page in French'
                        },
                        description: {
                            "en": 'Description for category page',
                            "fr": 'Description for category page in French'
                        }
                    }]
                });

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
