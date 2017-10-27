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
angular.module('experienceServiceModule', ['siteServiceModule', 'catalogServiceModule', 'languageServiceModule', 'sharedDataServiceModule', 'yLoDashModule'])

/**
 * @ngdoc service
 * @name experienceServiceModule.service:experienceService
 *
 * @description
 * The experience Service deals with building experience objects given a context.
 */
.factory('experienceService', function($q, $location, siteService, catalogService, languageService, sharedDataService, lodash, STOREFRONT_PATH_WITH_PAGE_ID) {

    return {
        /**
         * @ngdoc method
         * @name experienceServiceModule.service:experienceService#buildDefaultExperience
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Given an object containing a siteId, catalogId and catalogVersion, will return a reconstructed experience
         *
         * @returns {object} an experience
         */
        buildDefaultExperience: function(params) {

            var siteId = params.siteId;
            var catalogId = params.catalogId;
            var catalogVersion = params.catalogVersion;


            return siteService.getSiteById(siteId).then(function(siteDescriptor) {
                return catalogService.getCatalogsForSite(siteId).then(function(catalogVersionDescriptors) {

                    var filteredCatalogVersionDescriptors = catalogVersionDescriptors.filter(function(catalogVersionDescriptor) {
                        return catalogVersionDescriptor.catalogId == catalogId && catalogVersionDescriptor.catalogVersion == catalogVersion;
                    });
                    if (filteredCatalogVersionDescriptors.length !== 1) {
                        return $q.reject("no catalogVersionDescriptor found for _catalogId_ catalogId and _catalogVersion_ catalogVersion".replace("_catalogId_", catalogId).replace("_catalogVersion_", catalogVersion));
                    }
                    return languageService.getLanguagesForSite(siteId).then(function(languages) {
                        // Set the selected experience in the shared data service

                        var language = params.language ? languages.find(function(language) {
                            return language.isocode === params.language;
                        }) : languages[0];

                        var defaultExperience = lodash.cloneDeep(params);
                        delete defaultExperience.siteId;
                        delete defaultExperience.catalogId;
                        delete defaultExperience.catalogVersion;


                        defaultExperience.siteDescriptor = siteDescriptor;
                        defaultExperience.catalogDescriptor = filteredCatalogVersionDescriptors[0];
                        defaultExperience.languageDescriptor = language;
                        defaultExperience.time = defaultExperience.time || null;

                        return defaultExperience;
                    });
                });
            });
        },

        /**
         * @ngdoc method
         * @name iFrameManagerModule.iFrameManager#updateExperiencePageId
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Used to update the page ID stored in the current experience and reloads the page to make the changes visible.
         *
         * @param {String} newPageID the ID of the page that must be stored in the current experience.
         *
         */
        updateExperiencePageId: function(newPageID) {
            sharedDataService.get('experience').then(function(currentExperience) {
                if (!currentExperience) {
                    // Experience haven't been set. Thus, the experience hasn't been loaded. No need to update the
                    // experience then.
                    return;
                }

                currentExperience.pageId = newPageID;

                var experiencePath = this.getExperiencePath(currentExperience);
                $location.path(experiencePath).replace();

            }.bind(this));
        },

        getExperiencePath: function(experience) {
            return STOREFRONT_PATH_WITH_PAGE_ID
                .replace(":siteId", experience.siteDescriptor.uid)
                .replace(":catalogId", experience.catalogDescriptor.catalogId)
                .replace(":catalogVersion", experience.catalogDescriptor.catalogVersion)
                .replace(":pageId", experience.pageId);
        }
    };

});
