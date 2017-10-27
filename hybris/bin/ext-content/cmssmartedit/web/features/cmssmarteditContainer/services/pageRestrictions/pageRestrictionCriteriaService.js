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
 * @name pageRestrictionsCriteriaModule
 * @description
 * This module provides a service to consolidate business logic for restriction criteria.
 */
angular.module('pageRestrictionsCriteriaModule', [])

/**
 * @ngdoc service
 * @name pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService
 * @description
 * A service for working with restriction criteria.
 */
.service('pageRestrictionsCriteriaService', function() {

    var ALL = {},
        ANY = {};

    function setupCriteria(criteria, id, boolValue) {
        Object.defineProperty(criteria, 'id', {
            writable: false,
            value: id
        });
        Object.defineProperty(criteria, 'label', {
            writable: false,
            value: 'page.restrictions.criteria.' + id
        });
        Object.defineProperty(criteria, 'editLabel', {
            writable: false,
            value: 'page.restrictions.criteria.select.' + id
        });
        Object.defineProperty(criteria, 'value', {
            writable: false,
            value: boolValue
        });
    }
    setupCriteria(ALL, 'all', false);
    setupCriteria(ANY, 'any', true);

    var restrictionCriteriaOptions = [ALL, ANY];

    // ---------------------------------------------------------

    /**
     * @ngdoc method
     * @name pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService.getMatchCriteriaLabel
     * @methodOf pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService
     * 
     * @param {Boolean} onlyOneRestrictionMustApply A boolean to determine whether one restriction should be applied.
     * @return {String} The i18n key of the restriction criteria.
     */
    this.getMatchCriteriaLabel = function(onlyOneRestrictionMustApply) {
        if (onlyOneRestrictionMustApply) {
            return ANY.label;
        }
        return ALL.label;
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService.getRestrictionCriteriaOptions
     * @methodOf pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService
     * 
     * @return {Array} An array of criteria options.
     */
    this.getRestrictionCriteriaOptions = function() {
        return restrictionCriteriaOptions;
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService.getRestrictionCriteriaOptionFromPage
     * @methodOf pageRestrictionsCriteriaModule.pageRestrictionsCriteriaService
     * 
     * @return {Object} An object of the restriction criteria for the given page.
     */
    this.getRestrictionCriteriaOptionFromPage = function(page) {
        if (page && typeof page.onlyOneRestrictionMustApply === 'boolean') {
            if (page.onlyOneRestrictionMustApply) {
                return ANY;
            }
        }
        return ALL;
    };

});
