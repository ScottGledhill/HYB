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
angular.module('uriDropdownPopulatorModule', ['dropdownPopulatorInterfaceModule', 'restServiceFactoryModule'])
    /**
     * @ngdoc service
     * @name uriDropdownPopulatorModule.service:uriDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
     * containing uri attribute.
     */
    .factory('uriDropdownPopulator', function($q, DropdownPopulatorInterface, extend, restServiceFactory) {

        var uriDropdownPopulator = function() {};

        uriDropdownPopulator = extend(DropdownPopulatorInterface, uriDropdownPopulator);

        uriDropdownPopulator.prototype._buildQueryParams = function(dependsOn, model) {
            var queryParams = dependsOn.split(",").reduce(function(obj, current) {
                obj[current] = model[current];
                return obj;
            }, {});

            return queryParams;
        };

        uriDropdownPopulator.prototype.fetchAll = function(payload) {

            var deferred = $q.defer();
            var params;

            if (payload.field.dependsOn) {
                params = this._buildQueryParams(payload.field.dependsOn, payload.model);
            }

            restServiceFactory.get(payload.field.uri).get(params).then(function(response) {
                    var options = response[Object.keys(response)[0]];
                    if (payload.search) {
                        options = options.filter(function(option) {
                            return option.label.toUpperCase().indexOf(payload.search.toUpperCase()) > -1;
                        });
                    }
                    return deferred.resolve(options);
                },
                function() {
                    return deferred.reject();
                });
            return deferred.promise;
        };

        uriDropdownPopulator.prototype.fetchPage = function(payload) {

            var params = {};

            if (payload.field.dependsOn) {
                params = this._buildQueryParams(payload.field.dependsOn, payload.model);
            }
            params.pageSize = payload.pageSize;
            params.currentPage = payload.currentPage;
            params.mask = payload.search;
            return restServiceFactory.get(payload.field.uri).get(params);
        };

        uriDropdownPopulator.prototype.getItem = function(payload) {
            return restServiceFactory.get(payload.field.uri).getById(payload.id);
        };

        return new uriDropdownPopulator();
    });
