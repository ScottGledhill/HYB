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
angular.module('RestrictionsMocks', ['ngMockE2E'])
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {
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

        $httpBackend.whenGET(/pagesrestrictions/).respond({
            "pageRestrictionList": [{
                "pageId": "homepage",
                "restrictionId": "timeRestrictionIdA"
            }, {
                "pageId": "homepage",
                "restrictionId": "timeRestrictionIdB"
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

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
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
            'cms.toolbaritem.navigationmenu.name': 'Navigation',
            'icon.tooltip.visibility': '{{numberOfRestrictions}} restrictions on this page',
            'page.restrictions.item.remove': 'Remove'
        });

    });

angular.module('pageRestrictionsEditorModule').requires.push('RestrictionsMocks');
