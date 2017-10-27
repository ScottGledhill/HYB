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
describe('pageRestrictionsService', function() {

    var pageRestrictionsService, mocks;

    var MOCK_RESTRICTIONS = unit.mockData.restrictions;
    var MOCK_PAGES_RESTRICTIONS = unit.mockData.pagesRestrictions;
    var MOCK_TIME_RESTRICTIONS_TYPE = {
        code: 'CMSTimeRestriction',
        name: {
            de: 'DAS blabla',
            en: 'Time Restriction'
        }
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageRestrictionsServiceModule')
            .mock('restrictionTypesService', 'getRestrictionTypeForTypeCode')
            .mock('pageRestrictionsRestService', 'getPagesRestrictionsForPageId')
            .mock('pageRestrictionsRestService', 'getPagesRestrictionsForCatalogVersion')
            .mock('pageRestrictionsRestService', 'update')
            .mock('typeStructureRestService', 'getStructuresByCategory')
            .mock('restrictionsService', 'getAllRestrictions')
            .service('pageRestrictionsService');

        pageRestrictionsService = harness.service;
        mocks = harness.mocks;
    });

    beforeEach(function() {
        mocks.restrictionTypesService.getRestrictionTypeForTypeCode.andReturnResolvedPromise(MOCK_TIME_RESTRICTIONS_TYPE);
        mocks.pageRestrictionsRestService.getPagesRestrictionsForPageId.andReturnResolvedPromise(MOCK_PAGES_RESTRICTIONS);
        mocks.pageRestrictionsRestService.getPagesRestrictionsForCatalogVersion.andReturnResolvedPromise(MOCK_PAGES_RESTRICTIONS);
        mocks.pageRestrictionsRestService.update.andReturnResolvedPromise(true);
        mocks.restrictionsService.getAllRestrictions.andReturnResolvedPromise(MOCK_RESTRICTIONS);
    });

    describe('getRestrictionsByPageUID', function() {
        it('should get restrictions by page id', function() {
            expect(pageRestrictionsService.getRestrictionsByPageUID('someId')).toBeResolvedWithData([{
                uid: "timeRestrictionIdA",
                name: "Some Time restriction A",
                typeCode: "CMSTimeRestriction",
                description: "some description",
                typeName: MOCK_TIME_RESTRICTIONS_TYPE.name
            }, {
                uid: "timeRestrictionIdB",
                name: "another time B",
                typeCode: "CMSTimeRestriction",
                description: "some description",
                typeName: MOCK_TIME_RESTRICTIONS_TYPE.name
            }]);
        });
    });

    describe('updateRestrictionsByPageUID', function() {
        it('should update restrictions by page id', function() {
            pageRestrictionsService.updateRestrictionsByPageUID('homepage', [{
                description: "",
                name: "Anonymous User Restriction",
                typeCode: "CMSUserRestriction",
                uid: "anonymousUserRestriction"
            }, {
                description: "Page only applies on experience level: Mobile",
                name: "Restriction for Mobile Experience",
                typeCode: "CMSUiExperienceRestriction",
                uid: "MobileExperienceRestriction"
            }]);

            expect(mocks.pageRestrictionsRestService.update).toHaveBeenCalledWith({
                pageRestrictionList: [{
                    pageId: 'homepage',
                    restrictionId: 'anonymousUserRestriction'
                }, {
                    pageId: 'homepage',
                    restrictionId: 'MobileExperienceRestriction'
                }],
                pageid: 'homepage'
            });
        });
    });

    describe('getPageRestrictionsCountMapForCatalogVersion', function() {
        it('should return a map of page to number of restrictions', function() {
            expect(pageRestrictionsService.getPageRestrictionsCountMapForCatalogVersion()).toBeResolvedWithData({
                homepage: 2
            });
        });
    });

    describe('getPageRestrictionsCountForPageUID', function() {
        it('should return the page to number of restrictions for a given page UID', function() {
            expect(pageRestrictionsService.getPageRestrictionsCountForPageUID()).toBeResolvedWithData(2);
        });
    });

});
