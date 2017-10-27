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
angular
    .module('OuterMocks', ['ngMockE2E', 'languageServiceModule', 'resourceLocationsModule'])
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(
        function($httpBackend, languageService, I18N_RESOURCE_URI, parseQuery) {
            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var map = [{
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/NavEditor/innerMocks.js\"}",
                "key": "applications.InnerMocks"
            }, {
                "value": "{\"smartEditContainerLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/LanguagesMock.js\"}",
                "key": "applications.LanguageMocks"
            }, {
                "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
                "key": "applications.cmssmarteditContainer"
            }];

            $httpBackend
                .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
                .respond({
                    'landingpage.title': 'Your Touchpoints',
                    'cataloginfo.pagelist': 'PAGE LIST',
                    'cataloginfo.lastsynced': 'LAST SYNCED',
                    'cataloginfo.button.sync': 'SYNC',
                    'cataloginfo.navigationmanagement': 'NAVIGATION MANAGEMENT',
                    'navigationmanagement.title': 'NAVIGATION MANAGEMENT',
                    'navigationmanagement.navnode.addchild': 'Add a Child',
                    'navigationmanagement.navnode.addsibling': 'Add a Sibling',
                    'navigationmanagement.navnode.edit': 'Edit',
                    'navigationmanagement.navnode.removenode': 'Delete',
                    'navigationmanagement.navnode.move.up': 'Move Up',
                    'navigationmanagement.navnode.move.down': 'Move Down',
                    'navigationmanagement.add.top.level.node': 'Add New Top Level',
                    'navigationmanagement.navnode.objecttype.node': 'Node',
                    'navigationmanagement.navnode.node.entry.add.new.message': 'Add Entry',
                    'navigationmanagement.node.edit.cancel': 'Cancel',
                    'navigationmanagement.node.edit.save': 'Save',
                    'navigationmanagement.node.edit.title': 'Edit Node',
                    'navigationmanagement.navnode.node.entry.button.add': 'Add',
                    'navigationmanagement.navnode.node.entry.button.cancel': 'Cancel',
                    'navigationmanagement.navnode.node.entry.button.update': 'Update',
                    'navigationcomponent.management.node.level.root': 'Root',
                    'navigationmanagement.navnode.node.name': 'Name',
                    'navigationmanagement.navnode.node.title': 'Title',
                    'navigationmanagement.navnode.node.entries': 'Entries',
                    'navigationmanagement.navnode.node.create.entry': 'Create Entry',
                    'navigationmanagement.node.addentry.no.itemid.message': 'Please select an Item'
                });

            $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
                'sync.confirm.msg': 'this {{catalogName}}is a test'
            });

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenGET(/fragments/).passThrough();

            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/synchronizations\/versions\/Online/).
            respond({
                creationDate: '2016-01-29T16:25:28',
                syncStatus: 'RUNNING',
                endDate: '2016-01-29T16:25:28',
                lastModifiedDate: '2016-01-29T16:25:28',
                syncResult: 'UNKNOWN',
                startDate: '2016-01-29T16:25:28'

            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages/).respond({
                pages: [{
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
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "MyCustomPageTemplate",
                    name: "page2TitleSuffix",
                    typeCode: "HomePage",
                    uid: "uid4"
                }, {
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "ZTemplate",
                    name: "page3TitleSuffix",
                    typeCode: "ProductPage",
                    uid: "uid5"
                }]

            });

            var medias = [{
                id: '1',
                code: 'contextualmenu_delete_off',
                description: 'contextualmenu_delete_off',
                altText: 'contextualmenu_delete_off alttext',
                realFileName: 'contextualmenu_delete_off.png',
                url: '/web/webroot/images/contextualmenu_delete_off.png'
            }, {
                id: '2',
                code: 'contextualmenu_delete_on',
                description: 'contextualmenu_delete_on',
                altText: 'contextualmenu_delete_on alttext',
                realFileName: 'contextualmenu_delete_on.png',
                url: '/web/webroot/images/contextualmenu_delete_on.png'
            }, {
                id: '3',
                code: 'contextualmenu_edit_off',
                description: 'contextualmenu_edit_off',
                altText: 'contextualmenu_edit_off alttext',
                realFileName: 'contextualmenu_edit_off.png',
                url: '/web/webroot/images/contextualmenu_edit_off.png'
            }, {
                id: '3',
                code: 'contextualmenu_edit_on',
                description: 'contextualmenu_edit_on',
                altText: 'contextualmenu_edit_on alttext',
                realFileName: 'contextualmenu_edit_on.png',
                url: '/web/webroot/images/contextualmenu_edit_on.png'
            }];

            $httpBackend.whenGET(/cmswebservices\/v1\/media/).respond(function(method, url, data, headers) {

                var params = parseQuery(url).params;
                var search = params.split(",")[0].split(":").pop();
                var filtered = filterFilter(medias, search);
                return [200, {
                    media: filtered
                }];
            });

            var structure = {
                "componentItems": [{
                    "name": "Home Page Nav Link",
                    "typeCode": "CMSLinkComponent",
                    "uid": "HomepageNavLink",
                    "visible": true
                }, {
                    "name": "Al Merrick Link",
                    "typeCode": "CMSLinkComponent",
                    "uid": "AlMerrickLink",
                    "visible": true
                }, {
                    "name": "Nike Link",
                    "typeCode": "CMSLinkComponent",
                    "uid": "NikeLink",
                    "visible": true
                }, {
                    "name": "69 Slam Link",
                    "typeCode": "CMSLinkComponent",
                    "uid": "69SlamLink",
                    "visible": true
                }, {
                    "name": "Dakine Link",
                    "typeCode": "CMSLinkComponent",
                    "uid": "DakineLink",
                }, {
                    "name": "Custom Component 1",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent1",
                }, {
                    "name": "Custom Component 2",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent2",
                }, {
                    "name": "Custom Component 3",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent3",
                }, {
                    "name": "Custom Component 4",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent4",
                }, {
                    "name": "Custom Component 5",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent5",
                }, {
                    "name": "Custom Component 6",
                    "typeCode": "CustomComponent",
                    "uid": "CustomComponent6",
                }]
            };

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/items$/).respond(function(method, url, data, headers) {

                var uid = /cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/items\/(.+)/.exec(url);
                if (uid) {
                    var item = structure.componentItems.filter(function(item) {
                        return item.uid == uid[1];
                    })[0];

                    return [200, item];
                }

                return [200, structure];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/items\?currentPage=.*&mask=.*&pageSize=.*/).respond(function(method, url, data, headers) {

                var currentPage = url.split('?')[1].split('&')[0].split('=')[1];
                var mask = url.split('?')[1].split('&')[1].split('=')[1];
                var pageSize = url.split('?')[1].split('&')[2].split('=')[1];

                var filtered = structure.componentItems.filter(function(item) {
                    return mask ? item.name.toUpperCase().indexOf(mask.toUpperCase()) > -1 : true;
                });

                var results = filtered.slice(currentPage * 10, currentPage * 10 + 10);

                var pagedResults = {
                    pagination: {
                        totalCount: filtered.length
                    },
                    componentItems: results
                };

                return [200, pagedResults];
            });

            $httpBackend.whenGET(/cmswebservices\/.*\/navigationentrytypes/).respond(function(method, url, data, headers) {
                var entryTypes = {
                    "navigationEntryTypes": [{
                        "itemType": "AbstractCMSComponent"
                    }, {
                        "itemType": "AbstractPage"
                    }, {
                        "itemType": "Media"
                    }]
                };
                return [200, entryTypes];
            });

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
