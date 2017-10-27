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
angular.module('restrictionManagementSelectModule', [
    'alertServiceModule',
    'eventServiceModule',
    'pageRestrictionsModule',
    'restrictionsModule',
    'restrictionsServiceModule',
    'yActionableSearchItemModule',
    'ySelectModule'
])

.controller('RestrictionManagementSelectController', function($q, restrictionManagementSelectModel,
    restrictionsService, systemEventService, alertService) {

    var RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID = "RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID";

    this.resultsHeaderTemplate = "<y-actionable-search-item data-event-id='" + RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID + "'></y-actionable-search-item>";
    this.resultsHeaderLabel = "restrictionmanagement.restrictionresults.header";
    this.itemTemplateUrl = "web/features/cmssmarteditContainer/components/restrictions/restrictionManagement/flavours/restrictionManagementItemNameTemplate.html";
    this.editorHeader = "";

    this.getResultsHeaderTemplate = function getResultsHeaderTemplate() {
        return this.selectModel.isTypeSupported() ? this.resultsHeaderTemplate : "";
    };

    this.selectRestrictionType = function selectRestrictionType() {
        if (this.selectModel.restrictionTypeSelected()) {
            if (this.controllerModel.showRestrictionSelector) {
                this.resetSelector();
            } else {
                this.controllerModel.showRestrictionSelector = true;
            }
            this.controllerModel.showRestrictionEditor = false;
        }
    }.bind(this);

    this.selectRestriction = function selectRestriction() {
        if (this.selectModel.restrictionSelected()) {
            this.editorHeader = 'se.cms.page.restriction.management.select.editor.header.add';
            this.controllerModel.mode = 'add';
            this.controllerModel.structureApi = restrictionsService.getStructureApiUri(this.controllerModel.mode);

            if (this.controllerModel.showRestrictionEditor) {
                this.resetEditor();
            } else {
                this.controllerModel.showRestrictionEditor = true;
            }
        }
    }.bind(this);

    this.createButtonEventHandler = function(eventId, name) {
        this.createRestriction(name);
    }.bind(this);

    this.createRestriction = function createRestriction(name) {

        this.selectModel.createRestrictionSelected(name);
        this.editorHeader = 'se.cms.page.restriction.management.select.editor.header.create';
        this.controllerModel.mode = 'create';
        this.controllerModel.structureApi = restrictionsService.getStructureApiUri(this.controllerModel.mode);
        if (this.controllerModel.showRestrictionEditor) {
            this.resetEditor();
        } else {
            this.controllerModel.showRestrictionEditor = true;
        }
    }.bind(this);

    this.disableRestrictionChoice = function(restriction) {
        var existingIndex = this.existingRestrictions.findIndex(function(existingRestriction) {
            return restriction.uid === existingRestriction.uid;
        });
        return existingIndex !== -1;
    }.bind(this);

    this.$onDestroy = function() {
        systemEventService.unRegisterEventHandler(RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID, this.createButtonEventHandler);
    }.bind(this);

    this.$onInit = function $onInit() {

        this.selectModel = restrictionManagementSelectModel.createRestrictionManagementSelectModel(this.pageTypeCode);

        // bound by the recompile dom directive
        this.resetEditor = function resetEditor() {};
        this.resetSelector = function resetSelector() {};

        this.controllerModel = {
            showRestrictionSelector: false,
            showRestrictionEditor: false,
            mode: 'add',
            contentApi: restrictionsService.getContentApiUri(this.uriContext)
                // selectedRestrictionIndex,
                // restriction,
                // structureApi: restrictionsService.getStructureApiUri('add, this.controllerModel.type')
        };

        this.isDirtyFn = function() {
            if (this.controllerModel.mode === 'add') {
                // if we're in adding mode and an editor is displayed then a restriction has been picked
                return this.controllerModel.showRestrictionEditor;
            } else if (this.isDirtyInternal) {
                // if we're creating a new restriction the use isDirty from GE
                return this.isDirtyInternal();
            }
            return false;
        }.bind(this);


        this.fetchOptions = {
            fetchPage: this.selectModel.getRestrictionsPaged,
            fetchEntity: this.selectModel.getRestrictionFromBackend
        };

        this.submitFn = function() {
            if (this.selectModel.isTypeSupported()) {
                return this.submitInternal().then(function(value) {
                    if (this.controllerModel.mode === 'create') {
                        alertService.pushAlerts([{
                            successful: true,
                            message: 'se.cms.restriction.create.successful',
                            closable: false
                        }]);
                    }
                    return value;
                }.bind(this));
            } else {
                return $q.when(this.selectModel.getRestriction());
            }
        }.bind(this);

        systemEventService.registerEventHandler(RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID, this.createButtonEventHandler);


    }.bind(this);
})

