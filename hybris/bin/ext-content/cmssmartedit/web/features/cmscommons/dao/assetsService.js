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
/**
 * @ngdoc overview
 * @name assetsServiceModule
 * @description
 * # The assetsServiceModule
 *
 * The assetsServiceModule provides methods to handle assets such as images
 *
 */
angular.module('assetsServiceModule', [])
    /**
     * @ngdoc object
     * @name cmsConstantsModule.service:assetsService
     *
     * @description
     * returns the assets resources root depending whether or not we are in test mode
     */
    .factory('assetsService', function($injector) {
        return {
            getAssetsRoot: function() {
                var testAssets = false;

                if ($injector.has('testAssets')) {
                    testAssets = $injector.get('testAssets');
                }

                return testAssets ? '/web/webroot' : '/cmssmartedit';
            }
        };
    });
