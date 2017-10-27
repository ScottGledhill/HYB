describe('personalizationsmarteditUtils', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var mockHtml = '<div data-smartedit-personalization-action-id="myAction" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div><div data-smartedit-container-id="myContainer" data-smartedit-container-type="CxCmsComponentContainer" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div>';
    var mockHtmlWithSlot = '<div data-smartedit-component-id="anotherIdSlot" data-smartedit-component-type="ContentSlot">' + mockHtml + '</div>';

    var personalizationsmarteditUtils, PERSONALIZATION_MODEL_STATUS_CODES;

    beforeEach(module('personalizationsmarteditCommons', function($provide) {
        var mockTranslateFilter = function(value) {
            return value;
        };
        $provide.value('translateFilter', mockTranslateFilter);
    }));
    beforeEach(inject(function(_personalizationsmarteditUtils_, _PERSONALIZATION_MODEL_STATUS_CODES_) {
        personalizationsmarteditUtils = _personalizationsmarteditUtils_;
        PERSONALIZATION_MODEL_STATUS_CODES = _PERSONALIZATION_MODEL_STATUS_CODES_;
    }));

    describe('pushToArrayIfValueExists', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.pushToArrayIfValueExists).toBeDefined();
        });

        it('adds to array only values that exists', function() {
            // given
            var testArray = [];
            // when
            personalizationsmarteditUtils.pushToArrayIfValueExists(testArray, 'myKey', 'myValue');
            personalizationsmarteditUtils.pushToArrayIfValueExists(testArray, 'myKey2', null);
            personalizationsmarteditUtils.pushToArrayIfValueExists(testArray, 'myKey3', undefined);
            personalizationsmarteditUtils.pushToArrayIfValueExists(testArray, 'myKey4', 'myValue4');
            // then
            expect(testArray.length).toEqual(2);
        });

    });

    describe('getContainerIdForElement', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getContainerIdForElement).toBeDefined();
        });

        it('should return container id if element exists', function() {
            // given
            var element = angular.element(mockHtml);
            // when
            var ret = personalizationsmarteditUtils.getContainerIdForElement(element);
            // then
            expect(ret).toBe("myContainer");
        });

        it('should return null if element doesnt exists', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditUtils.getContainerIdForElement(element);
            // then
            expect(ret).toBe(null);
        });

    });

    describe('getContainerIdForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getContainerIdForComponent).toBeDefined();
        });

        it('should return container id if element with specified id and type exists', function() {
            // given
            var backup = angular.element;
            angular.element = function(selector) {
                return backup(mockHtml);
            };
            // when
            var ret = personalizationsmarteditUtils.getContainerIdForComponent("myId", "myType");
            // then
            expect(ret).toBe("myContainer");
            angular.element = backup;
        });

        it('should return null if element with specified id and type not exist', function() {
            // when
            var ret = personalizationsmarteditUtils.getContainerIdForComponent("mockId", "mockType");
            // then
            expect(ret).toBe(null);
        });

    });

    describe('getSlotIdForElement', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getSlotIdForElement).toBeDefined();
        });

        it('should return value if element exist', function() {
            // given
            var element = angular.element(mockHtmlWithSlot);
            // when
            var ret = personalizationsmarteditUtils.getSlotIdForElement(element);
            // then
            expect(ret).toBe("anotherIdSlot");
        });

        it('should return null if element doesnt exist', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditUtils.getSlotIdForElement(element);
            // then
            expect(ret).toBe(null);
        });

    });

    describe('getSlotIdForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getSlotIdForComponent).toBeDefined();
        });

        it('should return slot id if element with specified id and type exists', function() {
            // given
            var backup = angular.element;
            angular.element = function(selector) {
                return backup(mockHtmlWithSlot);
            };
            // when
            var ret = personalizationsmarteditUtils.getSlotIdForComponent("myId", "myType");
            // then
            expect(ret).toBe("anotherIdSlot");
            angular.element = backup;
        });

        it('should return null if element with specified id and type not exist', function() {
            // when
            var ret = personalizationsmarteditUtils.getSlotIdForComponent("mockId", "mockType");
            // then
            expect(ret).toBe(null);
        });

    });

    describe('getVariationCodes', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getVariationCodes).toBeDefined();
        });

        it('should return proper codes for variations', function() {
            // given
            var correctArray = [{
                code: "first",
                value: "next"
            }, {
                code: "second",
                value: "none",
                link: "empty"
            }];
            var incorrectArray = [{
                link: "my link",
                value: "next"
            }, {
                connection: "second",
                value: "none",
                link: "empty"
            }];
            // when
            var callForEmpty = personalizationsmarteditUtils.getVariationCodes([]);
            var callForCorrect = personalizationsmarteditUtils.getVariationCodes(correctArray);
            var callForIncorrect = personalizationsmarteditUtils.getVariationCodes(incorrectArray);
            // then
            expect(callForEmpty.length).toBe(0);

            expect(callForCorrect.length).toBe(2);
            expect(callForCorrect).toContain('first');
            expect(callForCorrect).toContain('second');

            expect(callForIncorrect.length).toBe(0);
        });

    });

    describe('getPageId', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getPageId).toBeDefined();
        });

    });

    describe('getVariationKey', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getVariationKey).toBeDefined();
        });

        it('should return empty array if customization id is undefined', function() {
            expect(personalizationsmarteditUtils.getVariationKey(undefined, "variations")).toEqual([]);
        });

        it('should return empty array if variations are undefined', function() {
            expect(personalizationsmarteditUtils.getVariationKey('customizationId')).toEqual([]);
        });

        it('should return proper array if parameters are ok', function() {
            var mockVariations = [{
                code: "varTest1"
            }, {
                code: "varTest2"
            }];
            var mockResult = [{
                "variationCode": "varTest1",
                "customizationCode": "customizationId"
            }, {
                "variationCode": "varTest2",
                "customizationCode": "customizationId"
            }];

            expect(personalizationsmarteditUtils.getVariationKey('customizationId', mockVariations)).toEqual(mockResult);
        });

    });

    describe('getSegmentTriggerForVariation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getSegmentTriggerForVariation).toBeDefined();
        });

        it('should return empty object if no segmentTriggerData in variation', function() {
            var mockVariation = {
                code: "test",
                triggers: []
            };
            expect(personalizationsmarteditUtils.getSegmentTriggerForVariation(mockVariation)).toEqual({});
        });

        it('should return empty object if no segmentTriggerData in variation', function() {
            var mockSegment = {
                type: "segmentTriggerData",
                name: "testowy"
            };
            var mockTestTrigger = {
                type: "myType",
                name: "testName"
            };
            var mockVariation = {
                code: "test",
                triggers: [mockTestTrigger, mockSegment]
            };
            expect(personalizationsmarteditUtils.getSegmentTriggerForVariation(mockVariation)).toBe(mockSegment);
        });

    });

    describe('isPersonalizationItemEnabled', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.isPersonalizationItemEnabled).toBeDefined();
        });

        it('should return true if status for item is enabled', function() {
            var mockItem = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            };
            expect(personalizationsmarteditUtils.isPersonalizationItemEnabled(mockItem)).toBe(true);
        });

        it('should return false if status for item is disabled', function() {
            var mockItem = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            expect(personalizationsmarteditUtils.isPersonalizationItemEnabled(mockItem)).toBe(false);
        });

        it('should return false if status for item is other than enabled', function() {
            var mockItem = {
                status: "notImportantStatus"
            };
            expect(personalizationsmarteditUtils.isPersonalizationItemEnabled(mockItem)).toBe(false);
        });

    });

    describe('getEnablementTextForCustomization', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getEnablementTextForCustomization).toBeDefined();
        });

        it('should return key for disabled if customization is disabled', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            expect(personalizationsmarteditUtils.getEnablementTextForCustomization(mockCustomization, "myKey")).toBe("myKey.customization.disabled");
        });

        it('should return key for enabled if customization is enabled', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            };
            expect(personalizationsmarteditUtils.getEnablementTextForCustomization(mockCustomization, "myKey")).toBe("myKey.customization.enabled");
        });

    });

    describe('getEnablementTextForVariation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getEnablementTextForVariation).toBeDefined();
        });

        it('should return key for disabled if variation is disabled', function() {
            var mockVariation = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            expect(personalizationsmarteditUtils.getEnablementTextForVariation(mockVariation, "myKey")).toBe("myKey.variation.disabled");
        });

        it('should return key for enabled if variation is enabled', function() {
            var mockVariation = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            };
            expect(personalizationsmarteditUtils.getEnablementTextForVariation(mockVariation, "myKey")).toBe("myKey.variation.enabled");
        });

    });

    describe('getEnablementActionTextForVariation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getEnablementActionTextForVariation).toBeDefined();
        });

        it('should return key for disabled if variation is disabled', function() {
            var mockVariation = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            expect(personalizationsmarteditUtils.getEnablementActionTextForVariation(mockVariation, "myKey")).toBe("myKey.variation.options.enable");
        });

        it('should return key for enabled if variation is enabled', function() {
            var mockVariation = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED
            };
            expect(personalizationsmarteditUtils.getEnablementActionTextForVariation(mockVariation, "myKey")).toBe("myKey.variation.options.disable");
        });

    });

    describe('getActivityStateForCustomization', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getActivityStateForCustomization).toBeDefined();
        });

        it('should return active if customization is active and dates are proper', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledStartDate: '2010-10-10',
                enabledEndDate: '2110-11-11'
            };
            expect(personalizationsmarteditUtils.getActivityStateForCustomization(mockCustomization)).toBe("status-active");
        });

        it('should return ignore if customization is active but dates are incorrect', function() {
            var mockCustomization1 = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledStartDate: '2110-10-10',
            };
            var mockCustomization2 = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledEndDate: '2010-11-11'
            };
            expect(personalizationsmarteditUtils.getActivityStateForCustomization(mockCustomization1)).toBe("status-ignore");
            expect(personalizationsmarteditUtils.getActivityStateForCustomization(mockCustomization2)).toBe("status-ignore");
        });

        it('should return inactive if customization is disabled', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            expect(personalizationsmarteditUtils.getActivityStateForCustomization(mockCustomization)).toBe("status-inactive");
        });

    });

    describe('getActivityStateForVariation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getActivityStateForVariation).toBeDefined();
        });

        it('should return inactive if variation is disabled', function() {
            var mockCustomization = {};
            var mockVariation = {
                enabled: false
            };
            expect(personalizationsmarteditUtils.getActivityStateForVariation(mockCustomization, mockVariation)).toBe("status-inactive");
        });

        it('should return inactive if variation is enabled but customization is disabled', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            };
            var mockVariation = {
                enabled: true
            };
            expect(personalizationsmarteditUtils.getActivityStateForVariation(mockCustomization, mockVariation)).toBe("status-inactive");
        });

        it('should return active if variation is enabled and customization is enabled and dates for customization are proper', function() {
            var mockCustomization = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledStartDate: '2010-10-10',
                enabledEndDate: '2110-11-11'
            };
            var mockVariation = {
                enabled: true
            };
            expect(personalizationsmarteditUtils.getActivityStateForVariation(mockCustomization, mockVariation)).toBe("status-active");
        });

        it('should return ignore if variation is enabled and customization is enabled but dates for customization are incorrect', function() {
            var mockCustomization1 = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledStartDate: '2110-10-10',
            };
            var mockCustomization2 = {
                status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                enabledEndDate: '2010-11-11'
            };
            var mockVariation = {
                enabled: true
            };
            expect(personalizationsmarteditUtils.getActivityStateForVariation(mockCustomization1, mockVariation)).toBe("status-ignore");
            expect(personalizationsmarteditUtils.getActivityStateForVariation(mockCustomization2, mockVariation)).toBe("status-ignore");
        });

    });

    describe('formatDate', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.formatDate).toBeDefined();
        });

        it('should return empty string if date parameter not passed', function() {
            expect(personalizationsmarteditUtils.formatDate()).toBe("");
            expect(personalizationsmarteditUtils.formatDate(null, "YYYY-MM-DD")).toBe("");
        });

        it('should return date in format that was passed as argument', function() {
            var mockFormat = "YYYY-MM-DD";
            var mockDate = "2010-11-20T12:12:12";
            var formattedDate = personalizationsmarteditUtils.formatDate(mockDate, mockFormat);
            expect(formattedDate).toMatch(/^(\d{4})\-(\d{2})\-(\d{2})$/);
        });

    });

    describe('getVisibleItems', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getVisibleItems).toBeDefined();
        });

        it('should return list with visible items', function() {
            var mockItems = [{
                status: "test"
            }, {
                status: "DELETED"
            }, {
                status: "DELETED"
            }, {
                status: "VISIBLE"
            }, {
                status: "test"
            }];
            var visibleItems = personalizationsmarteditUtils.getVisibleItems(mockItems);
            expect(visibleItems.length).toBe(3);
        });

    });

    describe('getStatusesMapping', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getStatusesMapping).toBeDefined();
        });

        it('should return array with proper mapping', function() {
            expect(personalizationsmarteditUtils.getStatusesMapping().length).toBe(3);
        });

    });

    describe('clearContext', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.clearContext).toBeDefined();
        });

        it('should call proper functions in services and set properties to initial values', function() {
            // given
            var mockVariations = [{
                name: "1"
            }, {
                name: "2"
            }];
            var mockIFrameUtils = {
                clearAndReloadPreview: function(value) {
                    return '';
                }
            };
            var mockContexService = {
                applySynchronization: function() {
                    return;
                },
                selectedVariations: mockVariations
            };
            spyOn(mockIFrameUtils, 'clearAndReloadPreview');
            spyOn(mockContexService, 'applySynchronization');
            // when
            personalizationsmarteditUtils.clearContext(mockIFrameUtils, mockContexService);

            // then
            expect(mockIFrameUtils.clearAndReloadPreview).toHaveBeenCalled();
            expect(mockIFrameUtils.clearAndReloadPreview).toHaveBeenCalledWith(mockVariations);
            expect(mockContexService.applySynchronization).toHaveBeenCalled();
            expect(mockContexService.selectedCustomizations).toBe(null);
            expect(mockContexService.selectedVariations).toBe(null);
            expect(mockContexService.selectedComponents).toBe(null);
        });

    });

    describe('clearCombinedView', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.clearCombinedView).toBeDefined();
        });

        it('should call proper functions in services and set properties to initial values', function() {
            // given
            var mockCombinedView = {
                enabled: true,
                selectedItems: []
            };
            var mockIFrameUtils = {
                clearAndReloadPreview: function(value) {
                    return '';
                }
            };
            var mockContexService = {
                applySynchronization: function() {
                    return;
                },
                combinedView: mockCombinedView
            };
            spyOn(mockIFrameUtils, 'clearAndReloadPreview');
            spyOn(mockContexService, 'applySynchronization');
            // when
            personalizationsmarteditUtils.clearCombinedView(mockIFrameUtils, mockContexService);

            // then
            expect(mockIFrameUtils.clearAndReloadPreview).toHaveBeenCalled();
            expect(mockIFrameUtils.clearAndReloadPreview).toHaveBeenCalledWith({});
            expect(mockContexService.applySynchronization).toHaveBeenCalled();
            expect(mockContexService.combinedView.enabled).toBe(false);
            expect(mockContexService.combinedView.selectedItems).toBe(null);
        });

    });

    describe('getValidRank', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditUtils.getValidRank).toBeDefined();
        });

        it('should return proper rank even if some variations are in deleted state', function() {
            // given
            var variation1 = {
                status: 'DELETED',
                rank: 0
            };

            var variation2 = {
                status: 'DELETED',
                rank: 1
            };

            var variation3 = {
                status: 'e',
                rank: 2
            };

            var variation4 = {
                status: 'e',
                rank: 3
            };

            var variation5 = {
                status: 'e',
                rank: 4
            };

            var variation6 = {
                status: 'DELETED',
                rank: 5
            };

            var variation7 = {
                status: 'e',
                rank: 6
            };

            var variation8 = {
                status: 'DELETED',
                rank: 7
            };

            var variation9 = {
                status: 'DELETED',
                rank: 8
            };

            var variations = [variation1, variation2, variation3, variation4, variation5, variation6, variation7, variation8, variation9];
            var to = 0;

            //move down
            to = personalizationsmarteditUtils.getValidRank(variations, variation1, 1);
            expect(to).toBe(2);

            to = personalizationsmarteditUtils.getValidRank(variations, variation2, 1);
            expect(to).toBe(2);

            to = personalizationsmarteditUtils.getValidRank(variations, variation3, 1);
            expect(to).toBe(3);

            to = personalizationsmarteditUtils.getValidRank(variations, variation4, 1);
            expect(to).toBe(4);

            to = personalizationsmarteditUtils.getValidRank(variations, variation5, 1);
            expect(to).toBe(6);

            to = personalizationsmarteditUtils.getValidRank(variations, variation6, 1);
            expect(to).toBe(6);

            to = personalizationsmarteditUtils.getValidRank(variations, variation7, 1);
            expect(to).toBe(8);

            to = personalizationsmarteditUtils.getValidRank(variations, variation8, 1);
            expect(to).toBe(8);

            to = personalizationsmarteditUtils.getValidRank(variations, variation9, 1);
            expect(to).toBe(8);

            //move up
            to = personalizationsmarteditUtils.getValidRank(variations, variation1, -1);
            expect(to).toBe(0);

            to = personalizationsmarteditUtils.getValidRank(variations, variation2, -1);
            expect(to).toBe(0);

            to = personalizationsmarteditUtils.getValidRank(variations, variation3, -1);
            expect(to).toBe(0);

            to = personalizationsmarteditUtils.getValidRank(variations, variation4, -1);
            expect(to).toBe(2);

            to = personalizationsmarteditUtils.getValidRank(variations, variation5, -1);
            expect(to).toBe(3);

            to = personalizationsmarteditUtils.getValidRank(variations, variation6, -1);
            expect(to).toBe(4);

            to = personalizationsmarteditUtils.getValidRank(variations, variation7, -1);
            expect(to).toBe(4);

            to = personalizationsmarteditUtils.getValidRank(variations, variation8, -1);
            expect(to).toBe(6);

            to = personalizationsmarteditUtils.getValidRank(variations, variation9, -1);
            expect(to).toBe(6);
        });

    });

});
