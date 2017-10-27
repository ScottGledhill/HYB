describe('personalizationsmarteditManager', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var personalizationsmarteditManager, scope;

    beforeEach(module('personalizationsmarteditManagerModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$controller_, _personalizationsmarteditManager_) {
        scope = _$rootScope_.$new();
        personalizationsmarteditManager = _personalizationsmarteditManager_;
    }));

    describe('openCreateCustomizationModal', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManager.openCreateCustomizationModal).toBeDefined();
        });

    });

    describe('openEditCustomizationModal', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditManager.openEditCustomizationModal).toBeDefined();
        });

    });

});
