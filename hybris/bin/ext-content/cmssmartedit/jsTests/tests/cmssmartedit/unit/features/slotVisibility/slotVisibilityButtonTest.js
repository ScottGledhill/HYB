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
describe('slotVisibilityButton', function() {

    beforeEach(module('ui.bootstrap'));

    describe('when hidden component list is empty', function() {
        describe('template', function() {
            it('should not display the button', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();

                // Act

                // Assert
                expect(element.find('button[type=button]').length).toBe(0);
            });
        });

        describe('controller', function() {
            it('should set button visibility to false', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.buttonVisible).toBe(false);
            });
            it('should expect that the hidden component list is empty', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.hiddenComponents).toEqual([]);
            });
            it('should expect component list visibility status to not be opened', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.isComponentListOpened).toBe(false);
            });
        });
    });

    describe('when hidden component list is not empty', function() {
        describe('template', function() {
            it('should display the hidden components button', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();

                // Act

                // Assert
                expect(element.find('button.slot-visibility-button-template__btn').length)
                    .toBe(1, 'Expected slot visibility hidden components button to be present');
            });

            it('should display dropdown menu if the button is clicked', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(element.find('.btn-group.slot-visibility-button-template__btn-group.dropdown').length).toBe(1);
            });
        });

        describe('controller', function() {
            it('should expect button to be visible', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.buttonVisible).toBe(true);
            });

            it('should expect that the hidden component list is not empty', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.hiddenComponents.length).toBe(2);
            });

            it('should expect component list visibility status to be opened', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.isComponentListOpened).toBe(true);
            });
        });
    });

    function buildComponentWithNoHiddenComponents() {
        var template = '<slot-visibility-button ' +
            'data-slot-id="{{::smarteditComponentId}}" ' +
            'data-set-remain-open="setRemainOpen(button, remainOpen)">' +
            '</slot-visibility-button>';

        return AngularUnitTestHelper.prepareModule('slotVisibilityButtonModule')
            .mock('slotVisibilityService', 'getHiddenComponents').andReturnResolvedPromise([])
            .mock('editorModalService', 'openAndRerenderSlot').andReturnResolvedPromise()
            .component(template, {
                smarteditComponentId: 'abc-slot-id',
                setRemainOpen: jasmine.createSpy('setRemainOpen')
            }).element;
    }

    function buildComponentWithTwoHiddenComponents() {
        var template = '<slot-visibility-button ' +
            'data-slot-id="{{::smarteditComponentId}}" ' +
            'data-set-remain-open="setRemainOpen(button, remainOpen)">' +
            '</slot-visibility-button>';

        return AngularUnitTestHelper.prepareModule('slotVisibilityButtonModule')
            .mock('slotVisibilityService', 'getHiddenComponents').andReturnResolvedPromise([{
                uid: '1',
                name: 'component1',
                typeCode: 'BannerComponent'
            }, {
                uid: '2',
                name: 'component2',
                typeCode: 'SimpleBannerComponent'
            }])
            .mock('editorModalService', 'openAndRerenderSlot').andReturnResolvedPromise()
            .component(template, {
                smarteditComponentId: 'abc-slot-id',
                setRemainOpen: jasmine.createSpy('setRemainOpen')
            }).element;
    }

});
