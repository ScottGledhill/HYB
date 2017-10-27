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
angular.module('pagesContentSlotsMocks', ['ngMockE2E'])
    .run(function($httpBackend) {

        $httpBackend.whenGET(/pagescontentslots\?pageId=.*/).respond({
            pageContentSlotList: [{
                pageId: "homepage",
                slotId: "topHeaderSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "topHeaderSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "topHeaderSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "topHeaderSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "topHeaderSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "bottomHeaderSlot",
                slotShared: false
            }, {
                pageId: "homepage",
                slotId: "footerSlot",
                slotShared: true
            }, {
                pageId: "homepage",
                slotId: "otherSlot",
                slotShared: false
            }]
        });
    });
