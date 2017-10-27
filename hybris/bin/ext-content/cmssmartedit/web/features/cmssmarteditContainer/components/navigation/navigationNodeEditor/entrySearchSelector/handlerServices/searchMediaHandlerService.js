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
angular.module("searchMediaHandlerServiceModule", ['resourceModule', 'resourceLocationsModule', 'functionsModule', 'entrySearchStrategyInterfaceModule'])
    .factory("searchMediaHandlerService", function(extend, EntrySearchStrategyInterface, restServiceFactory, MEDIA_PATH, MEDIA_RESOURCE_URI, URIBuilder, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, isBlank) {

        var searchMediaHandlerService = function() {
            this.SEARCH_TEMPLATE = 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/handlerTemplates/mediaSearchHandlerTemplate.html';
            this.PLACEHOLDER_KEY = 'navigationmanagement.navnode.node.entry.dropdown.media.search';
            this.uriParameters = {};
        };
        searchMediaHandlerService = extend(EntrySearchStrategyInterface, searchMediaHandlerService);

        searchMediaHandlerService.prototype._getMedias = function(mask, pageSize, currentPage) {

            var payload = {
                catalogId: this.uriParameters[CONTEXT_CATALOG] || CONTEXT_CATALOG,
                catalogVersion: this.uriParameters[CONTEXT_CATALOG_VERSION] || CONTEXT_CATALOG_VERSION
            };
            if (!isBlank(mask)) {
                payload.code = mask;
            }

            var subParams = Object.keys(payload).reduce(function(accumulator, next) {
                accumulator += "," + next + ":" + payload[next];
                return accumulator;
            }, "").substring(1);

            var params = {
                namedQuery: "namedQueryMediaSearchByCodeCatalogVersion",
                params: subParams,
                pageSize: pageSize,
                currentPage: currentPage
            };
            return restServiceFactory.get(MEDIA_PATH).get(params).then(function(response) {
                response.results = response.media.map(function(media) {
                    return {
                        id: media.code,
                        code: media.code,
                        description: media.description,
                        altText: media.altText,
                        url: media.url,
                        downloadUrl: media.downloadUrl
                    };
                });
                delete response.media;
                return response;
            });
        };

        searchMediaHandlerService.prototype.getSearchDropdownProperties = function() {
            var properties = {
                templateUrl: this.SEARCH_TEMPLATE,
                placeHolderI18nKey: this.PLACEHOLDER_KEY,
                isPaged: true
            };
            return properties;
        };

        searchMediaHandlerService.prototype.getSearchResults = function(mask, parameters) {
            this.uriParameters = parameters;
            return this._getMedias(mask).then(function(items) {
                return items.results;
            });
        };

        searchMediaHandlerService.prototype.getPage = function(mask, pageSize, currentPage, parameters) {
            this.uriParameters = parameters;
            return this._getMedias(mask, pageSize, currentPage);
        };

        searchMediaHandlerService.prototype.getItem = function(identifier, parameters) {
            this.uriParameters = parameters;
            //identifier is added to URI and not getByid argument because it contains slashes
            var mediaResourceURI = new URIBuilder(MEDIA_RESOURCE_URI).replaceParams(this.uriParameters).build();
            return restServiceFactory.get(mediaResourceURI + "/" + identifier).get().then(function(media) {
                return {
                    id: media.code,
                    code: media.code,
                    description: media.description,
                    altText: media.altText,
                    url: media.url,
                    downloadUrl: media.downloadUrl
                };
            });
        };


        return new searchMediaHandlerService();
    });
