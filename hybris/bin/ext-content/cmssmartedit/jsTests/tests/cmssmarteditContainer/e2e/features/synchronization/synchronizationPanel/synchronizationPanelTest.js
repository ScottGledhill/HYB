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
(function() {
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;

    describe('Synchronization Panel', function() {

        beforeEach(function(done) {
            browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/synchronization/synchronizationPanel/synchronizationPanelTest.html');
            synchronizationPanel.ignoreSynchronization();
            done();
        });

        it('WHEN panel is loaded THEN list of items are populated and sync button is enabled by default', function() {

            //header info
            expect(synchronizationPanel.getSyncPanelHeaderText()).toBe('Synchronize page information and content for non-shared slots.');
            expect(synchronizationPanel.getSyncPanelLastSyncTime()).toBe('11/10/16 1:10 PM');

            synchronizationPanel.hoverHelp().then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<span>Shared slots should be synchronized from the slot on the Advanced Edit mode.</span>');
            });

            //sync items
            expect(synchronizationPanel.getSyncItems()).toEqual(['All Slots and Page Information', 'topHeaderSlot', 'bottomHeaderSlot', 'footerSlot']);

            //sync button should be disabled when there are no checked item
            expect(synchronizationPanel.isSyncButtonEnabled()).toBe(false);
        });

        it('WHEN panel is loaded THEN the items have the right status and out of sync dependencies for out of sync items', function() {

            expect(synchronizationPanel.getSyncItemsStatus()).toEqual(['NOT_SYNC', 'NOT_SYNC', 'NOT_SYNC', 'NOT_SYNC']);

            expect(synchronizationPanel.getSyncItemDependenciesAvailable()).toEqual([true, true, true, true]);

            synchronizationPanel.hoverStatus(0).then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<br>MetaData<br>Restrictions<br>Slot<br>Component<br>Navigation<br>Customization');
            });

            synchronizationPanel.hoverStatus(1).then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<br>Component 1');
            });

            synchronizationPanel.hoverStatus(2).then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<br>Component 4');
            });

            synchronizationPanel.hoverStatus(3).then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<br>Restrictions');
            });

        });

        it('WHEN panel is loaded and the first item its checked THEN all other dependencies are checked and disabled', function() {
            expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([false, false, false, false]);
            expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, true, true, true]);

            synchronizationPanel.checkItem('All Slots and Page Information').then(function() {
                expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([true, true, true, true]);
                expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, false, false, false]);
            }).then(function() {
                //uncheck the 1st item then it will leave all the check-boxes selected and enabled
                synchronizationPanel.checkItem('All Slots and Page Information').then(function() {
                    expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([false, true, true, true]);
                    expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, true, true, true]);
                });
            });

        });

        it('WHEN third and fourth items are checked and sync button is clicked' +
            ' THEN first the status of ',
            function() {

                synchronizationPanel.checkItem('bottomHeaderSlot');
                synchronizationPanel.checkItem('footerSlot');
                synchronizationPanel.clickSync().then(function() {

                    expect(synchronizationPanel.isSyncButtonEnabled()).toBe(false);

                    expect(synchronizationPanel.getSyncFailedMessage()).toBe('Sync failed for items: footerSlot');
                    expect(synchronizationPanel.getSyncItemsStatus()).toEqual(['NOT_SYNC', 'NOT_SYNC', 'IN_PROGRESS', 'SYNC_FAILED']);

                    synchronizationPanel.waitForNumberOfSyncedItems(1);
                    expect(synchronizationPanel.getSyncItemsStatus()).toEqual(['NOT_SYNC', 'NOT_SYNC', 'IN_SYNC', 'SYNC_FAILED']);

                    expect(synchronizationPanel.getSyncItemDependenciesAvailable()).toEqual([true, true, false, true]);

                    synchronizationPanel.hoverStatus(2).then(function() {
                        expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('<br>component 5');
                    });

                });

            });

    });
})();
