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
describe('contextAwarePageStructureService', function() {

    var contextAwarePageStructureService;
    var mockTypeStructureRestService;
    var mockPageService;

    var mockStructure;
    var mockedPrimaryResponse;
    var expectedData;

    beforeEach(function() {
        mockStructure = PageStructureMocks.getFields();
        mockedPrimaryResponse = false;
        var fixture = AngularUnitTestHelper.prepareModule('contextAwarePageStructureServiceModule')
            .mock('typeStructureRestService', 'getStructureByType')
            .mock('pageService', 'isPagePrimary')
            .service('contextAwarePageStructureService');

        contextAwarePageStructureService = fixture.service;
        mockTypeStructureRestService = fixture.mocks.typeStructureRestService;
        mockPageService = fixture.mocks.pageService;
    });

    describe('getPageStructureForNewPage', function() {
        it('should return the re-ordered fields from the API, with the label disabled, for a variation page', function() {
            // Arrange
            mockPageService.isPagePrimary.andReturnResolvedPromise(false);
            mockTypeStructureRestService.getStructureByType.andReturnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForNewPage('ContentPage', false);

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForNewVariationPage());
        });

        it('should return the re-ordered fields from the API, with the label enabled, for a primary page', function() {
            // Arrange
            mockPageService.isPagePrimary.andReturnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.andReturnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForNewPage('ContentPage', true);

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForNewPrimaryPage());
        });
    });

    describe('getPageStructureForPageEditing', function() {
        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to false, for a variation page', function() {
            // Arrange
            mockPageService.isPagePrimary.andReturnResolvedPromise(false);
            mockTypeStructureRestService.getStructureByType.andReturnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForPageEditing('ContentPage', 'dummyId');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForEditingVariationPage());
        });

        it('should return the re-ordered fields from the API, stripping the creation time and modified time fields, and setting label editable to true, for a primary page', function() {
            // Arrange
            mockPageService.isPagePrimary.andReturnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.andReturnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForPageEditing('ContentPage', 'dummyId');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForEditingPrimaryPage());
        });
    });

    describe('getPageStructureForViewing', function() {
        it('should return the re-ordered fields from the API, disabling all fields for editing, for a page', function() {
            // Arrange
            mockPageService.isPagePrimary.andReturnResolvedPromise(true);
            mockTypeStructureRestService.getStructureByType.andReturnResolvedPromise(PageStructureMocks.getFields());

            // Act
            var structurePromise = contextAwarePageStructureService.getPageStructureForViewing('ContentPage');

            // Assert
            expect(structurePromise).toBeResolvedWithData(getExpectedStructureForViewing());
        });
    });

    function getExpectedStructureForNewVariationPage() {
        return [{
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.name.name",
            "localized": false,
            "qualifier": "name"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.contentpage.label.name",
            "localized": false,
            "qualifier": "label",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.uid.name",
            "localized": false,
            "qualifier": "uid"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.title.name",
            "localized": true,
            "qualifier": "title"
        }];
    }

    function getExpectedStructureForNewPrimaryPage() {
        return [{
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.name.name",
            "localized": false,
            "qualifier": "name"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.contentpage.label.name",
            "localized": false,
            "qualifier": "label",
            "editable": true
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.uid.name",
            "localized": false,
            "qualifier": "uid"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.title.name",
            "localized": true,
            "qualifier": "title"
        }];
    }

    function getExpectedStructureForEditingVariationPage() {
        return [{
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.name.name",
            "localized": false,
            "qualifier": "name"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.contentpage.label.name",
            "localized": false,
            "qualifier": "label",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.uid.name",
            "localized": false,
            "qualifier": "uid",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.title.name",
            "localized": true,
            "qualifier": "title"
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.creationtime.name",
            "localized": false,
            "qualifier": "creationtime",
            "editable": false
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.modifiedtime.name",
            "localized": false,
            "qualifier": "modifiedtime",
            "editable": false
        }];
    }

    function getExpectedStructureForEditingPrimaryPage() {
        return [{
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.name.name",
            "localized": false,
            "qualifier": "name"
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.contentpage.label.name",
            "localized": false,
            "qualifier": "label",
            "editable": true
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.uid.name",
            "localized": false,
            "qualifier": "uid",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.title.name",
            "localized": true,
            "qualifier": "title"
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.creationtime.name",
            "localized": false,
            "qualifier": "creationtime",
            "editable": false
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.modifiedtime.name",
            "localized": false,
            "qualifier": "modifiedtime",
            "editable": false
        }];
    }

    function getExpectedStructureForViewing() {
        return [{
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.name.name",
            "localized": false,
            "qualifier": "name",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.contentpage.label.name",
            "localized": false,
            "qualifier": "label",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.uid.name",
            "localized": false,
            "qualifier": "uid",
            "editable": false
        }, {
            "cmsStructureType": "ShortString",
            "i18nKey": "type.abstractpage.title.name",
            "localized": true,
            "qualifier": "title",
            "editable": false
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.creationtime.name",
            "localized": false,
            "qualifier": "creationtime",
            "editable": false
        }, {
            "cmsStructureType": "Date",
            "i18nKey": "type.abstractpage.modifiedtime.name",
            "localized": false,
            "qualifier": "modifiedtime",
            "editable": false
        }];
    }
});
