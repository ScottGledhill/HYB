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
describe("GenericEditor Dropdown - ", function() {

    var dropdown = require("./dropdownObject.js");

    beforeEach(function() {
        browser.get('smarteditcontainerJSTests/e2e/genericEditor/componentWithDropdown/genericEditorTest.html');
    });

    it("GIVEN a structure API and a content API THEN all dropdowns will be populated with respective values (if found in their list) else will reset", function() {

        //has data in content API and match from list
        dropdown.getDropdownValues(['dropdownA', 'dropdownB', 'dropdownC', 'dropdownD']).then(function(values) {
            expect(values).toEqual(['OptionA2', 'OptionB7-A1-A2', 'OptionC4-A2', 'OptionD2-sample-element']);
        });

        //has data in content API but does not values match in the list
        dropdown.getDropdownValues(['dropdownE']).then(function(values) {
            expect(values).toEqual(['Select an Option']);
        });

    });

    it("GIVEN a set of cascading dropdowns WHEN I change value of a dropdown THEN all the children dropdowns are reset while the independent ones are untouched", function() {

        dropdown.waitForDropdownsToLoad().then(function() {
            dropdown.clickDropdown('dropdownA').then(function() {
                dropdown.selectOption('dropdownA', 'OptionA1').then(function() {
                    dropdown.getDropdownValues(['dropdownA', 'dropdownB', 'dropdownC', 'dropdownD', 'dropdownE']).then(function(values) {
                        expect(values).toEqual(['OptionA1', 'OptionB7-A1-A2', 'Select an Option', 'OptionD2-sample-element', 'Select an Option']);
                    });
                });
            });
        });

    });

    it("GIVEN a set of cascading dropdowns WHEN I update value of the parent dropdown THEN all the children dropdowns should update their options", function() {

        //change dropdown A
        dropdown.waitForDropdownsToLoad().then(function() {
            dropdown.clickDropdown('dropdownA');
            dropdown.selectOption('dropdownA', 'OptionA1');
            dropdown.clickDropdown('dropdownB');
            dropdown.assertListOfOptions('dropdownB', ['OptionB1-A1', 'OptionB2-A1', 'OptionB7-A1-A2']);

            dropdown.clickDropdown('dropdownB');
            dropdown.clickDropdown('dropdownC');
            dropdown.assertListOfOptions('dropdownC', ['OptionC1-A1', 'OptionC2-A1']);

            dropdown.clickDropdown('dropdownC');
            dropdown.clickDropdown('dropdownE');
            dropdown.assertListOfOptions('dropdownE', ['OptionE7-B7']);

            dropdown.clickDropdown('dropdownE');
            //change dropdown B
            dropdown.clickDropdown('dropdownB');
            dropdown.selectOption('dropdownB', 'OptionB1-A1');
            dropdown.clickDropdown('dropdownE');
            dropdown.assertListOfOptions('dropdownE', ['OptionE1-B1']);
        });

    });

    it("GIVEN a dropdown WHEN I start typing in the dropdown search THEN the options should be filtered to match the searched key", function() {

        dropdown.waitForDropdownsToLoad().then(function() {
            dropdown.clickDropdown('dropdownD');
            dropdown.assertListOfOptions('dropdownD', ['OptionD1-sample', 'OptionD2-sample-element', 'OptionD3-element']);
            dropdown.searchAndAssertInDropdown('dropdownD', 'sample', ['OptionD1-sample', 'OptionD2-sample-element']);
            dropdown.searchAndAssertInDropdown('dropdownD', '', ['OptionD1-sample', 'OptionD2-sample-element', 'OptionD3-element']);
        });


    });


});
