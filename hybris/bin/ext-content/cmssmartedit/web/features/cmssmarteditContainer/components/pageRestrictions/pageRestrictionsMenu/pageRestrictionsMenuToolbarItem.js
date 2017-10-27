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
angular.module('restrictionsMenuModule', ['assetsServiceModule', 'restrictionsTableModule', 'restrictionsPageInfoModule', 'eventServiceModule', 'iframeClickDetectionServiceModule', 'pageRestrictionsModule', 'displayConditionsFacadeModule'])

.controller('toolbarItemController', function(assetsService, systemEventService, iframeClickDetectionService, $element, $document, $scope) {

    this.init = function(config) {

        this.isOpen = false;
        this.label = config.label || '';
        this.icons = config.icons || {};
        this.onOpen = config.onOpen;
        this.menuIcon = this.icons.closed;

        $scope.$watch('ctrl.isOpen', function() {
            if (this.isOpen) {
                this.onOpen();
                this.menuIcon = this.icons && this.icons.open;
                $element.closest('.ySEHybridAction').addClass("ySEOpenComponent");
            } else {
                this.menuIcon = this.icons && this.icons.closed;
                $element.closest('.ySEHybridAction').removeClass("ySEOpenComponent");
            }
        }.bind(this));

    };

    $document.on('click', function(event) {

        if ($(event.target).parents('.ySEComponentMenuW').length === 0 && this.isOpen) {
            this.isOpen = false;
            $scope.$apply();
        }

    }).bind(this);

    this.isOpen = false;

    iframeClickDetectionService.registerCallback('closeToolbarMenu', function() {
        $scope.$apply();
    }.bind(this));

    this.preventDefault = function(event) {
        event.stopPropagation();
        event.preventDefault();
    };

})

.controller('restrictionsMenuToolbarItemController', function($scope, $element, $controller, assetsService, componentHandlerService, pageListService, pageRestrictionsFacade, displayConditionsFacade) {

    angular.extend(this, $controller('toolbarItemController', {
        $scope: $scope,
        $element: $element
    }));

    this.init({
        label: 'page.restrictions.toolbar.menu',
        icons: {
            open: assetsService.getAssetsRoot() + "/images/icon_restrictions_white.png",
            closed: assetsService.getAssetsRoot() + "/images/icon_restrictions_blue.png"
        },
        onOpen: function() {

            var pageUID = componentHandlerService.getPageUID();

            pageRestrictionsFacade.getRestrictionsByPageUID(pageUID).then(function(restrictions) {
                this.restrictions = restrictions;
            }.bind(this));

            pageListService.getPageById(pageUID).then(function(page) {

                this.pageId = page && page.uid;
                this.pageNameLabelI18nKey = "se.label.page.name";
                this.pageName = page && page.name;

                displayConditionsFacade.isPagePrimary(this.pageId).then(function(isPrimary) {
                    this.pageIsPrimary = isPrimary;
                    if (!this.pageIsPrimary) {
                        displayConditionsFacade.getPrimaryPageForVariationPage(this.pageId).then(function(primaryPageData) {
                            this.associatedPrimaryPageName = primaryPageData.name;
                        }.bind(this));
                    }
                }.bind(this));

                this.restrictionCriteria = pageRestrictionsFacade.getRestrictionCriteriaOptionFromPage(page);

            }.bind(this));

        }
    });
})

.directive('restrictionsMenuToolbarItem', function() {
    return {
        templateUrl: 'web/features/cmssmarteditContainer/components/pageRestrictions/pageRestrictionsMenu/pageRestrictionsMenuToolbarItemTemplate.html',
        restrict: 'E',
        controller: 'restrictionsMenuToolbarItemController',
        controllerAs: 'ctrl',
        scope: {},
        bindToController: {}
    };
});
