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
angular.module('cmssmartedit', [
        'resourceLocationsModule',
        'decoratorServiceModule',
        'contextualMenuServiceModule',
        'removeComponentServiceModule',
        'experienceInterceptorModule',
        'editorEnablerServiceModule',
        'alertServiceModule',
        'translationServiceModule',
        'featureServiceModule',
        'slotVisibilityButtonModule',
        'slotVisibilityServiceModule',
        'cmssmarteditTemplates',
        'cmscommonsTemplates',
        'componentHandlerServiceModule',
        'assetsServiceModule',
        'slotSharedButtonModule',
        'cmsDragAndDropServiceModule',
        'syncIndicatorDecoratorModule',
        'slotSyncButtonModule',
        'synchronizationPollingServiceModule'
    ])
    .run(
        // Note: only instances can be injected in a run function
        function($rootScope, $translate, decoratorService, contextualMenuService, alertService, removeComponentService, editorEnablerService, featureService, componentHandlerService, assetsService, slotVisibilityService, cmsDragAndDropService) {

            var retriggerMouseEvent = function(element, eventType, originalEvent) {
                // Not using jQuery trigger since it was not working as expected.
                var event;
                if (typeof window.Event == "function") {
                    event = new MouseEvent(eventType, {
                        "bubbles": true,
                        "cancelable": false,
                        "clientX": originalEvent.clientX,
                        "clientY": originalEvent.clientY,
                        "offsetX": originalEvent.offsetX,
                        "offsetY": originalEvent.offsetY,
                        "pageX": originalEvent.pageX,
                        "pageY": originalEvent.pageY,
                        "screenX": originalEvent.screenX,
                        "screenY": originalEvent.screenY,
                        "view": originalEvent.view
                    });
                } else {
                    // IE Fix
                    event = document.createEvent("MouseEvents");
                    event.initMouseEvent(
                        eventType,
                        true, // can bubble
                        false, // cancelable
                        originalEvent.view, // viewArg
                        0, // detailArg
                        originalEvent.offsetX, // screenX
                        originalEvent.offsetY, // screenY
                        originalEvent.clientX, // clientX
                        originalEvent.clientY, // clientY
                        false, // ctrlKeyArg
                        false, // altKeyArg
                        false, // shiftKeyArg
                        false, // metaKeyArg
                        0, // buttonArg
                        null); // relatedTargetArg
                }

                element.dispatchEvent(event);
            };

            editorEnablerService.enableForComponents(['^.*Component$']);

            decoratorService.addMappings({
                '^((?!Slot).)*$': ['se.contextualMenu'],
                '^.*Slot$': ['se.slotContextualMenu', 'se.basicSlotContextualMenu', 'syncIndicator']
            });

            featureService.addContextualMenuButton({
                key: 'se.cms.dragandropbutton',
                nameI18nKey: 'contextmenu.title.dragndrop',
                i18nKey: 'contextmenu.title.dragndrop',
                regexpKeys: ['^((?!Slot).)*$'],
                condition: function(componentType, componentId) {
                    return true;
                },
                callback: function() {},
                callbacks: {
                    'mousedown': function(configuration, $event) {
                        cmsDragAndDropService.update();
                    }

                },
                displayClass: 'movebutton',
                iconIdle: assetsService.getAssetsRoot() + '/images/contextualmenu_move_off.png',
                iconNonIdle: assetsService.getAssetsRoot() + '/images/contextualmenu_move_on.png',
                smallIcon: assetsService.getAssetsRoot() + '/images/contextualmenu_move_on.png'
            });

            featureService.register({
                key: 'se.cms.html5DragAndDrop',
                nameI18nKey: 'se.cms.dragAndDrop.name',
                descriptionI18nKey: 'se.cms.dragAndDrop.description',
                enablingCallback: function() {
                    cmsDragAndDropService.register();
                    cmsDragAndDropService.apply();
                },
                disablingCallback: function() {
                    cmsDragAndDropService.unregister();
                }
            });

            featureService.addContextualMenuButton({
                key: 'se.cms.remove',
                i18nKey: 'contextmenu.title.remove',
                nameI18nKey: 'contextmenu.title.remove',
                regexpKeys: ['^((?!Slot).)*$'],
                condition: function(configuration) {
                    return true;
                },
                callback: function(configuration, $event) {
                    var element = componentHandlerService.getOriginalComponent(configuration.componentId, configuration.componentType).get(0);
                    var slotOperationRelatedId = componentHandlerService.getSlotOperationRelatedId(element);
                    var slotOperationRelatedType = componentHandlerService.getSlotOperationRelatedType(element);

                    removeComponentService.removeComponent({
                        slotId: configuration.slotId,
                        componentId: configuration.componentId,
                        componentType: configuration.componentType,
                        slotOperationRelatedId: slotOperationRelatedId,
                        slotOperationRelatedType: slotOperationRelatedType,
                    }).then(
                        function() {
                            $translate('alert.component.removed.from.slot', {
                                componentID: slotOperationRelatedId,
                                slotID: configuration.slotId
                            }).then(function(translation) {
                                alertService.pushAlerts([{
                                    successful: true,
                                    message: translation,
                                    closeable: true
                                }]);
                                $event.preventDefault();
                                $event.stopPropagation();
                            });
                        }
                    );
                },
                displayClass: 'removebutton',
                iconIdle: assetsService.getAssetsRoot() + '/images/contextualmenu_delete_off.png',
                iconNonIdle: assetsService.getAssetsRoot() + '/images/contextualmenu_delete_on.png',
                smallIcon: assetsService.getAssetsRoot() + '/images/contextualmenu_delete_on.png'
            });

            featureService.addContextualMenuButton({
                key: 'se.slotContextualMenuVisibility',
                nameI18nKey: 'slotcontextmenu.title.visibility',
                regexpKeys: ['^.*ContentSlot$'],
                callback: function() {},
                templateUrl: 'web/features/cmssmartedit/components/slotVisibility/slotVisibilityWidgetTemplate.html'
            });

            featureService.addContextualMenuButton({
                key: 'se.slotSharedButton',
                nameI18nKey: 'slotcontextmenu.title.shared.button',
                regexpKeys: ['^.*Slot$'],
                callback: function() {},
                templateUrl: 'web/features/cmssmartedit/components/slotShared/slotSharedTemplate.html'
            });

            featureService.addContextualMenuButton({
                key: 'se.slotSyncButton',
                nameI18nKey: 'slotcontextmenu.title.sync.button',
                regexpKeys: ['^.*Slot$'],
                callback: function() {},
                templateUrl: 'web/features/cmssmartedit/components/synchronize/slots/slotSyncTemplate.html'
            });


            featureService.addDecorator({
                key: 'syncIndicator',
                nameI18nKey: 'syncIndicator'
            });


        });
