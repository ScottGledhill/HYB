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
angular.module('ysmarteditmoduleContainer', ['featureServiceModule', 'abAnalyticsToolbarItemModule'])
    .run(function(featureService) {
        ////////////////////////////////////////////////////
        // Create Toolbar Item
        ////////////////////////////////////////////////////
        // Create the toolbar item as a feature.
        featureService.addToolbarItem({
            toolbarId: 'experienceSelectorToolbar',
            key: 'abAnalyticsToolbarItem',
            type: 'HYBRID_ACTION',
            nameI18nKey: 'ab.analytics.toolbar.item.name',
            priority: 2,
            section: 'left',
            include: 'web/features/ysmarteditmoduleContainer/abAnalyticsToolbarItem/abAnalyticsToolbarItemWrapperTemplate.html'
        });
    });
