angular.module('personalizationsmarteditCommons', [
        'alertServiceModule', 'personalizationcommonsTemplates', 'languageServiceModule'
    ])
    .constant('PERSONALIZATION_DATE_FORMATS', {
        VIEW_DATE_FORMAT: 'L LT',
        MODEL_DATE_FORMAT: 'YYYY-MM-DDTHH:mm:SSZ'
    })
    .constant('PERSONALIZATION_MODEL_STATUS_CODES', {
        ENABLED: 'ENABLED',
        DISABLED: 'DISABLED'
    })
    .constant('PERSONALIZATION_VIEW_STATUS_MAPPING_CODES', {
        ALL: 'ALL',
        ENABLED: 'ENABLED',
        DISABLED: 'DISABLED'
    })
    .constant('PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING', {
        0: {
            borderClass: 'personalizationsmarteditComponentSelected0',
            listClass: 'personalizationsmarteditComponentSelectedList0'
        },
        1: {
            borderClass: 'personalizationsmarteditComponentSelected1',
            listClass: 'personalizationsmarteditComponentSelectedList1'
        },
        2: {
            borderClass: 'personalizationsmarteditComponentSelected2',
            listClass: 'personalizationsmarteditComponentSelectedList2'
        },
        3: {
            borderClass: 'personalizationsmarteditComponentSelected3',
            listClass: 'personalizationsmarteditComponentSelectedList3'
        },
        4: {
            borderClass: 'personalizationsmarteditComponentSelected4',
            listClass: 'personalizationsmarteditComponentSelectedList4'
        },
        5: {
            borderClass: 'personalizationsmarteditComponentSelected5',
            listClass: 'personalizationsmarteditComponentSelectedList5'
        },
        6: {
            borderClass: 'personalizationsmarteditComponentSelected6',
            listClass: 'personalizationsmarteditComponentSelectedList6'
        }
    })
    .run(function($rootScope, PERSONALIZATION_MODEL_STATUS_CODES) {
        $rootScope.PERSONALIZATION_MODEL_STATUS_CODES = PERSONALIZATION_MODEL_STATUS_CODES;
    })
    .filter('statusNotDeleted', function(personalizationsmarteditUtils) {
        return function(value) {
            if (angular.isArray(value)) {
                return personalizationsmarteditUtils.getVisibleItems(value);
            }
            return value;
        };
    })
    .factory('personalizationsmarteditUtils', function($filter, PERSONALIZATION_DATE_FORMATS, PERSONALIZATION_MODEL_STATUS_CODES, PERSONALIZATION_VIEW_STATUS_MAPPING_CODES) {
        var utils = {};

        utils.pushToArrayIfValueExists = function(array, key, value) {
            if (value) {
                array.push({
                    "key": key,
                    "value": value
                });
            }
        };

        utils.getContainerIdForElement = function(element) {
            var container = element.closest('[data-smartedit-container-id][data-smartedit-container-type="CxCmsComponentContainer"]');
            if (container.length) {
                return container.data().smarteditContainerId;
            }
            return null;
        };

        utils.getContainerIdForComponent = function(componentType, componentId) {
            var element = angular.element('[data-smartedit-component-id="' + componentId + '"][data-smartedit-component-type="' + componentType + '"]');
            if (angular.isArray(element)) {
                element = element[0];
            }
            return utils.getContainerIdForElement(element);
        };

        utils.getSlotIdForElement = function(element) {
            var slot = element.closest('[data-smartedit-component-type="ContentSlot"]');
            if (slot.length) {
                return slot.data().smarteditComponentId;
            }
            return null;
        };

        utils.getSlotIdForComponent = function(componentType, componentId) {
            var element = angular.element('[data-smartedit-component-id="' + componentId + '"][data-smartedit-component-type="' + componentType + '"]');
            if (angular.isArray(element)) {
                element = element[0];
            }
            return utils.getSlotIdForElement(element);
        };

        utils.getVariationCodes = function(variations) {
            if ((typeof variations === 'undefined') || (variations === null)) {
                return [];
            }
            var allVariationsCodes = variations.map(function(elem) {
                return elem.code;
            }).filter(function(elem) {
                return typeof elem !== 'undefined';
            });
            return allVariationsCodes;
        };

        utils.getPageId = function() {
            return /page\-([\w]+)/.exec($('iframe').contents().find('body').attr('class'))[1];
        };

        utils.getVariationKey = function(customizationId, variations) {
            if (customizationId === undefined || variations === undefined) {
                return [];
            }

            var allVariationsKeys = variations.map(function(elem) {
                return elem.code;
            }).filter(function(elem) {
                return typeof elem !== 'undefined';
            }).map(function(variationId) {
                return {
                    "variationCode": variationId,
                    "customizationCode": customizationId
                };
            });
            return allVariationsKeys;
        };

        utils.getSegmentTriggerForVariation = function(variation) {
            var triggers = variation.triggers || [];
            var segmentTriggerArr = triggers.filter(function(trigger) {
                return trigger.type === "segmentTriggerData";
            });

            if (segmentTriggerArr.length === 0) {
                return {};
            }

            return segmentTriggerArr[0];
        };

        utils.isPersonalizationItemEnabled = function(item) {
            return item.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
        };

        utils.getEnablementTextForCustomization = function(customization, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";
            if (utils.isPersonalizationItemEnabled(customization)) {
                return $filter('translate')(keyPrefix + '.customization.enabled');
            } else {
                return $filter('translate')(keyPrefix + '.customization.disabled');
            }
        };

        utils.getEnablementTextForVariation = function(variation, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";

            if (utils.isPersonalizationItemEnabled(variation)) {
                return $filter('translate')(keyPrefix + '.variation.enabled');
            } else {
                return $filter('translate')(keyPrefix + '.variation.disabled');
            }
        };

        utils.getEnablementActionTextForVariation = function(variation, keyPrefix) {
            keyPrefix = keyPrefix || "personalization";

            if (utils.isPersonalizationItemEnabled(variation)) {
                return $filter('translate')(keyPrefix + '.variation.options.disable');
            } else {
                return $filter('translate')(keyPrefix + '.variation.options.enable');
            }
        };

        utils.getActivityStateForCustomization = function(customization) {
            if (customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED) {
                if (moment().isBetween(new Date(customization.enabledStartDate), new Date(customization.enabledEndDate), 'minute', '[]')) {
                    return "status-active";
                } else {
                    return "status-ignore";
                }
            } else {
                return "status-inactive";
            }
        };

        utils.getActivityStateForVariation = function(customization, variation) {
            if (variation.enabled) {
                return utils.getActivityStateForCustomization(customization);
            } else {
                return "status-inactive";
            }
        };

        utils.formatDate = function(dateStr, format) {
            format = format || PERSONALIZATION_DATE_FORMATS.VIEW_DATE_FORMAT;
            if (dateStr) {
                if (dateStr.match && dateStr.match(/^(\d{4})\-(\d{2})\-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\+|\-)(\d{4})$/)) {
                    dateStr = dateStr.slice(0, -2) + ":" + dateStr.slice(-2);
                }
                return moment(new Date(dateStr)).format(format);
            } else {
                return "";
            }
        };

        var isItemVisible = function(item) {
            return item.status != 'DELETED';
        };

        utils.getVisibleItems = function(items) {
            return items.filter(function(item) {
                return isItemVisible(item);
            });
        };

        utils.getValidRank = function(items, item, increaseValue) {
            var from = items.indexOf(item);
            var delta = increaseValue < 0 ? -1 : 1;

            var increase = from + increaseValue;

            while (increase >= 0 && increase < items.length && !isItemVisible(items[increase])) {
                increase += delta;
            }

            increase = increase >= items.length ? items.length - 1 : increase;
            increase = increase < 0 ? 0 : increase;

            return items[increase].rank;
        };

        utils.getStatusesMapping = function() {
            var statusesMapping = [];

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
                text: 'personalization.context.status.all',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.ENABLED, PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
            });

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ENABLED,
                text: 'personalization.context.status.enabled',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.ENABLED]
            });

            statusesMapping.push({
                code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.DISABLED,
                text: 'personalization.context.status.disabled',
                modelStatuses: [PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
            });

            return statusesMapping;
        };

        utils.clearContext = function(iFrameUtils, contexService) {
            iFrameUtils.clearAndReloadPreview(contexService.selectedVariations);
            contexService.selectedCustomizations = null;
            contexService.selectedVariations = null;
            contexService.selectedComponents = null;
            contexService.applySynchronization();
        };

        utils.clearCombinedView = function(iFrameUtils, contexService) {
            if (contexService.combinedView.enabled && contexService.combinedView.selectedItems) {
                iFrameUtils.clearAndReloadPreview({});
            }
            contexService.combinedView.enabled = false;
            contexService.combinedView.selectedItems = null;
            contexService.applySynchronization();
        };

        return utils;
    })
    .factory('personalizationsmarteditMessageHandler', function(alertService) {
        var sendMessage = function(message, isSuccessful) {
            alertService.pushAlerts([{
                successful: isSuccessful,
                message: message
            }]);
        };

        var messageHandler = {};
        messageHandler.sendInformation = function(informationMessage) {
            sendMessage(informationMessage, true);
        };

        messageHandler.sendError = function(errorMessage) {
            sendMessage(errorMessage, false);
        };

        messageHandler.send = function(messagesArray) {
            alertService.pushAlerts(messagesArray);
        };

        messageHandler.buildMessage = function(message, isSuccessful) {
            return {
                successful: isSuccessful,
                message: message
            };
        };

        return messageHandler;
    }).factory('personalizationsmarteditCommerceCustomizationService', function() {
        var nonCommerceActionTypes = ['cxCmsActionData'];

        var ccService = {};
        var types = [];

        var isNonCommerceAction = function(action) {
            return nonCommerceActionTypes.some(function(val) {
                return val == action.type;
            });
        };

        var isCommerceAction = function(action) {
            return !isNonCommerceAction(action);
        };

        var isTypeEnabled = function(type, seConfigurationData) {
            if (seConfigurationData !== undefined && seConfigurationData !== null) {
                if (seConfigurationData[type.confProperty] === true)
                    return true;
            }
            return false;
        };

        ccService.registerType = function(item) {
            var type = item.type;
            var exists = false;

            types.forEach(function(val) {
                if (val.type === type) {
                    exists = true;
                }
            });

            if (!exists) {
                types.push(item);
            }
        };

        ccService.getAvailableTypes = function(seConfigurationData) {
            return types.filter(function(item) {
                return isTypeEnabled(item, seConfigurationData);
            });
        };

        ccService.isCommerceCustomizationEnabled = function(seConfigurationData) {
            var at = ccService.getAvailableTypes(seConfigurationData);
            return at.length > 0;
        };

        ccService.getNonCommerceActionsCount = function(variation) {
            return (variation.actions || []).filter(isNonCommerceAction).length;
        };

        ccService.getCommerceActionsCountMap = function(variation) {
            var result = {};

            (variation.actions || [])
            .filter(isCommerceAction)
                .forEach(function(action) {
                    var typeKey = action.type.toLowerCase();

                    var count = result[typeKey];
                    if (count === undefined) {
                        count = 1;
                    } else {
                        count += 1;
                    }
                    result[typeKey] = count;
                });

            return result;
        };

        ccService.getCommerceActionsCount = function(variation) {
            return (variation.actions || [])
                .filter(isCommerceAction).length;
        };

        return ccService;
    }).directive('dateTimePickerRange', function($rootScope, $timeout, languageService, personalizationsmarteditUtils) {
        return {
            templateUrl: 'web/features/personalizationcommons/dateTimePickerRange/dateTimePickerRangeTemplate.html',
            restrict: 'E',
            transclude: true,
            replace: false,
            scope: {
                name: '=',
                dateFrom: '=',
                dateTo: '=',
                isEditable: '=',
                dateFormat: '='
            },
            link: function($scope, elem) {
                $scope.placeholderText = 'personalization.commons.datetimepicker.placeholder';

                if ($scope.isEditable) {

                    $scope.getDateOrDefault = function(date) {
                        if (date && Date.parse(date)) {
                            return moment(new Date(date));
                        } else {
                            return false;
                        }
                    };

                    $scope.fromDp = $(elem.children()[0].querySelector('#date-picker-range-from'))
                        .datetimepicker({
                            format: $scope.dateFormat || 'L LT',
                            showClear: true,
                            showClose: true,
                            maxDate: $scope.getDateOrDefault($scope.dateTo),
                            locale: languageService.getBrowserLocale().split('-')[0]
                        });

                    $scope.toDp = $(elem.children()[1].querySelector('#date-picker-range-to'))
                        .datetimepicker({
                            format: $scope.dateFormat || 'L LT',
                            showClear: true,
                            showClose: true,
                            useCurrent: false,
                            minDate: $scope.getDateOrDefault($scope.dateFrom),
                            locale: languageService.getBrowserLocale().split('-')[0]
                        });

                    $scope.fromDp.on('dp.change', function(e) {
                        $scope.dateFrom = personalizationsmarteditUtils.formatDate(e.date, $scope.dateFormat);
                        $timeout((function() {
                            $rootScope.$digest();
                        }), 0);
                    });

                    $scope.toDp.on('dp.change', function(e) {
                        $scope.dateTo = personalizationsmarteditUtils.formatDate(e.date, $scope.dateFormat);
                        $timeout((function() {
                            $rootScope.$digest();
                        }), 0);
                    });

                    $scope.$watch('dateFrom', function() {
                        $scope.toDp.data("DateTimePicker").minDate($scope.getDateOrDefault($scope.dateFrom));
                    }, true);

                    $scope.$watch('dateTo', function() {
                        $scope.fromDp.data("DateTimePicker").maxDate($scope.getDateOrDefault($scope.dateTo));
                    }, true);
                }

            }
        };
    })
    .directive('isinthefuture', ['isBlank', function(isBlank) {
        return {
            restrict: "A",
            require: "ngModel",
            scope: false,
            link: function(scope, element, attributes, ctrl) {
                ctrl.$validators.isinthefuture = function(modelValue) {
                    if (isBlank(modelValue)) {
                        return true;
                    } else {
                        return moment(new Date(modelValue)).isAfter();
                    }
                };
            }
        };
    }])
    //To remove when angular-ui-select would be upgraded to version > 0.19
    .directive('uisOpenClose', ['$parse', '$timeout', function($parse, $timeout) {
        return {
            restrict: 'A',
            require: 'uiSelect',
            link: function(scope, element, attrs, $select) {
                $select.onOpenCloseCallback = $parse(attrs.uisOpenClose);

                scope.$watch('$select.open', function(isOpen, previousState) {
                    if (isOpen !== previousState) {
                        $timeout(function() {
                            $select.onOpenCloseCallback(scope, {
                                isOpen: isOpen
                            });
                        });
                    }
                });
            }
        };
    }])
    .directive('negate', [
        function() {
            return {
                require: 'ngModel',
                link: function(scope, element, attribute, ngModelController) {
                    ngModelController.$isEmpty = function(value) {
                        return !!value;
                    };

                    ngModelController.$formatters.unshift(function(value) {
                        return !value;
                    });

                    ngModelController.$parsers.unshift(function(value) {
                        return !value;
                    });
                }
            };
        }
    ]);
