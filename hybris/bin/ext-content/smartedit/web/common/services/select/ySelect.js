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
 * @name ySelectModule
 * @description
 * # The ySelectModule
 *
 */
angular.module('ySelectModule', [
    'functionsModule',
    'yInfiniteScrollingModule',
    'itemPrinterModule',
    'ui.select',
    'ngSanitize',
    'coretemplates',
    'yActionableSearchItemModule'
])

.run(function($templateCache) {

    //use a copy of select2
    $templateCache.put("pagedSelect2/match-multiple.tpl.html", $templateCache.get("select2/match-multiple.tpl.html"));
    $templateCache.put("pagedSelect2/match.tpl.html", $templateCache.get("select2/match.tpl.html"));
    $templateCache.put("pagedSelect2/no-choice.tpl.html", $templateCache.get("select2/no-choice.tpl.html"));
    $templateCache.put("pagedSelect2/select-multiple.tpl.html", $templateCache.get("select2/select-multiple.tpl.html"));
    $templateCache.put("pagedSelect2/select.tpl.html", $templateCache.get("select2/select.tpl.html"));

    //our own flavor of select2 for paging that makes use of yInfiniteScrolling component
    $templateCache.put("pagedSelect2/choices.tpl.html", $templateCache.get("web/common/services/select/uiSelectPagedChoicesTemplate.html"));
    $templateCache.put("select2/choices.tpl.html", $templateCache.get("web/common/services/select/uiSelectChoicesTemplate.html"));
})

