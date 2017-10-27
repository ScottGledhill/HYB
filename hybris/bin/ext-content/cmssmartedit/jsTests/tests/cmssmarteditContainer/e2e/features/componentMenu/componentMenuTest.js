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
describe('Component Menu', function() {

    var perspective = e2e.componentObjects.modeSelector;
    var componentMenu = e2e.pageObjects.componentMenu;
    var defaultPage = new e2e.pageObjects.Default();

    beforeEach(function(done) {
        defaultPage.getAndWaitForWholeApp('jsTests/tests/cmssmarteditContainer/e2e/features/componentMenu/componentMenuTest.html').then(function() {
            done();
        });
    });

    it('WILL display the toolbar menu item', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        expect(componentMenu.menuIsVisible()).toBe(true, "The add component menu toolbar did not open properly");
    });

    it('WILL display the list of component types in first tab', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        expect(componentMenu.getComponentTypes()).toEqual(['PARAGRAPH COMPONENT', 'FOOTER COMPONENT', 'SIMPLE BANNER COMPONENT']);
    });

    it('WILL display the paged list of existing (custom) component names/types in second tab', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        componentMenu.switchTab(2);
        componentMenu.getCustomComponentNames().then(function(components) {
            expect(components.length).toBe(10);
        });
        expect(componentMenu.getCustomComponentNames()).toEqual(['Component1', 'Component2', 'Component3', 'Component4', 'Component5', 'Component6', 'Component7', 'Component8', 'Component9', 'Component10']);
    });

    it('WILL display more results as we scroll the paged list of existing (custom) component names/types in second tab', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        componentMenu.switchTab(2);
        componentMenu.getCustomComponentNames().then(function(components) {
            expect(components.length).toBe(10);

            browser.scrollToBottom(componentMenu.getCustomComponentsScrollElement()).then(function() {
                browser.waitUntil(function() {
                    return componentMenu.getCustomComponentNames().then(function(components) {
                        return components.length == 20;
                    });
                }, "could not load more items");

                browser.scrollToBottom(componentMenu.getCustomComponentsScrollElement()).then(function() {
                    browser.waitUntil(function() {
                        return componentMenu.getCustomComponentNames().then(function(components) {
                            return components.length == 23;
                        });
                    }, "could not load more items");

                });
            });
        });

    });

    it('WILL filter the list of component types in first tab when a search key is entered ', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        componentMenu.searchComponents('para');
        expect(componentMenu.getComponentTypes()).toEqual(['PARAGRAPH COMPONENT']);
    });

    it('WILL filter the list of existing (custom) component by name and create a paged list in second tab when a search key is entered ', function() {
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
        componentMenu.openMenu();
        componentMenu.switchTab(2);
        componentMenu.searchComponents('component2');
        componentMenu.switchTab(1);
        componentMenu.switchTab(2);
        componentMenu.getCustomComponentNames().then(function(components) {
            expect(components.length).toBe(5);
            expect(componentMenu.getCustomComponentNames()).toEqual(['Component2', 'Component20', 'Component21', 'Component22', 'Component23']);
        });
    });


});
