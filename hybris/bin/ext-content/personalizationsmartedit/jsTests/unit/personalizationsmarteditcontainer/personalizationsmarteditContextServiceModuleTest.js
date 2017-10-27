describe('personalizationsmarteditContextServiceModule', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var mockConfig = {
        test: "test"
    };

    var personalizationsmarteditContextService, personalizationsmarteditContextServiceProxy, scope;

    beforeEach(module('personalizationsmarteditContextServiceModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _personalizationsmarteditContextService_) {
        mockModules.sharedDataService.get.andCallFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(mockConfig);
            return deferred.promise;
        });
        mockModules.loadConfigManagerService.loadAsObject.andCallFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(mockConfig);
            return deferred.promise;
        });
        scope = _$rootScope_.$new();
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;
        personalizationsmarteditContextServiceProxy = personalizationsmarteditContextService.getContexServiceProxy();

        //Create spy objects
        spyOn(personalizationsmarteditContextService, 'refreshExperienceData').andCallThrough();
        spyOn(personalizationsmarteditContextService, 'refreshPreviewData').andCallThrough();
        spyOn(personalizationsmarteditContextService, 'refreshConfigurationData').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSeExperienceData').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSePreviewData').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSeConfigurationData').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSelectedVariations').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSelectedCustomizations').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSelectedComponents').andCallThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setPersonalizationContextEnabled').andCallThrough();
    }));

    describe('applySynchronization', function() {

        it('after call all objects in contex service are set properly', function() {
            //After object creation properties should have default values
            expect(personalizationsmarteditContextService.seExperienceData).toBe(null);
            expect(personalizationsmarteditContextService.seConfigurationData).toBe(null);
            expect(personalizationsmarteditContextService.sePreviewData).toBe(null);
            expect(personalizationsmarteditContextService.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(null);
            expect(personalizationsmarteditContextService.selectedComponents).toBe(null);
            expect(personalizationsmarteditContextService.personalizationEnabled).toBe(false);

            //Set some mock properties
            personalizationsmarteditContextService.selectedVariations = "mockVariation";
            personalizationsmarteditContextService.selectedCustomizations = "mockCustomization";
            personalizationsmarteditContextService.selectedComponents = "mockComponent";
            personalizationsmarteditContextService.personalizationEnabled = true;

            //Call method and run digest cycle
            personalizationsmarteditContextService.applySynchronization();
            scope.$digest();

            //Test if methods have been called properly
            expect(personalizationsmarteditContextService.refreshExperienceData).toHaveBeenCalled();
            expect(personalizationsmarteditContextService.refreshPreviewData).toHaveBeenCalled();
            expect(personalizationsmarteditContextService.refreshConfigurationData).toHaveBeenCalled();
            expect(personalizationsmarteditContextServiceProxy.setSelectedVariations).toHaveBeenCalledWith("mockVariation");
            expect(personalizationsmarteditContextServiceProxy.setSelectedCustomizations).toHaveBeenCalledWith("mockCustomization");
            expect(personalizationsmarteditContextServiceProxy.setSelectedComponents).toHaveBeenCalledWith("mockComponent");
            expect(personalizationsmarteditContextServiceProxy.setPersonalizationContextEnabled).toHaveBeenCalledWith(true);

            //Test if properties are set properly
            expect(personalizationsmarteditContextService.seExperienceData).toBe(mockConfig);
            expect(personalizationsmarteditContextService.seConfigurationData).toBe(mockConfig);
            expect(personalizationsmarteditContextService.sePreviewData).toBe(mockConfig);
        });

    });

    describe('setPersonalizationContextEnabled', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setPersonalizationContextEnabled(mockConfig);
            // then
            expect(personalizationsmarteditContextService.personalizationEnabled).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setPersonalizationContextEnabled(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setPersonalizationContextEnabled).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSelectedComponents', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedComponents(mockConfig);
            // then
            expect(personalizationsmarteditContextService.selectedComponents).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedComponents(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSelectedComponents).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSelectedVariations', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedVariations(mockConfig);
            // then
            expect(personalizationsmarteditContextService.selectedVariations).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedVariations(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSelectedVariations).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSelectedCustomizations', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedCustomizations(mockConfig);
            // then
            expect(personalizationsmarteditContextService.selectedCustomizations).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSelectedCustomizations(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSelectedCustomizations).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSeExperienceData', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeExperienceData(mockConfig);
            // then
            expect(personalizationsmarteditContextService.seExperienceData).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeExperienceData(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSeExperienceData).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSePreviewData', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSePreviewData(mockConfig);
            // then
            expect(personalizationsmarteditContextService.sePreviewData).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSePreviewData(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSePreviewData).toHaveBeenCalledWith(mockConfig);
        });

    });

    describe('setSeConfigurationData', function() {

        it('should properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeConfigurationData(mockConfig);
            // then
            expect(personalizationsmarteditContextService.seConfigurationData).toBe(mockConfig);
        });

        it('should call proxy with same parameters properly set value', function() {
            // when
            personalizationsmarteditContextService.setSeConfigurationData(mockConfig);
            // then
            expect(personalizationsmarteditContextServiceProxy.setSeConfigurationData).toHaveBeenCalledWith(mockConfig);
        });

    });

});
