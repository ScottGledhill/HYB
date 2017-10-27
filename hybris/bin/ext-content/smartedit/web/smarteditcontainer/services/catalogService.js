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
angular.module('catalogServiceModule', ['gatewayProxyModule', 'sharedDataServiceModule', 'restServiceFactoryModule', 'siteServiceModule', 'resourceLocationsModule'])
    .service('catalogService', function($q, gatewayProxy, sharedDataService, restServiceFactory, siteService, CATALOG_VERSION_DETAILS_RESOURCE_URI, PRODUCT_CATALOG_VERSION_DETAILS_RESOURCE_API, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {
        var cache = {};
        var productCatalogsCache = {};

        var catalogRestService = restServiceFactory.get(CATALOG_VERSION_DETAILS_RESOURCE_URI);
        var productCatalogRestService = restServiceFactory.get(PRODUCT_CATALOG_VERSION_DETAILS_RESOURCE_API);

        this.getCatalogsForSite = function(siteUID) {
            return cache[siteUID] ? $q.when(cache[siteUID]) : catalogRestService.get({
                siteUID: siteUID
            }).then(function(catalogsDTO) {
                cache[siteUID] = catalogsDTO.catalogVersionDetails.reduce(function(acc, catalogVersionDescriptor) {
                    if (catalogVersionDescriptor.name && catalogVersionDescriptor.catalogId && catalogVersionDescriptor.version) {
                        acc.push({
                            name: catalogVersionDescriptor.name,
                            catalogId: catalogVersionDescriptor.catalogId,
                            catalogVersion: catalogVersionDescriptor.version,
                            active: catalogVersionDescriptor.active,
                            thumbnailUrl: catalogVersionDescriptor.thumbnailUrl
                        });
                    }
                    return acc;
                }, []);
                return cache[siteUID];
            });
        };

        this.getAllCatalogsGroupedById = function() {
            var catalogGroupings = [];
            var deferred = $q.defer();
            var catalogService = this;

            siteService.getSites().then(function(sites) {
                var promises = [];

                sites.forEach(function(site, index) {
                    promises.push(catalogService.getCatalogsForSite(site.uid).then(function(catalogs) {
                        return catalogs.map(function(catalog) {
                            catalog.siteDescriptor = site;
                            return catalog;
                        });
                    }));
                });

                $q.all(promises).then(function(catalogs) {
                    catalogs = catalogs.reduce(function(allCatalogs, catalogsSubset) {
                        allCatalogs = allCatalogs.concat(catalogsSubset);
                        return allCatalogs;
                    }, []);

                    catalogs.forEach(function(catalog) {
                        var matchedCatalogGrouping = null;
                        catalogGroupings.forEach(function(catalogGrouping) {
                            if (catalogGrouping.catalogId == catalog.catalogId) {
                                matchedCatalogGrouping = catalogGrouping;
                            }
                        });

                        if (!matchedCatalogGrouping) {
                            matchedCatalogGrouping = {
                                name: catalog.name,
                                catalogId: catalog.catalogId,
                                catalogVersions: []
                            };
                            catalogGroupings.push(matchedCatalogGrouping);
                        }

                        matchedCatalogGrouping.catalogVersions.push(catalog);
                    });

                    deferred.resolve(catalogGroupings);

                }, function() {
                    deferred.reject();
                });

            }, function() {
                deferred.reject();
            });

            return deferred.promise;
        };

        this.getCatalogByVersion = function(siteUID, catalogVersion) {

            return this.getCatalogsForSite(siteUID).then(function(catalogs) {

                var filteredCatalog = catalogs.filter(function(catalog) {
                    return catalog.catalogVersion === catalogVersion;
                });

                return filteredCatalog;

            });
        };

        this.isContentCatalogVersionNonActive = function(_uriContext) {
            return this._getContext(_uriContext).then(function(uriContext) {
                return this.getCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    return !catalogs.find(function(catalog) {
                        return catalog.catalogId === uriContext[CONTEXT_CATALOG] && catalog.catalogVersion === uriContext[CONTEXT_CATALOG_VERSION];
                    }).active;
                });
            }.bind(this));
        };

        this.getContentCatalogActiveVersion = function(_uriContext) {
            return this._getContext(_uriContext).then(function(uriContext) {
                return this.getCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    return catalogs.find(function(catalog) {
                        return catalog.catalogId === uriContext[CONTEXT_CATALOG] && catalog.active === true;
                    }).catalogVersion;
                });
            }.bind(this));
        };

        this.retrieveUriContext = function(_uriContext) {
            return this._getContext(_uriContext);
        };

        this._getContext = function(_uriContext) {
            return _uriContext ? $q.when(_uriContext) : sharedDataService.get('experience').then(function(experience) {
                if (!experience) {
                    throw "catalogService was not provided with a uriContext and could not retrive an experience from sharedDataService";
                }
                var uriContext = {};
                uriContext[CONTEXT_SITE_ID] = experience.siteDescriptor.uid;
                uriContext[CONTEXT_CATALOG] = experience.catalogDescriptor.catalogId;
                uriContext[CONTEXT_CATALOG_VERSION] = experience.catalogDescriptor.catalogVersion;
                return uriContext;
            });
        };

        this.getProductCatalogsForSite = function(siteUID) {
            return productCatalogsCache[siteUID] ? $q.when(productCatalogsCache[siteUID]) : productCatalogRestService.get({
                siteUID: siteUID
            }).then(function(catalogsDTO) {
                productCatalogsCache[siteUID] = this._parseCatalogsVersionDetails(catalogsDTO);
                return productCatalogsCache[siteUID];
            }.bind(this));
        };

        this._parseCatalogsVersionDetails = function(catalogsDTO) {
            return catalogsDTO.catalogVersionDetails.reduce(function(acc, catalogVersionDescriptor) {
                if (catalogVersionDescriptor.name && catalogVersionDescriptor.catalogId && catalogVersionDescriptor.version) {
                    acc.push({
                        name: catalogVersionDescriptor.name,
                        catalogId: catalogVersionDescriptor.catalogId,
                        catalogVersion: catalogVersionDescriptor.version,
                        thumbnailUrl: catalogVersionDescriptor.thumbnailUrl
                    });
                }
                return acc;
            }, []);
        };

        this.getProductCatalogByVersion = function(siteUID, catalogVersion) {
            return this.getProductCatalogsForSite(siteUID).then(function(catalogs) {
                return catalogs.filter(function(catalog) {
                    return catalog.catalogVersion === catalogVersion;
                });
            });
        };

        this.gatewayId = "catalogService";
        gatewayProxy.initForService(this);

    });
