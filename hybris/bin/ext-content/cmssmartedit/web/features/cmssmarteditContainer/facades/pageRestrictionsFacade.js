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
 * @name pageRestrictionsModule
 * @description
 * This module provides a facade module for page restrictions.
 */
angular.module('pageRestrictionsModule', [
    'pageRestrictionsCriteriaModule',
    'restrictionTypesServiceModule',
    'pageRestrictionsServiceModule'
])

/**
 * @ngdoc service
 * @name pageRestrictionsModule.pageRestrictionsFacade
 * @description
 * A facade that exposes only the business logic necessary for features that need to work with page restrictions.
 */
.factory('pageRestrictionsFacade', function(pageRestrictionsCriteriaService, restrictionTypesService, pageRestrictionsService) {


    return {

        // pageRestrictionsCriteriaService
        getRestrictionCriteriaOptions: pageRestrictionsCriteriaService.getRestrictionCriteriaOptions,
        getRestrictionCriteriaOptionFromPage: pageRestrictionsCriteriaService.getRestrictionCriteriaOptionFromPage,

        // restrictionTypesService
        getRestrictionTypesByPageType: restrictionTypesService.getRestrictionTypesByPageType,

        // pageRestrictionsService
        getRestrictionsByPageUID: pageRestrictionsService.getRestrictionsByPageUID,
        isRestrictionTypeSupported: pageRestrictionsService.isRestrictionTypeSupported,
        updateRestrictionsByPageUID: pageRestrictionsService.updateRestrictionsByPageUID,
        getSupportedRestrictionTypeCodes: pageRestrictionsService.getSupportedRestrictionTypeCodes

    };

});
