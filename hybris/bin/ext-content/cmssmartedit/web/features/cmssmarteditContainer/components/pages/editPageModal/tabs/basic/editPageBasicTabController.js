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
angular.module('editPageBasicTabControllerModule', ['resourceLocationsModule', 'pageListServiceModule',
        'pageServiceModule', 'contextAwarePageStructureServiceModule'
    ])
    .controller('editPageBasicTabController', function($scope, $q, CONTEXTUAL_PAGES_RESOURCE_URI,
        pageListService, pageService, contextAwarePageStructureService) {
        var self = this;

        this.contentApi = CONTEXTUAL_PAGES_RESOURCE_URI;

        this.$onInit = function() {
            var pagePromise = pageService.getPageById(self.model.uid);
            var isPagePrimaryPromise = pageService.isPagePrimary(self.model.uid);

            $q.all([pagePromise, isPagePrimaryPromise]).then(function(values) {
                var page = values[0];
                contextAwarePageStructureService.getPageStructureForPageEditing(page.typeCode, page.uid).then(
                    function(fields) {
                        self.structure = fields;
                    }
                );
                self.page = page;
            });
        };

        this.saveTab = function() {
            if (self.submitCallback) {
                return self.submitCallback().then(function(result) {
                    if (result.uid !== self.model.uid) {
                        self.model.newUid = result.uid;
                        return result;
                    }
                });
            } else {
                var errorMsg = 'saveTab: Save callback not defined';
                return $q.reject(errorMsg);
            }
        };
    });
