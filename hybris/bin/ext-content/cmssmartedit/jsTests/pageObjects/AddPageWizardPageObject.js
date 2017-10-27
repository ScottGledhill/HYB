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
    var wizard = require('./WizardPageObject.js');
    var wizardStep = require('./WizardStepPageObject');
    var pageRestrictionsEditor = require('./PageRestrictionsEditorPageObject');

    var pageObject = {};

    pageObject.elements = {
        getAddNewPageButton: function() {
            return element(by.css('.ySEAdd-Page-button button'));
        },
        getContentPageTypeListItem: function() {
            return wizardStep.listItemByText('Content Page');
        },
        getProductPageTypeListItem: function() {
            return wizardStep.listItemByText('Product Page');
        },
        getCategoryPageTypeListItem: function() {
            return wizardStep.listItemByText('Category Page');
        },
        getPageTemplate1ListItem: function() {
            return wizardStep.listItemByText('Page Template 1');
        },
        getPageTemplate2ListItem: function() {
            return wizardStep.listItemByText('Page Template 2');
        },
        getConditionDropdownToggle: function() {
            return element(by.css('#page-condition-selector-id .ui-select-toggle'));
        },
        getPrimaryConditionOption: function() {
            return by.cssContainingText('.ui-select-choices-row', 'Primary');
        },
        isPrimaryConditionOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getPrimaryConditionOption());
        },
        isPrimaryConditionOptionNotDisplayed: function() {
            return browser.waitNotToBeDisplayed(this.getPrimaryConditionOption());
        },
        getVariationConditionOption: function() {
            return element(by.cssContainingText('.ui-select-choices-row', 'Variation'));
        },
        getPrimaryPageDropdownToggle: function() {
            return element(by.css('#page-condition-primary-selector-id .ui-select-toggle'));
        },
        getPage1PrimaryPageOption: function() {
            return by.cssContainingText('.ui-select-choices-row', 'page1TitleSuffix');
        },
        isPage1PrimaryPageOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getPage1PrimaryPageOption());
        },
        getCategoryPage1PrimaryPageOption: function() {
            return element(by.cssContainingText('.ui-select-choices-row', 'categoryPage1'));
        },
        getProductPage1PrimaryPageOption: function() {
            return by.cssContainingText('.ui-select-choices-row', 'productPage1');
        },
        isProductPage1PrimaryPageOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getProductPage1PrimaryPageOption());
        },
        getNameInput: function() {
            return element(by.css('#name-shortstring'));
        },
        getLabelInput: function() {
            return element(by.css('#label-shortstring'));
        },
        getTitleInput: function() {
            return element(by.css('[data-tab-id="en"] #title-shortstring'));
        },
        getIDInput: function() {
            return element(by.css('#uid-shortstring'));
        },
        getErrorMessage: function() {
            return element(by.css('.error-input'));
        },
        getNextButton: function() {
            return wizard.nextButton();
        },
        getPageRestrictionsEditor: function() {
            return pageRestrictionsEditor.elements.getPageRestrictionsEditor();
        },
        getCurrentStepText: function() {
            return wizard.getCurrentStepText();
        },
        getLabelInputText: function() {
            return pageObject.elements.getLabelInput().getAttribute('value').then(function(value) {
                return (value || '').trim();
            });
        },
        getLabelInputEnabled: function() {
            return pageObject.elements.getLabelInput().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        getDoneButton: function() {
            return wizard.submitButton();
        },
        isDoneButtonEnabled: function() {
            return pageObject.elements.getDoneButton().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        getNextButtonEnabled: function() {
            return pageObject.elements.getNextButton().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        getErrorMessageText: function() {
            return pageObject.elements.getErrorMessage().getText().then(function(text) {
                return (text || '').trim();
            });
        },
        isWindowOpen: function() {
            return wizard.isWindowsOpen();
        }
    };

    pageObject.actions = {
        selectContentPageType: function() {
            return browser.click(pageObject.elements.getContentPageTypeListItem());
        },
        selectProductPageType: function() {
            return browser.click(pageObject.elements.getProductPageTypeListItem());
        },
        selectCategoryPageType: function() {
            return browser.click(pageObject.elements.getCategoryPageTypeListItem());
        },
        selectPageTemplate1: function() {
            return browser.click(pageObject.elements.getPageTemplate1ListItem());
        },
        clickNext: function() {
            return wizard.moveNext();
        },
        clickSubmit: function() {
            return wizard.submit();
        },
        openAddPageWizard: function() {
            return browser.click(pageObject.elements.getAddNewPageButton());
        },
        openConditionDropdown: function() {
            return browser.click(pageObject.elements.getConditionDropdownToggle());
        },
        openPrimaryPageDropdown: function() {
            return browser.click(pageObject.elements.getPrimaryPageDropdownToggle());
        },
        selectPrimaryCondition: function() {
            return pageObject.actions.openConditionDropdown().then(function() {
                return browser.click(pageObject.elements.getPrimaryConditionOption());
            });

        },
        selectVariationCondition: function() {
            return pageObject.actions.openConditionDropdown().then(function() {
                return browser.click(pageObject.elements.getVariationConditionOption());
            });
        },
        selectPage1AsPrimaryPage: function() {
            return browser.click(pageObject.elements.getPrimaryPageDropdownToggle()).then(function() {
                return browser.click(pageObject.elements.getPage1PrimaryPageOption());
            });
        },
        selectCategoryPage1AsPrimaryPage: function() {
            return browser.click(pageObject.elements.getPrimaryPageDropdownToggle()).then(function() {
                return browser.click(pageObject.elements.getCategoryPage1PrimaryPageOption());
            });
        },
        enterSomeValidPageInfo: function() {
            wizardStep.enterFieldData('name-shortstring', 'someName');
            wizardStep.enterLocalizedFieldData('title-shortstring', 'en', 'someTitle');
            return wizardStep.enterFieldData('uid-shortstring', 'someId');
        },
        enterPageTitle: function(title) {
            return wizardStep.enterFieldData('title-shortstring', title);
        },
        enterPageName: function(name) {
            return wizardStep.enterFieldData('name-shortstring', name);
        },
        enterInvalidUid: function() {
            return wizardStep.enterFieldData('uid-shortstring', 'bla');
        },
        enterValidUid: function() {
            return wizardStep.enterFieldData('uid-shortstring', 'valid');
        },
        getErrorMessageText: function() {
            return this.elements.getErrorMessage().getText().then(function(text) {
                return (text || '').trim();
            });
        },
        getTemplateList: function() {
            return element.all(by.css('.page-type-step-template__item'));
        },
        sendSearchKeys: function(searchKey) {
            return browser.sendKeys(element(by.css('.ySEPage-list-search-input')), searchKey);
        },
        clearSearchFilter: function() {
            return browser.click(element(by.css('.glyphicon-remove-sign')));
        },
        addRestriction: function() {
            return pageRestrictionsEditor.actions.clickAddNew().then(function() {

            }).then(function() {
                return pageRestrictionsEditor.actions.openRestrictionTypesSelect();
            }).then(function() {
                return pageRestrictionsEditor.actions.selectFirstRestrictionTypeFromSelect();
            }).then(function() {
                return pageRestrictionsEditor.actions.enterSearchText('t');
            }).then(function() {
                return pageRestrictionsEditor.actions.clickSearchResultWithText('yet another');
            });
        }
    };

    return pageObject;
})();
