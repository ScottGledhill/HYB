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
/**
 * @ngdoc overview
 * @name pageEditorModalServiceModule
 * @description
 * # The pageEditorModalServiceModule
 *
 * The page editor modal service module provides a service that allows opening an editor modal for a given page. The editor modal is populated with a save and cancel button, and is loaded with the
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given page.
 */
angular.module('pageEditorModalServiceModule', ['genericEditorModalServiceModule', 'editPageBasicTabModule', 'editPageVisibilityTabModule', 'displayConditionsTabModule', 'pageServiceModule'])
    /**
     * @ngdoc service
     * @name pageEditorModalServiceModule.service:pageEditorModalService
     *
     * @description
     * Convenience service to open an editor modal window for a given page's data.
     *
     */
    .factory('pageEditorModalService', function(genericEditorModalService, pageService) {

        function PageEditorModalService() {}

        var PAGE_INFO_TAB_CONFIGURATION = {
            id: 'editPageBasicTab',
            title: 'se.pageinfo.information.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/pages/editPageModal/tabs/basic/editPageBasicTabTemplate.html'
        };

        var DISPLAY_CONDITIONS_TAB_CONFIGURATION = {
            id: 'editPageDisplayConditionsTab',
            title: 'se.pageinfo.display.conditions.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/pages/editPageModal/tabs/displayConditions/displayConditionsTabTemplate.html'
        };

        var RESTRICTIONS_TAB_CONFIGURATION = {
            id: 'editPageVisibilityTab',
            title: 'page.restrictions.editor.tab',
            templateUrl: 'web/features/cmssmarteditContainer/components/pages/editPageModal/tabs/visibility/editPageVisibilityTabTemplate.html'
        };

        var STATIC_TABS = [PAGE_INFO_TAB_CONFIGURATION, DISPLAY_CONDITIONS_TAB_CONFIGURATION];

        /**
         * @ngdoc method
         * @name pageEditorModalServiceModule.service:pageEditorModalService#open
         * @methodOf pageEditorModalServiceModule.service:pageEditorModalService
         *
         * @description
         * Uses the {@link genericEditorModalService.open genericEditorModalService} to open an editor modal.
         *
         * The editor modal is initialized with a title in the format '<TypeName> Editor', ie: 'Paragraph Editor'. The
         * editor modal is also wired with a save and cancel button.
         *
         * The content of the editor modal is the {@link editorTabsetModule.directive:editorTabset editorTabset}.
         *
         * @param {Object} page The data associated to a page as defined in the platform.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        PageEditorModalService.prototype.open = function(page) {
            return pageService.isPagePrimary(page.uid).then(function(isPagePrimary) {
                var tabs = STATIC_TABS.concat(isPagePrimary ? [] : [RESTRICTIONS_TAB_CONFIGURATION]);
                var editorModalConfiguration = {
                    uid: page.uid,
                    typeCode: page.typeCode,
                    uriContext: page.uriContext,
                    title: 'pageeditormodal.editpagetab.title',
                    template: page.template,
                    onlyOneRestrictionMustApply: page.onlyOneRestrictionMustApply
                };

                return genericEditorModalService.open(editorModalConfiguration, tabs, function() {});
            });
        };

        return new PageEditorModalService();
    });
