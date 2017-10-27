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
                    'confirmation.modal.ok': 'Ok',
                    'confirmation.modal.cancel': 'Cancel',
                    'se.ytree.template.header.name': 'Name',
                    'se.ytree.template.header.type': 'Type',
                    'navigationmanagement.title': 'NAVIGATION MANAGEMENT',
                    'navigationmanagement.navnode.addchild': 'Add a Child',
                    'navigationmanagement.navnode.addsibling': 'Add a Sibling',
                    'navigationmanagement.navnode.edit': 'Edit',
                    'navigationmanagement.navnode.removenode': 'Delete',
                    'navigationmanagement.navnode.move.up': 'Move Up',
                    'navigationmanagement.navnode.move.down': 'Move Down',
                    'navigationmanagement.add.top.level.node': 'Add New Top Level',
                    'navigationmanagement.navnode.objecttype.node': 'Node',
                    'navigationmanagement.navnode.removenode.confimation.title': 'Delete Node',
                    'navigationmanagement.navnode.removeentry.confimation.title': 'Delete Entry',
                    'navigationmanagement.navnode.removenode.confimation.message': 'Deleting a Node will delete all nodes and entries associated to it. Do you want to continue ?',
                    'navigationmanagement.navnode.removeentry.confimation.message': 'Do you want to delete the entry ?'
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
        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
