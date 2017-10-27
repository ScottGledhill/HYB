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
angular.module('pagesVariationsRestServiceModule', ['restServiceFactoryModule', 'yLoDashModule'])
    .service('pagesVariationsRestService', function(restServiceFactory, lodash) {
        var PAGE_VARIATIONS_URI = '/cmswebservices/v1/sites/:siteUID/catalogs/:catalogId/versions/:catalogVersion/pages/:pageId/variations';

        this.getVariationsForPrimaryPageId = function(pageId, params) {
            this._resource = this._resource || restServiceFactory.get(PAGE_VARIATIONS_URI);
            var extendedParams = lodash.assign({
                pageId: pageId,
                siteUID: 'CURRENT_CONTEXT_SITE_ID',
                catalogId: 'CURRENT_CONTEXT_CATALOG',
                catalogVersion: 'CURRENT_CONTEXT_CATALOG_VERSION'
            }, params || {});

            return this._resource.get(extendedParams).then(function(response) {
                return response.uids;
            });
        };
    });
