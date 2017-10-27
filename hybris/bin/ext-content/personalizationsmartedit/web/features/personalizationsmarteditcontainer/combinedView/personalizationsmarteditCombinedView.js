angular.module('personalizationsmarteditCombinedViewModule', [
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditCommons',
        'ui.select',
        'personalizationsmarteditContextServiceModule',
        'renderServiceModule',
        'personalizationsmarteditDataFactory',
        'modalServiceModule',
        'personalizationsmarteditPreviewServiceModule'
    ])
    .factory('personalizationsmarteditCombinedView', function($controller, modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, personalizationsmarteditContextService, personalizationsmarteditCombinedViewCommons) {
        var manager = {};
        manager.openManagerAction = function() {
            modalService.open({
                title: "personalization.modal.combinedview.title",
                templateUrl: 'web/features/personalizationsmarteditcontainer/combinedView/personalizationsmarteditCombinedViewConfigureTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    $scope.modalManager = modalManager;
                    angular.extend(this, $controller('personalizationsmarteditCombinedViewController', {
                        $scope: $scope
                    }));
                }],
                buttons: [{
                    id: 'confirmCancel',
                    label: 'personalization.modal.combinedview.button.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'confirmOk',
                    label: 'personalization.modal.combinedview.button.ok',
                    action: MODAL_BUTTON_ACTIONS.CLOSE
                }]
            }).then(function(result) {
                if (personalizationsmarteditContextService.getCombinedView().enabled) {
                    personalizationsmarteditCombinedViewCommons.updatePreview(personalizationsmarteditCombinedViewCommons.getVariationsForPreviewTicket());
                }
            }, function(failure) {});
        };

        return manager;
    })
    .factory('personalizationsmarteditCombinedViewCommons', function(personalizationsmarteditContextService, personalizationsmarteditPreviewService, personalizationsmarteditIFrameUtils, personalizationsmarteditMessageHandler) {
        var service = {};

        service.updatePreview = function(previewTicketVariations) {
            var previewTicketId = personalizationsmarteditContextService.getSePreviewData().previewTicketId;

            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicketId, previewTicketVariations).then(function successCallback() {
                var previewData = personalizationsmarteditContextService.getSePreviewData();
                personalizationsmarteditIFrameUtils.reloadPreview(previewData.resourcePath, previewData.previewTicketId);
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingpreviewticket'));
            });
        };

        service.getVariationsForPreviewTicket = function() {
            var previewTicketVariations = [];
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            (combinedView.selectedItems || []).forEach(function(item) {
                previewTicketVariations.push({
                    customizationCode: item.customization.code,
                    variationCode: item.variation.code
                });
            });
            return previewTicketVariations;
        };

        service.combinedViewEnabledEvent = function(isEnabled) {
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            combinedView.enabled = isEnabled;
            personalizationsmarteditContextService.setCombinedView(combinedView);
            if (isEnabled) {
                service.updatePreview(service.getVariationsForPreviewTicket());
            } else {
                service.updatePreview([]);
            }
        };

        return service;
    })
    .controller('personalizationsmarteditCombinedViewMenuController', function($scope, personalizationsmarteditContextService, personalizationsmarteditCombinedViewCommons, PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING) {

        $scope.combinedView = personalizationsmarteditContextService.getCombinedView();
        $scope.selectedItems = $scope.combinedView.selectedItems || [];

        $scope.getClassForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
        };

        $scope.getLetterForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return String.fromCharCode('a'.charCodeAt() + wrappedIndex).toUpperCase();
        };

        $scope.$watch('combinedView.enabled', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                personalizationsmarteditCombinedViewCommons.combinedViewEnabledEvent(newValue);
            }
        }, true);

        $scope.$watch('combinedView.selectedItems', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                $scope.selectedItems = $scope.combinedView.selectedItems || [];
            }
        }, true);

    })
    .controller('personalizationsmarteditCombinedViewController', function($q, $scope, personalizationsmarteditCombinedViewCommons, personalizationsmarteditContextService, personalizationsmarteditRestService, customizationDataFactory, PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING, PaginationHelper, personalizationsmarteditMessageHandler, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES, personalizationsmarteditUtils) {

        customizationDataFactory.resetData();

        var successCallback = function(response) {
            $scope.pagination = new PaginationHelper(response.pagination);
            $scope.selectionArray.length = 0;
            customizationDataFactory.items.map(function(customization) {
                customization.variations.forEach(function(variation) {
                    $scope.selectionArray.push({
                        customization: {
                            code: customization.code,
                            name: customization.name,
                            rank: customization.rank
                        },
                        variation: {
                            code: variation.code,
                            name: variation.name
                        }
                    });
                });
            });
            $scope.moreCustomizationsRequestProcessing = false;
        };

        var errorCallback = function(response) {
            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
            $scope.moreCustomizationsRequestProcessing = false;
        };

        var getCustomizationsFilterObject = function() {
            return {
                pageId: personalizationsmarteditContextService.getPageId(),
                currentSize: $scope.pagination.count,
                currentPage: $scope.pagination.page + 1,
                name: $scope.customizationFilter.name,
                statuses: getDefaultStatus().modelStatuses
            };
        };

        var getCustomizations = function(categoryFilter) {
            var params = {
                filter: categoryFilter,
                dataArrayName: 'customizations'
            };
            customizationDataFactory.updateData(params, successCallback, errorCallback);
        };

        var getDefaultStatus = function() {
            return personalizationsmarteditUtils.getStatusesMapping().filter(function(elem) {
                return elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL;
            })[0];
        };

        $scope.pagination = new PaginationHelper();
        $scope.pagination.reset();

        $scope.combinedView = personalizationsmarteditContextService.getCombinedView();
        $scope.selectedItems = [];
        angular.copy($scope.combinedView.selectedItems || [], $scope.selectedItems);
        $scope.selectedElement = {};
        $scope.selectionArray = [];

        $scope.moreCustomizationsRequestProcessing = false;
        $scope.addMoreItems = function() {
            if ($scope.pagination.page < $scope.pagination.totalPages - 1 && !$scope.moreCustomizationsRequestProcessing) {
                $scope.moreCustomizationsRequestProcessing = true;
                getCustomizations(getCustomizationsFilterObject());
            }
        };

        $scope.selectElement = function(item) {
            $scope.selectedItems.push(item);
            $scope.selectedItems.sort(function(a, b) {
                return a.customization.rank - b.customization.rank;
            });

            $scope.selectedElement = null;
            $scope.searchInputKeypress(null, '');
        };

        $scope.initUiSelect = function(uiSelectController) {
            uiSelectController.isActive = function(item) {
                return false;
            };
        };

        $scope.removeSelectedItem = function(item) {
            $scope.selectedItems.splice($scope.selectedItems.indexOf(item), 1);
            $scope.selectedElement = null;
            $scope.searchInputKeypress(null, '');
        };

        $scope.getClassForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
        };

        $scope.getLetterForElement = function(index) {
            var wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
            return String.fromCharCode('a'.charCodeAt() + wrappedIndex).toUpperCase();
        };

        $scope.isItemInSelectDisabled = function(item) {
            return $scope.selectedItems.find(function(currentItem) {
                return currentItem.customization.code === item.customization.code;
            });
        };

        $scope.isItemSelected = function(item) {
            return $scope.selectedItems.find(function(currentItem) {
                return currentItem.customization.code === item.customization.code && currentItem.variation.code === item.variation.code;
            });
        };

        $scope.customizationFilter = {
            name: ''
        };

        $scope.searchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            $scope.pagination.reset();
            $scope.customizationFilter.name = searchObj;
            customizationDataFactory.resetData();
            $scope.addMoreItems();
        };

        var buttonHandlerFn = function(buttonId) {
            var deferred = $q.defer();
            if (buttonId === 'confirmOk') {
                var combinedView = personalizationsmarteditContextService.getCombinedView();
                combinedView.selectedItems = $scope.selectedItems;
                personalizationsmarteditContextService.setCombinedView(combinedView);
                return deferred.resolve();
            }
            return deferred.reject();
        };
        $scope.modalManager.setButtonHandler(buttonHandlerFn);

    });
