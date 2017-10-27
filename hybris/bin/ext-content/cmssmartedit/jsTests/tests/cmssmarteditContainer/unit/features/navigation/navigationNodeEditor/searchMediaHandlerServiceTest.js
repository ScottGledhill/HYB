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
describe('SearchMediaHandlerService - ', function() {

    var searchMediaHandlerService, MEDIA_PATH;
    var restServiceMediaSearchResource, restServiceMediaResource, $q, $rootScope;
    var uriParams = {
        CURRENT_CONTEXT_SITE_ID: 'siteId',
        CURRENT_CONTEXT_CATALOG: 'catalogId',
        CURRENT_CONTEXT_CATALOG_VERSION: 'catalogVersion'
    };

    var id = 'somepath/someId.png';
    var mask = 'amask';
    var pageSize = 10;
    var currentPage = 5;

    beforeEach(customMatchers);
    beforeEach(module('functionsModule'));
    beforeEach(module('restServiceFactoryModule', function($provide) {
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', restServiceFactory);

        restServiceMediaSearchResource = jasmine.createSpyObj('restServiceMediaSearchResource', ['get']);
        restServiceMediaResource = jasmine.createSpyObj('restServiceMediaResource', ['get']);

        restServiceFactory.get.andCallFake(function(path) {
            if (path === MEDIA_PATH) {
                return restServiceMediaSearchResource;
            } else if (path === '/cmswebservices/v1/catalogs/catalogId/versions/catalogVersion/media' + '/' + id) {
                return restServiceMediaResource;
            }
        });
    }));

    beforeEach(module('searchMediaHandlerServiceModule'));

    beforeEach(inject(function(_searchMediaHandlerService_, _$q_, _$rootScope_, _MEDIA_PATH_) {
        searchMediaHandlerService = _searchMediaHandlerService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        MEDIA_PATH = _MEDIA_PATH_;
    }));

    it('WHEN getSearchResults is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        restServiceMediaSearchResource.get.andReturn($q.when(getMedias()));

        //THEN
        expect(searchMediaHandlerService.getSearchResults(mask, uriParams)).toBeResolvedWithData(resolvedPromiseData);

        expect(restServiceMediaSearchResource.get).toHaveBeenCalledWith({
            namedQuery: 'namedQueryMediaSearchByCodeCatalogVersion',
            params: 'catalogId:catalogId,catalogVersion:catalogVersion,code:amask',
            pageSize: undefined,
            currentPage: undefined
        });
    });

    it('WHEN getPage is called with a mask THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        restServiceMediaSearchResource.get.andReturn($q.when(getMedias()));

        //THEN
        expect(searchMediaHandlerService.getPage(mask, pageSize, currentPage, uriParams)).toBeResolvedWithData({
            results: resolvedPromiseData
        });

        expect(restServiceMediaSearchResource.get).toHaveBeenCalledWith({
            namedQuery: 'namedQueryMediaSearchByCodeCatalogVersion',
            params: 'catalogId:catalogId,catalogVersion:catalogVersion,code:amask',
            pageSize: 10,
            currentPage: 5
        });
    });

    it('WHEN getPage is called with no mask THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        restServiceMediaSearchResource.get.andReturn($q.when(getMedias()));

        //THEN
        expect(searchMediaHandlerService.getPage("", pageSize, currentPage, uriParams)).toBeResolvedWithData({
            results: resolvedPromiseData
        });

        expect(restServiceMediaSearchResource.get).toHaveBeenCalledWith({
            namedQuery: 'namedQueryMediaSearchByCodeCatalogVersion',
            params: 'catalogId:catalogId,catalogVersion:catalogVersion',
            pageSize: 10,
            currentPage: 5
        });
    });

    it('WHEN getItem is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        restServiceMediaResource.get.andReturn($q.when(media1));

        //THEN
        expect(searchMediaHandlerService.getItem(id, uriParams)).toBeResolvedWithData({
            id: 'contextualmenu_delete_off',
            code: 'contextualmenu_delete_off',
            description: 'contextualmenu_delete_off',
            altText: 'contextualmenu_delete_off alttext',
            url: '/web/webroot/images/contextualmenu_delete_off.png',
            downloadUrl: '/web/webroot/images/contextualmenu_delete_off_downloadUrl.png'
        });

        expect(restServiceMediaResource.get).toHaveBeenCalledWith();
    });


    it('WHEN getSearchDropdownProperties is called THEN response should be returned and resolved with given data', function() {

        //WHEN
        var properties = searchMediaHandlerService.getSearchDropdownProperties();

        //THEN
        expect(properties).toEqual(propertiesResponse);
    });

    var media1 = {
        id: '1',
        code: 'contextualmenu_delete_off',
        description: 'contextualmenu_delete_off',
        altText: 'contextualmenu_delete_off alttext',
        realFileName: 'contextualmenu_delete_off.png',
        url: '/web/webroot/images/contextualmenu_delete_off.png',
        downloadUrl: '/web/webroot/images/contextualmenu_delete_off_downloadUrl.png'
    };

    var getMedias = function() {

        return {
            "media": [media1, {
                id: '2',
                code: 'contextualmenu_delete_on',
                description: 'contextualmenu_delete_on',
                altText: 'contextualmenu_delete_on alttext',
                realFileName: 'contextualmenu_delete_on.png',
                url: '/web/webroot/images/contextualmenu_delete_on.png',
                downloadUrl: '/web/webroot/images/contextualmenu_delete_on_downloadUrl.png'
            }, {
                id: '3',
                code: 'contextualmenu_edit_off',
                description: 'contextualmenu_edit_off',
                altText: 'contextualmenu_edit_off alttext',
                realFileName: 'contextualmenu_edit_off.png',
                url: '/web/webroot/images/contextualmenu_edit_off.png',
                downloadUrl: '/web/webroot/images/contextualmenu_edit_off_downloadUrl.png'
            }, {
                id: '3',
                code: 'contextualmenu_edit_on',
                description: 'contextualmenu_edit_on',
                altText: 'contextualmenu_edit_on alttext',
                realFileName: 'contextualmenu_edit_on.png',
                url: '/web/webroot/images/contextualmenu_edit_on.png',
                downloadUrl: '/web/webroot/images/contextualmenu_edit_on_downloadUrl.png'
            }]
        };
    };

    var resolvedPromiseData = [{
        id: 'contextualmenu_delete_off',
        code: 'contextualmenu_delete_off',
        description: 'contextualmenu_delete_off',
        altText: 'contextualmenu_delete_off alttext',
        url: '/web/webroot/images/contextualmenu_delete_off.png',
        downloadUrl: '/web/webroot/images/contextualmenu_delete_off_downloadUrl.png'
    }, {
        id: 'contextualmenu_delete_on',
        code: 'contextualmenu_delete_on',
        description: 'contextualmenu_delete_on',
        altText: 'contextualmenu_delete_on alttext',
        url: '/web/webroot/images/contextualmenu_delete_on.png',
        downloadUrl: '/web/webroot/images/contextualmenu_delete_on_downloadUrl.png'
    }, {
        id: 'contextualmenu_edit_off',
        code: 'contextualmenu_edit_off',
        description: 'contextualmenu_edit_off',
        altText: 'contextualmenu_edit_off alttext',
        url: '/web/webroot/images/contextualmenu_edit_off.png',
        downloadUrl: '/web/webroot/images/contextualmenu_edit_off_downloadUrl.png'
    }, {
        id: 'contextualmenu_edit_on',
        code: 'contextualmenu_edit_on',
        description: 'contextualmenu_edit_on',
        altText: 'contextualmenu_edit_on alttext',
        url: '/web/webroot/images/contextualmenu_edit_on.png',
        downloadUrl: '/web/webroot/images/contextualmenu_edit_on_downloadUrl.png'
    }];

    var propertiesResponse = {
        templateUrl: 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/handlerTemplates/mediaSearchHandlerTemplate.html',
        placeHolderI18nKey: 'navigationmanagement.navnode.node.entry.dropdown.media.search',
        isPaged: true
    };

});
