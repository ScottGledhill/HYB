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
angular.module('cmssmarteditContainer', [
        'experienceInterceptorModule',
        'resourceLocationsModule',
        'cmssmarteditContainerTemplates',
        'featureServiceModule',
        'componentMenuModule',
        'cmscommonsTemplates',
        'restrictionsMenuModule',
        'pageInfoMenuModule',
        'editorModalServiceModule',
        'genericEditorModule',
        'eventServiceModule',
        'catalogDetailsModule',
        'synchronizeCatalogModule',
        'perspectiveServiceModule',
        'pageListLinkModule',
        'pageListControllerModule',
        'clientPagedListModule',
        'assetsServiceModule',
        'navigationEditorModule',
        'cmsDragAndDropServiceModule',
        'seMediaFieldModule',
        'seMediaContainerFieldModule',
        'editorFieldMappingServiceModule',
        'navigationNodeEditorModule',
        'entrySearchSelectorModule',
        'pageRestrictionsModule',
        'pageRestrictionsEditorModule',
        'yActionableSearchItemModule',
        'seNavigationNodeSelector',
        'pageSyncMenuToolbarItemModule',
        'synchronizationPollingServiceModule',
        'productSelectorModule',
        'categorySelectorModule'

    ])
    .config(function(PAGE_LIST_PATH, NAVIGATION_MANAGEMENT_PAGE_PATH, $routeProvider) {
        $routeProvider.when(PAGE_LIST_PATH, {
            templateUrl: 'web/features/cmssmarteditContainer/components/pages/pageList/pageListTemplate.html',
            controller: 'pageListController',
            controllerAs: 'pageListCtl'
        });
        $routeProvider.when(NAVIGATION_MANAGEMENT_PAGE_PATH, {
            templateUrl: 'web/features/cmssmarteditContainer/components/navigation/navigationEditor/navigationTemplate.html',
            controller: 'navigationController',
            controllerAs: 'nav'
        });
    })
    .controller('navigationController', function($routeParams, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var uriContext = {};
        uriContext[CONTEXT_SITE_ID] = $routeParams.siteId;
        uriContext[CONTEXT_CATALOG] = $routeParams.catalogId;
        uriContext[CONTEXT_CATALOG_VERSION] = $routeParams.catalogVersion;
        this.uriContext = uriContext;
    })
    .run(
        function($log, $rootScope, $routeParams, NAVIGATION_MANAGEMENT_PAGE_PATH, ComponentService, systemEventService, catalogDetailsService, featureService, perspectiveService, assetsService, editorFieldMappingService, cmsDragAndDropService, editorModalService) {

            // Add the mapping for the generic editor.
            editorFieldMappingService.addFieldMapping('EntrySearchSelector', null, null, {
                template: 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/entrySearchSelectorTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('Media', null, null, {
                template: 'web/features/cmssmarteditContainer/components/genericEditor/media/templates/mediaTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MediaContainer', null, null, {
                template: 'web/features/cmssmarteditContainer/components/genericEditor/media/templates/mediaContainerTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('NavigationNodeSelector', null, null, {
                template: 'web/features/cmssmarteditContainer/components/genericEditor/navigationNode/templates/navigationNodeSelectorWrapperTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MultiProductSelector', null, null, {
                template: 'web/features/cmssmarteditContainer/components/genericEditor/catalog/components/productSelector/multiProductSelectorTemplate.html'
            });

            editorFieldMappingService.addFieldMapping('MultiCategorySelector', null, null, {
                template: 'web/features/cmssmarteditContainer/components/genericEditor/catalog/components/categorySelector/multiCategorySelectorTemplate.html'
            });

            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'se.cms.componentMenuTemplate',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'cms.toolbaritem.componentmenutemplate.name',
                descriptionI18nKey: 'cms.toolbaritem.componentmenutemplate.description',
                priority: 1,
                section: 'left',
                callback: function() {
                    systemEventService.sendSynchEvent('ySEComponentMenuOpen', {});
                },
                include: 'web/features/cmssmarteditContainer/components/cmsComponents/componentMenu/componentMenuTemplate.html'
            });

            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'se.cms.restrictionsMenu',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'page.restrictions.toolbar.menu',
                priority: 2,
                section: 'left',
                include: 'web/features/cmssmarteditContainer/components/pageRestrictions/pageRestrictionsMenu/pageRestrictionsMenuToolbarItemWrapperTemplate.html'
            });

            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'se.cms.pageInfoMenu',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'cms.toolbarItem.pageInfoMenu.name',
                descriptionI18nKey: 'cms.toolbarItem.pageInfoMenu.description',
                priority: 3,
                section: 'left',
                include: 'web/features/cmssmarteditContainer/components/pages/pageInfoMenu/pageInfoMenuToolbarItemWrapperTemplate.html'
            });

            featureService.addToolbarItem({
                toolbarId: 'experienceSelectorToolbar',
                key: 'se.cms.pageSyncMenu',
                type: 'HYBRID_ACTION',
                nameI18nKey: 'cms.toolbaritem.pagesyncmenu.name',
                descriptionI18nKey: 'cms.toolbaritem.pagesyncmenu.description',
                priority: 4,
                section: 'left',
                include: 'web/features/cmssmarteditContainer/components/synchronize/pages/syncMenu/pageSyncMenuToolbarItemWrapperTemplate.html'
            });

            catalogDetailsService.addItems([{
                include: 'web/features/cmssmarteditContainer/components/pages/pageList/pageListLinkTemplate.html'
            }]);

            catalogDetailsService.addItems([{
                include: 'web/features/cmssmarteditContainer/components/navigation/navigationEditor/navigationEditorLinkTemplate.html'
            }]);

            catalogDetailsService.addItems([{
                include: 'web/features/cmssmarteditContainer/components/synchronize/catalogs/catalogDetailsSyncTemplate.html'
            }]);

            featureService.register({
                key: 'se.cms.html5DragAndDrop.outer',
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

            perspectiveService.register({
                key: 'se.cms.perspective.basic',
                nameI18nKey: 'se.cms.perspective.basic.name',
                descriptionI18nKey: 'se.cms.perspective.basic.description',
                features: ['se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.pageInfoMenu', 'se.cms.navigationMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'se.cms.html5DragAndDrop.outer'],
                perspectives: []
            });

            /* Note: For advance edit mode, the ordering of the entries in the features list will determine the order the buttons will show in the slot contextual menu */
            perspectiveService.register({
                key: 'se.cms.perspective.advanced',
                nameI18nKey: 'se.cms.perspective.advanced.name',
                descriptionI18nKey: 'se.cms.perspective.advanced.description',
                features: ['se.slotContextualMenu', 'se.slotSyncButton', 'se.slotSharedButton', 'se.slotContextualMenuVisibility', 'se.contextualMenu', 'se.cms.dragandropbutton', 'se.cms.remove', 'se.cms.edit', 'se.cms.componentMenuTemplate', 'se.cms.restrictionsMenu', 'se.cms.pageInfoMenu', 'se.cms.pageSyncMenu', 'se.cms.navigationMenu', 'se.emptySlotFix', 'se.cms.html5DragAndDrop', 'se.cms.html5DragAndDrop.outer', 'syncIndicator'],
                perspectives: []
            });

        });
