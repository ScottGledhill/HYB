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
angular.module('seMediaFormatModule', ['seMediaServiceModule', 'seFileSelectorModule', 'seFileValidationServiceModule', 'assetsServiceModule'])
    .constant('seMediaFormatConstants', {
        I18N_KEYS: {
            UPLOAD: 'media.format.upload',
            REPLACE: 'media.format.replace',
            UNDER_EDIT: 'media.format.under.edit',
            REMOVE: 'media.format.remove'
        },
        UPLOAD_ICON_URL: '/images/upload_image.png',
        UPLOAD_ICON_DIS_URL: '/images/upload_image_disabled.png',
        DELETE_ICON_URL: '/images/remove_image_small.png',
        REPLACE_ICON_URL: '/images/replace_image_small.png',
        ADV_INFO_ICON_URL: '/images/info_image_small.png'
    })
    .controller('seMediaFormatController', function(seMediaService, seMediaFormatConstants, seFileValidationServiceConstants, assetsService, $scope) {
        this.i18nKeys = seMediaFormatConstants.I18N_KEYS;
        this.acceptedFileTypes = seFileValidationServiceConstants.ACCEPTED_FILE_TYPES;

        var assetsRoot = assetsService.getAssetsRoot();
        this.uploadIconUrl = assetsRoot + seMediaFormatConstants.UPLOAD_ICON_URL;
        this.uploadIconDisabledUrl = assetsRoot + seMediaFormatConstants.UPLOAD_ICON_DIS_URL;

        this.deleteIconUrl = assetsRoot + seMediaFormatConstants.DELETE_ICON_URL;
        this.replaceIconUrl = assetsRoot + seMediaFormatConstants.REPLACE_ICON_URL;
        this.advInfoIconUrl = assetsRoot + seMediaFormatConstants.ADV_INFO_ICON_URL;

        this.fetchMediaForCode = function() {
            seMediaService.getMediaByCode(this.mediaCode).then(function(val) {
                this.media = val;
            }.bind(this));
        };

        this.isMediaCodeValid = function() {
            return this.mediaCode && typeof this.mediaCode === 'string';
        };

        this.getErrors = function() {
            return (this.errors || []).filter(function(error) {
                return error.format === this.mediaFormat;
            }.bind(this)).map(function(error) {
                return error.message;
            });
        };

        if (this.isMediaCodeValid()) {
            this.fetchMediaForCode();
        }

        $scope.$watch(function() {
            return this.mediaCode;
        }.bind(this), function(mediaCode, oldMediaCode) {
            if (mediaCode && typeof mediaCode === 'string') {
                if (mediaCode !== oldMediaCode) {
                    this.fetchMediaForCode();
                }
            } else {
                this.media = {};
            }
        }.bind(this));
    })
    .directive('seMediaFormat', function() {
        return {
            templateUrl: 'web/features/cmssmarteditContainer/components/genericEditor/media/components/mediaFormat/seMediaFormatTemplate.html',
            restrict: 'E',
            controller: 'seMediaFormatController',
            controllerAs: 'ctrl',
            scope: {},
            link: function(scope) {
                scope.ctrl.mediaFormatI18NKey = "media.format." + scope.ctrl.mediaFormat;
            },
            bindToController: {
                mediaCode: '=',
                mediaFormat: '=',
                isUnderEdit: '=',
                errors: '=',
                onFileSelect: '&',
                onDelete: '&'
            }
        };
    });
