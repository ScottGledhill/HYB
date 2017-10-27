angular.module('personalizationsmarteditPageCustomizationsToolbarItemModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditPreviewServiceModule',
        'personalizationsmarteditManagerModule',
        'personalizationsmarteditDataFactory'
    ])
    .directive('personalizationsmarteditPageCustomizationsToolbarItem', function() {
        return {
            templateUrl: 'web/features/personalizationsmarteditcontainer/pageCustomizationsToolbar/personalizationsmarteditPageCustomizationsToolbarItemTemplate.html',
            restrict: 'E',
            transclude: true,
            link: function(scope, elem, attrs) {
                //none
            }
        };
    })
    .controller('personalizationsmarteditPageCustomizationsToolbarController', function($scope, $filter, $timeout, personalizationsmarteditRestService, personalizationsmarteditContextService, personalizationsmarteditMessageHandler, personalizationsmarteditPreviewService, personalizationsmarteditUtils, personalizationsmarteditIFrameUtils, personalizationsmarteditManager, personalizationsmarteditCommerceCustomizationService, PERSONALIZATION_MODEL_STATUS_CODES, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES, customizationDataFactory, PaginationHelper) {
        $scope.closeCustomizeDropdowns = personalizationsmarteditContextService.closeCustomizeDropdowns;
        $scope.statuses = personalizationsmarteditUtils.getStatusesMapping();
        customizationDataFactory.resetData();

        $scope.search = {
            pageId: personalizationsmarteditContextService.getPageId(),
            libraryCustomizations: [],
            selectedLibraryCustomizations: [],
            searchCustomizationEnabled: false,
            status: $scope.statuses.filter(function(elem) {
                return elem.code === PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL;
            })[0]
        };

        var removeArrayFromArrayByCode = function(fromArray, toRemoveArray) {
            var filteredArray = fromArray.filter(function(elem) {
                return toRemoveArray.map(function(e) {
                    return e.code;
                }).indexOf(elem.code) < 0;
            });

            return filteredArray;
        };

        $scope.customizationsOnPage = customizationDataFactory.items;

        var errorCallback = function(response) {
            personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
            $scope.moreCustomizationsRequestProcessing = false;
        };

        var successCallback = function(response) {
            $scope.pagination = new PaginationHelper(response.pagination);
            $scope.search.libraryCustomizations.length = 0;
            $scope.libraryCustPagination.reset();
            $scope.addMoreLibraryCustomizationItems();
            $scope.moreCustomizationsRequestProcessing = false;

            //Update contex, because value could change name
            var currentCustomizations = personalizationsmarteditContextService.selectedCustomizations;
            var currentVariations = personalizationsmarteditContextService.selectedVariations;
            if (angular.isObject(currentVariations) && !angular.isArray(currentVariations)) {
                var newCustomization = $scope.customizationsOnPage.find(function(elem) {
                    return angular.equals(elem.code, currentCustomizations.code);
                });
                var newVariation = newCustomization.variations.find(function(elem) {
                    return angular.equals(elem.code, currentVariations.code);
                });
                $scope.variationClick(newCustomization, newVariation);
            }
            if (angular.isObject(currentVariations) && angular.isArray(currentVariations)) {
                var newFoundCustomization = $scope.customizationsOnPage.find(function(elem) {
                    return angular.equals(elem.code, currentCustomizations.code);
                });
                $scope.customizationClick(newFoundCustomization);
            }

        };

        var getCustomizations = function(categoryFilter) {
            var params = {
                filter: categoryFilter,
                dataArrayName: 'customizations'
            };
            customizationDataFactory.updateData(params, successCallback, errorCallback);
        };

        var getAndSetComponentsForVariation = function(customizationId, variationId) {
            personalizationsmarteditRestService.getComponenentsIdsForVariation(customizationId, variationId).then(function successCallback(response) {
                personalizationsmarteditContextService.setSelectedComponents(response.components);
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcomponentsforvariation'));
            });
        };

        var updatePreviewTicket = function(customizationId, variationArray) {
            var previewTicketId = personalizationsmarteditContextService.getSePreviewData().previewTicketId;
            var variationKeys = personalizationsmarteditUtils.getVariationKey(customizationId, variationArray);
            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicketId, variationKeys).then(function successCallback() {
                var previewData = personalizationsmarteditContextService.getSePreviewData();
                personalizationsmarteditIFrameUtils.reloadPreview(previewData.resourcePath, previewData.previewTicketId);
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.updatingpreviewticket'));
            });
        };

        var getCustomizationsFilterObject = function() {
            return {
                pageId: personalizationsmarteditContextService.getPageId(),
                currentSize: $scope.pagination.count,
                currentPage: $scope.pagination.page + 1,
                statuses: $scope.search.status.modelStatuses
            };
        };

        var getCustomizationsFilterObjectForLibrary = function() {
            return {
                pageId: personalizationsmarteditContextService.getPageId(),
                negatePageId: true,
                name: $scope.libraryCustomizationFilter.name,
                currentSize: $scope.libraryCustPagination.count,
                currentPage: $scope.libraryCustPagination.page + 1,
                statuses: $scope.search.status.modelStatuses
            };
        };

        var updateCustomizationData = function(customization) {
            var filter = {
                includeActions: true
            };

            personalizationsmarteditRestService.getVariationsForCustomization(customization.code, filter).then(
                function successCallback(response) {
                    customization.variations = response.variations || [];
                    customization.variations.forEach(function(variation) {
                        variation.numberOfCommerceActions = personalizationsmarteditCommerceCustomizationService.getCommerceActionsCount(variation);
                    });
                },
                function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomization'));
                });
        };

        $scope.libraryCustomizationFilter = {
            name: ''
        };

        $scope.libraryCustPagination = new PaginationHelper();
        $scope.libraryCustPagination.reset();
        $scope.moreLibraryCustomizationsRequestProcessing = false;

        $scope.addMoreLibraryCustomizationItems = function() {
            if ($scope.libraryCustPagination.page < $scope.libraryCustPagination.totalPages - 1 && !$scope.moreLibraryCustomizationsRequestProcessing) {
                $scope.moreLibraryCustomizationsRequestProcessing = true;
                personalizationsmarteditRestService.getCustomizations(getCustomizationsFilterObjectForLibrary()).then(function successCallback(response) {
                    var filteredCategories = removeArrayFromArrayByCode(response.customizations, $scope.customizationsOnPage);
                    filteredCategories.forEach(function(customization) {
                        customization.fromLibrary = true;
                    });
                    Array.prototype.push.apply($scope.search.libraryCustomizations, filteredCategories);

                    $scope.libraryCustPagination = new PaginationHelper(response.pagination);
                    $scope.moreLibraryCustomizationsRequestProcessing = false;
                }, function errorCallback(response) {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingcustomizations'));
                    $scope.moreLibraryCustomizationsRequestProcessing = false;
                });
            }
        };

        $scope.customizationSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            $scope.libraryCustPagination.reset();
            $scope.libraryCustomizationFilter.name = searchObj;
            $scope.search.libraryCustomizations.length = 0;
            $scope.addMoreLibraryCustomizationItems();
        };

        var refreshGrid = function() {
            $timeout(function() {
                getCustomizations(getCustomizationsFilterObject());
            }, 0);
        };

        $scope.variationClick = function(customization, variation) {
            personalizationsmarteditContextService.setSelectedCustomizations(customization);
            personalizationsmarteditContextService.setSelectedVariations(variation);
            getAndSetComponentsForVariation(customization.code, variation.code);
            updatePreviewTicket(customization.code, [variation]);
        };

        $scope.customizationClick = function(customization) {
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            var currentVariations = personalizationsmarteditContextService.selectedVariations;
            updateCustomizationData(customization);
            var visibleVariations = getVisibleVariations(customization);
            personalizationsmarteditContextService.setSelectedCustomizations(customization);
            personalizationsmarteditContextService.setSelectedVariations(visibleVariations);
            if (visibleVariations.length > 0) {
                var allVariations = personalizationsmarteditUtils.getVariationCodes(visibleVariations).join(",");
                getAndSetComponentsForVariation(customization.code, allVariations);
            }

            if ((angular.isObject(currentVariations) && !angular.isArray(currentVariations)) || combinedView.enabled) {
                updatePreviewTicket();
            }

            combinedView.enabled = false;
            personalizationsmarteditContextService.setCombinedView(combinedView);

            $scope.customizationsOnPage.filter(function(cust) {
                return customization.name !== cust.name;
            }).forEach(function(cust, index) {
                cust.collapsed = true;
            });
        };

        $scope.addCustomizationFromLibrary = function() {
            customizationDataFactory.pushData($scope.search.selectedLibraryCustomizations);
            $scope.search.libraryCustomizations = removeArrayFromArrayByCode($scope.search.libraryCustomizations, $scope.search.selectedLibraryCustomizations);
            $scope.search.selectedLibraryCustomizations = [];
            $scope.toggleAddMoreCustomizationsClick();
        };

        $scope.toggleAddMoreCustomizationsClick = function() {
            $scope.search.searchCustomizationEnabled = !$scope.search.searchCustomizationEnabled;
        };

        $scope.clearAllSubMenu = function() {
            angular.forEach($scope.customizationsOnPage, function(customization) {
                customization.subMenu = false;
            });
        };

        $scope.customizationSubMenuAction = function(customization) {
            if (!customization.subMenu) {
                $scope.clearAllSubMenu();
            }
            customization.subMenu = !customization.subMenu;
        };

        $scope.editCustomizationAction = function(customization) {
            personalizationsmarteditManager.openEditCustomizationModal(customization.code);
        };

        $scope.initCustomization = function(customization) {
            customization.collapsed = true;
            if ((personalizationsmarteditContextService.selectedCustomizations || {}).code === customization.code) {
                customization.collapsed = false;
            }
        };

        $scope.getEnablementTextForCustomization = function(customization) {
            var activityStr = personalizationsmarteditUtils.getEnablementTextForCustomization(customization, 'personalization.toolbar.pagecustomizations');
            return activityStr;
        };

        $scope.getEnablementTextForVariation = function(variation) {
            var activityStr = personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.toolbar.pagecustomizations');
            return activityStr;
        };

        $scope.isEnabled = function(customization) {
            return personalizationsmarteditUtils.isPersonalizationItemEnabled(customization);
        };

        $scope.isEnabled = function(variation) {
            return personalizationsmarteditUtils.isPersonalizationItemEnabled(variation);
        };

        $scope.getDatesForCustomization = function(customization) {
            var activityStr = "";
            var startDateStr = "";
            var endDateStr = "";

            if (customization.enabledStartDate || customization.enabledEndDate) {
                startDateStr = getFormattedDateForCustomization(customization.enabledStartDate);
                endDateStr = getFormattedDateForCustomization(customization.enabledEndDate);
                if (!customization.enabledStartDate) {
                    startDateStr = " ...";
                }
                if (!customization.enabledEndDate) {
                    endDateStr = "... ";
                }
                activityStr += " (" + startDateStr + " - " + endDateStr + ") ";
            }
            return activityStr;
        };


        $scope.getActivityStateForCustomization = function(customization) {
            return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
        };

        $scope.getActivityStateForVariation = function(customization, variation) {
            return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
        };

        var getVisibleVariations = function(customization) {
            return personalizationsmarteditUtils.getVisibleItems(customization.variations);
        };

        var getFormattedDateForCustomization = function(date) {
            if (date) {
                return personalizationsmarteditUtils.formatDate(date, "L");
            } else {
                return $filter('translate')('personalization.toolbar.pagecustomizations.nodatespecified');
            }
        };

        $scope.getSelectedVariationClass = function(variation) {
            if (angular.equals(variation.code, (personalizationsmarteditContextService.selectedVariations || {}).code)) {
                return "selectedVariation";
            }
        };

        $scope.isCommerceCustomization = function(variation) {
            return variation.numberOfCommerceActions > 0;
        };

        $scope.pagination = new PaginationHelper();
        $scope.pagination.reset();

        $scope.moreCustomizationsRequestProcessing = false;
        $scope.addMoreCustomizationItems = function() {
            if ($scope.pagination.page < $scope.pagination.totalPages - 1 && !$scope.moreCustomizationsRequestProcessing) {
                $scope.moreCustomizationsRequestProcessing = true;
                refreshGrid();
            }
        };

        $scope.$watch('search.status', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                $scope.pagination.reset();
                customizationDataFactory.resetData();
                $scope.addMoreCustomizationItems();
            }
        }, true);

        $scope.clearContext = function() {
            personalizationsmarteditUtils.clearContext(personalizationsmarteditIFrameUtils, personalizationsmarteditContextService);
        };

        $scope.libraryCustomizationsDropdownOpenClose = function(isOpen) {
            if (isOpen) {
                $timeout((function() {
                    angular.element("#dropdownCustomizationsStatus").controller('uiSelect').close();
                }), 0);
            }
        };

        $scope.statusCustomizationsDropdownOpenClose = function(isOpen) {
            if (isOpen) {
                $timeout((function() {
                    angular.element("#dropdownCustomizationsLibrary").controller('uiSelect').close();
                }), 0);
            }
        };

    });
