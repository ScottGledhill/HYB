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
angular.module('backendMocks', ['ngMockE2E', 'functionsModule', 'resourceLocationsModule', 'languageServiceModule'])
    .constant('URL_FOR_ITEM', /cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/items\/thesmarteditComponentId/)
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(function($httpBackend, filterFilter, parseQuery, URL_FOR_ITEM, I18N_RESOURCE_URI, languageService, $location) {


        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "genericeditor.dropdown.placeholder": "Select an image",
            "componentform.actions.cancel": "Cancel",
            "componentform.actions.submit": "Submit",
            "componentform.actions.replaceImage": "Replace Image",
            "type.thesmarteditcomponenttype.id.name": "id",
            "type.thesmarteditcomponenttype.headline.name": "Headline",
            "type.thesmarteditcomponenttype.active.name": "Activation",
            "type.thesmarteditcomponenttype.enabled.name": "Enabled",
            "type.thesmarteditcomponenttype.content.name": "Content",
            "type.thesmarteditcomponenttype.created.name": "Creation date",
            "type.thesmarteditcomponenttype.media.name": "Media",
            "type.thesmarteditcomponenttype.orientation.name": "Orientation",
            "type.thesmarteditcomponenttype.external.name": "External Link",
            "type.thesmarteditcomponenttype.urlLink.name": "Url Link",
            "editor.linkto.label": "Link to",
            "editor.linkto.external.label": "External Link",
            "editor.linkto.internal.label": "Existing Page",
            "navigationmanagement.navnode.objecttype.node": "Node",
            "navigationcomponent.management.node.selection.remove.action": "Remove",
            "navigationcomponent.management.node.selection.select.action": "Select",
            "navigationcomponent.management.node.selection.invite.action": "Select a node to associate to this component",
            "navigationcomponent.management.node.breadcrumb": "Node Path",
            "navigationcomponent.management.node.level.root": "Root",
            "navigationcomponent.management.node.level.non.root": "Level {{level}}",
        });

        var map = [{
            "value": "\"thepreviewTicketURI\"",
            "key": "previewTicketURI"
        }, {
            "value": "\"/cmswebservices/v1/i18n/languages\"",
            "key": "i18nAPIRoot"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/navigationNodeSelector/outerapp.js\"}",
            "key": "applications.outerapp"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/LanguagesMock.js\"}",
            "key": "applications.LanguageMocks"
        }, {
            "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/navigationNodeSelector/backendMocks.js\"}",
            "key": "applications.backendMocks"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
            "key": "applications.cmssmarteditContainer"
        }];

        $httpBackend.whenGET(/configuration/).respond(
            function(method, url, data, headers) {
                return [200, map];
            });


        var component = {
            navigationComponent: "8"
        };

        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        $httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data, headers) {
            component = JSON.parse(data);
            return [200, component];
        });

        $httpBackend.whenGET(/structureApi/).respond(function(method, url, data, headers) {
            var structure = {
                attributes: [{
                    cmsStructureType: "NavigationNodeSelector",
                    qualifier: "navigationComponent",
                    i18nKey: 'type.thesmarteditcomponenttype.navigationComponent.name',
                    localized: false,
                    required: true
                }]
            };

            return [200, structure];
        });



        $location.path("/test");

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();
    });
angular.module('smarteditloader').requires.push('backendMocks');
angular.module('smarteditcontainer').requires.push('backendMocks');
