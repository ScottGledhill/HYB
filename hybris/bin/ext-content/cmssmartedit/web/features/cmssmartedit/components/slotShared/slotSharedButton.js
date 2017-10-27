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
angular.module('slotSharedButtonModule', ['slotSharedServiceModule'])
    .controller('slotSharedButtonController', function(slotSharedService, $scope, $timeout) {
        this.sharedOffImageUrl = '/cmssmartedit/images/shared_slot_menu_off.png';
        this.sharedOnImageUrl = '/cmssmartedit/images/shared_slot_menu_on.png';
        this.slotSharedFlag = false;
        this.isPopupOpened = false;
        this.buttonName = 'slotSharedButton';

        slotSharedService.isSlotShared(this.slotId).then(function(result) {
            this.slotSharedFlag = result;
        }.bind(this));

        this.openPopup = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            this.setRemainOpen({
                button: this.buttonName,
                remainOpen: true
            });
        };
    })
    .directive('slotSharedButton', function() {
        return {
            templateUrl: 'web/features/cmssmartedit/components/slotShared/slotSharedButtonTemplate.html',
            restrict: 'E',
            controller: 'slotSharedButtonController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                setRemainOpen: '&',
                active: '=',
                slotId: '@'
            }
        };
    });
