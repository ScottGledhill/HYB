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
module.exports = (function() {

    var pageObject = {};

    function getTrimmedTextFromElement(element) {
        return element.getText().then(function(text) {
            return text.trim();
        });
    }

    pageObject.elements = {
        getSuccessfulEditPageButton: function() {
            return element(by.id('openPageEditorSuccess'));
        },
        getFailEditPageButton: function() {
            return element(by.id('openPageEditorFail'));
        },
        getPrimaryContentPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Primary Content Page')).element(by.css('button'));
        },
        getVariationContentPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Variation Content Page')).element(by.css('button'));
        },
        getPrimaryCategoryPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Primary Category Page')).element(by.css('button'));
        },
        getVariationCategoryPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Variation Category Page')).element(by.css('button'));
        },
        getPrimaryProductPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Primary Product Page')).element(by.css('button'));
        },
        getVariationProductPageModalButton: function() {
            return element(by.cssContainingText('tr', 'Variation Product Page')).element(by.css('button'));
        },
        getLabelField: function() {
            return element(by.id('label-shortstring'));
        },
        getNameField: function() {
            return element(by.id('name-shortstring'));
        },
        getUidField: function() {
            return element(by.id('uid-shortstring'));
        },
        getSaveButton: function() {
            return element(by.id('save'));
        },
        getCancelButton: function() {
            return element(by.id('cancel'));
        },
        getDisplayConditionsTab: function() {
            return element(by.cssContainingText('[data-tab-id="editPageDisplayConditionsTab"] a', 'Condition'));
        },
        getModal: function() {
            return element(by.css('.modal-dialog'));
        },
        getBasicInfoTabHeader: function() {
            return element(by.css('li[data-tab-id="editPageBasicTab"] a'));
        },
        getBasicInfoTabHeaderClass: function() {
            return this.getBasicInfoTabHeader().getAttribute('class');
        },
        getNameErrorsElement: function() {
            return element(by.css('#name se-generic-editor-field-errors'));
        },
        getNameErrorsText: function() {
            return getTrimmedTextFromElement(this.getNameErrorsElement());
        },
        getDisplayConditionsPageNameText: function() {
            return getTrimmedTextFromElement(element(by.css('.dc-page-name')));
        },
        getDisplayConditionsPageTypeText: function() {
            return getTrimmedTextFromElement(element(by.css('.dc-page-type')));
        },
        getPageDisplayConditionText: function() {
            return getTrimmedTextFromElement(element(by.css('.dc-page-display-condition')));
        },
        getPageVariationsFirstRow: function() {
            return element(by.css('display-conditions-page-variations tbody tr:nth-child(1)'));
        },
        getPageVariationsSecondRow: function() {
            return element(by.css('display-conditions-page-variations tbody tr:nth-child(2)'));
        },
        getPageNameOfFirstPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsFirstRow()
                .element(by.css('.paged-list-item-pageName')));
        },
        getCreationDateOfFirstPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsFirstRow()
                .element(by.css('.paged-list-item-creationDate')));
        },
        getRestrictionsOfFirstPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsFirstRow()
                .element(by.css('.paged-list-item-restrictions')));
        },
        getPageNameOfSecondPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsSecondRow()
                .element(by.css('.paged-list-item-pageName')));
        },
        getCreationDateOfSecondPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsSecondRow()
                .element(by.css('.paged-list-item-creationDate')));
        },
        getRestrictionsOfSecondPageVariationRow: function() {
            return getTrimmedTextFromElement(pageObject.elements.getPageVariationsSecondRow()
                .element(by.css('.paged-list-item-restrictions')));
        },
        getPrimaryPageDropdownToggle: function() {
            return element(by.css('display-conditions-primary-page .ui-select-toggle'));
        },
        getSelectedOptionText: function() {
            return getTrimmedTextFromElement(element(by.css('.ui-select-container')));
        },
        getFirstAvailableOptionText: function() {
            return getTrimmedTextFromElement(element(by.cssContainingText('.ui-select-choices-row', 'Primary Content Page')));
        },
        getSecondAvailableOptionText: function() {
            return getTrimmedTextFromElement(element(by.cssContainingText('.ui-select-choices-row', 'Some Other Primary Content Page')));
        },
        getThirdAvailableOptionText: function() {
            return getTrimmedTextFromElement(element(by.cssContainingText('.ui-select-choices-row', 'Another Primary Content Page')));
        },
        getNoAssociatedVariationPagesText: function() {
            return getTrimmedTextFromElement(element(by.css('.dc-no-variations')));
        },
        getAssociatedPrimaryPageText: function() {
            return getTrimmedTextFromElement(element(by.css('.dc-associated-primary-page')));
        }
    };

    pageObject.actions = {
        openPageEditorModalTestPage: function() {
            browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/pageEditorModal/pageEditorModalTest.html');
        },
        openSuccessfulEditPageModal: function() {
            pageObject.elements.getSuccessfulEditPageButton().click();
        },
        openFailEditPageModal: function() {
            pageObject.elements.getFailEditPageButton().click();
        },
        openPrimaryContentPageModal: function() {
            pageObject.elements.getPrimaryContentPageModalButton().click();
        },
        openVariationContentPageModal: function() {
            pageObject.elements.getVariationContentPageModalButton().click();
        },
        openPrimaryCategoryPageModal: function() {
            pageObject.elements.getPrimaryCategoryPageModalButton().click();
        },
        openVariationCategoryPageModal: function() {
            pageObject.elements.getVariationCategoryPageModalButton().click();
        },
        openPrimaryProductPageModal: function() {
            pageObject.elements.getPrimaryProductPageModalButton().click();
        },
        openVariationProductPageModal: function() {
            pageObject.elements.getVariationProductPageModalButton().click();
        },
        enterTextInNameField: function() {
            pageObject.elements.getNameField().sendKeys('update label');
        },
        clearNameField: function() {
            pageObject.elements.getNameField().clear();
        },
        clickSave: function() {
            pageObject.elements.getSaveButton().click();
        },
        clickCancel: function() {
            pageObject.elements.getCancelButton().click();
        },
        clickDisplayConditionsTab: function() {
            pageObject.elements.getDisplayConditionsTab().click();
        },
        openPrimaryPageSelectDropdown: function() {
            pageObject.elements.getPrimaryPageDropdownToggle().click();
        }
    };

    return pageObject;

})();