.controller('ySelectController', function($q, $log, lodash) {
    // this.items represent the options available in the control to choose from.
    // this.model represents the item(s) currently selected in the control. If the control is using the multiSelect
    // flag then the model is an array; otherwise it's a single object.
    this.items = [];
    this.searchEnabled = this.searchEnabled === false ? false : true;

    // Initialization
    this.$onInit = function() {
        //in order to propagate down changes to ngModel from the parent controller
        this.exposedModel.$viewChangeListeners.push(this.syncModels);
        this.exposedModel.$render = this.syncModels;
    };

    // Basic Functions
    this.clear = function($select, $event) {
        $event.preventDefault();
        $event.stopPropagation();
        delete this.model;
        this.internalOnChange();
    }.bind(this);

    this.resultsHeaderLabel = "yselect.options.inactiveoption.label";
    // this.noResultLabel = "yselect.options.noresultoption.label";

    this.showResultHeader = function() {
        return this.searchEnabled && this.items && this.items.length > 0;
    };

    this.getActionableTemplateUrl = function() {
        return this.actionableSearchItemTemplateConfig ? this.actionableSearchItemTemplateConfig.templateUrl : "";
    }.bind(this);

    //in case of paged dropdown, the triggering of refresh is handled by yInfiniteScrolling component part of the pagedSelect2/choices.tpl.html template
    this.refreshOptions = function(mask) {
        if (this.fetchStrategy.fetchAll) {
            this.fetchStrategy.fetchAll(mask).then(function(items) {
                this.items = items;
            }.bind(this));
        }
    }.bind(this);

    this.reset = function(forceReset) {
        this.items.length = 0;
        if (forceReset) {
            this.model.length = 0;
        }

        return this.$onChanges();
    }.bind(this);

    // Event Listeners

    /*
     * This function is called whenever the value in the ui-select changes from an external source (e.g., like
     * the user making a selection).
     * NOTE: This is not triggered if the model is changed programatically.
     */
    this.syncModels = function() {
        this.model = this.exposedModel.$modelValue;
        this.$onChanges();
    }.bind(this);

    this.$onChanges = function() {
        this.isValidConfiguration();
        this.updateControlTemplate();

        /* we must initialize the list to contain at least the selected item
         * if a fetchEntity has been provided, it will be used
         * if no fetchEntity was provided, we resort to finding a match in the result from fetchAll
         * if we fail to find a match, the directive throws an error to notify that a fetchEntity is required
         */
        var result = $q.when(null);
        if (!this.items || this.items.length === 0) {
            if (!this.isPagedDropdown()) {
                result = this.internalFetchAll();
            } else if (this.fetchStrategy.fetchEntity || this.fetchStrategy.fetchEntities) {
                if (!this.isModelEmpty()) {
                    result = this.internalFetchEntities();
                }
            } else {
                throw "could not initialize dropdown of ySelect, neither fetchEntity, fetchEntities, nor fetchAll were specified";
            }
        }

        return result;
    };

    /*
     * This method is used to propagate to the parent controller the changes made to the model programatically inside
     * this component.
     */
    this.internalOnChange = function() {
        //in order to propagate up changes to ngModel into parent controller
        this.exposedModel.$setViewValue(this.model);
        if (this.onChange) {
            this.onChange();
        }
    }.bind(this);

    // Items retrieval
    this.internalFetchAll = function() {
        return this.fetchStrategy.fetchAll().then(function(items) {
            this.items = items;

            if (this.model) {
                var result;

                if (this.multiSelect) {
                    result = lodash.every(this.model, function(modelKey) {
                        return lodash.find(this.items, function(item) {
                            return item.id === modelKey;
                        });
                    }.bind(this));
                } else {
                    result = items.find(function(item) {
                        return item.id == this.model;
                    }.bind(this));
                }

                if (!result) {
                    $log.debug("[ySelect - " + this.id + "] fetchAll was used to fetch the option identified by " + this.model + " but failed to find a match");
                }

                this.updateModelIfNecessary();
            }

            this.internalOnChange();
        }.bind(this));
    };

    this.internalFetchEntities = function() {
        var promise;
        if (!this.multiSelect) {
            promise = this.fetchEntity(this.model).then(function(item) {
                return [item];
            });
        } else {
            if (this.fetchStrategy.fetchEntities) {
                promise = this.fetchStrategy.fetchEntities(this.model).then(function(items) {
                    if (items.length !== this.model.length) {
                        $log.debug("!fetchEntities was used to fetch the options identified by " + this.model + " but failed to find all matches");
                    }

                    return items;
                }.bind(this));
            } else {
                var promiseArray = [];
                this.model.forEach(function(entryId) {
                    promiseArray.push(this.fetchEntity(entryId));
                }.bind(this));

                promise = $q.all(promiseArray);
            }
        }

        return promise.then(function(result) {
            this.items = result.filter(function(item) {
                return item !== null;
            });

            this.updateModelIfNecessary();

            this.internalOnChange();
        }.bind(this));
    };

    this.fetchEntity = function(entryId) {
        return this.fetchStrategy.fetchEntity(entryId).then(function(item) {
            if (!item) {
                $log.debug("fetchEntity was used to fetch the option identified by " + item + " but failed to find a match");
            }

            return item;
        });
    };

    this.updateModelIfNecessary = function() {
        if (!this.keepModelOnReset) {
            if (this.multiSelect) {
                this.model = this.model.filter(function(modelKey) {
                    return lodash.find(this.items, function(item) {
                        return item && (item.id === modelKey);
                    });
                }.bind(this));

            } else {
                var result = this.items.filter(function(item) {
                    return item.id === this.model;
                }.bind(this));
                this.model = (result.length > 0) ? this.model : null;
            }
        }
    };

    // Helper functions
    this.isValidConfiguration = function() {
        if (!this.fetchStrategy.fetchAll && !this.fetchStrategy.fetchPage) {
            throw "neither fetchAll nor fetchPage have been specified in fetchStrategy";
        }
        if (this.fetchStrategy.fetchAll && this.fetchStrategy.fetchPage) {
            throw "only one of either fetchAll or fetchPage must be specified in fetchStrategy";
        }
        if (this.fetchStrategy.fetchPage && this.model && !this.fetchStrategy.fetchEntity && !this.fetchStrategy.fetchEntities) {
            throw "fetchPage has been specified in fetchStrategy but neither fetchEntity nor fetchEntities are available to load item identified by " + this.model;
        }

        if (this.isPagedDropdown() && !this.keepModelOnReset) {
            $log.debug('current ySelect is paged, so keepModelOnReset flag is ignored (it will always keep the model on reset).');
        }
    };

    this.updateControlTemplate = function() {
        this.theme = this.isPagedDropdown() ? "pagedSelect2" : "select2";
        this.itemTemplate = this.itemTemplate || 'web/common/services/select/defaultItemTemplate.html';
    };

    this.requiresPaginatedStyling = function() {
        return (this.isPagedDropdown() || this.hasControls());
    };

    this.isPagedDropdown = function() {
        return !!this.fetchStrategy.fetchPage;
    };

    this.hasControls = function() {
        return (this.controls || "false") == "true";
    };

    this.isModelEmpty = function() {
        if (this.multiSelect) {
            return !this.model || (this.model && this.model.length === 0);
        } else {
            return !this.model;
        }
    };

    this.disableChoice = function(item) {
        if (this.disableChoiceFn) {
            return this.disableChoiceFn(item);
        }
        return false;
    }.bind(this);

})

