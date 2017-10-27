angular.module('personalizationsmarteditContextMenu', [
        'modalServiceModule',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditCommons',
        'ui.select',
        'genericEditorModule',
        'editorModalServiceModule',
        'gatewayProxyModule',
        'personalizationsmarteditContextServiceModule',
        'renderServiceModule',
        'personalizationsmarteditDataFactory'
    ])
    .factory('personalizationsmarteditContextModal', function($controller, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, $filter, personalizationsmarteditMessageHandler, gatewayProxy, personalizationsmarteditContextService, renderService, personalizationsmarteditRestService, editorModalService) {

        var PersonalizationsmarteditContextModal = function() {
            this.gatewayId = "personalizationsmarteditContextModal";
            gatewayProxy.initForService(this);
        };

        var modalButtons = [{
            id: 'cancel',
            label: "personalization.modal.addeditaction.button.cancel",
            style: MODAL_BUTTON_STYLES.SECONDARY,
            action: MODAL_BUTTON_ACTIONS.DISMISS
        }, {
            id: 'submit',
            label: "personalization.modal.addeditaction.button.submit",
            action: MODAL_BUTTON_ACTIONS.CLOSE
        }];

        PersonalizationsmarteditContextModal.prototype.openDeleteAction = function(componentType, componentId, containerId, slotId, actionId) {
            modalService.open({
                size: 'md',
                title: 'personalization.modal.deleteaction.title',
                templateInline: '<div id="confirmationModalDescription">{{ "' + "personalization.modal.deleteaction.content" + '" | translate }}</div>',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.componentType = componentType;
                    $scope.componentId = componentId;
                    $scope.actionId = actionId;
                    $scope.modalManager = modalManager;
                    angular.extend(this, $controller('modalDeleteActionController', {
                        $scope: $scope
                    }));
                }],
                buttons: [{
                    id: 'confirmCancel',
                    label: 'personalization.modal.deleteaction.button.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'confirmOk',
                    label: 'personalization.modal.deleteaction.button.ok',
                    action: MODAL_BUTTON_ACTIONS.CLOSE
                }]
            }).then(function(result) {
                personalizationsmarteditContextService.selectedComponents.splice(personalizationsmarteditContextService.selectedComponents.indexOf(containerId), 1);
                personalizationsmarteditContextService.applySynchronization();
                renderService.renderSlots([slotId]).then(function(result) {
                    renderService.renderPage(true);
                });
            });
        };

        PersonalizationsmarteditContextModal.prototype.openAddAction = function(componentType, componentId, containerId, slotId, actionId) {
            modalService.open({
                title: "personalization.modal.addaction.title",
                templateUrl: 'web/features/personalizationsmarteditcontainer/contextMenu/personalizationsmarteditAddEditActionTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.componentType = componentType;
                    $scope.componentId = componentId;
                    $scope.defaultComponentId = componentId;
                    $scope.containerId = containerId;
                    $scope.slotId = slotId;
                    $scope.actionId = actionId;
                    $scope.modalManager = modalManager;
                    $scope.editEnabled = false;
                    angular.extend(this, $controller('modalAddEditActionController', {
                        $scope: $scope
                    }));
                    angular.extend(this, $controller('modalAddActionController', {
                        $scope: $scope
                    }));
                }],
                cssClasses: 'yPersonalizationContextModal',
                buttons: modalButtons
            }).then(function(resultContainer) {
                personalizationsmarteditContextService.selectedComponents.push(resultContainer);
                personalizationsmarteditContextService.applySynchronization();
                renderService.renderSlots([slotId]).then(function(result) {
                    renderService.renderPage(true);
                });
            }, function(failure) {});
        };

        PersonalizationsmarteditContextModal.prototype.openEditAction = function(componentType, componentId, containerId, slotId, actionId) {
            modalService.open({
                title: "personalization.modal.editaction.title",
                templateUrl: 'web/features/personalizationsmarteditcontainer/contextMenu/personalizationsmarteditAddEditActionTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.componentType = componentType;
                    $scope.componentId = componentId;
                    $scope.containerId = containerId;
                    $scope.slotId = slotId;
                    $scope.actionId = actionId;
                    $scope.modalManager = modalManager;
                    $scope.editEnabled = true;
                    angular.extend(this, $controller('modalAddEditActionController', {
                        $scope: $scope
                    }));
                    angular.extend(this, $controller('modalEditActionController', {
                        $scope: $scope
                    }));
                }],
                cssClasses: 'yPersonalizationContextModal',
                buttons: modalButtons
            }).then(function(result) {
                renderService.renderSlots([slotId]).then(function(result) {
                    renderService.renderPage(true);
                });
            });
        };

        PersonalizationsmarteditContextModal.prototype.openInfoAction = function() {
            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.nocustomizationvariationselected'));
        };

        PersonalizationsmarteditContextModal.prototype.openEditComponentAction = function(componentType, componentId) {
            editorModalService.open(componentType, componentId);
        };

        return new PersonalizationsmarteditContextModal();

    })
    .controller('modalAddEditActionController', function($scope, $filter, $q, $timeout, editorModalService, personalizationsmarteditContextService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, PaginationHelper) {

        $scope.actions = [{
            id: "create",
            name: $filter('translate')("personalization.modal.addeditaction.createnewcomponent")
        }, {
            id: "use",
            name: $filter('translate')("personalization.modal.addeditaction.usecomponent")
        }];

        $scope.newComponent = {};
        $scope.component = {};
        $scope.components = [];
        $scope.newComponentTypes = [];

        var initNewComponentTypes = function() {
            personalizationsmarteditRestService.getNewComponentTypes().then(function success(resp) {
                $scope.newComponentTypes = resp.componentTypes;
            }, function error(resp) {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponentstypes'));
            });
        };

        var getAndSetComponentById = function(componentId) {
            personalizationsmarteditRestService.getComponent(componentId).then(function successCallback(resp) {
                $scope.component.selected = resp;
            }, function errorCallback(resp) {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponents'));
            });
        };

        $scope.newComponentTypeSelectedEvent = function($item, $model) {
            var options = {
                render: false
            };
            editorModalService.open($scope.newComponent.selected.code, null, options, personalizationsmarteditContextService.getPageId()).then(
                function(response) {
                    $scope.action = {
                        selected: $filter('filter')($scope.actions, {
                            id: "use"
                        }, true)[0]
                    };
                    $scope.componentId = response.uid;
                    getAndSetComponentById($scope.componentId);
                },
                function(response) {
                    $scope.newComponent = {};
                });

        };

        var editAction = function(customizationId, variationId, actionId, componentId) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.editAction(customizationId, variationId, actionId, componentId).then(
                function successCallback(response) {
                    personalizationsmarteditMessageHandler.sendInformation($filter('translate')('personalization.info.updatingaction'));
                    deferred.resolve();
                },
                function errorCallback(response) {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingaction'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var addActionToContainer = function(componentId, containerId, customizationId, variationId) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.addActionToContainer(componentId, containerId, customizationId, variationId).then(
                function successCallback(response) {
                    personalizationsmarteditMessageHandler.sendInformation($filter('translate')('personalization.info.creatingaction'));
                    deferred.resolve(containerId);
                },
                function errorCallback(response) {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.creatingaction'));
                    deferred.reject();
                });
            return deferred.promise;
        };

        var buttonHandlerFn = function(buttonId) {
            if (buttonId === 'submit') {
                if ($scope.editEnabled) {
                    return editAction($scope.selectedCustomization.code, $scope.selectedVariation.code, $scope.actionId, $scope.component.selected.uid);
                } else {
                    if ($scope.containerId) {
                        return addActionToContainer($scope.component.selected.uid, $scope.containerId, $scope.selectedCustomization.code, $scope.selectedVariation.code);
                    } else {
                        return personalizationsmarteditRestService.replaceComponentWithContainer($scope.defaultComponentId, $scope.slotId).then(
                            function successCallback(result) {
                                return addActionToContainer($scope.component.selected.uid, result.uid, $scope.selectedCustomization.code, $scope.selectedVariation.code);
                            },
                            function errorCallback(response) {
                                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.replacingcomponent'));
                                return $q.defer().reject();
                            });
                    }
                }
            }
            return $q.defer().reject();
        };
        $scope.modalManager.setButtonHandler(buttonHandlerFn);

        $scope.componentFilter = {
            name: ''
        };
        var getComponentFilterObject = function() {
            return {
                currentPage: $scope.componentPagination.page + 1,
                mask: $scope.componentFilter.name,
                pageSize: 100,
                sort: 'name'
            };
        };

        $scope.componentPagination = new PaginationHelper();
        $scope.componentPagination.reset();

        $scope.moreComponentRequestProcessing = false;
        $scope.addMoreComponentItems = function() {
            if ($scope.componentPagination.page < $scope.componentPagination.totalPages - 1 && !$scope.moreComponentRequestProcessing) {
                $scope.moreComponentRequestProcessing = true;
                personalizationsmarteditRestService.getComponents(getComponentFilterObject()).then(function successCallback(response) {
                    var filteredComponents = response.componentItems.filter(function(elem) {
                        return $scope.newComponentTypes.some(function(element, index, array) {
                            return elem.typeCode === element.code;
                        });
                    });
                    Array.prototype.push.apply($scope.components, filteredComponents);
                    $scope.componentPagination = new PaginationHelper(response.pagination);
                    $scope.moreComponentRequestProcessing = false;
                    if ($scope.components.length < 20) { //not enough components on list to enable scroll
                        $timeout((function() {
                            $scope.addMoreComponentItems();
                        }), 0);
                    }
                }, function errorCallback(response) {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponents'));
                    $scope.moreComponentRequestProcessing = false;
                });
            }
        };

        $scope.componentSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            $scope.componentPagination.reset();
            $scope.componentFilter.name = searchObj;
            $scope.components.length = 0;
            $scope.addMoreComponentItems();
        };

        $scope.$watch('action.selected', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                $scope.component.selected = undefined;
                if ($scope.editEnabled) {
                    getAndSetComponentById($scope.componentId);
                }
            }
        }, true);

        $scope.$watch('component.selected', function(newValue, oldValue) {
            $scope.modalManager.disableButton("submit");
            if (newValue !== undefined) {
                $scope.modalManager.enableButton("submit");
            }
        }, true);

        //init
        (function() {
            $scope.selectedCustomization = personalizationsmarteditContextService.selectedCustomizations;
            $scope.selectedVariation = personalizationsmarteditContextService.selectedVariations;

            if ($scope.editEnabled) {
                getAndSetComponentById($scope.componentId);
            }

            initNewComponentTypes();
        })();

    })
    .controller('modalAddActionController', function($scope) {
        $scope.action = {};
    })
    .controller('modalEditActionController', function($scope, $filter) {
        $scope.action = {
            selected: $filter('filter')($scope.actions, {
                id: "use"
            }, true)[0]
        };
    })
    .controller('modalDeleteActionController', function($scope, $q, personalizationsmarteditContextService, personalizationsmarteditRestService) {
        var buttonHandlerFn = function(buttonId) {
            if (buttonId === 'confirmOk') {
                return personalizationsmarteditRestService.deleteAction($scope.selectedCustomization.code, $scope.selectedVariation.code, $scope.actionId);
            }
            return $q.defer().reject();
        };
        $scope.modalManager.setButtonHandler(buttonHandlerFn);
        //init
        (function() {
            $scope.selectedVariation = personalizationsmarteditContextService.selectedVariations;
            $scope.selectedCustomization = personalizationsmarteditContextService.selectedCustomizations;
        })();
    });