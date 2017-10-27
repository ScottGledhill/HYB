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
angular.module('innerMocksModule', ['ngMockE2E', 'renderServiceModule', 'assetsServiceModule', 'editorModalServiceModule'])
    .constant('testAssets', true)
    .service('pagesContentSlotsResourceMocksService', function($httpBackend) {

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'pageId' set to 'homepage' to return four associations, indicating that the page has four slots.
         */
        this.mockGETForHomepage = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?pageId=homepage/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'topHeaderSlot'
                }, {
                    pageId: 'homepage',
                    position: 'bottomHeader',
                    slotId: 'bottomHeaderSlot'
                }, {
                    pageId: 'homepage',
                    position: 'footer',
                    slotId: 'footerSlot'
                }, {
                    pageId: 'homepage',
                    position: 'other',
                    slotId: 'otherSlot'
                }]
            });
        };

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'slotId' set to 'topHeaderSlot' to return a single association, indicating that the slot is not shared.
         */
        this.mockGETForTopHeaderSlot = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?slotId=topHeaderSlot/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'topHeaderSlot'
                }]
            });
        };

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'slotId' set to 'bottomHeaderSlot' to return a single association, indicating that the slot is not shared.
         */
        this.mockGETForBottomHeaderSlot = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?slotId=bottomHeaderSlot/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'bottomHeaderSlot'
                }]
            });
        };

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'slotId' set to 'otherSlot' to return a single association, indicating that the slot is not shared.
         */
        this.mockGETForOtherSlot = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?slotId=otherSlot/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'otherSlot'
                }]
            });
        };

        /**
         * Mocks the /pagescontentslots resource with the query parameter 'slotId' set to 'footerSlot' to return a single association, indicating that the slot is not shared.
         */
        this.mockGETForFooter = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslots\?slotId=footerSlot/).respond({
                pageContentSlotList: [{
                    pageId: 'homepage',
                    position: 'topHeader',
                    slotId: 'footerSlot'
                }]
            });
        };
    })
    .service('itemsResourceMocksService', function($httpBackend) {
        /**
         * Mocks the /items resource with the query parameter 'uids' set to the set of all components on the dummy storefront to return TODO.
         */
        this.mockGETForAllComponents = function() {
            $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/items\?uids=component1,component2,component3,component4,component5/).respond({
                'componentItems': [{
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 1',
                    'pk': '1',
                    'typeCode': 'SimpleResponsiveBannerComponent',
                    'uid': 'component1',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 2',
                    'pk': '2',
                    'typeCode': 'componentType2',
                    'uid': 'component2',
                    'visible': true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 3',
                    'pk': '3',
                    'typeCode': 'componentType3',
                    'uid': 'component3',
                    "visible": true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 4',
                    'pk': '4',
                    'typeCode': 'componentType4',
                    'uid': 'component4',
                    "visible": true
                }, {
                    'creationtime': '2016-08-17T16:05:47+0000',
                    'modifiedtime': '2016-08-17T16:05:47+0000',
                    'name': 'Component 5',
                    'pk': '5',
                    'typeCode': 'componentType5',
                    'uid': 'component5',
                    "visible": true
                }]
            });
        };
    })
    .service('pagesContentSlotsComponentsResourceMocksService', function($httpBackend) {
        /**
         * Mocks the /pagescontentslotscomponents resource with the query parameter 'pageId' set to homepage to return 5 associations, indicating 5 component/content slot combinations on the page
         */
        this.mockGETForHomepage = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents\?pageId=homepage/).respond({
                'pageContentSlotComponentList': [{
                    'componentId': 'component1',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component2',
                    'pageId': 'homepage',
                    'position': 1,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component3',
                    'pageId': 'homepage',
                    'position': 2,
                    'slotId': 'topHeaderSlot'
                }, {
                    'componentId': 'component4',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'bottomHeaderSlot'
                }, {
                    'componentId': 'component5',
                    'pageId': 'homepage',
                    'position': 0,
                    'slotId': 'footerSlot'
                }]
            });
        };
    })
    .service('typeRestrictionsResourceMocksService', function($httpBackend) {
        this.mockGETForTopHeaderSlot = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/topHeaderSlot\/typerestrictions/).respond({
                contentSlotName: 'topHeaderSlot',
                validComponentTypes: [
                    'componentType1',
                    'componentType2',
                    'componentType3',
                    'CMSParagraphComponent'
                ]
            });
        };

        this.mockGETBottomHeaderSlot = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/bottomHeaderSlot\/typerestrictions/).respond({
                contentSlotName: 'bottomHeaderSlot',
                validComponentTypes: [
                    'componentType4',
                    'CMSParagraphComponent',
                    'SimpleBannerComponent'
                ]
            });
        };

        this.mockGETForFooterSlot = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/footerSlot\/typerestrictions/).respond({
                contentSlotName: 'footerSlot',
                validComponentTypes: [
                    'componentType0',
                    'componentType2',
                    'componentType3',
                    'componentType4',
                    'componentType5',
                    'SimpleResponsiveBannerComponent',
                    'CMSParagraphComponent'
                ]
            });
        };

        this.mockGETForOtherSlot = function() {
            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/otherSlot\/typerestrictions/).respond({
                contentSlotName: 'otherSlot',
                validComponentTypes: [
                    'componentType0',
                    'componentType2',
                    'componentType3',
                    'componentType4',
                    'componentType5',
                    'SimpleResponsiveBannerComponent'
                ]
            });

            /*
             /cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/pagescontentslotscomponents/pages/homepage/contentslots/topHeaderSlot/components/component1
             */
        };
    })
    .service('mockRenderServiceBackend', function($httpBackend, $window, lodash) {

        this.idCounter = 10;
        this.slots = [{
            id: 'topHeaderSlot',
            components: ['component1', 'component2', 'component3']
        }, {
            id: 'otherSlot',
            components: []
        }, {
            id: 'bottomHeaderSlot',
            components: ['component4']
        }, {
            id: 'footerSlot',
            components: ['component5']
        }];

        this.types = {
            'component1': 'componentType1',
            'component2': 'componentType2',
            'component3': 'componentType3',
            'component4': 'componentType4',
            'component5': 'componentType5'
        };

        $httpBackend.whenPUT(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents\/pages\/homepage\/contentslots\/.*/).respond(function(method, url, data, headers) {
            var parsedData = JSON.parse(data);
            this.moveComponentToSlot(parsedData.currentSlotId, parsedData.componentId, parsedData.slotId, parsedData.position);

            return [200, data];
        }.bind(this));

        //cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/pagescontentslotscomponents
        $httpBackend.whenPOST(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagescontentslotscomponents/).respond(function(method, url, data, headers) {
            var parsedData = JSON.parse(data);
            var componentId = 'component' + this.idCounter++;
            this.addComponentToSlot(parsedData.slotId, componentId, parsedData.position);

            return [200, data];
        }.bind(this));


        $httpBackend.whenGET(/.*dummystorefrontAlternatelayout.html.*/).respond(function() {
            var slotsStr = '';
            lodash.each(this.slots, function(slot) {
                slotsStr += this._renderSlot(slot);
            }.bind(this));
            var document = '<!DOCTYPE html>' +
                '<html>' +
                '<head></head>' +
                '<body>' +
                slotsStr +
                '</body>' +
                '</html>';

            return [200, document];
        }.bind(this));

        var component1_data = {
            'creationtime': '2016-08-17T16:05:47+0000',
            'modifiedtime': '2016-08-17T16:05:47+0000',
            'name': 'Component 1',
            'pk': '1',
            'typeCode': 'CMSParagraphComponent',
            'uid': 'component1',
            'visible': true
        };

        $httpBackend.whenGET('/cmswebservices/v1/sites/apparel-uk/catalogs/apparel-ukContentCatalog/versions/Staged/items/component1').respond(component1_data);

        $window.smartedit.reprocessPage = function() {};

        this._renderSlot = function(slot) {
            var components = '';
            lodash.each(slot.components, function(componentId) {
                var type = this.getComponentType(componentId);
                var component = '<div class="smartEditComponent" id="' + componentId + '" data-smartedit-component-type="' + type + '" data-smartedit-component-id="' + componentId + '">' +
                    '<div class="box">' +
                    '<p>' + componentId + '</p>' +
                    '</div>' +
                    '</div>';
                components += component;
            }.bind(this));

            var slotStr = '<div class="smartEditComponent" id="' + slot.id + '" data-smartedit-component-type="ContentSlot" data-smartedit-component-id="' + slot.id + '">' +
                components + '</div>';

            return slotStr;
        };

        this.getComponentType = function(componentId) {
            if (this.types[componentId]) {
                return this.types[componentId];
            } else {
                return 'componentType0';
            }
        };

        this.removeComponent = function(slotId, componentId) {
            var slot = this.getSlot(slotId);
            var componentIndex = slot.components.indexOf(componentId);
            if (componentIndex != -1) {
                slot.components.splice(componentIndex, 1);
            }
        };

        this.addComponentToSlot = function(slotId, componentId, position) {
            var slot = this.getSlot(slotId);
            slot.components.splice(position, 0, componentId);
        };

        this.moveComponentToSlot = function(originalSlot, componentId, targetSlot, position) {
            this.removeComponent(originalSlot, componentId);
            this.addComponentToSlot(targetSlot, componentId, position);
        };

        this.getSlot = function(slotId) {
            var resultSlot = null;
            lodash.each(this.slots, function(slot) {
                if (slot.id === slotId) {
                    resultSlot = slot;
                    return false;
                }
            }.bind(this));

            return resultSlot;
        };
    })
    .run(function($q, editorModalService, pagesContentSlotsResourceMocksService, itemsResourceMocksService, pagesContentSlotsComponentsResourceMocksService, typeRestrictionsResourceMocksService, mockRenderServiceBackend) {
        pagesContentSlotsResourceMocksService.mockGETForHomepage();
        pagesContentSlotsResourceMocksService.mockGETForTopHeaderSlot();
        pagesContentSlotsResourceMocksService.mockGETForBottomHeaderSlot();
        pagesContentSlotsResourceMocksService.mockGETForOtherSlot();
        pagesContentSlotsResourceMocksService.mockGETForFooter();
        itemsResourceMocksService.mockGETForAllComponents();
        pagesContentSlotsComponentsResourceMocksService.mockGETForHomepage();
        typeRestrictionsResourceMocksService.mockGETForTopHeaderSlot();
        typeRestrictionsResourceMocksService.mockGETBottomHeaderSlot();
        typeRestrictionsResourceMocksService.mockGETForFooterSlot();
        typeRestrictionsResourceMocksService.mockGETForOtherSlot();
    });
