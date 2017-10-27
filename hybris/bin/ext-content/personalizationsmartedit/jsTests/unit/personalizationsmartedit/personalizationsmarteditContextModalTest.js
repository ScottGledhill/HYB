describe('personalizationsmarteditContextModal', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var personalizationsmarteditContextModal;

    beforeEach(module('personalizationsmarteditContextMenu'));
    beforeEach(inject(function(_personalizationsmarteditContextModal_) {
        personalizationsmarteditContextModal = _personalizationsmarteditContextModal_;
    }));

    describe('openDeleteAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openDeleteAction).toBeDefined();
        });

    });

    describe('openAddAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openAddAction).toBeDefined();
        });

    });

    describe('openEditAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openEditAction).toBeDefined();
        });

    });

    describe('openInfoAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openInfoAction).toBeDefined();
        });

    });

});
