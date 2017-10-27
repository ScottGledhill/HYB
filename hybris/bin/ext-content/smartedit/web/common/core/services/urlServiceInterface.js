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
 * @ngdoc service
 * @name urlServiceInterfaceModule.UrlServiceInterface
 *
 * @description
 * Provides an abstract extensible url service, Used to open a given URL
 * in a new browser url upon invocation. 
 * 
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
angular.module('urlServiceInterfaceModule', [])
    .factory('UrlServiceInterface', function() {

        function UrlServiceInterface() {}


        /** 
         * @ngdoc method
         * @name urlServiceInterfaceModule.UrlServiceInterface#openUrlInPopup
         * @methodOf urlServiceInterfaceModule.UrlServiceInterface
         *
         * @description
         * Opens a given URL in a new browser pop up without authentication.
         *
         * @param {String} url - the URL we wish to open.
         */
        UrlServiceInterface.prototype.openUrlInPopup = function(url) {};

        /**
         * @ngdoc method
         * @name urlServiceInterfaceModule.UrlServiceInterface#path
         * @methodOf urlServiceInterfaceModule.UrlServiceInterface
         *
         * @description
         * Navigates to the given path in the same browser tab.
         *
         * @param {String} path - the path we wish to navigate to.
         */
        UrlServiceInterface.prototype.path = function(path) {};


        /**
         * @ngdoc method
         * @name urlServiceInterfaceModule.UrlServiceInterface#buildUriContext
         * @methodOf urlServiceInterfaceModule.UrlServiceInterface
         *
         * @description
         * Builds a {@link resourceLocationsModule.object:UriContext uriContext} needed for some web components
         *
         * @param {String} siteId the site id
         * @param {String} catalogId the catalog id
         * @param {String} catalogVersion the catalog version
         */
        UrlServiceInterface.prototype.buildUriContext = function(siteId, catalogId, catalogVersion) {};

        return UrlServiceInterface;
    });
