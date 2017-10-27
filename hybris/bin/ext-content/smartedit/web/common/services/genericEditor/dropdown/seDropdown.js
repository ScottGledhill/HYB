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

angular.module('seDropdownModule', ['restServiceFactoryModule', 'eventServiceModule', 'optionsDropdownPopulatorModule', 'uriDropdownPopulatorModule'])
    .constant('LINKED_DROPDOWN', 'LinkedDropdown')
    .constant('CLICK_DROPDOWN', 'ClickDropdown')
    .constant('DROPDOWN_IMPLEMENTATION_SUFFIX', 'DropdownPopulator')
    .factory('SEDropdownService', function(
        $q,
        $injector,
        isBlank,
        isFunctionEmpty,
        LINKED_DROPDOWN,
        CLICK_DROPDOWN,
        DROPDOWN_IMPLEMENTATION_SUFFIX,
        systemEventService) {

        var SEDropdownService = function(conf) {
            this.field = conf.field;
            this.qualifier = conf.qualifier;
            this.model = conf.model;
            this.id = conf.id;
            this.onClickOtherDropdown = conf.onClickOtherDropdown;
            this.items = [];

        };

        SEDropdownService.prototype._respondToChange = function(key,
            handle) {
            if (this.field.dependsOn && this.field.dependsOn.split(",").indexOf(handle.qualifier) > -1) {
                this.selection = handle.optionObject;
                if (this.reset) {
                    this.reset();
                }
            }
        };


        SEDropdownService.prototype._respondToOtherClicks = function(key, qualifier) {
            if (this.field.qualifier !== qualifier && typeof this.onClickOtherDropdown === "function") {
                this.onClickOtherDropdown(key, qualifier);
            }
        };

        SEDropdownService.prototype.triggerAction = function() {
            var selectedObj = this.items.filter(function(option) {
                return option.id === this.model[this.qualifier];
            }.bind(this))[0];
            var handle = {
                qualifier: this.qualifier,
                optionObject: selectedObj
            };
            systemEventService.sendAsynchEvent(this.eventId, handle);
        };

        SEDropdownService.prototype.onClick = function() {
            systemEventService.sendAsynchEvent(this.clickEventKey, this.field.qualifier);
        };


        SEDropdownService.prototype.fetchAll = function(search) {
            return this.populator.populate({
                field: this.field,
                model: this.model,
                selection: this.selection,
                search: search,
            }).then(function(options) {
                this.items = options;
                return this.items;
            }.bind(this));

        };

        SEDropdownService.prototype.fetchEntity = function(id) {
            return this.populator.getItem({
                field: this.field,
                id: id
            });
        };

        SEDropdownService.prototype.fetchPage = function(search, pageSize, currentPage) {
            return this.populator.fetchPage({
                field: this.field,
                model: this.model,
                selection: this.selection,
                search: search,
                pageSize: pageSize,
                currentPage: currentPage
            }).then(function(page) {
                var holderProperty = Object.keys(page).filter(function(name) {
                    return name != "pagination";
                })[0];
                page.results = page[holderProperty];
                delete page[holderProperty];
                this.items = page.results;
                return page;
            }.bind(this));
        };

        SEDropdownService.prototype.init = function() {

            this.triggerAction = this.triggerAction.bind(this);

            var populatorObj;
            var injector;

            this.eventId = (this.id || '') + LINKED_DROPDOWN;
            this.clickEventKey = (this.id || '') + CLICK_DROPDOWN;

            if (this.field.dependsOn) {
                systemEventService.registerEventHandler(this.eventId, this._respondToChange.bind(this));
            }

            systemEventService.registerEventHandler(this.clickEventKey, this._respondToOtherClicks.bind(this));

            if (this.field.options && this.field.uri) {
                throw "sedropdown.contains.both.uri.and.options";
            } else if (this.field.options) {
                populatorObj = "options" + DROPDOWN_IMPLEMENTATION_SUFFIX;
                this.isPaged = false;
            } else if (this.field.uri) {
                populatorObj = "uri" + DROPDOWN_IMPLEMENTATION_SUFFIX;
                this.isPaged = this.field.paged ? this.field.paged === "true" : false;
            } else {
                if ($injector.has(this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                    populatorObj = this.field.smarteditComponentType + this.field.qualifier + DROPDOWN_IMPLEMENTATION_SUFFIX;
                } else if ($injector.has(this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX)) {
                    populatorObj = this.field.smarteditComponentType + DROPDOWN_IMPLEMENTATION_SUFFIX;
                } else {
                    throw "sedropdown.no.populator.found";
                }
                var populator = $injector.get(populatorObj);
                this.isPaged = populator.isPaged && populator.isPaged();
            }

            this.populator = $injector.get(populatorObj);

            this.fetchStrategy = {
                fetchEntity: this.fetchEntity.bind(this)
            };

            if (this.isPaged) {
                this.fetchStrategy.fetchPage = this.fetchPage.bind(this);
            } else {
                this.fetchStrategy.fetchAll = this.fetchAll.bind(this);
            }

            this.initialized = true;

        };

        return SEDropdownService;

    })

/**
 * @ngdoc directive
 * @name seDropdownModule.directive:seDropdown
 * @scope
 * @restrict E
 * @element se-dropdown
 *
 * @description
 * This directive generates a custom dropdown (standalone or dependent on another one) for the {@link genericEditorModule.service:GenericEditor genericEditor}.
 * It is an implementation of the PropertyEditorTemplate {@link editorFieldMappingServiceModule.service:PropertyEditorTemplate contract}.
 * <br/>{@link editorFieldMappingServiceModule.service:editorFieldMappingService editorFieldMappingService} maps seDropdown by default to the "EditableDropdown" cmsStructureType.
 * <br/>The dropdown will be configured and populated based on the field structure retrieved from the Structure API. The structure can contain information specific to seDropdown:
 * <ul>
 * <li><strong>options:</strong> An array of options to be populated (optional).</li>
 * <li><strong>uri:</strong> The uri to fetch the list of options from a REST call, especially if the dropdown is dependent on another one (optional).</li>
 * <li><strong>dependsOn:</strong> The qualifier of the parent dropdown that this dropdown depends on (optional).</li>
 * </ul>
 * 
 * The following is an example of the 3 possible field structures that can be returned by the Structure API for seDropdown to work:
 * <pre>
 * [
 * ...
 * {
 *		cmsStructureType: "EditableDropdown",
 *		qualifier: "someQualifier1",
 *		i18nKey: 'i18nkeyForsomeQualifier1',
 *		options: [{
 *      	id: '1',
 *      	label: 'option1'
 *      	}, {
 *      	id: '2',
 *      	label: 'option2'
 *      	}, {
 *      	id: '3',
 *      	label: 'option3'
 *      }],
 * }, {
 *		cmsStructureType: "EditableDropdown",
 *		qualifier: "someQualifier2",
 *		i18nKey: 'i18nkeyForsomeQualifier2',
 *		uri: '/someuri',
 *		dependsOn: 'someQualifier1'
 * }, {
 *		cmsStructureType: "EditableDropdown",
 *		qualifier: "someQualifier2",
 *		i18nKey: 'i18nkeyForsomeQualifier2',
 * }
 * ...
 * ]
 * </pre>
 * 
 * The field structure can contain options, uri attribute or neither of them.
 * <br/>If uri and options are not set, then seDropdown will look for an implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface}
 * with the following AngularJS recipe name:
 * <pre>smarteditComponentType + qualifier + "DropdownPopulator"</pre>
 * and default to:
 * <pre>smarteditComponentType + "DropdownPopulator"</pre>
 * If no custom populator can be found, an exception will be raised.
 * <br/><br/>For the above example, since someQualifier2 will depend on someQualifier1, then if someQualifier1 is changed, then the list of options
 * for someQualifier2 is populated by calling the populate method of {@link uriDropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator}.
 * 
 * @param {Object} The field description of the field being edited as defined by the structure API described in {@link genericEditorModule.service:GenericEditor genericEditor}.
 * @param {String} qualifier If the field is not localized, this is the actual field.qualifier, if it is localized, it is the language identifier such as en, de...
 * @param {Object} model If the field is not localized, this is the actual full parent model object, if it is localized, it is the language map: model[field.qualifier].
 * @param {String} id An identifier of the generated DOM element.
 *
 */
.directive('seDropdown', function($rootScope, SEDropdownService) {
    return {
        templateUrl: 'web/common/services/genericEditor/dropdown/seDropdownTemplate.html',
        restrict: 'E',
        transclude: true,
        replace: false,
        scope: {
            field: '=',
            qualifier: '=',
            model: '=',
            id: '=',
        },
        link: function($scope) {

            $scope.onClickOtherDropdown = function(eventKey, qualifier) {
                $scope.closeSelect();
            };

            $scope.closeSelect = function() {
                var uiSelectCtrl = $scope.getUiSelectCtrl();
                uiSelectCtrl.open = false;
            };

            $scope.getUiSelectCtrl = function() {
                var uiSelectId = "#" + $scope.field.qualifier + "-selector";
                return angular.element(uiSelectId).controller("uiSelect");
            };

            $scope.dropdown = new SEDropdownService({
                field: $scope.field,
                qualifier: $scope.qualifier,
                model: $scope.model,
                id: $scope.id,
                onClickOtherDropdown: $scope.onClickOtherDropdown
            });

            $scope.dropdown.init();

        }
    };
});
