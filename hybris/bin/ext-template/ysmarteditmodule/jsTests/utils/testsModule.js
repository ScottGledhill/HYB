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
customMatchers = function() {
    var PromiseMatcherHelper = {
        states: {
            RESOLVED: 'resolved',
            REJECTED: 'rejected'
        },
        getPromiseInfo: function(promise) {
            var that = this;
            var rootScope;
            angular.mock.inject(function($injector) {
                rootScope = $injector.get('$rootScope');
            });

            var promiseInfo = {};
            promise.then(function(data) {
                promiseInfo.status = that.states.RESOLVED;
                promiseInfo.data = data;
            }, function(data) {
                promiseInfo.status = that.states.REJECTED;
                promiseInfo.data = data;
            });

            rootScope.$apply(); // Trigger promise resolution
            return promiseInfo;
        },
        getMessageForPromise: function(promiseInfo, expected) {
            return function() {
                var unresolvedMessage = 'Expected promise to be ' + promiseInfo.status;
                var badDataMessage = 'Expected promise resolved data ' + jasmine.pp(promiseInfo.data) + ' to be ' + jasmine.pp(expected);
                return promiseInfo.status !== PromiseMatcherHelper.states.RESOLVED ? unresolvedMessage : badDataMessage;
            };
        }
    };

    this.addMatchers({
        toEqualData: function(expected) {
            return angular.equals(this.actual, expected);
        },
        toHaveClass: function(className) {
            return this.actual.hasClass(className);
        },
        fail: function(errormessage) {
            this.actual = null;
            return false === true;
        },
        toHaveThatManyAlerts: function(count) {
            this.actual = this.actual.find("div.alert span").length;
            return this.actual === count;
        },
        messageToBe: function(errormessage) {
            this.actual = this.actual.find("div.alert-success span").text();
            return this.actual === errormessage;
        },
        alertToBe: function(errormessage) {
            this.actual = this.actual.find("div.alert-danger span").text();
            return this.actual === errormessage;
        },
        toContainChildElement: function(expected) {
            this.message = function() {
                var html = $("<div>").append(this.actual.clone()).html();
                return 'Expected ' + html + ' to have child element <' + expected + '>';
            };

            return this.actual.find(expected).length === 1;
        },
        toContainElementText: function(expected) {
            this.message = function() {
                var html = $("<div>").append(this.actual.clone()).html();
                return 'Expected ' + html + ' to contain text ' + expected;
            };

            return this.actual.text().indexOf(expected) >= 0;
        },
        inputToBe: function(value) {
            this.actual = this.actual.find("div input[type=text]").val();
            return this.actual === value;
        },
        displayToBe: function(value) {
            this.actual = this.actual.find('span').html();
            return this.actual === value;

        },
        flagToBeTrue: function(value) {
            this.actual = this.actual
                .find("> input[src='http/images/tick.png']").length;
            return this.actual == 1;

        },
        flagToBeFalse: function(value) {
            this.actual = this.actual
                .find("> input[src='http/images/no-tick.png']").length;
            return this.actual == 1;

        },
        flagToBeUndetermined: function(value) {
            this.actual = this.actual
                .find("> input[src='http/images/question.png']").length;
            return this.actual == 1;

        },
        toBeInEditMode: function() {
            this.actual = this.actual.find("> div > input[type=text][data-ng-model='editor.temp']").length;
            return this.actual === 1;
        },
        notToBeInEditMode: function() {
            this.actual = this.actual.find("> div input[type=text][data-ng-model='editor.temp']").length;
            return this.actual === 0;
        },
        calendarToBeDisplayed: function() {
            //this.actual = this.actual.find("> div  li > table").length;
            this.actual = this.actual.find("ul.dropdown-menu").css('display');
            return this.actual === 'block';
        },
        toHaveAttribute: function(attr, expected) {
            this.message = function() {
                var html = $("<div>").append(this.actual.clone()).html();
                return 'Expected ' + html + ' to have attribute ' + attr + ' as ' + expected;
            };
            return this.actual.attr(attr) === expected;
        },
        toBeRejected: function() {
            return PromiseMatcherHelper.getPromiseInfo(this.actual).status === PromiseMatcherHelper.states.REJECTED;
        },
        toBeResolved: function() {
            return PromiseMatcherHelper.getPromiseInfo(this.actual).status === PromiseMatcherHelper.states.RESOLVED;
        },
        toBeRejectedWithData: function(expected) {
            var promiseInfo = PromiseMatcherHelper.getPromiseInfo(this.actual);
            this.message = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
            return promiseInfo.status === PromiseMatcherHelper.states.REJECTED && angular.equals(promiseInfo.data, expected);
        },
        toBeResolvedWithData: function(expected) {
            var promiseInfo = PromiseMatcherHelper.getPromiseInfo(this.actual);
            this.message = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
            return promiseInfo.status === PromiseMatcherHelper.states.RESOLVED && angular.equals(promiseInfo.data, expected);
        },
        toExist: function() {
            return $(this.actual).length > 0;
        },
        toBeRejectedWithDataContaining: function(expected) {
            var promiseInfo = PromiseMatcherHelper.getPromiseInfo(this.actual);
            return promiseInfo.status === PromiseMatcherHelper.states.REJECTED && promiseInfo.data.some(function(data) {
                return angular.equals(data, expected);
            });
        },
        toBePromise: function() {
            return !!this.actual.then;
        }
    });
};

$.fn.extend({
    sendKeys: function(keys) {
        return this.each(function() {
            $(this).find("div input").val(keys).trigger('input');
        });
    },
    openCalendar: function() {
        return this.each(function() {
            $(this).find(".datepickerbutton").click();
        });
    },
    selectDate: function(dateNumber) {
        return this.each(function() {
            $(this).find("span:contains('" + dateNumber + "')").click();
        });
    },
    pressEnter: function() {
        return this.each(function() {
            // $(this).trigger('keypress')
            $(this).trigger($.Event('keypress', {
                which: 13
            }));
        });
    },
    reset: function() {
        return this.each(function() {
            $(this).find("input[type=image]").click();
        });
    }
});
$.extend({
    getOptions: function(element) {
        return element.find("div.dropdown span");
    },
    getTristateOptions: function(element) {
        return element.find("div.dropdown > span"); // > input[type=image]
    }

});

$.extend($.expr[':'], {
    "block": function(a, i, m) {
        return $(a).css("display") == "block";
    }
});

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
            var defaultDependencies = ['cmssmarteditContainerTemplates', 'cmssmarteditTemplates', 'ui.select', 'ui.bootstrap'];
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
