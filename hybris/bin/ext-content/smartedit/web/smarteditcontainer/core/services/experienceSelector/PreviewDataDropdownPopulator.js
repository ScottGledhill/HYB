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
angular.module('previewDataDropdownPopulatorModule', ['dropdownPopulatorInterfaceModule', 'siteServiceModule', 'catalogServiceModule', 'languageServiceModule', 'l10nModule'])
    /**
     * @ngdoc service
     * @name previewDataDropdownPopulatorModule.service:previewDatapreviewCatalogDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for catalog dropdown in
     * experience selector to populate the list of catalogs by making a REST call to retrieve the sites and then the catalogs based on the site.
     * 
     */
    .factory('previewDatapreviewCatalogDropdownPopulator', function($q, DropdownPopulatorInterface, extend, catalogService, siteService, l10nFilter) {

        var previewDatapreviewCatalogDropdownPopulator = function() {};

        previewDatapreviewCatalogDropdownPopulator = extend(DropdownPopulatorInterface, previewDatapreviewCatalogDropdownPopulator);

        previewDatapreviewCatalogDropdownPopulator.prototype._initCatalogVersionDropdownChoices = function(search) {
            return siteService.getSites().then(function(siteDescriptors) {

                return $q.all(siteDescriptors.map(function(siteDescriptor) {
                    return catalogService.getCatalogsForSite(siteDescriptor.uid).then(function(catalogVersionDescriptors) {

                        return catalogVersionDescriptors.map(function(catalogVersionDescriptor) {
                            var candidateDropdownChoice = {
                                id: siteDescriptor.uid + '_' + catalogVersionDescriptor.catalogId + '_' + catalogVersionDescriptor.catalogVersion,
                                label: l10nFilter(catalogVersionDescriptor.name) + ' - ' + catalogVersionDescriptor.catalogVersion,
                            };
                            return candidateDropdownChoice;
                        }).filter(function(choice) {
                            return search ? choice.label.toUpperCase().indexOf(search.toUpperCase()) > -1 : true;
                        });
                    });
                })).then(function(arrayOfArrays) {
                    return arrayOfArrays.reduce(function(accumulator, nextArray, array, index) {
                        Array.prototype.push.apply(accumulator, nextArray);
                        return accumulator;
                    }, []).sort(function(e1, e2) {
                        return e1.label.localeCompare(e2.label);
                    });
                });
            });
        };

        /**
         * @ngdoc method
         * @name previewDataDropdownPopulatorModule.service:previewDatapreviewCatalogDropdownPopulator#populate
         * @methodOf previewDataDropdownPopulatorModule.service:previewDatapreviewCatalogDropdownPopulator
         *
         * @description
         * will returns a promise resolving to a list of site - catalogs to be displayed in the experience selector.
         *
         * @param {object} payload contains the field, model and additional attributes.
         */
        previewDatapreviewCatalogDropdownPopulator.prototype.populate = function(payload) {
            return this._initCatalogVersionDropdownChoices(payload.search);
        };

        return new previewDatapreviewCatalogDropdownPopulator();
    })
    /**
     * @ngdoc service
     * @name previewDatalanguageDropdownPopulator.service:previewDatalanguageDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for language dropdown in
     * experience selector to populate the list of languages by making a REST call to retrieve the list of langauges for a given site.
     * 
     */
    .factory('previewDatalanguageDropdownPopulator', function($q, DropdownPopulatorInterface, extend, languageService) {

        var previewDatalanguageDropdownPopulator = function() {};

        previewDatalanguageDropdownPopulator = extend(DropdownPopulatorInterface, previewDatalanguageDropdownPopulator);

        previewDatalanguageDropdownPopulator.prototype._getLanguageDropdownChoices = function(siteUid, search) {
            var deferred = $q.defer();
            var languagesDropdownChoices = [];

            languageService.getLanguagesForSite(siteUid).then(function(languages) {
                languagesDropdownChoices = languages.map(function(languageDescriptor) {
                    var dropdownChoices = {};
                    dropdownChoices.id = languageDescriptor.isocode;
                    dropdownChoices.label = languageDescriptor.nativeName;
                    return dropdownChoices;
                });

                if (search) {
                    languagesDropdownChoices = languagesDropdownChoices.filter(function(language) {
                        return language.label.toUpperCase().indexOf(search.toUpperCase()) > -1;
                    });
                }

                deferred.resolve(languagesDropdownChoices);
            }, function() {
                deferred.reject();
            });

            return deferred.promise;
        };

        /**
         * @ngdoc method
         * @name previewDatalanguageDropdownPopulator.service:previewDatalanguageDropdownPopulator#populate
         * @methodOf previewDatalanguageDropdownPopulator.service:previewDatalanguageDropdownPopulator
         *
         * @description
         * will returns a promise resolving to a list of languages for a given Site ID (based on the selected catalog). The site Id is generated from the
         * selected catalog in the 'catalog' dropdown.
         *
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {object} payload.model The model descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing the assigned values.
         */
        previewDatalanguageDropdownPopulator.prototype.populate = function(payload) {

            if (payload.model[payload.field.dependsOn]) {
                var siteUid = payload.model[payload.field.dependsOn].split('_')[0];
                return this._getLanguageDropdownChoices(siteUid, payload.search);
            } else {
                return $q.when([]);
            }

        };

        return new previewDatalanguageDropdownPopulator();
    });