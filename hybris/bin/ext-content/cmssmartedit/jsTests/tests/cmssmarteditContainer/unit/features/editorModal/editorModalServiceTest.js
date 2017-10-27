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
describe('editorModalService', function() {

    var editorModalService, mocks;

    beforeEach(function() {

        var harness = AngularUnitTestHelper.prepareModule('editorModalServiceModule')
            .mock('genericEditorModalService', 'open').andReturnResolvedPromise("somedata1")
            .mock('componentEditorService', 'init')
            .mock('gatewayProxy', 'initForService')
            .mock('renderService', 'renderComponent').andReturnResolvedPromise("somedata2")
            .mock('typeStructureRestService', 'getStructureByType').andCallFake(function(type) {
                if (type === 'WithStructure') {
                    return harness.injected.$q.when({
                        attributes: [{
                            qualifier: 'something'
                        }]
                    });
                } else if (type === 'WithoutStructure') {
                    return harness.injected.$q.when([]);
                }
            })
            .service('editorModalService');
        editorModalService = harness.service;
        mocks = harness.mocks;
    });


    it('GIVEN backend returns a non empty typeStructure THEN open will delegate to genericEditorModalService.open (and display a genericTab) and invoke a rerendering upon closing', function() {

        var type = 'WithStructure';
        var id = 'dfgsdf';

        expect(editorModalService.open(type, id)).toBeResolvedWithData('somedata1');
        expect(mocks.typeStructureRestService.getStructureByType).toHaveBeenCalledWith('WithStructure', {
            getWholeStructure: true
        });
        expect(mocks.genericEditorModalService.open.callCount).toBe(1);
        expect(mocks.genericEditorModalService.open.calls[0].args.length).toBe(3);
        expect(mocks.genericEditorModalService.open.calls[0].args[0]).toEqual({
            componentId: 'dfgsdf',
            componentType: 'WithStructure',
            title: 'type.withstructure.name'
        });
        expect(mocks.genericEditorModalService.open.calls[0].args[1]).toEqual([{
            id: 'genericTab',
            title: 'editortabset.generictab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/genericTabTemplate.html'
        }, {
            id: 'basicTab',
            title: 'editortabset.basictab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/basicTabTemplate.html'
        }, {
            id: 'adminTab',
            title: 'editortabset.admintab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/adminTabTemplate.html'
        }, {
            id: 'visibilityTab',
            title: 'editortabset.visibilitytab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/visibilityTabTemplate.html'
        }]);

        expect(mocks.renderService.renderComponent).not.toHaveBeenCalled();
        var callback = mocks.genericEditorModalService.open.calls[0].args[2];
        callback();
        expect(mocks.renderService.renderComponent).toHaveBeenCalledWith('dfgsdf', 'WithStructure');
    });

    it('GIVEN backend returns an empty typeStructure THEN open will delegate to genericEditorModalService.open (with no genericTab) and invoke a rerendering upon closing', function() {

        var type = 'WithoutStructure';
        var id = 'dfgsdf';

        expect(editorModalService.open(type, id)).toBeResolvedWithData('somedata1');
        expect(mocks.typeStructureRestService.getStructureByType).toHaveBeenCalledWith('WithoutStructure', {
            getWholeStructure: true
        });
        expect(mocks.genericEditorModalService.open.callCount).toBe(1);
        expect(mocks.genericEditorModalService.open.calls[0].args.length).toBe(3);
        expect(mocks.genericEditorModalService.open.calls[0].args[0]).toEqual({
            componentId: 'dfgsdf',
            componentType: 'WithoutStructure',
            title: 'type.withoutstructure.name'
        });
        expect(mocks.genericEditorModalService.open.calls[0].args[1]).toEqual([{
            id: 'basicTab',
            title: 'editortabset.basictab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/basicTabTemplate.html'
        }, {
            id: 'adminTab',
            title: 'editortabset.admintab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/adminTabTemplate.html'
        }, {
            id: 'visibilityTab',
            title: 'editortabset.visibilitytab.title',
            templateUrl: 'web/features/cmssmarteditContainer/components/cmsComponents/editorModal/tabs/visibilityTabTemplate.html'
        }]);

        expect(mocks.renderService.renderComponent).not.toHaveBeenCalled();
        var callback = mocks.genericEditorModalService.open.calls[0].args[2];
        callback();
        expect(mocks.renderService.renderComponent).toHaveBeenCalledWith('dfgsdf', 'WithoutStructure');
    });

    it('GIVEN backend returns an empty typeStructure THEN open will delegate to genericEditorModalService.open (with no genericTab), but do not invoke a rerendering upon closing due to a configuration', function() {
        var type = 'WithoutStructure';
        var id = 'dfgsdf';
        expect(editorModalService.open(type, id, {
            render: false
        })).toBeResolvedWithData('somedata1');
        expect(mocks.renderService.renderComponent).not.toHaveBeenCalled();
        var callback = mocks.genericEditorModalService.open.calls[0].args[2];
        callback();
        expect(mocks.renderService.renderComponent).not.toHaveBeenCalled();
    });
});
