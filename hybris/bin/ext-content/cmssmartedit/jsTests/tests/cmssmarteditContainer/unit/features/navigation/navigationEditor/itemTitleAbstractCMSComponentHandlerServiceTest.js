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
describe('ItemTitleAbstractCMSComponentHandlerService - ', function() {

    var itemTitleAbstractCMSComponentHandlerService;
    var restServiceFactory, restServiceItemsResource, $q;
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

        restServiceItemsResource = jasmine.createSpyObj('restServiceItemsResource', ['getById']);
        restServiceFactory.get.andReturn(restServiceItemsResource);
    }));

    beforeEach(module('itemTitleAbstractCMSComponentHandlerServiceModule'));

    beforeEach(inject(function(_itemTitleAbstractCMSComponentHandlerService_, _$q_) {
        itemTitleAbstractCMSComponentHandlerService = _itemTitleAbstractCMSComponentHandlerService_;
        $q = _$q_;
    }));

    it('WHEN getItemTitleById is called THEN a promise should be returned and resolved with itemType and a localized title for CMS Link Component', function() {

        //GIVEN
        restServiceItemsResource.getById.andReturn($q.when(cmsLinkComponent));

        //THEN
        expect(itemTitleAbstractCMSComponentHandlerService.getItemTitleById(itemId, uriParams)).toBeResolvedWithData({
            itemType: 'CMSLinkComponent',
            title: resolvedPromiseDataForCMSLinkComponent
        });
        expect(restServiceFactory.get).toHaveBeenCalledWith('/cmswebservices/v1/sites/siteId/catalogs/catalogId/versions/catalogversion/items');
        expect(restServiceItemsResource.getById).toHaveBeenCalledWith(itemId);
    });

    it('WHEN getItemTitleById is called THEN a promise should be returned and resolved with the itemType and name for CMS Paragraph Component', function() {

        //GIVEN
        restServiceItemsResource.getById.andReturn($q.when(cmsParagraphComponent));

        //THEN
        expect(itemTitleAbstractCMSComponentHandlerService.getItemTitleById(itemId, uriParams)).toBeResolvedWithData({
            itemType: 'CMSParagraphComponent',
            title: resolvedPromiseForParagraphName
        });
        expect(restServiceFactory.get).toHaveBeenCalledWith('/cmswebservices/v1/sites/siteId/catalogs/catalogId/versions/catalogversion/items');
        expect(restServiceItemsResource.getById).toHaveBeenCalledWith(itemId);
    });


    var resolvedPromiseDataForCMSLinkComponent = {
        "en": "HomepageNavLink name in english",
        "fr": "HomepageNavLink name in french"
    };

    var cmsLinkComponent = {
        "creationtime": "2016-07-25T12:57:48+0000",
        "modifiedtime": "2016-07-28T19:41:46+0000",
        "name": "Home Page Nav Link",
        "pk": "8796130608188",
        "typeCode": "CMSLinkComponent",
        "uid": "HomepageNavLink",
        "visible": true,
        "linkName": resolvedPromiseDataForCMSLinkComponent
    };

    var resolvedPromiseForParagraphName = "Home Page Paragraph";
    var cmsParagraphComponent = {
        "creationtime": "2016-07-25T12:57:48+0000",
        "modifiedtime": "2016-07-28T19:41:46+0000",
        "name": resolvedPromiseForParagraphName,
        "pk": "123123123123",
        "typeCode": "CMSParagraphComponent",
        "uid": "someParagraph",
        "visible": true,
    };

});
