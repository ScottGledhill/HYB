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
angular.module('experienceSelectorModule', ['eventServiceModule', 'genericEditorModule', 'sharedDataServiceModule', 'iframeClickDetectionServiceModule', 'iFrameManagerModule', 'siteServiceModule', 'genericEditorModule', 'resourceLocationsModule', 'previewDataDropdownPopulatorModule', 'experienceServiceModule', 'yLoDashModule'])
    .directive('experienceSelector', function($filter, $document, systemEventService, siteService, sharedDataService, iframeClickDetectionService, iFrameManager, GenericEditor, experienceService, EVENTS, TYPES_RESOURCE_URI, PREVIEW_RESOURCE_URI, lodash) {
        return {
            templateUrl: 'web/common/services/genericEditor/genericEditorTemplate.html',
            restrict: 'E',
            transclude: true,
            scope: {
                experience: '=',
                dropdownStatus: '=',
                resetExperienceSelector: '='
            },
            link: function($scope) {
                var selectedExperience = {};
                var siteCatalogs = {};

                $scope.resetExperienceSelector = function() {
                    sharedDataService.get('experience').then(function(experience) {

                        selectedExperience = lodash.cloneDeep(experience);
                        delete selectedExperience.siteDescriptor;
                        delete selectedExperience.languageDescriptor;

                        selectedExperience.previewCatalog = experience.siteDescriptor.uid + '_' + experience.catalogDescriptor.catalogId + '_' + experience.catalogDescriptor.catalogVersion;
                        selectedExperience.language = experience.languageDescriptor.isocode;
                        $scope.setFields();
                    });
                };

                $scope.setFields = function() {
                    sharedDataService.get('configuration').then(function(configuration) {
                        $scope.editor = new GenericEditor({
                            smarteditComponentType: 'previewData',
                            smarteditComponentId: null,
                            structureApi: TYPES_RESOURCE_URI + '/:smarteditComponentType',
                            contentApi: configuration && configuration.previewTicketURI || PREVIEW_RESOURCE_URI,
                            updateCallback: null,
                            content: selectedExperience
                        });

                        $scope.editor.alwaysShowReset = true;
                        $scope.editor.alwaysShowSubmit = true;

                        $scope.editor.init();

                        $scope.editor.preparePayload = function(originalPayload) {

                            siteCatalogs.siteId = originalPayload.previewCatalog.split('_')[0];
                            siteCatalogs.catalogId = originalPayload.previewCatalog.split('_')[1];
                            siteCatalogs.catalogVersion = originalPayload.previewCatalog.split('_')[2];

                            return sharedDataService.get('configuration').then(function(configuration) {

                                return sharedDataService.get('experience').then(function(experience) {

                                    return siteService.getSiteById(siteCatalogs.siteId).then(function(siteDescriptor) {

                                        var transformedPayload = lodash.cloneDeep(originalPayload);
                                        delete transformedPayload.previewCatalog;
                                        delete transformedPayload.time;

                                        transformedPayload.catalog = siteCatalogs.catalogId;
                                        transformedPayload.catalogVersion = siteCatalogs.catalogVersion;
                                        transformedPayload.resourcePath = configuration.domain + siteDescriptor.previewUrl;
                                        transformedPayload.pageId = experience.pageId;
                                        transformedPayload.time = originalPayload.time;

                                        return transformedPayload;

                                    }.bind(this));

                                }.bind(this));

                            }.bind(this));
                        };

                        $scope.editor.updateCallback = function(payload, response) {
                            delete this.smarteditComponentId; //to force a permanent POST
                            $scope.dropdownStatus.isopen = false;
                            sharedDataService.get('configuration').then(function(configuration) {
                                // First ensure that proper clean-up is performed before changing experiences.
                                systemEventService.sendAsynchEvent(EVENTS.CLEAR_PERSPECTIVE_FEATURES);
                                // Then perform the actual update.

                                var experienceParams = lodash.cloneDeep(response);
                                delete experienceParams.catalog;
                                delete experienceParams.time;
                                experienceParams.siteId = siteCatalogs.siteId;
                                experienceParams.catalogId = siteCatalogs.catalogId;
                                experienceParams.time = $filter('date')(payload.time, $scope.editor.format);
                                experienceParams.pageId = response.pageId;

                                experienceService.buildDefaultExperience(experienceParams).then(function(experience) {
                                    sharedDataService.set('experience', experience).then(function() {
                                        systemEventService.sendAsynchEvent("experienceUpdate");
                                        var fullPreviewUrl = configuration.domain + experience.siteDescriptor.previewUrl;
                                        iFrameManager.loadPreview(fullPreviewUrl, response.ticketId);
                                        var preview = {
                                            previewTicketId: response.ticketId,
                                            resourcePath: fullPreviewUrl
                                        };
                                        sharedDataService.set('preview', preview);
                                    });
                                });

                            });
                        };
                    });

                };

                $scope.submitButtonText = 'componentform.actions.apply';
                $scope.cancelButtonText = 'componentform.actions.cancel';
                $scope.modalHeaderTitle = 'experience.selector.header';

                $scope.reset = function() {
                    $scope.editor.reset($scope.componentForm);
                    $scope.dropdownStatus.isopen = false;
                };

                $scope.submit = function() {
                    return $scope.editor.submit($scope.componentForm);
                };

                ///////////////////////////////////////////////////////////////////////
                // Close on clicking away from the experience selector
                ///////////////////////////////////////////////////////////////////////

                $document.on('click', function(event) {
                    if ($(event.target).parents('.ySEPreviewSelector').length <= 0) {
                        if ($(event.target).parents('.ui-select-choices-row').length > 0) {
                            return;
                        }

                        if ($scope.dropdownStatus && $scope.dropdownStatus.isopen) {
                            $scope.editor.reset($scope.componentForm);
                            $scope.dropdownStatus.isopen = false;
                            $scope.$apply();
                        }
                    }
                });

                iframeClickDetectionService.registerCallback('closeExperienceSelector', function() {
                    if ($scope.dropdownStatus && $scope.dropdownStatus.isopen) {
                        $scope.editor.reset($scope.componentForm);
                        $scope.dropdownStatus.isopen = false;
                    }
                });
            }
        };
    });
