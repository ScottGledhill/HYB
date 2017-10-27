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
 * @name httpErrorInterceptorModule
 *
 * @description
 * Module that provides a service called {@link httpErrorInterceptorModule.httpErrorInterceptor httpErrorInterceptor}
 * for handling all response errors that are neither 401 errors (Unauthorized access error) nor OCC validation errors.
 */
angular.module('httpErrorInterceptorModule', ['interceptorHelperModule', 'functionsModule', 'translationServiceModule', 'authenticationModule', 'alertServiceModule', 'resourceLocationsModule', 'sharedDataServiceModule'])


/**
 * @ngdoc service
 * @name httpErrorInterceptorModule.httpErrorInterceptor
 * 
 * @description
 * Provides a way for global error handling by intercepting the requests before handing them to the server
 * and response before is its given to the application code.
 * 
 * The interceptors are service factories that are registered with the $httpProvider by adding them to the $httpProvider.interceptors array. 
 * The factory is called and injected with dependencies and returns the interceptor object with contains the interceptor methods.
 */
.factory('httpErrorInterceptor', function(LANGUAGE_RESOURCE_URI, $q, $location, $rootScope, $injector, hitch, authenticationService, $log, alertService, interceptorHelper, sharedDataService, PREVIEW_RESOURCE_URI) {

        var errorRecoverer = {};

        errorRecoverer.removeErrorsByType = function(type, errors) {

            return (errors || []).filter(function(error) {
                return error.type != type;
            });
        };

        errorRecoverer.convertErrorToTimedAlertObject = function(error) {

            var timedAlertObject = {
                successful: false,
                message: error.message,
                closeable: true
            };

            return timedAlertObject;
        };


        errorRecoverer._failingHTML = function(response) {

            return response.config.method === "GET" && response.status === 404 && response.headers('Content-type').indexOf('text/html') >= 0;

        };

        errorRecoverer._failingPreviewForUnkownPageId = function(response) {
            return response.status == 400 && response.config.url.indexOf(PREVIEW_RESOURCE_URI) > -1 && response.config.data.pageId && response.data.errors.find(function(error) {
                return error.type === 'UnknownIdentifierError';
            });
        };

        /**
         * @ngdoc method
         * @name httpErrorInterceptorModule.httpErrorInterceptor#responseError
         * @methodOf httpErrorInterceptorModule.httpErrorInterceptor
         *
         * @description
         * Interceptor method which intercepts all the failed responses and handles them based on the response code.
         *
         * This method handles all response errors that are neither 401 errors (Unauthorized access error) nor OCC validation errors.
         *
         * @param {Object} response - the response object which contains the status code and other parameters that explain the response.
         *
         * @returns {Promise} Returns a {@link https://docs.angularjs.org/api/ng/service/$q promise} that was already resolved as rejected with the given response.
         */
        errorRecoverer.responseError = function(response) {
            return interceptorHelper.handleResponseError(response, function() {
                $log.debug(["Intercepting response error: ", response.config.url, " status: ", response.status].join(" "));
                var languageResourceRegex = new RegExp(LANGUAGE_RESOURCE_URI.replace(/\:.*\//g, '.*/'));

                /*
                 * in case of a failing POST to preview API due to pageId not found in the given context, the POST will be replayed without pageId
                 * and the currentLocation will be reset so as to load the homepage
                 */
                if (errorRecoverer._failingPreviewForUnkownPageId(response)) {
                    delete response.config.data.pageId;
                    sharedDataService.update("experience", function(experience) {
                        delete experience.pageId;
                        return experience;
                    });
                    $injector.get('iFrameManager').setCurrentLocation(null);
                    return $q.when($injector.get('$http')(response.config));
                } else if (response.status == 400) {

                    var nonValidationErrors = errorRecoverer.removeErrorsByType("ValidationError", response.data.errors);

                    if (nonValidationErrors.length !== 0) {

                        var timedAlertObjects = [];

                        nonValidationErrors.forEach(function(nonValidationError) {
                            timedAlertObjects.push(errorRecoverer.convertErrorToTimedAlertObject(nonValidationError));
                        });

                        alertService.pushAlerts(timedAlertObjects);
                    }
                } else if (response.status != 401 && !languageResourceRegex.test(response.config.url)) {

                    var errorMessage = response.message === undefined ? "unknown.request.error" : response.message;

                    if (!errorRecoverer._failingHTML(response)) {
                        alertService.pushAlerts([{
                            successful: false,
                            message: errorMessage,
                            closeable: true
                        }]);
                    }

                }
                return $q.reject(response);
            });

        };

        return errorRecoverer;
    })
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('httpErrorInterceptor');
    });