describe('Test Personalizationsmartedit Container Module', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var mockVariation = {
        code: "testVariation"
    };
    var mockCustomization = {
        code: "testCustomization",
        variations: [mockVariation]
    };
    var mockComponentList = ['component1', 'component2'];

    var personalizationsmarteditPageCustomizationsToolbarController, scope, personalizationsmarteditContextService;

    beforeEach(module('personalizationsmarteditRestServiceModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCustomizations', 'getComponenentsIdsForVariation', 'getVariationsForCustomization']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditPreviewServiceModule', function($provide) {
        mockModules.personalizationsmarteditPreviewService = jasmine.createSpyObj('personalizationsmarteditPreviewService', ['updatePreviewTicketWithVariations']);
        $provide.value('personalizationsmarteditPreviewService', mockModules.personalizationsmarteditPreviewService);
    }));

    beforeEach(module('personalizationsmarteditManagerModule', function($provide) {
        mockModules.personalizationsmarteditManager = jasmine.createSpyObj('personalizationsmarteditManager', ['openCreateCustomizationModal']);
        $provide.value('personalizationsmarteditManager', mockModules.personalizationsmarteditManager);
    }));

    beforeEach(module('personalizationsmarteditDataFactory', function($provide) {
        mockModules.customizationDataFactory = jasmine.createSpyObj('customizationDataFactory', ['updateData', 'resetData', 'items']);
        $provide.value('customizationDataFactory', mockModules.customizationDataFactory);
    }));

    beforeEach(module('personalizationsmarteditcontainermodule'));
    beforeEach(inject(function($controller, _$rootScope_, _$q_, _$timeout_, _personalizationsmarteditContextService_) {
        scope = _$rootScope_.$new();
        $timeout = _$timeout_;
        mockModules.personalizationsmarteditRestService.getCustomizations.andCallFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                customizations: [mockCustomization, mockCustomization],
                pagination: {
                    count: 5,
                    page: 0,
                    totalCount: 5,
                    totalPages: 1
                }
            });
            return deferred.promise;
        });
        mockModules.personalizationsmarteditRestService.getComponenentsIdsForVariation.andCallFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                components: mockComponentList
            });
            return deferred.promise;
        });
        mockModules.personalizationsmarteditRestService.getVariationsForCustomization.andReturn(
            _$q_.defer().promise
        );
        mockModules.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations.andCallFake(function() {
            return _$q_.defer().promise;
        });
        mockModules.customizationDataFactory.items = [];
        mockModules.customizationDataFactory.updateData.andCallFake(function() {
            return mockModules.personalizationsmarteditRestService.getCustomizations().then(function(response) {
                angular.copy(response.customizations, mockModules.customizationDataFactory.items);
            });
        });
        personalizationsmarteditPageCustomizationsToolbarController = $controller('personalizationsmarteditPageCustomizationsToolbarController', {
            $scope: scope
        });
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;
        spyOn(personalizationsmarteditContextService, 'getSePreviewData').andCallFake(function() {
            return {
                previewTicketId: "mockTicketId"
            };
        });
    }));

    describe('mockModules.featureService.addToolbarItem', function() {

        it('should have been called', function() {
            expect(mockModules.featureService.addToolbarItem).toHaveBeenCalled();
        });

    });

    describe('mockModules.featureService.register', function() {

        it('should have been called', function() {
            expect(mockModules.featureService.register).toHaveBeenCalled();
        });

    });

    describe('mockModules.perspectiveService.register', function() {

        it('should have been called', function() {
            expect(mockModules.perspectiveService.register).toHaveBeenCalled();
        });

    });

    describe('scope.customizationsOnPage', function() {

        it('should be instantianed and empty', function() {
            expect(scope.customizationsOnPage).toBeDefined();
            expect(scope.customizationsOnPage.length).toBe(0);
        });

    });

    describe('getCustomization', function() {

        it('after called array scope.customizations should contain objects return by REST service', function() {
            // when
            scope.addMoreCustomizationItems();
            scope.$digest();

            $timeout.flush();
            // then
            expect(scope.customizationsOnPage).toBeDefined();
            expect(scope.customizationsOnPage.length).toBe(2);
            expect(scope.customizationsOnPage).toContain(mockCustomization);
        });

    });

    describe('variationClick', function() {

        it('after called all objects in contex service are set properly', function() {
            // given
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(null);
            expect(personalizationsmarteditContextService.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.selectedComponents).toBe(null);
            // when
            scope.variationClick(mockCustomization, mockVariation);
            scope.$digest();
            // then
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(mockCustomization);
            expect(personalizationsmarteditContextService.selectedVariations).toBe(mockVariation);
            expect(personalizationsmarteditContextService.selectedComponents).toBe(mockComponentList);
        });

    });

    describe('customizationClick', function() {

        it('after called all objects in contex service are set properly', function() {
            // given
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(null);
            expect(personalizationsmarteditContextService.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.selectedComponents).toBe(null);
            // when
            scope.customizationClick(mockCustomization);
            scope.$digest();
            // then
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(mockCustomization);
            expect(personalizationsmarteditContextService.selectedVariations[0].code).toBe(mockCustomization.variations[0].code);
            expect(personalizationsmarteditContextService.selectedComponents).toBe(mockComponentList);
        });

    });

});
