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
angular.module('optionsDropdownPopulatorModule', ['dropdownPopulatorInterfaceModule'])
    /**
     * @ngdoc service
     * @name optionsDropdownPopulatorModule.service:optionsDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
     * containing options attribute.
     */
    .factory('optionsDropdownPopulator', function(DropdownPopulatorInterface, $q, extend) {

        var optionsDropdownPopulator = function() {};

        optionsDropdownPopulator = extend(DropdownPopulatorInterface, optionsDropdownPopulator);

        optionsDropdownPopulator.prototype.populate = function(payload) {
            var options = payload.field.options || [];

            if (payload.search) {
                options = options.filter(function(option) {
                    return option.label.toUpperCase().indexOf(payload.search.toUpperCase()) > -1;
                });
            }

            return $q.when(options);
        };

        return new optionsDropdownPopulator();
    });
