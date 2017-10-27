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
(function() {
    /**
     * @ngdoc overview
     * @name addPageServiceModule
     * @description
     * # The addPageServiceModule
     *
     * The add page service module provides the functionality necessary to enable the creation of pages through a modal wizard.
     *
     * Use the {@link addPageServiceModule.service:addPageWizardService addPageWizardService} to open the add page wizard modal.
     *
     */
    angular.module('addPageServiceModule', ['wizardServiceModule', 'functionsModule', 'pageTypeServiceModule',
        'languageServiceModule', 'addPageInfoDirectiveModule', 'createPageServiceModule',
        'newPageDisplayConditionModule', 'yLoDashModule', 'experienceServiceModule',
        'selectPageTypeModule', 'selectPageTemplateModule', 'contextAwarePageStructureServiceModule',
        'confirmationModalServiceModule', 'resourceLocationsModule'
    ])


    /**
     * @ngdoc service
     * @name addPageServiceModule.service:addPageWizardService
     *
     * @description
     * The add page wizard service allows opening a modal wizard to create a page.
     */
    .service('addPageWizardService', function(modalWizard) {

        /**
         * @ngdoc method
         * @name addPageServiceModule.service:addPageWizardService#openAddPageWizard
         * @methodOf addPageServiceModule.service:addPageWizardService
         *
         * @description
         * When called, this method opens a modal window containing a wizard to create new pages.
         *
         * @returns {Promise} A promise that will resolve when the modal wizard is closed or reject if it's canceled.
         *
         */
        this.openAddPageWizard = function openAddPageWizard() {
            return modalWizard.open({
                controller: 'addPageWizardController',
                controllerAs: 'addPageWizardCtl'
            });
        };
    })

    .factory('restrictionsStepHandlerFactory', function($q) {

        function RestrictionsStepHandler(wizardManager, restrictionsEditorFunctionBindings) {

            var stepId = 'restrictionsStepId';
            var stepDetails = {
                id: stepId,
                name: 'page.restrictions.editor.tab',
                title: 'se.cms.addpagewizard.pagetype.title',
                templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/templates/pageRestrictionsStepTemplate.html'
            };

            function isStepOnWizard() {
                return wizardManager.containsStep(stepId);
            }

            this.hideStep = function hideStep() {
                if (isStepOnWizard()) {
                    wizardManager.removeStepById(stepId);
                }
            };

            this.showStep = function showStep() {
                if (!isStepOnWizard()) {
                    wizardManager.addStep(stepDetails, wizardManager.getStepsCount());
                }
            };

            this.isStepValid = function isStepValid() {
                return restrictionsEditorFunctionBindings.isDirty && restrictionsEditorFunctionBindings.isDirty();
            };

            this.save = function save() {
                return restrictionsEditorFunctionBindings.save && restrictionsEditorFunctionBindings.save() || $q.when();
            };

            this.getStepId = function getStepId() {
                return stepDetails.id;
            };

            this.goToStep = function goToStep() {
                wizardManager.goToStepWithId(stepId);
            };
        }

        return {
            createRestrictionsStepHandler: function(wizardManager, restrictionsEditorFunctionBindings) {
                return new RestrictionsStepHandler(wizardManager, restrictionsEditorFunctionBindings);
            }
        };
    })

    .factory('pageBuilderFactory', function($routeParams, contextAwarePageStructureService, CONTEXT_SITE_ID,
        CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        function PageBuilder(restrictionsStepHandler) {

            var model = {};
            var page = {};

            function updatePageInfoFields() {

                if (page.defaultPage !== undefined) {
                    if (model.pageType) {
                        contextAwarePageStructureService.getPageStructureForNewPage(model.pageType.code, page.defaultPage).then(
                            function(pageInfoFields) {
                                model.pageInfoFields = pageInfoFields;
                            }
                        );
                    } else {
                        model.pageInfoFields = [];
                    }
                }
            }

            this.pageTypeSelected = function pageTypeSelected(pageTypeObject) {
                model.pageType = pageTypeObject;
                model.pageTemplate = null;
                updatePageInfoFields();
            };

            this.pageTemplateSelected = function pageTemplateSelected(pageTemplateObject) {
                model.pageTemplate = pageTemplateObject;
            };

            this.getPageTypeCode = function getPageType() {
                return model.pageType ? model.pageType.code : null;
            };

            this.getTemplateId = function getTemplateId() {
                return model.pageTemplate ? model.pageTemplate.uid : "";
            };

            this.getPage = function getPage() {
                page.typeCode = model.pageType ? model.pageType.code : null;
                page.type = model.pageType ? model.pageType.type : null;
                page.template = model.pageTemplate ? model.pageTemplate.uid : null;
                return page;
            };

            this.setPageUid = function setPageUid(id) {
                page.uid = id;
                page.newUid = id;
            };

            this.getUriContext = function getUriContext() {
                if (!model.uriContext) {
                    model.uriContext = {};
                    model.uriContext[CONTEXT_SITE_ID] = $routeParams.siteId;
                    model.uriContext[CONTEXT_CATALOG] = $routeParams.catalogId;
                    model.uriContext[CONTEXT_CATALOG_VERSION] = $routeParams.catalogVersion;
                }
                return model.uriContext;
            };

            this.getPageInfoStructure = function getPageInfoStructure() {
                return model.pageInfoFields;
            };

            this.displayConditionSelected = function displayConditionSelected(displayConditionResult) {
                var isPrimaryPage = displayConditionResult.isPrimary;
                page.defaultPage = isPrimaryPage;
                if (isPrimaryPage) {
                    page.label = null;
                    restrictionsStepHandler.hideStep();
                } else {
                    page.label = displayConditionResult.primaryPage ? displayConditionResult.primaryPage.label : "";
                    restrictionsStepHandler.showStep();
                }
                updatePageInfoFields();
            };

        }

        return {
            createPageBuilder: function(restrictionsStepHandler) {
                return new PageBuilder(restrictionsStepHandler);
            }
        };
    })


    /**
     * @ngdoc controller
     * @name addPageServiceModule.controller:addPageWizardController
     *
     * @description
     * The add page wizard controller manages the operation of the wizard used to create new pages.
     */
    .controller('addPageWizardController', function($q, $scope, $timeout, hitch, wizardManager, pageTypeService, pageBuilderFactory,
        languageService, createPageService, lodash, restrictionsStepHandlerFactory, experienceService, confirmationModalService) {

        var self = this;

        var restrictionsEditorFunctionBindingsClosure = {}; // bound in the view for restrictions step
        var restrictionsStepHandler = restrictionsStepHandlerFactory.createRestrictionsStepHandler(wizardManager, restrictionsEditorFunctionBindingsClosure);

        // Constants
        var ADD_PAGE_WIZARD_STEPS = {
            PAGE_TYPE: 'pageType',
            PAGE_TEMPLATE: 'pageTemplate',
            PAGE_DISPLAY_CONDITION: 'pageDisplayCondition',
            PAGE_INFO: 'pageInfo',
            PAGE_RESTRICTIONS: restrictionsStepHandler.getStepId()
        };

        var uriContextCache = {};

        this.pageBuilder = pageBuilderFactory.createPageBuilder(restrictionsStepHandler);

        this.restrictionsEditorFunctionBindings = restrictionsEditorFunctionBindingsClosure;
        this.typeChanged = true;
        this.infoChanged = true;
        this.model = {
            page: {},
            sharedPage: {}
        };

        // Wizard Configuration
        this.getWizardConfig = hitch(this, function() {
            var wizardConfig = {
                isFormValid: hitch(this, this.isFormValid),
                onNext: hitch(this, this.onNext),
                onDone: hitch(this, this.onDone),
                onCancel: this.onCancel,
                steps: [{
                    id: ADD_PAGE_WIZARD_STEPS.PAGE_TYPE,
                    name: 'se.cms.addpagewizard.pagetype.tabname',
                    title: 'se.cms.addpagewizard.pagetype.title',
                    templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/templates/pageTypeStepTemplate.html'
                }, {
                    id: ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE,
                    name: 'se.cms.addpagewizard.pagetemplate.tabname',
                    title: 'se.cms.addpagewizard.pagetype.title',
                    templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/templates/pageTemplateStepTemplate.html'
                }, {
                    id: ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION,
                    name: 'se.cms.addpagewizard.pageconditions.tabname',
                    title: 'se.cms.addpagewizard.pagetype.title',
                    templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/templates/pageDisplayConditionStepTemplate.html'
                }, {
                    id: ADD_PAGE_WIZARD_STEPS.PAGE_INFO,
                    name: 'se.cms.addpagewizard.pageinfo.tabname',
                    title: 'se.cms.addpagewizard.pagetype.title',
                    templateUrl: 'web/features/cmssmarteditContainer/components/pages/addPageWizard/templates/pageInfoStepTemplate.html'
                }]
            };

            return wizardConfig;
        });

        this.onCancel = function onCancel() {
            return confirmationModalService.confirm({
                description: 'editor.cancel.confirm'
            });
        };

        // Wizard Navigation
        this.isFormValid = function(stepId) {
            switch (stepId) {
                case ADD_PAGE_WIZARD_STEPS.PAGE_TYPE:
                    return !!self.pageBuilder.getPageTypeCode();

                case ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE:
                    return !!self.pageBuilder.getTemplateId();

                case ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION:
                    return true;

                case ADD_PAGE_WIZARD_STEPS.PAGE_INFO:
                    return (this.model.editor && this.model.editor.isDirty() === true);

                case ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS:
                    return restrictionsStepHandler.isStepValid();

            }

            return false;
        };

        this.onNext = function(stepId) {
            return $q.when(true);
        };

        this.onDone = function() {
            return self.model.editor.submit(self.model.componentForm).then(function(pageCreated) {
                return restrictionsStepHandler.save().then(function(result) {
                    return experienceService.updateExperiencePageId(self.pageBuilder.getPage().uid);
                }, function() {
                    restrictionsStepHandler.goToStep();
                    return $q.reject();
                });
            }, function() {
                wizardManager.goToStepWithId(ADD_PAGE_WIZARD_STEPS.PAGE_INFO);
                return $q.reject();
            });
        };

        this.createPage = function() {
            var page = this.model.editor.component;
            lodash.defaultsDeep(page, self.pageBuilder.getPage());
            return createPageService.createPage(this.getUriContext(), page).then(function(response) {
                self.pageBuilder.setPageUid(response.uid);
                return {
                    payload: this.model.editor.component,
                    response: response
                };
            }.bind(this));
        }.bind(this);

        this.typeSelected = function typeSelected(pageType) {
            self.infoChanged = true;
            self.typeChanged = true;
            self.pageBuilder.pageTypeSelected(pageType);
        };

        this.templateSelected = function templateSelected(pageTemplate) {
            self.pageBuilder.pageTemplateSelected(pageTemplate);
        };

        this.getUriContext = function getUriContext() {
            if (!uriContextCache.uriContext) {
                uriContextCache.uriContext = self.pageBuilder.getUriContext();
            }
            return uriContextCache.uriContext;
        };

        this.getPageTypeCode = function getPageTypeCode() {
            return self.pageBuilder.getPageTypeCode();
        };

        this.variationResult = function(displayConditionResult) {
            self.infoChanged = true;
            self.pageBuilder.displayConditionSelected(displayConditionResult);
        };

        this.getPageInfo = function getPageInfo() {
            var page = self.pageBuilder.getPage();
            page.uriContext = this.getUriContext();
            return page;
        }.bind(this);

        this.getPageInfoStructure = function getPageInfoStructure() {
            return self.pageBuilder.getPageInfoStructure();
        };

        this.isRestrictionsActive = function isRestrictionsActive() {
            if (!self.typeChanged || wizardManager.getCurrentStepId() === ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS) {
                self.typeChanged = false;
                return true;
            }
            return false;
        };

        this.isPageInfoActive = function isPageInfoActive() {
            if (!self.infoChanged || wizardManager.getCurrentStepId() === ADD_PAGE_WIZARD_STEPS.PAGE_INFO) {
                self.infoChanged = false;
                return true;
            }
            return false;
        };

        this.resetQueryFilter = function() {
            this.query.value = '';
        };


    });
})();
