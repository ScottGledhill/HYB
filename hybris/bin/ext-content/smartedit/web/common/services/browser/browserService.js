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
(function() {

    angular.module('browserServiceModule', [])
        .constant('SUPPORTED_BROWSERS', {
            IE: 'IE',
            CHROME: 'Chrome',
            FIREFOX: 'Firefox',
            EDGE: 'Edge',
            SAFARI: 'Safari',
            UNKNOWN: 'Uknown'
        })
        .service('browserService', function(SUPPORTED_BROWSERS) {

            this.getCurrentBrowser = function() {
                var browser = SUPPORTED_BROWSERS.UNKNOWN;
                if (typeof InstallTrigger !== 'undefined') {
                    browser = SUPPORTED_BROWSERS.FIREFOX;
                } else if (Object.prototype.toString.call(window.HTMLElement).indexOf('Constructor') > 0) {
                    browser = SUPPORTED_BROWSERS.SAFARI;
                } else if ( /*@cc_on!@*/ false || !!document.documentMode) {
                    browser = SUPPORTED_BROWSERS.IE;
                } else if (!!window.StyleMedia) {
                    browser = SUPPORTED_BROWSERS.EDGE;
                } else if (!!window.chrome && !!window.chrome.webstore) {
                    browser = SUPPORTED_BROWSERS.CHROME;
                }

                return browser;
            };

        });

})();
