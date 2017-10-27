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
describe('PreviewDataDropdownPopulatorModule Test - ', function() {

    var previewDatapreviewCatalogDropdownPopulator, previewDatalanguageDropdownPopulator, $rootScope, $q;

    var siteService, catalogService, languageService, l10nFilter;

    var siteDescriptors = [{
        uid: 'siteId1'
    }, {
        uid: 'siteId2'
    }, {
        uid: 'siteId3'
    }];

    var catalogVersionDescriptors = [{
        siteId: 'siteId1',
        catalogId: 'myCatalogId1',
        catalogVersion: 'myCatalogVersion1',
        name: "myCatalog1"
    }, {
        siteId: 'siteId1',
        catalogId: 'myCatalogId2',
        catalogVersion: 'myCatalogVersion2',
        name: "myCatalog2"
    }, {
        siteId: 'siteId2',
        catalogId: 'myCatalogId3',
        catalogVersion: 'myCatalogVersion3',
        name: "myCatalog3"
    }, {
        siteId: 'siteId3',
        catalogId: 'myCatalogId4',
        catalogVersion: 'myCatalogVersion4',
        name: "myCatalog4"
    }, {
        siteId: 'siteId4',
        catalogId: 'myCatalogId5',
        catalogVersion: 'myCatalogVersion5',
        name: "myCatalog5"
    }];

    var languageDescriptors = [{
        isocode: 'en',
        nativeName: 'English'
    }, {
        isocode: 'hi',
        nativeName: 'Hindi'
    }, {
        isocode: 'te',
        nativeName: 'Telugu'
    }];

    var l10nFilterFunction = function() {
        return 'catalogName';
    };

    beforeEach(customMatchers);
    beforeEach(module('previewDataDropdownPopulatorModule', function($provide) {

        siteService = jasmine.createSpyObj('siteService', ['getSites']);
        $provide.value('siteService', siteService);

        catalogService = jasmine.createSpyObj('catalogService', ['getCatalogsForSite']);
        $provide.value('catalogService', catalogService);

        languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite']);
        $provide.value('languageService', languageService);

        $provide.value('l10nFilter', l10nFilterFunction);

    }));

    beforeEach(inject(function(_previewDatapreviewCatalogDropdownPopulator_, _previewDatalanguageDropdownPopulator_, _$rootScope_, _$q_) {
        previewDatapreviewCatalogDropdownPopulator = _previewDatapreviewCatalogDropdownPopulator_;
        previewDatalanguageDropdownPopulator = _previewDatalanguageDropdownPopulator_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));

    it('GIVEN siteService returns a resolved promise WHEN previewDatapreviewCatalogDropdownPopulator.populate is called THEN it will return a list of catalog ID - catalog versions', function() {

        //GIVEN
        siteService.getSites.andReturn($q.when(siteDescriptors));
        catalogService.getCatalogsForSite.andCallFake(function(siteId) {
            return $q.when(catalogVersionDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor.siteId === siteId;
            }));
        });

        //WHEN
        var catalogsPromise = previewDatapreviewCatalogDropdownPopulator.populate({});

        //THEN
        expect(catalogsPromise).toBeResolvedWithData([{
            id: 'siteId1_myCatalogId1_myCatalogVersion1',
            label: 'catalogName - myCatalogVersion1'
        }, {
            id: 'siteId1_myCatalogId2_myCatalogVersion2',
            label: 'catalogName - myCatalogVersion2'
        }, {
            id: 'siteId2_myCatalogId3_myCatalogVersion3',
            label: 'catalogName - myCatalogVersion3'
        }, {
            id: 'siteId3_myCatalogId4_myCatalogVersion4',
            label: 'catalogName - myCatalogVersion4'
        }]);

        expect(siteService.getSites).toHaveBeenCalled();
        expect(catalogService.getCatalogsForSite.calls.length).toBe(3);
        expect(catalogService.getCatalogsForSite.calls[0].args).toEqual(['siteId1']);
        expect(catalogService.getCatalogsForSite.calls[1].args).toEqual(['siteId2']);
        expect(catalogService.getCatalogsForSite.calls[2].args).toEqual(['siteId3']);

    });

    it('GIVEN siteService returns a resolved promise WHEN previewDatapreviewCatalogDropdownPopulator.populate is called with a search string THEN it will return a list of catalog ID - catalog versions filtered based on the search string', function() {

        //GIVEN
        siteService.getSites.andReturn($q.when(siteDescriptors));
        catalogService.getCatalogsForSite.andCallFake(function(siteId) {
            return $q.when(catalogVersionDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor.siteId === siteId;
            }));
        });

        var payload = {
            search: 'myCatalogVersion1'
        };

        //WHEN
        var catalogsPromise = previewDatapreviewCatalogDropdownPopulator.populate(payload);

        //THEN
        expect(catalogsPromise).toBeResolvedWithData([{
            id: 'siteId1_myCatalogId1_myCatalogVersion1',
            label: 'catalogName - myCatalogVersion1'
        }]);

    });

    it('GIVEN siteService returns a rejected promise WHEN previewDatapreviewCatalogDropdownPopulator.populate is called THEN it will return a rejected promise', function() {

        //GIVEN
        siteService.getSites.andReturn($q.reject(siteDescriptors));
        catalogService.getCatalogsForSite.andCallFake(function(siteId) {
            return $q.when(catalogVersionDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor.siteId === siteId;
            }));
        });

        //WHEN
        var catalogsPromise = previewDatapreviewCatalogDropdownPopulator.populate({});

        //THEN
        expect(catalogsPromise).toBeRejected();
        expect(siteService.getSites).toHaveBeenCalled();
        expect(catalogService.getCatalogsForSite).not.toHaveBeenCalled();

    });

    it('GIVEN a correct siteId WHEN previewDatalanguageDropdownPopulator.populate is called THEN populate will return a list of associated languages', function() {

        //GIVEN
        languageService.getLanguagesForSite.andReturn($q.when(languageDescriptors));
        spyOn(previewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').andCallThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteId1_myCatalogId1_myCatalogVersion1'
            }
        };

        //WHEN
        var languagesPromise = previewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeResolvedWithData([{
            id: 'en',
            label: 'English'
        }, {
            id: 'hi',
            label: 'Hindi'
        }, {
            id: 'te',
            label: 'Telugu'
        }]);

        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('siteId1');
        expect(previewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteId1', undefined);

    });

    it('GIVEN a correct siteId WHEN previewDatalanguageDropdownPopulator.populate is called with a search string THEN populate will return a list of associated languages filtered based on the search string', function() {

        //GIVEN
        languageService.getLanguagesForSite.andReturn($q.when(languageDescriptors));
        spyOn(previewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').andCallThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteId1_myCatalogId1_myCatalogVersion1'
            },
            search: 'te'
        };

        //WHEN
        var languagesPromise = previewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeResolvedWithData([{
            id: 'te',
            label: 'Telugu'
        }]);
        expect(previewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteId1', 'te');

    });

    it('GIVEN a wrong siteId WHEN previewDatalanguageDropdownPopulator.populate is called THEN populate will return a rejected promise', function() {

        //GIVEN
        languageService.getLanguagesForSite.andReturn($q.reject(languageDescriptors));
        spyOn(previewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').andCallThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteIdX_myCatalogId1_myCatalogVersion1'
            }
        };

        //WHEN
        var languagesPromise = previewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeRejected();
        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('siteIdX');
        expect(previewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteIdX', undefined);

    });



});
