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
 * @name pageInfoDetailsModule
 * @description
 * This module contains pageInfoDetails component.
 */
angular.module('pageInfoDetailsModule', [])

/**
 * @ngdoc directive
 * @name pageInfoDetailsModule.pageInfoDetails
 * @description
 * Directive that can render page information, using an underlying generic editor. Additionally, provides an edit button
 * tied to a parameterized on click callback.
 *
 * @param {String} pageUid The UID of the page of which to render information
 * @param {String} pageTypeCode The type code of the page of which to render information
 * @param {Object} pageStructure The structure used to render the generic editor
 * @param {Object} pageContent The content of the fields of the generic editor
 * @param {Function} onEditClick The callback triggered when clicking the Edit button
 */
.component('pageInfoDetails', {
    templateUrl: 'web/features/cmssmarteditContainer/components/pages/pageInfoMenu/pageInfo/pageInfoDetailsTemplate.html',
    bindings: {
        pageUid: '<',
        pageTypeCode: '<',
        pageStructure: '<',
        pageContent: '<',
        onEditClick: '&'
    }
});
