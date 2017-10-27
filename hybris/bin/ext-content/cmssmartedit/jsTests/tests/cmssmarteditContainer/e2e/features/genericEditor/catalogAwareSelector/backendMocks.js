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
    //.constant('URL_FOR_ITEM', /cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/items\/thesmarteditComponentId/)
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(function($httpBackend, filterFilter, parseQuery, I18N_RESOURCE_URI, languageService, $location) {


        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({

        });

        var map = [{
            "value": "\"thepreviewTicketURI\"",
            "key": "previewTicketURI"
        }, {
            "value": "\"/cmswebservices/v1/i18n/languages\"",
            "key": "i18nAPIRoot"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/catalogAwareSelector/outerapp.js\"}",
            "key": "applications.outerapp"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/util/commonMockedModule/LanguagesMock.js\"}",
            "key": "applications.LanguageMocks"
        }, {
            "value": "{\"smartEditLocation\":\"/jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/catalogAwareSelector/backendMocks.js\"}",
            "key": "applications.backendMocks"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/web/webroot/cmssmartedit/js/cmssmarteditContainer.js\"}",
            "key": "applications.cmssmarteditContainer"
        }];

        $httpBackend.whenGET(/configuration/).respond(
            function(method, url, data, headers) {
                return [200, map];
            });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/productcatalogversiondetails/).respond(function() {
            var catalogDetails = {
                "catalogVersionDetails": [{
                    "catalogId": "apparelProductCatalog",
                    "name": {
                        "en": "Apparel Product Catalog",
                        "de": "Produktkatalog Kleidung"
                    },
                    "thumbnailUrl": "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3w5NDgyOXxpbWFnZS9wbmd8aW1hZ2VzL2hmOC9oZDUvODc5NjgwNDkzOTgwNi5wbmd8ZDQ5MDMzZGYyOGE5OWFlZTQyMmQ0YjZkNzYyYzA3ZTY0NDQ0ZWEyOWRkNjYxMGI4MDI0MGJhNzQxYzllMjYwZg&attachment=true",
                    "version": "Staged"
                }, {
                    "catalogId": "apparelProductCatalog",
                    "name": {
                        "en": "Apparel Product Catalog",
                        "de": "Produktkatalog Kleidung"
                    },
                    "thumbnailUrl": "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3w5NDgyOXxpbWFnZS9wbmd8aW1hZ2VzL2hmOC9oZDUvODc5NjgwNDkzOTgwNi5wbmd8ZDQ5MDMzZGYyOGE5OWFlZTQyMmQ0YjZkNzYyYzA3ZTY0NDQ0ZWEyOWRkNjYxMGI4MDI0MGJhNzQxYzllMjYwZg&attachment=true",
                    "version": "Online"
                }],
                "name": {
                    "en": "Apparel Site UK"
                },
                "uid": "apparel-uk"
            };

            return [200, catalogDetails];
        });


        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/products\/(.+)/, undefined, ['code']).respond(function(method, url, data, header, params) {
            var products = {
                '300608207': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300608207",
                    "uid": "300608207",
                    "description": {},
                    "name": {
                        "en": "Assortment Web Belt blue black Uni"
                    }
                },
                '122409_black': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "122409_black",
                    "uid": "122409_black",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/122409_2.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA"
                    }
                },
                '300738117': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738117",
                    "uid": "300738117",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black L"
                    }
                },
                '300738116': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738116",
                    "uid": "300738116",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                },
                '300738118': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738118",
                    "uid": "300738118",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XL"
                    }
                },
                '300738114': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738114",
                    "uid": "300738114",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XS"
                    }
                },
                '118514': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514",
                    "uid": "118514",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                },
                '118514_grey': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514_grey",
                    "uid": "118514_grey",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black grey"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                },
                '111159_black': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "111159_black",
                    "uid": "111159_black",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/111159_3.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg"
                    }
                },
                '300689173': {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300689173",
                    "uid": "300689173",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black LXL"
                    }
                }
            };

            return [200, products[params.code]];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/productcatalogs\/apparelProductCatalog\/versions\/Online\/products\?.*/).respond(function() {
            var productsList = {
                "pagination": {
                    "count": 10,
                    "page": 1,
                    "totalCount": 10,
                    "totalPages": 1
                },
                "products": [{
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300608207",
                    "uid": "300608207",
                    "description": {},
                    "name": {
                        "en": "Assortment Web Belt blue black Uni"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "122409_black",
                    "uid": "122409_black",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/122409_2.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0MjUxfGltYWdlL2pwZWd8aW1hZ2VzL2g2NS9oNTUvODc5NjcxMDE3NDc1MC5qcGd8MDg1Yzc0MWU1NmI5YmYyZDVjZjMyOThkZDAzYzVmNzc4NzI2YmEyNGM5MDQ5NWY0NzM5ZjRlNGNiNDNlY2I4ZA"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738117",
                    "uid": "300738117",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black L"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738116",
                    "uid": "300738116",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black M"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738118",
                    "uid": "300738118",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XL"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300738114",
                    "uid": "300738114",
                    "description": {},
                    "name": {
                        "en": "Asterisk SS youth black XS"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514",
                    "uid": "118514",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "118514_grey",
                    "uid": "118514_grey",
                    "description": {},
                    "name": {
                        "en": "Avionics Shades Black grey"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/118514_1.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3wyNzkyfGltYWdlL2pwZWd8aW1hZ2VzL2g5Zi9oMGEvODc5Njc1NDkwMzA3MC5qcGd8ZWY5MWY3Mzg0NDQyMWM2MDEyMWEyZmJlNjU4ZTMwYzQwYmE0YWIxMzNjNzhjMTA3ZDc2NzJmYTU0MDQwNDZkOQ"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "111159_black",
                    "uid": "111159_black",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black"
                    },
                    "thumbnail": {
                        "catalogId": "apparelProductCatalog",
                        "catalogVersion": "Online",
                        "code": "/96Wx96H/111159_3.jpg",
                        "downloadUrl": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg&attachment=true",
                        "mime": "image/jpeg",
                        "url": "/medias/?context=bWFzdGVyfGltYWdlc3w0NDc2fGltYWdlL2pwZWd8aW1hZ2VzL2g3Yy9oYWQvODc5NjU3NTgyNTk1MC5qcGd8NGVlOTkxNTc0NjcwMDk4ZGNlMTIzYmI0MGE5MTIwYTEwZTRmOTQ1ZmYzMWJiYWY4OThjNDBmZTQ4ZDE0YTNlNg"
                    }
                }, {
                    "catalogId": "apparelProductCatalog",
                    "catalogVersion": "Online",
                    "code": "300689173",
                    "uid": "300689173",
                    "description": {},
                    "name": {
                        "en": "BT Airhole Splatter Facemask black LXL"
                    }
                }]
            };

            return [200, productsList];
        });


        var categoriesList = {
            "pagination": {
                "count": 10,
                "page": 1,
                "totalCount": 10,
                "totalPages": 1
            },
            "productCategories": [{
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Aesthetiker",
                "description": {
                    "en": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi dapibus convallis magna eu placerat. Cras non tortor nulla, quis pharetra dui. Sed nisl tortor, lacinia nec molestie in, pellentesque ut metus. Nunc ut sapien ut augue vestibulum luctus."
                },
                "name": {
                    "en": "Aesthetiker"
                },
                "uid": "eyJpdGVtSWQiOiJBZXN0aGV0aWtlciIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Airhole",
                "description": {},
                "name": {
                    "en": "Airhole"
                },
                "uid": "eyJpdGVtSWQiOiJBaXJob2xlIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Al Merrick",
                "description": {},
                "name": {
                    "en": "Al Merrick"
                },
                "uid": "eyJpdGVtSWQiOiJBbCBNZXJyaWNrIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alien Workshop",
                "description": {},
                "name": {
                    "en": "Alien Workshop"
                },
                "uid": "eyJpdGVtSWQiOiJBbGllbiBXb3Jrc2hvcCIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alpinestars",
                "description": {},
                "name": {
                    "en": "Alpinestars"
                },
                "uid": "eyJpdGVtSWQiOiJBbHBpbmVzdGFycyIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Alptraum",
                "description": {},
                "name": {
                    "en": "Alptraum"
                },
                "uid": "eyJpdGVtSWQiOiJBbHB0cmF1bSIsImNhdGFsb2dJZCI6ImFwcGFyZWxQcm9kdWN0Q2F0YWxvZyIsImNhdGFsb2dWZXJzaW9uIjoiT25saW5lIn0="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Amplid",
                "description": {},
                "name": {
                    "en": "Amplid"
                },
                "uid": "eyJpdGVtSWQiOiJBbXBsaWQiLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Analog",
                "description": {},
                "name": {
                    "en": "Analog"
                },
                "uid": "eyJpdGVtSWQiOiJBbmFsb2ciLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Anon",
                "description": {},
                "name": {
                    "en": "Anon"
                },
                "uid": "eyJpdGVtSWQiOiJBbm9uIiwiY2F0YWxvZ0lkIjoiYXBwYXJlbFByb2R1Y3RDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ=="
            }, {
                "catalogId": "apparelProductCatalog",
                "catalogVersion": "Online",
                "code": "Apo",
                "description": {},
                "name": {
                    "en": "Apo"
                },
                "uid": "eyJpdGVtSWQiOiJBcG8iLCJjYXRhbG9nSWQiOiJhcHBhcmVsUHJvZHVjdENhdGFsb2ciLCJjYXRhbG9nVmVyc2lvbiI6Ik9ubGluZSJ9"
            }]
        };

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/productCatalogs\/apparelProductCatalog\/versions\/Online\/categories\?.*/).respond(function() {
            return [200, categoriesList];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/categories\/(.+)/, undefined, ['categoryUID']).respond(function(method, url, data, header, params) {
            var result = categoriesList.productCategories.filter(function(category) {
                return category.uid === params.categoryUID;
            })[0];

            return [200, result];
        });


        //var component = {
        //	navigationComponent: "8"
        //};
        //
        //$httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        //$httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data, headers) {
        //	component = JSON.parse(data);
        //	return [200, component];
        //});

        //$httpBackend.whenGET(/structureApi/).respond(function(method, url, data, headers) {
        //	var structure = {
        //		attributes: [{
        //			cmsStructureType: "NavigationNodeSelector",
        //			qualifier: "navigationComponent",
        //			i18nKey: 'type.thesmarteditcomponenttype.navigationComponent.name',
        //			localized: false,
        //			required: true
        //		}]
        //	};
        //
        //	return [200, structure];
        //});



        $location.path("/test");

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();
    });
angular.module('smarteditloader').requires.push('backendMocks');
angular.module('smarteditcontainer').requires.push('backendMocks');
