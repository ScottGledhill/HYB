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
describe('slotRestrictionsService', function() {

    var fixture;
    var slotRestrictionsService;
    var $q;
    var mockComponentHandlerService;
    var mockRestServiceFactory;
    var mockSlotRestrictionsRestService;

    var MOCK_SLOT_RESTRICTIONS;
    var MOCK_PAGE_UID;

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('slotRestrictionsServiceModule')
            .mockConstant('CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI', 'CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI')
            .mock('componentHandlerService', 'getPageUID')
            .mock('restServiceFactory', 'get')
            .service('slotRestrictionsService');

        slotRestrictionsService = fixture.service;
        $q = fixture.injected.$q;
        mockRestServiceFactory = fixture.mocks.restServiceFactory;
        mockComponentHandlerService = fixture.mocks.componentHandlerService;
    });

    beforeEach(function() {
        mockSlotRestrictionsRestService = jasmine.createSpyObj('mockSlotRestrictionsRestService', ['get']);
        mockRestServiceFactory.get.andReturn(mockSlotRestrictionsRestService);
    });

    beforeEach(function() {
        MOCK_PAGE_UID = 'SomePageUID';
        MOCK_SLOT_RESTRICTIONS = {
            validComponentTypes: [
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]
        };
    });

    describe('getSlotRestrictions', function() {
        it('should cache the page ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID.calls.length)
                .toBe(1, 'Expected componentHandlerService.getPageUID() to have been called only once');
        });

        it('should delegate to the slot restrictions REST service to fetch the components allowed in a given slot and page', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get).toHaveBeenCalledWith({
                slotUid: 'SomeSlotUID',
                pageUid: 'SomePageUID'
            });
        });

        it('should cache type restrictions by slot ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get.calls.length)
                .toBe(1, 'Expected slot restrictions REST service GET to have been called only once');
        });

        it('should return a promise that resolves to a list of valid component types', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            var slotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(slotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
        });

        it('should return the cached list of valid component types on subsequent calls with the same slot ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            var firstCallSlotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            var secondCallSlotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(firstCallSlotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
            expect(secondCallSlotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
        });
    });

    describe('isComponentAllowedInSlot', function() {
        it('should return a promise resolving to true if the component type is allowed in the given slot AND source and target slots are the same AND the target slot already contains the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'something'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(true);
        });

        it('should return a promise resolving to true if the component type is allowed in the given slot AND the slot does not already contain the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'SomeComponentId1'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(true);
        });

        it('should return a promise resolving to false if the component type is allowed in the given slot AND source and target slots are different AND the slot already contains the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'SomeComponentId1'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'SomeComponentId1'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(false);
        });

        it('should return a promise resolving to false if the component type is not allowed in the given slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType4',
                componentId: 'SomeComponentId4'
            };

            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(false);
        });
    });

    describe('emptyCache', function() {
        it('should invalidate the cache such that the next call to getSlotRestrictions will fetch the current page ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.emptyCache();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID.calls.length)
                .toBe(2, 'Expected componentHandlerService.getPageUID() to have been called both before and after cache invalidation');
        });

        it('should invalidate the cache such that the next call to getSlotRestrictions for a slot ID that was once cached should fetch the type restrictions for this slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockSlotRestrictionsRestService.get.andReturn($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.emptyCache();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get.calls.length)
                .toBe(2, 'Expected slot restrictions REST service GET to have been called both before and after cache invalidation');
        });
    });

});
