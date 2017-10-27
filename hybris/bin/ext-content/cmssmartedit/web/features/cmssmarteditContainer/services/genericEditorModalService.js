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
 * @name genericEditorModalServiceModule
 * @description
 * # The genericEditorModalServiceModule
 *
 * The generic editor modal service module provides a service to open an editor modal window with a tabset inside. The editor modal is populated with a save
 * and cancel button, and allows specifying the different editor tabs.
 *
 */
angular.module('genericEditorModalServiceModule', ['modalServiceModule', 'gatewayProxyModule', 'coretemplates', 'editorTabsetModule', 'confirmationModalServiceModule'])
    /**
     * @ngdoc service
     * @name genericEditorModalServiceModule.service:genericEditorModalService
     *
     * @description
     * The Generic Editor Modal Service is used to open an editor modal window that contains a tabset.
     *
     */
    .factory('genericEditorModalService', function(modalService, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, systemEventService, gatewayProxy, confirmationModalService, $q, $log) {
        function GenericEditorModalService() {}

        /**
         * @ngdoc method
         * @name genericEditorModalServiceModule.service:genericEditorModalService#open
         * @methodOf genericEditorModalServiceModule.service:genericEditorModalService
         *
         * @description
         * Function that opens an editor modal. For this method, you must specify the list of tabs to be displayed in the tabset, an object to contain the edited information, and a save
         * callback that will be triggered once the Save button is clicked.
         *
         * @param {Object} data The object that contains the information to be displayed and edited in the modal.
         * @param {Object} tabs The list of tabs to be displayed in the tabset. Note that each of the tabs must follow the contract specified by the {@link editorTabsetModule.directive:editorTabset editor tabset} .
         * @param {String} tabs.id The ID of the current tab.
         * @param {String} tabs.title The localization key of the title to be displayed for the current tab.
         * @param {String} tabs.templateUrl The URL of the HTML template to be displayed within the current tab.
         * @param {Object} saveCallback a function executed if the user clicks the Save button and the modal closes successfully.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        GenericEditorModalService.prototype.open = function(data, tabs, saveCallback) {

            return modalService.open({
                title: data.title,
                titleSuffix: 'editor.title.suffix',
                templateInline: '<editor-tabset control="modalController.tabsetControl" model="modalController.data" tabs="modalController.tabs"></editor-tabset>',
                controller: ['genericEditorModalService', 'modalManager', 'systemEventService', 'hitch', '$scope', '$log', '$q',
                    function(genericEditorModalService, modalManager, systemEventService, hitch, $scope, $log, $q) {
                        this.isDirty = false;

                        this.data = data;

                        this.tabs = tabs;

                        this.onSave = function() {
                            return this.tabsetControl.saveTabs().then(function(item) {
                                saveCallback();
                                return item;
                            });
                        };

                        this.onCancel = function() {
                            var deferred = $q.defer();
                            if (this.isDirty) {
                                confirmationModalService.confirm({
                                    description: 'editor.cancel.confirm'
                                }).then(hitch(this, function() {
                                    this.tabsetControl.cancelTabs().then(function() {
                                        deferred.resolve();
                                    }, function() {
                                        deferred.reject();
                                    });
                                }), function() {
                                    deferred.reject();
                                });
                            } else {
                                deferred.resolve();
                            }

                            return deferred.promise;
                        };

                        this.init = function() {
                            modalManager.setDismissCallback(hitch(this, this.onCancel));

                            modalManager.setButtonHandler(hitch(this, function(buttonId) {
                                switch (buttonId) {
                                    case 'save':
                                        return this.onSave();
                                    case 'cancel':
                                        return this.onCancel();
                                    default:
                                        $log.error('A button callback has not been registered for button with id', buttonId);
                                        break;
                                }
                            }));

                            $scope.$watch(hitch(this, function() {
                                var isDirty = this.tabsetControl && typeof this.tabsetControl.isDirty === 'function' && this.tabsetControl.isDirty();
                                return isDirty;
                            }), hitch(this, function(isDirty) {
                                if (typeof isDirty === 'boolean') {
                                    if (isDirty) {
                                        this.isDirty = true;
                                        modalManager.enableButton('save');
                                    } else {
                                        this.isDirty = false;
                                        modalManager.disableButton('save');
                                    }
                                }
                            }));
                        };
                    }
                ],
                buttons: [{
                    id: 'cancel',
                    label: 'component.confirmation.modal.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'save',
                    label: 'component.confirmation.modal.save',
                    action: MODAL_BUTTON_ACTIONS.CLOSE,
                    disabled: true
                }]
            });
        };



        return new GenericEditorModalService();
    });
