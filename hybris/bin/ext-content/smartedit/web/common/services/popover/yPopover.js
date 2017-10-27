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
angular.module('yPopoverModule', ['functionsModule', 'translationServiceModule', 'ui.bootstrap', 'coretemplates'])
    .controller('YPopoverController', function($sce, isBlank) {
        this.$onChanges = function() {

            if (isBlank(this.template) && isBlank(this.templateUrl)) {
                throw "yPopover directive was invoked with neither a template nor a templateUrl";
            }
            if (!isBlank(this.template) && !isBlank(this.templateUrl)) {
                throw "yPopover directive was invoked with both a template and a templateUrl";
            }

            if (!isBlank(this.template)) {
                this.template = $sce.trustAsHtml(this.template);
            }
            this.placement = this.placement || "top";
            if (isBlank(this.trigger)) {
                this.trigger = "click";
            } else if (this.trigger == 'hover') {
                this.trigger = "mouseenter";
            }
        };
    })

/**
 * @ngdoc directive
 * @name yPopoverModule.directive:yPopover
 * @scope
 * @restrict A
 * 
 * @description
 * This directive attaches a customizable popover on a DOM element.
 * @param {String} template the HTML body to be used in the popover body, it will automatically be trusted by the directive. Optional but exactly one of either template or templateUrl must be defined.
 * @param {String} templateUrl the location of the HTML template to be used in the popover body. Optional but exactly one of either template or templateUrl must be defined.
 * @param {String} title the title to be used in the popover title section. Optional.
 * @param {String} placement the placement of the popover around the target element. Possible values are <b>top, left, right, bottom</b>, as well as any
 * concatenation of them with the following format: placement1-placement2 such as bottom-right. Optional, default value is top.
 * @param {String} trigger the event type that will trigger the popover. Possibles values are <b>hover, click, outsideClick, none</b>. Optional, default value is 'click'.
 */
.directive('yPopover', function() {
    return {
        templateUrl: 'web/common/services/popover/yPopoverTemplate.html',
        restrict: 'A',
        transclude: true,
        replace: false,
        controller: 'YPopoverController',
        controllerAs: 'ypop',
        scope: {},
        bindToController: {
            template: '<?',
            templateUrl: '<?',
            title: '<?',
            placement: "<?",
            trigger: "<?"
        }
    };
});
