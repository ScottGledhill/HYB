angular.module('InnerMocks', ['ngMockE2E', 'resourceLocationsModule', 'languageServiceModule'])
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {

        $httpBackend
            .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
            .respond({});

    });
