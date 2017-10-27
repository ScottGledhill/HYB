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
/* jshint loopfunc:true */
/**
 * @ngdoc overview
 * @name administration
 *
 * @description
 * # The administration module
 *
 * The administration module provides services to display and manage configurations
 * that point to web service and the value property contains the URI of the web service or data.
 *
 */
angular.module('administration', ['functionsModule', 'translationServiceModule', 'ngResource', 'loadConfigModule', 'modalServiceModule', 'confirmationModalServiceModule'])
    /**
     * @ngdoc service
     * @name administration.ConfigurationEditor
     *
     * @description
     * The Configuration Editor Service is a convenience service that provides the methods to manage configurations within the Configuration Editor UI, such as filtering configurations, adding entries and removing entries.
     */
    .factory('ConfigurationEditor', function($resource, copy, $q, convertToArray, $log, loadConfigManagerService, ParseError, CONFIGURATION_URI, isBlank) {

        // Constants
        var ABSOLUTE_URI_NOT_APPROVED = "URI_EXCEPTION";
        var ABSOLUTE_URI_REGEX = /(\"[A-Za-z]+:\/|\/\/)/;

        var ConfigurationEditor = function() {
            this.editorCRUDService = $resource(CONFIGURATION_URI, {}, {
                update: {
                    method: 'PUT',
                    cache: false,
                    isArray: false
                },
                remove: {
                    method: 'DELETE',
                    cache: false,
                    isArray: false
                },
                save: {
                    method: 'POST',
                    cache: false,
                    isArray: false
                }
            });

            this.configuration = [];
        };

        ConfigurationEditor.prototype._reset = function(configurationForm) {
            this.configuration = copy(this.pristine);
            if (configurationForm) {
                configurationForm.$setPristine();
            }
            if (this.loadCallback) {
                this.loadCallback();
            }
        };

        ConfigurationEditor.prototype._addError = function(entry, type, message) {
            entry.errors = entry.errors || {};
            entry.errors[type] = entry.errors[type] || [];
            entry.errors[type].push({
                message: message
            });
        };

        ConfigurationEditor.prototype._addKeyError = function(entry, message) {
            this._addError(entry, "keys", message);
        };
        ConfigurationEditor.prototype._addValueError = function(entry, message) {
            this._addError(entry, "values", message);
        };

        ConfigurationEditor.prototype._prettify = function(array) {
            var configuration = copy(array);
            configuration.forEach(function(entry) {
                try {
                    entry.value = JSON.stringify(JSON.parse(entry.value), null, 2);
                } catch (parseError) {
                    this._addValueError(entry, 'configurationform.json.parse.error');
                }
            }.bind(this));
            return configuration;
        };


        /**
         * for editing purposes
         */
        ConfigurationEditor.prototype.loadAndPresent = function() {
            var deferred = $q.defer();

            loadConfigManagerService.loadAsArray().then(function(response) {
                    this.pristine = this._prettify(response);
                    this._reset();
                    deferred.resolve();
                }.bind(this),
                function(failure) {
                    $log.log("load failed");
                    deferred.reject();
                }
            );
            return deferred.promise;
        };

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#addEntry
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Add Entry method adds an entry to the list of configurations.
         *
         */
        ConfigurationEditor.prototype.addEntry = function() {
            this.configuration.push({
                isNew: true
            });
        };

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#removeEntry
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Remove Entry method deletes the specified entry from the list of configurations. The method does not delete the actual configuration, but just removes it from the array of configurations.
         * The entry will be deleted when a user clicks the Submit button but if the entry is new we can are removing it from the configuration
         *
         * @param {Object} entry The object to be deleted
         * @param {Object} configurationForm The form object which is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}
         * that provides methods to monitor and control the state of the form.
         */
        ConfigurationEditor.prototype.removeEntry = function(entry, configurationForm) {
            if (entry.isNew) {
                this.configuration = this.configuration.filter(function(confEntry) {
                    return !confEntry.isNew || confEntry.key !== entry.key;
                });
            } else {
                configurationForm.$setDirty();
                entry.toDelete = true;
            }
        };

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#filterConfiguration
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * Method that returns a list of configurations by filtering out only those configurations whose 'toDelete' parameter is set to false.
         *
         * @returns {Object} A list of filtered configurations.
         */
        ConfigurationEditor.prototype.filterConfiguration = function() {
            return this.configuration.filter(function(instance) {
                return instance.toDelete !== true;
            });
        };

        ConfigurationEditor.prototype._validate = function(entry) {
            try {
                if (entry.requiresUserCheck && !entry.isCheckedByUser) {
                    throw new Error(ABSOLUTE_URI_NOT_APPROVED);
                }
                return JSON.stringify(JSON.parse(entry.value));
            } catch (parseError) {
                throw new ParseError(entry.value);
            }
        };

        ConfigurationEditor.prototype._isValid = function(configurationForm) {
            this.configuration.forEach(function(entry) {
                delete entry.errors;
            });

            if (configurationForm.$invalid) {
                this.configuration.forEach(function(entry) {
                    if (isBlank(entry.key)) {
                        this._addKeyError(entry, 'configurationform.required.entry.error');
                        entry.hasErrors = true;
                    }
                    if (isBlank(entry.value)) {
                        this._addValueError(entry, 'configurationform.required.entry.error');
                        entry.hasErrors = true;
                    }
                }.bind(this));
            }
            return configurationForm.$valid && !this.configuration.reduce(function(confHolder, nextConfiguration) {
                if (confHolder.keys.indexOf(nextConfiguration.key) > -1) {
                    this._addKeyError(nextConfiguration, 'configurationform.duplicate.entry.error');
                    confHolder.errors = true;
                } else {
                    confHolder.keys.push(nextConfiguration.key);
                }
                return confHolder;
            }.bind(this), {
                keys: [],
                errors: false
            }).errors;
        };

        ConfigurationEditor.prototype._validateUserInput = function(entry) {
            if (entry.value) {
                entry.requiresUserCheck = (entry.value.match(ABSOLUTE_URI_REGEX)) ? true : false;
            }
        };

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#submit
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Submit method saves the list of available configurations by making a REST call to a web service.
         * The method is called when a user clicks the Submit button in the configuration editor.
         *
         * @param {Object} configurationForm The form object that is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}.
         * It provides methods to monitor and control the state of the form.
         */
        ConfigurationEditor.prototype.submit = function(configurationForm) {
            var deferred = $q.defer();
            if (configurationForm.$dirty && this._isValid(configurationForm)) {
                this.configuration.forEach(function(entry, i) {
                    try {
                        var payload = copy(entry);
                        delete payload.toDelete;
                        delete payload.errors;
                        var method = entry.toDelete === true ? 'remove' : (payload.isNew === true ? 'save' : 'update');
                        payload.secured = false; //needed for yaas configuration service
                        delete payload.isNew;
                        var params;
                        switch (method) {
                            case 'save':
                                payload.value = this._validate(payload);
                                params = {};
                                break;
                            case 'update':
                                payload.value = this._validate(payload);
                                params = {
                                    key: payload.key
                                };
                                break;
                            case 'remove':
                                params = {
                                    key: payload.key
                                };
                                payload = undefined;
                                break;
                        }

                        this.editorCRUDService[method](params, payload).$promise.then(
                            function(entity, index, method, response) {
                                switch (method) {
                                    case 'save':
                                        delete entity.isNew;
                                        break;
                                    case 'remove':
                                        this.configuration.splice(index, 1);
                                        break;
                                }
                            }.bind(this, entry, i, method),
                            function(failure) {
                                this._addValueError(entry, 'configurationform.save.error');
                                deferred.reject();
                            }.bind(this)
                        );
                        entry.hasErrors = false;
                    } catch (error) {
                        if (error instanceof ParseError) {
                            this._addValueError(entry, 'configurationform.json.parse.error');
                            deferred.reject();
                        }
                        entry.hasErrors = true;
                    }
                }.bind(this));
                deferred.resolve();
                configurationForm.$setPristine();
            } else {
                deferred.reject();
            }
            return deferred.promise;
        };

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#init
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The init method initializes the configuration editor and loads all the configurations so they can be edited.
         *
         * @param {Function} loadCallback The callback to be executed after loading the configurations.
         */
        ConfigurationEditor.prototype.init = function(loadCallback) {
            this.loadCallback = loadCallback;
            var deferred = $q.defer();
            this.loadAndPresent().then(function(ok) {
                deferred.resolve();
            }, function(ko) {
                deferred.reject();
            });
            return deferred.promise;
        };

        return ConfigurationEditor;

    })
    .factory('configurationService', function(ConfigurationEditor) {
        return new ConfigurationEditor();
    })

