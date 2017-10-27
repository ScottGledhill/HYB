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
describe('displayConditionsEditor', function() {

    var element;
    var mocks;
    var scope;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsEditorModule')
            .mock('displayConditionsEditorModel', 'initModel')
            .mock('displayConditionsEditorModel', 'setAssociatedPrimaryPage')
            .component('<display-conditions-editor></display-conditions-editor>');
        element = harness.element;
        mocks = harness.mocks;
        scope = harness.scope;
    });

    it('should render one display-conditions-page-info element', function() {
        expect(element).toContainChildElement('display-conditions-page-info');
    });

    it('should display one display-conditions-page-variations element if the page is a primary', function() {
        mocks.displayConditionsEditorModel.isPrimary = true;
        scope.$digest();

        expect(element).toContainChildElement('display-conditions-page-variations');
    });

    it('should display one display-conditions-primary-page element if the page is a variation', function() {
        mocks.displayConditionsEditorModel.isPrimary = false;
        scope.$digest();

        expect(element).toContainChildElement('display-conditions-primary-page');
    });

});
