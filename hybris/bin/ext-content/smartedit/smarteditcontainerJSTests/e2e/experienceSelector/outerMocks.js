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
    .module('OuterMocks', ['ngMockE2E', 'resourceLocationsModule', 'languageServiceModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/smarteditcontainerJSTests/)
    .factory('previewTicketDataService', function() {

        var currentPreviewTicket = 'defaultTicket';

        var _getCurrentPreviewTicket = function() {
            return currentPreviewTicket;
        };

        var _setCurrentPreviewTicket = function(previewTicket) {
            currentPreviewTicket = previewTicket;
        };

        return {
            getCurrentPreviewTicket: _getCurrentPreviewTicket,
            setCurrentPreviewTicket: _setCurrentPreviewTicket
        };

    })
    .run(
        function($httpBackend, languageService, previewTicketDataService, I18N_RESOURCE_URI) {

            var map = [{
                "id": "2",
                "value": "\"previewwebservices/v1/preview\"",
                "key": "previewTicketURI"
            }, {
                "id": "9",
                "value": "{\"smartEditLocation\":\"/smarteditcontainerJSTests/e2e/experienceSelector/innerMocks.js\"}",
                "key": "applications.InnerMocks"
            }, {
                "id": "10",
                "value": "{\"smartEditContainerLocation\":\"/smarteditcontainerJSTests/e2e/experienceSelector/experienceSelectorApp.js\"}",
                "key": "applications.experienceSelectorApp"
            }];

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);

            $httpBackend
                .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
                .respond({
                    'experience.selector.catalog': 'CATALOG',
                    'experience.selector.date.and.time': 'DATE/TIME',
                    'experience.selector.language': 'LANGUAGE',
                    'experience.selector.newfield': 'NEW FIELD',
                    'componentform.actions.cancel': 'CANCEL',
                    'componentform.actions.apply': 'APPLY',
                    'componentform.select.date': 'Select a Date and Time',
                    'genericeditor.sedropdown.placeholder': 'Select an Option'
                });

            $httpBackend.whenGET(/fragments/).passThrough();


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/dummystorefront\.html/).respond("<somehtml/>");

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'French',
                    isocode: 'fr'
                }]
            });

            $httpBackend.whenGET(/\/dummystorefrontOtherPage.html/).respond(function() {
                // Test if we already loaded the homepage of the initial experience with a valid ticket
                if (previewTicketDataService.getCurrentPreviewTicket() === 'validTicketId') {
                    previewTicketDataService.setCurrentPreviewTicket('');
                    return [404, null, {
                        'Content-type': 'text/html'
                    }];
                } else {
                    return [200];
                }
            });

            $httpBackend.whenPOST(/previewwebservices\/v1\/preview/).respond(function(method, url, data) {
                var postedData = angular.fromJson(data);
                for (var ref in postedData) {
                    postedData[ref] = '' + postedData[ref];
                }

                if (postedData.catalog === 'electronicsContentCatalog' &&
                    postedData.catalogVersion === 'Online' &&
                    postedData.language === 'it') {
                    return [400, {
                        errors: [{
                            message: 'CatalogVersion with catalogId \'electronicsContentCatalog\' and version \'Online\' not found!',
                            "type": "UnknownIdentifierError"
                        }]
                    }];
                }

                if (postedData.catalog === 'electronicsContentCatalog' &&
                    postedData.catalogVersion === 'Online' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.language === 'pl') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'pl',
                        ticketId: 'validTicketId1'
                    }];
                }


                if (postedData.catalog === 'apparel-ukContentCatalog' &&
                    postedData.catalogVersion === 'Staged' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.language === 'en') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'en',
                        ticketId: 'apparel-ukContentCatalogStagedValidTicket'
                    }];
                }

                if (postedData.catalog === 'apparel-ukContentCatalog' &&
                    postedData.catalogVersion === 'Online' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.time && postedData.time.indexOf('2016-01-01T13:00') >= 0 &&
                    postedData.language === 'fr') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'fr',
                        time: '1/1/16 1:00 PM',
                        ticketId: 'apparel-ukContentCatalogOnlineValidTicket'
                    }];
                }

                if (postedData.catalog === 'apparel-ukContentCatalog' &&
                    postedData.catalogVersion === 'Online' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.language === 'fr') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'fr',
                        ticketId: 'apparel-ukContentCatalogOnlineValidTicket'
                    }];
                }

                if (postedData.catalog === 'electronicsContentCatalog' &&
                    postedData.catalogVersion === 'Online' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.time && postedData.time.indexOf('2016-01-01T13:00') >= 0 &&
                    postedData.language === 'pl') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'pl',
                        time: '1/1/16 1:00 PM',
                        ticketId: 'validTicketId2'
                    }];
                }


                if (postedData.catalog === 'electronicsContentCatalog' &&
                    postedData.catalogVersion === 'Staged' &&
                    postedData.newField === 'New Data For Preview' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.time && postedData.time.indexOf('2016-01-01T00:00:00') >= 0 &&
                    postedData.language === 'it') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'it',
                        newField: 'New Data For Preview',
                        time: '1/1/16 12:00 AM',
                        ticketId: 'validTicketId2'
                    }];
                }

                if (postedData.catalog === 'electronicsContentCatalog' &&
                    postedData.catalogVersion === 'Staged' &&
                    postedData.resourcePath === document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html' &&
                    postedData.time && postedData.time.indexOf('2016-01-01T00:00:00') >= 0 &&
                    postedData.language === 'it') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                        language: 'it',
                        time: '1/1/16 12:00 AM',
                        ticketId: 'validTicketId2'
                    }];
                }

                if (previewTicketDataService.getCurrentPreviewTicket() !== '') {
                    previewTicketDataService.setCurrentPreviewTicket('validTicketId');
                }

                return [200, {
                    resourcePath: document.location.origin + '/smarteditcontainerJSTests/e2e/dummystorefront.html',
                    ticketId: 'validTicketId'
                }];
            });

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
