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

    angular.module('editorFieldMappingServiceModule', ['yLoDashModule'])
        /**
         * @ngdoc service
         * @name editorFieldMappingServiceModule.service:editorFieldMappingService
         * @description
         * The editorFieldMappingServices contains the strategy that the {@link genericEditorModule.directive:genericEditor genericEditor} directive
         * uses to select a property editor for a specific cmsStructureType.
         * The cmsStructureType for a specific field name is retrieved from a call to the REST Structure API. 
         * 
         * The genericEditor uses preselected property editors.
         * The genericEditor selects the property editor based on the cmsStructureType.
         * Property editors are defined for the following default cmsStructureTypes:
         * 	<ul>
         * 		<li><b>ShortString</b>:			Displays a text input field.</li>
         * 		<li><b>LongString</b>:  		Displays a text area.</li>
         * 		<li><b>RichText</b>:    		Displays an HTML/rich text editor.</li>
         * 		<li><b>Boolean</b>:     		Displays a check box.</li>
         * 		<li><b>Date</b>:        		Displays an input field with a date picker.</li>
         * 		<li><b>Media</b>:       		Displays a filterable dropdown list of media</li>
         * 		<li><b>Enum</b>:		 		Displays a filterable dropdown list of the enum class values identified by cmsStructureEnumType property.
         * 		<li><b>EditableDropdown</b>: 	Displays a configurable dropdown list that is enabled by {@link seDropdownModule.directive:seDropdown seDropdown} directive.
         * </ul>
         * 
         * You can program the {@link genericEditorModule.directive:genericEditor genericEditor} to use other property editors for these cmsStructureTypes. You can also add custom cmsStructureTypes.
         * All default and custom property editors are HTML templates. These templates must adhere to the PropertyEditorTemplate {@link editorFieldMappingServiceModule.service:PropertyEditorTemplate contract}.
         */

    /**
     * @ngdoc object
     * @name editorFieldMappingServiceModule.service:PropertyEditorTemplate
     * @description
     * The purpose of the property editor template is to assign a value to model[qualifier].
     * To achieve this goal, templates will receive the following entities in their scope:
     */
    /**
     * @ngdoc property
     * @name field
     * @propertyOf editorFieldMappingServiceModule.service:PropertyEditorTemplate
     * @description
     * The field description of the field being edited as defined by the structure API described in {@link genericEditorModule.service:GenericEditor genericEditor}
     **/
    /**
     * @ngdoc property
     * @name qualifier
     * @propertyOf editorFieldMappingServiceModule.service:PropertyEditorTemplate
     * @description
     * If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
     **/
    /**
     * @ngdoc property
     * @name model
     * @propertyOf editorFieldMappingServiceModule.service:PropertyEditorTemplate
     * @description
     * If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
     *
     */
    .service('editorFieldMappingService', function($log, lodash) {

        // Constants
        var KEY_SEPARATOR = '_';

        // Variables
        this._editorsFieldMapping = {};

        this._getMappingKey = function(structureTypeName, componentTypeName, discriminator) {
            var mappingKey = structureTypeName;
            if (componentTypeName) {
                mappingKey += KEY_SEPARATOR + componentTypeName;
                if (discriminator) {
                    mappingKey += (KEY_SEPARATOR + discriminator);
                }
            }

            return mappingKey;
        };
        /**
         * @ngdoc method
         * @name editorFieldMappingServiceModule.service:editorFieldMappingService#addFieldMapping
         * @methodOf editorFieldMappingServiceModule.service:editorFieldMappingService
         * @description
         * This method overrides the default strategy of the {@link genericEditorModule.directive:genericEditor genericEditor} directive
         * used to choose the property editor for a given cmsStructureType.
         * This method can be invoked 3 different ways (cmsStructureType and configuration are always mandatory):
         * - If you only specify cmsStructureType, your custom editor will be used for all fields of the specified cmsStructureType in any smarteditComponentType.
         * - If you specify cmsStructureType and smarteditComponentType, your custom editor will be used for all fields of the specified cmsStructureType only for the specified smarteditComponentType.
         * - If you specify  cmsStructureType, smarteditComponentType and discriminator, your custom editor will only be used for the field of the specified discriminator and cmsStructureType only for the specified smarteditComponentType.
         * 
         * If multiple overrides are set, the one which specifies the most conditions is used.
         * 
         * @param {String} cmsStructureType The cmsStructureType for which a custom property editor is required. Cannot be null.
         * @param {String} smarteditComponentType The SmartEdit component type that the custom property editor is created for. Can be null.
         * @param {String} discriminator The field name of the smarteditComponentType that the custom property editor is created for. Can be null.
         * @param {Object} configuration The holder that contains the override instructions. Cannot be null.
         * @param {String} configuration.template The path to the HTML template used in the override. Cannot be null.
         */
        this.addFieldMapping = function(structureTypeName, componentTypeName, discriminator, configuration) {
            var mappingKey = this._getMappingKey(structureTypeName, componentTypeName, discriminator);

            this._editorsFieldMapping[mappingKey] = {
                template: configuration.template
            };
        };

        this.getFieldMapping = function(structureTypeName, componentTypeName, discriminator) {
            // Go from specific to generic.
            var params = [structureTypeName, componentTypeName, discriminator];

            var fieldMapping = null;
            while (params.length > 0) {
                var mappingKey = this._getMappingKey.apply(this, params);
                fieldMapping = this._editorsFieldMapping[mappingKey];
                if (fieldMapping) {
                    break;
                }

                params.pop();
            }

            if (!fieldMapping) {
                $log.warn('editorFieldMappingService - Cannot find suitable field mapping for type ', structureTypeName);
                fieldMapping = null;
            }

            return fieldMapping;
        };

        this._registerDefaultFieldMappings = function() {
            if (lodash.isEmpty(this._editorsFieldMapping)) {
                this.addFieldMapping('Boolean', null, null, {
                    template: 'web/common/services/genericEditor/templates/booleanTemplate.html'
                });

                this.addFieldMapping('ShortString', null, null, {
                    template: 'web/common/services/genericEditor/templates/shortStringTemplate.html'
                });

                this.addFieldMapping('LongString', null, null, {
                    template: 'web/common/services/genericEditor/templates/longStringTemplate.html'
                });

                this.addFieldMapping('RichText', null, null, {
                    template: 'web/common/services/genericEditor/templates/richTextTemplate.html'
                });

                this.addFieldMapping('LinkToggle', null, null, {
                    template: 'web/common/services/genericEditor/templates/linkToggleTemplate.html'
                });

                this.addFieldMapping('Dropdown', null, null, {
                    template: 'web/common/services/genericEditor/templates/dropdownTemplate.html'
                });

                this.addFieldMapping('EditableDropdown', null, null, {
                    template: 'web/common/services/genericEditor/templates/dropdownWrapperTemplate.html'
                });

                this.addFieldMapping('Date', null, null, {
                    template: 'web/common/services/genericEditor/templates/dateTimePickerWrapperTemplate.html'
                });

                this.addFieldMapping('Enum', null, null, {
                    template: 'web/common/services/genericEditor/templates/enumTemplate.html'
                });
            }

        };
    });

})();
