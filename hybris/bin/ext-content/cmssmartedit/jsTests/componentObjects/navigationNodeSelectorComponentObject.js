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

module.exports = {

    waitForGenericEditorToShow: function() {
        return browser.waitForPresence(by.css("form[name=componentForm]"), "generic editor was not loaded");
    },
    assertOnBreadCrumb: function(namesArray) {
        return element.all(by.css('se-breadcrumb div > div > div.ySEBreadcrumbInfo')).then(function(crumbs) {
            return protractor.promise.all(crumbs.map(function(e) {
                return e.element(by.css('.yNodeLevel')).getText().then(function(nodeLevel) {
                    return e.element(by.css('.yNodeName')).getText().then(function(nodeName) {
                        return [nodeLevel, nodeName];
                    });
                });
            })).then(function(names) {
                return expect(names).toEqual(namesArray);
            });
        });
    },
    pressRemove: function() {
        return browser.click(by.cssContainingText('button p', 'Remove'));
    },
    assertThatNodeSelectionInviteIsVisible: function() {
        return browser.waitForPresence(by.css("*", "Select a node to associate to this component"));
    },
    pickNode: function(nodeLabel) {
        return browser.actions().mouseMove(element(by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/div[3]"))).perform().then(function() {
            return browser.click(by.xpath("//div/span[.='" + nodeLabel + "']/ancestor::div[2]/div[3]/button[.='Select']"));
        });
    }
};
