describe('Personalization Toolbar - ', function() {

    beforeEach(function() {
        browser.ignoreSynchronization = true;
        browser.get('jsTests/ui/common/dummysmartedit.html');
        browser.waitForWholeAppToBeReady();

        openPerspectiveSelector();
        clickPersonalizationPerspective();
    });

    afterEach(function() {
        browser.driver.manage().deleteAllCookies();

        var currentSpec = jasmine.getEnv().currentSpec;

        browser.takeScreenshot().then(function(png) {
            filename = 'personalizationToolbar' + Date.now() + '.png';
            ScreenshotUtils.writeScreenshot(png, filename);
        });
    });

    it('GIVEN customize dropdown is open WHEN user filters customizations by status THEN a proper amount of customizations is displayed', function() {
        // GIVEN
        clickCustomize();
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

    it('GIVEN customize dropdown is open WHEN uncollapses a customization in customize dropdown THEN variations are visible', function() {
        // GIVEN
        clickCustomize();

        // WHEN
        collapseCustomizationByName("WinterSale");

        // THEN
        numberOfDisplayedVariationsForCustomization("WinterSale", 2);
    });

    it('GIVEN customize dropdown is open WHEN user clicks on edit on a customization THEN Customization modal shows up', function() {
        // GIVEN
        clickCustomize();

        // WHEN
        editCustomizationByName("WinterSale");

        // THEN
        customizationVariationManagerModalIsVisible();
    });

    it('GIVEN combined view dropdown is open WHEN user clicks on configure THEN Combined View Configuration modal shows up', function() {
        // GIVEN
        clickCombinedView();

        // WHEN
        clickConfigure();

        // THEN
        combinedViewConfigurationModalIsVisible();
    });

    it('GIVEN library dropdown is open WHEN user clicks on manage library THEN Manage Customization Library modal shows up', function() {
        // GIVEN
        clickLibrary();

        // WHEN
        clickManageLibrary();

        // THEN
        manageCustomizationLibraryModalIsVisible();
    });

    it('GIVEN library dropdown is open WHEN user clicks on manage library THEN Manage Customization Library modal shows up', function() {
        // GIVEN
        clickLibrary();

        // WHEN
        clickCreateNewCustomization();

        // THEN
        customizationVariationManagerModalIsVisible();
    });

    //CLICK
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
        $("span[data-translate='personalization.toolbar.pagecustomizations']").element(by.xpath('..')).click();
    }

    function clickCombinedView() {
        $("span[data-translate*='personalization.toolbar.combinedview.name']").element(by.xpath('..')).click();
    }

    function clickManageLibrary() {
        $("a[data-translate='personalization.toolbar.library.manager.name']").click();
    }

    function clickCreateNewCustomization() {
        $("a[data-translate='personalization.toolbar.library.customizationvariationmanagement.name']").click();
    }

    function clickConfigure() {
        $("button[data-translate='personalization.toolbar.combinedview.openconfigure.button']").click();
    }

    function collapseCustomizationByName(customizationName) {
        element.all(by.binding('customization.name')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == customizationName;
            });
        }).first().element(by.xpath('../..')).$("a[class*='category-toggle']").click();
    }

    function editCustomizationByName(customizationName) {
        element.all(by.binding('customization.name')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == customizationName;
            });
        }).first().element(by.xpath('../..')).$("button[class*='dropdown-toggle']").click();

        $("a[data-translate*='personalization.toolbar.pagecustomizations.customization.options.edit']").click();
    }

    function filterCustomizationsByStatus(status) {
        element(by.model('search.status')).$("a[class*='select2-choice']").click();

        element.all(by.binding('item.text | translate')).filter(function(elm) {
            return elm.getText().then(function(text) {
                return text == status;
            });
        }).first().element(by.xpath('../..')).click();
    }

    //expect
    function numberOfDisplayedCustomizations(n) {
        expect(element.all(by.binding('customization.name')).count()).toEqual(n);
    }

    function numberOfDisplayedVariationsForCustomization(customizationName, n) {
        expect(
            element.all(by.binding('customization.name')).filter(function(elm) {
                return elm.getText().then(function(text) {
                    return text == customizationName;
                });
            }).first().element(by.xpath('../../../..'))
            .$("div[aria-expanded='true'][aria-hidden='false']")
            .all(by.binding('variation.name'))
            .count()
        ).toEqual(n);
    }

    function customizationVariationManagerModalIsVisible() {
        expect($("[data-translate='personalization.modal.customizationvariationmanagement.basicinformationtab.name']").isDisplayed()).toBeTruthy();
    }

    function manageCustomizationLibraryModalIsVisible() {
        expect($("[data-translate='personalization.modal.manager.search.result.label']").isDisplayed()).toBeTruthy();
    }

    function combinedViewConfigurationModalIsVisible() {
        expect(element(by.id('smartedit-modal-title-personalization.modal.combinedview.title')).isDisplayed()).toBeTruthy();
    }
});
