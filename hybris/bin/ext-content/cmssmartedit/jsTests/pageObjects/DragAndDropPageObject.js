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
(function() {

    var dragAndDropPageObject = {};

    dragAndDropPageObject.structure = {
        slots: {
            TOP_HEADER_SLOT: 'topHeaderSlot',
            MIDDLE_HEADER_SLOT: 'otherSlot',
            BOTTOM_HEADER_SLOT: 'bottomHeaderSlot',
            FOOTER_SLOT: 'footerSlot'
        },
        components: {
            COMPONENT1: 'component1',
            COMPONENT2: 'component2',
            COMPONENT3: 'component3',
            COMPONENT5: 'component5',
            COMPONENT10: 'component10'
        }
    };

    dragAndDropPageObject.elements = {
        getSlotById: function(slotId) {
            return element(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "']"));
        },
        getComponentById: function(componentId) {
            return element(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + componentId + "']"));
        },
        getMoveButtonForComponentId: function(componentId) {
            return element(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + componentId + "'] .movebutton"));
        },
        getComponentsInSlot: function(slotId, expectedElementsInSlotCount) {
            return browser.wait(function() {
                var deferred = protractor.promise.defer();
                element.all(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "'] .smartEditComponentX")).count().then(function(count) {
                    deferred.fulfill(count === expectedElementsInSlotCount);
                });
                return deferred.promise;
            }, 10000).then(function() {
                return element.all(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "'] .smartEditComponentX"));
            });
        },
        getComponentHelpers: function(slotId, position) {
            position++; // The nth-child selector doesn't start at 0, but at 1.
            return browser.wait(function() {
                return element.all(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "'] .smartEditComponentX:nth-child(" + position + ") .overlayDropzone"));
            }, 10000);
        },
        getComponentMenuButton: function() {
            return element(by.css('.ySEComponentMenuW--button'));
        },
        getCustomizedComponentsTab: function() {
            return element(by.css('li.ySEComTypeTab:nth-child(2)'));
        },
        getComponentInComponentMenu: function() {
            return element(by.css(".smartEditComponent[data-smartedit-component-type='CMSParagraphComponent'] img"));
        },
        getComponentInCustomizedComponentMenu: function() {
            return element(by.css("y-infinite-scrolling .smartEditComponent[data-smartedit-component-type='CMSParagraphComponent'] img"));
        },
        getTopScrollHint: function() {
            return element(by.id('top_scroll_page'));
        },
        getBottomScrollHint: function() {
            return element(by.id('bottom_scroll_page'));
        }
    };

    dragAndDropPageObject.actions = {
        moveToStoreFront: function() {
            browser.switchToIFrame();
        },
        moveToParent: function() {
            browser.switchToParent();
        },
        moveComponentToSlot: function(sourceSlotId, componentId, targetSlotId) {
            _grabComponent(sourceSlotId, componentId);

            var targetSlot = dragAndDropPageObject.elements.getSlotById(targetSlotId);

            _hoverOverSlot(targetSlot);
            _dropComponent(componentId, targetSlotId);
        },
        moveComponentToPositionInSlot: function(sourceSlotId, componentId, targetSlotId, position, expectedElementsInSlotCount) {
            dragAndDropPageObject.elements.getComponentsInSlot(targetSlotId, expectedElementsInSlotCount).then(function(elements) {
                var elementCount = elements.length;
                var targetSlot = dragAndDropPageObject.elements.getSlotById(targetSlotId);

                _grabComponent(sourceSlotId, componentId);
                _hoverOverSlot(targetSlot);

                if (position < elementCount) {
                    dragAndDropPageObject.elements.getComponentHelpers(targetSlotId, position + 1).then(function(helpers) {
                        _hoverOverHint(helpers[0]);

                        this.dropAtDroppableArea(componentId, helpers[0]);
                    }.bind(this));
                } else {
                    dragAndDropPageObject.elements.getComponentHelpers(targetSlotId, position).then(function(helpers) {
                        _hoverOverHint(helpers[1]);
                        this.dropAtDroppableArea(componentId, helpers[1]);
                    }.bind(this));
                }

            }.bind(this));
        },
        addComponentToSlotInPosition: function(targetSlotId, position, expectedElementsInSlotCount) {
            dragAndDropPageObject.elements.getComponentsInSlot(targetSlotId, expectedElementsInSlotCount).then(function(elements) {
                var elementCount = elements.length;
                var targetSlot = dragAndDropPageObject.elements.getSlotById(targetSlotId);

                _hoverOverSlot(targetSlot);
                if (position < elementCount) {
                    dragAndDropPageObject.elements.getComponentHelpers(targetSlotId, position + 1).then(function(helpers) {
                        _hoverOverHint(helpers[0]);
                        this.dropComponentFromComponentMenu(helpers[0]);
                    }.bind(this));
                } else {
                    dragAndDropPageObject.elements.getComponentHelpers(targetSlotId, position).then(function(helpers) {
                        _hoverOverHint(helpers[1]);
                        this.dropComponentFromComponentMenu(helpers[1]);
                    }.bind(this));
                }
            }.bind(this));
        },
        moveToElement: function(element) {
            browser.actions()
                .mouseMove(element)
                .perform();
        },
        grabComponent: function(slotId, componentId) {
            _grabComponent(slotId, componentId);
        },
        grabComponentInComponentMenu: function() {
            _grabComponentInComponentMenu();
        },
        grabCustomizedComponentInComponentMenu: function() {
            _grabCustomizedComponentInComponentMenu();
        },
        hoverOverSlot: function(slot) {
            return _hoverOverSlot(slot);
        },
        dropComponent: function(componentId, slotId) {
            _dropComponent(componentId, slotId);
        },
        dropComponentFromComponentMenu: function(element) {
            _dropComponentFromComponentMenu(element);
        },
        dropAtDroppableArea: function(componentId, droppableArea) {
            _dropAtDroppableArea(componentId, droppableArea);
        },
        isComponentInPosition: function(slot, component, position, expectedElementsInSlotCount) {
            return _isComponentInPosition(slot, component, position, expectedElementsInSlotCount);
        },
        isSlotEnabled: function(slotId) {
            var slot = dragAndDropPageObject.elements.getSlotById(slotId);
            return hasClass(slot, 'over-slot-enabled');
        },
        isSlotDisabled: function(slotId) {
            var slot = dragAndDropPageObject.elements.getSlotById(slotId);
            return hasClass(slot, 'over-slot-disabled');
        },
        hoverOverTopHint: function() {
            var hint = dragAndDropPageObject.elements.getTopScrollHint();
            this.moveToElement(hint);
            browser.executeScript(simulateDragAndDropOperation, hint.getWebElement(), EVENT_TYPES.DRAG_ENTER);
        },
        hoverOverBottomHint: function() {
            var hint = dragAndDropPageObject.elements.getBottomScrollHint();
            this.moveToElement(hint);
            browser.executeScript(simulateDragAndDropOperation, hint.getWebElement(), EVENT_TYPES.DRAG_ENTER);
        },
        scrollToBottom: function() {
            _scrollToBottom();
        },
        getPageVerticalScroll: function() {
            return _getCurrentPageVerticalScroll();
        }
    };

    var EVENT_TYPES = {
        DRAG_START: 'dragstart',
        DRAG_END: 'dragend',
        DROP: 'drop',
        DRAG_ENTER: 'dragenter',
        DRAG_OVER: 'dragover'
    };

    function _grabComponent(slotId, componentId) {
        browser.switchToIFrame();
        browser.actions()
            .mouseMove(dragAndDropPageObject.elements.getSlotById(slotId))
            .mouseMove(dragAndDropPageObject.elements.getComponentById(componentId))
            .mouseMove(dragAndDropPageObject.elements.getMoveButtonForComponentId(componentId))
            .perform();

        var elementToDrag = dragAndDropPageObject.elements.getMoveButtonForComponentId(componentId);
        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    function _grabComponentInComponentMenu() {
        dragAndDropPageObject.elements.getComponentMenuButton().click();

        var elementToDrag = dragAndDropPageObject.elements.getComponentInComponentMenu();
        browser.actions()
            .mouseMove(elementToDrag)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    function _grabCustomizedComponentInComponentMenu() {
        dragAndDropPageObject.elements.getComponentMenuButton().click();
        dragAndDropPageObject.elements.getCustomizedComponentsTab().click();

        var elementToDrag = dragAndDropPageObject.elements.getComponentInCustomizedComponentMenu();
        browser.actions()
            .mouseMove(elementToDrag)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_START);
    }

    // this method needs to be retired at some point when all of its usages are replaced by dropComponentFromComponentMenu or dropAtDroppableArea
    function _dropComponent(componentId, slotId) {
        var elementToDrag = dragAndDropPageObject.elements.getMoveButtonForComponentId(componentId);
        var slot = dragAndDropPageObject.elements.getSlotById(slotId);

        browser.executeScript(simulateDragAndDropOperation, slot.getWebElement(), EVENT_TYPES.DROP);
        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_END);
    }

    function _dropComponentFromComponentMenu(dropabbleArea) {

        browser.executeScript(simulateDragAndDropOperation, dropabbleArea.getWebElement(), EVENT_TYPES.DROP);
        browser.switchToParent();

        var elementToDrag = dragAndDropPageObject.elements.getComponentInCustomizedComponentMenu();
        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_END);
        browser.switchToIFrame();
    }

    function _dropAtDroppableArea(componentId, dropabbleArea) {
        browser.switchToIFrame();
        var elementToDrag = dragAndDropPageObject.elements.getMoveButtonForComponentId(componentId);
        browser.executeScript(simulateDragAndDropOperation, dropabbleArea.getWebElement(), EVENT_TYPES.DROP);
        browser.executeScript(simulateDragAndDropOperation, elementToDrag.getWebElement(), EVENT_TYPES.DRAG_END);
    }

    function _hoverOverSlot(slot) {

        browser.actions()
            .mouseMove(slot)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, slot.getWebElement(), EVENT_TYPES.DRAG_ENTER);
        browser.executeScript(simulateDragAndDropOperation, slot.getWebElement(), EVENT_TYPES.DRAG_OVER);
    }

    function _hoverOverHint(hint) {

        browser.actions()
            .mouseMove(hint)
            .perform();

        browser.executeScript(simulateDragAndDropOperation, hint.getWebElement(), EVENT_TYPES.DRAG_OVER);
    }

    /*
        Unfortunately, Protractor's Chrome Driver is not compatible with HTML 5 drag and drop. If it's simulated (hover
        over component, mouse down, and then mouse move), nothing happens; the events are never triggered. Thus, we
        have to simulate the events.
      */
    function simulateDragAndDropOperation(element, operationType) {
        function createCustomEvent(type) {
            var event = document.createEvent("CustomEvent");
            event.initCustomEvent(type, true, true, null);
            event.dataTransfer = {
                data: {},
                setData: function(type, val) {
                    this.data[type] = val;
                },
                getData: function(type) {
                    return this.data[type];
                }
            };

            var rects = element.getBoundingClientRect();
            event.pageX = rects.left + Math.ceil((rects.right - rects.left) / 2);
            event.pageY = window.scrollY + rects.top + Math.ceil((rects.bottom - rects.top) / 2);

            return event;
        }

        function dispatchEvent(node, type, event) {
            if (node.dispatchEvent) {
                return node.dispatchEvent(event);
            }
            if (node.fireEvent) {
                return node.fireEvent("on" + type, event);
            }
        }

        var event = createCustomEvent(operationType);
        dispatchEvent(element, operationType, event);
    }

    // Helpers
    function _scrollToBottom() {
        return browser.executeScript('window.scrollTo(0,document.body.scrollHeight);');
    }

    function _getCurrentPageVerticalScroll() {
        return browser.executeScript('return document.body.scrollTop;');
    }

    function _isComponentInPosition(slotId, componentId, position, expectedElementsInSlotCount) {
        return dragAndDropPageObject.elements.getComponentsInSlot(slotId, expectedElementsInSlotCount)
            .then(function(components) {
                if (components.length > position) {
                    return browser.wait(function() {
                        var component = components[position];
                        return component.getAttribute('data-smartedit-component-id');
                    }, 10000).then(function(attr) {
                        return (attr === componentId);
                    });
                } else {
                    return false;
                }
            });
    }

    function hasClass(element, className) {
        return element.getAttribute('class').then(function(classes) {
            return classes.split(' ').indexOf(className) !== -1;
        });
    }

    module.exports = dragAndDropPageObject;

}());
