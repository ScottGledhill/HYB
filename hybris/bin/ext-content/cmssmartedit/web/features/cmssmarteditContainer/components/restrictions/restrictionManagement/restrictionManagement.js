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
 * @name restrictionPickerModule
 * @description
 * This module defines the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement} component
 **/
angular.module('restrictionPickerModule', ['yLoDashModule', 'recompileDomModule',
    'restrictionManagementSelectModule', 'restrictionManagementEditModule', 'itemManagementModule'
])

/**
 * @ngdoc service
 * @name restrictionPickerModule.service:restrictionPickerConfig
 *
 * @description
 * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
 *
 */
.service('restrictionPickerConfig', function(lodash) {

    this.MODE_EDITING = 'editing';
    this.MODE_SELECT = 'select';

    /**
     * @ngdoc method
     * @name restrictionPickerModule.service:restrictionPickerConfig#getConfigForEditing
     * @methodOf restrictionPickerModule.service:restrictionPickerConfig
     *
     * @param {String} existingRestrictionId The ID of the restriction to be edited
     *
     * @returns {Object} A config object to used with the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement}
     * component in edit mode.
     */
    this.getConfigForEditing = function getConfigForEditing(existingRestrictionId) {
        return {
            mode: this.MODE_EDITING,
            restrictionId: existingRestrictionId
        };
    }.bind(this);

    /**
     * @ngdoc method
     * @name restrictionPickerModule.service:restrictionPickerConfig#getConfigForSelecting
     * @methodOf restrictionPickerModule.service:restrictionPickerConfig
     *
     * @param {String} typeCode The typeCode of the page for which you would like to seelect restrictions
     * @param {Array=} existingRestrictions An array of existing restrictions, that will be not be selectable
     *
     * @returns {Object} A config object to used with the {@link restrictionPickerModule.directive:restrictionManagement restrictionManagement}
     * component in select/create mode.
     */
    this.getConfigForSelecting = function getConfigForSelecting(typeCode, existingRestrictions) {
        return {
            mode: this.MODE_SELECT,
            pageTypeCode: typeCode,
            existingRestrictions: existingRestrictions
        };
    }.bind(this);

    /**
     * @ngdoc method
     * @name restrictionPickerModule.service:restrictionPickerConfig#isEditingMode
     * @methodOf restrictionPickerModule.service:restrictionPickerConfig
     *
     * @param {Object} config A config to check
     *
     * @returns {Boolean} True if the config param is a config object created with
     * {@link restrictionPickerModule.service:restrictionPickerConfig#methods_getConfigForEditing getConfigForEditing()}
     */
    this.isEditingMode = function isEditingMode(config) {
        return config.mode === this.MODE_EDITING;
    }.bind(this);

    /**
     * @ngdoc method
     * @name restrictionPickerModule.service:restrictionPickerConfig#isSelectMode
     * @methodOf restrictionPickerModule.service:restrictionPickerConfig
     *
     * @param {Object} config A config to check
     *
     * @returns {Boolean} True if the config param is a config object created with
     * {@link restrictionPickerModule.service:restrictionPickerConfig#methods_getConfigForSelecting getConfigForSelecting()}
     */
    this.isSelectMode = function isSelectMode(config) {
        return config.mode === this.MODE_SELECT;
    }.bind(this);

    /**
     * @ngdoc method
     * @name restrictionPickerModule.service:restrictionPickerConfig#isValidConfig
     * @methodOf restrictionPickerModule.service:restrictionPickerConfig
     *
     * @param {Object} config A config to check
     *
     * @returns {Boolean} True if the config object was created with proper params
     */
    this.isValidConfig = function isValidConfig(config) {
        return ((config.mode === this.MODE_EDITING && lodash.isString(config.restrictionId)) ||
            (config.mode === this.MODE_SELECT && lodash.isString(config.pageTypeCode)));
    }.bind(this);

})


.controller('RestrictionManagementController', function($q, restrictionPickerConfig) {

    this.$onInit = function() {
        this.submitFn = function() {
            return this.submitInternal().then(function(value) {
                return value;
            });
        }.bind(this);
    };

    this.$onChanges = function $onChanges() {
        if (restrictionPickerConfig.isValidConfig(this.config)) {
            this.editMode = restrictionPickerConfig.isEditingMode(this.config);
            if (this.editMode) {
                this.restrictionId = this.config.restrictionId;
            } else {
                this.pageTypeCode = this.config.pageTypeCode;
                this.existingRestrictions = this.config.existingRestrictions;
            }
        } else {
            throw "restrictionManagementController - invalid restrictionPickerConfig";
        }
    };
})

/**
 * @ngdoc directive
 * @name restrictionPickerModule.directive:restrictionManagement
 * @restrict E
 * @scope
 * @param {<Object} Config object created by {@link restrictionPickerModule.service:restrictionPickerConfig restrictionPickerConfig}
 * @param {<Object} uriContext the {@link resourceLocationsModule.object:UriContext uriContext}
 * @param {=Function=} submitFn A function defined internally. After binding is complete, the caller may execute this
 * function to trigger the POST/PUT depending on the config. Returns a promise resolving to a restriction object
 * @param {=Function=} isDirtyFn A function defined internally. After binding is complete, the caller may execute this
 * function, which return a boolean True if the generic edit use tor represent the restriction is in a dirty state.
 * @description
 * The restrictionManagement Angular component is designed to be able to create new restrictions, editing existing
 * restrictions, or search for restrictions, depending on the config provided.
 */
.component('restrictionManagement', {
    controller: 'RestrictionManagementController',
    templateUrl: 'web/features/cmssmarteditContainer/components/restrictions/restrictionManagement/restrictionManagementTemplate.html',
    bindings: {
        config: '<',
        uriContext: '<',
        submitFn: '=?',
        isDirtyFn: '=?'
    }
});
