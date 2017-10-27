describe('Customization Modal - ', function() {

    beforeEach(function() {
        browser.ignoreSynchronization = true;
        browser.get('jsTests/ui/common/dummysmartedit.html');
        browser.waitForWholeAppToBeReady();

        openPerspectiveSelector();
        clickPersonalizationPerspective();
        clickLibrary();
        clickCreateNewCustomization();
    });

    afterEach(function() {
        browser.driver.manage().deleteAllCookies();

        var currentSpec = jasmine.getEnv().currentSpec;

        browser.takeScreenshot().then(function(png) {
            filename = 'customizationVariationManagerModal' + Date.now() + '.png';
            ScreenshotUtils.writeScreenshot(png, filename);
        });
    });

    it('GIVEN user sets disabled status in basic information tab WHEN user navigates to target group tab THEN proper status is displayed', function() {
        // GIVEN
        enterCustomizationName("customizationA");

        // WHEN
        clickNextButton();

        // THEN
        customizationIsDisabled();
    });

    it('GIVEN user sets enabled status in basic information tab WHEN user navigates to target group tab THEN proper status is displayed', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        toggleStatusConfiguration();

        // WHEN
        clickNextButton();

        // THEN
        customizationIsEnabled();
    });

    it('GIVEN user sets a start and end date in basic information tab WHEN user navigates to target group tab THEN proper date is displayed', function() {
        var startDate = "08/27/2011 8:00 AM";
        var endDate = "07/26/2117 8:00 PM";

        // GIVEN
        enterCustomizationName("customizationA");
        toggleStatusConfiguration();
        clickConfigureDate();
        enterStartDate(startDate);
        enterEndDate(endDate);

        // WHEN
        clickNextButton();

        // THEN
        customizationStartDateEquals(startDate);
        customizationEndDateEquals(endDate);
    });

    it('WHEN user sets an end date from the past basic information tab THEN user cannot move to target group tab', function() {
        var endDate = "08/27/2011 8:00 AM";

        // GIVEN
        enterCustomizationName("customizationA");
        toggleStatusConfiguration();
        clickConfigureDate();

        // WHEN
        enterEndDate(endDate);

        // THEN
        nextButtonIsNotClickable();
    });

    it('GIVEN user provides customization name and target group name WHEN user selects a segment THEN add button is clickable', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName("TARGETGROUPA");

        // WHEN
        openSegmentList();
        selectNthSegmentFromTop(0);

        // THEN
        addButtonIsClickable();
    });

    it('GIVEN user provides customization name and target group name WHEN user adds a segment THEN save button is clickable', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName("TARGETGROUPA");

        // WHEN
        openSegmentList();
        selectNthSegmentFromTop(0);
        clickAddButton();

        // THEN
        saveButtonIsClickable();
    });

    it('GIVEN user provides customization name and target group name WHEN two segments are being added THEN criteria radio buttons are visible', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName("TARGETGROUPA");

        // WHEN
        openSegmentList();
        selectNthSegmentFromTop(0);
        openSegmentList();
        selectNthSegmentFromTop(1);

        // THEN
        criteriaRadioButtonsAreEnabled();
    });

    it('GIVEN user provides customization name and target group name WHEN he adds a target group with two segments with "all" criteria THEN "all" criteria is displayed with the group', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName("TARGETGROUPA");

        // WHEN
        openSegmentList();
        selectNthSegmentFromTop(0);
        openSegmentList();
        selectNthSegmentFromTop(1);
        clickAddButton();

        // THEN
        targetGroupWithUseAllSegmentsFirstFromTop();
    });

    it('GIVEN user provides customization name and target group name WHEN he adds a target group with two segments with "any" criteria THEN "any" criteria is displayed with the group', function() {
        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName("TARGETGROUPA");

        // WHEN
        openSegmentList();
        selectNthSegmentFromTop(0);
        openSegmentList();
        selectNthSegmentFromTop(1);
        clickUseAnySegments();
        clickAddButton();

        // THEN
        targetGroupWithUseAnySegmentsFirstFromTop();
    });

    it('GIVEN a customization has at least one target group WHEN " /edit" is selected THEN "APPLY" and "CANCEL" buttons are clickable', function() {
        var targetGroupName = "TARGETGROUPA";

        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName(targetGroupName);
        openSegmentList();
        selectNthSegmentFromTop(0);
        clickAddButton();

        // WHEN
        clickTargetGroupActionEdit(targetGroupName);

        // THEN
        changesButtonsAreEnabled();
    });

    it('GIVEN a customization has one target group WHEN "/remove" is selected THEN customization has no segments', function() {
        var targetGroupName = "TARGETGROUPA";

        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName(targetGroupName);
        openSegmentList();
        selectNthSegmentFromTop(0);
        clickAddButton();

        // WHEN
        clickTargetGroupActionRemove(targetGroupName);
        clickConfirmOk();

        // THEN
        customizationHasNoTargetGroups();
    });

    it('GIVEN two target groups exist WHEN user moves up or moves down a target group THEN target groups are rearranged properly', function() {
        var targetGroup1Name = "TARGETGROUPA";
        var targetGroup2Name = "TARGETGROUPB";

        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();

        enterTargetGroupName(targetGroup1Name);
        openSegmentList();
        selectNthSegmentFromTop(0);
        clickAddButton();

        enterTargetGroupName(targetGroup2Name);
        openSegmentList();
        selectNthSegmentFromTop(1);
        clickAddButton();

        targetGroupIsNthFromTop(targetGroup1Name, 0);
        targetGroupIsNthFromTop(targetGroup2Name, 1);

        // WHEN
        clickTargetGroupActionMoveDown(targetGroup1Name);

        // THEN
        targetGroupIsNthFromTop(targetGroup1Name, 1);
        targetGroupIsNthFromTop(targetGroup2Name, 0);

        // WHEN
        clickTargetGroupActionMoveUp(targetGroup1Name);

        // THEN
        targetGroupIsNthFromTop(targetGroup1Name, 0);
        targetGroupIsNthFromTop(targetGroup2Name, 1);
    });

    it('GIVEN a customization a  target group WHEN "/disable" is selected on the target group THEN target group status is properly displayed', function() {
        var targetGroupName = "TARGETGROUPA";

        // GIVEN
        enterCustomizationName("customizationA");
        clickNextButton();
        enterTargetGroupName(targetGroupName);
        openSegmentList();
        selectNthSegmentFromTop(0);
        clickAddButton();

        // WHEN
        clickTargetGroupActionDisable(targetGroupName);

        // THEN
        targetGroupIsDisabled(targetGroupName);
    });

    // Actions
    function openPerspectiveSelector() {
        $("div[class*='ySEPerspectiveSelector']").$("button").click();
    }

    function clickPersonalizationPerspective() {
        $$("li[class*='ySEPerspectiveList--item']").filter(function(elm) {
            return elm.$("a").getText().then(function(text) {
                return text == 'PERSONALIZATION';
            });
        }).first().click();
    }

    function clickLibrary() {
        $("span[data-translate='personalization.toolbar.library.name']").element(by.xpath('..')).click();
    }

    function clickCustomize() {
        $("span[data-translate='personalization.toolbar.pagecustomization.name']").element(by.xpath('..')).click();
    }

    function clickCreateNewCustomization() {
        $("a[data-translate='personalization.toolbar.library.customizationvariationmanagement.name']").click();
    }

    function enterCustomizationName(customizationName) {
        element(by.model('customization.name')).sendKeys(customizationName);
    }

    function enterCustomizationDetails(customizationDetails) {
        element(by.model('customization.description')).sendKeys(customizationDetails);
    }

    function enterTargetGroupName(targetGroupName) {
        element(by.model('edit.name')).sendKeys(targetGroupName);
    }

    function clickNextButton() {
        element(by.id('confirmNext')).click();
    }

    function openSegmentList() {
        element(by.model('singleSegment')).$("span[class*='ui-select-toggle']").click();
    }

    function selectNthSegmentFromTop(n) {
        element.all(by.repeater('item in $select.items')).get(n).click();
    }

    function clickAddButton() {
        $("button[class*='add-target-group']").click();
    }

    function clickTargetGroupActionEdit(targetGroupName) {
        var targetGroup = element.all(by.repeater('variation in customization.variations ')).filter(function(elm) {
            return elm.element(by.binding('variation.name')).getText().then(function(text) {
                return text == targetGroupName;
            });
        }).first().$("div[class*='row']");

        targetGroup.$("button").click();
        targetGroup.$("a[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.edit']")
            .click();
    }

    function clickTargetGroupActionRemove(targetGroupName) {
        var targetGroup = element.all(by.repeater('variation in customization.variations ')).filter(function(elm) {
            return elm.element(by.binding('variation.name')).getText().then(function(text) {
                return text == targetGroupName;
            });
        }).first().$("div[class*='row']");

        targetGroup.$("button").click();
        targetGroup.$("a[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.remove']")
            .click();
    }

    function clickTargetGroupActionMoveUp(targetGroupName) {
        var targetGroup = element.all(by.repeater('variation in customization.variations ')).filter(function(elm) {
            return elm.element(by.binding('variation.name')).getText().then(function(text) {
                return text == targetGroupName;
            });
        }).first().$("div[class*='row']");

        targetGroup.$("button").click();
        targetGroup.$("a[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.moveup']")
            .click();
    }

    function clickTargetGroupActionMoveDown(targetGroupName) {
        var targetGroup = element.all(by.repeater('variation in customization.variations ')).filter(function(elm) {
            return elm.element(by.binding('variation.name')).getText().then(function(text) {
                return text == targetGroupName;
            });
        }).first().$("div[class*='row']");

        targetGroup.$("button").click();
        targetGroup.$("a[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.variation.options.movedown']")
            .click();
    }

    function clickTargetGroupActionDisable(targetGroupName) {
        var targetGroup = element.all(by.repeater('variation in customization.variations ')).filter(function(elm) {
            return elm.element(by.binding('variation.name')).getText().then(function(text) {
                return text == targetGroupName;
            });
        }).first().$("div[class*='row']");

        targetGroup.$("button").click();

        targetGroup.$$("a").filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == "Disable";
            });
        }).first().click();
    }

    function clickUseAllSegments() {
        element.all(by.model('edit.allSegmentsChecked')).get(0).click();
    }

    function clickUseAnySegments() {
        element.all(by.model('edit.allSegmentsChecked')).get(1).click();
    }

    function clickConfirmOk() {
        element(by.id('smartedit-modal-title-confirmation.modal.title')).element(by.xpath('../..')).element(by.id('confirmOk')).click();
    }

    function clickEnabledButton() {
        $("button[ng-class*='PERSONALIZATION_MODEL_STATUS_CODES.ENABLED'").click();
    }

    function toggleStatusConfiguration() {
        element(by.model('customization.statusBoolean')).click();
    }

    function clickConfigureDate() {
        $("a[data-translate='personalization.modal.customizationvariationmanagement.basicinformationtab.details.showdateconfigdata']").click();
    }

    function enterStartDate(date) {
        var startDateInput = element(by.id('customization-start-date')).sendKeys(date);
    }

    function enterEndDate(date) {
        var endDateInput = element(by.id('customization-end-date')).sendKeys(date);
    }

    // Expectations
    function addButtonIsClickable() {
        var addButton = $("button[class*='add-target-group']");

        expect(addButton.isDisplayed()).toBeTruthy();
        expect(addButton.isEnabled()).toBeTruthy();
    }

    function saveButtonIsClickable() {
        var saveButton = element(by.id('confirmOk'));

        expect(saveButton.isDisplayed()).toBeTruthy();
        expect(saveButton.isEnabled()).toBeTruthy();
    }

    function nextButtonIsNotClickable() {
        var nextButton = element(by.id('confirmNext'));

        expect(nextButton.isDisplayed()).toBeTruthy();
        expect(nextButton.isEnabled()).toBeFalsy();
    }

    function criteriaRadioButtonsAreEnabled() {
        var criteriaRadioButtons = element.all(by.model('edit.allSegmentsChecked'));

        expect(criteriaRadioButtons.count()).toBe(2);
        expect(criteriaRadioButtons.get(0).isDisplayed()).toBeTruthy();
        expect(criteriaRadioButtons.get(0).isEnabled()).toBeTruthy();
        expect(criteriaRadioButtons.get(1).isDisplayed()).toBeTruthy();
        expect(criteriaRadioButtons.get(1).isEnabled()).toBeTruthy();

        expect($("label[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.anysegments']").isDisplayed()).toBeTruthy();
        expect($("label[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.allsegments']").isDisplayed()).toBeTruthy();
    }

    function changesButtonsAreEnabled() {
        var cancelChangesButton = $("button[class*='cancel-target-group-edit']");
        var saveChangesButton = $("button[class*='submit-target-group-edit']");

        expect(cancelChangesButton.isDisplayed()).toBeTruthy();
        expect(cancelChangesButton.isEnabled()).toBeTruthy();
        expect(saveChangesButton.isDisplayed()).toBeTruthy();
        expect(saveChangesButton.isEnabled()).toBeTruthy();
    }

    function targetGroupWithUseAnySegmentsFirstFromTop() {
        expect(
                $("span[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.criteria.colon']")
                .element(by.xpath(".."))
                .element(by.exactBinding('getCriteriaDescrForVariation(variation)'))
                .getText())
            .toEqual("Match any segment");
    }

    function targetGroupWithUseAllSegmentsFirstFromTop() {
        expect(
                $("span[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.criteria.colon']")
                .element(by.xpath(".."))
                .element(by.exactBinding('getCriteriaDescrForVariation(variation)'))
                .getText())
            .toEqual("Match all segments");
    }

    function targetGroupIsDisabled(targetGroupName) {
        expect(
            element.all(by.exactBinding('variation.name'))
            .filter(function(elm) {
                return elm.getText().then(function(text) {
                    return text == targetGroupName;
                });
            }).first().element(by.xpath('..'))
            .element(by.exactBinding('getEnablementTextForVariation(variation)')).getText()
        ).toEqual("Disabled");
    }

    function customizationHasNoTargetGroups() {
        var noTargetGroupsLabel = $("h6[data-translate='personalization.modal.customizationvariationmanagement.targetgrouptab.notargetgroups']");
        expect(noTargetGroupsLabel.isPresent()).toBeTruthy();
        expect(noTargetGroupsLabel.isDisplayed()).toBeTruthy();
    }

    function targetGroupIsNthFromTop(searchedTargetGroupName, n) {
        var nthTargetGroupName = element.all(by.binding('variation.name')).get(n).getText();
        expect(nthTargetGroupName).toEqual(searchedTargetGroupName);
    }

    function customizationIsEnabled() {
        var statusLabel = element(by.binding('customization.status')).getText();
        expect(statusLabel).toEqual("ENABLED");
    }

    function customizationIsDisabled() {
        var statusLabel = element(by.binding('customization.status')).getText();
        expect(statusLabel).toEqual("DISABLED");
    }

    function customizationStartDateEquals(date) {
        var startDateLabel = element(by.binding('customization.enabledStartDate')).getText();
        expect(startDateLabel).toEqual(date);
    }

    function customizationEndDateEquals(date) {
        var endDateLabel = element(by.binding('customization.enabledEndDate')).getText();
        expect(endDateLabel).toEqual(date);
    }
});
