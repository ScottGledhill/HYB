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
angular.module("itemTitleAbstractPageHandlerServiceModule", ['resourceModule', 'resourceLocationsModule', 'componentServiceModule', 'itemTitleStrategyInterfaceModule'])
    .factory("itemTitleAbstractPageHandlerService", function($q, extend, restServiceFactory, ItemTitleStrategyInterface, PAGES_LIST_RESOURCE_URI, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var itemTitleAbstractPageHandlerService = function() {};
        itemTitleAbstractPageHandlerService = extend(ItemTitleStrategyInterface, itemTitleAbstractPageHandlerService);

        itemTitleAbstractPageHandlerService.prototype.getItemTitleById = function(itemId, uriParameters) {

            var pagesUri = PAGES_LIST_RESOURCE_URI
                .replace(':siteUID', uriParameters[CONTEXT_SITE_ID])
                .replace(':catalogId', uriParameters[CONTEXT_CATALOG])
                .replace(':catalogVersion', uriParameters[CONTEXT_CATALOG_VERSION]);
            var restServiceItemsResource = restServiceFactory.get(pagesUri);
            return restServiceItemsResource.getById(itemId).then(function(response) {
                var itemInfo = {};
                itemInfo.itemType = response.typeCode;
                itemInfo.title = response.name;
                return itemInfo;
            });
        };

        return new itemTitleAbstractPageHandlerService();
    });
