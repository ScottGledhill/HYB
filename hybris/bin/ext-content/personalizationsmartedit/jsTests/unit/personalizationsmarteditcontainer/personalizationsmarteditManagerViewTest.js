describe('personalizationsmarteditManagerViewModule', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var personalizationsmarteditManagerView, personalizationsmarteditManagerViewController, personalizationsmarteditContextService, scope;

    beforeEach(module('personalizationsmarteditContextServiceModule', function($provide) {
        personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getSeExperienceData']);
        $provide.value("personalizationsmarteditContextService", personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditManagerViewModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$controller_, _personalizationsmarteditManagerView_, _personalizationsmarteditContextService_) {
        scope = _$rootScope_.$new();
        personalizationsmarteditManagerView = _personalizationsmarteditManagerView_;
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;

        mockModules.modalService.open.andCallFake(function() {
            return _$q_.defer().promise;
        });

        mockModules.confirmationModalService.confirm.andCallFake(function() {
            return _$q_.defer().promise;
        });

        personalizationsmarteditContextService.getSeExperienceData.andCallFake(function() {
            return {
                catalogDescriptor: {
                    name: {
                        en: "testName"
                    },
                    catalogVersion: "testOnline"
                },
                languageDescriptor: {
                    isocode: "en",
                }
            };
        });

        personalizationsmarteditManagerViewController = _$controller_('personalizationsmarteditManagerViewController', {
            '$scope': scope
        });

    }));

    describe('openManagerAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManagerView.openManagerAction).toBeDefined();
        });

        it('after called it is calling proper services', function() {
            personalizationsmarteditManagerView.openManagerAction();
            expect(mockModules.modalService.open).toHaveBeenCalled();
        });

    });

    describe('personalizationsmarteditManagerViewController', function() {

        it('after instantiation scope should be initialized properly', function() {
            expect(scope.catalogName).toBeDefined();
            expect(scope.customizations).toBeDefined();
            expect(scope.allCustomizationsCount).not.toBeDefined();
            expect(scope.filteredCustomizationsCount).toBeDefined();
            expect(scope.search.name).toBeDefined();
            expect(scope.pagination).toBeDefined();
            expect(scope.searchInputKeypress).toBeDefined();
            expect(scope.editCustomizationAction).toBeDefined();
            expect(scope.deleteCustomizationAction).toBeDefined();
            expect(scope.editVariationAction).toBeDefined();
            expect(scope.deleteVariationAction).toBeDefined();
            expect(scope.openNewModal).toBeDefined();
            expect(scope.paginationCallback).toBeDefined();
            expect(scope.setCustomizationRank).toBeDefined();
            expect(scope.setVariationRank).toBeDefined();
            expect(scope.isUndefined).toBeDefined();
            expect(scope.isFilterEnabled).toBeDefined();
            expect(scope.resetSearchInput).toBeDefined();
        });

    });

    describe('personalizationsmarteditManagerViewController.deleteCustomizationAction', function() {

        it('after called it is calling proper services', function() {
            scope.deleteCustomizationAction();
            expect(mockModules.confirmationModalService.confirm).toHaveBeenCalled();
        });

    });

    describe('personalizationsmarteditManagerViewController.deleteVariationAction', function() {

        it('after called it is calling proper services', function() {
            // given
            var variation1 = {
                code: "var1",
                status: "a"
            };
            var variation2 = {
                code: "var2",
                status: "a"
            };
            var customization = {
                code: "test",
                variations: [variation1, variation2]
            };
            var f = function() {};
            var event = {
                stopPropagation: f
            };
            // when
            scope.deleteVariationAction(customization, variation1, event);
            // then
            expect(mockModules.confirmationModalService.confirm).toHaveBeenCalled();
        });

    });

});
