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
    angular.module('addPageInfoDirectiveModule', ['genericEditorModule'])
        .directive('pageInfoForm', function(GenericEditor) {
            return {
                templateUrl: 'web/common/services/genericEditor/genericEditorTemplate.html',
                restrict: 'E',
                transclude: true,
                scope: {
                    page: '=',
                    structure: '=',
                    sharedData: '=',
                    onSubmit: '&'
                },
                link: function($scope) {
                    // Initialize the structure required by the generic editor.
                    $scope.editor = new GenericEditor({
                        structureApi: null,
                        smarteditComponentType: "",
                        structure: $scope.structure,
                        customOnSubmit: $scope.onSubmit,
                        content: $scope.page
                    });

                    $scope.sharedData.editor = $scope.editor;
                    $scope.sharedData.componentForm = $scope.componentForm;
                    $scope.editor.init();
                }
            };
        });
})();
