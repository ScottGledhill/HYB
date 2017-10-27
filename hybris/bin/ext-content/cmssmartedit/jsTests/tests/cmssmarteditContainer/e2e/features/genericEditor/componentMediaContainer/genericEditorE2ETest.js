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
describe("GenericEditor Media Upload Container", function() {

    var path;

    beforeEach(function() {
        require("../commonFunctions.js");
        path = require('path');
        browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/genericEditor/componentMediaContainer/genericEditorTest.html');

    });

    it('GIVEN a Media Container structure type is present in the Generic Editor ' +
        'WHEN I select an inflection point and select a file to upload ' +
        'THEN I expect to see the media upload form populated',
        function() {
            selectFileToUpload('more_bckg.png', '.widescreen .media-present input[type="file"]');

            expect(element(by.css('se-media-upload-form')).isPresent()).toBe(true);
            expect(element(by.css('input[name="code"]')).getAttribute('value')).toBe('more_bckg.png');
            expect(element(by.css('input[name="description"]')).getAttribute('value')).toBe('more_bckg.png');
            expect(element(by.css('input[name="altText"]')).getAttribute('value')).toBe('more_bckg.png');
        });

    it('GIVEN a Media Container structure type is present in the Generic Editor ' +
        'WHEN I select an inflection point with an existing image and upload  ' +
        'THEN I expect to see that inflection point updated with the newly uploaded image',
        function() {
            selectFileToUpload('more_bckg.png', '.widescreen .media-present input[type="file"]');
            clickUpload();

            expect(element(by.css('se-media-upload-form')).isPresent()).toBe(false);
            expect(element(by.css('.widescreen .media-present .thumbnail--image-preview')).getAttribute('data-ng-src')).toContain('more_bckg.png');
        });

    it('GIVEN a Media Container structure type is present in the Generic Editor ' +
        'WHEN I select an inflection point and attempt to upload an invalid file   ' +
        'THEN I expect to see the errors populated',
        function() {
            selectFileToUpload('invalid.doc', '.mobile .media-absent input[type="file"]');

            expect(element(by.css('se-media-upload-form')).isPresent()).toBe(false);
            expect(element(by.css(".field-errors")).getText()).toContain('upload.file.type.invalid');
        });

    it('GIVEN a Media Container structure type is present in the Generic Editor ' +
        'WHEN I select an inflection point with no image selected and upload  ' +
        'THEN I expect to see that inflection point updated with the newly uploaded image',
        function() {
            selectFileToUpload('more_bckg.png', '.mobile .media-absent input[type="file"]');
            clickUpload();

            expect(element(by.css('se-media-upload-form')).isPresent()).toBe(false);
            expect(element(by.css('.mobile .media-present .thumbnail--image-preview')).getAttribute('data-ng-src')).toContain('more_bckg.png');
        });

    function clickUpload() {
        return browser.click(by.css('.se-media-upload-btn__submit'));
    }

    function selectFileToUpload(fileName, selector) {
        browser.wait(EC.presenceOf(element(by.css(selector))), 5000, 'File input element not present after 5000ms');
        var absolutePath = path.resolve(__dirname, fileName);
        return element(by.css(selector)).sendKeys(absolutePath);
    }

});
