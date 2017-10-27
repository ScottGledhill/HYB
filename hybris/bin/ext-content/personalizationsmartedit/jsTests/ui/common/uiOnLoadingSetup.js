(function() {

    angular.module('uiOnLoadingSetup', ['ngMockE2E', 'sharedDataServiceModule', 'resourceLocationsModule'])
        .run(function($httpBackend, STOREFRONT_PATH, $location, I18N_RESOURCE_URI) {

            $httpBackend.whenGET(/web\/webroot\/cmssmartedit/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            $httpBackend.whenGET(/\.js/).passThrough();

            $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
                sites: [{
                    previewUrl: '/jsTests/ui/common/dummystorefront.html',
                    name: {
                        en: "Electronics"
                    },
                    redirectUrl: 'redirecturlElectronics',
                    uid: 'electronics'
                }, {
                    previewUrl: '/jsTests/ui/common/dummystorefront.html',
                    name: {
                        en: "Apparels"
                    },
                    redirectUrl: 'redirecturlApparels',
                    uid: 'apparel-uk'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogversiondetails/).respond({
                name: {
                    en: "Apparels"
                },
                uid: 'apparel-uk',
                catalogVersionDetails: [{
                    name: {
                        en: "Apparel UK Content Catalog"
                    },
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Online',
                    redirectUrl: null
                }, {
                    name: {
                        en: "Apparel UK Content Catalog"
                    },
                    catalogId: 'apparel-ukContentCatalog',
                    version: 'Staged',
                    redirectUrl: null
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/languages/).respond({
                languages: [{
                    language: 'en',
                    required: true
                }, {
                    language: 'pl',
                    required: true
                }, {
                    language: 'it'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types$/).respond(function(method, url, data, headers) {
                return [200, {
                    "componentTypes": [{
                        "code": "CMSParagraphComponent",
                        "i18nKey": "type.cmsparagraphcomponent.name",
                        "name": "Paragraph",
                        "attributes": [{
                            "cmsStructureType": "RichText",
                            "i18nKey": "type.cmsparagraphcomponent.content.name",
                            "qualifier": "content",
                            "localized": true
                        }]
                    }, {
                        "code": "CMSProductListComponent",
                        "i18nKey": "type.cmsproductlistcomponent.name",
                        "name": "Product List Component",
                        "attributes": []
                    }]
                }];
            });

            var pathWithExperience = STOREFRONT_PATH
                .replace(":siteId", "apparel-uk")
                .replace(":catalogId", "apparel-ukContentCatalog")
                .replace(":catalogVersion", "Staged");
            $location.path(pathWithExperience);
        });

    angular.module('smarteditcontainer').constant('perspectiveTest', true);
    angular.module('smarteditcontainer').requires.push('uiOnLoadingSetup');

})();