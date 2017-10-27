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
(function() {
    angular.module('sakExecutorDecorator', ['coretemplates', 'decoratorServiceModule', 'componentHandlerServiceModule', 'yLoDashModule'])
        .factory('sakExecutor', function($compile, decoratorService, NUM_SE_SLOTS, lodash) {
            var ATTR_DATA = 'data-';
            var ATTR_SMARTEDIT = 'smartedit';
            var ATTR_DATA_SMARTEDIT = ATTR_DATA + ATTR_SMARTEDIT;
            var expectedNumElements = NUM_SE_SLOTS;

            var processedElements = {
                length: 0
            };
            var scopes = [];

            /*
             * Validates if a given attribute name present on the decorated element is eligible 
             * to be added as a smartedit property.
             */
            var isValidSmartEditAttribute = function(nodeName) {
                return lodash.startsWith(nodeName, ATTR_DATA_SMARTEDIT) || lodash.startsWith(nodeName, ATTR_SMARTEDIT);
            };

            /*
             * Parses the attribute name by removing ATTR_DATA prefix and 
             * converting to a camel case string representation.  
             */
            var parseAttributeName = function(nodeName) {
                if (lodash.startsWith(nodeName, ATTR_DATA)) {
                    nodeName = nodeName.substring(ATTR_DATA.length);
                }
                return lodash.camelCase(nodeName);
            };

            return {
                resetCounters: function(newNumElements) {
                    expectedNumElements = newNumElements;
                    processedElements = {
                        length: 0
                    };
                },
                wrapDecorators: function(transcludeFn, smarteditComponentId, smarteditComponentType, smartEditProperties) {

                    var decorators = decoratorService.getDecoratorsForComponent(smarteditComponentType);
                    var template = "<div data-ng-transclude></div>";

                    decorators.forEach(function(decorator) {
                        template = "<div class='" + decorator + "' data-active='active' data-smartedit-component-id='{{smarteditComponentId}}' " +
                            "data-smartedit-component-type='{{smarteditComponentType}}' data-smartedit-container-id='{{smarteditContainerId}}' " +
                            "data-smartedit-container-type='{{smarteditContainerType}}' data-smartedit-properties='{{smarteditProperties}}'>" + template;
                        template += "</div>";
                    });

                    return $compile(template, transcludeFn);
                },
                markDecoratorProcessed: function(type, id) {
                    var key = type + "_" + id;
                    if (!(key in processedElements)) {
                        processedElements[key] = key;
                        processedElements.length++;
                    }
                },
                areAllDecoratorsProcessed: function() {
                    //FIXME : inconsistency of expected and processed in new storefront
                    return processedElements.length >= 0;
                    //return processedElements.length >= expectedNumElements;
                },
                registerScope: function(scope) {
                    scopes.push(scope);
                },
                destroyAllScopes: function() {
                    scopes.forEach(function(scope) {
                        scope.$destroy();
                    });
                    scopes = [];
                },
                /*
                 * Inspect the decorated element by scanning its attributes and 
                 * collecting any 'smartedit' property present in it. 
                 * The resulting map is then added to the directive's scope. 
                 */
                prepareScope: function(scope, element) {
                    var smarteditProperties = {};
                    $(element.get(0).attributes).each(function() {
                        var nodeName = this.nodeName;
                        if (isValidSmartEditAttribute(nodeName)) {
                            nodeName = parseAttributeName(nodeName);
                            smarteditProperties[nodeName] = this.nodeValue;
                        }
                    });
                    scope.smarteditProperties = smarteditProperties;
                }
            };
        })
        .directive('smartEditComponentX', function($rootScope, $q, $timeout, sakExecutor) {
            // Constants
            var CONTENT_SLOT_TYPE = "ContentSlot";

            return {
                restrict: 'C',
                transclude: true,
                replace: false,
                scope: {
                    smarteditComponentId: '@',
                    smarteditComponentType: '@',
                    smarteditContainerId: '@',
                    smarteditContainerType: '@'
                },
                link: function($scope, element, attrs, controller, transcludeFn) {

                    sakExecutor.registerScope($scope);

                    sakExecutor.prepareScope($scope, element);

                    $scope.active = false;
                    transcludeFn($scope, function(clone) {
                        var compiled = sakExecutor.wrapDecorators(transcludeFn, $scope.smarteditComponentId, $scope.smarteditComponentType, $scope.smarteditProperties);
                        element.append(compiled($scope));

                        var inactivateDecorator = function() {
                            $scope.active = false;
                        };

                        var activateDecorator = function() {
                            $scope.active = true;
                        };

                        // Register Event Listeners
                        element.bind("mouseleave", function($event) {
                            $rootScope.$apply(inactivateDecorator);
                        });

                        element.bind("mouseenter", function($event) {
                            $rootScope.$apply(activateDecorator);
                        });

                        if ($scope.smarteditComponentType === CONTENT_SLOT_TYPE) {
                            sakExecutor.markDecoratorProcessed($scope.smarteditComponentType, $scope.smarteditComponentId);
                        }

                    });
                }
            };
        });

})();