.factory('restrictionManagementSelectModel', function($q, pageRestrictionsFacade, restrictionsFacade) {

    function RestrictionManagementSelectModel(pageTypeCode) {

        var model = {};
        var restrictions;
        var selectedRestriction;

        var supportedRestrictionTypes = [];
        pageRestrictionsFacade.getSupportedRestrictionTypeCodes().then(function(result) {
            supportedRestrictionTypes = result;
        });

        this.selectedIds = {
            // restriction
            // restrictionType
        };

        this.getRestrictionsPaged = function(mask, pageSize, currentPage) {
            return restrictionsFacade.getPagedRestrictionsForType(model.selectedRestrictionType.code, mask, pageSize, currentPage).then(function(pagedRestrictionsResult) {
                restrictions = pagedRestrictionsResult.restrictions;
                var ctr = 0;
                pagedRestrictionsResult.restrictions.forEach(function(restriction) {
                    restriction.id = ctr++;
                }.bind(this));
                pagedRestrictionsResult.results = pagedRestrictionsResult.restrictions;
                delete pagedRestrictionsResult.restrictions;
                return pagedRestrictionsResult;
            }.bind(this));
        }.bind(this);

        this.getRestrictionFromBackend = function() {
            return {};
        };

        this.getRestrictions = function() {
            if (restrictions) {
                return $q.when(restrictions);
            }
            return restrictionsFacade.getAllRestrictions().then(function(restrictionsResult) {
                restrictions = restrictionsResult.restrictions;
                var ctr = 0;
                restrictions.forEach(function(restriction) {
                    restriction.id = ctr++;
                });
                return $q.when(restrictions);
            }.bind(this));
        }.bind(this);

        this.getRestrictionTypes = function() {
            if (model.restrictionTypes) {
                return $q.when(model.restrictionTypes);
            }
            return pageRestrictionsFacade.getRestrictionTypesByPageType(pageTypeCode).then(function(restrictionTypesResponse) {
                model.restrictionTypes = restrictionTypesResponse;
                var ctr = 0;
                model.restrictionTypes.forEach(function(type) {
                    type.id = ctr++;
                });
                return model.restrictionTypes;
            }.bind(this));
        }.bind(this);

        this.restrictionSelected = function() {
            if (this.selectedIds.restriction || this.selectedIds.restriction === 0) {
                selectedRestriction = restrictions.find(function(restriction) {
                    return restriction.id === this.selectedIds.restriction;
                }.bind(this));
                return true;
            }
            return false;
        }.bind(this);

        this.restrictionTypeSelected = function() {
            delete this.selectedIds.restriction;
            model.selectedRestrictionType = model.restrictionTypes.find(function(restrictionType) {
                return restrictionType.id === this.selectedIds.restrictionType;
            }.bind(this));
            if (model.selectedRestrictionType) {
                selectedRestriction = {
                    typeCode: model.selectedRestrictionType.code
                };
                return true;
            }
            return false;
        }.bind(this);

        this.createRestrictionSelected = function(name) {
            // selectedRestriction = null;
            selectedRestriction = {
                typeCode: model.selectedRestrictionType.code,
                name: name
            };
        }.bind(this);

        this.getRestrictionTypeCode = function() {
            return model.selectedRestrictionType.code;
        };

        this.getRestriction = function() {
            return selectedRestriction;
        };

        this.isTypeSupported = function() {
            if (model.selectedRestrictionType && model.selectedRestrictionType.code) {
                return supportedRestrictionTypes.indexOf(model.selectedRestrictionType.code) >= 0;
            }
            return false;
        };
    }


    return {
        createRestrictionManagementSelectModel: function(pageTypeCode) {
            return new RestrictionManagementSelectModel(pageTypeCode);
        }
    };


})


.component('restrictionManagementSelect', {
    controller: 'RestrictionManagementSelectController',
    templateUrl: 'web/features/cmssmarteditContainer/components/restrictions/restrictionManagement/flavours/restrictionManagementSelectTemplate.html',
    bindings: {
        //in
        pageTypeCode: '<',
        uriContext: '<',
        existingRestrictions: '<?',
        //out
        submitFn: '=?',
        isDirtyFn: '=?'
    }
});
