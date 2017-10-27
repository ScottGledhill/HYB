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
module.exports = function() {

    var pageObject = {};

    pageObject.elements = {
        getPageType: function() {
            return element(by.css('.page-type-code'));
        },
        getPageTemplate: function() {
            return element(by.css('.page-template'));
        },
        getPageInfoMenuToolbarItem: function() {
            return element(by.css('page-info-menu-toolbar-item'));
        },
        getPageInfoMenuButton: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('button.ySEComponentMenuW--button'));
        },
        getEditButton: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('.ySEPageInfoStaticInfoContainer button'));
        },
        getPageNameField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#name-shortstring'));
        },
        getPageLabelField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#label-shortstring'));
        },
        getPageUidField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#uid-shortstring'));
        },
        getPageTitleField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('[data-tab-id="en"] #title-shortstring'));
        },
        getPageCreationTimeField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#creationtime input[disabled]'));
        },
        getPageModifiedTimeField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#modifiedtime input[disabled]'));
        },
        getPageEditorModal: function() {
            return element(by.css('.modal-dialog'));
        }
    };

    pageObject.actions = {

        openPageInfoMenu: function() {
            pageObject.elements.getPageInfoMenuButton().click();
        },
        clickEditButton: function() {
            pageObject.elements.getEditButton().click();
        }
    };

    return pageObject;

}();
