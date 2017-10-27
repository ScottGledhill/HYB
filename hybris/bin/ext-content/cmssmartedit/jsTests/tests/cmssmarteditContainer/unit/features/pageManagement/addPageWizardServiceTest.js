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
describe('addPageWizardService', function() {

    var addPageWizardService;
    var mocks;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('addPageServiceModule')
            .mock('modalWizard', 'open').andReturn('')
            .service('addPageWizardService');

        addPageWizardService = harness.service;
        mocks = harness.mocks;
    });

    describe('openAddPageWizard', function() {
        it('should delegate to the modal wizard', function() {
            addPageWizardService.openAddPageWizard();
            expect(mocks.modalWizard.open).toHaveBeenCalledWith({
                controller: 'addPageWizardController',
                controllerAs: 'addPageWizardCtl'
            });
        });
    });

});
