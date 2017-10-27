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
 * @name restrictionTypesRestServiceModule
 * @description
 * This module contains REST service for restrictionTypes API.
 */
angular.module('restrictionTypesRestServiceModule', ['restServiceFactoryModule'])


/**
 * @ngdoc service
 * @name restrictionTypesRestServiceModule.restrictionTypesRestService
 * @description
 * Service that handles REST requests for the restrictionTypes CMS API endpoint.
 */
.service('restrictionTypesRestService', function(restServiceFactory, languageService, RESTRICTION_TYPES_URI) {

    var restrictionTypesRestService = restServiceFactory.get(RESTRICTION_TYPES_URI);

    /**
     * @ngdoc method
     * @name restrictionTypesRestServiceModule.restrictionTypesRestService.getRestrictionTypes
     * @methodOf restrictionTypesRestServiceModule.restrictionTypesRestService
     * 
     * @returns {Array} An array of all restriction types in the system.
     */
    this.getRestrictionTypes = function() {
        return restrictionTypesRestService.get().then(function(restrictionTypesResponse) {
            return restrictionTypesResponse;
        });
    };

});
