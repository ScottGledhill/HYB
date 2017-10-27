angular.module('personalizationsmarteditManagerViewModule', [
        'modalServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditContextServiceModule',
        'confirmationModalServiceModule',
        'personalizationsmarteditManagerModule',
        'personalizationsmarteditCommerceCustomizationModule',
        'eventServiceModule',
        'personalizationsmarteditDataFactory'
    ])
    .factory('personalizationsmarteditManagerView', function(modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES) {
        var manager = {};
        manager.openManagerAction = function() {
            modalService.open({
                title: "personalization.modal.manager.title",
                templateUrl: 'web/features/personalizationsmarteditcontainer/management/managerView/personalizationsmarteditManagerViewTemplate.html',
                controller: 'personalizationsmarteditManagerViewController',
                size: 'fullscreen',
                cssClasses: 'manage-customization yPersonalizationManagerModal'
            }).then(function(result) {

            }, function(failure) {});
        };

        return manager;
    })
    .controller('personalizationsmarteditManagerViewController', function($q, $scope, $filter, $timeout, confirmationModalService, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditContextService, personalizationsmarteditManager, systemEventService, personalizationsmarteditUtils, personalizationsmarteditCommerceCustomizationView, personalizationsmarteditCommerceCustomizationService, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES, PERSONALIZATION_MODEL_STATUS_CODES, PaginationHelper) {

        var getCustomizations = function(filter) {
            personalizationsmarteditRestService.getCustomizations(filter)
                .then(function successCallback(response) {
                    if ($scope.isUndefined($scope.allCustomizationsCount)) {
                        $scope.allCustomizationsCount = response.pagination.totalCount;
                    }
                    $scope.customizations = response.customizations || [];
                    $scope.filteredCustomizationsCount = response.pagination.totalCount;
                    $scope.pagination.page = response.pagination.page;
                    $scope.pagination.pages = Array(response.pagination.totalPages).join().split(',').map(function(item, index) {
                        return index;
                    });
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
                });
        };

        var getVariations = function(customization, filter) {
            personalizationsmarteditRestService.getCustomization(customization.code)
                .then(function successCallback(response) {
                    customization.variations = response.variations;
                    $scope.customizationClickAction(customization);
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                });
        };

        var getCustomizationsFilterObject = function() {
            return {
                active: "all",
                name: $scope.search.name,
                currentSize: $scope.pagination.count,
                currentPage: $scope.pagination.page,
                statuses: $scope.search.status.modelStatuses
            };
        };

        var refreshGrid = function() {
            $timeout(function() {
                getCustomizations(getCustomizationsFilterObject());
            }, 0);
        };

        var getDefaultStatus = function() {
            return $scope.statuses.filter(function(elem) {
                return elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL;
            })[0];
        };

        var currentLanguageIsocode = personalizationsmarteditContextService.getSeExperienceData().languageDescriptor.isocode;
        $scope.catalogName = personalizationsmarteditContextService.getSeExperienceData().catalogDescriptor.name[currentLanguageIsocode];
        $scope.catalogName += " - " + personalizationsmarteditContextService.getSeExperienceData().catalogDescriptor.catalogVersion;
        $scope.customizations = [];
        $scope.allCustomizationsCount = undefined;
        $scope.filteredCustomizationsCount = 0;

        $scope.statuses = personalizationsmarteditUtils.getStatusesMapping();

        $scope.search = {
            name: '',
            status: getDefaultStatus()
        };

        $scope.pagination = new PaginationHelper();
        $scope.pagination.reset();

        $scope.searchInputKeypress = function(keyEvent) {
            if (keyEvent.which === 13 || $scope.search.name.length > 2 || $scope.search.name.length === 0) {
                $scope.pagination.page = 0;
                refreshGrid();
            }
        };

        $scope.resetSearchInput = function() {
            $scope.search.name = "";
            $scope.search.status = getDefaultStatus();
            refreshGrid();
        };

        $scope.editCustomizationAction = function(customization) {
            personalizationsmarteditManager.openEditCustomizationModal(customization.code);
        };
        $scope.deleteCustomizationAction = function(customization) {
            confirmationModalService.confirm({
                description: 'personalization.modal.manager.deletecustomization.content'
            }).then(function() {
                personalizationsmarteditRestService.getCustomization(customization.code)
                    .then(function successCallback(responseCustomization) {
                        responseCustomization.status = "DELETED";
                        personalizationsmarteditRestService.updateCustomization(responseCustomization)
                            .then(function successCallback() {
                                $scope.allCustomizationsCount = undefined;
                                refreshGrid();
                            }, function errorCallback() {
                                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingcustomization'));
                            });
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingcustomization'));
                    });
            });
        };
        $scope.editVariationAction = function(customization, variation) {
            personalizationsmarteditManager.openEditCustomizationModal(customization.code, variation.code);
        };

        $scope.isCommerceCustomizationEnabled = function() {
            return personalizationsmarteditCommerceCustomizationService.isCommerceCustomizationEnabled(personalizationsmarteditContextService.getSeConfigurationData());
        };

        $scope.manageCommerceCustomization = function(customization, variation) {
            personalizationsmarteditCommerceCustomizationView.openCommerceCustomizationAction(customization, variation);
        };

        $scope.isDeleteVariationEnabled = function(customization) {
            return personalizationsmarteditUtils.getVisibleItems(customization.variations).length > 1;
        };

        $scope.deleteVariationAction = function(customization, variation, $event) {
            if ($scope.isDeleteVariationEnabled(customization)) {
                confirmationModalService.confirm({
                    description: 'personalization.modal.manager.deletevariation.content'
                }).then(function() {
                    personalizationsmarteditRestService.getVariation(customization.code, variation.code)
                        .then(function successCallback(responseVariation) {
                            responseVariation.status = "DELETED";
                            personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                                .then(function successCallback() {
                                    getVariations(customization);
                                }, function errorCallback() {
                                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingvariation'));
                                });
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.deletingvariation'));
                        });
                });
            } else {
                $event.stopPropagation();
            }
        };

        $scope.openNewModal = function() {
            personalizationsmarteditManager.openCreateCustomizationModal();
        };

        $scope.paginationCallback = function() {
            refreshGrid();
        };

        $scope.setCustomizationRank = function(customization, increaseValue, $event, firstOrLast) {
            var stopProcessing = increaseValue < 0 && firstOrLast && $scope.pagination.page === 0;
            stopProcessing = stopProcessing || (increaseValue > 0 && firstOrLast && $scope.pagination.page === $scope.pagination.pages.length - 1);
            if ($scope.isFilterEnabled() || stopProcessing) {
                $event.stopPropagation();
            } else {
                personalizationsmarteditRestService.updateCustomizationRank(customization.code, increaseValue)
                    .then(function successCallback() {
                        refreshGrid();
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingcustomization'));
                    });
            }
        };

        $scope.setVariationRank = function(customization, variation, increaseValue, $event, firstOrLast) {
            if (firstOrLast) {
                $event.stopPropagation();
            } else {
                personalizationsmarteditRestService.getVariation(customization.code, variation.code)
                    .then(function successCallback(responseVariation) {
                        responseVariation.rank = personalizationsmarteditUtils.getValidRank(customization.variations, variation, increaseValue);
                        personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                            .then(function successCallback() {
                                getVariations(customization);
                            }, function errorCallback() {
                                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.editingvariation'));
                            });
                    }, function errorCallback() {
                        personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingvariation'));
                    });
            }
        };

        $scope.toogleVariationActive = function(customization, variation) {
            personalizationsmarteditRestService.getVariation(customization.code, variation.code)
                .then(function successCallback(responseVariation) {
                    responseVariation.enabled = !responseVariation.enabled;
                    responseVariation.status = responseVariation.enabled ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
                    personalizationsmarteditRestService.editVariation(customization.code, responseVariation)
                        .then(function successCallback() {
                            getVariations(customization);
                        }, function errorCallback() {
                            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.editingvariation'));
                        });
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingvariation'));
                });
        };

        $scope.isUndefined = function(value) {
            return value === undefined;
        };

        $scope.isFilterEnabled = function() {
            return $scope.filteredCustomizationsCount < $scope.allCustomizationsCount;
        };

        $scope.customizationClickAction = function(customization) {
            var filter = {
                includeActions: true
            };

            personalizationsmarteditRestService.getVariationsForCustomization(customization.code, filter).then(
                function successCallback(response) {
                    customization.variations = response.variations || [];
                    customization.variations.forEach(function(variation) {
                        variation.numberOfComponents = personalizationsmarteditCommerceCustomizationService.getNonCommerceActionsCount(variation);
                        variation.commerceCustomizations = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCountMap(variation);
                        variation.numberOfCommerceActions = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                    });
                },
                function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                });
        };

        $scope.getCommerceCustomizationTooltip = function(variation) {
            var result = "";
            for (var property in variation.commerceCustomizations) {
                result += $filter('translate')('personalization.modal.manager.commercecustomization.' + property) + ": " + variation.commerceCustomizations[property] + "\n";
            }
            return result;
        };

        $scope.hasCommerceCustomization = function(variation) {
            return variation.numberOfCommerceActions > 0;
        };

        $scope.getFormattedDate = function(myDate) {
            if (myDate) {
                return personalizationsmarteditUtils.formatDate(myDate);
            } else {
                return "";
            }
        };

        $scope.getEnablementTextForCustomization = function(customization) {
            return personalizationsmarteditUtils.getEnablementTextForCustomization(customization, 'personalization.modal.manager');
        };

        $scope.getEnablementTextForVariation = function(variation) {
            return personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.modal.manager');
        };

        $scope.getEnablementActionTextForVariation = function(variation) {
            return personalizationsmarteditUtils.getEnablementActionTextForVariation(variation, 'personalization.modal.manager');
        };

        $scope.getActivityStateForCustomization = function(customization) {
            return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
        };

        $scope.getActivityStateForVariation = function(customization, variation) {
            return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
        };

        $scope.allCustomizationsCollapsed = function() {
            return $scope.customizations.map(function(elem) {
                return elem.isCollapsed;
            }).reduce(function(previousValue, currentValue) {
                return previousValue && currentValue;
            }, true);
        };

        $scope.getVisibleCustomizations = function() {
            return $scope.customizations;
        };

        $scope.$watch('search.status', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                $scope.pagination.page = 0;
                refreshGrid();
            }
        }, true);

        //init
        (function() {
            systemEventService.registerEventHandler('CUSTOMIZATIONS_MODIFIED', function() {
                refreshGrid();
                return $q.when();
            });
            refreshGrid();
        })();

    });
