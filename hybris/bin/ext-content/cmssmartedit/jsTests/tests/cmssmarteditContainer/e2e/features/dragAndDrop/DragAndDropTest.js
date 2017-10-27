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
describe('Drag and Drop', function() {

    var dragAndDrop = e2e.pageObjects.DragAndDrop;
    var perspective = e2e.componentObjects.modeSelector;
    var slots = dragAndDrop.structure.slots;
    var components = dragAndDrop.structure.components;

    beforeEach(function() {
        browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/dragAndDrop/index.html');
        browser.waitForWholeAppToBeReady();
    });

    describe('within frame', function() {
        it('GIVEN I am in a perspective in which drag and drop is enabled ' +
            'WHEN I grab a component from one slot ' +
            'AND hover over another slot in which its type is not allowed ' +
            'THEN I should see the slot highlighted to indicate that drop is not allowed',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();

                // Act
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT1);
                var bottomSlot = dragAndDrop.elements.getSlotById(slots.BOTTOM_HEADER_SLOT);
                dragAndDrop.actions.hoverOverSlot(bottomSlot);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.BOTTOM_HEADER_SLOT)).toBe(false, 'Expected slot not to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.BOTTOM_HEADER_SLOT)).toBe(true, 'Expected slot to display that the component is forbidden');
            });


        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from one slot' +
            'AND hover over another slot in which its type is allowed' +
            'THEN I should see the slot highlighted to indicate that drop is allowed',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();

                // Act
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT1);
                var middleHeaderSlot = dragAndDrop.elements.getSlotById(slots.MIDDLE_HEADER_SLOT);
                dragAndDrop.actions.hoverOverSlot(middleHeaderSlot);

                //Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.MIDDLE_HEADER_SLOT)).toBe(true, 'Expected slot to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.MIDDLE_HEADER_SLOT)).toBe(false, 'Expected slot not to display that the component is forbidden');
            });


        xit('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from a slot' +
            'AND drop it into another position within the slot' +
            'THEN the component is moved to the new position',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();

                // Act
                dragAndDrop.actions.moveComponentToPositionInSlot(slots.TOP_HEADER_SLOT, components.COMPONENT3, slots.TOP_HEADER_SLOT, 0, 3);

                // Assert
                expect(dragAndDrop.actions.isComponentInPosition(slots.TOP_HEADER_SLOT, components.COMPONENT3, 0, 3)).toBe(true, 'Expect component 3 to at the first position of the middle slot');
                expect(dragAndDrop.actions.isComponentInPosition(slots.TOP_HEADER_SLOT, components.COMPONENT1, 1, 3)).toBe(true, 'Expect component 1 to be at the second position of the middle slot');
                expect(dragAndDrop.actions.isComponentInPosition(slots.TOP_HEADER_SLOT, components.COMPONENT2, 2, 3)).toBe(true, 'Expect component 2 to be at the third position of the middle slot');
            });
    });

    describe('across frames', function() {
        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from the component menu' +
            'AND hover over a slot in which its type is not allowed' +
            'THEN I should see the slot highlighted to indicate that drop is not allowed',
            function() {
                // Arrange
                perspective.selectBasicPerspective();

                // Act
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                var middleHeaderSlot = dragAndDrop.elements.getSlotById(slots.MIDDLE_HEADER_SLOT);
                dragAndDrop.actions.hoverOverSlot(middleHeaderSlot);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.MIDDLE_HEADER_SLOT)).toBe(false, 'Expected slot not to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.MIDDLE_HEADER_SLOT)).toBe(true, 'Expected slot to display that the component is forbidden');
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from the component menu' +
            'AND hover over a slot in which its type is allowed' +
            'THEN I should see the slot highlighted to indicate that drop is allowed',
            function() {
                // Arrange
                perspective.selectBasicPerspective();

                // Act
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                var bottomHeaderSlot = dragAndDrop.elements.getSlotById(slots.BOTTOM_HEADER_SLOT);
                dragAndDrop.actions.hoverOverSlot(bottomHeaderSlot);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.BOTTOM_HEADER_SLOT)).toBe(true, 'Expected slot to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.BOTTOM_HEADER_SLOT)).toBe(false, 'Expected slot not to display that the component is forbidden');

            });

        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from the component menu' +
            'AND hover over an empty slot in which its type is allowed' +
            'THEN I should be able to drop the component',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();

                // Making slot empty.
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT5);
                var middleHeaderSlot = dragAndDrop.elements.getSlotById(slots.MIDDLE_HEADER_SLOT);
                dragAndDrop.actions.hoverOverSlot(middleHeaderSlot);
                dragAndDrop.actions.dropComponent(slots.MIDDLE_HEADER_SLOT, components.COMPONENT5);

                dragAndDrop.actions.moveToParent();

                // Act
                dragAndDrop.actions.grabCustomizedComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();

                var footerSlot = dragAndDrop.elements.getSlotById(slots.FOOTER_SLOT);
                dragAndDrop.actions.hoverOverSlot(footerSlot);
                dragAndDrop.actions.dropComponentFromComponentMenu(footerSlot);

                // Assert
                expect(dragAndDrop.actions.isComponentInPosition(slots.FOOTER_SLOT, components.COMPONENT10, 0, 1)).toBe(true, 'Expected new component to at the first position of the footer slot');
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from the component menu' +
            'AND hover over a non empty slot in which its type is allowed' +
            'THEN I should be able to drop the component',
            function() {
                // Arrange
                perspective.selectBasicPerspective();

                // Act
                dragAndDrop.actions.grabCustomizedComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.addComponentToSlotInPosition(slots.FOOTER_SLOT, 1, 1);

                // Assert
                expect(dragAndDrop.actions.isComponentInPosition(slots.FOOTER_SLOT, components.COMPONENT10, 1, 2)).toBe(true, 'Expected new component to move to the second position of the footer slot');
            });
    });

    describe('scrolling ', function() {
        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component' +
            'AND I position the mouse in the lower hint' +
            'THEN the page scrolls down',
            function() {
                perspective.selectBasicPerspective();
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverBottomHint();
                    browser.sleep(1000);

                    // Assert
                    dragAndDrop.actions.getPageVerticalScroll().then(function(newScroll) {
                        expect(newScroll > oldScroll).toBe(true, 'Expected page to scroll down');
                    });
                });
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component' +
            'AND I position the mouse in the top hint' +
            'THEN the page scrolls down',
            function() {
                perspective.selectBasicPerspective();
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.scrollToBottom();
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverTopHint();
                    browser.sleep(1000);

                    // Assert
                    dragAndDrop.actions.getPageVerticalScroll().then(function(newScroll) {
                        expect(newScroll < oldScroll).toBe(true, 'Expected page to scroll up');
                    });
                });
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled' +
            'WHEN I grab a component from the component menu' +
            'AND I position the mouse in the lower hint' +
            'THEN the page scrolls down',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT1);
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverBottomHint();
                    browser.sleep(1000);

                    // Assert
                    dragAndDrop.actions.getPageVerticalScroll().then(function(newScroll) {
                        expect(newScroll > oldScroll).toBe(true, 'Expected page to scroll down');
                    });
                });
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled and the page is at the bottom' +
            'WHEN I grab a component from the component menu' +
            'AND I position the mouse in the upper hint' +
            'THEN the page scrolls up',
            function() {
                // Arrange
                perspective.selectBasicPerspective();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.scrollToBottom();
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT1);
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverTopHint();
                    browser.sleep(1000);

                    // Assert
                    dragAndDrop.actions.getPageVerticalScroll().then(function(newScroll) {
                        expect(newScroll < oldScroll).toBe(true, 'Expected page to scroll up');
                    });
                });
            });
    });

});
