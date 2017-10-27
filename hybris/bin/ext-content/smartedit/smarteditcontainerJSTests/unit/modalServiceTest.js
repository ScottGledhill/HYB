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
describe('test modalService Module', function() {

    var $q, $uibModal, $controller, $controllerHolder, $rootScope, modalService, childScope, generateIdentifier, $translate, $templateCache, $uibModalInstance;
    var result = {
        somefield: 'somevalue'
    };

    beforeEach(customMatchers);

    beforeEach(module('modalServiceModule', function($provide) {

        $uibModal = jasmine.createSpyObj('$uibModal', ['open']);
        $uibModal.open.andReturn({
            result: result
        });
        $controllerHolder = jasmine.createSpyObj('$controllerHolder', ['$controller']);


        $templateCache = jasmine.createSpyObj('$templateCache', ['put', 'remove']);
        generateIdentifier = jasmine.createSpy('generateIdentifier');
        $translate = jasmine.createSpy('$translate');

        $provide.value('$uibModal', $uibModal);
        $provide.value('$controller', $controllerHolder.$controller);
        $provide.value('$templateCache', $templateCache);
        $provide.value('generateIdentifier', generateIdentifier);
        $provide.value('$translate', $translate);
    }));

    beforeEach(inject(function(_$rootScope_, _modalService_, _$q_, _$controller_) {
        $rootScope = _$rootScope_;
        $controller = $controller;
        childScope = {
            'rootScopeIntialField': 'rootScopeIntialValue'
        };

        spyOn($rootScope, '$new').andReturn(childScope);
        modalService = _modalService_;
        $q = _$q_;
    }));

    it('will throw exception when neither templateUrl nor template provided', function() {

        expect(modalService.open).toThrow("modalService.configuration.errors.no.template.provided");

    });

    it('will throw exception when both templateUrl and template provided', function() {

        expect(function() {
            return modalService.open({
                templateUrl: 'sometemplateurl',
                templateInline: 'sometemplate'
            });
        }).toThrow("modalService.configuration.errors.2.templates.provided");
    });

    it('open with just templateUrl or templateInline will call $uibModal.open with expected configuration', function() {

        expect(modalService.open({
            templateUrl: 'someURL'
        })).toBe(result);

        expect($uibModal.open).toHaveBeenCalled();

        var modalConfiguration = $uibModal.open.calls[0].args[0];

        expect(modalConfiguration).toEqual(jasmine.objectContaining({
            templateUrl: 'web/common/services/modalTemplate.html',
            size: 'lg'
        }));
    });

    it('open with templateUrl and cssClasses will call $uibModal.open with expected configuration', function() {

        expect(modalService.open({
            templateUrl: 'someURL',
            cssClasses: 'class1 class2 class3'
        })).toBe(result);

        expect($uibModal.open).toHaveBeenCalled();

        var modalConfiguration = $uibModal.open.calls[0].args[0];

        expect(modalConfiguration).toEqual(jasmine.objectContaining({
            templateUrl: 'web/common/services/modalTemplate.html',
            size: 'lg',
            windowClass: 'class1 class2 class3'
        }));
    });


    it('when no controller is passed default controller is set : properties, cancel and close functions', function() {

        expect(modalService.open({
            templateUrl: 'someURL'
        })).toBe(result);

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect(controller.templateUrl).toBe('someURL');
        expect(controller.templateInline).toBeUndefined();

        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.dismiss();
        expect($uibModalInstance.dismiss).toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.close();
        expect($uibModalInstance.close).toHaveBeenCalled();

        expect($controllerHolder.$controller).not.toHaveBeenCalled();

    });

    it('when a controller ID is passed, $controller instantiates the controller and sets it with properties, cancel and close functions', function() {

        expect(modalService.open({
            templateUrl: 'someURL',
            controller: 'controllerID'
        })).toBe(result);

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect(controller.templateUrl).toBe('someURL');
        expect(controller.templateInline).toBeUndefined();

        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.dismiss();
        expect($uibModalInstance.dismiss).toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.close();
        expect($uibModalInstance.close).toHaveBeenCalled();

        expect($controllerHolder.$controller).toHaveBeenCalledWith("controllerID", {
            $scope: scope,
            modalManager: controller._modalManager
        });
    });

    it('when a controller function is passed, $controller executes the controller and sets it with properties, cancel and close functions', function() {

        var configuration = {
            templateUrl: 'someURL',
            controller: function() {}
        };
        spyOn(configuration, 'controller').andReturn();

        expect(modalService.open(configuration)).toBe(result);

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect(controller.templateUrl).toBe('someURL');
        expect(controller.templateInline).toBeUndefined();

        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.dismiss();
        expect($uibModalInstance.dismiss).toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.close();
        expect($uibModalInstance.close).toHaveBeenCalled();

        //expect($controllerHolder.$controller).toHaveBeenCalledWith(controller, {
        //    $scope: scope,
        //    modalManager: controller._modalManager
        //});
    });

    it('open with inline template will call $uibModal.open with expected configuration and will register template in the cache', function() {
        var inlineTemplate = 'sometemplate';
        var mockIdentifier = 'someID';
        var expectedTemplateUrl = 'modalTemplateKey' + btoa(mockIdentifier);

        generateIdentifier.andReturn(mockIdentifier);

        expect(modalService.open({
            templateInline: inlineTemplate
        })).toBe(result);

        expect($uibModal.open).toHaveBeenCalled();

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect(generateIdentifier).toHaveBeenCalled();
        expect(controller.templateUrl).toBe(expectedTemplateUrl);
        expect($templateCache.put).toHaveBeenCalledWith(controller.templateUrl, inlineTemplate);
    });

    it('when a template url is passed, $controller does not add anything to the template cache', function() {
        expect(modalService.open({
            templateUrl: 'someURL'
        })).toBe(result);

        expect($uibModal.open).toHaveBeenCalled();

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect($templateCache.put).not.toHaveBeenCalledWith(controller.templateUrl, jasmine.any(String));
    });

    it('when an inline template is passed, $controller clears the templateCache entries after a cancel operation', function() {
        var inlineTemplate = 'sometemplate';
        expect(modalService.open({
            templateInline: inlineTemplate
        })).toBe(result);

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.dismiss();
        expect($templateCache.remove).toHaveBeenCalledWith(controller.templateUrl);
        expect($uibModalInstance.dismiss).toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
    });

    it('when an inline template is passed, $controller clears the templateCache entries after a close operation', function() {
        var inlineTemplate = 'sometemplate';
        expect(modalService.open({
            templateInline: inlineTemplate
        })).toBe(result);

        var modalConfiguration = $uibModal.open.calls[0].args[0];
        var ModalController = modalConfiguration.controller;

        var scope = {};
        $uibModalInstance = jasmine.createSpyObj('$uibModalInstance', ['dismiss', 'close']);
        var controller = new ModalController[2](scope, $uibModalInstance);

        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).not.toHaveBeenCalled();
        controller.close();
        expect($templateCache.remove).toHaveBeenCalledWith(controller.templateUrl);
        expect($uibModalInstance.dismiss).not.toHaveBeenCalled();
        expect($uibModalInstance.close).toHaveBeenCalled();
    });
});