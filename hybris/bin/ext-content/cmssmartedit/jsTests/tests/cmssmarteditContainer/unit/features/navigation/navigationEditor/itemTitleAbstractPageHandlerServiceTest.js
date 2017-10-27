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
describe('ItemTitleAbstractPageHandlerService - ', function() {

    var itemTitleAbstractPageHandlerService;
    var restServiceFactory, restServicePagesResource, $q;
    var uriParams = {
        CURRENT_CONTEXT_SITE_ID: 'siteId',
        CURRENT_CONTEXT_CATALOG: 'catalogId',
        CURRENT_CONTEXT_CATALOG_VERSION: 'catalogversion'
    };
    var itemId = '';

    beforeEach(customMatchers);

    beforeEach(module('restServiceFactoryModule', function($provide) {
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', restServiceFactory);

        restServicePagesResource = jasmine.createSpyObj('restServicePagesResource', ['getById']);
        restServiceFactory.get.andReturn(restServicePagesResource);
    }));

    beforeEach(module('itemTitleAbstractPageHandlerServiceModule'));

    beforeEach(inject(function(_itemTitleAbstractPageHandlerService_, _$q_) {
        itemTitleAbstractPageHandlerService = _itemTitleAbstractPageHandlerService_;
        $q = _$q_;
    }));

    it('WHEN getItemTitleById is called THEN a promise should be returned and resolved with the page name and itemType', function() {

        //GIVEN
        restServicePagesResource.getById.andReturn($q.when(pageResponse));

        //THEN
        expect(itemTitleAbstractPageHandlerService.getItemTitleById(itemId, uriParams)).toBeResolvedWithData({
            itemType: 'MyCustomType',
            title: resolvedPromiseData
        });
        expect(restServiceFactory.get).toHaveBeenCalledWith("/cmswebservices/v1/sites/siteId/catalogs/catalogId/versions/catalogversion/pages");
        expect(restServicePagesResource.getById).toHaveBeenCalledWith(itemId);
    });

    var resolvedPromiseData = "Advertise";

    var pageResponse = {
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "PageTemplate",
        name: resolvedPromiseData,
        typeCode: "MyCustomType",
        uid: "uid3"
    };

});
