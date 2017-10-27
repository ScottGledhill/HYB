describe('personalizationsmarteditContextService', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var personalizationsmarteditContextService;

    beforeEach(module('personalizationsmarteditContextServiceModule'));
    beforeEach(inject(function(_personalizationsmarteditContextService_) {
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;
    }));

    describe('seExperienceData', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.seExperienceData).toBeDefined();
            expect(personalizationsmarteditContextService.seExperienceData).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeExperienceData("mock1");
            // then
            expect(personalizationsmarteditContextService.seExperienceData).toBe("mock1");
        });

    });

    describe('seConfigurationData', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.seConfigurationData).toBeDefined();
            expect(personalizationsmarteditContextService.seConfigurationData).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeConfigurationData("mock2");
            // then
            expect(personalizationsmarteditContextService.seConfigurationData).toBe("mock2");
        });

    });

    describe('sePreviewData', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.sePreviewData).toBeDefined();
            expect(personalizationsmarteditContextService.sePreviewData).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSePreviewData("mock3");
            // then
            expect(personalizationsmarteditContextService.sePreviewData).toBe("mock3");
        });

    });

    describe('selectedVariations', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.selectedVariations).toBeDefined();
            expect(personalizationsmarteditContextService.selectedVariations).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedVariations("mock4");
            // then
            expect(personalizationsmarteditContextService.selectedVariations).toBe("mock4");
        });

    });

    describe('selectedCustomizations', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.selectedCustomizations).toBeDefined();
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedCustomizations("mock5");
            // then
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe("mock5");
        });

    });

    describe('selectedComponents', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.selectedComponents).toBeDefined();
            expect(personalizationsmarteditContextService.selectedComponents).toBe(null);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedComponents("mock6");
            // then
            expect(personalizationsmarteditContextService.selectedComponents).toBe("mock6");
        });

    });

    describe('personalizationEnabled', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.personalizationEnabled).toBeDefined();
            expect(personalizationsmarteditContextService.personalizationEnabled).toBe(false);
        });

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setPersonalizationContextEnabled(true);
            // then
            expect(personalizationsmarteditContextService.personalizationEnabled).toBe(true);
        });

    });

});
