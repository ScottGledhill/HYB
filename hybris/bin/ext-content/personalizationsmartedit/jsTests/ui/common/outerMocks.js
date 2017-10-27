angular
    .module('uiBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'languageServiceModule'])
    .constant('SMARTEDIT_ROOT', 'buildArtifacts')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/jsTests/)
    .run(
        function($httpBackend, languageService, I18N_RESOURCE_URI) {

            var map = [{
                "id": "2",
                "value": "\"/thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "id": "3",
                "value": "\"somepath\"",
                "key": "i18nAPIRoot"
            }, {
                "id": "7",
                "value": "{\"smartEditContainerLocation\":\"/jsTarget/personalizationsmarteditcontainer.js\"}",
                "key": "applications.personalizationsmarteditcontainermodule"
            }, {
                "id": "8",
                "value": "{\"smartEditLocation\":\"/jsTarget/personalizationsmartedit.js\"}",
                "key": "applications.personalizationsmarteditmodule"
            }, {
                "id": "9",
                "value": "{\"smartEditLocation\":\"/jsTests/ui/personalizationManagerModal/innerMocksForPersonalizationManagerModal.js\"}",
                "key": "applications.InnerMocks"
            }];

            $httpBackend.whenGET(/configuration/).respond(
                function(method, url, data, headers) {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);

            $httpBackend
                .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
                .respond({
                    'personalization.perspective.name': 'PERSONALIZATION',
                    'personalization.perspective.description': 'Personalization description',
                    'personalization.toolbar.pagecustomizations': 'CUSTOMIZE',
                    'personalization.toolbar.pagecustomizations.header.title': 'CUSTOMIZATIONS ON THIS PAGE',
                    'personalization.toolbar.pagecustomizations.header.description': 'Select a customization and target group and customize a component on this page',
                    'personalization.toolbar.pagecustomizations.addmorecustomizations.button': 'ADD MORE',
                    'personalization.toolbar.pagecustomizations.addmorecustomizations.customization.library.search.placeholder': 'Search customizations in the library',
                    'personalization.toolbar.pagecustomizations.customizations.show': 'Show customizations',
                    'personalization.toolbar.pagecustomizations.customization.enabled': 'Enabled',
                    'personalization.toolbar.pagecustomizations.customization.disabled': 'Disabled',
                    'personalization.toolbar.pagecustomizations.customization.options.edit': 'Edit',
                    'personalization.toolbar.pagecustomizations.variation.enabled': 'Enabled',
                    'personalization.toolbar.pagecustomizations.variation.disabled': 'Disabled',
                    'personalization.toolbar.pagecustomizations.nodatespecified': 'No date',
                    'personalization.modal.customizationvariationmanagement.title': 'Customization',
                    'personalization.modal.customizationvariationmanagement.button.cancel': 'Cancel',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab': 'Basic information',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.name': 'Name',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.name.placeholder': 'Enter a name for this customization',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details': 'Details',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.placeholder': 'Enter a description for this customization',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.startdate': 'Start date and time',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.statusfortimeframe.description': 'Date settings only apply if the customization is enabled. If the customization is disabled the date configuration will have no effect.',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.timeframe.description': 'A customization is active within the Start Date and End Date range. If no Start Date is provided, the customization is automatically active until the End Date. If no End Date is provided the customization becomes active at Start Date and is active indefinitely.',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddate': 'End date and time',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.button.next': 'Next',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.status': 'Status configuration',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.schedule': 'Schedule configuration',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.showdateconfigdata': 'Configure date for activation',
                    'personalization.modal.customizationvariationmanagement.basicinformationtab.details.hidedateconfigdata': 'Remove time configuration',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab': 'Target group',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelconfirmation': 'Confirm you want to leave and lose unsaved information',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.invalidbuttonid': 'A button callback has not been registered for button with id',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.customization': 'Customization',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.customization.enabled': 'Enabled',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.customization.disabled': 'Disabled',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.enabled': 'Enabled',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.disabled': 'Disabled',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.edit': 'Edit',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.remove': 'Remove',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.moveup': 'Move up',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.movedown': 'Move down',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable': 'Enable',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable': 'Disable',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname': 'Name',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.notargetgroups': 'No target group created',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroupname.placeholder': 'Enter target group name',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.segments': 'Segment',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.segments.placeholder': 'Search segments',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.criteria': 'Criteria',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.criteria.colon': 'Criteria:',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.allsegments': 'Match all segments',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.anysegments': 'Match any segment',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.addvariation': 'ADD',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.savechanges': 'APPLY',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelchanges': 'CANCEL',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.targetgroup.uniquename.validation.message': 'Target group name needs to be unique',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.button.submit': 'Save',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.commercecustomization.info': 'Commerce Customizations are managed from the Library',
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.commercecustomization.cxpromotionactiondata': 'Promotion actions',
                    'personalization.toolbar.library.name': 'LIBRARY',
                    'personalization.toolbar.library.header.title': 'CUSTOMIZATION LIBRARY',
                    'personalization.toolbar.library.header.description': 'The library contains all saved customizations for the entire catalog (version)',
                    'personalization.toolbar.library.manager.name': 'MANAGE LIBRARY',
                    'personalization.toolbar.library.customizationvariationmanagement.name': 'CREATE NEW CUSTOMIZATION',
                    'personalization.toolbar.combinedview.name': 'COMBINED VIEW',
                    'personalization.toolbar.combinedview.header.title': 'Combined View Configuration',
                    'personalization.toolbar.combinedview.header.description': 'Select target groups and preview the page matching multiple segments',
                    'personalization.toolbar.combinedview.currentselected.name': 'Current selected customizations and target groups',
                    'personalization.toolbar.combinedview.addmore.name': 'Add more',
                    'personalization.toolbar.combinedview.openconfigure.button': 'Configure',
                    'personalization.modal.combinedview.title': 'Combined View Configuration',
                    'personalization.modal.combinedview.button.ok': 'Save',
                    'personalization.modal.combinedview.button.cancel': 'Cancel',
                    'personalization.modal.manager.title': 'Manage Customization Library',
                    'personalization.modal.manager.search.placeholder': 'Search Customization',
                    'personalization.modal.manager.search.result.label': 'Customization(s) found',
                    'personalization.modal.manager.customization.label': 'customizations on this catalog version',
                    'personalization.modal.manager.add.button': 'CREATE NEW CUSTOMIZATION',
                    'personalization.modal.manager.grid.header.customization': 'Customization',
                    'personalization.modal.manager.grid.header.variations': 'Target Group',
                    'personalization.modal.manager.grid.header.components': 'Components',
                    'personalization.modal.manager.grid.header.status': 'Status',
                    'personalization.modal.manager.grid.header.startdate': 'Start',
                    'personalization.modal.manager.grid.header.enddate': 'End',
                    'personalization.modal.manager.customization.enabled': 'Enabled',
                    'personalization.modal.manager.customization.disabled': 'Disabled',
                    'personalization.modal.manager.customization.nodatespecified': 'No date',
                    'personalization.modal.manager.customization.options.edit': 'Edit',
                    'personalization.modal.manager.customization.options.delete': 'Delete',
                    'personalization.modal.manager.customization.options.moveup': 'Move up',
                    'personalization.modal.manager.customization.options.movedown': 'Move down',
                    'personalization.modal.manager.variation.enabled': 'Enabled',
                    'personalization.modal.manager.variation.disabled': 'Disabled',
                    'personalization.modal.manager.variation.options.enable': 'Enable',
                    'personalization.modal.manager.variation.options.disable': 'Disable',
                    'personalization.modal.manager.variation.options.edit': 'Edit',
                    'personalization.modal.manager.variation.options.commercecustomization': 'Commerce Customization',
                    'personalization.modal.manager.variation.options.delete': 'Delete',
                    'personalization.modal.manager.variation.options.moveup': 'Move up',
                    'personalization.modal.manager.variation.options.movedown': 'Move down',
                    'personalization.modal.manager.deletecustomization.content': 'Are you sure you want to remove selected customization?',
                    'personalization.modal.manager.deletevariation.content': 'Are you sure you want to remove selected target group?',
                    'personalization.modal.manager.targetgrouptab.deletevariation.content': 'Are you sure you want to remove selected target group?',
                    'personalization.modal.manager.commercecustomization.cxpromotionactiondata': 'Promotion actions',
                    'personalization.modal.commercecustomization.title': 'Commerce Customization',
                    'personalization.modal.commercecustomization.button.cancel': 'Cancel',
                    'personalization.modal.commercecustomization.button.submit': 'Save',
                    'personalization.modal.commercecustomization.customization.enabled': 'Enabled',
                    'personalization.modal.commercecustomization.customization.disabled': 'Disabled',
                    'personalization.modal.commercecustomization.variation.enabled': 'Enabled',
                    'personalization.modal.commercecustomization.variation.disabled': 'Disabled',
                    'personalization.modal.commercecustomization.action.type': 'Commerce Customization Action',
                    'personalization.modal.commercecustomization.action.type.promotion': 'Promotion',
                    'personalization.modal.commercecustomization.promotion.label': 'Promotion',
                    'personalization.modal.commercecustomization.promotion.display.name': 'Promotion',
                    'personalization.modal.commercecustomization.promotion.search.placeholder': 'Search Promotion',
                    'personalization.modal.addaction.title': 'Customize Component',
                    'personalization.modal.editaction.title': 'Customize Component',
                    'personalization.modal.addeditaction.createnewcomponent': 'Replace master component creating new one',
                    'personalization.modal.addeditaction.createnewcomponent.banner': 'Banner',
                    'personalization.modal.addeditaction.createnewcomponent.paragraph': 'Paragraph',
                    'personalization.modal.addeditaction.createnewcomponent.simplebanner': 'Simple Banner',
                    'personalization.modal.addeditaction.createnewcomponent.simpleresponsivebanner': 'Simple Responsive Banner',
                    'personalization.modal.addeditaction.usecomponent': 'Replace master component with another saved component',
                    'personalization.modal.addeditaction.selected.customization.title': 'selected customization',
                    'personalization.modal.addeditaction.selected.variation.title': 'selected target group',
                    'personalization.modal.addeditaction.selected.mastercomponent.title': 'master component info',
                    'personalization.modal.addeditaction.selected.actions.title': 'action',
                    'personalization.modal.addeditaction.dropdown.placeholder': 'Select an action type',
                    'personalization.modal.addeditaction.dropdown.componentlist.placeholder': 'Search for a component in the library',
                    'personalization.modal.addeditaction.dropdown.componenttype.placeholder': 'Select component type',
                    'personalization.modal.addeditaction.button.cancel': 'Cancel',
                    'personalization.modal.addeditaction.button.submit': 'Save',
                    'personalization.modal.deleteaction.title': 'Confirm',
                    'personalization.modal.deleteaction.content': 'The component will be restored to default?',
                    'personalization.modal.deleteaction.button.cancel': 'Cancel',
                    'personalization.modal.deleteaction.button.ok': 'Ok',
                    'personalization.context.action.add': 'ADD ACTION',
                    'personalization.context.action.edit': 'EDIT ACTION',
                    'personalization.context.action.delete': 'DELETE ACTION',
                    'personalization.context.action.info': 'INFO',
                    'personalization.context.status.all': 'All',
                    'personalization.context.status.enabled': 'Enabled',
                    'personalization.context.status.disabled': 'Disabled',
                    'personalization.info.creatingcustomization': 'Customization has been successfully created',
                    'personalization.info.updatingcustomization': 'Customization has been successfully updated',
                    'personalization.info.newpreviewticketcreated': 'Preview ticket has been successfully created',
                    'personalization.info.updatingaction': 'Action has been successfully changed',
                    'personalization.info.creatingaction': 'Action has been successfully added',
                    'personalization.info.removingaction': 'Action has been successfully removed',
                    'personalization.info.creatingremovingaction': 'Actions have been successfully modified',
                    'personalization.error.gettingcomponents': 'Error during getting components',
                    'personalization.error.creatingcomponent': 'Error during creating component',
                    'personalization.error.removingcomponent': 'Error during removing component',
                    'personalization.error.gettingcomponentstypes': 'Error during getting components types',
                    'personalization.error.gettingcustomizations': 'Error during getting customizations',
                    'personalization.error.gettingcustomization': 'Error during getting customization',
                    'personalization.error.creatingcustomization': 'Error during creating customization',
                    'personalization.error.updatingcustomization': 'Error during updating customization',
                    'personalization.error.deletingcustomization': 'Error during deleting customization',
                    'personalization.error.gettingsegments': 'Error during getting segments',
                    'personalization.error.gettingvariation': 'Error during getting variation',
                    'personalization.error.editingvariation': 'Error during editing variation',
                    'personalization.error.deletingvariation': 'Error during deleting variation',
                    'personalization.error.gettingcomponentsforvariation': 'Error during getting components for target group',
                    'personalization.error.gettingpreviewticket': 'Error during getting preview ticket',
                    'personalization.error.updatingpreviewticket': 'Error during updating preview ticket',
                    'personalization.error.creatingpreviewticket': 'Error during creating preview ticket',
                    'personalization.error.previewticketexpired': 'Current preview ticket has expired! Creating new preview ticket',
                    'personalization.error.updatingaction': 'Error during updating action',
                    'personalization.error.creatingaction': 'Error during creating action',
                    'personalization.error.deletinggaction': 'Error during removing action',
                    'personalization.error.replacingcomponent': 'Error during replacing component with container',
                    'personalization.error.nocustomizationvariationselected': 'Select a customization and a target group',
                    'personalization.error.gettingactions': 'Error during getting actions',
                    'personalization.error.gettingpromotions': 'Error during getting promotions',
                    'personalization.error.creatingremovingaction': 'Error during creating and removing actions',
                    'personalization.commons.datetimepicker.placeholder': 'mm/dd/yyyy',
                    'personalization.commons.pagination.rowsperpage': 'Rows per page'
                });

            $httpBackend.whenPOST("/thepreviewTicketURI")
                .respond({
                    resourcePath: '/jsTests/ui/common/dummystorefront.html',
                    ticketId: 'dasdfasdfasdfa'
                });

            $httpBackend.whenGET(/fragments/).passThrough();

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    name: 'English',
                    required: true
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/items\/componentId/).respond({
                catalog: 'Some Catalog - Some Catalog Version',
                dateAndTime: 'Some Date',
                language: 'English'
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/segments/).respond({
                "segments": [{
                    "code": "segment1"
                }, {
                    "code": "segment2"
                }, {
                    "code": "VIPGold"
                }, {
                    "code": "segment4"
                }, {
                    "code": "segment5"
                }, {
                    "code": "segment6"
                }, {
                    "code": "segment7"
                }, {
                    "code": "segment8"
                }, {
                    "code": "segment9"
                }, {
                    "code": "segment10"
                }]
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/apparel-ukContentCatalog\/catalogVersions\/Staged\/customizations\/WinterSale/).respond({
                "active": true,
                "code": "WinterSale",
                "name": "WinterSale",
                "rank": 0,
                "status": "ENABLED",
                "variations": [{
                    "active": true,
                    "code": "variationWinterSale1",
                    "enabled": true,
                    "name": "variationWinterSale1",
                    "rank": 0
                }, {
                    "active": true,
                    "code": "variationWinterSale2",
                    "enabled": true,
                    "name": "variationWinterSale2",
                    "rank": 1
                }]
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/.*\/catalogVersions\/.*\/customizations?.*name=winter.*/).respond({
                "customizations": [{
                    "active": true,
                    "code": "WinterSale",
                    "name": "WinterSale",
                    "rank": 0,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationWinterSale1",
                        "enabled": true,
                        "name": "variationWinterSale1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationWinterSale2",
                        "enabled": true,
                        "name": "variationWinterSale2",
                        "rank": 1
                    }]
                }],
                "pagination": {
                    "count": 1,
                    "page": 0,
                    "totalCount": 1,
                    "totalPages": 1
                }
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/.*\/catalogVersions\/.*\/customizations?.*statuses=ENABLED$/).respond({
                "customizations": [{
                    "active": true,
                    "code": "WinterSaleaaaaaaaaaaaaaaaaaaaaaaa",
                    "name": "WinterSaleaaaaaaaaaaaaaaaaaaaaaaaa",
                    "rank": 0,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationWinterSale1",
                        "enabled": true,
                        "name": "variationWinterSale1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationWinterSale2",
                        "enabled": true,
                        "name": "variationWinterSale2",
                        "rank": 1
                    }]
                }, {
                    "active": true,
                    "code": "customizationAAA",
                    "name": "customizationAAA",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationAAA1",
                        "enabled": true,
                        "name": "variationAAA1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationAAA2",
                        "enabled": true,
                        "name": "variationAAA2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationAAA3",
                        "enabled": true,
                        "name": "variationAAA3",
                        "rank": 2
                    }]
                }, {
                    "active": true,
                    "code": "customizationCCC",
                    "name": "customizationCCC",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationCCC1",
                        "enabled": true,
                        "name": "variationCCC1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationCCC2",
                        "enabled": true,
                        "name": "variationCCC2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationCCC3",
                        "enabled": true,
                        "name": "variationCCC3",
                        "rank": 2
                    }]
                }],
                "pagination": {
                    "count": 3,
                    "page": 0,
                    "totalCount": 3,
                    "totalPages": 1
                }
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/.*\/catalogVersions\/.*\/customizations?.*currentPage=0&.*pageSize=5.*/).respond({
                "customizations": [{
                    "active": true,
                    "code": "WinterSale",
                    "name": "WinterSale",
                    "rank": 0,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationWinterSale1",
                        "enabled": true,
                        "name": "variationWinterSale1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationWinterSale2",
                        "enabled": true,
                        "name": "variationWinterSale2",
                        "rank": 1
                    }]
                }, {
                    "active": true,
                    "code": "CategoryLover",
                    "name": "CategoryLover",
                    "rank": 1,
                    "variations": [{
                        "active": true,
                        "code": "variationCategoryLover1",
                        "enabled": true,
                        "name": "variationCategoryLover1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationCategoryLover2",
                        "enabled": true,
                        "name": "variationCategoryLover2",
                        "rank": 1
                    }]
                }, {
                    "active": true,
                    "code": "customizationAAA",
                    "name": "customizationAAA",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationAAA1",
                        "enabled": true,
                        "name": "variationAAA1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationAAA2",
                        "enabled": true,
                        "name": "variationAAA2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationAAA3",
                        "enabled": true,
                        "name": "variationAAA3",
                        "rank": 2
                    }]
                }, {
                    "active": true,
                    "code": "customizationBBB",
                    "name": "customizationBBB",
                    "rank": 2,
                    "variations": [{
                        "active": true,
                        "code": "variationBBB1",
                        "enabled": true,
                        "name": "variationBBB1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationBBB2",
                        "enabled": true,
                        "name": "variationBBB2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationBBB3",
                        "enabled": true,
                        "name": "variationBBB3",
                        "rank": 2
                    }, {
                        "active": true,
                        "code": "variationBBB4",
                        "enabled": true,
                        "name": "variationBBB4",
                        "rank": 3
                    }]
                }, {
                    "active": true,
                    "code": "customizationCCC",
                    "name": "customizationCCC",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationCCC1",
                        "enabled": true,
                        "name": "variationCCC1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationCCC2",
                        "enabled": true,
                        "name": "variationCCC2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationCCC3",
                        "enabled": true,
                        "name": "variationCCC3",
                        "rank": 2
                    }]
                }],
                "pagination": {
                    "count": 5,
                    "page": 0,
                    "totalCount": 6,
                    "totalPages": 2
                }
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/.*\/catalogVersions\/.*\/customizations?.*currentPage=1&.*pageSize=5.*/).respond({
                "customizations": [{
                    "active": true,
                    "code": "customizationDDD",
                    "name": "customizationDDD",
                    "rank": 2,
                    "variations": [{
                        "active": true,
                        "code": "variationDDD1",
                        "enabled": true,
                        "name": "variationDDD1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationDDD2",
                        "enabled": true,
                        "name": "variationDDD2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationDDD3",
                        "enabled": true,
                        "name": "variationDDD3",
                        "rank": 2
                    }, {
                        "active": true,
                        "code": "variationDDD4",
                        "enabled": true,
                        "name": "variationDDD4",
                        "rank": 3
                    }, {
                        "active": true,
                        "code": "variationDDD5",
                        "enabled": true,
                        "name": "variationDDD5",
                        "rank": 4
                    }]
                }],
                "pagination": {
                    "count": 1,
                    "page": 1,
                    "totalCount": 6,
                    "totalPages": 2
                }
            });

            $httpBackend.whenGET(/personalizationwebservices\/v1\/catalogs\/.*\/catalogVersions\/.*\/customizations/).respond({
                "customizations": [{
                    "active": true,
                    "code": "WinterSale",
                    "name": "WinterSale",
                    "rank": 0,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationWinterSale1",
                        "enabled": true,
                        "name": "variationWinterSale1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationWinterSale2",
                        "enabled": true,
                        "name": "variationWinterSale2",
                        "rank": 1
                    }]
                }, {
                    "active": true,
                    "code": "CategoryLover",
                    "name": "CategoryLover",
                    "rank": 1,
                    "variations": [{
                        "active": true,
                        "code": "variationCategoryLover1",
                        "enabled": true,
                        "name": "variationCategoryLover1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationCategoryLover2",
                        "enabled": true,
                        "name": "variationCategoryLover2",
                        "rank": 1
                    }]
                }, {
                    "active": true,
                    "code": "customizationAAA",
                    "name": "customizationAAA",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationAAA1",
                        "enabled": true,
                        "name": "variationAAA1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationAAA2",
                        "enabled": true,
                        "name": "variationAAA2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationAAA3",
                        "enabled": true,
                        "name": "variationAAA3",
                        "rank": 2
                    }]
                }, {
                    "active": true,
                    "code": "customizationBBB",
                    "name": "customizationBBB",
                    "rank": 2,
                    "variations": [{
                        "active": true,
                        "code": "variationBBB1",
                        "enabled": true,
                        "name": "variationBBB1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationBBB2",
                        "enabled": true,
                        "name": "variationBBB2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationBBB3",
                        "enabled": true,
                        "name": "variationBBB3",
                        "rank": 2
                    }, {
                        "active": true,
                        "code": "variationBBB4",
                        "enabled": true,
                        "name": "variationBBB4",
                        "rank": 3
                    }]
                }, {
                    "active": true,
                    "code": "customizationCCC",
                    "name": "customizationCCC",
                    "rank": 2,
                    "status": "ENABLED",
                    "variations": [{
                        "active": true,
                        "code": "variationCCC1",
                        "enabled": true,
                        "name": "variationCCC1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationCCC2",
                        "enabled": true,
                        "name": "variationCCC2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationCCC3",
                        "enabled": true,
                        "name": "variationCCC3",
                        "rank": 2
                    }]
                }, {
                    "active": true,
                    "code": "customizationDDD",
                    "name": "customizationDDD",
                    "rank": 2,
                    "variations": [{
                        "active": true,
                        "code": "variationDDD1",
                        "enabled": true,
                        "name": "variationDDD1",
                        "rank": 0
                    }, {
                        "active": true,
                        "code": "variationDDD2",
                        "enabled": true,
                        "name": "variationDDD2",
                        "rank": 1
                    }, {
                        "active": true,
                        "code": "variationDDD3",
                        "enabled": true,
                        "name": "variationDDD3",
                        "rank": 2
                    }, {
                        "active": true,
                        "code": "variationDDD4",
                        "enabled": true,
                        "name": "variationDDD4",
                        "rank": 3
                    }, {
                        "active": true,
                        "code": "variationDDD5",
                        "enabled": true,
                        "name": "variationDDD5",
                        "rank": 4
                    }]
                }],
                "pagination": {
                    "count": 6,
                    "page": 0,
                    "totalCount": 6,
                    "totalPages": 1
                }
            });
        });

angular.module('smarteditloader').requires.push('uiBackendMocks');
angular.module('smarteditcontainer').requires.push('uiBackendMocks');
angular.module('editorModalServiceModule', []).factory('editorModalService', function() {
    return {};
});
angular.module('ui.tree', []);
