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
 * @name pageRestrictionsRestServiceModule
 * @description
 * This module contains REST service for page restrictions API.
 */
angular.module('pageRestrictionsRestServiceModule', ['restServiceFactoryModule'])

/**
 * @ngdoc service
 * @name pageRestrictionsRestServiceModule.pageRestrictionsRestService
 * @description 
 * Service that handles REST requests for the pagesRestrictions CMS API endpoint.
 */
.service('pageRestrictionsRestService', function(restServiceFactory,
    CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI, PAGES_RESTRICTIONS_RESOURCE_URI, UPDATE_PAGES_RESTRICTIONS_RESOURCE_URI) {

    var contextualPageRestrictionsRestService = restServiceFactory.get(CONTEXTUAL_PAGES_RESTRICTIONS_RESOURCE_URI);
    var pageRestrictionsRestService = restServiceFactory.get(PAGES_RESTRICTIONS_RESOURCE_URI);
    var updatePageRestrictionsRestService = restServiceFactory.get(UPDATE_PAGES_RESTRICTIONS_RESOURCE_URI, "pageid");


    /**
     * @ngdoc method
     * @name pageRestrictionsRestServiceModule.pageRestrictionsRestService.update
     * @methodOf pageRestrictionsRestServiceModule.pageRestrictionsRestService
     * 
     * @param {Object} payload The pageRestriction object containing the pageRestrictionList. 
     * @return {Array} An array of pageRestrictions.
     * 
     * @description
     * It will do a PUT to the pageRestrictions endpoint.
     */
    this.update = function(payload) {
        return updatePageRestrictionsRestService.update(payload);
    };

    /**
     * @ngdoc method
     * @name  pageRestrictionsRestServiceModule.pageRestrictionsRestService.getPagesRestrictionsForPageId
     * @methodOf pageRestrictionsRestServiceModule.pageRestrictionsRestService
     * 
     * @param {String} pageId The unique page identifier for which to fetch pages-restrictions relation.
     * @return {Array} An array of pageRestrictions for the given page.
     */
    this.getPagesRestrictionsForPageId = function(pageId) {
        return contextualPageRestrictionsRestService.get({
            pageId: pageId
        });
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsRestServiceModule.pageRestrictionsRestService.getPagesRestrictionsForcatalogVersion
     * @methodOf pageRestrictionsRestServiceModule.pageRestrictionsRestService
     * 
     * @param {String} siteUID The unique identifier for site.
     * @param {String} catalogUID The unique identifier for catalog.
     * @param {String} catalogVersionUID The unique identifier for catalog version.
     * @return {Array} An array of all pageRestrictions for the given catalog.
     */
    this.getPagesRestrictionsForCatalogVersion = function(siteUID, catalogUID, catalogVersionUID) {
        return pageRestrictionsRestService.get({
            siteUID: siteUID,
            catalogId: catalogUID,
            catalogVersion: catalogVersionUID
        });
    };
});
