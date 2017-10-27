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
describe("GenericEditor With External and URL Link", function() {

    beforeEach(function() {
        require("../commonFunctions.js");
        browser.get('smarteditcontainerJSTests/e2e/genericEditor/componentWithExternalAndUrlLink/genericEditorTest.html');
    });

    it("will load 'Component ID' as default content for ID attribute", function() {

        var val = element(by.name('id')).getAttribute('value');
        expect(val).toEqual('Component ID');

    });

    it("will set 'The Headline' as default content for Headline attribute", function() {

        var val = element(by.name('headline')).getAttribute('value');
        expect(val).toEqual('The Headline');

    });

    it("will set different content for each language as default content for 'Content' attribute", function() {

        switchToIframeForRichTextAndValidateContent('#cke_1_contents iframe', 'the content to edit');
        browser.switchToParent();

        selectLocalizedTab('fr', 'content', false);
        switchToIframeForRichTextAndValidateContent('#cke_2_contents iframe', 'le contenu a editer');
        browser.switchToParent();

        selectLocalizedTab('it', 'content', false);
        switchToIframeForRichTextAndValidateContent('#cke_3_contents iframe', 'il contenuto da modificare');
        browser.switchToParent();

        selectLocalizedTab('pl', 'content', false);
        switchToIframeForRichTextAndValidateContent('#cke_4_contents iframe', 'tresc edytowac');
        browser.switchToParent();

        selectLocalizedTab('hi', 'content', false);
        switchToIframeForRichTextAndValidateContent('#cke_5_contents iframe', 'Sampaadit karanee kee liee saamagree');
        browser.switchToParent();

    });

    it("will set 'vertical' as default content for Orientation attribute", function() {

        expect(element(by.xpath("//span[@id='enum-orientation']")).getText()).toEqual('Vertical');

    });


    it("will set active checkbox to selected", function() {

        expect(element(by.name('active')).isSelected()).toBeTruthy();

    });

    it("will set enabled checkbox not un-selected", function() {

        expect(element(by.name('enabled')).isSelected()).toBeFalsy();

    });

    it("will set 'Existing Page' radio button to selected and 'urlLink' value to 'myPageUrl'", function() {

        expect(element(by.id("external")).isSelected()).toBeFalsy();
        expect(element(by.id("internal")).isSelected()).toBeTruthy();
        expect(element(by.name('urlLink')).getAttribute('value')).toEqual('myPageUrl');

    });

    it("given both urlLink and external attributes are present, when the component is rendered then external is a radio selection with false selected and urllink is on the same line as external", function() {

        expect(by.css("[id='external-checkbox']")).toBeAbsent();

        expect(element.all(by.css("[name='external']")).count()).toBe(2);

        expect(element(by.css("[id='external']")).isPresent()).toBe(true);
        expect(element(by.css("[id='urlLink']")).isPresent()).toBe(true);

        expect(element(by.css("[id='external']")).isSelected()).toBe(false);
        expect(element(by.css("[id='internal']")).isSelected()).toBe(true);

        expect(element(by.css("[id='urlLink']")).isPresent()).toBe(true);
        expect(by.css("[id='urlLink-shortstring']")).toBeAbsent();

    });

    it('GIVEN I am editing a component WHEN I enter dangerous characters in a ShortString or LongString CMS component type THEN the genericEditor will escape those dangerous characters', function() {

        element(by.name('id')).clear().sendKeys("<script>var x = new XMLHttpRequest()</script>Foo Bar");
        element(by.name('headline')).clear().sendKeys("<button onclick='alert(1);'>Will trigger XSS</button>");

        browser.sleep('500');
        browser.click(by.id('submit'));

        expect(element(by.name('id')).getAttribute('value')).toBe('&lt;script&gt;var x = new XMLHttpRequest()&lt;/script&gt;Foo Bar');
        expect(element(by.name('headline')).getAttribute('value')).toBe('&lt;button onclick=&apos;alert(1);&apos;&gt;Will trigger XSS&lt;/button&gt;');

    });

});
