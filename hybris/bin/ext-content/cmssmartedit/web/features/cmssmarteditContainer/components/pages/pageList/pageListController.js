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
 * @name pageListModule
 * @description
 *
 * The page list module contains the controller associated to the page list view.
 *
 * This view displays the list of pages for a specific catalog and allows the user
 * to search and sort the list.
 *
 * Use the {@link pageListServiceModule.service:pageListService pageListService}
 * to call backend API in order to get the list of pages for a specific catalog
 *
 */
angular.module('pageListControllerModule', ['pageListServiceModule', 'functionsModule', 'urlServiceModule', 'addPageServiceModule', 'experienceServiceModule', 'eventServiceModule', 'resourceLocationsModule', 'pageEditorModalServiceModule', 'syncPageModalServiceModule', 'restrictionsPageListIconModule', 'pageListSyncIconModule', 'pageRestrictionsServiceModule'])

/**
 * @ngdoc controller
 * @name pageListModule.controller:pageListController
 *
 * @description
 * The page list controller fetches pages for a specified catalog using the {@link pageListServiceModule.service:pageListService pageListService}
 */
.controller('pageListController', function($scope, $routeParams, $location, $q, urlService, STOREFRONT_PATH_WITH_PAGE_ID,
    hitch, pageListService, catalogService, addPageWizardService, experienceService,
    sharedDataService, systemEventService, LANDING_PAGE_PATH, pageEditorModalService,
    syncPageModalService, pageRestrictionsService, $timeout, CONTEXT_SITE_ID,
    CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

    this.siteUID = $routeParams.siteId;
    this.catalogId = $routeParams.catalogId;
    this.catalogVersion = $routeParams.catalogVersion;

    urlService.buildUriContext(this.siteUID, this.catalogId, this.catalogVersion).then(function(uriContext) {

        experienceService.buildDefaultExperience($routeParams).then(function(experience) {

            sharedDataService.set('experience', experience).then(function() {

                systemEventService.sendAsynchEvent("experienceUpdate");

                catalogService.isContentCatalogVersionNonActive().then(function(isNonActive) {

                    this.uriContext = uriContext;
                    this.pages = [];
                    this.catalogName = "";
                    this.itemsReady = false;
                    this.searchKeys = ['name', 'uid', 'typeCode', 'template'];
                    this.query = {
                        value: ""
                    };
                    this.keys = [{
                        property: 'name',
                        i18n: 'pagelist.headerpagename'
                    }, {
                        property: 'uid',
                        i18n: 'pagelist.headerpageid'
                    }, {
                        property: 'typeCode',
                        i18n: 'pagelist.headerpagetype'
                    }, {
                        property: 'template',
                        i18n: 'pagelist.headerpagetemplate'
                    }, {
                        property: 'numberOfRestrictions',
                        i18n: 'pagelist.headerrestrictions'
                    }];
                    if (isNonActive) {
                        this.keys.push({
                            property: 'syncStatus',
                            i18n: 'pagelist.dropdown.sync'
                        });
                    }

                    this.reset = function() {
                        this.query.value = '';
                    };

                    var filteredCatalog = [];

                    this.reloadPages = function() {
                        var uriContext = {};
                        uriContext[CONTEXT_SITE_ID] = this.siteUID;
                        uriContext[CONTEXT_CATALOG] = this.catalogId;
                        uriContext[CONTEXT_CATALOG_VERSION] = this.catalogVersion;
                        $q.all([
                            pageListService.getPageListForCatalog(uriContext),
                            pageRestrictionsService.getPageRestrictionsCountMapForCatalogVersion(this.siteUID, this.catalogId, this.catalogVersion)
                        ]).then(function(values) {
                            var pages = values[0];
                            var pageRestrictionsCountMap = values[1];

                            this.pages = pages.map(function(page) {
                                return {
                                    name: page.name,
                                    uid: page.uid,
                                    typeCode: page.typeCode,
                                    template: page.template,
                                    onlyOneRestrictionMustApply: page.onlyOneRestrictionMustApply,
                                    numberOfRestrictions: pageRestrictionsCountMap[page.uid] || 0
                                };
                            });
                            this.itemsReady = true;
                        }.bind(this));
                    };

                    this.reloadPages();

                    catalogService.getCatalogsForSite(this.siteUID).then(function(catalogs) {
                        filteredCatalog = catalogs.filter(hitch(this, function(catalog) {
                            return catalog.catalogVersion === this.catalogVersion;
                        }));

                        if (filteredCatalog.length == 1) {
                            this.catalogName = filteredCatalog[0].name;
                        }

                    }.bind(this));

                    // renderers Object that contains custom HTML renderers for a given key
                    this.renderers = {
                        name: function(item, key) {
                            return '<a data-ng-click="injectedContext.onLink( item.uid )">{{ item.name }}</a>';
                        },
                        numberOfRestrictions: function(item, key) {
                            return '<restrictions-page-list-icon data-number-of-restrictions="item.numberOfRestrictions"/>';
                        },
                        syncStatus: function(item, key) {
                            return '<page-list-sync-icon data-uri-context="injectedContext.uriContext" data-page-id="item.uid" />';
                        }
                    };

                    this.reloadUpdatedPage = function(uid, updatedUid) {

                        var search = this.query.value; // if search value exists, save it, delete from scope and reset it after pages list is updated
                        delete this.query.value;

                        updatedUid = updatedUid || uid;
                        return $q.all([
                            pageListService.getPageById(updatedUid),
                            pageRestrictionsService.getPageRestrictionsCountForPageUID(updatedUid)
                        ]).then(function(values) {
                            var updatedPage = values[0];
                            updatedPage.numberOfRestrictions = values[1];
                            this.pages = this.pages.map(function(page) {
                                return page.uid === uid ? updatedPage : page;
                            }.bind(this));

                            this.query.value = search;
                        }.bind(this));
                    };

                    this.dropdownItems = [{
                        key: 'pagelist.dropdown.edit',
                        callback: function(item) {

                            item.uriContext = uriContext;

                            pageEditorModalService.open(item).then(function(response) {
                                //in this case uid is not editable so the item.uid is equal to updated uid
                                if (!response.uid) {
                                    response.uid = item.uid;
                                }
                                this.reloadUpdatedPage(item.uid, response.uid);
                            }.bind(this));
                        }.bind(this)
                    }];

                    if (isNonActive) {
                        this.dropdownItems.push({
                            key: 'pagelist.dropdown.sync',
                            callback: function(item) {
                                syncPageModalService.open(item, this.injectedContext.uriContext);
                            }.bind(this)
                        });
                    }
                    // injectedContext Object. This object is passed to the client-paged-list directive.
                    this.injectedContext = {
                        onLink: function(uid) {
                            if (uid) {
                                var experiencePath = this._buildExperiencePath(uid);
                                //iFrameManager.setCurrentLocation(link);
                                $location.path(experiencePath);
                            }
                        }.bind(this),

                        uriContext: this.uriContext
                    };

                    this._buildExperiencePath = function(uid) {
                        return STOREFRONT_PATH_WITH_PAGE_ID
                            .replace(":siteId", this.siteUID)
                            .replace(":catalogId", this.catalogId)
                            .replace(":catalogVersion", this.catalogVersion)
                            .replace(":pageId", uid);
                    };

                    this.openAddPageWizard = function() {
                        addPageWizardService.openAddPageWizard().then(function() {
                            this.reloadPages();
                        }.bind(this));
                    };

                }.bind(this));

            }.bind(this), function(buildError) {
                $log.error("the provided path could not be parsed: " + $location.url());
                $log.error(buildError);
                $location.url(LANDING_PAGE_PATH);
            });
        }.bind(this));
    }.bind(this));
});
