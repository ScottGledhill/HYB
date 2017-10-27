angular.module('personalizationsmarteditCommons')
    .directive('personalizationInfiniteScroll', ['$rootScope', '$window', '$timeout', function($rootScope, $window, $timeout) {
        return {
            link: function(scope, elem, attrs) {
                var checkWhenEnabled, handler, scrollDistance, scrollEnabled;
                $window = angular.element($window);
                scrollDistance = 0;
                if (attrs.personalizationInfiniteScrollDistance !== null) {
                    scope.$watch(attrs.personalizationInfiniteScrollDistance, function(value) {
                        return (scrollDistance = parseInt(value, 10));
                    });
                }
                scrollEnabled = true;
                checkWhenEnabled = false;
                if (attrs.personalizationInfiniteScrollDisabled !== null) {
                    scope.$watch(attrs.personalizationInfiniteScrollDisabled, function(value) {
                        scrollEnabled = !value;
                        if (scrollEnabled && checkWhenEnabled) {
                            checkWhenEnabled = false;
                            return handler();
                        }
                    });
                }
                $rootScope.$on('refreshStart', function() {
                    elem.animate({
                        scrollTop: "0"
                    });
                });
                handler = function() {
                    var container, elementBottom, remaining, shouldScroll, containerBottom;
                    if (elem.children().length <= 0) {
                        return;
                    }
                    container = $(elem.children()[0]);
                    elementBottom = elem.offset().top + elem.height();
                    containerBottom = container.offset().top + container.height();
                    remaining = containerBottom - elementBottom;
                    shouldScroll = remaining <= elem.height() * scrollDistance;
                    if (shouldScroll && scrollEnabled) {
                        if ($rootScope.$$phase) {
                            return scope.$eval(attrs.personalizationInfiniteScroll);
                        } else {
                            return scope.$apply(attrs.personalizationInfiniteScroll);
                        }
                    } else if (shouldScroll) {
                        return (checkWhenEnabled = true);
                    }
                };
                elem.on('scroll', handler);
                scope.$on('$destroy', function() {
                    return $window.off('scroll', handler);
                });
                return $timeout((function() {
                    if (attrs.personalizationInfiniteScrollImmediateCheck) {
                        if (scope.$eval(attrs.personalizationInfiniteScrollImmediateCheck)) {
                            return handler();
                        }
                    } else {
                        return handler();
                    }
                }), 0);
            }
        };
    }]);
