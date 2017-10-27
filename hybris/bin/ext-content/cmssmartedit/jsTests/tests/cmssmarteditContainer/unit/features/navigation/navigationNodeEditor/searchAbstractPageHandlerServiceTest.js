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
describe('SearchAbstractPageHandlerService - ', function() {

    var searchAbstractPageHandlerService;
    var pageListService, $q, $rootScope;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };
    var mask = '';

    beforeEach(customMatchers);
    beforeEach(module('functionsModule'));

    beforeEach(module('pageListServiceModule', function($provide) {
        pageListService = jasmine.createSpyObj('pageListService', ['getPageListForCatalog']);
        $provide.value('pageListService', pageListService);
    }));

    beforeEach(module('searchAbstractPageHandlerServiceModule'));

    beforeEach(inject(function(_searchAbstractPageHandlerService_, _$q_, _$rootScope_) {
        searchAbstractPageHandlerService = _searchAbstractPageHandlerService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('WHEN getSearchResults is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        pageListService.getPageListForCatalog.andReturn($q.when(pages));

        //WHEN
        $rootScope.$digest();
        expect(searchAbstractPageHandlerService.getSearchResults(mask, uriParams)).toBeResolvedWithData(resolvedPromiseData);
    });

    it('WHEN getSearchDropdownProperties is called THEN response should be returned and resolved with given data', function() {

        //WHEN
        var properties = searchAbstractPageHandlerService.getSearchDropdownProperties();

        //THEN
        expect(properties).toEqual(propertiesResponse);
    });


    var pages = [{
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "PageTemplate",
        name: "page1TitleSuffix",
        typeCode: "ContentPage",
        uid: "auid1"
    }, {
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "ActionTemplate",
        name: "welcomePage",
        typeCode: "ActionPage",
        uid: "uid2"
    }, {
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "PageTemplate",
        name: "Advertise",
        typeCode: "MyCustomType",
        uid: "uid3"
    }];

    var resolvedPromiseData = [{
        name: 'page1TitleSuffix',
        id: 'auid1',
        typeCode: 'ContentPage'
    }, {
        name: 'welcomePage',
        id: 'uid2',
        typeCode: 'ActionPage'
    }, {
        name: 'Advertise',
        id: 'uid3',
        typeCode: 'MyCustomType'
    }];

    var propertiesResponse = {
        templateUrl: 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/handlerTemplates/itemSearchHandlerTemplate.html',
        placeHolderI18nKey: 'navigationmanagement.navnode.node.entry.dropdown.page.search',
        isPaged: false
    };

});
