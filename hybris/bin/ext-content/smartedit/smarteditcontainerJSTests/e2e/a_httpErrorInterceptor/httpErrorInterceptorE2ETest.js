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
describe('HTTP Error Interceptor - ', function() {
    var alerts = require("../utils/components/Alerts.js");
    var configurations = require("../utils/components/Configurations.js");
    var login = require("../utils/components/Login.js");
    var page = require("../utils/components/Page.js");
    var perspectives = require("../utils/components/Perspectives.js");
    var storefront = require("../utils/components/Storefront.js");

    beforeEach(function() {
        page.getAndWaitForLogin('smarteditcontainerJSTests/e2e/a_httpErrorInterceptor/httpErrorInterceptorTest.html').then(function() {
            return login.loginAsAdmin().then(function() {
                return perspectives.selectPerspective(perspectives.DEFAULT_PERSPECTIVES.ALL).then(function() {
                    return page.setWaitForPresence(0);
                });
            });
        });
    });

    afterEach(function() {
        login.logoutUser();
    });

    it("GIVEN I'm in the SmartEdit container WHEN I trigger a GET AJAX request to an API AND I receive a failure response status that is not an HTML failure THEN I expect to see a timed alert with the message ", function() {
        configurations.openConfigurationEditor();
        configurations.setConfigurationValue(0, '\"new\"');
        configurations.clickSave();

        expect(alerts.alertMsg()).toBeDisplayed();
        expect(alerts.alertMsg().getText()).toBe('Your request could not be processed! Please try again later!');

        alerts.closeAlert();
    });

    it("GIVEN I'm in the SmartEdit application WHEN I trigger a GET AJAX request to an API AND I receive a failure response status that is not an HTML failure THEN I expect to see a timed alert with the message", function() {
        browser.switchToIFrame();
        browser.click(storefront.componentButton());
        browser.switchToParent();

        expect(alerts.alertMsg()).toBeDisplayed();
        expect(alerts.alertMsg().getText()).toBe('Your request could not be processed! Please try again later!');

        alerts.closeAlert();
    });

    it("GIVEN I'm in the SmartEdit application WHEN I trigger a GET AJAX request to an API AND I receive an HTML failure THEN a timed alert won't be displayed", function() {
        browser.switchToIFrame();
        browser.click(storefront.secondComponentButton(), "could not find secondary button");
        browser.switchToParent();

        expect(element.all(by.css('#alertMsg')).count()).toBe(0);
    });

});
