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
describe('editorFileMappingService ', function() {

    var editorFieldMappingService;

    beforeEach(customMatchers);
    beforeEach(module('editorFieldMappingServiceModule'));
    beforeEach(inject(function(_editorFieldMappingService_) {
        editorFieldMappingService = _editorFieldMappingService_;
    }));

    it('WHEN _getMappingKey is called THEN it returns the right mapping key based on the input provided.', function() {
        // Arrange
        var structureTypeName = 'SOME STRUCTURE TYPE';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        // Act
        var result1 = editorFieldMappingService._getMappingKey(structureTypeName, componentTypeName, discriminator);
        var result2 = editorFieldMappingService._getMappingKey(structureTypeName, componentTypeName);
        var result3 = editorFieldMappingService._getMappingKey(structureTypeName);
        var result4 = editorFieldMappingService._getMappingKey(structureTypeName, null, discriminator);

        // Assert
        expect(result1).toBe(structureTypeName + '_' + componentTypeName + "_" + discriminator);
        expect(result2).toBe(structureTypeName + '_' + componentTypeName);
        expect(result3).toBe(structureTypeName);
        expect(result4).toBe(structureTypeName);
    });

    it('WHEN addFieldMapping is called THEN it stores the right mapping.', function() {
        // Arrange
        var structureTypeName = 'SOME STRUCTURE TYPE';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        var expectedKey = structureTypeName + '_' + componentTypeName + "_" + discriminator;
        var template = 'someTemplate';
        var expectedMapping = {
            template: template
        };

        expect(Object.keys(editorFieldMappingService._editorsFieldMapping).length).toBe(0);

        // Act
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, discriminator, {
            template: template
        });

        // Assert
        expect(Object.keys(editorFieldMappingService._editorsFieldMapping).length).toBe(1);
        expect(editorFieldMappingService._editorsFieldMapping[expectedKey]).toEqual(expectedMapping);

    });

    it('GIVEN a mapping already exists WHEN addFieldMapping is called THEN it overrides the mapping if the keys collide.', function() {
        // Arrange
        var structureTypeName = 'SOME STRUCTURE TYPE';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        var expectedKey = structureTypeName + '_' + componentTypeName + "_" + discriminator;
        var template = 'someTemplate';
        var expectedMapping = {
            template: template
        };

        expect(Object.keys(editorFieldMappingService._editorsFieldMapping).length).toBe(0);

        // Act
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, discriminator, {
            template: template
        });

        // Assert
        expect(editorFieldMappingService._editorsFieldMapping[expectedKey]).toEqual(expectedMapping);

    });

    it('GIVEN a mapping is defined WHEN getFieldMapping is called THEN the mapping will be returned if theres a match', function() {
        // Arrange
        var structureTypeName = 'SOME STRUCTURE TYPE';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        var config1 = {
            template: 'template1'
        };
        var config2 = {
            template: 'template2'
        };
        var config3 = {
            template: 'template3'
        };

        editorFieldMappingService.addFieldMapping(structureTypeName, null, null, config1);
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, null, config2);
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, discriminator, config3);

        // Act
        var result1 = editorFieldMappingService.getFieldMapping(structureTypeName);
        var result2 = editorFieldMappingService.getFieldMapping(structureTypeName, componentTypeName);
        var result3 = editorFieldMappingService.getFieldMapping(structureTypeName, componentTypeName, discriminator);

        // Assert
        expect(result1).toEqual(config1);
        expect(result2).toEqual(config2);
        expect(result3).toEqual(config3);
    });

    it('GIVEN a mapping is defined WHEN getFieldMapping is called THEN the mapping will be returned if there is a partial match', function() {
        // Arrange
        var structureTypeName1 = 'SOME STRUCTURE TYPE1';
        var structureTypeName2 = 'SOME STRUCTURE TYPE2';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        var config1 = {
            template: 'template1'
        };
        var config2 = {
            template: 'template2'
        };

        editorFieldMappingService.addFieldMapping(structureTypeName1, null, null, config1);
        editorFieldMappingService.addFieldMapping(structureTypeName2, componentTypeName, null, config2);

        // Act
        var result1 = editorFieldMappingService.getFieldMapping(structureTypeName1, componentTypeName);
        var result2 = editorFieldMappingService.getFieldMapping(structureTypeName1, componentTypeName, discriminator);
        var result3 = editorFieldMappingService.getFieldMapping(structureTypeName2, componentTypeName, discriminator);

        // Assert
        expect(result1).toEqual(config1);
        expect(result2).toEqual(config1);
        expect(result3).toEqual(config2);
    });


    it('GIVEN a mapping is defined WHEN getFieldMapping is called THEN the no mapping will be returned if there is no match', function() {
        // Arrange
        var structureTypeName = 'SOME STRUCTURE TYPE';
        var componentTypeName = 'SOME COMPONENT TYPE';
        var discriminator = 'SOME DISCRIMINATOR';

        var config1 = {
            template: 'template1'
        };
        var config2 = {
            template: 'template2'
        };
        var config3 = {
            template: 'template3'
        };

        editorFieldMappingService.addFieldMapping(structureTypeName, null, null, config1);
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, null, config2);
        editorFieldMappingService.addFieldMapping(structureTypeName, componentTypeName, discriminator, config3);

        // Act
        var result = editorFieldMappingService.getFieldMapping(structureTypeName + 'change', componentTypeName, discriminator);

        // Assert
        expect(result).toBe(null);
    });

    it('WHEN _registerDefaultFieldMappings is called THEN all default mappings are added', function() {
        // Arrange
        expect(Object.keys(editorFieldMappingService._editorsFieldMapping).length).toBe(0);

        // Act
        editorFieldMappingService._registerDefaultFieldMappings();

        // Assert
        var baseTemplateUrl = 'web/common/services/genericEditor/templates/';
        expect(editorFieldMappingService.getFieldMapping('Boolean')).toEqual({
            template: baseTemplateUrl + 'booleanTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('ShortString')).toEqual({
            template: baseTemplateUrl + 'shortStringTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('LongString')).toEqual({
            template: baseTemplateUrl + 'longStringTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('RichText')).toEqual({
            template: baseTemplateUrl + 'richTextTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('LinkToggle')).toEqual({
            template: baseTemplateUrl + 'linkToggleTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('Dropdown')).toEqual({
            template: baseTemplateUrl + 'dropdownTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('Date')).toEqual({
            template: baseTemplateUrl + 'dateTimePickerWrapperTemplate.html'
        });
        expect(editorFieldMappingService.getFieldMapping('Enum')).toEqual({
            template: baseTemplateUrl + 'enumTemplate.html'
        });
    });
});
