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
angular.module('editPageBasicTabModule', ['editPageBasicTabControllerModule', 'genericEditorModule'])
    .component('editPageBasicTab', {
        templateUrl: 'web/features/cmssmarteditContainer/components/pages/editPageModal/tabs/basic/editPageBasicTabInnerTemplate.html',
        controller: 'editPageBasicTabController',
        bindings: {
            model: '=',
            saveTab: '=',
            resetTab: '=',
            cancelTab: '=',
            isDirtyTab: '=',
            tabId: '='
        }
    });
