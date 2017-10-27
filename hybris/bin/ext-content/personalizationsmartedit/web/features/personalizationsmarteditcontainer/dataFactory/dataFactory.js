angular.module('personalizationsmarteditDataFactory', ['personalizationsmarteditRestServiceModule'])
    .factory('PaginationHelper', function() {
        PaginationHelper = function(initialData) {
            initialData = initialData || {};

            this.count = initialData.count || 0;
            this.page = initialData.page || 0;
            this.totalCount = initialData.totalCount || 0;
            this.totalPages = initialData.totalPages || 0;

            this.reset = function() {
                this.count = 10;
                this.page = -1;
                this.totalPages = 1;
            };
        };

        return PaginationHelper;
    })
    .factory('customizationDataFactory', function(personalizationsmarteditRestService) {
        var factory = {};
        var defaultFilter = {};
        var defaultDataArrayName = "customizations";
        var defaultSuccessCallbackFunction = function() {};
        var defaultErrorCallbackFunction = function() {};

        var getCustomizations = function(filter) {
            personalizationsmarteditRestService.getCustomizations(filter).then(function(response) {
                Array.prototype.push.apply(factory.items, response[defaultDataArrayName]);
                defaultSuccessCallbackFunction(response);
            }, function(response) {
                defaultErrorCallbackFunction(response);
            });
        };

        factory.items = [];

        factory.updateData = function(params, successCallbackFunction, errorCallbackFunction) {
            params = params || {};
            defaultFilter = params.filter || defaultFilter;
            defaultDataArrayName = params.dataArrayName || defaultDataArrayName;
            if (successCallbackFunction && typeof(successCallbackFunction) == "function") {
                defaultSuccessCallbackFunction = successCallbackFunction;
            }
            if (errorCallbackFunction && typeof(errorCallbackFunction) == "function") {
                defaultErrorCallbackFunction = errorCallbackFunction;
            }

            getCustomizations(defaultFilter);
        };

        factory.refreshData = function() {
            if (angular.equals({}, defaultFilter)) {
                return;
            }
            var tempFilter = {};
            angular.copy(defaultFilter, tempFilter);
            tempFilter.currentSize = factory.items.length;
            tempFilter.currentPage = 0;
            factory.resetData();
            getCustomizations(tempFilter);
        };

        factory.resetData = function() {
            factory.items.length = 0;
        };

        factory.pushData = function(newData) {
            Array.prototype.push.apply(factory.items, newData);
        };

        return factory;
    });
