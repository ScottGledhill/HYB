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
describe('componentEditingFacadeModule', function() {

    var fixture;
    var componentEditingFacade;
    var mockRestServiceFactory;
    var mockContentSlotComponentsRestService;
    var mockComponentHandlerService;
    var mockComponentService;
    var mockEditorModalService;
    var mockRenderService;
    var mockRemoveComponentService;
    var mockAlertService;
    var $q;

    var MOCK_PAGE_UID;
    var MOCK_NEW_COMPONENT;
    var MOCK_ERROR;
    var MOCK_COMPONENT_ITEM_VISIBLE, MOCK_COMPONENT_ITEM_HIDDEN;

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('componentEditingFacadeModule')
            .mock('ComponentService', 'addNewComponent')
            .mock('ComponentService', 'removeComponent')
            .mock('ComponentService', 'loadComponentItem')
            .mock('ComponentService', 'addExistingComponent')
            .mock('componentHandlerService', 'getPageUID')
            .mock('restServiceFactory', 'get')
            .mock('editorModalService', 'open')
            .mock('removeComponentService', 'removeComponent')
            .mock('renderService', 'renderSlots')
            .mock('alertService', 'pushAlerts')
            .mockConstant('PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI', 'PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI')
            .withTranslations({
                'cmsdraganddrop.error': 'Failed to move {{sourceComponentId}} to slot {{targetSlotId}}: {{detailedError}}',
                'cmsdraganddrop.move.failed': 'Failed to move {{componentID}} to slot {{slotID}}',
                'cmsdraganddrop.success': 'The component {{sourceComponentId}} has been successfully added to slot {{targetSlotId}}',
                'cmsdraganddrop.success.but.hidden': 'The component {{sourceComponentId}} has been successfully added to slot {{targetSlotId}} but is hidden'
            })
            .service('componentEditingFacade');

        componentEditingFacade = fixture.service;
        mockRestServiceFactory = fixture.mocks.restServiceFactory;
        mockComponentHandlerService = fixture.mocks.componentHandlerService;
        mockComponentService = fixture.mocks.ComponentService;
        mockEditorModalService = fixture.mocks.editorModalService;
        mockRenderService = fixture.mocks.renderService;
        mockRemoveComponentService = fixture.mocks.removeComponentService;
        mockAlertService = fixture.mocks.alertService;
        $q = fixture.injected.$q;
    });

    beforeEach(function() {
        mockContentSlotComponentsRestService = jasmine.createSpyObj('mockContentSlotComponentsRestService', ['update']);
        mockRestServiceFactory.get.andReturn(mockContentSlotComponentsRestService);
    });

    beforeEach(function() {
        MOCK_PAGE_UID = 'SomePageUID';
        MOCK_NEW_COMPONENT = {
            uid: 'SomeNewComponentUID'
        };
        MOCK_ERROR = {
            data: {
                errors: [{
                    message: 'Some detailed error message'
                }]
            }
        };
        MOCK_COMPONENT_ITEM_VISIBLE = {
            visible: true
        };
        MOCK_COMPONENT_ITEM_HIDDEN = {
            visible: false
        };
    });

    describe('addNewComponentToSlot', function() {
        it('should get the current page UID by delegating to the componentHandlerService', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addNewComponent.andReturn($q.when(MOCK_NEW_COMPONENT));
            mockEditorModalService.open.andReturn($q.when());
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.addNewComponentToSlot('SomeTargetSlotUID', 'SomeComponentType', 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
        });


        it('should open the component editor in a modal', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockEditorModalService.open.andReturn($q.when());

            // Act
            componentEditingFacade.addNewComponentToSlot('SomeTargetSlotUID', 'SomeComponentType', 1);
            fixture.detectChanges();

            // Assert
            expect(mockEditorModalService.open).toHaveBeenCalledWith('SomeComponentType', 0, 0, MOCK_PAGE_UID, 'SomeTargetSlotUID', 1);
        });

    });

    describe('addExistingComponentToSlot', function() {
        it('should get the current page UID by delegating to the componentHandlerService', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addExistingComponent.andReturn($q.when());
            mockComponentService.loadComponentItem.andReturn($q.when(MOCK_COMPONENT_ITEM_VISIBLE));
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot('SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot and show a success message if the component is visible', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addExistingComponent.andReturn($q.when());
            mockComponentService.loadComponentItem.andReturn($q.when(MOCK_COMPONENT_ITEM_VISIBLE));
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot('SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith('SomePageUID', 'SomeExistingComponentUID', 'SomeTargetSlotUID', 1);
            expect(mockAlertService.pushAlerts).toHaveBeenCalledWith([{
                successful: true,
                message: 'The component SomeExistingComponentUID has been successfully added to slot SomeTargetSlotUID',
                closeable: true
            }]);
        });

        it('should delegate to componentService to add the existing component to the slot and show a success but hidden message if the component is hidden', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addExistingComponent.andReturn($q.when());
            mockComponentService.loadComponentItem.andReturn($q.when(MOCK_COMPONENT_ITEM_HIDDEN));
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot('SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith('SomePageUID', 'SomeExistingComponentUID', 'SomeTargetSlotUID', 1);
            expect(mockAlertService.pushAlerts).toHaveBeenCalledWith([{
                successful: true,
                message: 'The component SomeExistingComponentUID has been successfully added to slot SomeTargetSlotUID but is hidden',
                closeable: true
            }]);
        });

        it('should delegate to the renderService to re-render the slot the componentService has successfully added the existing component to the slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addExistingComponent.andReturn($q.when());
            mockComponentService.loadComponentItem.andReturn($q.when(MOCK_COMPONENT_ITEM_VISIBLE));
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot('SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith('SomeTargetSlotUID');
        });

        it('should push an alert if adding the existing component to the slot fails', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockComponentService.addExistingComponent.andReturn($q.reject(MOCK_ERROR));

            // Act
            componentEditingFacade.addExistingComponentToSlot('SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.pushAlerts).toHaveBeenCalledWith([{
                successful: false,
                message: 'Failed to move SomeExistingComponentUID to slot SomeTargetSlotUID: Some detailed error message',
                closeable: true
            }]);
        });
    });

    describe('moveComponent', function() {
        it('should delegate to the slot update REST service to update the slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockContentSlotComponentsRestService.update.andReturn($q.when());
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', 'SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockContentSlotComponentsRestService.update).toHaveBeenCalledWith({
                pageId: MOCK_PAGE_UID,
                currentSlotId: 'SomeSourceSlotUID',
                componentId: 'SomeExistingComponentUID',
                slotId: 'SomeTargetSlotUID',
                position: 1
            });
        });

        it('should delegate to the renderService to re-render both changed slots', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockContentSlotComponentsRestService.update.andReturn($q.when());
            mockRenderService.renderSlots.andReturn($q.when());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', 'SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith(['SomeSourceSlotUID', 'SomeTargetSlotUID']);
        });

        it('should push an alert if updating the slot via the slot update REST service fails', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.andReturn(MOCK_PAGE_UID);
            mockContentSlotComponentsRestService.update.andReturn($q.reject());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', 'SomeTargetSlotUID', 'SomeExistingComponentUID', 1);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.pushAlerts).toHaveBeenCalledWith([{
                successful: false,
                message: 'Failed to move SomeExistingComponentUID to slot SomeTargetSlotUID',
                closeable: true
            }]);
        });
    });

});
