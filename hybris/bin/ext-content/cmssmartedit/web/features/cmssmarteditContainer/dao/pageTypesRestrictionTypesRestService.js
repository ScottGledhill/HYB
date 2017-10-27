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
 * @name pageTypesRestrictionTypesRestServiceModule
 * @description
 * This module contains REST service for pageTypes restrictionTypes API.
 */
angular.module('pageTypesRestrictionTypesRestServiceModule', ['restServiceFactoryModule'])


/**
 * @ngdoc service
 * @name pageTypesRestrictionTypesRestServiceModule.pageTypesRestrictionTypesRestService
 *
 * @description
 * ervice that handles REST requests for the pageTypes restrictionTypes CMS API endpoint.
 */
.service('pageTypesRestrictionTypesRestService', function(restServiceFactory, languageService, PAGE_TYPES_RESTRICTION_TYPES_URI) {

    var rest = restServiceFactory.get(PAGE_TYPES_RESTRICTION_TYPES_URI);

    /**
     * @ngdoc method
     * @name pageTypesRestrictionTypesRestServiceModule.pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes
     * @methodOf pageTypesRestrictionTypesRestServiceModule.pageTypesRestrictionTypesRestService
     * 
     * @return {Array} An array of all pageType-restrictionType in the system.
     */
    this.getPageTypesRestrictionTypes = function() {
        return rest.get().then(function(pageTypesRestrictionTypesArray) {
            return pageTypesRestrictionTypesArray;
        });
    };

});
