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
angular.module('restrictionsApp', ['pageRestrictionsEditorModule'])

.controller('testController', function() {

    this.editable = true;
    this.page = {
        "type": "contentPageData",
        "uid": "add-edit-address",
        "typeCode": "ContentPage",
        "onlyOneRestrictionMustApply": false,
        "creationtime": "2016-07-15T23:35:21+0000",
        "defaultPage": true,
        "modifiedtime": "2016-07-15T23:38:01+0000",
        "name": "Add Edit Address Page",
        "pk": "8796095743024",
        "template": "AccountPageTemplate",
        "title": {
            "de": "Adresse hinzufügen/bearbeiten"
        },
        "label": "add-edit-address"
    };

});
