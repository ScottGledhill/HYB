angular.module('personalizationsmarteditManagerModule')
    .controller('personalizationsmarteditManagerTargetGrpController', function($scope, isBlank, $filter, personalizationsmarteditUtils, personalizationsmarteditManagementService, personalizationsmarteditMessageHandler, confirmationModalService, CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY, PERSONALIZATION_MODEL_STATUS_CODES) {

        var getSegmentGroupByForCriteria = function(allSegmentsSelected) {
            return allSegmentsSelected ? CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY.CRITERIA_AND : CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY.CRITERIA_OR;
        };

        var getSegmentsFilterObject = function() {
            return {
                code: $scope.segmentFilter.code,
                pageSize: $scope.segmentPagination.count,
                currentPage: $scope.segmentPagination.page + 1
            };
        };

        var resetPagination = function(pagination) {
            pagination.count = 10;
            pagination.page = -1;
            pagination.totalPages = 1;
        };

        $scope.temporarySelectedSegments = [];
        $scope.segmentPagination = {};
        resetPagination($scope.segmentPagination);
        $scope.segmentFilter = {
            code: ''
        };

        $scope.addVariationClick = function() {
            $scope.customization.variations.push({
                code: $scope.edit.code,
                name: $scope.edit.name,
                enabled: true,
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                triggers: [{
                    type: 'segmentTriggerData',
                    groupBy: getSegmentGroupByForCriteria($scope.edit.allSegmentsChecked),
                    segments: $scope.temporarySelectedSegments
                }],
                allSegmentsCriteria: $scope.edit.allSegmentsChecked,
                rank: $scope.customization.variations.length,
                isNew: true
            });

            $scope.edit.name = '';
            $scope.temporarySelectedSegments = [];
        };

        $scope.submitChangesClick = function() {
            angular.copy($scope.temporarySelectedSegments, $scope.edit.selectedSegments);
            $scope.edit.selectedVariation.triggers = $scope.edit.selectedVariation.triggers || [];
            var segmentTriggerArr = $scope.edit.selectedVariation.triggers.filter(function(trigger) {
                return trigger.type === "segmentTriggerData";
            });

            if (segmentTriggerArr.length === 0 && $scope.edit.selectedSegments.length > 0) {
                $scope.edit.selectedVariation.triggers.push({
                    type: 'segmentTriggerData',
                    groupBy: '',
                    segments: []
                });
            }

            $scope.edit.selectedVariation.name = $scope.edit.name;
            var triggerSegment = personalizationsmarteditUtils.getSegmentTriggerForVariation($scope.edit.selectedVariation);
            triggerSegment.segments = $scope.edit.selectedSegments;
            triggerSegment.groupBy = getSegmentGroupByForCriteria($scope.edit.allSegmentsChecked);
            $scope.edit.selectedVariation.allSegmentsCriteria = $scope.edit.allSegmentsChecked;
            $scope.edit.selectedVariation = undefined;
            $scope.temporarySelectedSegments = [];
        };

        $scope.cancelChangesClick = function() {
            $scope.edit.selectedVariation = undefined;
            $scope.temporarySelectedSegments = [];
        };

        $scope.removeVariationClick = function(variation) {
            confirmationModalService.confirm({
                description: 'personalization.modal.manager.targetgrouptab.deletevariation.content'
            }).then(function() {
                if (variation.isNew) {
                    $scope.customization.variations.splice($scope.customization.variations.indexOf(variation), 1);
                } else {
                    variation.status = "DELETED";
                }
                $scope.edit.selectedVariation = undefined;
            });
        };

        $scope.setVariationRank = function(variation, increaseValue, $event, firstOrLast) {
            if (firstOrLast) {
                $event.stopPropagation();
            } else {
                var from = $scope.customization.variations.indexOf(variation);
                var to = personalizationsmarteditUtils.getValidRank($scope.customization.variations, variation, increaseValue);
                var variationsArr = $scope.customization.variations;
                if (to >= 0 && to < variationsArr.length) {
                    variationsArr.splice(to, 0, variationsArr.splice(from, 1)[0]);
                    $scope.recalculateRanksForVariations();
                }
            }
        };

        $scope.toogleVariationActive = function(variation) {
            variation.enabled = !variation.enabled;
            variation.status = variation.enabled ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        };

        $scope.recalculateRanksForVariations = function() {
            $scope.customization.variations.forEach(function(part, index) {
                $scope.customization.variations[index].rank = index;
            });
        };

        $scope.canShowVariationSegmentationCriteria = function() {
            return $scope.temporarySelectedSegments && $scope.temporarySelectedSegments.length > 1;
        };

        $scope.editVariationAction = function(variation) {
            $scope.edit.selectedVariation = variation;
            angular.copy(personalizationsmarteditUtils.getSegmentTriggerForVariation(variation).segments, $scope.temporarySelectedSegments);
        };

        $scope.isSelected = function(variation) {
            if (angular.isUndefined(variation))
                return false;
            return angular.equals(variation, $scope.edit.selectedVariation);
        };

        $scope.canSaveVariation = function() {
            return $scope.temporarySelectedSegments && $scope.temporarySelectedSegments.length > 0 && !isBlank($scope.edit.name);
        };


        $scope.isVariationSelected = function() {
            return angular.isDefined($scope.edit.selectedVariation);
        };

        $scope.getSegmentCodesStrForVariation = function(variation) {
            var segments = personalizationsmarteditUtils.getSegmentTriggerForVariation(variation).segments || [];
            var segmentCodes = segments.map(function(elem) {
                return elem.code;
            }).filter(function(elem) {
                return typeof elem !== 'undefined';
            });
            return segmentCodes.join(", ");
        };

        $scope.getSegmentLenghtForVariation = function(variation) {
            var segments = personalizationsmarteditUtils.getSegmentTriggerForVariation(variation).segments || [];
            return segments.length;
        };

        $scope.getCriteriaDescrForVariation = function(variation) {
            var segmentTrigger = personalizationsmarteditUtils.getSegmentTriggerForVariation(variation);
            if (segmentTrigger.groupBy === CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY.CRITERIA_AND) {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.allsegments');
            } else {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.anysegments');
            }
        };

        $scope.getEnablementTextForVariation = function(variation) {
            return personalizationsmarteditUtils.getEnablementTextForVariation(variation, 'personalization.modal.customizationvariationmanagement.targetgrouptab');
        };

        $scope.getActivityActionTextForVariation = function(variation) {
            if (variation.enabled) {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.disable');
            } else {
                return $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.enable');
            }
        };

        $scope.getActivityStateForCustomization = function(customization) {
            return personalizationsmarteditUtils.getActivityStateForCustomization(customization);
        };

        $scope.getActivityStateForVariation = function(customization, variation) {
            return personalizationsmarteditUtils.getActivityStateForVariation(customization, variation);
        };

        $scope.getCommerceCustomizationTooltip = function(variation) {
            var result = "";
            for (var property in variation.commerceCustomizations) {
                result += $filter('translate')('personalization.modal.customizationvariationmanagement.targetgrouptab.commercecustomization.' + property) + ": " + variation.commerceCustomizations[property] + "\n";
            }
            return result;
        };

        $scope.hasCommerceCustomization = function(variation) {
            return variation.numberOfCommerceActions > 0;
        };

        $scope.moreSegmentRequestProcessing = false;
        $scope.addMoreSegmentItems = function() {
            if ($scope.segmentPagination.page < $scope.segmentPagination.totalPages - 1 && !$scope.moreSegmentRequestProcessing) {
                $scope.moreSegmentRequestProcessing = true;
                personalizationsmarteditManagementService.getSegments(getSegmentsFilterObject()).then(function successCallback(response) {
                    Array.prototype.push.apply($scope.edit.segments, response.segments);
                    $scope.segmentPagination = response.pagination;
                    $scope.moreSegmentRequestProcessing = false;
                }, function errorCallback(response) {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingsegments'));
                    $scope.moreSegmentRequestProcessing = false;
                });
            }
        };

        $scope.segmentSearchInputKeypress = function(keyEvent, searchObj) {
            if (keyEvent && ([37, 38, 39, 40].indexOf(keyEvent.which) > -1)) { //keyleft, keyup, keyright, keydown
                return;
            }
            resetPagination($scope.segmentPagination);
            $scope.segmentFilter.code = searchObj;
            $scope.edit.segments.length = 0;
            $scope.addMoreSegmentItems();
        };

        $scope.segmentSelectedEvent = function(item, itemsToSelect) {
            $scope.temporarySelectedSegments.push(item);
            $scope.singleSegment = null;
            $scope.segmentSearchInputKeypress(null, '');
        };

        $scope.removeSelectedSegment = function(seg) {
            $scope.temporarySelectedSegments.splice($scope.temporarySelectedSegments.indexOf(seg), 1);
            $scope.singleSegment = null;
            $scope.segmentSearchInputKeypress(null, '');
        };

        $scope.isItemInSelectDisabled = function(item) {
            return $scope.temporarySelectedSegments.find(function(currentItem) {
                return currentItem.code === item.code;
            });
        };

        $scope.initUiSelect = function(uiSelectController) {
            uiSelectController.isActive = function(item) {
                return false;
            };
        };

        $scope.$watch('temporarySelectedSegments', function() {
            if (($scope.temporarySelectedSegments || []).length === 0)
                $scope.segmentSearchInputKeypress(null, '');
        }, true);

        $scope.$watch('customization.statusBoolean', function() {
            $scope.customization.status = $scope.customization.statusBoolean ? PERSONALIZATION_MODEL_STATUS_CODES.ENABLED : PERSONALIZATION_MODEL_STATUS_CODES.DISABLED;
        }, true);

        $scope.$watch('edit.selectedVariation', function() {
            if ($scope.isVariationSelected())
                angular.copy(personalizationsmarteditUtils.getSegmentTriggerForVariation($scope.edit.selectedVariation).segments, $scope.temporarySelectedSegments);
            else
                $scope.temporarySelectedSegments = [];
        }, true);

    });
