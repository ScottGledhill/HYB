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
describe('pageListController for a non active catalogVersion', function() {

    var $rootScope, $q;
    var controller, mocks;
    var uriContext = "uriContext";

    var MOCK_UPDATED_NUMBER_OF_RESTRICTIONS = 7;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageListControllerModule')
            .mock('pageListService', 'getPageListForCatalog').andReturnResolvedPromise([{
                uid: '12345',
                label: 'original label',
                name: 'name',
                typeCode: 'ContentPage',
                template: 'ContentPageTemplate',
                modifiedtime: null,
                creationtime: null
            }])
            .mock('pageListService', 'getPageById').andReturnResolvedPromise({
                uid: '12345',
                label: 'updated label'
            })
            .mock('urlService', 'buildUriContext').andReturnResolvedPromise(uriContext)
            .mock('catalogService', 'getCatalogsForSite').andReturnResolvedPromise([])
            .mock('catalogService', 'isContentCatalogVersionNonActive').andReturnResolvedPromise(true)
            .mock('experienceService', 'buildDefaultExperience').andReturnResolvedPromise('')
            .mock('syncPageModalService', 'open').andReturnResolvedPromise('')
            .mock('pageEditorModalService', 'open').andReturnResolvedPromise('')
            .mock('sharedDataService', 'set').andReturnResolvedPromise('')
            .mock('pageRestrictionsService', 'getPageRestrictionsCountMapForCatalogVersion').andReturnResolvedPromise({
                12345: 1
            })
            .mock('pageRestrictionsService', 'getPageRestrictionsCountForPageUID').andReturnResolvedPromise(MOCK_UPDATED_NUMBER_OF_RESTRICTIONS)
            .mock('systemEventService', 'sendAsynchEvent')
            .mock('addPageWizardService', 'openAddPageWizard')
            .mock('syncPollingService', 'initSyncPolling')
            .controller('pageListController');

        controller = harness.controller;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

    });

    describe('init', function() {
        it('should initialize a list of dropdown items', function() {
            expect(controller.dropdownItems).toEqual([{
                key: 'pagelist.dropdown.edit',
                callback: jasmine.any(Function)
            }, {
                key: 'pagelist.dropdown.sync',
                callback: jasmine.any(Function)
            }]);
        });


        it('should initialize a list of dropdown items', function() {

            expect(controller.keys).toEqual([{
                property: 'name',
                i18n: 'pagelist.headerpagename'
            }, {
                property: 'uid',
                i18n: 'pagelist.headerpageid'
            }, {
                property: 'typeCode',
                i18n: 'pagelist.headerpagetype'
            }, {
                property: 'template',
                i18n: 'pagelist.headerpagetemplate'
            }, {
                property: 'numberOfRestrictions',
                i18n: 'pagelist.headerrestrictions'
            }, {
                property: 'syncStatus',
                i18n: 'pagelist.dropdown.sync'
            }]);
        });

        it('should open page editor the edit page dropdown item is called', function() {
            var pageData = {
                uid: '1223'
            };
            controller.dropdownItems[0].callback(pageData);
            expect(mocks.pageEditorModalService.open).toHaveBeenCalledWith(pageData);
        });

        it('should display the expected number of pages as fetched from the backend', function() {
            expect(controller.pages.length).toBe(1);
        });

        it('should provide the page name for each page object in the list of pages', function() {
            expect(controller.pages[0].name).toBe('name');
        });

        it('should provide the page uid for each page object in the list of pages', function() {
            expect(controller.pages[0].uid).toBe('12345');
        });

        it('should provide the page type code for each page object in the list of pages', function() {
            expect(controller.pages[0].typeCode).toBe('ContentPage');
        });

        it('should provide the page template for each page object in the list of pages', function() {
            expect(controller.pages[0].template).toBe('ContentPageTemplate');
        });

        it('should provide the number of restrictions for each page object in the list of pages', function() {
            expect(controller.pages[0].numberOfRestrictions).toBe(1);
        });

    });

    describe('reloadUpdatedPage', function() {
        it('should refresh the page in the list for the corresponding UID on reloadUpdatedPage', function() {
            controller.reloadUpdatedPage('12345', '12345');
            $rootScope.$digest();
            expect(controller.pages[0].label).toBe('updated label');

        });

        it('should refresh the page in the list for an updated page UID', function() {
            mocks.pageListService.getPageById.andReturn($q.when({
                'uid': '11111',
                'label': 'updated label'
            }));

            controller.reloadUpdatedPage('12345', '11111');
            $rootScope.$digest();
            expect(controller.pages[0].uid).toBe('11111');
        });

        it('should update the number of restrictions for the corresponding UID', function() {
            controller.reloadUpdatedPage('12345', '12345');
            $rootScope.$digest();
            expect(controller.pages[0].numberOfRestrictions).toBe(MOCK_UPDATED_NUMBER_OF_RESTRICTIONS);
        });
    });
});
