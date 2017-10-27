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
describe('YPopover directive and yHelp component', function() {

    var popover = require("../utils/components/PopoverComponentObject.js");

    beforeEach(function() {
        browser.get('smarteditcontainerJSTests/e2e/popoverAndHelp/popoverAndHelp.html');
    });

    it('Given a help with title and a template url, when I hover both title and body show', function() {

        expect(popover.getPopover().isPresent()).toBe(false);
        popover.hover("#helpWithTitle");
        expect(popover.getTitle()).toEqual("my translated title");
        expect(popover.getBody()).toEqual("<div>some inline template</div>");
    });

    it('Given a help with an inline body, when I hover body shows', function() {

        expect(popover.getPopover().isPresent()).toBe(false);
        popover.hover("#helpWithoutTitle");
        expect(popover.getTitleBox().isPresent()).toBe(false);
        expect(popover.getBody()).toEqual("<b>some HTML body</b>");
    });

    it('Given a shows-on-hover popover that has both title and inline body, when I hover both title and body show', function() {

        expect(popover.getPopover().isPresent()).toBe(false);
        popover.hover("#popoverWithTitleAndTop");
        expect(popover.getTitle()).toEqual("my translated title");
        expect(popover.getBody()).toEqual("<b>some HTML body</b>");
    });

    it('Given a show-on-click popover with template url, when I click body shows', function() {

        expect(popover.getPopover().isPresent()).toBe(false);
        popover.hover("#popoverWithoutTitleAndRight");
        expect(popover.getPopover().isPresent()).toBe(false);
        popover.click("#popoverWithoutTitleAndRight");
        expect(popover.getBody()).toEqual("<div>some inline template</div>");
    });



});
