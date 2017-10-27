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
describe("GenericEditor form save", function() {
    beforeEach(function() {
        require("../commonFunctions.js");
        browser.driver.manage().timeouts().implicitlyWait(0);
        browser.get('smarteditcontainerJSTests/e2e/genericEditor/componentSave/genericEditorTest.html');
    });

    it("will display cancel button and not display submit button by default", function() {
        expect(element(by.id('cancel')).isPresent()).toBeFalsy();
        expect(element(by.id('submit')).isPresent()).toBeFalsy();

    });

    it("will display cancel and submit buttons when component attribute is modified", function() {
        element(by.name('headline')).sendKeys("I have changed");
        expect(element(by.id('cancel')).isPresent()).toBeTruthy();
        expect(element(by.id('submit')).isPresent()).toBeTruthy();
    });

    it("will display validation errors for headline when headline is modified and saved", function() {

        element(by.name('headline')).clear().sendKeys("I have changed to an invalid headline with two validation errors, % and lots of text");
        clickSubmit();
        var elements = getValidationErrorElements('headline');

        expect(elements.count()).toBe(2);
    });

    it("will display validation errors for link toggle when url link is modified and saved", function() {

        element(by.name('urlLink')).clear().sendKeys("/url-link-invalid");
        clickSubmit();
        var elements = getValidationErrorElements('linkToggle');
        expect(elements.count()).toBe(1);
    });

    it("will display a validation error for only 'en' and 'it' tab for content when content of 'en' and 'it' tab's are modified and saved", function() {

        switchToIframeForRichTextAndAddContent('#cke_1_contents iframe', "I have changed to an invalid content with one validation error").then(function() {
            browser.switchToParent().then(function() {

                selectLocalizedTab('it', 'content', false);
                switchToIframeForRichTextAndAddContent('#cke_3_contents iframe', "Ho cambiato ad un contenuto non valido con un errore di validazione").then(function() {
                    browser.switchToParent().then(function() {
                        clickSubmit().then(function() {
                            selectLocalizedTab('en', 'content', false).then(function() {
                                expect(getValidationErrorElementByLanguage('content', 'en').getText()).toEqual("This field is required and must to be between 1 and 255 characters long.");
                                selectLocalizedTab('it', 'content', false).then(function() {
                                    expect(getValidationErrorElementByLanguage('content', 'it').getText()).toEqual("This field is required and must to be between 1 and 255 characters long.");
                                    selectLocalizedTab('fr', 'content', false).then(function() {
                                        expect(getValidationErrorElementByLanguage('content', 'fr').isPresent()).toBe(false);
                                        selectLocalizedTab('pl', 'content', false).then(function() {
                                            expect(getValidationErrorElementByLanguage('content', 'pl').isPresent()).toBe(false);
                                            selectLocalizedTab('hi', 'content', false).then(function() {
                                                expect(getValidationErrorElementByLanguage('content', 'hi').isPresent()).toBe(false);
                                            });
                                        });
                                    });
                                });
                            });
                        });

                    });
                });

            });
        });
    });

    it("will remove validation errors when reset is clicked after contents are modified and saved", function() {
        element(by.name('headline')).clear().sendKeys("I have changed to an invalid headline with two validation errors, % and lots of text");
        clickSubmit();

        expect(getValidationErrorElements('headline').count()).toBe(2);

        browser.click(by.id('cancel'));

        expect(getValidationErrorElements('headline').count()).toBe(0);
    });

    it("will display 2 validation errors, then on second save will display 1 validation error for headline", function() {

        element(by.name('headline')).clear().sendKeys("I have changed to an invalid headline with two validation errors, % and lots of text");
        clickSubmit();

        expect(getValidationErrorElements('headline').count()).toBe(2);


        element(by.name('headline')).clear().sendKeys("I have changed to an invalid headline with one validation error, %");
        clickSubmit();

        expect(getValidationErrorElements('headline').count()).toBe(1);
    });

    it("will display no validation errors when submit is clicked and when API returns a field that does not exist", function() {

        element(by.name('headline')).clear().sendKeys("Checking unknown type");
        clickSubmit();

        expect(element(by.css("[id^='validation-error']")).isPresent()).toBeFalsy();

    });

});
