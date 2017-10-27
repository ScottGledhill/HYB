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

    angular.module('_dragAndDropScrollingModule', ['browserServiceModule', 'yLoDashModule'])
        .service('_dragAndDropScrollingService', function($window, $translate, lodash, browserService, SUPPORTED_BROWSERS) {

            // Constants
            this._SCROLLING_AREA_HEIGHT = 50;
            this._FAST_SCROLLING_AREA_HEIGHT = 25;

            this._SCROLLING_STEP = 5;
            this._FAST_SCROLLING_STEP = 15;

            var TOP_SCROLL_AREA_ID = 'top_scroll_page';
            var BOTTOM_SCROLL_AREA_ID = 'bottom_scroll_page';

            // Variables
            this._topScrollArea = null;
            this._bottomScrollArea = null;

            this._initialize = function() {
                var currentBrowser = browserService.getCurrentBrowser();
                this._scrollable = (currentBrowser === SUPPORTED_BROWSERS.FIREFOX || currentBrowser === SUPPORTED_BROWSERS.IE) ?
                    this._getSelector('html') : this._getSelector('body');

                this._addScrollAreas();
                this._addEventListeners();

                this._scrollDelta = 0;
                this._initialized = true;
            };

            this._deactivate = function() {
                this._removeEventListeners();

                this._scrollDelta = 0;
                this._initialized = false;
            };

            this._enable = function() {
                if (this._initialized) {
                    // Calculate limits based on current state.
                    this._scrollLimitY = this._scrollable.outerHeight(true) - this._getSelector($window).height();
                    this._showScrollAreas();
                }
            };

            this._disable = function() {
                if (this._initialized) {
                    var scrollAreas = this._getScrollAreas();
                    scrollAreas.hide();
                }
            };

            this._addScrollAreas = function() {
                this._topScrollArea = this._getSelector('<div id="top_scroll_page" class="ySECmsScrollArea"></div>').appendTo('body');
                this._bottomScrollArea = this._getSelector('<div id="bottom_scroll_page" class="ySECmsScrollArea"></div>').appendTo('body');

                var scrollAreas = this._getScrollAreas();
                scrollAreas.height(this._SCROLLING_AREA_HEIGHT);

                this._topScrollArea.css({
                    top: 0
                });
                this._bottomScrollArea.css({
                    bottom: 0
                });

                scrollAreas.hide();

                var topMessage, bottomMessage;
                $translate('draganddrop.uihint.top').then(function(localizedTopMessage) {
                    topMessage = localizedTopMessage;
                    return $translate('draganddrop.uihint.bottom');
                }).then(function(localizedBottomMsg) {
                    bottomMessage = localizedBottomMsg;

                    this._topScrollArea.text(topMessage);
                    this._bottomScrollArea.text(bottomMessage);
                }.bind(this));
            };

            this._addEventListeners = function() {
                var scrollAreas = this._getScrollAreas();

                scrollAreas.on('dragenter', this._onDragEnter.bind(this));
                scrollAreas.on('dragover', this._onDragOver.bind(this));
                scrollAreas.on('dragleave', this._onDragLeave.bind(this));
            };

            this._removeEventListeners = function() {
                var scrollAreas = this._getScrollAreas();

                scrollAreas.off('dragenter');
                scrollAreas.off('dragover');
                scrollAreas.off('dragleave');

                scrollAreas.remove();
            };

            // Event Listeners
            this._onDragEnter = function(event) {
                var scrollDelta = this._SCROLLING_STEP;
                var scrollArea = this._getSelector(event.target);
                var scrollAreaId = scrollArea.attr('id');
                if (scrollAreaId === TOP_SCROLL_AREA_ID) {
                    scrollDelta *= -1;
                }

                this._scrollDelta = scrollDelta;

                this._animationFrameId = $window.requestAnimationFrame(this._scrollPage.bind(this));
            };

            this._onDragOver = function(evt) {
                var event = evt.originalEvent;
                var scrollArea = this._getSelector(event.target);
                var scrollAreaId = scrollArea.attr('id');

                if (scrollAreaId === TOP_SCROLL_AREA_ID) {
                    if (event.clientY <= this._FAST_SCROLLING_AREA_HEIGHT) {
                        this._scrollDelta = -this._FAST_SCROLLING_STEP;
                    } else {
                        this._scrollDelta = -this._SCROLLING_STEP;
                    }
                } else {
                    var windowHeight = this._getSelector($window).height();

                    if (event.clientY >= windowHeight - this._FAST_SCROLLING_AREA_HEIGHT) {
                        this._scrollDelta = this._FAST_SCROLLING_STEP;
                    } else {
                        this._scrollDelta = this._SCROLLING_STEP;
                    }
                }

            };

            this._onDragLeave = function(event) {
                this._scrollDelta = 0;
                $window.cancelAnimationFrame(this._animationFrameId);
            };

            this._scrollPage = function() {
                if (this._scrollDelta) {
                    var scrollTop = this._scrollable.scrollTop();
                    var continueScrolling = false;

                    if (this._scrollDelta > 0 && scrollTop < this._scrollLimitY) {
                        continueScrolling = true;
                    } else if (this._scrollDelta < 0 && scrollTop > 0) {
                        continueScrolling = true;
                    }

                    if (continueScrolling) {
                        this._scrollable.scrollTop(function(i, currentValue) {
                            return currentValue + this._scrollDelta;
                        }.bind(this));

                        this._animationFrameId = $window.requestAnimationFrame(this._scrollPage.bind(this));
                    }

                    this._showScrollAreas();
                }
            };

            this._getSelector = function(selector) {
                return $(selector);
            };

            this._getScrollAreas = function() {
                return $('#top_scroll_page, #bottom_scroll_page');
            };

            this._showScrollAreas = function() {
                var scrollTop = this._scrollable.scrollTop();

                if (scrollTop === 0) {
                    this._topScrollArea.hide();
                } else {
                    this._topScrollArea.show();
                }

                if (scrollTop >= this._scrollLimitY) {
                    this._bottomScrollArea.hide();
                } else {
                    this._bottomScrollArea.show();
                }
            };
        });

})();
