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
//new file which can be used to add helper methods for testing

//injects rootscope and q for global use
var $rootScope, $q;
var directiveScope, response, element, componentService;
var setupBackendResponse, templateSetup;

var testSetup = inject(function(_$rootScope_, _$q_) {
    $rootScope = _$rootScope_;
    $q = _$q_;
});


// temp - to be replaced with proper loading as in testDependencyLoader.js in future
var unit = {};
unit.mockData = {};
unit.mockServices = {};
unit.mockServices.dao = {};
unit.mockServices.services = {};


var setupDirectiveTest = function() {

    setupBackendResponse = function($httpBackend, uri, response) {
        response = response || {};
        $httpBackend.whenGET(uri).respond(response);
    };

    templateSetup = function(template, $compile, $rootScope, scopeAugmentation) {
        directiveScope = $rootScope.$new();
        $.extend(directiveScope, scopeAugmentation || {});

        element = angular.element(template);
        $compile(element)(directiveScope);

        $rootScope.$digest();
        expect(element.scope()).toBe(directiveScope);
        $('body').append(element);
        return element;
    };

};


var AngularUnitTestHelper = (function() {

    function AngularUnitTestBuilder(moduleName) {

        var mockServices = {};
        var mockConstants = {};
        var injected = [];
        var translations = {};
        var injectedMap;
        var lastMock;

        function moduleExists(moduleName) {
            try {
                return !!angular.module(moduleName);
            } catch (ex) {
                return false;
            }
        }

        function createModulesInChainIfNecessary(moduleNames) {
            moduleNames.forEach(function(moduleName) {
                if (!moduleExists(moduleName)) {
                    angular.module(moduleName, []);
                }
                createModulesInChainIfNecessary(angular.module(moduleName).requires);
            });
        }

        function loadDependencyModules(moduleName) {
            var directDependencies = angular.module(moduleName).requires;
            var defaultDependencies = ['cmssmarteditContainerTemplates', 'cmssmarteditTemplates', 'ui.select'];
            var allDependencies = directDependencies.concat(defaultDependencies);
            createModulesInChainIfNecessary(allDependencies);
            allDependencies.forEach(function(moduleName) {
                module(moduleName);
            });
        }

        function addDefaultMocks() {
            mockConstants.l10nFilter = function(localizedMap) {
                return localizedMap.en;
            };
        }

        function loadModule() {
            if (!moduleExists(moduleName)) {
                throw Error('Could not find module ' + moduleName + ', should this test exist in cmssmartedit or cmssmarteditContainer?');
            }

            // Add custom matchers
            customMatchers.bind(jasmine.getEnv().currentSpec)();

            // Add default mocks
            addDefaultMocks();

            // Create/load the dependency modules as required
            loadDependencyModules(moduleName);

            // Load Translations
            module('pascalprecht.translate', function($translateProvider) {
                $translateProvider.translations('en', translations);
                $translateProvider.preferredLanguage('en');
            });

            // Prepare required module and provide the mocks
            module(moduleName, function($provide) {
                // Load mock services
                Object.keys(mockServices).forEach(function(mockServiceName) {
                    var mockService = mockServices[mockServiceName];
                    $provide.value(mockServiceName, mockService);
                });

                // Load mock constants
                Object.keys(mockConstants).forEach(function(mockConstant) {
                    var mockValue = mockConstants[mockConstant];
                    $provide.constant(mockConstant, mockValue);
                });
            });
        }

        function loadInjected() {
            loadModule();
            var injectedMap = {};
            var defaultInjected = ['$controller', '$q', '$rootScope', '$compile'];
            var allInjected = injected.concat(defaultInjected);
            inject(allInjected.concat([function() {
                var injectedServices = Array.prototype.slice.call(arguments);
                allInjected.forEach(function(injectedName, i) {
                    injectedMap[injectedName] = injectedServices[i];
                });
            }]));
            return injectedMap;
        }

        function build() {
            injectedMap = loadInjected();
            //extendMockBehaviour(injectedMap.$q);
            return {
                mocks: mockServices,
                injected: injectedMap,
                detectChanges: function() {
                    injectedMap.$rootScope.$digest();
                }
            };
        }

        this.mock = function(serviceName, functionName) {
            mockServices[serviceName] = mockServices[serviceName] || {};
            if (functionName) {
                var spy = jasmine.createSpy(functionName);
                lastMock = spy;

                spy.andReturnResolvedPromise = function(data) {
                    spy.andCallFake(function() {
                        return injectedMap.$q.when(data);
                    });
                };

                spy.andReturnRejectedPromise = function(data) {
                    spy.andCallFake(function() {
                        return injectedMap.$q.reject(data);
                    });
                };

                mockServices[serviceName][functionName] = spy;
            }
            return this;
        };

        this.andReturn = function(data) {
            if (!lastMock) return;
            lastMock.andReturn(data);
            return this;
        };

        this.andCallFake = function(callback) {
            if (!lastMock) return;
            lastMock.andCallFake(callback);
            return this;
        };

        this.andReturnResolvedPromise = function(data) {
            if (!lastMock) return;
            lastMock.andReturnResolvedPromise(data);
            return this;
        };

        this.andReturnRejectedPromise = function(data) {
            if (!lastMock) return;
            lastMock.andReturnRejectedPromise(data);
            return this;
        };

        this.mockConstant = function(constant, value) {
            mockConstants[constant] = value;
            return this;
        };

        this.inject = function(serviceName) {
            injected.push(serviceName);
            return this;
        };

        this.withTranslations = function(newTranslations) {
            translations = newTranslations;
            return this;
        };

        this.controller = function(controllerName, locals) {
            locals = locals || {};
            locals.$scope = locals.$scope || {};
            locals.$routeParams = locals.$routeParams || {};

            var fixture = build();
            fixture.controller = fixture.injected.$controller(controllerName, locals);
            fixture.detectChanges();
            return fixture;
        };

        this.directive = function(template, locals) {
            var fixture = build();

            directiveScope = fixture.injected.$rootScope.$new();
            $.extend(directiveScope, locals || {});
            element = angular.element(template);
            fixture.injected.$compile(element)(directiveScope);
            fixture.detectChanges();

            fixture.pageObjects = {};
            fixture.pageObjects.uiSelect = new UiSelectPageObject(element);

            $('body').append(element);
            fixture.element = element;
            fixture.scope = directiveScope;
            return fixture;
        };

        this.component = function(template, locals) {
            return this.directive(template, locals);
        };

        this.service = function(serviceName) {
            this.inject(serviceName);
            var fixture = build();
            fixture.service = fixture.injected[serviceName];
            return fixture;
        };
    }

    return {
        prepareModule: function(moduleName) {
            return new AngularUnitTestBuilder(moduleName);
        }
    };

}());
