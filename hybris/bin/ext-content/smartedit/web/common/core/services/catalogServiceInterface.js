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
 * @name catalogServiceModule
 * @description
 * # The catalogServiceModule
 *
 * The Catalog Service module provides a service that fetches catalogs for a specified site or for all sites registered
 * on the hybris platform.
 */
angular.module('catalogServiceInterfaceModule', [])

/**
 * @ngdoc service
 * @name catalogServiceModule.service:catalogService
 *
 * @description
 * The Catalog Service fetches catalogs for a specified site or for all sites registered on the hybris platform using
 * REST calls to the cmswebservices Catalog Version Details API.
 */
.factory('CatalogServiceInterface', function() {

    var CatalogServiceInterface = function() {};


    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#retrieveUriContext
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * Convenience method to return a full {@link resourceLocationsModule.object:UriContext uriContext} to the invoker through a promise.
     * <br/>if uriContext is provided, it will be returned as such.
     * <br/>if uriContext is not provided, A uriContext will be built from the experience present in {@link  sharedDataServiceModule.sharedDataService sharedDataService}.
     * if we fail to find a uriContext in sharedDataService, an exception will be thrown.
     * @param {=Object=} uriContext An optional uriContext that, if provided, is simply returned wrapped in a promise
     *
     * @returns {Object} a {@link resourceLocationsModule.object:UriContext uriContext}
     */
    CatalogServiceInterface.prototype.retrieveUriContext = function(uriContext) {};

    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#getCatalogsForSite
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * Fetches a list of catalogs for the site that corresponds to the specified site UID. The catalogs are
     * retrieved using REST calls to the cmswebservices catalog ersion details API.
     *
     * @param {String} siteUID The UID of the site that the catalog versions are to be fetched.
     *
     * @returns {Array} An array of catalog descriptors. Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalogVersion.
     */
    CatalogServiceInterface.prototype.getCatalogsForSite = function(siteUID) {};

    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#getAllCatalogsGroupedById
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * Fetches a list of catalog groupings for all sites. The catalogs are retrieved using REST calls to the
     * cmswebservices catalog version details API.
     *
     * @returns {Array} An array of catalog groupings sorted by catalog ID, each of which has a name, a catalog ID, and a list of
     * catalog version descriptors.
     */
    CatalogServiceInterface.prototype.getAllCatalogsGroupedById = function() {};

    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#getCatalogsByVersion
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * Fetches a list of catalogs for the given site UID and a given catalog version. The catalogs are
     * retrieved using REST calls to the cmswebservices catalog version details API.
     *
     * @param {String} siteUID The UID of the site that the catalog versions are to be fetched.
     * @param {String} catalogVersion The version of the catalog that is to be fetched.
     *
     * @returns {Array} An array containing the catalog descriptor (if any). Each descriptor provides the following catalog properties:
     * catalog (name), catalogId, and catalogVersion.
     */
    //FIXME : this method does not seem to be safe for same catalogversion version name across multiple catalogs
    CatalogServiceInterface.prototype.getCatalogByVersion = function(siteUID, catalogVersion) {};

    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#isContentCatalogVersionNonActive
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * Determines whether the catalog version identified by the given uriContext is a non active one
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link sharedDataServiceModule.sharedDataService sharedDataService} 
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext}. Optional
     * @returns {Boolean} true if the given catalog version is non active
     */
    CatalogServiceInterface.prototype.isContentCatalogVersionNonActive = function(_uriContext) {};
    /**
     * @ngdoc method
     * @name catalogServiceModule.service:catalogService#getContentCatalogActiveVersion
     * @methodOf catalogServiceModule.service:catalogService
     *
     * @description
     * find the version that is flagged as active for the given uriContext
     * if no uriContext is provided, an attempt will be made to retrieve an experience from {@link sharedDataServiceModule.sharedDataService sharedDataService} 
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext UriContext}. Optional
     * @returns {String} the version name
     */
    CatalogServiceInterface.prototype.getContentCatalogActiveVersion = function(_uriContext) {};

    return CatalogServiceInterface;

});
