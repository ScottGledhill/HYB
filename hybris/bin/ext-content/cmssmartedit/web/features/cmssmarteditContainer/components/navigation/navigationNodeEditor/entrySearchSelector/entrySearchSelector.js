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
angular.module("entrySearchSelectorModule", ['ui.bootstrap', 'ui.select', 'entryDropdownMatcherModule', 'eventServiceModule', 'searchAbstractCMSComponentHandlerServiceModule', 'searchAbstractPageHandlerServiceModule', 'searchMediaHandlerServiceModule'])
    .controller("entrySearchSelectorController", function($injector, $q, systemEventService, LINKED_DROPDOWN, isBlank, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var ITEM_SUPER_TYPE = 'itemSuperType';
        var HANDLER_PREFIX = 'search';
        var HANDLER_Suffix = 'HandlerService';

        /*
         * Event that will be triggered on item type drop down selection change.   
         */
        var selectedItemTypeDropdownEvent = function(key, event) {
            //reset selected values on change
            if (this.reset) {
                this.reset();
            }

            //return if it does not have itemType, otherwise handler will fail
            if (!this.setup(event)) {
                return;
            }

        }.bind(this);

        this._getUriContext = function() {
            var uriContext = {};
            uriContext[CONTEXT_SITE_ID] = this.editor.parameters.siteId;
            uriContext[CONTEXT_CATALOG] = this.editor.parameters.catalogId;
            uriContext[CONTEXT_CATALOG_VERSION] = this.editor.parameters.catalogVersion;
            return uriContext;
        };

        this._fetchEntity = function(id) {
            return this.handlerService.getItem(id, this._getUriContext());
        }.bind(this);

        this._fetchOptions = function(mask) {
            if (this.handlerService) {
                return this.handlerService.getSearchResults(mask, this._getUriContext());
            } else {
                return $q.when([]);
            }
        }.bind(this);

        this._fetchPage = function(mask, pageSize, currentPage) {
            if (this.handlerService) {
                return this.handlerService.getPage(mask, pageSize, currentPage, this._getUriContext());
            } else {
                return $q.when();
            }
        }.bind(this);

        var eventId = this.id + LINKED_DROPDOWN;
        systemEventService.registerEventHandler(eventId, selectedItemTypeDropdownEvent);

        this.fetchStrategy = {
            fetchEntity: this._fetchEntity
        };

        this.setup = function(event) {

            var itemType = event ? this.model[event.qualifier] : this.model.itemSuperType;
            if (itemType) {
                // retrieve the search handler for this item type
                var searchHandlerServiceStrategy = HANDLER_PREFIX + itemType + HANDLER_Suffix;
                if ($injector.has(searchHandlerServiceStrategy)) {
                    this.handlerService = $injector.get(searchHandlerServiceStrategy);
                } else {
                    var errorMessage = "handler not found for " + searchHandlerServiceStrategy;
                    throw errorMessage;
                }

                this.dropdownProperties = this.handlerService.getSearchDropdownProperties();

                if (this.dropdownProperties.isPaged) {
                    this.fetchStrategy.fetchPage = this._fetchPage;
                    delete this.fetchStrategy.fetchAll;
                } else {
                    delete this.fetchStrategy.fetchPage;
                    this.fetchStrategy.fetchAll = this._fetchOptions;
                }

                this.initialized = true;
            }
            return !isBlank(itemType);

        };

        this.$onChanges = function() {
            this.setup();
        };

        this.$onDestroy = function() {
            systemEventService.unRegisterEventHandler(eventId, selectedItemTypeDropdownEvent);
        };

        this.dropdownProperties = {};

    })
    .component('entrySearchSelector', {
        transclude: false,
        replace: false,
        templateUrl: 'web/features/cmssmarteditContainer/components/navigation/navigationNodeEditor/entrySearchSelector/entrySearchSelectorDropdownTemplate.html',
        controller: 'entrySearchSelectorController',
        controllerAs: 'ctrl',
        bindings: {
            model: "=",
            qualifier: "=",
            field: "=",
            id: '=',
            editor: '='
        }
    });
