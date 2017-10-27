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
angular.module('restrictionsManagementApp', ['restrictionPickerModule'])

.controller('testController', function($q, $timeout, restrictionPickerConfig) {

    this.uriContext = {
        siteUID: 'mySite',
        catalogId: 'myCatalog',
        catalogVersion: 'myCatalogVersion'
    };

    this.selectMode = function(mode) {
        this.show = false;
        this.mode = mode;
        if (mode === 'select') {
            this.config = restrictionPickerConfig.getConfigForSelecting("ContentPage");
        } else {
            this.config = restrictionPickerConfig.getConfigForEditing("someRestrictionId");
        }
        $timeout(function() {
            this.show = true;
        }.bind(this), 0);
    }.bind(this);


    this.submit = function() {
        this.result = this.submitFn().then(function(result) {
            this.result = result;
            return result;
        }.bind(this));
    };

    this.selectMode('select');

});

angular.module('cmssmarteditContainer').requires.push('restrictionsManagementApp');
