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
angular.module('dropdownPopulatorInterfaceModule', [])
    /**
     * @ngdoc service
     * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
     *
     * @description
     * Interface describing the contract of a DropdownPopulator fetched through dependency injection by the
     * {@link genericEditorModule.service:GenericEditor GenericEditor} to populate the dropdowns of {@link seDropdownModule.directive:seDropdown seDropdown}.
     */
    .factory('DropdownPopulatorInterface', function() {

        var DropdownPopulatorInterface = function() {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#populate
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         * @description
         * Will returns a promise resolving to a list of items.
         * this method is deprecated, use {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchAll, fetchAll}.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @returns {object} a list of objects.
         */
        DropdownPopulatorInterface.prototype.populate = function(payload) {
            return this.fetchAll(payload);
        };

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchAll
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         * @deprecated
         * @description
         * Will returns a promise resolving to a list of items.
         * The items must all contain a property <b>id</b>.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @returns {object} a list of objects.
         */
        DropdownPopulatorInterface.prototype.fetchAll = function(payload) {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchPage
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Will returns a promise resolving to a {@link Page.object:Page page} of items.
         * The items must all contain a property <b>id</b>.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @param {String} payload.pageSize number of items in the page.
         * @param {String} payload.currentPage current page number.
         * @returns {object} a {@link Page.object:Page page}
         */
        DropdownPopulatorInterface.prototype.fetchPage = function(payload) {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#isPaged
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false
         */
        DropdownPopulatorInterface.prototype.isPaged = function() {
            return false;
        };

        return DropdownPopulatorInterface;
    });
