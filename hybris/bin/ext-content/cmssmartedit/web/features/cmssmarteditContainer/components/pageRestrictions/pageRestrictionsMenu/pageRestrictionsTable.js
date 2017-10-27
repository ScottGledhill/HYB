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
 * @name restrictionsTableModule
 * @description
 * This module contains restrictionsTable component.
 */
angular.module('restrictionsTableModule', ['l10nModule', 'pageRestrictionsModule'])

.controller('restrictionsTableController', function(pageRestrictionsFacade) {

    this.removeRestriction = function(restriction) {
        this.restrictions.splice(this.restrictions.indexOf(restriction), 1);
    }.bind(this);

    this.editRestriction = function(restriction) {
        this.onClickOnEdit(restriction);
    }.bind(this);

    this.criteriaOptions = pageRestrictionsFacade.getRestrictionCriteriaOptions();
    if (!this.restrictionCriteria) { //default if none is provided
        this.restrictionCriteria = this.criteriaOptions[0];
    }

    this.actions = [{
        key: 'page.restrictions.item.edit',
        callback: this.editRestriction
    }, {
        key: 'page.restrictions.item.remove',
        callback: this.removeRestriction
    }];

    this.criteriaClicked = function() {
        this.onCriteriaSelected(this.restrictionCriteria);
    };

})

/**
 * @ngdoc directive
 * @name restrictionsTableModule.restrictionsTable
 * @description 
 * Directive that can render a list of restrictions and provides callback functions such as onSelect and onCriteriaSelected.
 * 
 * @param {Object} restrictionCriteria The object that contains information about criteria. 
 * @param {Object} restrictions The object of restrictions.
 * @param {Function} onSelect Triggers the custom on select event.
 * @param {Function} onCriteriaSelected Function that accepts the selected value of criteria.
 * @param {Boolean} editable States whether the restrictions table could be modified.
 * @param {String} customClass The name of the CSS class.
 */
.directive('restrictionsTable', function() {
    return {
        templateUrl: 'web/features/cmssmarteditContainer/components/pageRestrictions/pageRestrictionsMenu/pageRestrictionsTableTemplate.html',
        restrict: 'E',
        controller: 'restrictionsTableController',
        controllerAs: '$ctrl',
        scope: {},
        bindToController: {
            restrictionCriteria: '=?',
            restrictions: '=',
            onSelect: '=',
            onCriteriaSelected: '=',
            editable: '=',
            customClass: '=',
            onClickOnEdit: '<?',
        }
    };
});
