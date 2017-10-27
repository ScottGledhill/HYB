//new file which can be used to add helper methods for testing

//injects rootscope and q for global use
var $rootScope, $q, $compile;
var directiveScope, response, element, componentService;
var setupBackendResponse, templateSetup;

var testSetup = inject(function(_$rootScope_, _$q_, _$compile_) {
    $rootScope = _$rootScope_;
    $q = _$q_;
    $compile = _$compile_;
});

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

var AngularUnitTestHelper = {
    _reset: function() {
        this._moduleName = null;
        this._mockedServices = {};
        this._harness = null;
        this._injectables = [];
    },
    _moduleExists: function(moduleName) {
        try {
            return !!angular.module(moduleName);
        } catch (ex) {
            return false;
        }
    },
    _createModuleIfNecessary: function(moduleName) {
        if (!this._moduleExists(moduleName)) {
            angular.module(moduleName, []);
        }
    },
    _createModulesInChainIfNecessary: function(moduleNames) {
        moduleNames.forEach(function(moduleName) {
            this._createModuleIfNecessary(moduleName);
            this._createModulesInChainIfNecessary(angular.module(moduleName).requires);
        }.bind(this));
    },
    _loadDependencyModules: function(moduleName) {
        var directDependencies = angular.module(moduleName).requires;
        var otherDependencies = ['cmssmarteditContainerTemplates', 'cmssmarteditTemplates', 'ui.select'];
        var allDependencies = directDependencies.concat(otherDependencies);
        this._createModulesInChainIfNecessary(allDependencies);
        allDependencies.forEach(function(moduleName) {
            module(moduleName);
        });
    },
    _loadPreparedModuleAndGetMocks: function() {
        var helper = this;
        var mocks = {};
        module(this._moduleName, function($provide) {
            Object.keys(helper._mockedServices).forEach(function(mockedServiceName) {
                var mockedFunctionNames = Object.keys(helper._mockedServices[mockedServiceName]);
                mocks[mockedServiceName] = jasmine.createSpyObj(mockedServiceName, mockedFunctionNames);
                $provide.value(mockedServiceName, mocks[mockedServiceName]);
            });
            $provide.value('l10nFilter', function(localizedMap) {
                return localizedMap.en;
            });
        });
        return mocks;
    },
    _injectAndGetInjected: function() {
        var injectablesMap = {};
        var defaultInjectables = ['$controller', '$q', '$rootScope', '$compile', '$timeout'];
        var allInjectables = this._injectables.concat(defaultInjectables);
        inject(allInjectables.concat([function() {
            var injectedServices = Array.prototype.slice.call(arguments);
            allInjectables.forEach(function(injectableName, i) {
                injectablesMap[injectableName] = injectedServices[i];
            });
        }]));
        return injectablesMap;
    },
    _extendMockBehaviour: function(mocks, injected) {
        Object.keys(mocks).forEach(function(mockName) {
            Object.keys(mocks[mockName]).forEach(function(mockFunctionName) {
                var mockFunction = mocks[mockName][mockFunctionName];
                mockFunction.andReturnResolvedPromise = function(data) {
                    return mockFunction.andReturn(injected.$q.when(data));
                };
                mockFunction.andReturnRejectedPromise = function(data) {
                    return mockFunction.andReturn(injected.$q.reject(data));
                };
                mockFunction.andDoNothing = function() {};
            });
        });
    },
    _prepareMockBehaviour: function(mocks, injected) {
        this._extendMockBehaviour(mocks, injected);
        Object.keys(this._mockedServices).forEach(function(mockedServiceName) {
            Object.keys(this._mockedServices[mockedServiceName]).forEach(function(mockedFunctionName) {
                var prepare = this._mockedServices[mockedServiceName][mockedFunctionName].prepare;
                var data = this._mockedServices[mockedServiceName][mockedFunctionName].data;
                var mock = mocks[mockedServiceName];
                mock[mockedFunctionName][prepare](data);
            }.bind(this));
        }.bind(this));
    },
    _getHarness: function() {
        if (!this._harness) {
            var mocks = this._loadPreparedModuleAndGetMocks();
            var injected = this._injectAndGetInjected();
            this._prepareMockBehaviour(mocks, injected);
            this._harness = {
                mocks: mocks,
                injected: injected
            };
        }
        return this._harness;
    },
    prepareModule: function(moduleName) {
        customMatchers.bind(jasmine.getEnv().currentSpec)();
        this._reset();
        if (this._moduleExists(moduleName)) {
            this._moduleName = moduleName;
            this._loadDependencyModules(moduleName);
        }
        return this;
    },
    mock: function(serviceName, functionName) {
        this._mockedServices[serviceName] = this._mockedServices[serviceName] || {};
        this._mockedServices[serviceName][functionName] = {};
        var helper = this;
        var jasminePrepares = ['andReturn', 'andCallFake', 'andCallThrough'];
        var otherPrepares = ['andReturnResolvedPromise', 'andReturnRejectedPromise', 'andDoNothing'];
        return jasminePrepares.concat(otherPrepares).reduce(function(mockWrapper, prepare) {
            mockWrapper[prepare] = function(data) {
                helper._mockedServices[serviceName][functionName].prepare = prepare;
                helper._mockedServices[serviceName][functionName].data = data;
                return helper;
            };
            return mockWrapper;
        }, {});
    },
    inject: function() {
        this._injectables = Array.prototype.slice.call(arguments);
        return this;
    },
    withTranslations: function(translations) {
        module('pascalprecht.translate', function($translateProvider) {
            $translateProvider.translations('en', translations);
            $translateProvider.preferredLanguage('en');
        });
        return this;
    },
    controller: function(controllerName, locals) {
        locals = locals || {};
        locals.$scope = locals.$scope || {};
        locals.$routeParams = locals.$routeParams || {};

        var harness = this._getHarness();
        harness.controller = harness.injected.$controller(controllerName, locals);
        harness.injected.$rootScope.$digest();
        return harness;
    },
    directive: function(template, locals) {
        var harness = this._getHarness();
        directiveScope = harness.injected.$rootScope.$new();
        $.extend(directiveScope, locals || {});

        element = angular.element(template);
        harness.injected.$compile(element)(directiveScope);

        harness.injected.$rootScope.$digest();
        harness.pageObjects = {};

        $('body').append(element);
        harness.element = element;
        harness.scope = directiveScope;
        return harness;
    },
    service: function(serviceName) {
        this.inject(serviceName);
        var harness = this._getHarness();
        harness.service = harness.injected[serviceName];
        return harness;
    }
};
