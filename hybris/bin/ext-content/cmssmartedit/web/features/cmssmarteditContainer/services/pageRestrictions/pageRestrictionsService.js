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
 * @name pageRestrictionsServiceModule
 * @description
 * This module provides a service used to consolidate business logic for SAP Hybris platform CMS restrictions for pages.
 */
angular.module('pageRestrictionsServiceModule', [
    'pageRestrictionsRestServiceModule',
    'restrictionTypesServiceModule',
    'restrictionsServiceModule',
    'yLoDashModule',
    'typeStructureRestServiceModule'
])

/**
 * @ngdoc service
 * @name pageRestrictionsServiceModule.pageRestrictionsService
 *
 * @description
 * Service that concerns business logic tasks related to CMS restrictions for CMS pages.
 */
.service('pageRestrictionsService', function($q, restrictionTypesService, pageRestrictionsRestService,
    restrictionsService, lodash, typeStructureRestService) {

    function getOriginalRestrictionsByPageUID(pageId) {
        return $q.all([pageRestrictionsRestService.getPagesRestrictionsForPageId(pageId), restrictionsService.getAllRestrictions()]).then(function(values) {
            var pagesRestrictionsResponse = values[0];
            var restrictionsResponse = values[1];

            var restrictionUIDs = pagesRestrictionsResponse.pageRestrictionList.map(function(pageRestriction) {
                return pageRestriction.restrictionId;
            });

            return restrictionsResponse.restrictions.filter(function(restriction) {
                return restrictionUIDs.indexOf(restriction.uid) >= 0;
            });
        });
    }

    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.getRestrictionsByPageUID
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @param {String} pageId The unique page identifier for which to fetch the restrictions.
     * @returns {Array} An array of all restrictions applied to the page with the given page ID
     */
    this.getRestrictionsByPageUID = function(pageId) {
        return getOriginalRestrictionsByPageUID(pageId).then(function(restrictions) {
            return $q.all(restrictions.map(function(restriction) {
                return restrictionTypesService.getRestrictionTypeForTypeCode(restriction.typeCode).then(function(restrictionType) {
                    return angular.extend({}, restriction, {
                        typeName: restrictionType.name
                    });
                });
            }));
        });
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.updateRestrictionsByPageUID
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @description
     * Update the list of restrictions for a page. The provided list of restrictions replaces any/all restrictions
     * that are currently on the page.
     *
     * @param {String} pageUid The unique page identifier for the page to be updated
     * @param {Array} restrictionsArray An array of restrictions to be applied to the page
     * @returns {Array} All restrictions for the given pageUid
     */
    this.updateRestrictionsByPageUID = function(pageUid, restrictionsArray) {
        var payload = {
            pageid: pageUid,
            pageRestrictionList: []
        };
        restrictionsArray.forEach(function(restriction) {
            payload.pageRestrictionList.push({
                restrictionId: restriction.uid,
                pageId: pageUid
            });
        });
        return pageRestrictionsRestService.update(payload);
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.getPageRestrictionsCountMapForCatalogVersion
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @param {String} siteUID The site Id
     * @param {String} catalogUID The catalog Id
     * @param {String} catalogVersionUID The catalog version
     * @returns {Object} A map of all pageId as keys, and the number of restrictions applied to that page as the values
     */
    this.getPageRestrictionsCountMapForCatalogVersion = function getPageRestrictionsCountMapForCatalogVersion(siteUID, catalogUID, catalogVersionUID) {
        return pageRestrictionsRestService.getPagesRestrictionsForCatalogVersion(siteUID, catalogUID, catalogVersionUID).then(function(relations) {
            return lodash.countBy(relations.pageRestrictionList, 'pageId');
        });
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.getPageRestrictionsCountForPageUID
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @param {String} pageUID The page Id
     * @returns {Number} The number of restrictions applied to the page with the give page UID
     */
    this.getPageRestrictionsCountForPageUID = function getPageRestrictionsCountForPageUID(pageUID) {
        return pageRestrictionsRestService.getPagesRestrictionsForPageId(pageUID).then(function(response) {
            return response.pageRestrictionList.length;
        });
    };

    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.isRestrictionTypeSupported
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @returns {Boolean} True if smartedit supports editing or creating restrictions of this type
     */
    this.isRestrictionTypeSupported = function isRestrictionTypeSupported(restrictionTypeCode) {
        return this.getSupportedRestrictionTypeCodes().then(function(supportedTypes) {
            return supportedTypes.indexOf(restrictionTypeCode) >= 0;
        });
    };


    /**
     * @ngdoc method
     * @name pageRestrictionsServiceModule.pageRestrictionsService.getSupportedRestrictionTypeCodes
     * @methodOf pageRestrictionsServiceModule.pageRestrictionsService
     *
     * @returns {Array} An array of restriction TypeCodes that are supported by SmartEdit
     */
    this.getSupportedRestrictionTypeCodes = function getSupportedRestrictionTypeCodes() {
        return typeStructureRestService.getStructuresByCategory('RESTRICTION').then(function(structures) {
            return lodash.map(structures, function(structure) {
                return structure.code;
            });
        });
    };


});
