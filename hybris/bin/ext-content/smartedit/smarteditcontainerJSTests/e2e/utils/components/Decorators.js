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
var perspectives = require('./Perspectives.js');

module.exports = {
    renderDecorator: function(componentId) {
        var renderButtonId = componentId + '-render-button-inner';
        return perspectives.getElementInOverlay(componentId).element(by.id(renderButtonId));
    },
    renderSlotDecorator: function(componentId) {
        var renderButtonId = componentId + '-render-slot-button-inner';
        return perspectives.getElementInOverlay(componentId).element(by.id(renderButtonId));
    },
    dirtyContentDecorator: function(componentId) {
        var renderButtonId = componentId + '-dirty-content-button';
        return perspectives.getElementInOverlay(componentId).element(by.id(renderButtonId));
    }
};
