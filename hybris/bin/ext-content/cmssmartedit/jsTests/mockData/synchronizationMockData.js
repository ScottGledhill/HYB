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
unit.mockData.synchronization = function() {

    this.SLOT1_SYNC_STATUS = {
        itemId: 'slot1',
        itemType: 'slot1',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3']
    };

    this.SLOT2_SYNC_STATUS = {
        itemId: 'slot2',
        itemType: 'slot2',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem3', 'someItem4']
    };

    this.SLOT3_SYNC_STATUS = {
        itemId: 'slot3',
        itemType: 'slot3',
        status: 'SOME_STATUS'
    };

    this.PAGE_ID1_SYNC_STATUS = {
        itemId: 'pageId1',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [this.SLOT1_SYNC_STATUS, this.SLOT2_SYNC_STATUS],
        sharedDependencies: [this.SLOT3_SYNC_STATUS]
    };

    this.PAGE_ID2_SYNC_STATUS = {
        itemId: 'pageId2',
        itemType: 'page',
        status: 'SOME_STATUS',
        dependentItemTypesOutOfSync: ['someItem1', 'someItem2'],
        selectedDependencies: [this.SLOT1_SYNC_STATUS, this.SLOT2_SYNC_STATUS],
        sharedDependencies: [this.SLOT3_SYNC_STATUS]
    };
};