/**
 * @ngdoc directive

 * @name administration.directive:generalConfiguration
 * @restrict E
 * @element ANY
 *
 * @description
 * The Generation Configuration directive is an HTML marker. It attaches functions of the Configuration Editor to the
 * DOM elements of the General Configuration Template in order to display the configuration editor.
 *
 */
.directive('generalConfiguration', function(modalService, $log, MODAL_BUTTON_ACTIONS, MODAL_BUTTON_STYLES, confirmationModalService) {
    return {
        templateUrl: 'web/smarteditcontainer/modules/administrationModule/generalConfigurationTemplate.html',
        restrict: 'E',
        transclude: true,
        replace: true,
        link: function($scope) {
            $scope.editConfiguration = function() {
                modalService.open({
                    title: 'modal.administration.configuration.edit.title',
                    templateUrl: 'web/smarteditcontainer/modules/administrationModule/editConfigurationsTemplate.html',
                    controller: ['$scope', '$timeout', 'configurationService', '$q', 'modalManager',
                        function($scope, $timeout, configurationService, $q, modalManager) {
                            this.isDirty = false;
                            $scope.form = {};

                            this.onSave = function() {
                                $scope.editor.submit($scope.form.configurationForm).then(function() {
                                    modalManager.close();
                                });
                            };

                            this.onCancel = function() {
                                var deferred = $q.defer();

                                if (this.isDirty) {
                                    confirmationModalService.confirm({
                                        description: 'editor.cancel.confirm'
                                    }).then(function() {
                                        modalManager.close();
                                        deferred.resolve();
                                    }.bind(this), function() {
                                        deferred.reject();
                                    });
                                } else {
                                    deferred.resolve();
                                }

                                return deferred.promise;
                            };

                            this.init = function() {
                                modalManager.setDismissCallback((this.onCancel).bind(this));

                                modalManager.setButtonHandler(function(buttonId) {
                                    switch (buttonId) {
                                        case 'save':
                                            return this.onSave();
                                        case 'cancel':
                                            return this.onCancel();
                                        default:
                                            $log.error('A button callback has not been registered for button with id', buttonId);
                                            break;
                                    }
                                }.bind(this));

                                $scope.$watch(function() {
                                    var isDirty = $scope.form.configurationForm && $scope.form.configurationForm.$dirty;
                                    var isValid = $scope.form.configurationForm && $scope.form.configurationForm.$valid;
                                    return {
                                        isDirty: isDirty,
                                        isValid: isValid
                                    };
                                }.bind(this), function(obj) {
                                    if (typeof obj.isDirty === 'boolean') {
                                        if (obj.isDirty) {
                                            this.isDirty = true;
                                            modalManager.enableButton('save');
                                        } else {
                                            this.isDirty = false;
                                            modalManager.disableButton('save');
                                        }
                                    }
                                }.bind(this), true);
                            };

                            $scope.editor = configurationService;
                            $scope.editor.init(function() {
                                $timeout(function() {
                                    $("textarea").each(
                                        function(index, textarea) {
                                            $(this).height(this.scrollHeight);
                                        });
                                }, 100);
                            });
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
                        action: MODAL_BUTTON_ACTIONS.NONE,
                        disabled: true
                    }]
                });
            };
        }
    };
});