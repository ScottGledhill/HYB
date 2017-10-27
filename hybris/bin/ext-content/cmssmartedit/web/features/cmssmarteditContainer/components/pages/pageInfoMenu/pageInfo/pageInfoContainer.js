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
 * @name pageInfoContainerModule
 * @description
 * This module contains pageInfoContainer component.
 */
angular.module('pageInfoContainerModule', ['catalogServiceModule', 'pageInfoDetailsModule', 'pageInfoHeaderModule', 'componentHandlerServiceModule', 'pageListServiceModule', 'contextAwarePageStructureServiceModule', 'pageEditorModalServiceModule'])

.controller('pageInfoContainerController', function(catalogService, componentHandlerService, pageListService, contextAwarePageStructureService, pageEditorModalService) {
    this.$onInit = function() {
        var pageUid = componentHandlerService.getPageUID();
        pageListService.getPageById(pageUid).then(function(pageInfo) {
            this.pageUid = pageInfo.uid;
            this.pageTypeCode = pageInfo.typeCode;
            this.pageTemplate = pageInfo.template;
            this.pageContent = pageInfo;
            catalogService.retrieveUriContext().then(function(uriContext) {
                this.pageContent = pageInfo;
                this.pageContent.uriContext = uriContext;
            }.bind(this));
        }.bind(this)).then(function() {
            contextAwarePageStructureService.getPageStructureForViewing(this.pageTypeCode).then(function(pageStructure) {
                this.pageStructure = pageStructure;
            }.bind(this));
        }.bind(this));
    };

    this.onEditClick = function() {
        this.onEditClickCallback();
        pageEditorModalService.open(this.pageContent);
    };
})


/**
 * @ngdoc directive
 * @name pageInfoContainerModule.pageInfoContainer
 * @description
 * Directive that can render current storefront page's information and provides a callback function triggered on opening the editor.
 *
 * @param {Function} onEditClickCallback Triggers when the user opens the Page Editor dialog
 */
.component('pageInfoContainer', {
    templateUrl: 'web/features/cmssmarteditContainer/components/pages/pageInfoMenu/pageInfo/pageInfoContainerTemplate.html',
    controller: 'pageInfoContainerController',
    bindings: {
        onEditClickCallback: '&'
    }
});
