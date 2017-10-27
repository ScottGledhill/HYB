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
xdescribe('newPageDisplayCondition', function() {

    var scope, element, mocks, $q;
    var displayConditionUiSelect;
    var primaryPageUiSelect;
    var pageService;
    var pageDisplayConditionsService;

    var pageDisplayConditionMocks = unit.mockData.pageDisplayCondition;
    var primaryMocks = unit.mockData.pages.PrimaryPageMocks;

    var template =
        '<new-page-display-condition ' +
        'page-type-code="pageTypeCode" ' +
        'uri-context="uriContext" ' +
        'result-fn="resultFn"> ' +
        '</new-page-display-condition>';

    var MOCK_RESTRICITON_1 = {
        uid: 'SampleUid',
        name: 'Sample Name',
        typeCode: 'SampleRestriction',
        typeName: {
            en: 'Sample Restriction'
        },
        description: 'Sample Description'
    };

    var MOCK_RESTRICTION_2 = {
        uid: 'SampleUid2',
        name: 'Sample Name 2',
        typeCode: 'SampleRestriction2',
        typeName: {
            en: 'Sample Restriction 2'
        },
        description: 'Sample Description 2'
    };

    var elements = {
        getConditionUiSelect: function() {
            return element.find('#page-condition-selector-id');
        },
        getPrimaryPageUiSelector: function() {
            return element.find('#page-condition-primary-selector-id');
        }
    };

    function prepareComponent(mockDisplayConditions, mockPrimaryPages, resultFn) {
        if (!mockPrimaryPages) {
            mockPrimaryPages = primaryMocks.getMocksForType('ContentPage');
        }
        if (!mockDisplayConditions) {
            mockDisplayConditions = new pageDisplayConditionMocks().ALL;
        }
        if (!resultFn) {
            resultFn = function() {};
        }
        var harness = AngularUnitTestHelper.prepareModule('newPageDisplayConditionModule')
            .mock('pageService', 'getPrimaryPagesForPageType').andReturnResolvedPromise(mockPrimaryPages)
            .mock('pageDisplayConditionsService', 'getNewPageConditions').andReturn(mockDisplayConditions)
            .withTranslations({
                'page.displaycondition.primary': 'PRIMARY',
                'page.displaycondition.variation': 'VARIATION'
            })
            .component(template, {
                pageTypeCode: 'ContentPage',
                uriContext: {},
                resultFn: resultFn
            });
        element = harness.element;
        scope = harness.scope;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        displayConditionUiSelect = new UiSelectPageObject(elements.getConditionUiSelect());
    }

    function setupPrimaryPageUiSelect() {
        primaryPageUiSelect = new UiSelectPageObject(elements.getPrimaryPageUiSelector());
    }

    describe('general -', function() {

        beforeEach(function() {
            prepareComponent();
        });

        it('if primary condition is selected, primary page selector should not be visible', function() {
            expect(elements.getPrimaryPageUiSelector().length).toBe(0);
        });

        it('if variation condition is selected, primary page selector should be visible', function() {
            displayConditionUiSelect.clickSelectToggle();
            displayConditionUiSelect.clickSelectElement(1);
            expect(elements.getPrimaryPageUiSelector().length).toBe(1);
        });

        it('if variation is selected, all primary options should be in the ui select', function() {
            var mockPages = primaryMocks.getMocksForType('ContentPage');

            displayConditionUiSelect.clickSelectToggle();
            displayConditionUiSelect.clickSelectElement(1);

            setupPrimaryPageUiSelect();
            primaryPageUiSelect.clickSelectToggle();

            primaryPageUiSelect.assertNumberElements(mockPages.length);
            for (var i = 0; i < mockPages.length; i++) {
                primaryPageUiSelect.assertElementTextEquals(i, mockPages[i].name);
            }
        });

    });

    describe('component output -', function() {

        var resultFn = jasmine.createSpy('resultFn');

        beforeEach(function() {
            prepareComponent(null, null, resultFn);
        });

        it('should fire the result function with only primary flag for primary', function() {
            displayConditionUiSelect.clickSelectToggle();
            displayConditionUiSelect.clickSelectElement(0);
            expect(resultFn.mostRecentCall.args[0]).toEqualData({
                isPrimary: new pageDisplayConditionMocks().ALL[0].isPrimary
            });
        });

        it('should fire the result function with primary flag and primary page for variation pages', function() {

            displayConditionUiSelect.clickSelectToggle();
            displayConditionUiSelect.clickSelectElement(1);
            expect(resultFn.mostRecentCall.args[0]).toEqualData({
                isPrimary: new pageDisplayConditionMocks().ALL[1].isPrimary,
                primaryPage: primaryMocks.getMocksForType('ContentPage')[0]
            });
        });
    });

    describe('component inputs -', function() {

        describe('all conditions -', function() {
            beforeEach(function() {
                prepareComponent();
            });

            it('should display both variation and primary options in the ui select', function() {
                displayConditionUiSelect.clickSelectToggle();
                displayConditionUiSelect.assertElementInList('PRIMARY');
                displayConditionUiSelect.assertElementInList('VARIATION');
            });
        });

        describe('just variation -', function() {
            beforeEach(function() {
                prepareComponent([new pageDisplayConditionMocks().PRIMARY]);
            });

            it('should display only variation option in the ui select', function() {
                displayConditionUiSelect.clickSelectToggle();
                displayConditionUiSelect.assertElementInList('PRIMARY');
                displayConditionUiSelect.assertElementNotInList('VARIATION');
            });
        });

        describe('just variation -', function() {
            beforeEach(function() {
                prepareComponent([new pageDisplayConditionMocks().VARIANT]);
            });

            it('should display only variation option in the ui select', function() {
                displayConditionUiSelect.clickSelectToggle();
                displayConditionUiSelect.assertElementInList('VARIATION');
                displayConditionUiSelect.assertElementNotInList('PRIMARY');
            });
        });

    });

});
