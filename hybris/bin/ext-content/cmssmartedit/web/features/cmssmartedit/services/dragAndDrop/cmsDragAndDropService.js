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

    /**
     * @ngdoc overview
     * @name cmsDragAndDropServiceModule
     * @description
     * # The cmsDragAndDropServiceModule
     *
     * The cmsDragAndDropServiceModule contains a service that provides a rich drag and drop experience tailored for CMS operations.
     *
     */
    angular.module('cmsDragAndDropServiceModule', ['dragAndDropServiceModule', 'alertServiceModule', 'yLoDashModule', 'componentHandlerServiceModule', 'eventServiceModule', 'gatewayFactoryModule', 'slotRestrictionsServiceModule', 'componentEditingFacadeModule', 'translationServiceModule', 'removeComponentServiceModule'])
        /**
         * @ngdoc object
         * @name cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
         *
         * @description
         * Injectable angular constant<br/>
         * Constants identifying CMS drag and drop events.
         *
         */
        .constant('DRAG_AND_DROP_EVENTS', {
            /**
             * @ngdoc property
             * @name DRAG_STARTED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when a drag and drop event starts.
             **/
            DRAG_STARTED: 'CMS_DRAG_STARTED',
            /**
             * @ngdoc property
             * @name DRAG_STOPPED
             * @propertyOf cmsDragAndDropServiceModule.object:DRAG_AND_DROP_EVENTS
             *
             * @description
             * Name of event executed when a drag and drop event stops.
             **/
            DRAG_STOPPED: 'CMS_DRAG_STOPPED'
        })
        /**
         * @ngdoc service
         * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService
         *
         * @description
         * This service provides a rich drag and drop experience tailored for CMS operations.
         */
        .service('cmsDragAndDropService', function($q, $window, $translate, lodash, dragAndDropService, componentHandlerService, systemEventService, gatewayFactory, slotRestrictionsService, alertService, componentEditingFacade, DRAG_AND_DROP_EVENTS, COMPONENT_REMOVED_EVENT, CONTENT_SLOT_TYPE) {
            // Constants
            var CMS_DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

            var TARGET_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type='ContentSlot']";
            var SOURCE_SELECTOR = "#smarteditoverlay .smartEditComponentX[data-smartedit-component-type!='ContentSlot'] .movebutton";
            var MORE_MENU_SOURCE_SELECTOR = ".smartedit-html-container ul[id*='more-menu'] .movebutton";

            var SLOT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type='ContentSlot']";
            var COMPONENT_SELECTOR = ".smartEditComponentX[data-smartedit-component-type!='ContentSlot']";
            var HINT_SELECTOR = '.overlayDropzone';

            var CSS_CLASSES = {
                UI_HELPER_OVERLAY: 'overlayDnd',
                DROPZONE: 'overlayDropzone',
                DROPZONE_FULL: 'overlayDropzone--full',
                DROPZONE_TOP: 'overlayDropzone--top',
                DROPZONE_BOTTOM: 'overlayDropzone--bottom',
                DROPZONE_LEFT: 'overlayDropzone--left',
                DROPZONE_RIGHT: 'overlayDropzone--right',
                DROPZONE_HOVERED: 'overlayDropzone--hovered',
                OVERLAY_IN_DRAG_DROP: 'smarteditoverlay_dndRendering',
                COMPONENT_DRAGGED: 'component_dragged',
                COMPONENT_DRAGGED_HOVERED: 'component_dragged_hovered',
                SLOTS_MARKED: 'slot-marked',
                SLOT_ALLOWED: 'over-slot-enabled',
                SLOT_NOT_ALLOWED: 'over-slot-disabled'
            };

            // Variables
            this._cachedSlots = {};
            this._highlightedSlot = null;
            this._highlightedComponent = null;
            this._highlightedHint = null;

            this._gateway = gatewayFactory.createGateway("cmsDragAndDrop");

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#register
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method registers this drag and drop instance in SmartEdit.
             */
            this.register = function() {
                dragAndDropService.register({
                    id: CMS_DRAG_AND_DROP_ID,
                    sourceSelector: [SOURCE_SELECTOR, MORE_MENU_SOURCE_SELECTOR], //the source selectors are DnD menus located both inside and outside the more options of the overlay
                    targetSelector: TARGET_SELECTOR,
                    startCallback: this.onStart,
                    dragEnterCallback: this.onDragEnter,
                    dragOverCallback: this.onDragOver,
                    dropCallback: this.onDrop,
                    outCallback: this.onDragLeave,
                    stopCallback: this.onStop,
                    enableScrolling: true
                });
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#unregister
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method unregisters this drag and drop instance from SmartEdit.
             */
            this.unregister = function() {
                dragAndDropService.unregister([CMS_DRAG_AND_DROP_ID]);
                slotRestrictionsService.emptyCache();

                systemEventService.unRegisterEventHandler('overlayRerendered', this._onOverlayUpdate);
                systemEventService.unRegisterEventHandler(COMPONENT_REMOVED_EVENT, this._onOverlayUpdate);
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#apply
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method applies this drag and drop instance in the current page. After this method is executed, the user can start a
             * drag and drop operation.
             */
            this.apply = function() {
                dragAndDropService.apply(CMS_DRAG_AND_DROP_ID);
                this._addUIHelpers();

                // Register a listener for every time the overlay is updated.
                systemEventService.registerEventHandler('overlayRerendered', this._onOverlayUpdate);
                systemEventService.registerEventHandler(COMPONENT_REMOVED_EVENT, this._onOverlayUpdate);

                this._gateway.subscribe(DRAG_AND_DROP_EVENTS.DRAG_STARTED, function(eventId, data) {
                    dragAndDropService.markDragStarted();
                    this._initializeDragOperation(data);
                }.bind(this));

                this._gateway.subscribe(DRAG_AND_DROP_EVENTS.DRAG_STOPPED, function(eventId, data) {
                    dragAndDropService.markDragStopped();
                    this._cleanDragOperation();
                }.bind(this));
            };

            /**
             * @ngdoc method
             * @name cmsDragAndDropServiceModule.service:cmsDragAndDropService#update
             * @methodOf cmsDragAndDropServiceModule.service:cmsDragAndDropService
             *
             * @description
             * This method updates this drag and drop instance in the current page. It is important to execute this method every time a draggable or droppable element
             * is added or removed from the page DOM.
             *
             */
            this.update = function() {
                dragAndDropService.update(CMS_DRAG_AND_DROP_ID);

                // Add UI helpers -> They identify the places where you can drop components.
                this._addUIHelpers();
            };

            // Other Event Handlers
            this._onOverlayUpdate = function() {
                this.update();
                return $q.when();
            }.bind(this);

            // Drag and Drop Event Handlers
            this.onStart = function(event) {
                // Find element
                var targetElm = this._getSelector(event.target);
                var component = targetElm.closest(COMPONENT_SELECTOR);
                var slot = component.closest(SLOT_SELECTOR);

                // Here if the component evaluated above exits that means the component has been located and we can fetch its attributes 
                // else it is not located as the DnD option is hidden inside the more option of the contextual menu in which case 
                // we find the component/slot info by accessing attributes of the DnD icon.
                var componentId = component.length > 0 ? componentHandlerService.getSlotOperationRelatedId(component) : targetElm.attr('data-component-id');
                var componentType = component.length > 0 ? componentHandlerService.getSlotOperationRelatedType(component) : targetElm.attr('data-component-type');
                var slotId = component.length > 0 ? componentHandlerService.getId(slot) : targetElm.attr('data-slot-id');

                var dragInfo = {
                    componentId: componentId,
                    componentType: componentType,
                    slotId: slotId
                };
                component.addClass(CSS_CLASSES.COMPONENT_DRAGGED);

                this._initializeDragOperation(dragInfo);
            }.bind(this);

            this.onDragEnter = function(event) {
                this._highlightSlot(event);
            }.bind(this);

            this.onDragOver = function(event) {
                this._highlightSlot(event).then(function() {
                    if (!this._highlightedSlot || !this._highlightedSlot.isAllowed) {
                        return;
                    }

                    var slotId = componentHandlerService.getId(this._highlightedSlot.original);

                    // Check which component is highlighted
                    if (this._highlightedHint && this._isMouseInRegion(event, this._highlightedHint)) {
                        // If right hint is already highlighted don't do anything.
                        return;
                    } else if (this._highlightedHint) {
                        // Hint is not longer hovered.
                        this._clearHighlightedHint();
                    }

                    var cachedSlot = this._cachedSlots[slotId];
                    if (cachedSlot.components.length > 0) {
                        // Find the hovered component.
                        if (!this._highlightedComponent || !this._isMouseInRegion(event, this._highlightedComponent)) {
                            this._clearHighlightedComponent();

                            lodash.forEach(cachedSlot.components, function(component) {
                                if (this._isMouseInRegion(event, component)) {
                                    this._highlightedComponent = component;
                                    return false;
                                }
                            }.bind(this));
                        }

                        // Find the hint, if any, to highlight.
                        if (this._highlightedComponent) {
                            lodash.forEach(this._highlightedComponent.hints, function(hint) {
                                if (this._isMouseInRegion(event, hint)) {
                                    this._highlightedHint = hint;
                                    return false;
                                }
                            }.bind(this));
                        }
                    }

                    if (this._highlightedComponent && this._highlightedComponent.id === this._dragInfo.componentId) {
                        this._highlightedComponent.original.addClass(CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
                    } else if (this._highlightedHint) {
                        this._highlightedHint.original.addClass(CSS_CLASSES.DROPZONE_HOVERED);
                    }
                }.bind(this));

            }.bind(this);

            this.onDrop = function(event) {
                if (this._highlightedSlot) {
                    var sourceSlotId = this._dragInfo.slotId;
                    var targetSlotId = componentHandlerService.getId(this._highlightedSlot.original);
                    var sourceComponentId = this._dragInfo.componentId;
                    var componentType = this._dragInfo.componentType;

                    if (!this._highlightedSlot.isAllowed) {
                        var translation = $translate.instant("drag.and.drop.not.valid.component.type", {
                            slotUID: targetSlotId,
                            componentUID: sourceComponentId
                        });
                        alertService.pushAlerts([{
                            successful: false,
                            message: translation,
                            closeable: true
                        }]);

                        return this.onStop(event);
                    }
                    if (this._highlightedHint || this._highlightedSlot.components.length === 0) {
                        var position = (this._highlightedHint) ? this._highlightedHint.position : 0;
                        var result;

                        if (!sourceSlotId) {
                            if (!sourceComponentId) {
                                result = componentEditingFacade.addNewComponentToSlot(targetSlotId, componentType, position);
                            } else {
                                result = componentEditingFacade.addExistingComponentToSlot(targetSlotId, sourceComponentId, position);
                            }
                        } else {
                            if (sourceSlotId === targetSlotId) {
                                var currentComponentPos = this._getComponentPositionInSlot(sourceSlotId, sourceComponentId);
                                if (currentComponentPos < position) {
                                    // The current component will be removed from its current position, thus the target
                                    // position needs to take this into account. 
                                    position--;
                                }

                            }
                            result = componentEditingFacade.moveComponent(sourceSlotId, targetSlotId, sourceComponentId, position);
                        }

                        return result.then(function() {
                            this._scrollToModifiedSlot(targetSlotId);
                        }.bind(this), function() {
                            this.onStop(event);
                        }.bind(this));
                    }

                    return this.onStop(event);
                }
            }.bind(this);

            this.onDragLeave = function(event) {
                if (this._highlightedSlot) {
                    var slotId = componentHandlerService.getId(this._highlightedSlot.original);
                    var cachedSlot = this._cachedSlots[slotId];

                    if (!this._isMouseInRegion(event, cachedSlot)) {
                        this._clearHighlightedSlot();
                    }
                }
            }.bind(this);

            this.onStop = function(event) {
                var component = this._getSelector(event.target).closest(COMPONENT_SELECTOR);
                this._cleanDragOperation(component);
            }.bind(this);

            // Helpers
            this._initializeDragOperation = function(dragInfo) {
                this._dragInfo = dragInfo;
                this._cacheElements();

                // Prepare UI
                var overlay = componentHandlerService.getOverlay();
                overlay.addClass(CSS_CLASSES.OVERLAY_IN_DRAG_DROP);

                // Send an event to signal that the drag operation is started. Other pieces of SE, like contextual menus
                // need to be aware.
                systemEventService.sendAsynchEvent(DRAG_AND_DROP_EVENTS.DRAG_STARTED);
            };

            this._cleanDragOperation = function(draggedComponent) {
                this._clearHighlightedSlot();
                if (draggedComponent) {
                    draggedComponent.removeClass(CSS_CLASSES.COMPONENT_DRAGGED);
                }

                var overlay = componentHandlerService.getOverlay();
                overlay.removeClass(CSS_CLASSES.OVERLAY_IN_DRAG_DROP);
                systemEventService.sendAsynchEvent(DRAG_AND_DROP_EVENTS.DRAG_STOPPED);

                this._dragInfo = null;
                this._cachedSlots = {};
                this._highlightedSlot = null;
            };

            this._highlightSlot = function(event) {
                var slot = $(event.target).closest(SLOT_SELECTOR);
                var slotId = componentHandlerService.getId(slot);

                if (this._highlightedSlot) {
                    var oldSlotId = componentHandlerService.getId(this._highlightedSlot.original);

                    if (oldSlotId !== slotId) {
                        this._clearHighlightedSlot();
                    }
                }

                if (!this._highlightedSlot || this._highlightedSlot.isAllowed === undefined) {
                    this._highlightedSlot = this._cachedSlots[slotId];

                    return slotRestrictionsService.isComponentAllowedInSlot(this._highlightedSlot, this._dragInfo).then(function(isAllowed) {
                        // The highlighted slot might have changed while waiting for the promise to be resolved.
                        if (this._highlightedSlot && this._highlightedSlot.id === slotId) {
                            this._highlightedSlot.isAllowed = isAllowed;

                            if (isAllowed) {
                                slot.addClass(CSS_CLASSES.SLOT_ALLOWED);
                            } else {
                                slot.addClass(CSS_CLASSES.SLOT_NOT_ALLOWED);
                            }
                        }
                    }.bind(this));
                }

                return $q.when();
            };

            this._addUIHelpers = function() {
                var overlay = componentHandlerService.getOverlay();

                // First remove all dropzones.
                overlay.find('.' + CSS_CLASSES.UI_HELPER_OVERLAY).remove();

                overlay.find(SLOT_SELECTOR).each(function() {
                    var slot = $(this);
                    var slotHeight = slot.height();
                    var slotWidth = slot.width();

                    // Make a call to the restrictions service to cache the call if necessary.
                    // NOTE: This call only checks for the slot's restrictions, which don't change
                    // too often. So it makes sense to cache them.
                    var slotId = componentHandlerService.getId(slot);

                    slotRestrictionsService.getSlotRestrictions(slotId);

                    var components = slot.find(COMPONENT_SELECTOR);

                    if (components.length === 0) {
                        var uiHelperOverlay = $("<div></div>");
                        uiHelperOverlay.addClass(CSS_CLASSES.UI_HELPER_OVERLAY);

                        var uiHelper = $("<div></div>");
                        uiHelper.addClass(CSS_CLASSES.DROPZONE);
                        uiHelper.addClass(CSS_CLASSES.DROPZONE_FULL);

                        uiHelperOverlay.height(slotHeight);
                        uiHelperOverlay.width(slotWidth);

                        uiHelperOverlay.append(uiHelper);
                        slot.append(uiHelperOverlay);
                    } else {
                        components.each(function() {
                            var component = $(this);
                            var componentHeight = component.height();
                            var componentWidth = component.width();

                            var uiHelperOverlay = $("<div></div>");
                            uiHelperOverlay.addClass(CSS_CLASSES.UI_HELPER_OVERLAY);

                            uiHelperOverlay.height(componentHeight);
                            uiHelperOverlay.width(componentWidth);

                            var firstHelper = $('<div></div>');
                            var secondHelper = $('<div></div>');

                            firstHelper.addClass(CSS_CLASSES.DROPZONE);
                            secondHelper.addClass(CSS_CLASSES.DROPZONE);

                            if (componentWidth === slotWidth) {
                                firstHelper.addClass(CSS_CLASSES.DROPZONE_TOP);
                                secondHelper.addClass(CSS_CLASSES.DROPZONE_BOTTOM);
                            } else {
                                firstHelper.addClass(CSS_CLASSES.DROPZONE_LEFT);
                                secondHelper.addClass(CSS_CLASSES.DROPZONE_RIGHT);
                            }

                            uiHelperOverlay.append(firstHelper);
                            uiHelperOverlay.append(secondHelper);

                            component.append(uiHelperOverlay);
                        });
                    }
                });
            };

            this._cacheElements = function() {
                var overlay = componentHandlerService.getOverlay();

                var currentService = this;
                var scrollY = this._getWindowScrolling();

                overlay.children(SLOT_SELECTOR).each(function() {
                    var slot = $(this);
                    var slotId = componentHandlerService.getId(slot);

                    var cachedSlot = {
                        id: slotId,
                        original: slot,
                        components: [],
                        rect: currentService._getElementRects(slot, scrollY)
                    };

                    var components = slot.children(COMPONENT_SELECTOR);
                    if (components.length === 0) {
                        var hint = slot.find(HINT_SELECTOR);
                        cachedSlot.hint = (hint.length > 0) ? {
                            original: hint,
                            rect: currentService._getElementRects(hint, scrollY)
                        } : null;
                    } else {

                        var positionInSlot = 0;
                        components.each(function() {
                            var component = $(this);
                            var cachedComponent = {
                                id: componentHandlerService.getSlotOperationRelatedId(component),
                                type: componentHandlerService.getSlotOperationRelatedType(component),
                                original: component,
                                position: positionInSlot,
                                hints: [],
                                rect: currentService._getElementRects(component, scrollY)
                            };

                            var positionInComponent = positionInSlot++;
                            component.find(HINT_SELECTOR).each(function() {
                                var hint = $(this);
                                var cachedHint = {
                                    original: hint,
                                    position: positionInComponent++,
                                    rect: currentService._getElementRects(hint, scrollY)
                                };

                                cachedComponent.hints.push(cachedHint);
                            });

                            cachedSlot.components.push(cachedComponent);
                        });
                    }

                    currentService._cachedSlots[cachedSlot.id] = cachedSlot;
                });
            };

            this._clearHighlightedHint = function() {
                if (this._highlightedHint) {
                    this._highlightedHint.original.removeClass(CSS_CLASSES.DROPZONE_HOVERED);
                    this._highlightedHint = null;
                }
            };

            this._clearHighlightedComponent = function() {
                this._clearHighlightedHint();
                if (this._highlightedComponent) {
                    this._highlightedComponent.original.removeClass(CSS_CLASSES.COMPONENT_DRAGGED_HOVERED);
                    this._highlightedComponent = null;
                }
            };

            this._clearHighlightedSlot = function() {
                this._clearHighlightedComponent();

                if (this._highlightedSlot) {
                    this._highlightedSlot.original.removeClass(CSS_CLASSES.SLOT_ALLOWED);
                    this._highlightedSlot.original.removeClass(CSS_CLASSES.SLOT_NOT_ALLOWED);
                }

                this._highlightedSlot = null;
            };

            this._isMouseInRegion = function(event, element) {
                var boundingRect = element.rect;

                return (event.pageX >= boundingRect.left && event.pageX <= boundingRect.right && event.pageY >= boundingRect.top && event.pageY <= boundingRect.bottom);
            };

            this._getElementRects = function(element, scrollY) {
                var baseRect = element[0].getBoundingClientRect();
                var rect = {
                    left: baseRect.left,
                    right: baseRect.right,
                    bottom: baseRect.bottom + scrollY,
                    top: baseRect.top + scrollY
                };

                return rect;
            };

            this._getWindowScrolling = function() {
                return ($window.scrollY || $window.pageYOffset);
            };

            this._getComponentPositionInSlot = function(slotId, componentId) {
                var components = this._cachedSlots[slotId].components;

                var result = lodash.takeWhile(components, function(comp) {
                    return comp.id !== componentId;
                });

                return (result.length !== components.length) ? result.length : -1;
            };

            this._scrollToModifiedSlot = function(componentId) {
                var component = componentHandlerService.getComponentInOverlay(componentId, CONTENT_SLOT_TYPE);
                if (component && component.length > 0) {
                    component[0].scrollIntoView();
                }
            };

            this._getSelector = function(selector) {
                return $(selector);
            };

        });
})();
