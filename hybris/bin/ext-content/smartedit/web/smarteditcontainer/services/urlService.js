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
angular.module('urlServiceModule', ['gatewayProxyModule', 'urlServiceInterfaceModule', 'resourceLocationsModule'])
    .factory('urlService', function($location, gatewayProxy, UrlServiceInterface, extend, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID) {

        var UrlService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };


        UrlService = extend(UrlServiceInterface, UrlService);

        UrlService.prototype.openUrlInPopup = function(url) {
            var win = window.open(url, '_blank', 'toolbar=no, scrollbars=yes, resizable=yes');
            win.focus();
        };

        UrlService.prototype.path = function(path) {
            $location.path(path);
        };

        UrlService.prototype.buildUriContext = function(siteId, catalogId, catalogVersion) {
            var uriContext = {};
            uriContext[CONTEXT_SITE_ID] = siteId;
            uriContext[CONTEXT_CATALOG] = catalogId;
            uriContext[CONTEXT_CATALOG_VERSION] = catalogVersion;
            return uriContext;
        };

        return new UrlService('urlService');
    });
