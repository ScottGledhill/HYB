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
 * @name pageInfoMenuModule
 * @description
 *
 * The page info menu module contains the directive and controller necessary to view the page information menu from the white ribbon..
 *
 * Use the {@link pageInfoMenuModule.directive:pageInfoMenuToolbarItem pageInfoMenuToolbarItem} add this page info toolbar menu.
 *
 */
angular.module('pageInfoMenuModule', ['assetsServiceModule', 'eventServiceModule', 'iframeClickDetectionServiceModule', 'componentHandlerServiceModule', 'pageListServiceModule', 'pageInfoContainerModule', 'languageServiceModule', 'yLoDashModule', 'sharedDataServiceModule'])
    .controller('pageInfoMenuToolbarItemController', function(assetsService, systemEventService, iframeClickDetectionService, $document, $scope, $element) {
        this.$onInit = function() {
            this.isOpen = false;
            this.label = 'se.pageinfo.menu.btn.label';
            this.icons = {
                open: assetsService.getAssetsRoot() + "/images/icon_info_white.png",
                closed: assetsService.getAssetsRoot() + "/images/icon_info_blue.png"
            };
            this.menuIcon = this.icons.closed;


            $scope.$watch('$ctrl.isOpen', function() {
                if (this.isOpen) {
                    this.menuIcon = this.icons && this.icons.open;
                    $element.closest('.ySEHybridAction').addClass("ySEOpenComponent");
                } else {
                    this.menuIcon = this.icons && this.icons.closed;
                    $element.closest('.ySEHybridAction').removeClass("ySEOpenComponent");
                }
            }.bind(this));

            $document.on('click', function(event) {
                if ($(event.target).parents('.ySEComponentMenuW').length === 0 && this.isOpen) {
                    this.isOpen = false;
                    $scope.$apply();
                }
            }.bind(this));

            iframeClickDetectionService.registerCallback('closeToolbarMenu', function() {
                this.isOpen = false;
                $scope.$apply();
            }.bind(this));
        };

        this.toggleMenu = function() {
            this.isOpen = !this.isOpen;
        };

        this.preventDefault = function(event) {
            event.stopPropagation();
            event.preventDefault();
        };
    })
    /**
     * @ngdoc directive
     * @name pageInfoMenuModule.directive:pageInfoMenuToolbarItem
     * @scope
     * @restrict E
     * @element page-info-menu-toolbar-item
     *
     * @description
     * Directive responsible for displaying the current page's meta data.
     *
     * The directive also allows access to the page editor modal.
     *
     */
    .component('pageInfoMenuToolbarItem', {
        templateUrl: 'web/features/cmssmarteditContainer/components/pages/pageInfoMenu/pageInfoMenuToolbarItemTemplate.html',
        controller: 'pageInfoMenuToolbarItemController'
    });
