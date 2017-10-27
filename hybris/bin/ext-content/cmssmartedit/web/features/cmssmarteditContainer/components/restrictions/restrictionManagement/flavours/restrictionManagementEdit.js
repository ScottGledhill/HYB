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
angular.module('restrictionManagementEditModule', [
    'alertServiceModule',
    'pageRestrictionsModule',
    'restrictionsServiceModule'
])

.controller('RestrictionManagementEditController', function($q, alertService, restrictionsService, pageRestrictionsFacade) {

    this.ready = false;
    this.restriction = {};
    this.itemManagementMode = 'edit';
    this.contentApi = restrictionsService.getContentApiUri(this.uriContext);
    this.structureApi = restrictionsService.getStructureApiUri(this.itemManagementMode);

    this._internalInit = function(isRestrictionTypeSupported) {
        this.isTypeSupported = isRestrictionTypeSupported;
        if (isRestrictionTypeSupported) {
            this.submitFn = function() {
                return this.submitInternal().then(function(itemResponse) {

                    alertService.pushAlerts([{
                        successful: true,
                        message: 'se.cms.restriction.saved.successful',
                        closable: false
                    }]);

                    return restrictionsService.getById(this.restrictionId).then(function(restriction) {
                        this.restriction = restriction;
                        return $q.when(this.restriction);
                    });

                }.bind(this));
            }.bind(this);
        } else {
            // type not supported, disable the save button always
            this.submitFn = function() {};
            this.isDirtyFn = function() {
                return false;
            };
        }
        this.ready = true;
    }.bind(this);

    this.$onInit = function $onInit() {

        restrictionsService.getById(this.restrictionId).then(function(restriction) {
            this.restriction = restriction;
            return pageRestrictionsFacade.isRestrictionTypeSupported(this.restriction.typeCode).then(function(isSupported) {
                this._internalInit(isSupported);
            }.bind(this));
        }.bind(this));

    };

})

.component('restrictionManagementEdit', {
    controller: 'RestrictionManagementEditController',
    templateUrl: 'web/features/cmssmarteditContainer/components/restrictions/restrictionManagement/flavours/restrictionManagementEditTemplate.html',
    bindings: {
        restrictionId: '<',
        uriContext: '<',
        isDirtyFn: '=?',
        submitFn: '=?'
    }
});
