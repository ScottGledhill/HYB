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
var DEFAULT_IMPLICIT_WAIT = 1000;

exports.config = {
    allScriptsTimeout: 11000,

    specs: [
        'e2e/**/*Test.js'
    ],
    //exclude: ['e2e/**/drag*Test.js'],

    seleniumServerJar: '../node_modules/protractor/selenium/selenium-server-standalone-2.52.jar',

    capabilities: {
        'browserName': 'chrome',
        'shardTestFiles': false,
        'maxInstances': 10,
        'chromeOptions': {
            args: ['lang=en-US']
        }
    },
    chromeDriver: '../node_modules/protractor/selenium/chromedriver/chromedriver',
    directConnect: true,

    troubleshoot: false,

    baseUrl: 'http://127.0.0.1:7000',

    framework: 'jasmine2',

    jasmineNodeOpts: {
        defaultTimeoutInterval: 120000
    },

    //method found in node_modules/protractor/docs/referenceConf.js :
    onPrepare: function() {

        global.EC = protractor.ExpectedConditions;

        var jasmineReporters = require('jasmine-reporters');
        jasmine.getEnv().addReporter(new jasmineReporters.JUnitXmlReporter({
            consolidateAll: false,
            savePath: 'jsTarget/test/smarteditContainer/junit/protractor'
        }));

        //this is protractor but both protractor and browser instances are available in this closure

        browser.setSize = function() {
            return browser.driver.manage().window().setSize(1366, 820);
        };
        browser.setSize();

        // Wait up to 5 seconds for trying to find an element before failing
        browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT);

        //If you are outputting logs into the shell after running a protractor test.
        //i.e. grunt connect:test protractor:run --specs='cmsxuiJSTests/e2e/apiAuthentication/apiAuthenticationTest.js'
        //Setting this value to anything other than 0 will wait in milliseconds between each log statement
        global.waitForSprintDemoLogTime = 0;
        //Set log levels to display in shell
        global.sprintDemoLogLevels = ["WARNING", "INFO"];
        //Show any log parsing errors - by default they are not shown
        global.sprintDemoShowLogParsingErrors = false;

        browser.dumpLogs = function() {
            browser.manage().logs().get('browser').then(function(browserLogs) {
                browserLogs.forEach(function(log) {
                    console.log(log.message);
                });
            });
        };

        browser.waitForContainerToBeReady = function() {
            //click on load preview button
            return browser.wait(protractor.ExpectedConditions.elementToBeClickable(element(by.id('nav-expander'))), 20000, "could not find burger menu/toolbar when first loading app").then(function() {
                return browser.waitForAngular();
            });
        };


        browser.waitForFrameToBeReady = function() {
            /*
             * protractor cannot nicely use browser api until angular app is bootstrapped.
             * to do so it needs to see ng-app attribute.
             * But in our smartEdit setup, app is bootstrapped programmatically, not through ng-app
             * workaround consists then in waiting arbitrary amount fo time
             */
            return browser.wait(function() {
                browser.ignoreSynchronization = true;
                return element.all(by.css('body')).count().then(function(count) {
                    if (count === 1) {
                        return element(by.css('body')).getAttribute('data-smartedit-ready').then(function(attribute) {
                            if (attribute === 'true') {
                                browser.ignoreSynchronization = false;
                            }
                            return attribute === 'true';
                        });
                    } else {
                        return false;
                    }
                });
            }, 30000, "could not find data-smartedit-ready='true' attribute on the iframe body tag");
        };

        browser.switchToIFrame = function(waitForFrameToBeReady) {
            return browser.driver.switchTo().frame(element(by.tagName('iframe')).getWebElement('')).then(function() {
                if (waitForFrameToBeReady !== false) {
                    return browser.waitForFrameToBeReady();
                } else {
                    return;
                }
            });
        };

        browser.waitForWholeAppToBeReady = function() {
            return browser.waitForContainerToBeReady().then(function() {
                return browser.switchToIFrame().then(function() {
                    return browser.waitForFrameToBeReady().then(function() {
                        return browser.switchToParent().then(function() {
                            //console.info("whole app is ready");
                            return;
                        });
                    });
                });
            });
        };

        browser.linkAndBackToParent = function(bySelector) {
            return browser.switchToIFrame().then(function() {
                return browser.click(bySelector).then(function() {
                    return browser.switchToParent().then(function() {
                        return browser.waitForWholeAppToBeReady();
                    });
                });
            });
        };

        browser.clickLoadPreview = function() {
            //click on load preview button
            return browser.waitForContainerToBeReady().then(function() {
                return element(by.id('loadPreview')).click();
            });
        };

        browser.switchToParent = function() {
            return browser.driver.switchTo().defaultContent();
        };

        browser.waitForUrlToMatch = function(regex) {
            browser.wait(function() {
                return browser.getCurrentUrl().then(function(url) {
                    return regex.test(url);
                });
            }, 5000, 'URL did not change');
        };

        var disableNgAnimate = function() {
            angular.module('disableNgAnimate', []).run(['$animate', function($animate) {
                $animate.enabled(false);
            }]);
        };
        browser.addMockModule('disableNgAnimate', disableNgAnimate);

        var disableCssAnimate = function() {
            angular
                .module('disableCssAnimate', [])
                .run(function() {
                    var style = document.createElement('style');
                    style.type = 'text/css';
                    style.innerHTML = '* {' +
                        /*CSS transitions*/
                        '-o-transition:none !important; ' +
                        '-moz-transition:none !important; ' +
                        '-ms-transition:none !important; ' +
                        '-webkit-transition:none !important; ' +
                        'transition:none !important; ' +
                        '}';
                    document.getElementsByTagName('head')[0].appendChild(style);
                });
        };

        browser.addMockModule('disableCssAnimate', disableCssAnimate);

        browser._getElementFromSource = function(source) {
            if (typeof source === 'string') {
                return $(source);
            } else if (source.hasOwnProperty('then')) {
                return source;
            } else {
                return element(source);
            }
        };

        browser.click = function(source, errorMessage) {
            if (!errorMessage) {
                errorMessage = "could not click on element " + source;
            }
            return browser.findElement(source, true, errorMessage).click();

        };
        browser.sendKeys = function(source, text, errorMessage) {
            var ele = this._getElementFromSource(source);
            var message = errorMessage ? errorMessage : "could not click on element " + source;
            return browser.wait(protractor.ExpectedConditions.elementToBeClickable(ele), 5000, message).then(function() {
                return ele.sendKeys(text);
            });
        };

        browser.waitUntil = function(assertionFunction, errorMessage) {
            var message = errorMessage ? errorMessage : "could not match condition";
            return browser.wait(assertionFunction, 5000, errorMessage);
        };

        browser.scrollToBottom = function(scrollElm) {
            return browser.executeScript('arguments[0].scrollTop = arguments[0].scrollHeight;', scrollElm.getWebElement());
        };

        browser.scrollToTop = function(scrollElm) {
            return browser.executeScript('arguments[0].scrollTop = 0;', scrollElm.getWebElement());
        };

        // [ wait for helpers ]

        browser.waitForVisibility = function(source) {
            return browser.waitFor(source, "visibilityOf", "Could not verify visibility of " + source);
        };

        browser.waitForInvisibility = function(source) {
            return browser.waitFor(source, "invisibilityOf", "Could not verify invisibility of " + source);
        };

        browser.waitForPresence = function(source, errorMessage) {
            if (!errorMessage) {
                errorMessage = "Could not verify presence of " + source;
            }
            return browser.waitFor(source, "presenceOf", errorMessage);
        };

        browser.waitForNonPresence = function(source) {
            return browser.wait(EC.not(EC.presenceOf(browser._getElementFromSource(source))), DEFAULT_IMPLICIT_WAIT, "Could not verify non-presence of " + source);
        };

        browser.waitFor = function(source, expectedConditions, errorMessage) {
            return browser.wait(EC[expectedConditions](browser._getElementFromSource(source)), DEFAULT_IMPLICIT_WAIT, (errorMessage ? errorMessage : "Could not verify " + expectedConditions + " for " + source));
        };

        // [ find element helper ]

        browser.findElement = function(source, searchOption, errorMessage) {

            if (!errorMessage) {
                errorMessage = "Could not find element " + source;
            }

            switch (typeof searchOption) {
                case "boolean":
                    searchOption = (searchOption) ? "visibilityOf" : "presenceOf";
                    break;
                case "string":
                    break;
                default:
                    searchOption = "presenceOf";
            }

            browser.waitFor(source, searchOption, errorMessage);
            return browser._getElementFromSource(source);

        };

        // [ scrolling helper ]

        browser.testThatOverflowingContentIsHidden = function(source) {
            var element = browser._getElementFromSource(source);
            return element.getCssValue("height").then(function(height) {
                expect(element.getAttribute("scrollHeight")).toBeGreaterThan(height.replace("px", ""));
            });
        };

        //---------------------------------------------------------------------------------
        //-----------------------------------ACTIONS---------------------------------------
        //---------------------------------------------------------------------------------

        /* all keys of protractor.Key :
         *[ 'NULL', 'CANCEL', 'HELP', 'BACK_SPACE', 'TAB', 'CLEAR', 'RETURN', 'ENTER', 'SHIFT', 'CONTROL',
         *  'ALT', 'PAUSE', 'ESCAPE', 'SPACE', 'PAGE_UP', 'PAGE_DOWN', 'END', 'HOME', 'ARROW_LEFT', 'LEFT',
         *  'ARROW_UP', 'UP', 'ARROW_RIGHT', 'RIGHT', 'ARROW_DOWN', 'DOWN', 'INSERT', 'DELETE', 'SEMICOLON',
         *  'EQUALS', 'NUMPAD0', 'NUMPAD1', 'NUMPAD2', 'NUMPAD3', 'NUMPAD4', 'NUMPAD5', 'NUMPAD6', 'NUMPAD7',
         *  'NUMPAD8', 'NUMPAD9', 'MULTIPLY', 'ADD', 'SEPARATOR', 'SUBTRACT', 'DECIMAL', 'DIVIDE', 'F1',
         *  'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9', 'F10', 'F11', 'F12', 'COMMAND', 'META', 'chord' ]
         */

        browser.press = function(protractorKey) {
            browser.actions().sendKeys(protractorKey).perform();
        };

        browser.isDelayed = false;

        var currentDelayedState;

        var controlFlow = browser.driver.controlFlow();
        var originalExecute = browser.driver.controlFlow().execute.bind(controlFlow);
        browser.driver.controlFlow().execute = function() {

            if (currentDelayedState != browser.isDelayed) {
                console.info("switched to isDelayed ", browser.isDelayed);
            }
            currentDelayedState = browser.isDelayed;

            // queue 10ms wait
            var args = arguments;
            if (browser.isDelayed) {
                return originalExecute(function() {
                    return protractor.promise.delayed(10).then(function() {
                        return originalExecute.apply(null, args);
                    });
                });
            } else {
                return originalExecute.apply(null, args);
            }
        };

        //---------------------------------------------------------------------------------
        //---------------------------------ASSERTIONS--------------------------------------
        //---------------------------------------------------------------------------------

        beforeEach(function() {
            jasmine.addMatchers({
                toEqualData: function() {
                    return {
                        compare: function(actual, expected) {
                            return {
                                pass: JSON.stringify(actual) === JSON.stringify(expected)
                            };
                        }
                    };
                },
                toBeEmptyString: function() {
                    return {
                        compare: function(actual, expected) {
                            return {
                                pass: actual === ''
                            };
                        }
                    };
                },
                toContain: function() {
                    return {
                        compare: function(actual, expected) {
                            return {
                                pass: actual.indexOf(expected) > -1
                            };
                        }
                    };
                },
                toBeDisplayed: function() {
                    return {
                        compare: function(actual) {
                            return {
                                pass: actual.isDisplayed()
                            };
                        }
                    };
                },
                toBeWithinRange: function() {
                    return {
                        compare: function(actual, expected, range) {
                            range = range || 1;
                            return {
                                pass: Math.abs(expected - actual) < range
                            };
                        }
                    };
                },
                toBeAbsent: function() {
                    return {
                        compare: function(locator) {
                            var message = 'Expected element with locator ' + locator + ' to be present in DOM';
                            return {
                                pass: browser.driver.manage().timeouts().implicitlyWait(0).then(function() {
                                    return browser.wait(function() {
                                        return element(locator).isPresent().then(function(isPresent) {
                                            return !isPresent;
                                        });
                                    }, 5000, message).then(function(result) {
                                        return browser.driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT).then(function() {
                                            return result;
                                        });
                                    });
                                }),
                                message: message
                            };
                        }
                    };
                }
            });

        });

    },
    params: {
        implicitWait: DEFAULT_IMPLICIT_WAIT
    }
};
