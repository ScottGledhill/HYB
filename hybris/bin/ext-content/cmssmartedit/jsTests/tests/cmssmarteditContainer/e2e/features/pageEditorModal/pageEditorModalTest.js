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
describe('Page Editor Modal', function() {

    var pageEditorModal = e2e.pageObjects.PageEditorModal;

    beforeEach(function() {
        pageEditorModal.actions.openPageEditorModalTestPage();
    });

    describe('General', function() {
        it('GIVEN the Page Editor Modal is open THEN the save button is disabled by default', function() {
            pageEditorModal.actions.openSuccessfulEditPageModal();

            expect(pageEditorModal.elements.getSaveButton().isEnabled()).toBe(false);
        });
    });

    describe('Information Tab', function() {
        it('GIVEN the information tab is selected WHEN I change the page label THEN the save button is enabled', function() {
            pageEditorModal.actions.openSuccessfulEditPageModal();
            pageEditorModal.actions.enterTextInNameField();

            expect(pageEditorModal.elements.getSaveButton().isEnabled()).toBe(true);
        });

        it('GIVEN the information tab is selected THEN the page UID field should be disabled', function() {
            pageEditorModal.actions.openSuccessfulEditPageModal();

            expect(pageEditorModal.elements.getUidField().isEnabled()).toBe(false);
        });

        it('GIVEN the information tab is selected WHEN I click on cancel button THEN it should close the modal', function() {
            pageEditorModal.actions.openSuccessfulEditPageModal();
            pageEditorModal.actions.clickCancel();

            expect(pageEditorModal.elements.getModal().isPresent()).toBe(false);
        });

        it('GIVEN the information tab is selected WHEN I clear a required field THEN an error will be highlighted in the faulty tab', function() {
            pageEditorModal.actions.openFailEditPageModal();
            pageEditorModal.actions.clearNameField();
            pageEditorModal.actions.clickSave();

            expect(pageEditorModal.elements.getBasicInfoTabHeaderClass()).toMatch('sm-tab-error');
        });

        it('GIVEN the information tab is selected WHEN I clear a required field THEN the validation error will be visible', function() {
            pageEditorModal.actions.openFailEditPageModal();
            pageEditorModal.actions.clearNameField();
            pageEditorModal.actions.clickSave();

            expect(pageEditorModal.elements.getNameErrorsText()).toBe('This field is required.');
        });

        it('GIVEN a primary content page information tab is open THEN the label is present and editable', function() {
            pageEditorModal.actions.openPrimaryContentPageModal();

            expect(pageEditorModal.elements.getLabelField().isPresent())
                .toBe(true, 'Expected label field to be present for primary content page.');
            expect(pageEditorModal.elements.getLabelField().isEnabled())
                .toBe(true, 'Expected label field to be editable for primary content page.');
        });

        it('GIVEN a variation content page information tab is open THEN the label is present and read-only', function() {
            pageEditorModal.actions.openVariationContentPageModal();

            expect(pageEditorModal.elements.getLabelField().isPresent())
                .toBe(true, 'Expected label field to be present for variation content page.');
            expect(pageEditorModal.elements.getLabelField().isEnabled())
                .toBe(false, 'Expected label field not to be editable for variation content page.');
        });

        it('GIVEN a category or product page information tab is open THEN the label is not present', function() {
            pageEditorModal.actions.openPrimaryCategoryPageModal();

            expect(pageEditorModal.elements.getLabelField().isPresent())
                .toBe(false, 'Expected label field not to be present for primary category page.');
        });
    });

    describe('Display Condition Tab', function() {
        describe('Primary Content Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openPrimaryContentPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Primary Content Page', 'Expected page name to be "My Little Primary Content Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('ContentPage', 'Expected page type to be "ContentPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Primary', 'Expected page display condition to be "Primary"');
            });

            it('should display the page variations deriving from this primary with their names, creation dates, and number of restrictions', function() {
                expect(pageEditorModal.elements.getPageNameOfFirstPageVariationRow())
                    .toBe('Some Variation Content Page', 'Expected first variation page name to be "Some Variation Content Page"');
                expect(pageEditorModal.elements.getCreationDateOfFirstPageVariationRow())
                    .toMatch(/\d*\/\d*\/\d* \d*:\d* (AM|PM)/, 'Expected first variation page creation date to be a short date');
                expect(pageEditorModal.elements.getRestrictionsOfFirstPageVariationRow())
                    .toBe('2', 'Expected first variation page number of restrictions to be "2"');
                expect(pageEditorModal.elements.getPageNameOfSecondPageVariationRow())
                    .toBe('Some Other Variation Content Page', 'Expected second variation page name to be "Some Variation Content Page"');
                expect(pageEditorModal.elements.getCreationDateOfSecondPageVariationRow())
                    .toMatch(/\d*\/\d*\/\d* \d*:\d* (AM|PM)/, 'Expected second variation page creation date to be a short date');
                expect(pageEditorModal.elements.getRestrictionsOfSecondPageVariationRow())
                    .toBe('1', 'Expected second variation page number of restrictions to be "2"');
            });
        });

        describe('Variation Content Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openVariationContentPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Variation Content Page', 'Expected page name to be "My Little Variation Content Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('ContentPage', 'Expected page type to be "ContentPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Variation', 'Expected page display condition to be "Variation"');
            });

            it('should display the associated primary page in a dropdown', function() {
                expect(pageEditorModal.elements.getSelectedOptionText())
                    .toBe('My Little Primary Content Page', 'Expected primary page selection to be "My Little Primary Content Page"');
            });

            it('should populate the dropdown with all primary pages available', function() {
                pageEditorModal.actions.openPrimaryPageSelectDropdown();

                expect(pageEditorModal.elements.getFirstAvailableOptionText())
                    .toBe('My Little Primary Content Page', 'Expected options to include "My Little Primary Content Page"');
                expect(pageEditorModal.elements.getSecondAvailableOptionText())
                    .toBe('Some Other Primary Content Page', 'Expected options to include "Some Other Primary Content Page"');
                expect(pageEditorModal.elements.getThirdAvailableOptionText())
                    .toBe('Another Primary Content Page', 'Expected options to include "Another Primary Content Page"');
            });
        });

        describe('Primary Category Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openPrimaryCategoryPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Primary Category Page', 'Expected page name to be "My Little Primary Category Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('CategoryPage', 'Expected page type to be "CategoryPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Primary', 'Expected page display condition to be "Primary"');
            });

            it('should display a message indicating that no variation pages exist for this primary', function() {
                expect(pageEditorModal.elements.getNoAssociatedVariationPagesText())
                    .toBe('There are no page variations for this primary.', 'Expected a message indicating no variation pages exist for this primary');
            });
        });

        describe('Variation Category Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openVariationCategoryPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Variation Category Page', 'Expected page name to be "My Little Variation Category Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('CategoryPage', 'Expected page type to be "CategoryPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Variation', 'Expected page display condition to be "Variation"');
            });

            it('should show the associated primary page name', function() {
                expect(pageEditorModal.elements.getAssociatedPrimaryPageText())
                    .toBe('My Little Primary Category Page', 'Expected associated primary page to be "My Little Primary Category Page"');
            });
        });

        describe('Primary Product Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openPrimaryProductPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Primary Product Page', 'Expected page name to be "My Little Primary Product Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('ProductPage', 'Expected page type to be "ProductPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Primary', 'Expected page display condition to be "Primary"');
            });

            it('should display the page variations deriving from this primary with their names, creation dates, and number of restrictions', function() {
                expect(pageEditorModal.elements.getPageNameOfFirstPageVariationRow())
                    .toBe('Some Variation Product Page', 'Expected first variation page name to be "Some Variation Product Page"');
                expect(pageEditorModal.elements.getCreationDateOfFirstPageVariationRow())
                    .toMatch(/\d*\/\d*\/\d* \d*:\d* (AM|PM)/, 'Expected first variation page creation date to be a short date');
                expect(pageEditorModal.elements.getRestrictionsOfFirstPageVariationRow())
                    .toBe('2', 'Expected first variation page number of restrictions to be "2"');
                expect(pageEditorModal.elements.getPageNameOfSecondPageVariationRow())
                    .toBe('Some Other Variation Product Page', 'Expected second variation page name to be "Some Variation Product Page"');
                expect(pageEditorModal.elements.getCreationDateOfSecondPageVariationRow())
                    .toMatch(/\d*\/\d*\/\d* \d*:\d* (AM|PM)/, 'Expected second variation page creation date to be a short date');
                expect(pageEditorModal.elements.getRestrictionsOfSecondPageVariationRow())
                    .toBe('1', 'Expected second variation page number of restrictions to be "2"');
            });
        });

        describe('Variation Product Page', function() {
            beforeEach(function() {
                pageEditorModal.actions.openVariationProductPageModal();
                pageEditorModal.actions.clickDisplayConditionsTab();
            });

            it('should display the page name, type, and display condition', function() {
                expect(pageEditorModal.elements.getDisplayConditionsPageNameText())
                    .toBe('My Little Variation Product Page', 'Expected page name to be "My Little Variation Product Page"');
                expect(pageEditorModal.elements.getDisplayConditionsPageTypeText())
                    .toBe('ProductPage', 'Expected page type to be "ProductPage"');
                expect(pageEditorModal.elements.getPageDisplayConditionText())
                    .toBe('Variation', 'Expected page display condition to be "Variation"');
            });

            it('should show the associated primary page name', function() {
                expect(pageEditorModal.elements.getAssociatedPrimaryPageText())
                    .toBe('My Little Primary Product Page', 'Expected associated primary page to be "My Little Primary Product Page"');
            });
        });
    });
});
