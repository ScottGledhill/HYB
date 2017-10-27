angular.module('personalizationsmarteditContextServiceModule', ['sharedDataServiceModule', 'loadConfigModule'])
    .factory('personalizationsmarteditContextService', function($timeout, personalizationsmarteditContextServiceProxy, sharedDataService, loadConfigManagerService) {

        var ContextServiceProxy = new personalizationsmarteditContextServiceProxy('PersonalizationCtxGateway');

        var ContextService = {};
        ContextService.personalizationEnabled = false;
        ContextService.selectedCustomizations = null;
        ContextService.selectedVariations = null;
        ContextService.selectedComponents = null;
        ContextService.seExperienceData = null;
        ContextService.seConfigurationData = null;
        ContextService.sePreviewData = null;
        ContextService.pageId = null;
        ContextService.combinedView = {};
        ContextService.combinedView.enabled = false;
        ContextService.combinedView.selectedItems = null;

        ContextService.isPersonalizationContextEnabled = function() {
            return ContextService.personalizationEnabled;
        };

        ContextService.setPersonalizationContextEnabled = function(persCtxEnabled) {
            ContextService.personalizationEnabled = persCtxEnabled;
            ContextServiceProxy.setPersonalizationContextEnabled(persCtxEnabled);
        };

        ContextService.setSelectedComponents = function(newSelectedComponents) {
            ContextService.selectedComponents = newSelectedComponents;
            ContextServiceProxy.setSelectedComponents(newSelectedComponents);
        };

        ContextService.setSelectedVariations = function(newSelectedVariations) {
            ContextService.selectedVariations = newSelectedVariations;
            ContextServiceProxy.setSelectedVariations(newSelectedVariations);
        };

        ContextService.setSelectedCustomizations = function(newSelectedCustomization) {
            ContextService.selectedCustomizations = newSelectedCustomization;
            ContextServiceProxy.setSelectedCustomizations(newSelectedCustomization);
        };

        ContextService.refreshExperienceData = function() {
            sharedDataService.get('experience').then(function(data) {
                ContextService.setSeExperienceData(data);
            });
        };

        ContextService.getSeExperienceData = function() {
            return ContextService.seExperienceData;
        };

        ContextService.setSeExperienceData = function(newSeExperienceData) {
            ContextService.seExperienceData = newSeExperienceData;
            ContextServiceProxy.setSeExperienceData(newSeExperienceData);
        };

        ContextService.getSePreviewData = function() {
            return ContextService.sePreviewData;
        };

        ContextService.setSePreviewData = function(newSePreviewData) {
            ContextService.sePreviewData = newSePreviewData;
            ContextServiceProxy.setSePreviewData(newSePreviewData);
        };

        ContextService.refreshPreviewData = function() {
            sharedDataService.get('preview').then(function(data) {
                ContextService.setSePreviewData(data);
            });
        };

        ContextService.refreshConfigurationData = function() {
            loadConfigManagerService.loadAsObject().then(function(configurations) {
                ContextService.setSeConfigurationData(configurations);
            });
        };

        ContextService.getSeConfigurationData = function() {
            return ContextService.seConfigurationData;
        };

        ContextService.setSeConfigurationData = function(newSeConfigurationData) {
            ContextService.seConfigurationData = newSeConfigurationData;
            ContextServiceProxy.setSeConfigurationData(newSeConfigurationData);
        };

        ContextService.getPageId = function() {
            return ContextService.pageId;
        };

        ContextService.setPageId = function(newPageId) {
            ContextService.pageId = newPageId;
        };

        ContextService.getCombinedView = function() {
            return ContextService.combinedView;
        };

        ContextService.setCombinedView = function(newCombinedView) {
            ContextService.combinedView = newCombinedView;
            ContextServiceProxy.setCombinedView(newCombinedView);
        };

        ContextService.applySynchronization = function() {
            ContextServiceProxy.setSelectedVariations(ContextService.selectedVariations);
            ContextServiceProxy.setSelectedCustomizations(ContextService.selectedCustomizations);
            ContextServiceProxy.setSelectedComponents(ContextService.selectedComponents);
            ContextServiceProxy.setPersonalizationContextEnabled(ContextService.personalizationEnabled);
            ContextServiceProxy.setCombinedView(ContextService.combinedView);

            ContextService.refreshExperienceData();
            ContextService.refreshPreviewData();
            ContextService.refreshConfigurationData();
        };

        ContextService.getContexServiceProxy = function() {
            return ContextServiceProxy;
        };

        ContextService.closeCustomizeDropdowns = function() {
            $timeout((function() {
                try {
                    angular.element("#dropdownCustomizationsLibrary").controller('uiSelect').close();
                    angular.element("#dropdownCustomizationsStatus").controller('uiSelect').close();
                } catch (err) {}
            }), 0);
        };

        return ContextService;
    })
    .factory('personalizationsmarteditContextServiceProxy', function(gatewayProxy) {
        var proxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };
        proxy.prototype.setPersonalizationContextEnabled = function() {};
        proxy.prototype.setSelectedComponents = function() {};
        proxy.prototype.setSelectedVariations = function() {};
        proxy.prototype.setSelectedCustomizations = function() {};
        proxy.prototype.setSeExperienceData = function() {};
        proxy.prototype.setSeConfigurationData = function() {};
        proxy.prototype.setSePreviewData = function() {};
        proxy.prototype.setCombinedView = function() {};

        return proxy;
    })
    .factory('personalizationsmarteditContextServiceReverseProxy', function(gatewayProxy, personalizationsmarteditContextService) {
        var reverseProxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        reverseProxy.prototype.applySynchronization = function() {
            personalizationsmarteditContextService.applySynchronization();
        };

        reverseProxy.prototype.setPageId = function(newPageId) {
            personalizationsmarteditContextService.setPageId(newPageId);
        };

        reverseProxy.prototype.closeCustomizeDropdowns = function() {
            personalizationsmarteditContextService.closeCustomizeDropdowns();
        };

        return reverseProxy;
    });
