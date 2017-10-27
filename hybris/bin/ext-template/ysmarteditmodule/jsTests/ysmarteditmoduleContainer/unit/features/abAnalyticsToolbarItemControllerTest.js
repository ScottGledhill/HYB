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
describe('abAnalyticsToolbarItemController', function() {

    var controller;
    var mockIframeClickDetectionService;

    beforeEach(function() {
        var fixture = AngularUnitTestHelper.prepareModule('abAnalyticsToolbarItemControllerModule')
            .mock('iframeClickDetectionService', 'registerCallback')
            .controller('abAnalyticsToolbarItemController');

        controller = fixture.controller;
        mockIframeClickDetectionService = fixture.mocks.iframeClickDetectionService;
    });

    it('should put a URL to the open icon on the scope of the controller', function() {
        // Arrange

        // Act
        controller.$onInit();

        // Assert
        expect(controller.openIcon).toBe('/ysmarteditmodule/icons/icon_ab_analytics_white.png');
    });

    it('should put a URL to the closed icon on the scope of the controller', function() {
        // Arrange

        // Act
        controller.$onInit();

        // Assert
        expect(controller.closedIcon).toBe('/ysmarteditmodule/icons/icon_ab_analytics_blue.png');
    });

    it('should put a label on the scope of the controller', function() {
        // Arrange

        // Act
        controller.$onInit();

        // Assert
        expect(controller.label).toBe('AB Analytics');
    });

    it('should register a callback to the iframe click detection service', function() {
        // Arrange

        // Act
        controller.$onInit();

        // Assert
        expect(mockIframeClickDetectionService.registerCallback).toHaveBeenCalledWith('closeAbAnalytics', jasmine.any(Function));
    });
});
