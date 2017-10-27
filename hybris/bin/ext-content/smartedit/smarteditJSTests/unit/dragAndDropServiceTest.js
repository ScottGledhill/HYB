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
describe('dragAndDropService', function() {

    // Constants
    var DRAGGABLE_ATTR = 'draggable';
    var DROPPABLE_ATTR = 'data-droppable';

    // Variables
    var dragAndDropService, mocks, $timeout;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('dragAndDropServiceModule')
            .mock('_dragAndDropScrollingService', '_enable').andReturn(null)
            .mock('_dragAndDropScrollingService', '_disable').andReturn(null)
            .mock('_dragAndDropScrollingService', '_deactivate').andReturn(null)
            .withTranslations({})
            .service('dragAndDropService');

        dragAndDropService = harness.service;
        $timeout = harness.injected.$timeout;
        mocks = harness.mocks;
    });

    describe('register', function() {

        it('GIVEN no id is provided THEN it will throw an error and not add the configuration', function() {
            // Arrange
            var configuration = {
                property: 'some property'
            };
            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(0);

            // Act
            var registerFunction = function() {
                dragAndDropService.register(configuration);
            };

            // Assert
            expect(registerFunction).toThrow(new Error('dragAndDropService - register(): Configuration needs an ID.'));
            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(0);
        });

        it('GIVEN an id is provided THEN the configuration is added', function() {
            // Arrange
            var id = 'id1';
            var configuration = {
                property: 'some property',
                id: id
            };
            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(0);

            // Act
            dragAndDropService.register(configuration);

            // Assert
            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(1);
        });

    });

    describe('update', function() {
        var configurations = [{
            id: 'id1',
            sourceSelector: 'some source selector',
            targetSelector: 'some target selector'
        }];

        beforeEach(function() {
            spyOn(dragAndDropService, 'update').andCallThrough();
            spyOn(dragAndDropService, '_update').andCallThrough();
            spyOn(dragAndDropService, '_initializeScrolling').andCallThrough();
            spyOn(dragAndDropService, '_deactivateConfiguration');

            dragAndDropService.register(configurations[0]);
        });

        it('WHEN apply is called THEN it updates the registered configurations', function() {
            // Arrange

            // Act
            dragAndDropService.apply(configurations[0].id);

            // Assert
            expect(dragAndDropService.update).toHaveBeenCalledWith(configurations[0]);
            expect(dragAndDropService._initializeScrolling).toHaveBeenCalledWith(configurations[0]);
        });

        it('WHEN update is called THEN it adds events and attributes as necessary', function() {
            // Arrange
            var droppable = jasmine.createSpyObj('droppable', ['attr', 'on', 'filter']);
            var draggable = jasmine.createSpyObj('draggable', ['attr', 'on', 'filter']);

            spyOn(dragAndDropService, '_getSelector').andCallFake(function(attr) {
                if (attr === configurations[0].sourceSelector) {
                    return draggable;
                } else if (attr === configurations[0].targetSelector) {
                    return droppable;
                }
            });

            draggable.filter.andReturn(draggable);
            droppable.filter.andReturn(droppable);

            // Act
            dragAndDropService.update(configurations[0].id);

            // Assert
            expect(dragAndDropService._deactivateConfiguration).toHaveBeenCalledWith(configurations[0]);

            expect(droppable.attr).toHaveBeenCalledWith(DROPPABLE_ATTR, true);
            expect(droppable.on).toHaveBeenCalledWith('dragenter', jasmine.any(Function));
            expect(droppable.on).toHaveBeenCalledWith('dragover', jasmine.any(Function));
            expect(droppable.on).toHaveBeenCalledWith('drop', jasmine.any(Function));
            expect(droppable.on).toHaveBeenCalledWith('dragleave', jasmine.any(Function));

            expect(draggable.attr).toHaveBeenCalledWith(DRAGGABLE_ATTR, true);
            expect(draggable.on).toHaveBeenCalledWith('dragstart', jasmine.any(Function));
            expect(draggable.on).toHaveBeenCalledWith('dragend', jasmine.any(Function));
        });
    });

    describe('unregister', function() {
        var configurations = [{
            id: 'id1',
            sourceSelector: 'some source selector',
            targetSelector: 'some target selector'
        }, {
            id: 'id2',
            enableScrolling: false
        }, {
            id: 'id3',
            enableScrolling: true
        }];

        beforeEach(function() {
            spyOn(dragAndDropService, '_deactivateConfiguration').andCallThrough();
            spyOn(dragAndDropService, '_deactivateScrolling').andCallThrough();

            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(0);
            expect(dragAndDropService._deactivateConfiguration.calls.length).toBe(0);
            expect(dragAndDropService._deactivateScrolling.calls.length).toBe(0);

            configurations.forEach(function(conf) {
                dragAndDropService.register(conf);
            });
        });

        it('GIVEN a list of configurations THEN each configuration is unregistered if found', function() {
            // Arrange
            var configurationsIDList = ['id1', 'id2', 'id4'];

            // Act
            dragAndDropService.unregister(configurationsIDList);

            // Assert
            expect(dragAndDropService._deactivateConfiguration.calls.length).toBe(2);
            expect(dragAndDropService._deactivateConfiguration).toHaveBeenCalledWith(configurations[0]);
            expect(dragAndDropService._deactivateConfiguration).toHaveBeenCalledWith(configurations[1]);

            expect(dragAndDropService._deactivateScrolling.calls.length).toBe(2);
            expect(dragAndDropService._deactivateScrolling).toHaveBeenCalledWith(configurations[0]);
            expect(dragAndDropService._deactivateScrolling).toHaveBeenCalledWith(configurations[1]);

            expect(getNumPropertiesInObject(dragAndDropService.configurations)).toBe(1);
        });

        it('WHEN _deactivateConfiguration is called THEN all attributes and event listeners are removed ', function() {
            // Arrange
            var droppable = jasmine.createSpyObj('droppable', ['removeAttr', 'off']);
            var draggable = jasmine.createSpyObj('draggable', ['removeAttr', 'off']);

            spyOn(dragAndDropService, '_getSelector').andCallFake(function(attr) {
                if (attr === configurations[0].sourceSelector) {
                    return draggable;
                } else {
                    return droppable;
                }
            });

            // Act
            dragAndDropService._deactivateConfiguration(configurations[0]);

            // Assert
            expect(draggable.removeAttr).toHaveBeenCalledWith(DRAGGABLE_ATTR);
            expect(draggable.off).toHaveBeenCalledWith('dragstart');
            expect(draggable.off).toHaveBeenCalledWith('dragend');

            expect(droppable.removeAttr).toHaveBeenCalledWith(DROPPABLE_ATTR);
            expect(droppable.off).toHaveBeenCalledWith('dragenter');
            expect(droppable.off).toHaveBeenCalledWith('dragover');
            expect(droppable.off).toHaveBeenCalledWith('dragleave');
            expect(droppable.off).toHaveBeenCalledWith('drop');
        });

        it('GIVEN scrolling is enabled WHEN _deactivateScrolling is called THEN scrolling is disabled', function() {
            // Assert
            var configuration = configurations[2];
            expect(configuration.enableScrolling).toBe(true);

            // Act
            dragAndDropService._deactivateScrolling(configuration);

            // Assert
            expect(mocks._dragAndDropScrollingService._deactivate).toHaveBeenCalled();
        });

        it('GIVEN scrolling is not enabled WHEN _deactivateScrolling is called THEN scrolling is left untouched', function() {
            // Assert
            var configuration = configurations[1];
            expect(configuration.enableScrolling).toBe(false);

            // Act
            dragAndDropService._deactivateScrolling(configuration);

            // Assert
            expect(mocks._dragAndDropScrollingService._deactivate).not.toHaveBeenCalled();
        });
    });

    describe('event handlers', function() {
        var configuration = jasmine.createSpyObj('configuration', ['startCallback', 'stopCallback', 'dragOverCallback', 'dropCallback', 'outCallback']);
        var event, evt;

        beforeEach(function() {
            spyOn(dragAndDropService, '_setDragAndDropExecutionStatus').andCallThrough();
            spyOn(dragAndDropService, '_setDragImage').andCallThrough();

            evt = jasmine.createSpyObj('evt', ['preventDefault', 'stopPropagation', 'relatedTarget', 'target']);
            event = {
                originalEvent: evt
            };
        });

        it('WHEN _onDragStart is called THEN the drag is started', function() {
            // Arrange

            // Act
            dragAndDropService._onDragStart(configuration, event);
            $timeout.flush();

            // Assert
            expect(configuration.startCallback).toHaveBeenCalledWith(evt);
            expect(dragAndDropService._setDragAndDropExecutionStatus).toHaveBeenCalledWith(true);
            expect(dragAndDropService._setDragImage).toHaveBeenCalledWith(configuration, evt);
            expect(mocks._dragAndDropScrollingService._enable).toHaveBeenCalled();
        });

        it('WHEN _onDragEnd is called THEN the drag is ended', function() {
            // Arrange
            dragAndDropService._setDragAndDropExecutionStatus(true);

            // Act
            dragAndDropService._onDragEnd(configuration, event);

            // Assert
            expect(configuration.stopCallback).toHaveBeenCalledWith(evt);
            expect(dragAndDropService._setDragAndDropExecutionStatus).toHaveBeenCalledWith(false);
            expect(mocks._dragAndDropScrollingService._disable).toHaveBeenCalled();
        });

        it('WHEN mouse is over droppable area THEN drag over is handled', function() {
            // Arrange
            dragAndDropService._setDragAndDropExecutionStatus(true);

            // Act
            dragAndDropService._onDragOver(configuration, event);

            // Assert
            expect(evt.preventDefault).toHaveBeenCalled();
            expect(configuration.dragOverCallback).toHaveBeenCalledWith(evt);
        });

        it('WHEN mouse is released THEN on drop is handled', function() {
            // Arrange
            dragAndDropService._setDragAndDropExecutionStatus(true);

            // Act
            dragAndDropService._onDrop(configuration, event);

            // Assert;
            expect(evt.preventDefault).toHaveBeenCalled();
            expect(evt.stopPropagation).toHaveBeenCalled();
            expect(configuration.dropCallback).toHaveBeenCalledWith(evt);
        });

        it('_onDragLeave', function() {
            // Arrange
            dragAndDropService._setDragAndDropExecutionStatus(true);

            // Act
            dragAndDropService._onDragLeave(configuration, event);

            // Assert
            expect(evt.preventDefault).toHaveBeenCalled();
            expect(configuration.outCallback).toHaveBeenCalledWith(evt);
        });
    });

    it('WHEN markDragStarted is executed THEN drag and drop is started.', function() {
        // Arrange
        spyOn(dragAndDropService, '_setDragAndDropExecutionStatus');

        // Act
        dragAndDropService.markDragStarted();

        // Assert
        expect(dragAndDropService._setDragAndDropExecutionStatus).toHaveBeenCalledWith(true);
        expect(mocks._dragAndDropScrollingService._enable).toHaveBeenCalled();
    });

    it('WHEN markDragStopped is executed THEN drag and drop is stopped.', function() {
        // Arrange
        spyOn(dragAndDropService, '_setDragAndDropExecutionStatus');

        // Act
        dragAndDropService.markDragStopped();

        // Assert
        expect(dragAndDropService._setDragAndDropExecutionStatus).toHaveBeenCalledWith(false);
        expect(mocks._dragAndDropScrollingService._disable).toHaveBeenCalled();
    });

    // Helper method
    function getNumPropertiesInObject(obj) {
        return Object.keys(obj).length;
    }
});
