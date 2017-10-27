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
angular.module('displayConditionsPageInfoControllerModule', [])
    .controller('displayConditionsPageInfoController', function() {
        this.displayConditionLabelI18nKey = 'se.cms.display.conditions.label';
        this.pageNameI18nKey = 'pagelist.headerpagename';
        this.pageTypeI18nKey = 'pagelist.headerpagetype';

        this.getPageDisplayConditionI18nKey = function() {
            return this.isPrimary ? 'se.cms.display.conditions.primary.value' : 'se.cms.display.conditions.variation.value';
        };

        this.getPageDisplayConditionDescriptionI18nKey = function() {
            return this.isPrimary ? 'se.cms.display.conditions.primary.description' : 'se.cms.display.conditions.variation.description';
        };
    });
