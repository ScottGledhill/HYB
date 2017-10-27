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
angular.module('yDropDownMenuModule', [])
    .controller('yDropDownController', function() {

        this.dropdownItems.forEach(function(item) {
            item.condition = item.condition || function() {
                return true;
            };
        });
    })
    /**
     * @ngdoc directive
     * @name yDropDownMenuModule.directive:yDropDownMenu
     * @scope
     * @restrict E
     * @description
     * yDropDownMenu builds a drop-down menu. It has two parameters dropdownItems and selectedItem. The dropdownItems is an array of object which contains an key, condition and callback function. 
     * The callback function will be called when the user click on the drop down item.
     * The selectedItem is the object associated to the drop-down. It is passed as argument for the callback of dropdownItems.
     * For a given item, if a condition callback is defined, the item will show only if this callback returns true
     * Example:
     * <pre>
     *	this.dropdownItems = [{
     *       key: 'pagelist.dropdown.edit',
     *       condition:function(item){
     *       	return true;
     *       },
     *       callback: function(item) {
     *           pageEditorModalService.open(item).then(function(response) {
     *               this.reloadUpdatedPage(item.uid, response.uid);
     *           }.bind(this));
     *       }.bind(this)
     *   }, {
     *       key: 'pagelist.dropdown.sync',
     *       condition:function(item){
     *       	return false;
     *       },
     *       callback: function(item) {
     *           alert('not yet implemented');
     *       }
     *   }, {
     *       key: 'pagelist.dropdown.hide',
     *       callback: function(item) {
     *           alert('not yet implemented');
     *       }
     *   }];
     * </pre>
     */
    .component(
        'yDropDownMenu', {
            templateUrl: 'web/common/services/dropdownMenu/yDropdownMenuTemplate.html',
            transclude: true,
            controller: 'yDropDownController',
            controllerAs: 'dropdown',
            bindings: {
                dropdownItems: '<',
                selectedItem: '<'
            }
        }
    );
