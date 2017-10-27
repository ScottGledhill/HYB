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
describe('cmsDragAndDropService', function() {

    // Constants
    var ID_ATTRIBUTE = 'ID';
    var TYPE_ATTRIBUTE = 'TYPE';
    var DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

    // Variables
    var cmsDragAndDropService, dragAndDropService, systemEventService, gatewayFactory;
    var gateway;

    beforeEach(function() {
        angular.module('dragAndDropServiceModule', []);
    });
    beforeEach(module('cmsDragAndDropServiceModule', function($provide) {
        dragAndDropService = jasmine.createSpyObj('dragAndDropService', ['register', 'unregister', 'apply', 'update']);
        systemEventService = jasmine.createSpyObj('systemEventService', ['sendAsynchEvent']);
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
        gateway = jasmine.createSpyObj('gateway', ['publish']);

        gatewayFactory.createGateway.andReturn(gateway);

        $provide.value('dragAndDropService', dragAndDropService);
        $provide.value('systemEventService', systemEventService);
        $provide.value('gatewayFactory', gatewayFactory);
        $provide.value('ID_ATTRIBUTE', ID_ATTRIBUTE);
        $provide.value('TYPE_ATTRIBUTE', TYPE_ATTRIBUTE);
    }));

    beforeEach(inject(function(_cmsDragAndDropService_) {
        cmsDragAndDropService = _cmsDragAndDropService_;
    }));

    it('WHEN cmsDragAndDropService is created THEN a gateway is created to communicate with the inner frame', function() {
        // Assert
        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('cmsDragAndDrop');
        expect(cmsDragAndDropService._gateway).toBe(gateway);
    });

    describe('register', function() {

        beforeEach(function() {

        });

        it('WHEN register is called THEN it is registered in the base drag and drop service.', function() {
            // Arrange

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls[0].args[0];
            expect(dragAndDropService.register).toHaveBeenCalled();
            expect(arg.id).toBe(DRAG_AND_DROP_ID);
            expect(arg.sourceSelector).toBe(".smartEditComponent[data-smartedit-component-type!='ContentSlot']");
            expect(arg.targetSelector).toBe("");
            expect(arg.enableScrolling).toBe(false);
        });

        it('WHEN register is called THEN it is registered with the right onStart callback.', function() {
            // Arrange
            var expectedResult = 'some result';
            spyOn(cmsDragAndDropService, '_onStart').andReturn(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls[0].args[0];
            var result = arg.startCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN it is registered with the right onStop callback.', function() {
            // Arrange
            var expectedResult = 'some result';
            spyOn(cmsDragAndDropService, '_onStop').andReturn(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls[0].args[0];
            var result = arg.stopCallback();
            expect(result).toBe(expectedResult);
        });

    });

    it('WHEN apply is called THEN the cms service is applied in the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.apply();

        // Assert
        expect(dragAndDropService.apply).toHaveBeenCalled();
    });

    it('WHEN update is called THEN the cms service is updated in the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.update();

        // Assert
        expect(dragAndDropService.update).toHaveBeenCalledWith(DRAG_AND_DROP_ID);
    });

    it('WHEN unregister is called THEN the cms service is unregistered from the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.unregister();

        // Assert
        expect(dragAndDropService.unregister).toHaveBeenCalledWith([DRAG_AND_DROP_ID]);
    });

    it('WHEN drag is started THEN the service informs other components', function() {
        // Arrange
        var componentInfo = {
            id: 'some id',
            type: 'some type'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.andCallFake(function(arg) {
            if (arg === ID_ATTRIBUTE) {
                return componentInfo.id;
            } else if (arg === TYPE_ATTRIBUTE) {
                return componentInfo.type;
            }
        });

        var event = {
            target: 'some target'
        };
        var draggedElement = {
            closest: function() {
                return component;
            }
        };

        spyOn(cmsDragAndDropService, '_getSelector').andReturn(draggedElement);

        // Act
        cmsDragAndDropService._onStart(event);

        // Assert
        expect(cmsDragAndDropService._gateway.publish).toHaveBeenCalledWith('CMS_DRAG_STARTED', {
            componentId: componentInfo.id,
            componentType: componentInfo.type,
            slotId: null
        });
        expect(systemEventService.sendAsynchEvent).toHaveBeenCalledWith('CMS_DRAG_STARTED');
    });

    it('WHEN drag is stopped THEN the inner frame is informed', function() {
        // Arrange

        // Act
        cmsDragAndDropService._onStop();

        // Assert
        expect(cmsDragAndDropService._gateway.publish).toHaveBeenCalledWith('CMS_DRAG_STOPPED');
    });

});
