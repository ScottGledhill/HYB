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
/**
 * @ngdoc overview
 * @name pageRestrictionsEditorModule
 * @description
 * This module contains the {@link pageRestrictionsEditorModule.pageRestrictionsEditor pageRestrictionsEditor} component.
 */
angular.module('pageRestrictionsEditorModule', [
    'cmssmarteditContainerTemplates',
    'clientPagedListModule',
    "eventServiceModule",
    'pageRestrictionsModule',
    'restrictionsTableModule',
    'restServiceFactoryModule',
    "restrictionPickerModule",
    "sliderPanelModule"
])

.controller('pageRestrictionsEditorController', function(
    $scope, $q, $log,
    GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT,
    CONTEXTUAL_PAGES_RESOURCE_URI,
    PAGEINFO_RESOURCE_URI,
    pageRestrictionsFacade,
    restServiceFactory,
    systemEventService,
    restrictionPickerConfig) {

    var getPageInfoRestResource = restServiceFactory.get(PAGEINFO_RESOURCE_URI);
    var updatePageInfoRestResource = restServiceFactory.get(CONTEXTUAL_PAGES_RESOURCE_URI, "pageUid");
    var self = this;

    function setSliderConfigForAddOrCreate() {
        self.sliderPanelConfiguration.modal.title = "se.cms.page.restriction.management.panel.title.add";
        self.sliderPanelConfiguration.modal.save.label = "se.cms.page.restriction.management.panel.button.add";
        self.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
            if (self.restrictionManagement.isDirtyFn) {
                return !self.restrictionManagement.isDirtyFn();
            }
            return true; // disable save until save FN is bound byt restriction management component
        };
        self.sliderPanelConfiguration.modal.save.onClick = function() {
            self.restrictionManagement.submitFn().then(function(restriction) {
                self.restrictions.push(restriction);
                self.sliderPanelHide();
            });
        };
    }

    function setSliderConfigForEditing() {
        self.sliderPanelConfiguration.modal.title = "se.cms.page.restriction.management.panel.title.edit";
        self.sliderPanelConfiguration.modal.save.label = "se.cms.page.restriction.management.panel.button.save";
        self.sliderPanelConfiguration.modal.save.isDisabledFn = function() {
            if (self.restrictionManagement.isDirtyFn) {
                return !self.restrictionManagement.isDirtyFn();
            }
            return true; // disable save until save FN is bound byt restriction management component
        };
        self.sliderPanelConfiguration.modal.save.onClick = function() {
            self.restrictionManagement.submitFn().then(function(restrictionEdited) {
                var index = self.restrictions.findIndex(function(restriction) {
                    return restriction.uid === restrictionEdited.uid;
                });
                if (index !== -1) {
                    self.restrictions[index] = restrictionEdited;
                } else {
                    throw "pageRestrictionsEditorController - edited restriction not found in list: " + restrictionEdited;
                }
                self.sliderPanelHide();
            });
        };
    }

    this.showPageRestrictionPicker = false;
    this.restrictions = [];
    this.orrigRestrictionIds = [];
    this.restrictionsArrayIsDirty = false;
    this.criteria = {};
    this.orrigCriteria = {};
    this.matchCriteriaIsDirty = false;

    this.sliderPanelConfiguration = {
        modal: {
            showDismissButton: true,
            cancel: {
                label: "se.cms.page.restriction.management.panel.button.cancel",
                onClick: function() {
                    self.sliderPanelHide();
                }
            },
            save: {}
        },
        cssSelector: "#y-modal-dialog"
    };

    this._getPageId = function() {
        return (self.page.newUid) ? self.page.newUid : self.page.uid;
    };

    this.onClickOnAdd = function() {
        setSliderConfigForAddOrCreate();
        this.restrictionManagement.operation = restrictionPickerConfig.getConfigForSelecting(this.page.typeCode, this.restrictions);
        this.sliderPanelShow();
    }.bind(this);

    this.onClickOnEdit = function(restriction) {
        setSliderConfigForEditing();
        this.restrictionManagement.operation = restrictionPickerConfig.getConfigForEditing(restriction.uid);
        this.sliderPanelShow();
    }.bind(this);

    this.matchCriteriaChanged = function(criteriaSelected) {
        this.criteria = criteriaSelected;
        this.matchCriteriaIsDirty = this.criteria != this.orrigCriteria;
    }.bind(this);

    this.restrictionCriteria = pageRestrictionsFacade.getRestrictionCriteriaOptionFromPage(this.page);

    this.saveFn = function() {
        if (!self.isDirtyFn()) {
            return $q.when(true);
        }

        // could be optimized to only send the changed
        var pageID = self._getPageId();
        return getPageInfoRestResource.get({
            pageUid: pageID
        }).then(function(page) {
            page.onlyOneRestrictionMustApply = self.criteria.value;
            page.pageUid = pageID;

            return updatePageInfoRestResource.update(page).then(function() {
                return pageRestrictionsFacade.updateRestrictionsByPageUID(pageID, self.restrictions);
            }, function(failures) {
                //report errors to the correct tab since unrelated errors with other tabs will flag the restriction tab as faulty
                systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, failures.data.errors.filter(function(error) {
                    return error.type === 'ValidationError';
                }));
                return $q.reject(false);
            });
        });
    };

    this.resetFn = function() {
        return true;
    };

    this.cancelFn = function() {
        return $q.when(true);
    };

    this.isDirtyFn = function() {
        return self.restrictionsArrayIsDirty || (self.matchCriteriaIsDirty && self.restrictions.length >= 2);
    };


    this.$onInit = function() {

        function setupResults(results) {
            self.restrictions = results;
            results.forEach(function(restriction) {
                self.orrigRestrictionIds.push(restriction.uid);
            });

            $scope.$watchCollection('$ctrl.restrictions', function(newCol, oldCol) {
                if (newCol.length != self.orrigRestrictionIds.length) {
                    self.restrictionsArrayIsDirty = true;
                    return;
                }

                var isDirty = false;
                newCol.forEach(function(element, index) {
                    if (element.uid !== self.orrigRestrictionIds[index]) {
                        isDirty = true;
                    }
                });
                self.restrictionsArrayIsDirty = isDirty;
            });
        }

        if (self.initialRestrictions) {
            setupResults([self.initialRestrictions]);
        } else {
            if (self.page.uid) {
                // fetch restrictions for this page and setup the dirty state watchers
                pageRestrictionsFacade.getRestrictionsByPageUID(self.page.uid).then(
                    function(results) {
                        setupResults(results);
                    }
                );
            } else {
                setupResults([]);
            }
        }

        self.criteria = pageRestrictionsFacade.getRestrictionCriteriaOptionFromPage(self.page);
        self.orrigCriteria = self.criteria;
        self.restrictionManagement = {
            uriContext: self.page.uriContext
        };


    };
})

