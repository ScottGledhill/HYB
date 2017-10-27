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

    /**
     * @ngdoc overview
     * @name yLoDashModule
     * @description
     * This module manages the use of the lodash library in SmartEdit. It makes sure the library is introduced
     * in the Angular lifecycle and makes it easy to mock for unit tests.
     */
    angular.module('yLoDashModule', [])
        /**
         * @ngdoc object
         * @name yLoDashModule._
         * @description
         * Makes the underscore library available to SmartEdit.
         *
         * Note: _ shouldn't be accessed directly from the window object. It should always be accessed through this angular service.
         */
        .constant('lodash', window._);

})();
