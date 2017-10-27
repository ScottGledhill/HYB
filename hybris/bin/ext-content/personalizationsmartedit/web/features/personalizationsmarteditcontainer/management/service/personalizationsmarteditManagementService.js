angular.module('personalizationsmarteditManagementServiceModule', ['personalizationsmarteditRestServiceModule',
        'sharedDataServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule'
    ])
    .factory('personalizationsmarteditManagementService', function(personalizationsmarteditRestService) {
        var ManagementService = function() {};

        ManagementService.getSegments = function(filter) {
            return personalizationsmarteditRestService.getSegments(filter);
        };

        ManagementService.getCustomization = function(customizationCode) {
            return personalizationsmarteditRestService.getCustomization(customizationCode);
        };

        ManagementService.getVariation = function(customizationCode, variationCode) {
            return personalizationsmarteditRestService.getVariation(customizationCode, variationCode);
        };

        ManagementService.createCustomization = function(customization) {
            return personalizationsmarteditRestService.createCustomization(customization);
        };

        ManagementService.updateCustomizationPackage = function(customization) {
            return personalizationsmarteditRestService.updateCustomizationPackage(customization);
        };


        ManagementService.createVariationForCustomization = function(customizationCode, variation) {
            return personalizationsmarteditRestService.createVariationForCustomization(customizationCode, variation);
        };

        ManagementService.getVariationsForCustomization = function(customizationCode, filter) {
            return personalizationsmarteditRestService.getVariationsForCustomization(customizationCode, filter);
        };

        return ManagementService;
    });
