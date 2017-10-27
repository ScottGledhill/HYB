angular.module('personalizationsmarteditComponentLightUpDecorator', ['personalizationsmarteditTemplates', 'personalizationsmarteditContextServiceModule', 'personalizationsmarteditCommons'])
    .directive('personalizationsmarteditComponentLightUp', function(personalizationsmarteditContextService, personalizationsmarteditUtils) {
        return {
            templateUrl: 'web/features/personalizationsmartedit/componentLightUpDecorator/personalizationsmarteditComponentLightUpDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@'
            },
            link: function($scope, element, attrs) {
                var isElementSelected = function() {
                    var ctxEnabled = personalizationsmarteditContextService.isPersonalizationContextEnabled();
                    ctxEnabled = ctxEnabled && !personalizationsmarteditContextService.getCombinedView().enabled;
                    var containerId = personalizationsmarteditUtils.getContainerIdForElement(element);
                    var elementSelected = $.inArray(containerId, personalizationsmarteditContextService.selectedComponents) > -1;
                    return ctxEnabled && elementSelected;
                };

                var isComponentSelected = function() {
                    var componentSelected = isElementSelected();
                    componentSelected = componentSelected && angular.isArray(personalizationsmarteditContextService.selectedVariations);
                    return componentSelected;
                };

                var isVariationComponentSelected = function() {
                    var componentSelected = isElementSelected();
                    componentSelected = componentSelected && angular.isObject(personalizationsmarteditContextService.selectedVariations);
                    componentSelected = componentSelected && !angular.isArray(personalizationsmarteditContextService.selectedVariations);
                    return componentSelected;
                };

                $scope.getPersonalizationComponentBorderClass = function() {
                    var container = element.parent().closest('[class~="smartEditComponentX"][data-smartedit-container-id][data-smartedit-container-type="CxCmsComponentContainer"]');
                    container.toggleClass("personalizationsmarteditVariationComponentSelected", isVariationComponentSelected());
                    container.toggleClass("personalizationsmarteditVariationComponentSelected-icon", isVariationComponentSelected());
                    container.toggleClass("personalizationsmarteditComponentSelected", isComponentSelected());
                };
            }
        };
    });
