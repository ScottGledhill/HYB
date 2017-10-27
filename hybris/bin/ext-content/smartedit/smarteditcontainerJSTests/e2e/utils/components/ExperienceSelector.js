/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
module.exports = {
    widget: {
        button: function() {
            return element(by.id('experience-selector-btn', 'Experience Selector button not found'));
        },
        clickButton: function() {
            return browser.click(element(by.id('experience-selector-btn', 'Experience Selector button not found'))).then(function() {
                return browser.wait(EC.visibilityOf(element(by.xpath("//item-printer[@id='language-selected']/div/span"))), 5000, 'cannot load catalog item');
            });
        },

        text: function() {
            return element(by.css('[id=\'experience-selector-btn\'] > span:nth-child(2)', 'Selector widget not found')).getText();
        }
    },
    catalog: {
        label: function() {
            return element(by.id('previewCatalog-label', 'Experience Selector Catalog Field Label not found'));
        },
        selectedOption: function() {
            return element(by.css('[id=\'previewCatalog-selected\']', 'Experience Selector Catalog Field not found'));
        },
        dropdown: function() {
            return element(by.css('[id=\'previewCatalog\'] [class*=\'ui-select-container\'] > a'));
        },
        option: function(index) {
            return element(by.css('[id=\'previewCatalog\'] ul[role=\'listbox\'] li[role=\'option\']:nth-child(' + index + ') span'));
        },
        options: function() {
            return element.all(by.css('[id=\'previewCatalog\'] ul[role=\'listbox\'] li[role=\'option\'] span'));
        },
        selectDropdown: function() {
            return browser.click(element(by.id('previewCatalog')));
        },
        selectOption: function(option) {
            var optionElement = element(by.cssContainingText('[id=\'previewCatalog-list\'] li[role=\'option\'] span', option));

            browser.wait(function() {
                return optionElement.click().then(function() {
                    return true;
                }, function() {
                    return false;
                }).then(function(clickable) {
                    return clickable;
                });
            }, 5000, 'Option not clickable');
        },
        assertOptionText: function(index, expectedText) {
            browser.wait(function() {
                return this.option(index).getText().then(function(text) {
                    return text;
                }, function() {
                    return '';
                }).then(function(actualText) {
                    return actualText === expectedText;
                });
            }.bind(this), 3000, 'Dropdown options missing');
        },
        assertNumberOfOptions: function(length) {
            browser.wait(function() {
                return this.options().count().then(function(count) {
                    return count;
                }, function() {
                    return '';
                }).then(function(actualValue) {
                    return actualValue === length;
                });
            }.bind(this), 3000, 'dropdown failed to contain ' + length + ' elements');
        }
    },
    dateAndTime: {
        label: function() {
            return element(by.id('time-label', 'Experience Selector Date and Time Field Label not found'));
        },
        field: function() {
            return element(by.css('input[name=\'time\']', 'Experience Selector Date and Time Field not found'));
        },
        button: function() {
            return element(by.css('[id=\'time\'] div[class*=\'date\'] span[class*=\'input-group-addon\']'));
        }
    },
    language: {
        label: function() {
            return element(by.id('language-label', 'Experience Selector Language Field Label not found'));
        },
        selectedOption: function() {
            return element(by.css('[id=\'language-selected\']', 'Experience Selector Language Field not found'));
        },
        dropdown: function() {
            return element(by.css('[id=\'language\'] [class*=\'ui-select-container\'] > a'));
        },
        option: function(index) {
            return element(by.css('[id=\'language\'] ul[role=\'listbox\'] li[role=\'option\']:nth-child(' + index + ') span'));
        },
        options: function() {
            return element.all(by.css('[id=\'language\'] ul[role=\'listbox\'] li[role=\'option\'] span'));
        },
        selectDropdown: function() {
            return browser.click(element(by.id('language')));
        },
        selectOption: function(option) {
            var optionElement = element(by.cssContainingText('[id=\'language-list\'] li[role=\'option\'] span', option));

            browser.wait(function() {
                return optionElement.click().then(function() {
                    return true;
                }, function() {
                    return false;
                }).then(function(clickable) {
                    return clickable;
                });
            }, 5000, 'Option not clickable');
        },
        assertOptionText: function(index, expectedText) {
            browser.wait(function() {
                return this.option(index).getText().then(function(text) {
                    return text;
                }, function() {
                    return '';
                }).then(function(actualText) {
                    return actualText === expectedText;
                });
            }.bind(this), 3000, 'Dropdown options missing');
        },
        assertNumberOfOptions: function(length) {
            browser.wait(function() {
                return this.options().count().then(function(count) {
                    return count;
                }, function() {
                    return '';
                }).then(function(actualValue) {
                    return actualValue === length;
                });
            }.bind(this), 3000, 'dropdown failed to contain ' + length + ' elements');
        }

    },
    otherFields: {
        label: function(fieldName) {
            return element(by.id(fieldName + '-label', 'Experience Selector ' + fieldName + ' Label not found'));
        },
        field: function(fieldName) {
            return element(by.css("input[name='" + fieldName + "']", 'Experience Selector ' + fieldName + ' not found'));
        }
    },
    buttons: {
        ok: function() {
            return element(by.id('submit', 'Experience Selector Apply Button not found'));
        },
        cancel: function() {
            return element(by.id('cancel', 'Experience Selector Cancel Button not found'));
        }
    },
    page: {
        iframe: function() {
            return element(by.css('#js_iFrameWrapper iframe', 'iFrame not found'));
        }
    },
    actions: {
        clickInIframe: function() {
            browser.switchToIFrame();
            browser.click(element(by.css('.noOffset1')));
            browser.switchToParent();
        },
        clickInApplication: function() {
            browser.click(element(by.css('.ySmartEditAppLogo')));
        },
        selectExpectedDate: function() {
            browser.click(element(by.css('div[class*=\'datepicker-days\'] th[class*=\'picker-switch\']')));
            browser.click(element(by.css('div[class*=\'datepicker-months\'] th[class*=\'picker-switch\']')));
            browser.click(element(by.cssContainingText('span[class*=\'year\']', '2016')));
            browser.click(element(by.css('span[class*=\'month\']:first-child')));
            browser.click(element(by.xpath('.//*[.="1" and contains(@class,\'day\') and not(contains(@class, \'old\')) and not(contains(@class, \'new\'))]')));
            browser.click(element(by.css('span[class*=\'glyphicon-time\']')));
            browser.click(element(by.css('div[class=\'timepicker-picker\'] .timepicker-hour')));
            browser.click(element(by.cssContainingText('td[class*=\'hour\']', '01')));
            browser.click(element(by.css('div[class=\'timepicker-picker\'] .timepicker-minute')));
            browser.click(element(by.cssContainingText('td[class*=\'minute\']', '00')));

            var periodToggleElement = element(by.cssContainingText('div[class*=\'timepicker\'] button[class*=\'btn\']', 'AM'));
            periodToggleElement.isPresent().then(function(result) {
                if (result) {
                    browser.click(periodToggleElement);
                }
            });
        }
    }
};
