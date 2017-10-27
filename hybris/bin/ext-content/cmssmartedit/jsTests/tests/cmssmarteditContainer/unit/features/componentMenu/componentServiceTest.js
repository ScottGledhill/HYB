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
describe('ComponentService', function() {

    var ComponentService, $rootScope, $q;
    var restServiceFactory, restServiceForTypes, restServiceForItems, restServiceForAddExistingComponent;

    beforeEach(function() {
        angular.module('gatewayProxyModule', []);
        angular.module('gatewayFactoryModule', []);
        angular.module('restServiceFactoryModule', []);
        angular.module('renderServiceModule', []);
        angular.module('experienceInterceptorModule', []);
    });

    beforeEach(module('resourceLocationsModule', function($provide) {
        $provide.constant("CONTEXT_CATALOG", "CURRENT_CONTEXT_CATALOG");
        $provide.constant("CONTEXT_CATALOG_VERSION", "CURRENT_CONTEXT_CATALOG_VERSION");
        $provide.constant("CONTEXT_SITE_ID", "CURRENT_CONTEXT_SITE_ID");
        $provide.constant("TYPES_RESOURCE_URI", "/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/types");
        $provide.constant("ITEMS_RESOURCE_URI", "/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/items");
        $provide.constant("PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI", "/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/pagescontentslotscomponents");

        restServiceForTypes = jasmine.createSpyObj("restServiceForTypes", ["get"]);
        restServiceForItems = jasmine.createSpyObj("restServiceForItems", ["get", "getById", "save", "update"]);
        restServiceForAddExistingComponent = jasmine.createSpyObj("restServiceForAddExistingComponent", ["save"]);

        restServiceFactory = jasmine.createSpyObj("restServiceFactory", ["get"]);
        $provide.value("restServiceFactory", restServiceFactory);

        restServiceFactory.get.andCallFake(function(uri) {
            if (uri == "/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/types") {
                return restServiceForTypes;
            } else if (uri == "/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/items") {
                return restServiceForItems;
            } else if (uri == "/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/pagescontentslotscomponents") {
                return restServiceForAddExistingComponent;
            }
        });
    }));

    beforeEach(module('gatewayProxyModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService', 'asdfasdf']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(module('gatewayFactoryModule', function($provide) {
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('renderServiceModule', function($provide) {

        renderService = jasmine.createSpyObj('renderService', ['removeComponent', 'renderRemoval']);
        $provide.value('renderService', renderService);
    }));

    beforeEach(customMatchers);

    beforeEach(module('componentServiceModule'));

    beforeEach(inject(function(_ComponentService_, _$rootScope_, _$q_) {
        ComponentService = _ComponentService_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));


    describe("Component Menu - Get All Types Tests", function() {

        it('ComponentService returns a listOfComponentTypes', function() {
            var response = {
                a: 'b'
            };

            restServiceForTypes.get.andReturn($q.when(response));

            //checks data inside of the promise itself
            expect(ComponentService.loadComponentTypes()).toBeResolvedWithData(response);

            expect(restServiceForTypes.get).toHaveBeenCalledWith({
                category: 'COMPONENT'
            });
        });

        it('ComponentService is unable to load ComponentTypes', function() {

            var deferred = $q.defer();
            deferred.reject();

            restServiceForTypes.get.andReturn(deferred.promise);
            var promiseResult = ComponentService.loadComponentTypes();

            //checks data inside of the promise itself
            promiseResult.then(function(result) {
                //expect to fail
            }, function() {
                //console.log("rejected");
            });

            $rootScope.$digest(); //resolve promises

            expect(restServiceForTypes.get).toHaveBeenCalled();

        });
    });


    describe("Component Menu - Get All Items Tests", function() {

        it('ComponentService returns a page of items', function() {
            var page = {
                results: []
            };
            var mask = 'something';
            var pageSize = 10;
            var pageNumber = 1;
            restServiceForItems.get.andReturn($q.when(page));
            expect(ComponentService.loadPagedComponentItems(mask, pageSize, pageNumber)).toBeResolvedWithData(page);
            expect(restServiceForItems.get).toHaveBeenCalledWith({
                pageSize: 10,
                currentPage: 1,
                mask: 'something',
                sort: 'name'
            });
        });

        it('ComponentService loads an item by id', function() {
            var item = {};
            var id = "someid";
            restServiceForItems.getById.andReturn($q.when(item));
            expect(ComponentService.loadComponentItem(id)).toBeResolvedWithData(item);
            expect(restServiceForItems.getById).toHaveBeenCalledWith(id);
        });

    });


    describe('Add New Component Type Tests ', function() {

        it('Should save a ComponentType', function() {

            var componentInfo = {
                targetSlotId: "adfasdfasd",
                pageId: "sdfasdfasdf",
                position: "sdfasdrwereryrty",
                componentType: "fghuytuer",
                type: "QWEDFE"
            };

            var componentPayload = {
                a: 'a',
                b: 'b'
            };
            restServiceForItems.save.andReturn($q.when(response));
            expect(ComponentService.createNewComponent(componentInfo, componentPayload)).toBeResolvedWithData(response);

            expect(restServiceForItems.save).toHaveBeenCalledWith({
                slotId: "adfasdfasd",
                pageId: "sdfasdfasdf",
                position: "sdfasdrwereryrty",
                typeCode: "fghuytuer",
                type: "QWEDFE",
                a: 'a',
                b: 'b'
            });
        });
    });


    describe('Add New Component Items Tests ', function() {

        it('should add existing component item to slot', function() {
            var pageId = "pageId",
                componentId = "componentId",
                slotId = "slotId",
                position = "position";

            var response = {
                a: 'b'
            };

            restServiceForAddExistingComponent.save.andReturn($q.when(response));
            expect(ComponentService.addExistingComponent(pageId, componentId, slotId, position)).toBeResolvedWithData(response);

            $rootScope.$digest(); //resolve promises

            expect(restServiceForAddExistingComponent.save).toHaveBeenCalledWith({
                pageId: 'pageId',
                slotId: 'slotId',
                componentId: 'componentId',
                position: 'position'
            });
        });
    });

});
