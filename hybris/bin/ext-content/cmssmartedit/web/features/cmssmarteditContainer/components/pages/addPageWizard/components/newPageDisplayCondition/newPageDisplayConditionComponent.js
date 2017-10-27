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
 * @name newPageDisplayConditionModule
 * @description
 * #newPageDisplayConditionModule
 *
 * The newPageDisplayConditionModule module contains the
 * {@link newPageDisplayConditionModule.directive:newPageDisplayCondition newPageDisplayCondition} component
 *
 */
angular.module('newPageDisplayConditionModule', ['pageServiceModule', 'pageDisplayConditionsServiceModule'])

/**
 * @ngdoc object
 * @name newPageDisplayConditionModule.object:newPageDisplayConditionResult
 *
 * @description
 * The (optional) output of the
 * {@link newPageDisplayConditionModule.directive:newPageDisplayCondition newPageDisplayCondition} component
 * ```
 * {
    isPrimary: {Boolean} True if the chosen new page display condition is Primary
    primaryPage: {Object} [Optional] If isPrimary is false (meaning this is a variant page),
            the value is a page object, representing the primary page that this
            new page will be a variant of.
 * }
 * ```
 */


.controller('newPageDisplayConditionController', function($scope, pageService, pageDisplayConditionsService) {

    var cache = {};

    var self = this;

    this.previousPageTypeCode = null;
    this.conditions = null;
    this.conditionSelected = null;
    this.primarySelected = null;
    this.ready = false;
    this.resultFn = this.resultFn || function() {};

    this.showPrimarySelector = function showPrimarySelector() {
        return !(this.conditionSelected && this.conditionSelected.isPrimary === true);
    };

    this.dataChanged = function dataChanged() {
        self.resultFn(self._getResults());
    };

    this._getResults = function _getResults() {
        var result = {
            isPrimary: self.conditionSelected.isPrimary
        };
        if (!self.conditionSelected.isPrimary) {
            result.primaryPage = self.primarySelected;
        }
        return result;
    };

    this._delayedInit = function _delayedInit() {
        pageDisplayConditionsService.getNewPageConditions(this.pageTypeCode, this.uriContext).then(function(response) {
            this.conditions = response;
            this.conditionSelected = this.conditions[0];
            if (this.primaryPageChoices && this.primaryPageChoices.length > 0) {
                this.primarySelected = this.primaryPageChoices[0];
            }
            this.ready = true;
            this.dataChanged(); //trigger initial setting of the result
        }.bind(this));
    };

    this._getPrimaryPages = function _getPrimaryPages() {

        if (self.pageTypeCode) {
            if (cache[self.pageTypeCode]) {
                self.primaryPageChoices = cache[self.pageTypeCode];
                self._delayedInit();
            } else {
                pageService.getPrimaryPagesForPageType(self.pageTypeCode, self.uriContext).then(
                        function(primaryPages) {
                            self.primaryPageChoices = primaryPages;
                            cache[self.pageTypeCode] = primaryPages;
                        },
                        function(error) {
                            console.error(error);
                            self.primaryPageChoices = [];
                        })
                    .finally(function() {
                        self._delayedInit();
                    });
            }
        } else {
            self._delayedInit();
        }
    };

    this.$onChanges = function $onChanges() {
        if (self.pageTypeCode !== self.previousPageTypeCode) {
            self.previousPageTypeCode = self.pageTypeCode;
            self._getPrimaryPages();
        }
    };

})

/**
 * @ngdoc directive
 * @name newPageDisplayConditionModule.directive:newPageDisplayCondition
 * @scope
 * @restrict E
 * @element new-page-display-condition
 *
 * @description
 * Component for selecting the page condition that can be applied to a new page.
 * The component takes a page type and some URI params that it needs to load the necessary information, and outputs
 * a display condition result. See below
 *
 * @param {String} pageTypeCode [Required] The page typeCode of a potential new page
 * @param {Object} uriContext [Required] The uri context containing site/catalog information. This is necessary for the
 * component to determine which display conditions can be applied.
 * @param {String} uriContext.siteUID [Required] The site ID for the new page
 * @param {String} uriContext.catalogId [Required] The catalog ID for the new page
 * @param {String} uriContext.catalogVersion [Required] The catalog version for the new page
 * @param {Function} resultFn [Optional] An optional output function binding. Every time there is a change to the output,
 * or resulting display condition, this function (if it exists) will get executed with a
 * {@link newPageDisplayConditionModule.object:newPageDisplayConditionResult newPageDisplayConditionResult} as the single
 * parameter.
 */
.component('newPageDisplayCondition', {
    controller: 'newPageDisplayConditionController',
    templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/components/newPageDisplayCondition/newPageDisplayConditionTemplate.html',
    bindings: {
        pageTypeCode: '<',
        uriContext: '<',
        resultFn: '<?'
    }
});
