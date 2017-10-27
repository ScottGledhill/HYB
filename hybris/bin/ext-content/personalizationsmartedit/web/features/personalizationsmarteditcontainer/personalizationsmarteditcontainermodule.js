jQuery(document).ready(function($) {

    var loadCSS = function(href) {
        var cssLink = $("<link rel='stylesheet' type='text/css' href='" + href + "'>");
        $("head").append(cssLink);
    };

    loadCSS("/personalizationsmartedit/css/style.css");

});

angular.module('personalizationsmarteditcontainermodule', [
        'personalizationsmarteditcontainerTemplates',
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditRestServiceModule',
        'ui.bootstrap',
        'personalizationsmarteditCommons',
        'functionsModule',
        'personalizationsmarteditPreviewServiceModule',
        'personalizationsmarteditManagerModule',
        'personalizationsmarteditManagerViewModule',
        'personalizationsmarteditContextMenu',
        'personalizationsmarteditPageCustomizationsToolbarItemModule',
        'featureServiceModule',
        'perspectiveServiceModule',
        'iFrameManagerModule',
        'personalizationsmarteditCombinedViewModule',
        'personalizationsmarteditPromotionModule'
    ])
    .factory('personalizationsmarteditIFrameUtils', function($filter, iFrameManager, personalizationsmarteditContextService, personalizationsmarteditPreviewService, personalizationsmarteditMessageHandler) {
        var iframeUtils = {};

        iframeUtils.reloadPreview = function(resourcePath, previewTicketId) {
            iFrameManager.loadPreview(resourcePath, previewTicketId);
        };

        iframeUtils.clearAndReloadPreview = function(currentVariations) {
            if (angular.isObject(currentVariations) && !angular.isArray(currentVariations)) {
                var previewTicketId = personalizationsmarteditContextService.getSePreviewData().previewTicketId;
                personalizationsmarteditPreviewService.removePersonalizationDataFromPreview(previewTicketId).then(function successCallback() {
                    var previewData = personalizationsmarteditContextService.getSePreviewData();
                    iframeUtils.reloadPreview(previewData.resourcePath, previewData.previewTicketId);
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingpreviewticket'));
                });
            }
        };

        return iframeUtils;
    })
    .controller('topToolbarMenuController', function($scope, personalizationsmarteditManager, personalizationsmarteditManagerView, personalizationsmarteditIFrameUtils, personalizationsmarteditContextService, personalizationsmarteditUtils, personalizationsmarteditCombinedView) {
        $scope.status = {
            isopen: false
        };

        $scope.preventDefault = function(oEvent) {
            oEvent.stopPropagation();
        };

        $scope.createCustomizationClick = function() {
            personalizationsmarteditManager.openCreateCustomizationModal();
        };

        $scope.managerViewClick = function() {
            personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
            personalizationsmarteditUtils.clearCombinedView(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
            personalizationsmarteditManagerView.openManagerAction();
        };

        $scope.combinedViewClick = function() {
            personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
            personalizationsmarteditCombinedView.openManagerAction();
        };

        $scope.clearContext = function() {
            personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
        };

    })
    .run(
        function($rootScope, personalizationsmarteditContextService, personalizationsmarteditContextServiceReverseProxy, personalizationsmarteditContextModal, featureService, perspectiveService, personalizationsmarteditIFrameUtils, personalizationsmarteditUtils) {
            var PersonalizationviewContextServiceReverseProxy = new personalizationsmarteditContextServiceReverseProxy('PersonalizationCtxReverseGateway');

            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'personalizationsmartedit.container.pagecustomizations.toolbar',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'personalization.toolbar.pagecustomizations',
                priority: 4,
                section: 'left',
                include: 'web/features/personalizationsmarteditcontainer/pageCustomizationsToolbar/personalizationsmarteditPageCustomizationsToolbarItemWrapperTemplate.html'
            });
            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'personalizationsmartedit.container.combinedview.toolbar',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'personalization.toolbar.combinedview.name',
                priority: 6,
                section: 'left',
                include: 'web/features/personalizationsmarteditcontainer/combinedView/personalizationsmarteditCombinedViewMenuTemplate.html'
            });
            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'personalizationsmartedit.container.manager.toolbar',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'personalization.toolbar.library.name',
                priority: 8,
                section: 'left',
                include: 'web/features/personalizationsmarteditcontainer/management/personalizationsmarteditCustomizationManagMenuTemplate.html'
            });
            featureService.register({
                key: 'personalizationsmartedit.context.service',
                nameI18nKey: 'personalization.context.service.name',
                descriptionI18nKey: 'personalization.context.service.description',
                enablingCallback: function() {
                    personalizationsmarteditContextService.setPersonalizationContextEnabled(true);
                },
                disablingCallback: function() {
                    personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
                    personalizationsmarteditContextService.setPersonalizationContextEnabled(false);
                }
            });

            perspectiveService.register({
                key: 'personalizationsmartedit.perspective',
                nameI18nKey: 'personalization.perspective.name',
                descriptionI18nKey: 'personalization.perspective.description',
                features: ['personalizationsmartedit.context.service',
                    'personalizationsmartedit.container.pagecustomizations.toolbar',
                    'personalizationsmartedit.container.manager.toolbar',
                    'personalizationsmartedit.container.combinedview.toolbar',
                    'personalizationsmarteditComponentLightUp',
                    'personalizationsmarteditCombinedViewComponentLightUp',
                    'personalizationsmartedit.context.add.action',
                    'personalizationsmartedit.context.edit.action',
                    'personalizationsmartedit.context.delete.action',
                    'personalizationsmartedit.context.info.action',
                    'personalizationsmartedit.context.component.edit.action',
                    'personalizationsmartedit.innerapp.clickevent',
                    'se.contextualMenu',
                    'se.emptySlotFix'
                ],
                perspectives: []
            });

            $rootScope.$on('$locationChangeSuccess', function() {
                personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
            });

        });
