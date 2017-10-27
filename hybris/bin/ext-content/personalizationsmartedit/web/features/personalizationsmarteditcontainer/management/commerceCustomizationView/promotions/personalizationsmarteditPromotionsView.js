angular.module('personalizationsmarteditPromotionModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditCommerceCustomizationModule'
    ])
    .run(function(personalizationsmarteditCommerceCustomizationService, $filter) {

        personalizationsmarteditCommerceCustomizationService.registerType({
            type: 'cxPromotionActionData',
            text: 'personalization.modal.commercecustomization.action.type.promotion',
            template: 'web/features/personalizationsmarteditcontainer/management/commerceCustomizationView/promotions/personalizationsmarteditPromotionsTemplate.html',
            confProperty: 'personalizationsmartedit.commercecustomization.promotions.enabled',
            getName: function(action) {
                return $filter('translate')('personalization.modal.commercecustomization.promotion.display.name') + " - " + action.promotionId;
            }
        });
    })

.controller('personalizationsmarteditPromotionController', function($scope, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, personalizationsmarteditContextService, personalizationsmarteditManager) {

    $scope.promotion = null;
    $scope.availablePromotions = [];

    var getAvailablePromotions = function() {
        personalizationsmarteditRestService.getPromotions()
            .then(function successCallback(response) {
                $scope.availablePromotions = response.promotions;
            }, function errorCallback() {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingpromotions'));
            });

    };

    $scope.promotionSelected = function(item, uiSelectObject) {
        var action = {
            type: 'cxPromotionActionData',
            promotionId: item.code
        };
        $scope.addAction(action, function(a1, a2) {
            return a1.type === a2.type && a1.promotionId === a2.promotionId;
        });

        uiSelectObject.selected = null;
    };

    $scope.isItemInSelectDisabled = function(item) {
        return $scope.isItemInSelectedActions(item);
    };

    $scope.initUiSelect = function(uiSelectController) {
        uiSelectController.isActive = function(item) {
            return false;
        };

        //workarround of existing ui-select issue - remove after upgrade of ui-select library on smartedit side
        $scope.availablePromotions = JSON.parse(JSON.stringify($scope.availablePromotions));
    };

    (function init() {
        getAvailablePromotions();
    })();
});
