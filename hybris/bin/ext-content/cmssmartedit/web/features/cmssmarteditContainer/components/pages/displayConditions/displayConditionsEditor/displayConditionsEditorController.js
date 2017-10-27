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
angular.module('displayConditionsEditorControllerModule', ['displayConditionsEditorModelModule'])
    .controller('displayConditionsEditorController', function(displayConditionsEditorModel) {
        this.$onInit = function() {
            displayConditionsEditorModel.initModel(this.pageUid);
        };

        this.getPageName = function() {
            return displayConditionsEditorModel.pageName;
        };

        this.getPageType = function() {
            return displayConditionsEditorModel.pageType;
        };

        this.isPagePrimary = function() {
            return displayConditionsEditorModel.isPrimary;
        };

        this.getVariations = function() {
            return displayConditionsEditorModel.variations;
        };

        this.getAssociatedPrimaryPage = function() {
            return displayConditionsEditorModel.associatedPrimaryPage;
        };

        this.getIsAssociatedPrimaryReadOnly = function() {
            return displayConditionsEditorModel.isAssociatedPrimaryReadOnly;
        };

        this.getPrimaryPages = function() {
            return displayConditionsEditorModel.primaryPages;
        };

        this.onPrimaryPageSelect = function(primaryPage) {
            displayConditionsEditorModel.setAssociatedPrimaryPage(primaryPage);
        };
    });
