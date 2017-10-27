angular
    .module(
        'personalizationsmarteditCommerceCustomizationModule', ['modalServiceModule', 'personalizationsmarteditCommons', 'personalizationsmarteditRestServiceModule', 'eventServiceModule', 'personalizationsmarteditContextServiceModule']
    )
    .factory(
        'personalizationsmarteditCommerceCustomizationView',
        function($controller, modalService, MODAL_BUTTON_ACTIONS,
            MODAL_BUTTON_STYLES) {
            var manager = {};
            manager.openCommerceCustomizationAction = function(
                customization, variation) {
                modalService.open({
                    title: "personalization.modal.commercecustomization.title",
                    templateUrl: 'web/features/personalizationsmarteditcontainer/management/commerceCustomizationView/personalizationsmarteditCommerceCustomizationViewTemplate.html',
                    cssClasses: 'yPersonalizationModal',
                    controller: ['$scope', 'modalManager', function($scope, modalManager) {
                        $scope.customization = customization;
                        $scope.variation = variation;
                        $scope.modalManager = modalManager;
                        angular.extend(this, $controller('personalizationsmarteditCommerceCustomizationViewController', {
                            $scope: $scope
                        }));
                    }],
                    buttons: [{
                        id: 'confirmCancel',
                        label: 'personalization.modal.commercecustomization.button.cancel',
                        style: MODAL_BUTTON_STYLES.SECONDARY,
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }, {
                        id: 'confirmSave',
                        label: 'personalization.modal.commercecustomization.button.submit',
                        action: MODAL_BUTTON_ACTIONS.CLOSE
                    }]
                }).then(function(result) {

                }, function(failure) {});
            };

            return manager;
        })
    .controller(
        'personalizationsmarteditCommerceCustomizationViewController',
        function($scope, $filter,
            personalizationsmarteditRestService,
            personalizationsmarteditMessageHandler,
            systemEventService,
            personalizationsmarteditCommerceCustomizationService,
            personalizationsmarteditContextService,
            personalizationsmarteditUtils) {

            var STATUS_OLD = 'old';
            var STATUS_NEW = 'new';
            var STATUS_DELETE = 'delete';


            $scope.availableTypes = [];
            $scope.select = {};
            $scope.actions = [];
            $scope.removedActions = [];

            var populateActions = function() {
                personalizationsmarteditRestService.getActions($scope.customization.code, $scope.variation.code)
                    .then(function successCallback(response) {

                        $scope.actions = response.actions.filter(function(elem) {
                            return elem.type !== 'cxCmsActionData';
                        }).map(function(item) {
                            return {
                                action: item,
                                status: STATUS_OLD
                            };
                        });
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingactions'));
                    });
            };

            var getType = function(type) {
                for (var i = 0; i < $scope.availableTypes.length; ++i) {
                    if ($scope.availableTypes[i].type === type) {
                        return $scope.availableTypes[i];
                    }
                }
                return {};
            };

            var sendRefreshEvent = function() {
                systemEventService.sendSynchEvent('CUSTOMIZATIONS_MODIFIED', {});
            };


            var displaySaveMessage = function(shouldCreate, createStatus, shouldDelete, deleteStatus) {
                var created = createStatus === "OK";
                var deleted = deleteStatus === "OK";
                var createFinished = created || createStatus === "FAIL";
                var deleteFinished = deleted || deleteStatus === "FAIL";

                var messages = [];

                if (shouldCreate && createFinished) {
                    if (created) {
                        messages.push(personalizationsmarteditMessageHandler.buildMessage($filter('translate')('personalization.info.creatingaction'), true));
                        sendRefreshEvent();
                    } else {
                        messages.push(personalizationsmarteditMessageHandler.buildMessage($filter('translate')('personalization.error.creatingaction'), false));
                    }
                }

                if (shouldDelete && deleteFinished) {
                    if (deleted) {
                        messages.push(personalizationsmarteditMessageHandler.buildMessage($filter('translate')('personalization.info.removingaction'), true));
                        sendRefreshEvent();
                    } else {
                        messages.push(personalizationsmarteditMessageHandler.buildMessage($filter('translate')('personalization.error.removingaction'), false));
                    }
                }

                if (messages.length > 0) {
                    personalizationsmarteditMessageHandler.send(messages);
                }
            };

            $scope.getActionsToDisplay = function() {
                return $scope.actions;
            };

            $scope.isItemInSelectedActions = function(item) {
                return $scope.actions.find(function(wrapper) {
                    return wrapper.action.promotionId === item.code;
                });
            };

            $scope.displayAction = function(actionWrapper) {
                var action = actionWrapper.action;
                var type = getType(action.type);
                if (type.getName) {
                    return type.getName(action);
                } else {
                    return action.code;
                }
            };

            //This function requires two parameters
            // action to be added
            // and comparer = function(action,action) for defining if two actions are identical
            // comparer is used
            $scope.addAction = function(action, comparer) {

                var exist = false;
                $scope.actions.forEach(function(wrapper) {
                    exist = exist || comparer(action, wrapper.action);
                });
                if (!exist) {
                    var status = STATUS_NEW;
                    var removedIndex = -1;
                    $scope.removedActions.forEach(function(wrapper, index) {
                        if (comparer(action, wrapper.action)) {
                            removedIndex = index;
                        }
                    });
                    if (removedIndex >= 0) //we found or action in delete queue
                    {
                        status = STATUS_OLD;
                        $scope.removedActions.splice(removedIndex, 1);
                    }
                    $scope.actions.push({
                        action: action,
                        status: status
                    });
                }
            };

            $scope.removeSelectedAction = function(actionWrapper) {
                var index = $scope.actions.indexOf(actionWrapper);
                if (index < 0) {
                    return;
                }
                var removed = $scope.actions.splice(index, 1);
                //only old item should be added to delete queue
                //new items are just deleted
                if (removed[0].status === STATUS_OLD) {
                    removed[0].status = STATUS_DELETE;
                    $scope.removedActions.push(removed[0]);
                }
            };

            $scope.isDirty = function() {
                var dirty = false;
                //dirty if at least one new
                $scope.actions.forEach(function(wrapper) {
                    dirty = dirty || wrapper.status === STATUS_NEW;
                });
                //or one deleted
                dirty = dirty || $scope.removedActions.length > 0;
                return dirty;
            };

            // customization and variation status helper fucntions
            $scope.customizationStatusText = personalizationsmarteditUtils.getEnablementTextForCustomization($scope.customization, 'personalization.modal.commercecustomization');
            $scope.variationStatusText = personalizationsmarteditUtils.getEnablementTextForVariation($scope.variation, 'personalization.modal.commercecustomization');
            $scope.customizationStatus = personalizationsmarteditUtils.getActivityStateForCustomization($scope.customization);
            $scope.variationStatus = personalizationsmarteditUtils.getActivityStateForVariation($scope.customization, $scope.variation);

            // modal buttons
            $scope.onSave = function() {
                var createData = {
                    actions: $scope.actions.filter(function(item) {
                        return item.status === STATUS_NEW;
                    }).map(function(item) {
                        return item.action;
                    })
                };

                var deleteData = $scope.removedActions.filter(function(item) {
                    return item.status === STATUS_DELETE;
                }).map(function(item) {
                    return item.action.code;
                });

                var shouldCreate = createData.actions.length > 0;
                var shouldDelete = deleteData.length > 0;
                var createStatus = null;
                var deleteStatus = null;

                if (shouldCreate) {
                    personalizationsmarteditRestService.createActions($scope.customization.code, $scope.variation.code, createData)
                        .then(function successCallback(response) {
                            createStatus = "OK";
                            displaySaveMessage(shouldCreate, createStatus, shouldDelete, deleteStatus);
                        }, function errorCallback() {
                            createStatus = "FAIL";
                            displaySaveMessage(shouldCreate, createStatus, shouldDelete, deleteStatus);
                        });
                }

                if (shouldDelete) {
                    personalizationsmarteditRestService.deleteActions($scope.customization.code, $scope.variation.code, deleteData)
                        .then(function successCallback(response) {
                            deleteStatus = "OK";
                            displaySaveMessage(shouldCreate, createStatus, shouldDelete, deleteStatus);
                        }, function errorCallback() {
                            deleteStatus = "FAIL";
                            displaySaveMessage(shouldCreate, createStatus, shouldDelete, deleteStatus);
                        });
                }
            };

            $scope.modalManager.setButtonHandler(function(buttonId) {
                if (buttonId === 'confirmSave') {
                    $scope.onSave();
                }
            });

            //init
            (function() {
                $scope.availableTypes = personalizationsmarteditCommerceCustomizationService.getAvailableTypes(personalizationsmarteditContextService.getSeConfigurationData());
                $scope.select = {
                    type: $scope.availableTypes[0]
                };
                populateActions();
            })();
        });
