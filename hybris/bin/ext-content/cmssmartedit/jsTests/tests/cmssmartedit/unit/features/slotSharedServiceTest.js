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
describe('slotSharedService', function() {

    var $q, $rootScope, pagesContentSlotsResource, slotSharedService, restServiceFactory;
    var pagescontentslots = {
        pageContentSlotList: [{
            pageId: "homepage",
            slotId: "topHeaderSlot",
            position: 0,
            slotShared: true
        }, {
            pageId: "homepage",
            slotId: "bottomHeaderSlot",
            position: 1,
            slotShared: false
        }, {
            pageId: "homepage",
            slotId: "footerSlot",
            position: 2,
            slotShared: false
        }, {
            pageId: "homepage",
            slotId: "otherSlot",
            position: 3,
            slotShared: true
        }]
    };

    beforeEach(customMatchers);

    beforeEach(function() {
        angular.module('resourceModule', []);
        angular.module('componentHandlerServiceModule', []);
    });

    beforeEach(module('slotSharedServiceModule', function($provide) {

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getPageUID']);
        componentHandlerService.getPageUID.andReturn('homepage');
        $provide.value('componentHandlerService', componentHandlerService);

        $provide.service('restServiceFactory', function($q) {
            pagesContentSlotsResource = jasmine.createSpyObj('pagesContentSlotsResource', ['get']);
            pagesContentSlotsResource.get.andCallFake(function(queryParams) {
                return $q.when(pagescontentslots);
            });
            this.get = jasmine.createSpy('get');
            this.get.andReturn(pagesContentSlotsResource);
        });
    }));

    beforeEach(inject(function(_slotSharedService_, _$rootScope_, _$q_) {
        $q = _$q_;
        slotSharedService = _slotSharedService_;
        $rootScope = _$rootScope_;
    }));

    describe('reloadSharedSlotMap ', function() {
        it('should resolve with true when slots are shared else with false', function() {
            $rootScope.$digest();
            var resolvedPromise = slotSharedService.reloadSharedSlotMap();
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData({
                topHeaderSlot: true,
                bottomHeaderSlot: false,
                footerSlot: false,
                otherSlot: true
            });
        });

    });

    describe('isSlotShared ', function() {
        it('should return a promise which resolves to true when the backend response indicates the slot is shared', function() {
            var resolvedPromise = slotSharedService.isSlotShared("topHeaderSlot");
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData(true);
        });

        it('should return a promise which resolves to false when the backend response indicates the slot is not shared', function() {
            var resolvedPromise = slotSharedService.isSlotShared("footerSlot");
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData(false);
        });
    });

});
