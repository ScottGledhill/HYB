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
 * @name restrictionsServiceModule
 * @description
 * This module provides a service used to consolidate business logic for SAP Hybris platform CMS restrictions.
 */
angular.module('restrictionsServiceModule', ['restrictionsRestServiceModule', 'structuresRestServiceModule'])

/**
 * @ngdoc service
 * @name restrictionsServiceModule.restrictionsService
 *
 * @description
 * Service that concerns business logic tasks related to CMS restrictions in the SAP Hybris platform.
 */
.service('restrictionsService', function(restrictionsRestService, structuresRestService, structureModeManagerFactory) {

    var modeManager = structureModeManagerFactory.createModeManager(["add", "edit", "create"]);

    /**
     * @ngdoc method
     * @name restrictionsServiceModule.restrictionsService.getAllRestrictions
     * @methodOf restrictionsServiceModule.restrictionsService
     *
     * @returns {Array} All restrictions in the system (any type of restriction)
     */
    this.getAllRestrictions = function() {
        return restrictionsRestService.get();
    };

    /**
     * @ngdoc method
     * @name restrictionsServiceModule.restrictionsService.getStructureApiUri
     * @methodOf restrictionsServiceModule.restrictionsService
     *
     * @param {String} mode The structure mode
     * @param {String} mode Optional typecode, if omited will leave a placeholder in URI that will be replaced with the item.typeCode
     *
     * @returns {String} A URI for the structure of restrictions, given a structure mode
     */
    this.getStructureApiUri = function getStructureApiUri(mode, typeCode) {
        modeManager.validateMode(mode);
        return structuresRestService.getUriForContext(mode, typeCode);
    };

    /**
     * @ngdoc method
     * @name restrictionsServiceModule.restrictionsService.getContentApiUri
     * @methodOf restrictionsServiceModule.restrictionsService
     *
     * @param {Object} uriContext the {@link resourceLocationsModule.object:UriContext uriContext}
     *
     * @returns {String} A URI to CRUD restrictions, to a given site/catalog/version
     */
    this.getContentApiUri = function getContentApiUri(uriContext) {
        return restrictionsRestService.getContentApiUri(uriContext);
    };

    /**
     * @ngdoc method
     * @name restrictionsServiceModule.restrictionsService.getById
     * @methodOf restrictionsServiceModule.restrictionsService
     *
     * @return {Object} The restriction matching the given ID
     */
    this.getById = function getById(restrictionId) {
        return restrictionsRestService.getById(restrictionId).then(function(restriction) {
            return restriction;
        });
    };

    /**
     * @ngdoc method
     * @name restrictionsServiceModule.restrictionsService.getById
     * @methodOf restrictionsServiceModule.restrictionsService
     *
     * @return {Object} The restriction matching the given ID
     */
    this.getPagedRestrictionsForType = function(restrictionTypeCode, mask, pageSize, currentPage) {
        return restrictionsRestService.get({
            pageSize: pageSize,
            currentPage: currentPage,
            mask: mask,
            sort: 'name:ASC',
            params: "typeCode:" + restrictionTypeCode
        });
    };

});
