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
angular.module("searchAbstractPageHandlerServiceModule", ['pageListServiceModule', 'entrySearchStrategyInterfaceModule'])
    .factory("searchAbstractPageHandlerService", function($q, extend, EntrySearchStrategyInterface, pageListService) {

        var searchAbstractPageHandlerService = function() {
            this.SEARCH_TEMPLATE = "web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/handlerTemplates/itemSearchHandlerTemplate.html";
            this.PLACEHOLDER_KEY = 'navigationmanagement.navnode.node.entry.dropdown.page.search';
            this.uriParameters = {};
        };
        searchAbstractPageHandlerService = extend(EntrySearchStrategyInterface, searchAbstractPageHandlerService);

        searchAbstractPageHandlerService.prototype._getPageObjects = function() {
            return pageListService.getPageListForCatalog(this.uriParameters).then(function(pages) {
                return pages.map(function(page) {
                    return {
                        name: page.name,
                        id: page.uid,
                        typeCode: page.typeCode
                    };
                });
            });
        };

        searchAbstractPageHandlerService.prototype.getSearchDropdownProperties = function() {
            var properties = {
                templateUrl: this.SEARCH_TEMPLATE,
                placeHolderI18nKey: this.PLACEHOLDER_KEY,
                isPaged: false
            };
            return properties;
        };

        searchAbstractPageHandlerService.prototype.getSearchResults = function(mask, parameters) {
            this.uriParameters = parameters;
            return this._getPageObjects();
        };

        searchAbstractPageHandlerService.prototype.getItem = function(id, parameters) {
            this.uriParameters = parameters;
            return this._getPageObjects().then(function(items) {
                return items.find(function(item) {
                    return item.id == id;
                });
            });
        };

        return new searchAbstractPageHandlerService();
    });
