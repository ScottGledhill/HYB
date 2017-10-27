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
angular.module('productServiceModule', ['restServiceFactoryModule', 'resourceLocationsModule'])
    .factory('productService', ['restServiceFactory', 'siteService', 'PRODUCT_RESOURCE_API', 'PRODUCT_LIST_RESOURCE_API', function(restServiceFactory, siteService, PRODUCT_RESOURCE_API, PRODUCT_LIST_RESOURCE_API) {
        var productService = restServiceFactory.get(PRODUCT_RESOURCE_API);
        var productListService = restServiceFactory.get(PRODUCT_LIST_RESOURCE_API);

        return {
            getProductById: function(siteUID, productUID) {
                return productService.get({
                    siteUID: siteUID,
                    productUID: productUID
                });
            },
            getProducts: function(productCatalogInfo, mask, pageSize, currentPage) {
                this._validateProductCatalogInfo(productCatalogInfo);
                return productListService.get({
                    catalogId: productCatalogInfo.catalogId,
                    catalogVersion: productCatalogInfo.catalogVersion,
                    text: mask,
                    pageSize: pageSize,
                    currentPage: currentPage
                });
            },
            _validateProductCatalogInfo: function(productCatalogInfo) {
                if (!productCatalogInfo.catalogId) {
                    throw Error("[productService] - catalog ID missing.");
                }
                if (!productCatalogInfo.catalogVersion) {
                    throw Error("[productService] - catalog version  missing.");
                }
            }
        };
    }]);