/**
 * @ngdoc directive
 * @name ySelectModule.directive:ySelect
 * @scope
 * @restrict E
 * @element y-select
 *
 * @description
 * This component is a wrapper around ui-select directive and provides filtering capabilities for the dropdown menu that is customizable with an item template.
 * <br/>ySelect can work in both paged and non paged mode: providing either fetchOptions or fetchPage function in the fetchStrategy will determine the flavour of the dropdown.
 *
 *
 * @param {String} id will be used to identify internal elements of ySelect for styling (and testing) purposes. Optional
 * @param {boolean} controls Adds controls such as the magnifier and the remove button. Optional, default is set to false.
 * @param {Object} fetchStrategy strategy object containing the necessary functions for ySelect to populate the dropdown:
 * <b>Only one of either fetchOptions or fetchPage must be defined.</b>
 * @param {Function} fetchStrategy.fetchOptions Function required to fetch options for a given optional mask.
 * fetchOptions will be called without arguments upon initialization and with a mask everytime the search section receives an input.
 * It must return a promise resolving to a list of items.
 * Every item must have a property "id" used for identification. If no itemTemplate is provided, these items will need to display a "label" property.
 * @param {Function} fetchStrategy.fetchPage Function required to fetch a page for a given optional mask.
 * fetchPage must fulfill the contract of fetchPage from {@link yInfiniteScrollingModule.directive:yInfiniteScrolling yInfiniteScrolling}
 * It must return a promise resolving to a page of items as per {@link Page.object:Page Page}.
 * Every item must have a property "id" used for identification. If no itemTemplate is provided, these items will need to display a "label" property.
 * @param {Function} fetchStrategy.fetchEntity Function to fetch an option by its identifier when we are in paged mode (fetchPage is defined) and the dropdown is initialized with a value.
 * @param {Function} disableChoiceFn A function to disable results in the drop-down. It is invoked for each item in the drop-down, with a single parameter, the item itself.
 * @param {String} placeholder the placeholder label or i18nKey that will be printed in the search section. Optional.
 * @param {String} itemTemplate the path to the template that will be used to display items in both the dropdown menu and the selection.
 * ItemTemplate has access to item, selected and the ySelect controller.
 * item is the item to print, selected is a boolean that is true when the template is used in the selection as opposed to the dropdown menu.
 * Optional. default template will be:
 * <pre>
 * <span data-ng-bind-html="item.label | translate"></span>
 * </pre>
 */
.directive('ySelect', function($templateCache, $compile) {
    return {
        transclude: true,
        controller: 'ySelectController',
        controllerAs: 'ySelect',
        require: {
            exposedModel: 'ngModel'
        },
        scope: {
            id: '@?',
            fetchStrategy: '<',
            onChange: '<?',
            controls: '@?',
            multiSelect: '<?',
            keepModelOnReset: '<?',
            reset: '=?',
            isReadOnly: '<?',
            resultsHeaderTemplate: '<?',
            resultsHeaderTemplateUrl: '<?',
            resultsHeaderLabel: '<?',
            disableChoiceFn: '<?',
            placeholder: '<?',
            itemTemplate: '<?',
            searchEnabled: '<?'
        },
        bindToController: true,
        link: function(scope, elem, attr) {
            var ySelectTemplate = 'web/common/services/select/ySelectTemplate.html';
            var yMultiSelectTemplate = 'web/common/services/select/yMultiSelectTemplate.html';

            var theme = ((!scope.ySelect.fetchStrategy.fetchPage) ? "select2" : "pagedSelect2");
            var selectedFilters = ((!scope.ySelect.fetchStrategy.fetchPage) ?
                'repeat="item.id as item in ySelect.items | filter: $select.search" refresh="ySelect.refreshOptions($select.search)"' : 'repeat="item.id as item in ySelect.items"'
            );

            var rawTemplate = $templateCache.get((attr.multiSelect) ? yMultiSelectTemplate : ySelectTemplate);
            var result = rawTemplate.replace('<%= theme %>', theme).replace('"<%= filtering %>"', selectedFilters);

            elem.append($compile(result)(scope));
        }
    };
});
