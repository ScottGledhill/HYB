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
 * @name genericEditorModule
 */
angular.module('genericEditorModule', ['genericEditorFieldModule', 'restServiceFactoryModule', 'functionsModule', 'eventServiceModule', 'coretemplates', 'translationServiceModule', 'localizedElementModule', 'languageServiceModule', 'experienceInterceptorModule', 'dateTimePickerModule', 'fetchEnumDataHandlerModule', 'seRichTextFieldModule', 'seValidationErrorParserModule', 'seDropdownModule', 'editorFieldMappingServiceModule', 'yLoDashModule'])
    .constant('GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT', 'UnrelatedValidationErrors')
    .constant('GENERIC_EDITOR_LOADED_EVENT', 'genericEditorLoadedEvent')
    .run(function(editorFieldMappingService) {
        editorFieldMappingService._registerDefaultFieldMappings();
    })
    /**
     * @ngdoc service
     * @name genericEditorModule.service:GenericEditor
     * @description
     * The Generic Editor is a class that makes it possible for SmartEdit users (CMS managers, editors, etc.) to edit components in the SmartEdit interface.
     * The Generic Editor class is used by the {@link genericEditorModule.directive:genericEditor genericEditor} directive.
     * The genericEditor directive makes a call either to a Structure API or, if the Structure API is not available, it reads the data from a local structure to request the information that it needs to build an HTML form.
     * It then requests the component by its type and ID from the Content API. The genericEditor directive populates the form with the data that is has received.
     * The form can now be used to edit the component. The modified data is saved using the Content API if it is provided else it would return the form data itself.
     * <br/><br/>
     * <strong>The structure and the REST structure API</strong>.
     * <br/>
     * The constructor of the {@link genericEditorModule.service:GenericEditor GenericEditor} must be provided with the pattern of a REST Structure API, which must contain the string  ":smarteditComponentType", or with a local data structure.
     * If the pattern, Structure API, or the local structure is not provided, the Generic Editor will fail. If the Structure API is used, it must return a JSON payload that holds an array within the attributes property.
     * If the actual structure is used, it must return an array. Each entry in the array provides details about a component property to be displayed and edited. The following details are provided for each property:
     *
     *<ul>
     * <li><strong>qualifier:</strong> Name of the property.
     * <li><strong>i18nKey:</strong> Key of the property label to be translated into the requested language.
     * <li><strong>editable:</strong> Boolean that indicates if a property is editable or not. The default value is true.
     * <li><strong>localized:</strong> Boolean that indicates if a property is localized or not. The default value is false.
     * <li><strong>required:</strong> Boolean that indicates if a property is mandatory or not. The default value is false.
     * <li><strong>cmsStructureType:</strong> Value that is used to determine which form widget (property editor) to display for a specified property.
     * The selection is based on an extensible strategy mechanism owned by {@link editorFieldMappingServiceModule.service:editorFieldMappingService editorFieldMappingService}.
     * <li><strong>cmsStructureEnumType:</strong> The qualified name of the Enum class when cmsStructureType is "Enum"
     * </li>
     * <ul><br/>
     * The following is an example of the JSON payload that is returned by the Structure API:
     * <pre>
     * {
     *     attributes: [{
     *         cmsStructureType: "ShortString",
     *         qualifier: "someQualifier1",
     *         i18nKey: 'i18nkeyForsomeQualifier1',
     *         localized: false
     *     }, {
     *         cmsStructureType: "LongString",
     *         qualifier: "someQualifier2",
     *         i18nKey: 'i18nkeyForsomeQualifier2',
     *         localized: false
     *    }, {
     *         cmsStructureType: "RichText",
     *         qualifier: "someQualifier3",
     *         i18nKey: 'i18nkeyForsomeQualifier3',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Boolean",
     *         qualifier: "someQualifier4",
     *         i18nKey: 'i18nkeyForsomeQualifier4',
     *         localized: false
     *     }, {
     *         cmsStructureType: "Date",
     *         qualifier: "someQualifier5",
     *         i18nKey: 'i18nkeyForsomeQualifier5',
     *         localized: false
     *     }, {
     *         cmsStructureType: "Media",
     *         qualifier: "someQualifier6",
     *         i18nKey: 'i18nkeyForsomeQualifier6',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Enum",
     *         cmsStructureEnumType:'de.mypackage.Orientation'
     *         qualifier: "someQualifier7",
     *         i18nKey: 'i18nkeyForsomeQualifier7',
     *         localized: true,
     *         required: true
     *     }]
     * }
     * </pre>
     * The following is an example of the expected format of a structure:
     * <pre>
     *    [{
     *         cmsStructureType: "ShortString",
     *         qualifier: "someQualifier1",
     *         i18nKey: 'i18nkeyForsomeQualifier1',
     *         localized: false
     *     }, {
     *         cmsStructureType: "LongString",
     *         qualifier: "someQualifier2",
     *         i18nKey: 'i18nkeyForsomeQualifier2',
     *         editable: false,
     *         localized: false
     *    }, {
     *         cmsStructureType: "RichText",
     *         qualifier: "someQualifier3",
     *         i18nKey: 'i18nkeyForsomeQualifier3',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Boolean",
     *         qualifier: "someQualifier4",
     *         i18nKey: 'i18nkeyForsomeQualifier4',
     *         localized: false
     *     }, {
     *         cmsStructureType: "Date",
     *         qualifier: "someQualifier5",
     *         i18nKey: 'i18nkeyForsomeQualifier5',
     *         editable: false,
     *         localized: false
     *     }, {
     *         cmsStructureType: "Media",
     *         qualifier: "someQualifier6",
     *         i18nKey: 'i18nkeyForsomeQualifier6',
     *         localized: true,
     *         required: true
     *     }, {
     *         cmsStructureType: "Enum",
     *         cmsStructureEnumType:'de.mypackage.Orientation'
     *         qualifier: "someQualifier7",
     *         i18nKey: 'i18nkeyForsomeQualifier7',
     *         localized: true,
     *         required: true
     *     }]
     * </pre>
     * 
     * <strong>The REST CRUD API</strong>, is given to the constructor of {@link genericEditorModule.service:GenericEditor GenericEditor}.
     * The CRUD API must support GET and PUT of JSON payloads.
     * The PUT method must return the updated payload in its response. Specific to the GET and PUT, the payload must fulfill the following requirements:
     * <ul>
     * 	<li>Date types: Must be serialized as long timestamps.</li>
     * 	<li>Media types: Must be serialized as identifier strings.</li>
     * 	<li>If a cmsStructureType is localized, then we expect that the CRUD API returns a map containing the type (string or map) and the map of values, where the key is the language and the value is the content that the type returns.</li>
     * </ul>
     *
     * The following is an example of a localized payload:
     * <pre>
     * {
     *    content: {
     * 		'en': 'content in english',
     * 		'fr': 'content in french',
     * 		'hi': 'content in hindi'
     * 	  }
     * }
     * </pre>
     *
     * <br/><br/>
     *
     * If a validation error occurs, the PUT method of the REST CRUD API will return a validation error object that contains an array of validation errors. The information returned for each validation error is as follows:
     * <ul>
     * 	<li><strong>subject:</strong> The qualifier that has the error</li>
     * 	<li><strong>message:</strong> The error message to be displayed</li>
     * 	<li><strong>type:</strong> The type of error returned. This is always of the type ValidationError.</li>
     * 	<li><strong>language:</strong> The language the error needs to be associated with. If no language property is provided, a match with regular expression /(Language: \[)[a-z]{2}\]/g is attempted from the message property. As a fallback, it implies that the field is not localized.</li>
     * </ul>
     *
     * The following code is an example of an error response object:
     * <pre>
     * {
     *    errors: [{
     *		  subject: 'qualifier1',
     *		  message: 'error message for qualifier',
     *		  type: 'ValidationError'
     *	  }, {
     *		  subject: 'qualifier2',
     *		  message: 'error message for qualifier2 language: [fr]',
     *		  type: 'ValidationError'
     *    }, {
     *		  subject: 'qualifier3',
     *		  message: 'error message for qualifier2',
     *		  type: 'ValidationError'
     *    }]
     * }
     * 
     * </pre>
     *
     * Whenever any sort of dropdown is used in one of the cmsStructureType widgets, it is advised using {@link genericEditorModule.service:GenericEditor#methods_refreshOptions refreshOptions method}. See this method documentation to learn more.
     *
     */
    .factory('GenericEditor', function(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, GENERIC_EDITOR_LOADED_EVENT, lodash, restServiceFactory, languageService, sharedDataService, systemEventService, escapeHtml, sanitizeHTML, copy, isBlank, $q, $log, $translate, $injector, seValidationErrorParser, editorFieldMappingService, resetObject, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        var primitiveTypes = ["Boolean", "ShortString", "LongString", "RichText", "Date", "Dropdown"];
        var LINK_TO_TOGGLE_QUALIFIER = "linkToggle";

        editorFieldMappingService._registerDefaultFieldMappings();

        var validate = function(conf) {
            if (isBlank(conf.structure) && isBlank(conf.structureApi)) {
                throw "genericEditor.configuration.error.no.structure";
            } else if (!isBlank(conf.structure) && !isBlank(conf.structureApi)) {
                throw "genericEditor.configuration.error.2.structures";
            }
        };

        /**
         * @constructor
         */
        var GenericEditor = function(conf) {

            validate(conf);
            this.id = conf.id;
            this.smarteditComponentType = conf.smarteditComponentType;
            this.smarteditComponentId = conf.smarteditComponentId;
            this.updateCallback = conf.updateCallback;
            this.structure = conf.structure;
            if (conf.structureApi) {
                this.editorStructureService = restServiceFactory.get(conf.structureApi);
            }
            this.uriContext = conf.uriContext;
            if (conf.contentApi) {
                this.editorCRUDService = restServiceFactory.get(conf.contentApi);
            }
            this.initialContent = conf.content;
            this.component = null;
            this.fields = [];
            this.languages = [];
            this.linkToStatus = null;

            if (conf.customOnSubmit) {
                this.onSubmit = conf.customOnSubmit;
            }

            /* any GenericEditor may receive notificatino from another GenericEditor that the latter received validation errors not relevant for themselves
             * In such a situation every GenericEditor will try and see if errors our relevant to them and notify editorTabset to mark them in failure if applicable
             */
            systemEventService.registerEventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, this._handleUnrelatedValidationErrors.bind(this));
        };

        GenericEditor.prototype._handleUnrelatedValidationErrors = function(key, validationErrors) {
            // A tab only cares about unrelated validation errors in other tabs if it isn't dirty.
            // Otherwise its default handling mechanism will find the errors.
            if (!this.isDirty()) {
                this.removeValidationErrors();
                var hasErrors = this._displayValidationErrors(validationErrors);
                if (hasErrors) {
                    systemEventService.sendAsynchEvent("EDITOR_IN_ERROR_EVENT", this.id);
                }
            }

            return $q.when();
        };

        GenericEditor.prototype._isPrimitive = function(type) {
            return primitiveTypes.indexOf(type) > -1;
        };

        GenericEditor.prototype._getSelector = function(selector) {
            return $(selector);
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#reset
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Sets the content within the editor to its original state.
         *
         * @param {Object} componentForm The component form to be reset.
         */
        GenericEditor.prototype.reset = function(componentForm) {
            //need to empty the searches for refreshOptions to enable resetting to pristine state
            this._getSelector('.ui-select-search').val('');
            this._getSelector('.ui-select-search').trigger('input');
            this.removeValidationErrors();
            this.component = resetObject(this.component, this.pristine);

            this.fields.forEach(function(field) {
                delete field.initiated;
            });
            if (!this.holders) {
                this.holders = this.fields.map(function(field) {
                    return {
                        editor: this,
                        field: field
                    };
                }.bind(this));
            }
            if (componentForm) {
                componentForm.$setPristine();
            }
            return $q.when();
        };

        GenericEditor.prototype.removeValidationErrors = function() {
            for (var f = 0; f < this.fields.length; f++) {
                var field = this.fields[f];
                field.errors = undefined;
            }
        };

        GenericEditor.prototype.fetch = function() {
            return this.initialContent ? $q.when(this.initialContent) : (this.smarteditComponentId ? this.editorCRUDService.get({
                identifier: this.smarteditComponentId
            }) : $q.when({}));
        };

        GenericEditor.prototype.sanitizeLoad = function(response) {
            this.fields.forEach(function(field) {
                if (field.localized === true && isBlank(response[field.qualifier])) {
                    response[field.qualifier] = {};
                }
            });
            return response;
        };


        GenericEditor.prototype.load = function() {
            var deferred = $q.defer();
            this.fetch().then(
                function(response) {
                    this.pristine = this.sanitizeLoad(response);
                    this.reset();

                    deferred.resolve();
                }.bind(this),
                function(failure) {
                    $log.error("GenericEditor.load failed");
                    deferred.reject();
                }
            );
            return deferred.promise;
        };

        /**
         * upon submitting, server side may have been updated,
         * since we PUT and not PATCH, we need to take latest of the fields not presented and push them back with the editable ones
         */
        GenericEditor.prototype._merge = function(refreshedComponent, modifiedComponent) {

            var merger = refreshedComponent;
            modifiedComponent = copy(modifiedComponent);

            this.fields.forEach(function(field) {
                if (field.editable === true) {
                    merger[field.qualifier] = modifiedComponent[field.qualifier];
                }
            });
            if (this.linkToStatus.hasBoth()) {
                merger.external = modifiedComponent.external;
                merger.urlLink = modifiedComponent.urlLink;
            }

            return merger;
        };

        GenericEditor.prototype.getComponent = function(componentForm) {
            return this.component;
        };

        GenericEditor.prototype.sanitizePayload = function(payload, fields) {

            var CMS_STRUCTURE_TYPE = {
                SHORT_STRING: "ShortString",
                LONG_STRING: "LongString",
                LINK_TOGGLE: "LinkToggle"
            };
            var LINK_TOGGLE_QUALIFIER = "linkToggle";
            var qualifierToEscape = [];

            fields.forEach(function(field) {
                if (field.cmsStructureType === CMS_STRUCTURE_TYPE.LONG_STRING || field.cmsStructureType === CMS_STRUCTURE_TYPE.SHORT_STRING || field.cmsStructureType === CMS_STRUCTURE_TYPE.LINK_TOGGLE) {
                    qualifierToEscape.push({
                        name: field.qualifier === LINK_TOGGLE_QUALIFIER ? "urlLink" : field.qualifier,
                        localized: field.localized ? true : false
                    });
                }
            });

            qualifierToEscape.forEach(function(qualifier) {
                if (typeof payload[qualifier.name] !== 'undefined' && qualifier.name in payload) {
                    if (qualifier.localized) {
                        var qualifierValueObject = payload[qualifier.name];
                        Object.keys(qualifierValueObject).forEach(function(locale) {
                            qualifierValueObject[locale] = escapeHtml(qualifierValueObject[locale]);
                        });
                    } else {
                        payload[qualifier.name] = escapeHtml(payload[qualifier.name]);
                    }
                }
            });

            return payload;
        };

        GenericEditor.prototype._fieldsAreUserChecked = function() {
            return this.fields.every(function(field) {
                var requiresUserCheck = false;
                for (var qualifier in field.requiresUserCheck) {
                    requiresUserCheck = requiresUserCheck || field.requiresUserCheck[qualifier];
                }
                return !requiresUserCheck || field.isUserChecked;
            });
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#preparePayload
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Transforms the payload before POST/PUT to server
         *
         * @param {Object} the transformed payload
         */
        GenericEditor.prototype.preparePayload = function(originalPayload) {

            return $q.when(originalPayload);
        };

        GenericEditor.prototype.onSubmit = function() {

            return this.fetch().then(function(refreshedComponent) {
                var payload = this._merge(refreshedComponent, this.component);

                payload = this.sanitizePayload(payload, this.fields);

                if (this.smarteditComponentId) {
                    payload.identifier = this.smarteditComponentId;
                }

                // if POST mode
                if (this.editorCRUDService && !this.smarteditComponentId) {
                    // if we have a type field in the structure, use it for the type in the POST payload
                    if (this.structure && this.structure.type) {
                        // if the user already provided a type field, lets be nice
                        if (!payload.type) {
                            payload.type = this.structure.type;
                        }
                    }
                }

                return this.preparePayload(payload).then(function(preparedPayload) {

                    var promise = this.editorCRUDService ? (this.smarteditComponentId ? this.editorCRUDService.update(preparedPayload) : this.editorCRUDService.save(preparedPayload)) : $q.when(preparedPayload);
                    return promise.then(function(response) {
                        return {
                            payload: payload,
                            response: response
                        };
                    });
                }.bind(this));

            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#submit
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Saves the content within the form for a specified component. If there are any validation errors returned by the CRUD API after saving the content, it will display the errors.
         *
         * @param {Object} componentForm The component form to be saved.
         */
        GenericEditor.prototype.submit = function(componentForm) {
            var deferred = $q.defer();

            var cleanComponent = this.getComponent(componentForm);

            // It's necessary to remove validation errors even if the form is not dirty. This might be because of unrelated validation errors
            // triggered in other tab.
            this.removeValidationErrors();
            this.hasFrontEndValidationErrors = false;

            if (!this._fieldsAreUserChecked()) {
                deferred.reject(true); // Mark this tab as "in error" due to front-end validation. 
                this.hasFrontEndValidationErrors = true;
            } else if (this.isDirty() && componentForm.$valid) {
                /*
                 * upon submitting, server side may have been updated,
                 * since we PUT and not PATCH, we need to take latest of the fields not presented and send them back with the editable ones
                 */
                this.onSubmit().then(function(submitResult) {
                    // If we're doing a POST and the post returns a payload, the backend should be consistent
                    // and return as a payload, the newly created item, but in PUT mode we don't have this
                    // so we stay with the requested payload
                    if (!this.smarteditComponentId && submitResult.response) {
                        this.pristine = copy(submitResult.response);
                    } else {
                        this.pristine = copy(submitResult.payload);
                    }

                    delete this.pristine.identifier;

                    if (!this.smarteditComponentId) {
                        this.smarteditComponentId = submitResult.response.uid;
                    }
                    this.removeValidationErrors();

                    this.reset(componentForm);
                    deferred.resolve(this.pristine);
                    if (this.updateCallback) {
                        this.updateCallback(this.pristine, submitResult.response);
                    }
                }.bind(this), function(failure) {
                    this.removeValidationErrors();
                    var hasErrors = this._displayValidationErrors(failure.data.errors);
                    //send unrelated validation errors to any other listening genericEditor when no other errors
                    var unrelatedValidationErrors = this._collectUnrelatedValidationErrors(failure.data.errors);
                    if (unrelatedValidationErrors.length > 0) {
                        systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_ERRORS_EVENT, unrelatedValidationErrors);
                        deferred.reject(hasErrors); // Marks this tab if it has errors.
                    } else {
                        deferred.reject(true); // Marks this tab as "in error".
                    }
                }.bind(this));
            } else {
                deferred.resolve(cleanComponent);
            }
            return deferred.promise;
        };

        GenericEditor.prototype._displayValidationErrors = function(errors) {
            var hasErrors = false;
            errors.filter(function(error) {
                return error.type === 'ValidationError';
            }).forEach(function(validationError) {

                var field = this.fields.filter(function(element) {
                    return (element.qualifier === validationError.subject) ||
                        (validationError.subject === 'urlLink' && element.qualifier === LINK_TO_TOGGLE_QUALIFIER);
                })[0];

                if (field) {
                    if (field.errors === undefined) {
                        field.errors = [];
                    }
                    hasErrors = true;

                    var error = seValidationErrorParser.parse(validationError.message);
                    error.language = field.localized ? error.language : field.qualifier;

                    field.errors.push(error);
                }
            }.bind(this));
            return hasErrors;
        };

        GenericEditor.prototype._collectUnrelatedValidationErrors = function(errors) {

            return errors.filter(function(error) {
                return error.type === 'ValidationError' && this.fields.filter(function(field) {
                    return field.qualifier === error.subject;
                }).length === 0;
            }.bind(this));
        };

        GenericEditor.prototype.fieldAdaptor = function(fields) {

            fields.forEach(function(field) {
                var fieldMapping = editorFieldMappingService.getFieldMapping(field.cmsStructureType, this.smarteditComponentType, field.qualifier);
                lodash.assign(field, fieldMapping);

                if (field.editable === undefined) {
                    field.editable = true;
                }

                if (!field.postfixText) {
                    var key = this.smarteditComponentType.toLowerCase() + '.' + field.qualifier.toLowerCase() + '.postfix.text';
                    var translated = $translate.instant(key);
                    field.postfixText = translated != key ? translated : "";
                }

                field.smarteditComponentType = this.smarteditComponentType;
            }.bind(this));



            this.linkToStatus = fields.reduce(function(prev, next) {

                if (next.qualifier === "external") {
                    prev.hasExternal = true;
                    prev.externalI18nKey = next.i18nKey;
                }
                if (next.qualifier === "urlLink") {
                    prev.hasUrlLink = true;
                    prev.urlLinkI18nKey = next.i18nKey;
                }
                return prev;
            }, {
                hasExternal: false,
                hasUrlLink: false,
                hasBoth: function() {
                    return this.hasExternal && this.hasUrlLink;
                },
            });



            if (this.linkToStatus.hasBoth()) {
                fields = fields.filter(function(field) {
                    return field.qualifier !== "external" && field.qualifier !== "urlLink";
                });

                var linkToggleConfig = {
                    cmsStructureType: "LinkToggle",
                    qualifier: LINK_TO_TOGGLE_QUALIFIER,
                    i18nKey: "editor.linkto.label",
                    externalI18nKey: "editor.linkto.external.label",
                    internalI18nKey: "editor.linkto.internal.label",
                    smarteditComponentType: this.smarteditComponentType
                };

                var fieldMapping = editorFieldMappingService.getFieldMapping(linkToggleConfig.cmsStructureType);
                lodash.assign(linkToggleConfig, fieldMapping);

                fields.push(linkToggleConfig);

            }

            return fields;
        };

        GenericEditor.prototype.emptyUrlLink = function() {
            this.component.urlLink = '';
        };

        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#refreshOptions
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * Is invoked by HTML field templates that update and manage dropdowns.
         *  It updates the dropdown list upon initialization (creates a list of one option) and when performing a search (returns a filtered list).
         *  To do this, the GenericEditor fetches an implementation of the  {@link FetchDataHandlerInterfaceModule.FetchDataHandlerInterface FetchDataHandlerInterface} using the following naming convention: 
         * <pre>"fetch" + cmsStructureType + "DataHandler"</pre>
         * @param {Object} field The field in the structure that requires a dropdown to be built.
         * @param {string} qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
         * @param {string} search The value of the mask to filter the dropdown entries on.
         */

        GenericEditor.prototype.refreshOptions = function(field, qualifier, search) {
            var theHandlerObj = "fetch" + field.cmsStructureType + "DataHandler";
            var theIdentifier, optionsIdentifier;

            if (field.localized) {
                theIdentifier = this.component[field.qualifier][qualifier];
                optionsIdentifier = qualifier;
            } else {
                theIdentifier = this.component[field.qualifier];
                optionsIdentifier = field.qualifier;
            }

            var objHandler = $injector.get(theHandlerObj);

            field.initiated = field.initiated || [];
            field.options = field.options || {};

            if (field.cmsStructureType === 'Enum') {
                field.initiated.push(optionsIdentifier);
            }
            if (field.initiated.indexOf(optionsIdentifier) > -1) {
                if (search.length > 2 || field.cmsStructureType === 'Enum') {
                    objHandler.findByMask(field, search).then(function(entities) {
                        field.options[optionsIdentifier] = entities;
                    });
                }
            } else if (theIdentifier) {
                objHandler.getById(field, theIdentifier).then(function(entity) {
                    field.options[optionsIdentifier] = [entity];
                    field.initiated.push(optionsIdentifier);
                }.bind(field));
            } else {
                field.initiated.push(optionsIdentifier);
            }
        };

        GenericEditor.prototype._buildComparable = function(obj) {
            if (!obj) return obj;
            var comparable = {};
            var fields = this.fields;
            angular.forEach(obj, function(value, key) {
                var field = fields.filter(function(field) {
                    return field.qualifier === key && field.cmsStructureType === 'RichText';
                });

                if (field.length == 1) {
                    comparable[key] = sanitizeHTML(value, field[0].localized);
                } else {
                    comparable[key] = value;
                }
            });
            return comparable;
        };



        /**
         * @ngdoc method
         * @name genericEditorModule.service:GenericEditor#isDirty
         * @methodOf genericEditorModule.service:GenericEditor
         *
         * @description
         * A predicate function that returns true if the editor is in dirty state or false if it not.
         *  The state of the editor is determined by comparing the current state of the component with the state of the component when it was pristine.
         *
         * @return {Boolean} An indicator if the editor is in dirty state or not.
         */
        GenericEditor.prototype.isDirty = function() {
            //try to get away from angular.equals
            return !angular.equals(this._buildComparable(this.pristine), this._buildComparable(this.component));
        };

        GenericEditor.prototype.isValid = function() {
            return !this.fields.filter(function(field) {
                return field.required;
            }).map(function(field) {
                return field.qualifier;
            }).some(function(qualifier) {
                return this.component && !this.component[qualifier];
            }.bind(this));

        };

        GenericEditor.prototype._getUriContext = function() {

            return this.uriContext ? $q.when(this.uriContext) : sharedDataService.get('experience').then(function(experience) {
                var uriContext = {};
                uriContext[CONTEXT_SITE_ID] = experience.siteDescriptor.uid;
                uriContext[CONTEXT_CATALOG] = experience.catalogDescriptor.catalogId;
                uriContext[CONTEXT_CATALOG_VERSION] = experience.catalogDescriptor.catalogVersion;
                return uriContext;
            });
        };

        GenericEditor.prototype.init = function() {
            var deferred = $q.defer();

            var structurePromise = this.editorStructureService ? this.editorStructureService.get({
                smarteditComponentType: this.smarteditComponentType
            }) : $q.when({
                attributes: this.structure
            });

            structurePromise.then(
                function(structure) {

                    if (lodash.isArray(structure.structures)) {
                        if (structure.structures.length === 0) {
                            throw "GenericEditor.prototype.init: Invalid structure, no structure returned";
                        } else if (structure.structures.length > 1) {
                            throw "GenericEditor.prototype.init: Invalid structure, multiple structures returned";
                        }
                        structure = structure.structures[0];
                    }
                    this.structure = structure;


                    this._getUriContext().then(function(uriContext) {
                        languageService.getLanguagesForSite(uriContext[CONTEXT_SITE_ID]).then(function(languages) {
                            this.languages = languages;
                            this.fields = this.fieldAdaptor(structure.attributes);
                            //for setting uri params into custom widgets
                            this.parameters = {
                                siteId: uriContext[CONTEXT_SITE_ID],
                                catalogId: uriContext[CONTEXT_CATALOG],
                                catalogVersion: uriContext[CONTEXT_CATALOG_VERSION]
                            };
                            this.load().then(function() {
                                systemEventService.sendAsynchEvent(GENERIC_EDITOR_LOADED_EVENT, this.id);
                                deferred.resolve();
                            }.bind(this), function(failure) {
                                deferred.reject();
                            });
                        }.bind(this), function() {
                            $log.error("GenericEditor failed to fetch storefront languages");
                            deferred.reject();
                        });
                    }.bind(this));
                }.bind(this),
                function() {
                    $log.error("GenericEditor.init failed");
                    deferred.reject();
                });

            return deferred.promise;
        };

        return GenericEditor;

    })

/**
 * @ngdoc directive
 * @name genericEditorModule.directive:genericEditor
 * @scope
 * @restrict E
 * @element generic-editor
 *
 * @description
 * Directive responsible for generating custom HTML CRUD form for any smarteditComponent type.
 *
 * The link function has a method that creates a new instance for the {@link genericEditorModule.service:GenericEditor GenericEditor}
 * and sets the scope of smarteditComponentId and smarteditComponentType to a value that has been extracted from the original DOM element in the storefront.
 *
 * @param {String} smartedit-component-type The SmartEdit component type that is to be created, read, updated, or deleted.
 * @param {String} smartedit-component-id The identifier of the SmartEdit component that is to be created, read, updated, or deleted.
 * @param {String} structureApi An optional parameter. The data binding to a REST Structure API that fulfills the contract described in the  {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
 * @param {String} structure An optional parameter. he data binding to a REST Structure API that fulfills the contract described in the {@link genericEditorModule.service:GenericEditor GenericEditor} service. Only the Structure API or the local structure must be set.
 * @param {String} contentApi The REST API used to create, read, update, or delete content.
 * @param {Object} uriContext is an optional parameter and is used to pass the uri Params which can be used in making
 * api calls in custom widgets. It is an optional parameter and if not found, generic editor will find an experience in
 * sharedDataService and set this uriContext.
 * @param {Function} customOnSubmit An optional parameter. It exposes the inner onSubmit function to the invoker scope. If the parameter is set, the inner onSubmit is overridden by the custom one and this function must return a promise to the response format expected by the generic editor.
 * @param {Function} submit An optional parameter. It exposes the inner submit function to the invoker scope. If this parameter is set, the directive will not display an inner submit button.
 * @param {Function} reset An optional parameter. It exposes the inner reset function to the invoker scope. If this parameter is set, the directive will not display an inner cancel button.
 */
.directive('genericEditor', function(GenericEditor, isBlank, generateIdentifier) {



    return {
        templateUrl: 'web/common/services/genericEditor/genericEditorTemplate.html',
        restrict: 'E',
        transclude: true,
        replace: false,
        scope: {
            id: '=',
            smarteditComponentId: '=',
            smarteditComponentType: '=',
            structureApi: '=',
            structure: '=',
            contentApi: '=',
            uriContext: '<?',
            content: '=',
            submit: '=?',
            getComponent: '=?',
            reset: '=?',
            isDirty: '=?',
            isValid: '=?',
            updateCallback: '=',
            customOnSubmit: '=?'
        },
        link: function($scope, element, attrs) {

            $scope.editor = new GenericEditor({
                id: $scope.id || generateIdentifier(),
                smarteditComponentType: $scope.smarteditComponentType,
                smarteditComponentId: $scope.smarteditComponentId,
                structureApi: $scope.structureApi,
                structure: $scope.structure,
                contentApi: $scope.contentApi,
                updateCallback: $scope.updateCallback,
                content: $scope.content,
                uriContext: $scope.uriContext,
                customOnSubmit: $scope.customOnSubmit
            });

            $scope.editor.init();

            $scope.editor.showReset = isBlank(attrs.reset);
            $scope.editor.showSubmit = isBlank(attrs.submit);

            $scope.submitButtonText = 'componentform.actions.submit';
            $scope.cancelButtonText = 'componentform.actions.cancel';

            $scope.reset = function() {
                return $scope.editor.reset($scope.componentForm);
            };

            $scope.submit = function() {
                return $scope.editor.submit($scope.componentForm);
            };

            $scope.getComponent = function() {
                return $scope.editor.getComponent($scope.componentForm);
            };

            $scope.isDirty = function() {
                return $scope.editor.isDirty();
            };

            $scope.isValid = function() {
                return $scope.editor.isValid();
            };

            // Prevent enter key from triggering form submit
            $(element.find('.no-enter-submit')[0]).bind('keypress', function(key) {
                if (key.keyCode === 13) {
                    return false;
                }
            });


            //#################################################################################################################

            // Disable weekend selection
            $scope.editor.disabled = function(date, mode) {
                return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
            };

            $scope.editor.dateOptions = {
                formatYear: 'yy',
                startingDay: 1
            };

            $scope.editor.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate', 'MMM dd, yyyy hh:mm'];
            $scope.editor.format = $scope.editor.formats[4];
        }
    };
});