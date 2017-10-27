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
module.exports = {
    SYNC_PANEL_HEADER: 'synchronization-panel page-synchronization-header .se-sync-panel-header__text span ',
    SYNC_PANEL_LAST_SYNC: 'synchronization-panel page-synchronization-header .se-sync-panel-header__timestamp ',
    SYNC_PANEL_HEADER_HELPER: 'synchronization-panel page-synchronization-header y-help ',
    SYNC_ITEMS_SELECTOR: 'synchronization-panel .se-sync-panel__sync-info__row',
    SYNC_ITEMS_CHECKBOX: 'synchronization-panel .se-sync-panel__sync-info__row div input',
    SYNC_ITEMS_CHECKBOX_SELECTOR: 'synchronization-panel .se-sync-panel__sync-info__row div label',
    ignoreSynchronization: function() {
        browser.ignoreSynchronization = true;
    },
    setupTest: function() {
        this.ignoreSynchronization();
        browser.manage().window().setSize(1700, 1000);
    },
    getSyncPanelHeaderText: function() {
        return element(by.css(this.SYNC_PANEL_HEADER)).getText();
    },
    getSyncPanelLastSyncTime: function() {
        return element(by.css(this.SYNC_PANEL_LAST_SYNC)).getText();
    },
    getSyncPanelHelperText: function() {
        element(by.css(this.SYNC_PANEL_HEADER_HELPER)).click();
    },
    getPopoverAnchor: function() {
        var selector = 'synchronization-panel .se-sync-panel__sync-info__row span[data-y-popover] .popoverAnchor';
        return by.css(selector);
    },
    hoverStatus: function(index) {
        return browser.actions().mouseMove(element.all(this.getPopoverAnchor()).get(index)).perform();
    },
    hoverHelp: function() {
        return browser.actions().mouseMove(element(by.css(this.SYNC_PANEL_HEADER_HELPER))).perform();
    },
    getSyncItemDependenciesContent: function() {
        browser.waitUntil(function() {
            return element.all(by.css('.popover .popover-content')).then(function(popovers) {
                return popovers.length == 1;
            });
        }, 'no popovers are available');

        return element(by.css('.popover .popover-content')).getAttribute("innerHTML").then(function(innerHTML) {
            return innerHTML.replace(" class=\"ng-scope\"", "");
        });
    },
    getSyncItems: function() {
        return element.all(by.css(this.SYNC_ITEMS_SELECTOR + ' label')).map(function(element) {
            return element.getText();
        });
    },
    getSyncItemsCount: function() {
        return element.all(by.css(this.SYNC_ITEMS_SELECTOR + ' label')).count();
    },
    getInSyncItemsCount: function() {
        return element.all(by.css(".hyicon__se-sync-panel__sync-done")).count();
    },
    getSyncItemsStatus: function() {
        return element.all(by.css(this.SYNC_ITEMS_SELECTOR + ' span:first-child')).map(function(element) {
            return element.getAttribute('data-status');
        });
    },
    getPageSyncStatus: function() {
        return element(by.css(this.SYNC_ITEMS_SELECTOR + ':first-child span:first-child')).getAttribute('data-status');
    },
    getSyncItemDependenciesAvailable: function() {

        return this.getSyncItemsStatus().then(function(allStatus) {
            return allStatus.map(function(status) {
                return status === 'IN_SYNC' ? false : true;
            });
        });
    },
    checkItem: function(item) {
        return browser.click(by.cssContainingText(this.SYNC_ITEMS_CHECKBOX_SELECTOR, item));
    },
    getItemsCheckedStatus: function() {
        return element.all(by.css(this.SYNC_ITEMS_CHECKBOX)).map(function(element) {
            return element.isSelected();
        });
    },
    getItemsCheckboxEnabled: function() {
        return element.all(by.css(this.SYNC_ITEMS_CHECKBOX)).map(function(element) {
            return element.isEnabled();
        });
    },
    isSyncButtonEnabled: function() {
        return element(by.cssContainingText('synchronization-panel button', 'Sync')).isEnabled();
    },
    clickSync: function() {
        return browser.click(element(by.cssContainingText('synchronization-panel button', 'Sync'))).then(function() {
            return this.ignoreSynchronization();
        }.bind(this));
    },
    getSyncFailedMessage: function() {
        return browser.waitForPresence(by.css('alerts-box span'), 'cound not find alert box').then(function() {
            return element(by.css('alerts-box span')).getText();
        });
    },
    waitForSyncButtonToBeEnabled: function() {
        return browser.waitUntil(function() {
            return this.isSyncButtonEnabled();
        }.bind(this), 'sync button is not enabled');
    },
    waitForNumberOfSyncedItems: function(number) {
        return browser.waitUntil(function() {
            return this.getInSyncItemsCount().then(function(numberOfItems) {
                return numberOfItems === number;
            });
        }.bind(this), 'number of synced items never reached');
    },
    switchToIFrame: function() {
        return browser.waitForContainerToBeReady().then(function() {
            return browser.switchToIFrame(true).then(function() {
                return this.ignoreSynchronization();
            }.bind(this));
        }.bind(this));
    }

};
