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
angular.module('seBreadcrumbModule', ['resourceLocationsModule',
        'navigationEditorNodeServiceModule',
        'resourceModule',
        'functionsModule',
        'assetsServiceModule'
    ])
    .controller('BreadcrumbController', function(navigationEditorNodeService, assetsService) {

        navigationEditorNodeService.getNavigationNodeAncestry(this.nodeUid, this.uriContext).then(function(ancestry) {
            this.breadcrumb = ancestry;
        }.bind(this));

        this.arrowIconUrl = assetsService.getAssetsRoot() + '/images/slash_icon.png';
    })
    /**
     * @ngdoc directive
     * @name seBreadcrumbModule.directive:seBreadcrumb
     * @scope
     * @restrict E
     * @element ANY
     *
     * @description
     * Directive that will build a navigation breadcrumb for the Node identified by its uid.
     * @param {Object} nodeUid the uid of the node the breadcrumb of which we want to build.
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations.
     */
    .component('seBreadcrumb', {
        templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/navigationNode/components/breadcrumb/seBreadcrumbTemplate.html',
        bindings: {
            nodeUid: '<',
            uriContext: '<'
        },
        controller: 'BreadcrumbController',
        controllerAs: 'bc',
    });
