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
describe('Experience Selector - ', function() {

    var ELECTRONICS_SITE = {
        CATALOGS: {
            ONLINE: "Electronics Content Catalog - Online",
            STAGED: "Electronics Content Catalog - Staged"
        },
        LANGUAGES: {
            ENGLISH: "English",
            POLISH: "Polish",
            ITALIAN: "Italian"
        }
    };

    var APPAREL_SITE = {
        CATALOGS: {
            ONLINE: "Apparel UK Content Catalog - Online",
            STAGED: "Apparel UK Content Catalog - Staged"
        },
        LANGUAGES: {
            ENGLISH: "English",
            FRENCH: "French"
        }
    };

    var experienceSelector, alerts, page;

    beforeEach(function() {
        experienceSelector = require('./../utils/components/ExperienceSelector.js');
        alerts = require("../utils/components/Alerts.js");
        page = require("../utils/components/Page.js");
        page.getAndWaitForWholeApp('smarteditcontainerJSTests/e2e/experienceSelector/experienceSelectorTest.html');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector button THEN I expect to see the Experience Selector", function() {
        //WHEN
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.label().getText()).toBe('CATALOG');
        expect(experienceSelector.dateAndTime.label().getText()).toBe('DATE/TIME');
        expect(experienceSelector.language.label().getText()).toBe('LANGUAGE');

        expect(experienceSelector.buttons.ok().getText()).toBe('APPLY');
        expect(experienceSelector.buttons.cancel().getText()).toBe('CANCEL');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector THEN I expect to see the currently selected experience in the Experience Selector", function() {
        //WHEN
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Online');
        expect(experienceSelector.language.selectedOption().getText()).toBe('English');
        expect(experienceSelector.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
    });

    it("GIVEN I'm in the experience selector WHEN I do not choose a catalog from the catalog dropdown THEN I expect to see a disabled Apply button", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.buttons.ok().getAttribute('disabled')).toBe('true');
    });

    it("GIVEN I'm in the experience selector WHEN I click on the catalog selector dropdown THEN I expect to see all catalog/catalog versions combinations", function() {

        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.catalog.selectDropdown();

        // THEN
        experienceSelector.catalog.assertNumberOfOptions(4);
        experienceSelector.catalog.assertOptionText(2, 'Apparel UK Content Catalog - Online');
        experienceSelector.catalog.assertOptionText(3, 'Apparel UK Content Catalog - Staged');
        experienceSelector.catalog.assertOptionText(4, 'Electronics Content Catalog - Online');
        experienceSelector.catalog.assertOptionText(5, 'Electronics Content Catalog - Staged');
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog THEN I expect to see the apply button enabled", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        //THEN
        expect(experienceSelector.buttons.ok().getAttribute('disabled')).toBeFalsy();
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog belonging to the electronics site THEN I expect to see the language dropdown populated with the electronics sites languages", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);
        experienceSelector.language.selectDropdown();

        //THEN
        experienceSelector.language.assertNumberOfOptions(3);
        experienceSelector.language.assertOptionText(2, 'English');
        experienceSelector.language.assertOptionText(3, 'Polish');
        experienceSelector.language.assertOptionText(4, 'Italian');
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog belonging to the apparel site THEN I expect to see the language dropdown populated with the apprel sites languages", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.language.selectDropdown();

        experienceSelector.language.assertNumberOfOptions(2);
        experienceSelector.language.assertOptionText(2, 'English');
        experienceSelector.language.assertOptionText(3, 'French');
    });

    it("GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the smartEdit application with the new preview ticket", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(APPAREL_SITE.LANGUAGES.ENGLISH);

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        //THEN
        var expectedUriPostfix = '/smarteditcontainerJSTests/e2e/dummystorefront.html?cmsTicketId=validTicketId';
        expect(experienceSelector.page.iframe().getAttribute('src')).toContain(expectedUriPostfix);
    });

    // TODO this should be part of a unit test
    it("GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service fails due to an invalid catalog and catalog version THEN I expect to see an error displayed", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);
        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        //THEN
        expect(alerts.alertMsg().isDisplayed()).toBe(true);
    });

    it("GIVEN I'm in the experience selector AND I click on the apply button to update the experience with the one I chose THEN it should update the experience widget text", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        //THEN
        var VALID_EXPERIENCE_WIDGET_TEXT = 'Apparel UK Content Catalog - Online | French';
        expect(experienceSelector.widget.text()).toBe(VALID_EXPERIENCE_WIDGET_TEXT);
    });

    it("GIVEN I'm in the experience selector AND I select a date and time using the date-time picker WHEN I click the apply button THEN it should update the experience widget text", function() {

        // GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        browser.click(experienceSelector.dateAndTime.button());
        experienceSelector.actions.selectExpectedDate();
        browser.click(experienceSelector.dateAndTime.button());

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);


        // WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        // THEN
        var VALID_EXPERIENCE_WIDGET_TEXT = 'Apparel UK Content Catalog - Online | French | 1/1/16 1:00 PM';
        expect(experienceSelector.widget.text()).toBe(VALID_EXPERIENCE_WIDGET_TEXT);
    });

    it("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit container THEN the experience selector is closed and reset", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.actions.clickInApplication();

        //THEN
        expect(experienceSelector.catalog.label().isDisplayed()).toBe(false);
    });

    it("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit application THEN the experience selector is closed and reset", function() {
        //GIVEN
        experienceSelector.widget.clickButton();

        //WHEN
        experienceSelector.actions.clickInIframe();

        //THEN
        expect(experienceSelector.catalog.label().isDisplayed()).toBe(false);
    });

    it("GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        browser.click(experienceSelector.dateAndTime.field());
        experienceSelector.dateAndTime.field().sendKeys("1/1/16 12:00 AM");

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Staged');
        expect(experienceSelector.language.selectedOption().getText()).toBe('Italian');
        expect(experienceSelector.dateAndTime.field().getAttribute('value')).toBe('1/1/16 12:00 AM');
    });

    it("GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience", function() {

        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Online');
        expect(experienceSelector.language.selectedOption().getText()).toBe('Polish');
        expect(experienceSelector.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
    });

    it("GIVEN I'm in the experience selector AND I've changed the values in the editor fields WHEN I click cancel AND I re-open the experience selector THEN I expect to see the currently selected experience", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        browser.click(experienceSelector.dateAndTime.field());
        experienceSelector.dateAndTime.field().sendKeys("1/1/16 12:00 AM");

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        //WHEN
        experienceSelector.actions.clickInApplication();
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Online');
        expect(experienceSelector.language.selectedOption().getText()).toBe('English');
        expect(experienceSelector.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
    });

    it("GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the payload to match the API's expected payload", function() {

        // GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        // WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        // THEN
        var EXPECTED_URI_SUFFIX = '/smarteditcontainerJSTests/e2e/dummystorefront.html?cmsTicketId=validTicketId1';
        expect(experienceSelector.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);
    });

    it("GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the payload to match the API's expected payload", function() {

        // GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        browser.click(experienceSelector.dateAndTime.field());
        experienceSelector.dateAndTime.field().sendKeys("1/1/16 1:00 PM");

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        // WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();

        // THEN
        var EXPECTED_URI_SUFFIX = '/smarteditcontainerJSTests/e2e/dummystorefront.html?cmsTicketId=validTicketId1';
        expect(experienceSelector.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);
    });

    it("GIVEN that I have deep linked and I have selected a new experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect to reload the page to which I have deep linked without a preview ticket", function() {
        browser.linkAndBackToParent(by.id("deepLink")).then(function() {

            // GIVEN
            experienceSelector.widget.clickButton();
            experienceSelector.catalog.selectDropdown();
            experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

            browser.click(experienceSelector.dateAndTime.field());
            experienceSelector.dateAndTime.field().sendKeys("1/1/16 1:00 PM");

            experienceSelector.language.selectDropdown();
            experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

            // WHEN
            browser.click(experienceSelector.buttons.ok());
            browser.waitForWholeAppToBeReady();

            // THEN
            var EXPECTED_URI_SUFFIX = '/smarteditcontainerJSTests/e2e/dummystorefrontSecondPage.html';
            expect(experienceSelector.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);

        });

    });


    it('GIVEN that I have deep linked WHEN I select a new experience and the current page does not exist for this new experience THEN I will be redirected to the landing page of the new experience', function() {

        // GIVEN
        browser.linkAndBackToParent(by.id("deepLinkFailsWhenNewExperience")).then(function() {

            // WHEN
            experienceSelector.widget.clickButton();
            experienceSelector.catalog.selectDropdown();
            experienceSelector.catalog.selectOption(APPAREL_SITE.CATALOGS.STAGED);
            experienceSelector.language.selectDropdown();
            experienceSelector.language.selectOption(APPAREL_SITE.LANGUAGES.ENGLISH);
            browser.click(experienceSelector.buttons.ok());
            browser.waitForWholeAppToBeReady();

            var APPAREL_UK_STAGED_HOMEPAGE = 'dummystorefront.html?cmsTicketId=apparel-ukContentCatalogStagedValidTicket';
            expect(experienceSelector.page.iframe().getAttribute('src')).toContain(APPAREL_UK_STAGED_HOMEPAGE);

        });

    });

    it("GIVEN I have selected an experience by setting the new field WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the new field set", function() {
        //GIVEN
        experienceSelector.widget.clickButton();
        experienceSelector.catalog.selectDropdown();
        experienceSelector.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        browser.click(experienceSelector.dateAndTime.field());
        experienceSelector.dateAndTime.field().sendKeys("1/1/16 12:00 AM");

        experienceSelector.language.selectDropdown();
        experienceSelector.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        experienceSelector.otherFields.field('newField').sendKeys('New Data For Preview');

        //WHEN
        browser.click(experienceSelector.buttons.ok());
        browser.waitForWholeAppToBeReady();
        experienceSelector.widget.clickButton();

        //THEN
        expect(experienceSelector.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Staged');
        expect(experienceSelector.language.selectedOption().getText()).toBe('Italian');
        expect(experienceSelector.dateAndTime.field().getAttribute('value')).toBe('1/1/16 12:00 AM');
        expect(experienceSelector.otherFields.field('newField').getAttribute('value')).toBe('New Data For Preview');
    });


});
