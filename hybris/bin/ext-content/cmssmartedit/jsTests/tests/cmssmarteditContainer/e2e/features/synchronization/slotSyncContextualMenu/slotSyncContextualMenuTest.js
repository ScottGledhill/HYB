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
describe('slotSyncContextualMenu - ', function() {

    var defaultPage = new e2e.pageObjects.Default();
    var storefront = e2e.componentObjects.storefront;
    var modeSelector = e2e.componentObjects.modeSelector;
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;
    var slotContextualMenu = e2e.componentObjects.slotContextualMenu;
    var syncMenu = e2e.componentObjects.syncMenu;

    var TOP_HEADER_SLOT_ID = storefront.TOP_HEADER_SLOT_ID;
    var BOTTOM_HEADER_SLOT_ID = storefront.BOTTOM_HEADER_SLOT_ID;
    var FOOTER_SLOT_ID = storefront.FOOTER_SLOT_ID;
    var OTHER_SLOT_ID = storefront.OTHER_SLOT_ID;

    beforeEach(function(done) {
        defaultPage.getAndWaitForWholeApp('jsTests/tests/cmssmarteditContainer/e2e/features/synchronization/slotSyncContextualMenu/slotSyncContextualMenuTest.html').then(function() {
            modeSelector.selectAdvancedPerspective().then(function() {
                synchronizationPanel.setupTest();
                done();
            });
        });
    });

    it('GIVEN on advanced edit mode WHEN we select sync menu icon for topHeaderSlot then it should be out of sync', function() {
        storefront.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
            expect(slotContextualMenu.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true);
        });
    });

    it('GIVEN on advanced edit mode WHEN we select sync menu icon should show a warning if the slot is not in sync', function() {

        storefront.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
            expect(slotContextualMenu.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync

            storefront.moveToComponent(BOTTOM_HEADER_SLOT_ID).then(function() {
                expect(slotContextualMenu.syncButtonStatusBySlotId(BOTTOM_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync

                storefront.moveToComponent(FOOTER_SLOT_ID).then(function() {
                    expect(slotContextualMenu.syncButtonStatusBySlotId(FOOTER_SLOT_ID).isPresent()).toBe(true); // out of sync

                    storefront.moveToComponent(OTHER_SLOT_ID).then(function() {
                        expect(slotContextualMenu.syncButtonStatusBySlotId(OTHER_SLOT_ID).isPresent()).toBe(false); // in sync
                    });
                });
            });
        });

    });

    it('GIVEN I open sync panel of topHeaderSlot then open sync panel of page WHEN I sync topHeaderSlot from the page panel THEN the status of the slot panel must be automatically updated', function() {

        storefront.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
            expect(slotContextualMenu.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync
            browser.click(slotContextualMenu.syncButtonBySlotId(TOP_HEADER_SLOT_ID)).then(function() {

                syncMenu.click();
                synchronizationPanel.checkItem('All Slots and Page Information');
                synchronizationPanel.clickSync().then(function() {
                    synchronizationPanel.switchToIFrame().then(function() {
                        expect(slotContextualMenu.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(false); // in sync
                    });
                });
            });
        });

    });

});
