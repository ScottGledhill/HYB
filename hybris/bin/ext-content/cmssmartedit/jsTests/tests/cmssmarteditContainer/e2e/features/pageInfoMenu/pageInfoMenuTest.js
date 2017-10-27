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
describe('Page Info Menu', function() {

    var pageInfo = e2e.pageObjects.PageInfo;
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;

    function goToStorefrontContentPage() {
        browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/pageInfoMenu/pageInfoMenuTest.html');
        browser.waitForWholeAppToBeReady();
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
    }

    function goToStorefrontProductPage() {
        browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/pageInfoMenu/pageInfoMenuTest.html');
        browser.waitForWholeAppToBeReady();
        browser.switchToIFrame();
        storefront.goToSecondPage();
        browser.waitForFrameToBeReady();
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
    }

    it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page type is displayed', function() {
        // GIVEN
        goToStorefrontContentPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageType().getText())
            .toBe('ContentPage', 'Expected page type to be "ContentPage"');
    });

    it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page template is displayed', function() {
        // GIVEN
        goToStorefrontContentPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageTemplate().getText())
            .toBe('AccountPageTemplate', 'Expected page template to be "AccountPageTemplate"');
    });

    it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page info is displayed', function() {
        // GIVEN
        goToStorefrontContentPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageNameField().getAttribute('value'))
            .toBe('Homepage', 'Expected page name to be "Homepage"');
        expect(pageInfo.elements.getPageLabelField().getAttribute('value'))
            .toBe('i-love-pandas', 'Expected page label to be "i-love-pandas"');
        expect(pageInfo.elements.getPageUidField().getAttribute('value'))
            .toBe('homepage', 'Expected page UID to be "homepage"');
        expect(pageInfo.elements.getPageTitleField().getAttribute('value'))
            .toBe('I love pandas', 'Expected page name to be "I love pandas"');
        expect(pageInfo.elements.getPageCreationTimeField().getAttribute('value'))
            .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
        expect(pageInfo.elements.getPageModifiedTimeField().getAttribute('value'))
            .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
    });

    it('GIVEN the user is in the page info menu WHEN the user clicks the Edit button THEN the page editor modal is opened', function() {
        // GIVEN
        goToStorefrontContentPage();
        pageInfo.actions.openPageInfoMenu();

        // WHEN
        pageInfo.actions.clickEditButton();

        // THEN
        expect(pageInfo.elements.getPageEditorModal().isPresent())
            .toBe(true, 'Expected Page Editor modal to be opened');
    });

    it('GIVEN the user is on a storefront product page WHEN the user opens the page info menu THEN the page info is displayed', function() {
        // GIVEN
        goToStorefrontProductPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageType().getText())
            .toBe('ProductPage', 'Expected page type to be "ProductPage"');
    });

    it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the page template is displayed', function() {
        // GIVEN
        goToStorefrontProductPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageTemplate().getText())
            .toBe('ProductPageTemplate', 'Expected page template to be "ProductPageTemplate"');
    });

    it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the page info is displayed', function() {
        // GIVEN
        goToStorefrontProductPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageNameField().getAttribute('value'))
            .toBe('Some Other Page', 'Expected page name to be "Some Other Page"');
        expect(pageInfo.elements.getPageUidField().getAttribute('value'))
            .toBe('secondpage', 'Expected page UID to be "secondpage"');
        expect(pageInfo.elements.getPageTitleField().getAttribute('value'))
            .toBe('I hate pandas', 'Expected page name to be "I hate pandas"');
        expect(pageInfo.elements.getPageCreationTimeField().getAttribute('value'))
            .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
        expect(pageInfo.elements.getPageModifiedTimeField().getAttribute('value'))
            .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
    });

    it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the label field is not present', function() {
        // GIVEN
        goToStorefrontProductPage();

        // WHEN
        pageInfo.actions.openPageInfoMenu();

        // THEN
        expect(pageInfo.elements.getPageLabelField().isPresent())
            .toBe(false, 'Expected label field not to be present');
    });

});
