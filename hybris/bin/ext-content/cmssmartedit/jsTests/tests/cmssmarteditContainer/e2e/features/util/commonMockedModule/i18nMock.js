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
angular.module('i18nMockModule', ['ngMockE2E', 'resourceLocationsModule', 'languageServiceModule'])
    .run(function($httpBackend, I18N_RESOURCE_URI, languageService) {
        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            'landingpage.title': 'Your Touchpoints',
            'cataloginfo.pagelist': 'PAGE LIST',
            'cataloginfo.lastsynced': 'LAST SYNCED',
            'cataloginfo.button.sync': 'SYNC',
            'se.cms.perspective.basic.name': 'Basic CMS',
            'se.cms.perspective.advanced.name': 'Advanced CMS',
            'slot.shared.popover.message': 'This slot is shared, any changes you make will affect other pages using the same slot.',
            'se.restrictions.menu.btn.label': 'Restrictions',
            'se.restrictions.instance': 'Instance',
            'se.restrictions.type': 'Type',
            'page.restrictions.criteria': 'Criteria:',
            'page.restrictions.criteria.all': 'Match All',
            'page.restrictions.criteria.any': 'Match Any',
            'page.restrictions.list.empty': 'This page has no restrictions',
            'drag.and.drop.not.valid.component.type': 'Component {{componentUID}} not allowed in {{slotUID}}.',
            'compomentmenu.tabs.customizedcomp': 'Customized Components',
            'compomentmenu.tabs.componenttypes': 'Component Types',
            'cmsdraganddrop.error': 'failed to move component component2 to slot footerSlot',
            'se.cms.synchronization.panel.update.title': 'Updates',
            'some.key.for.MetaData': 'MetaData',
            'some.key.for.Restrictions': 'Restrictions',
            'some.key.for.Slot': 'Slot',
            'some.key.for.Navigation': 'Navigation',
            'some.key.for.Customization': 'Customization',
            'some.key.for.Component': 'Component',
            'synchronization.AbstractPage.header': 'Page information and Restrictions',
            'se.cms.synchronization.page.header': 'Synchronize page information and content for non-shared slots.',
            'se.cms.synchronization.page.header.help': 'Shared slots should be synchronized from the slot on the Advanced Edit mode.',
            'synchronization.btn.sync': 'Sync',
            'se.cms.synchronization.panel.lastsync.text': 'Last Page Sync on ',
            'cms.toolbaritem.pagesyncmenu.name': 'Sync',
            'cms.toolbaritem.pagesyncmenu.description': 'This is a synchronization panel to get sync status of a page and some dependencies and to perform sync',
            'se.cms.synchronization.page.title': 'SYNCHRONIZE PAGE',
            'se.cms.synchronization.panel.failure.message': 'Sync failed for items: {{items}}',
            'pageeditormodal.editpagetab.title': 'Page',
            'pagelist.dropdown.sync': 'Sync',
            'se.cms.synchronization.pagelist.modal.title.prefix': 'Synchronize',
            'pagelist.dropdown.edit': 'Edit',
            'compoment.confirmation.modal.cancel': 'Cancel',
            'pagelist.headerrestrictions': 'Restrictions',
            'pagelist.headerpagetype': 'Type',
            'pagelist.headerpagetemplate': 'Template',
            'pagelist.headerpageid': 'ID',
            'pagelist.headerpagename': 'Name',
            'synchronization.topHeaderSlotContentSlot.header': 'Top Header Slot',
            'synchronization.bottomHeaderSlotContentSlot.header': 'Bottom Header Slot',
            'synchronization.footerSlotContentSlot.header': 'Footer Slot',
            'synchronization.otherSlotContentSlot.header': 'Other Slot',
            'some.key.for.component1': 'Component 1',
            'some.key.for.component4': 'Component 4',
            'se.cms.synchronization.slot.header': 'Sync Slot',
            'pagelist.title': 'Page list',
            'pagelist.searchplaceholder': 'Search page',
            'pagelist.countsearchresult': 'Pages found',
            'pagelist.headerpagetitle': 'Page title',
            'pagelist.headerparentpage': 'Parent page',
            'pagelist.dropdown.hide': 'Hide',
            'icon.tooltip.visibility': '{{numberOfRestrictions}} restrictions',
            'se.cms.synchronization.panel.live.recent.notice': 'Live Catalog version has the most recent changes.',
            'se.cms.synchronization.panel.live.override.warning': 'Synchronization will overwrite Stage content to the Live Catalog.',
            'se.cms.synchronization.page.select.all.slots': 'All Slots and Page Information',
            'se.cms.synchronization.slots.select.all.components': 'Select All',
            'component.confirmation.modal.close': 'Close'
        });
    });

try {
    angular.module('smarteditloader').requires.push('i18nMockModule');
    angular.module('smarteditcontainer').requires.push('i18nMockModule');
} catch (ex) {}
