jQuery(document).ready(function($) {

    var loadCSS = function(href) {
        var cssLink = $("<link rel='stylesheet' type='text/css' href='" + href + "'>");
        $("head").append(cssLink);
    };

    loadCSS("/personalizationsmartedit/css/style.css");

});

angular.module('personalizationsmarteditmodule', [
        'decoratorServiceModule',
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditComponentLightUpDecorator',
        'personalizationsmarteditCombinedViewComponentLightUpDecorator',
        'personalizationsmarteditContextMenu',
        'personalizationsmarteditCommons',
        'featureServiceModule'
    ])
    .directive('body', function(personalizationsmarteditContextService) {
        return {
            link: function(scope, element, attrs) {
                scope.$watch(
                    function() {
                        return element.attr('data-smartedit-ready');
                    },
                    function(newValue, oldValue) {
                        if (newValue !== oldValue && (newValue === true || newValue === "true")) {
                            personalizationsmarteditContextService.applySynchronization();
                        }
                    }, true);
                scope.$watch('element.class', function() {
                    var pageIdArray = element.attr('class').split(" ").filter(function(elem) {
                        return /smartedit-page-uid\-(\S+)/.test(elem);
                    });
                    if (pageIdArray.length > 0) {
                        var pageId = /smartedit-page-uid\-(\S+)/.exec(pageIdArray[0])[1];
                        personalizationsmarteditContextService.setPageId(pageId);
                        if (pageIdArray.length > 1) {
                            console.log("more than one page- class element attribute defined");
                        }
                    }
                }, true);

            }
        };
    })
    .run(
        function($document, decoratorService, personalizationsmarteditContextService, personalizationsmarteditContextServiceProxy, personalizationsmarteditContextModal, featureService, personalizationsmarteditUtils) {
            var PersonalizationviewContextServiceProxy = new personalizationsmarteditContextServiceProxy('PersonalizationCtxGateway');

            decoratorService.addMappings({
                '^.*Component$': ['personalizationsmarteditComponentLightUp', 'personalizationsmarteditCombinedViewComponentLightUp']
            });

            featureService.addDecorator({
                key: 'personalizationsmarteditComponentLightUp',
                nameI18nKey: 'personalizationsmarteditComponentLightUp'
            });

            featureService.addDecorator({
                key: 'personalizationsmarteditCombinedViewComponentLightUp',
                nameI18nKey: 'personalizationsmarteditCombinedViewComponentLightUp'
            });

            var clickEventHandler = function(event) {
                personalizationsmarteditContextService.closeCustomizeDropdowns();
            };
            featureService.register({
                key: 'personalizationsmartedit.innerapp.clickevent',
                nameI18nKey: 'personalization.innerapp.clickevent',
                enablingCallback: function() {
                    $document.on('click', clickEventHandler);
                },
                disablingCallback: function() {
                    $document.off('click', clickEventHandler);
                }
            });

            featureService.addContextualMenuButton({
                key: "personalizationsmartedit.context.add.action",
                i18nKey: 'personalization.context.action.add',
                nameI18nKey: 'personalization.context.action.add',
                regexpKeys: ['^.*Component$'],
                condition: function(config) {
                    return personalizationsmarteditContextService.isContextualMenuAddItemEnabled(config.element);
                },
                callback: function(config, $event) {
                    var configProperties = angular.fromJson(config.properties);
                    var actionId = configProperties.smarteditPersonalizationActionId || null;
                    personalizationsmarteditContextModal.openAddAction(config.componentType, config.componentId, config.containerId, config.slotId, actionId);
                },
                displayClass: '',
                iconIdle: '/personalizationsmartedit/icons/contextualmenu_add_off.png',
                iconNonIdle: '/personalizationsmartedit/icons/contextualmenu_add_on.png',
                smallIcon: '/personalizationsmartedit/icons/contextualmenu_add_on.png'
            });
            featureService.addContextualMenuButton({
                key: "personalizationsmartedit.context.edit.action",
                i18nKey: 'personalization.context.action.edit',
                nameI18nKey: 'personalization.context.action.edit',
                regexpKeys: ['^.*Component$'],
                condition: function(config) {
                    return personalizationsmarteditContextService.isContextualMenuEditItemEnabled(config.element);
                },
                callback: function(config, $event) {
                    var configProperties = angular.fromJson(config.properties);
                    var actionId = configProperties.smarteditPersonalizationActionId || null;
                    personalizationsmarteditContextModal.openEditAction(config.componentType, config.componentId, config.containerId, config.slotId, actionId);
                },
                displayClass: '',
                iconIdle: '/personalizationsmartedit/icons/contextualmenu_edit_off.png',
                iconNonIdle: '/personalizationsmartedit/icons/contextualmenu_edit_on.png',
                smallIcon: '/personalizationsmartedit/icons/contextualmenu_edit_on.png'
            });
            featureService.addContextualMenuButton({
                key: "personalizationsmartedit.context.delete.action",
                i18nKey: 'personalization.context.action.delete',
                nameI18nKey: 'personalization.context.action.delete',
                regexpKeys: ['^.*Component$'],
                condition: function(config) {
                    return personalizationsmarteditContextService.isContextualMenuDeleteItemEnabled(config.element);
                },
                callback: function(config, $event) {
                    var configProperties = angular.fromJson(config.properties);
                    var actionId = configProperties.smarteditPersonalizationActionId || null;
                    personalizationsmarteditContextModal.openDeleteAction(config.componentType, config.componentId, config.containerId, config.slotId, actionId);
                },
                displayClass: '',
                iconIdle: '/personalizationsmartedit/icons/contextualmenu_delete_off.png',
                iconNonIdle: '/personalizationsmartedit/icons/contextualmenu_delete_on.png',
                smallIcon: '/personalizationsmartedit/icons/contextualmenu_delete_on.png'
            });
            featureService.addContextualMenuButton({
                key: "personalizationsmartedit.context.info.action",
                i18nKey: 'personalization.context.action.info',
                nameI18nKey: 'personalization.context.action.info',
                regexpKeys: ['^.*Component$'],
                condition: function(config) {
                    return personalizationsmarteditContextService.isContextualMenuInfoItemEnabled(config.element);
                },
                callback: function(config, $event) {
                    personalizationsmarteditContextModal.openInfoAction();
                },
                displayClass: '',
                iconIdle: '/personalizationsmartedit/icons/contextualmenu_info_off.png',
                iconNonIdle: '/personalizationsmartedit/icons/contextualmenu_info_on.png',
                smallIcon: '/personalizationsmartedit/icons/contextualmenu_info_on.png'
            });
            featureService.addContextualMenuButton({
                key: "personalizationsmartedit.context.component.edit.action",
                i18nKey: 'personalization.context.component.action.edit',
                nameI18nKey: 'personalization.context.component.action.edit',
                regexpKeys: ['^.*Component$'],
                condition: function(config) {
                    return personalizationsmarteditContextService.isContextualMenuEditComponentItemEnabled(config.element);
                },
                callback: function(config, $event) {
                    personalizationsmarteditContextModal.openEditComponentAction(config.componentType, config.componentId);
                },
                displayClass: '',
                iconIdle: '/cmssmartedit/images/contextualmenu_edit_off.png',
                iconNonIdle: '/cmssmartedit/images/contextualmenu_edit_on.png',
                smallIcon: '/cmssmartedit/images/contextualmenu_edit_on.png'
            });

        });