/**
 * @ngdoc directive
 * @name pageRestrictionsEditorModule.pageRestrictionsEditor
 * @restrict E
 * @scope
 * @description
 * The purpose of this directive is to allow the user to manage the restrictions for a given page. The restrictionsEditor has an editable and non-editable mode.
 * It uses the restrictionsTable to display the list of restrictions and it uses the restrictionsPicker to add or remove the restrictions.
 * 
 * @param {=Object} page The page object for the page you want to manage restrictions.
 * @param {Boolean} page.onlyOneRestrictionMustApply The page object for the page you want to manage restrictions.
 * @param {String} page.uid The id of the page. Required if not passing initialRestrictions. Used to fetch and update restrictions for the page.
 * @param {String} page.newUid If page.uid is not known at the compile time of the pageRestrictionsEditor, then you can set this field before calling the saveFn.
 * @param {<Object} page.uriContext the {@link resourceLocationsModule.object:UriContext uriContext}
 * @param {=Boolean} editable Boolean to determine whether the editor is enabled.
 * @param {=Array=} initialRestrictions An array of initial restrictions to be loaded in the restrictions editor. If initialRestrictions is provided, then the initial fetching of restrictions for the page is prevented, and this array is used instead.
 * @param {=Function=} saveFn Function that saves the result of the editor. This means saving the restriction criteria if there is more than 1 restrictions, and the restrictions themselves. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
 * @param {=Function=} resetFn Function that returns true. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
 * @param {=Function=} cancelFn Function that returns a promise. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
 * @param {=Function=} isDirtyFn Function that returns a boolean. This function is defined in the restrictionsEditor controller and exists only to provide an external callback.
 */
.component('pageRestrictionsEditor', {
    templateUrl: 'web/features/cmssmarteditContainer/components/pageRestrictions/pageRestrictionsEditor/pageRestrictionsEditorTemplate.html',
    controller: 'pageRestrictionsEditorController',
    scope: {},
    bindings: {
        page: '=',
        editable: '=',
        initialRestrictions: '=?',
        saveFn: '=?',
        resetFn: '=?',
        cancelFn: '=?',
        isDirtyFn: '=?'
    }
});
