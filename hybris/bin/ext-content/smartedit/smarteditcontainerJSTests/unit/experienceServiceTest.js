/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('experienceService - ', function() {

    var $q, $rootScope, $location, experienceService;
    var siteService, catalogService, languageService, systemEventService, sharedDataService;
    var siteDescriptor, catalogVersionDescriptor, languageDescriptor;

    beforeEach(customMatchers);

    beforeEach(module('eventServiceModule', function($provide) {
        systemEventService = jasmine.createSpyObj('systemEventService', ['sendAsynchEvent', 'registerEventHandler']);
        systemEventService.registerEventHandler.andReturn(null);

        $provide.value("systemEventService", systemEventService);
    }));

    beforeEach(module('sharedDataServiceModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ["set", "get"]);
        $provide.value("sharedDataService", sharedDataService);

    }));

    beforeEach(module('experienceServiceModule', function($provide) {
        siteService = jasmine.createSpyObj('siteService', ['getSiteById']);
        siteDescriptor = {
            someProperty: Math.random()
        };

        $provide.value('siteService', siteService);

        catalogService = jasmine.createSpyObj('catalogService', ['getCatalogsForSite']);
        catalogVersionDescriptor = {
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        };

        $provide.value('catalogService', catalogService);

        languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite']);
        languageDescriptor = {
            someProperty: Math.random()
        };

        $provide.value('languageService', languageService);

    }));

    beforeEach(inject(function(_$q_, _$rootScope_, _$location_, _experienceService_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        experienceService = _experienceService_;
    }));

    it('GIVEN a pageId has been passed to the params WHEN I call buildDefaultExperience THEN it will return an experience with a pageId', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.when(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion',
            pageId: 'myPageId'
        });

        // THEN
        expect(promise).toBeResolvedWithData({
            siteDescriptor: siteDescriptor,
            catalogDescriptor: catalogVersionDescriptor,
            languageDescriptor: languageDescriptor,
            time: null,
            pageId: 'myPageId'
        });


    });

    it('GIVEN pageId has not been passed to the params WHEN I call buildDefaultExperience THEN it will return an experience without a pageId', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.when(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeResolvedWithData({
            siteDescriptor: siteDescriptor,
            catalogDescriptor: catalogVersionDescriptor,
            languageDescriptor: languageDescriptor,
            time: null
        });

    });

    it('GIVEN a siteId, catalogId and catalogVersion, buildDefaultExperience will reconstruct an experience', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.when(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeResolvedWithData({
            siteDescriptor: siteDescriptor,
            catalogDescriptor: catalogVersionDescriptor,
            languageDescriptor: languageDescriptor,
            time: null
        });

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('mySiteId');
    });

    it('GIVEN a siteId, catalogId and unknown catalogVersion, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.when(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'unknownVersion'
        });

        // THEN
        expect(promise).toBeRejectedWithData('no catalogVersionDescriptor found for myCatalogId catalogId and unknownVersion catalogVersion');

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('GIVEN a siteId, unknown catalogId and right catalogVersion, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.when(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'unknownCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeRejectedWithData('no catalogVersionDescriptor found for unknownCatalogId catalogId and myCatalogVersion catalogVersion');

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('GIVEN a wrong siteId, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.andReturn($q.reject(siteDescriptor));
        catalogService.getCatalogsForSite.andReturn($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.andReturn($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeRejected();

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getCatalogsForSite).not.toHaveBeenCalled();
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('WHEN updateExperiencePageID is called THEN it retrieves the current experience, changes it and re-initializes the catalog', function() {
        // Arrange
        var newPageId = 'some new page ID';
        var oldPageId = 'some old page ID';
        var url = 'some url';

        var experience = {
            pageId: oldPageId
        };
        var newExperience = {
            pageId: newPageId
        };

        sharedDataService.get.andReturn($q.when(experience));
        spyOn(experienceService, 'getExperiencePath').andReturn(url);
        spyOn($location, 'path').andReturn({
            replace: function() {}
        });

        // Act
        experienceService.updateExperiencePageId(newPageId);
        $rootScope.$digest();

        // Assert
        expect(sharedDataService.get).toHaveBeenCalledWith('experience');
        expect(experienceService.getExperiencePath).toHaveBeenCalledWith(newExperience);
        expect($location.path).toHaveBeenCalledWith(url);
    });

    it('WHEN getExperiencePath is called THEN it returns the right URL', function() {
        // Arrange
        var siteId = 'someSite';
        var catalogId = 'someCatalog';
        var catalogVersion = 'someVersion';
        var pageId = 'somePageId';

        var experience = {
            siteDescriptor: {
                uid: siteId
            },
            catalogDescriptor: {
                catalogId: catalogId,
                catalogVersion: catalogVersion
            },
            pageId: pageId
        };

        // Act
        var result = experienceService.getExperiencePath(experience);

        // Assert
        expect(result).toBe('/storefront/' + siteId + '/' + catalogId + "/" + catalogVersion + '/' + pageId);
    });
});
