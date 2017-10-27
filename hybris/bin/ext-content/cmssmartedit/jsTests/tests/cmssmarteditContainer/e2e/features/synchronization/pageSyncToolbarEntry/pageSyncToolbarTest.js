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
describe('Component Menu', function() {

    var defaultPage = new e2e.pageObjects.Default();
    var modeSelector = e2e.componentObjects.modeSelector;
    var syncMenu = e2e.componentObjects.syncMenu;
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;

    beforeEach(function(done) {
        defaultPage.getAndWaitForWholeApp('jsTests/tests/cmssmarteditContainer/e2e/features/synchronization/pageSyncToolbarEntry/pageSyncToolbarEntry.html').then(function() {
            modeSelector.selectAdvancedPerspective().then(function() {
                synchronizationPanel.setupTest();
                done();
            });
        });
    });

    it('GIVEN advanced edit mode is on, WHEN we click on sync toolbar entry THEN a sync panel shows along with a sync button', function() {

        //find and click on sync toolbar entry
        syncMenu.click();
        //assess that sync panel shows
        expect(syncMenu.getPanel().isPresent()).toBe(true);
        //assess that it is related to pages
        expect(syncMenu.getPanelHeader()).toEqual("Synchronize page information and content for non-shared slots.");
        //check the page's checkbox to select all items to sync
        synchronizationPanel.checkItem('All Slots and Page Information');
        //assess that a sync button is clickable
        expect(synchronizationPanel.isSyncButtonEnabled()).toBe(true);
    });

});
