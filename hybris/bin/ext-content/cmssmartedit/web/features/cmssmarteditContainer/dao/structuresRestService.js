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
angular.module('structuresRestServiceModule', ['yLoDashModule'])

.service('structureModeManagerFactory', function(lodash) {

    function ModeManager(supportedModes) {

        if (!lodash.isArray(supportedModes)) {
            throw "ModeManager initialization error: supportedModes must be an array of strings";
        }

        var modes = supportedModes;

        this.getSupportedModes = function getSupportedModes() {
            return lodash.clone(modes);
        };
    }

    ModeManager.prototype.isModeSupported = function isModeSupported(mode) {
        return this.getSupportedModes().indexOf(mode) !== -1;
    };

    ModeManager.prototype.validateMode = function validateMode(mode) {
        if (this.getSupportedModes().indexOf(mode) === -1) {
            throw "ModeManager.validateMode() - mode [" + mode + "] not in list of supported modes: " + this.getSupportedModes();
        }
        return true;
    };

    return {
        createModeManager: function(modes) {
            return new ModeManager(modes);
        }
    };
})

.service('structuresRestService', function() {

    var URI = '/cmssmarteditwebservices/v1/structures/';
    var TYPE_PLACEHOLDER = ':smarteditComponentType';

    this.getUriForContext = function getUriForContext(mode, type) {
        var uri = type ? URI + type : URI + TYPE_PLACEHOLDER;
        if (mode) {
            uri = uri + '?mode=' + mode.toUpperCase();
        }
        return uri;
    };

});
