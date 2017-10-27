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
        function($httpBackend, languageService, I18N_RESOURCE_URI) {
            $httpBackend.whenGET(/jsTests/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var map = [{
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/i18nMock.js\"}",
                "key": "applications.i18nMockModule"
            }, {
                "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
                "key": "applications.cmssmarteditContainer"
            }];

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);


            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond(function(method, url, data, headers) {
                    var dataObject = angular.fromJson(data);
                    if (dataObject.pageId) {
                        return [200, {
                            ticketId: 'previewTicketForPageId',
                            resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefrontAlternatelayout.html'
                        }];
                    } else {
                        return [200, {
                            ticketId: 'dasdfasdfasdfa',
                            resourcePath: '/jsTests/tests/cmssmarteditContainer/e2e/features/dummystorefront.html'
                        }];
                    }
                });

            $httpBackend.whenGET(/fragments/).passThrough();

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
