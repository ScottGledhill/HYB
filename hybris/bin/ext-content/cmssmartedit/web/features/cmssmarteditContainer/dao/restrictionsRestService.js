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
 * @name restrictionsRestServiceModule
 * @description
 * This module contains REST service for restrictions API.
 */

angular.module('restrictionsRestServiceModule', ['restServiceFactoryModule', 'functionsModule'])

/**
 * @ngdoc service
 * @name restrictionsRestServiceModule.restrictionRestService
 * @description
 * Service that handles REST requests for the restrictions CMS API endpoint.
 */
.service('restrictionsRestService', function(restServiceFactory, languageService, RESTRICTIONS_RESOURCE_URI, URIBuilder) {

    var restrictionsRestService = restServiceFactory.get(RESTRICTIONS_RESOURCE_URI);

    /**
     * @ngdoc method
     * @name restrictionsRestServiceModule.restrictionRestService.getContentApiUri
     * @methodOf restrictionsRestServiceModule.restrictionRestService
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext uriContext}
     * @return {String} A URI for CRUD of restrictions on a specific site/catalog/version
     */
    this.getContentApiUri = function(uriContext) {
        return new URIBuilder(RESTRICTIONS_RESOURCE_URI).replaceParams(uriContext).build();
    };

    /**
     * @ngdoc method
     * @name restrictionsRestServiceModule.restrictionRestService.get
     * @methodOf restrictionsRestServiceModule.restrictionRestService
     * 
     * @return {Array} An array of all restrictions in the system.
     */
    this.get = function(params) {
        return restrictionsRestService.get(params);
    };

    /**
     * @ngdoc method
     * @name restrictionsRestServiceModule.restrictionRestService.getById
     * @methodOf restrictionsRestServiceModule.restrictionRestService
     *
     * @return {Object} The restriction matching the given ID
     */
    this.getById = function getById(restrictionId) {
        return restrictionsRestService.getById(restrictionId).then(function(restriction) {
            return restriction;
        });
    };

});
