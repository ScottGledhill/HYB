describe('Personalization Manager Modal - ', function() {

    beforeEach(function() {
        browser.ignoreSynchronization = true;
        browser.get('jsTests/ui/common/dummysmartedit.html');
        browser.waitForWholeAppToBeReady();

        openPerspectiveSelector();
        clickPersonalizationPerspective();
        clickLibrary();
        clickManageLibrary();
    });

    afterEach(function() {
        browser.takeScreenshot().then(function(png) {
            filename = 'personalizationManagerModal' + Date.now() + '.png';
            ScreenshotUtils.writeScreenshot(png, filename);
        });

        browser.driver.manage().deleteAllCookies();
    });

    it('WHEN user changes number of rows per page THEN a proper number of rows is displayed', function() {
        // GIVEN
        numberOfDisplayedCustomizations(6);

        // WHEN        
        openRowsPerPageDropdown();
        clickRowsPerPage(5);

        //THEN
        numberOfDisplayedCustomizations(5);

        // WHEN        
        openRowsPerPageDropdown();
        clickRowsPerPage(10);

        //THEN
        numberOfDisplayedCustomizations(6);
    });

    it('WHEN user filters customizations by name THEN a proper amount of customizations is displayed', function() {
        // GIVEN
        numberOfDisplayedCustomizations(6);

        // WHEN        
        filterCustomizationsByName("winter");

        //THEN
        numberOfDisplayedCustomizations(1);

        // WHEN
        clearCustomizationSearch();

        //THEN
        numberOfDisplayedCustomizations(6);
    });

    it('WHEN user filters customizations by status THEN a proper amount of customizations is displayed', function() {
        // GIVEN
        numberOfDisplayedCustomizations(6);

        // WHEN        
        filterCustomizationsByStatus("Enabled");

        //THEN
        numberOfDisplayedCustomizations(3);

        // WHEN
        filterCustomizationsByStatus("All");

        //THEN
        numberOfDisplayedCustomizations(6);
    });

    it('WHEN user click on ADD NEW CUSTOMIZATION THEN customization variation manager modal shows up with name input enabled', function() {
        // GIVEN
        numberOfDisplayedCustomizations(6);

        // WHEN
        clickAddNewCustomization();

        // THEN
        customizationVariationManagerModalIsVisible();
        customizationNameInputIsEnabled();
    });

    it('WHEN user clicks on a customization row THEN a list of variation shows up', function() {
        var customizationName = "WinterSale";

        // WHEN
        collapseCustomizationByName(customizationName);

        // THEN
        numberOfDisplayedVariationsForCustomization(customizationName, 2);
    });

    it('WHEN user clicks on EDIT on one of the customizations THEN customization variation manager modal shows up with name input enabled', function() {
        // WHEN
        openActionDropdownForCustomizationByName("CategoryLover");
        clickEdit();

        // THEN
        customizationVariationManagerModalIsVisible();
        customizationNameInputIsEnabled();
    });

    it('WHEN user clicks on DELETE on one of the customizations THEN confirmation dialog shows up', function() {
        // WHEN
        openActionDropdownForCustomizationByName("CategoryLover");
        clickDelete();

        // THEN
        removeConfirmationDialogVisible();
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

    function clickManageLibrary() {
        $("a[data-translate='personalization.toolbar.library.manager.name']").click();
    }

    function filterCustomizationsByName(name) {
        element(by.model('search.name')).sendKeys(name);
    }

    function filterCustomizationsByStatus(status) {
        element(by.model('search.status')).$("a[class*='select2-choice']").click();

        element.all(by.binding('item.text | translate')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == status;
            });
        }).first().element(by.xpath('../..')).click();
    }

    function clearCustomizationSearch() {
        var customizationSearchInput = element(by.model('search.name'));
        customizationSearchInput.clear();
        customizationSearchInput.sendKeys(protractor.Key.ENTER);
    }

    function clickAddNewCustomization() {
        $("button[class*='y-add-btn']").click();
    }

    function collapseCustomizationByName(customizationName) {
        element.all(by.binding('customization.name')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == customizationName;
            });
        }).first().element(by.xpath('../../..')).$("a[class*='category-toggle']").click();
    }

    function openActionDropdownForCustomizationByName(customizationName) {
        element.all(by.binding('customization.name')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == customizationName;
            });
        }).first().element(by.xpath('../../..')).$("button[class*='dropdown-toggle']").click();
    }

    function openActionDropdownForVariationByName(variationName) {
        element.all(by.exactBinding('variation.name')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == variationName;
            });
        }).first().element(by.xpath('../..')).$("button[class*='dropdown-toggle']").click();
    }

    function clickEdit() {
        $("div[class*='categoryTable']").$("div[class*='open']").$("a[data-translate*='options.edit']").click();
    }

    function clickDelete() {
        $("div[class*='categoryTable']").$("div[class*='open']").$("a[data-translate*='options.delete']").click();
    }

    function clickMoveUp() {
        $("div[class*='categoryTable']").$("div[class*='open']").$("a[data-translate*='options.moveup']").click();
    }

    function clickMoveDown() {
        $("div[class*='categoryTable']").$("div[class*='open']").$("a[data-translate*='options.movedown']").click();
    }

    function clickCommerceCustomization() {
        $("div[class*='categoryTable']").$("div[class*='open']").$("a[data-translate*='options.commercecustomization']").click();
    }

    function clickEnable() {
        $("div[class*='categoryTable']").$("div[class*='open']").by.binding('getEnablementActionTextForVariation(variation)').click();
    }

    function clickDisable() {
        clickEnable();
    }

    function openRowsPerPageDropdown() {
        $("personalizationsmartedit-pagination").$("span[class*='hyicon']").click();
    }

    function clickRowsPerPage(n) {
        element.all(by.repeater('i in availablePageSizes() track by $index')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == n;
            });
        }).first().$("a").click();
    }

    // Expectations
    function customizationVariationManagerModalIsVisible() {
        expect(element(by.id('smartedit-modal-title-personalization.modal.manager.title')).isDisplayed()).toBeTruthy();
    }

    function customizationNameInputIsEnabled() {
        expect(element(by.model('customization.name')).isEnabled()).toBeTruthy();
    }

    function customizationNameInputIsDisabled() {
        expect(element(by.model('customization.name')).isEnabled()).toBeFalsy();
    }

    function removeConfirmationDialogVisible() {
        expect(element(by.id('smartedit-modal-title-confirmation.modal.title')).isDisplayed()).toBeTruthy();
    }

    function numberOfDisplayedCustomizations(n) {
        expect(element.all(by.repeater('customization in getVisibleCustomizations()')).count()).toEqual(n);
    }

    function numberOfDisplayedVariationsForCustomization(customizationName, n) {
        expect(
            element.all(by.repeater('customization in getVisibleCustomizations()')).filter(function(elm) {
                return elm.$("span[class*='personalizationsmartedit-customization-code']").getText().then(function(text) {
                    return text == customizationName;
                });
            }).first()
            .$("div[aria-expanded='true'][aria-hidden='false']")
            .all(by.repeater('variation in customization.variations | statusNotDeleted as filteredVariations'))
            .count()
        ).toEqual(n);
    }

    function commerceCustomizationModalVisible() {
        expect(element(by.id('smartedit-modal-title-personalization.modal.commercecustomization.title')).isDisplayed()).toBeTruthy();
    }
});
