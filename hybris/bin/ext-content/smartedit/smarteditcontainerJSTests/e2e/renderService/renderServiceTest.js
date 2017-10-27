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
describe('Component and Slot Rendering', function() {

    var EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER = 'Some dirtied content';
    var EXPECTED_CONTENT_1_AFTER_RENDER = 'test component 1';
    var EXPECTED_CONTENT_2_AFTER_RENDER = 'test component 2';

    var decorators = require('../utils/components/Decorators.js');
    var page = require('../utils/components/Page.js');
    var perspectives = require('../utils/components/Perspectives.js');
    var storefront = require('../utils/components/Storefront.js');
    var toolbar = require('../utils/components/Toolbar.js');

    beforeEach(function() {
        page.getAndWaitForWholeApp('smarteditcontainerJSTests/e2e/renderService/index.html');
        perspectives.selectPerspective(perspectives.DEFAULT_PERSPECTIVES.ALL);
    });

    it('WHEN the user triggers a re-render from SmartEdit THEN the component is re-rendered with new content', function() {
        browser.switchToIFrame();
        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_1_ID));
        expect(storefront.component1().getText()).toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content to contain default content');
        expect(storefront.component1().getText()).not.toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 not to contain re-rendered content');

        browser.click(decorators.renderDecorator(storefront.COMPONENT_1_ID));
        browser.wait(EC.presenceOf(storefront.component1()), 5000, 'Timed out waiting for presence of component 1');
        expect(storefront.component1().getText()).not.toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content not to contain default content');
        expect(storefront.component1().getText()).toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 to contain re-rendered content');
    });

    it('WHEN the user triggers a re-render from SmartEdit container THEN the component is re-rendered with new content', function() {
        browser.switchToIFrame();

        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_1_ID));
        expect(storefront.component1().getText()).toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content to contain default content');
        expect(storefront.component1().getText()).not.toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 not to contain re-rendered content');

        browser.switchToParent();
        browser.click(toolbar.renderButton());
        browser.switchToIFrame();
        expect(storefront.component1().getText()).not.toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content not to contain default content');
        expect(storefront.component1().getText()).toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 to contain re-rendered content');
    });

    it('WHEN the user triggers a slot re-render from SmartEdit THEN the slot is re-rendered with content from the storefront', function() {
        browser.switchToIFrame();
        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_1_ID));
        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_2_ID));
        browser.click(decorators.renderSlotDecorator(storefront.TOP_HEADER_SLOT_ID));

        assertComponent1IsReRendered();
        assertComponent2IsReRendered();
    });

    it('WHEN the user triggers a slot re-render from SmartEdit Container THEN the slot is re-rendered with content from the storefront', function() {
        browser.switchToIFrame();
        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_1_ID));
        browser.click(decorators.dirtyContentDecorator(storefront.COMPONENT_2_ID));

        browser.switchToParent();
        browser.click(toolbar.renderSlotButton());
        browser.switchToIFrame();

        assertComponent1IsReRendered();
        assertComponent2IsReRendered();
    });

    function assertComponent1IsReRendered() {
        browser.wait(function() {
            return storefront.component1().getText().then(function(text) {
                return text;
            }, function() {
                return '';
            }).then(function(componentText) {
                return componentText.indexOf(EXPECTED_CONTENT_1_AFTER_RENDER) >= 0 &&
                    componentText.indexOf(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER) < 0;
            });
        }, 5000, 'Expected component to re-render');
    }

    function assertComponent2IsReRendered() {
        browser.wait(function() {
            return storefront.component2().getText().then(function(text) {
                return text;
            }, function() {
                return '';
            }).then(function(componentText) {
                return componentText.indexOf(EXPECTED_CONTENT_2_AFTER_RENDER) >= 0 &&
                    componentText.indexOf(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER) < 0;
            });
        }, 5000, 'Expected component to re-render');
    }

});
