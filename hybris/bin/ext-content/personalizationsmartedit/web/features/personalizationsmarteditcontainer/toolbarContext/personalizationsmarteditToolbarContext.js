angular.module('personalizationsmarteditcontainermodule')
    .controller('toolbarContextController', function($scope, $timeout, personalizationsmarteditContextService, personalizationsmarteditIFrameUtils, personalizationsmarteditUtils) {

        $scope.$watch(function() {
            return personalizationsmarteditContextService.selectedCustomizations;
        }, function(newValue) {
            $scope.selectedCustomization = newValue;
        });

        $scope.$watch(function() {
            return personalizationsmarteditContextService.selectedVariations;
        }, function(newValue) {
            $scope.selectedVariation = newValue;
        });

        $scope.clearSelection = function() {
            personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
            $timeout((function() {
                angular.element(".personalizationsmarteditTopToolbarCustomizeButton[aria-expanded='true']").click();
            }), 0);
        };

    });
