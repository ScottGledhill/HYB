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
describe('Page Restrictions Editor', function() {

    var page = e2e.pageObjects.PageRestrictionsEditor;

    beforeEach(function() {
        browser.get(page.pageURI);
    });

    xit('GIVEN a list of existing restrictions for a page, ' +
        'AND the page restrictions editor is editable, ' +
        'WHEN a restriction is selected, ' +
        'THEN it will be displayed on the right panel',
        function() {
            page.actions.selectRestriction(1);
            page.assertions.assertRestrictionIsSelected(1);
        }
    );

    xit('GIVEN a list of at least 2 existing restrictions for a page, ' +
        'AND the page restrictions editor is editable, ' +
        'WHEN the first restriction is removed, ' +
        'THEN the second restriction should be the only remaining restriction in the list',
        function() {
            page.actions.removeRestriction(1);
            page.actions.selectRestriction(1);
            page.assertions.assertRestrictionDisplayedName("ANOTHER TIME B");
        }
    );

    xit('GIVEN a restriction is selected and displayed on the right panel, ' +
        'WHEN the "add new" button is pressed, ' +
        'THEN the displayed restrictions should be hidden and the restriction picker should be displayed',
        function() {
            page.actions.clickAddNew();
            page.assertions.assertAddNewNotDisplayed();
            page.assertions.assertNoSelectedRestrictionIsDisplayed();
            page.assertions.assertPickerIsDisplayed();
        }
    );

    xit('GIVEN the restriction picker is visible on the right panel, ' +
        'WHEN a restriction on the left is selected, ' +
        'THEN the selected restriciton should be displayed, hiding the restriction picker',
        function() {
            page.actions.clickAddNew();
            page.actions.selectRestriction(1);
            page.assertions.assertAddNewIsDisplayed();
        }
    );

    xit('GIVEN some search results in the restriction picker , ' +
        'WHEN a restriction is selected that is not in the list, ' +
        'THEN that restriction should be added to the list displayed on the left panel',
        function() {
            var restrictionName = 'yet another';
            page.actions.clickAddNew();
            page.actions.openRestrictionTypesSelect();
            page.actions.selectFirstRestrictionTypeFromSelect();
            page.actions.enterSearchText('t');
            page.actions.clickSearchResultWithText(restrictionName);
            page.assertions.assertRestrictionInListWithName(restrictionName);
        }
    );

});
