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
var fs = require('fs');
var path = require('path');
var PATHS = require('./paths.js');
module.exports = function() {

    function loadDir(directory, suffix) {
        var dir = path.resolve(directory);
        var collection = fs.readdirSync(dir).reduce(function(collection, filename) {
            // standards protection
            if (!filename.endsWith(suffix)) {
                throw "Invalid filename: " + dir + '/' + filename;
            }
            var objectKey = filename.substring(0, filename.length - suffix.length);
            var module = require(dir + '/' + filename);
            collection[objectKey] = module;
            return collection;
        }, {});
        return collection;
    }

    function checkFileNames(directory, suffix) {
        var dir = path.resolve(directory);
        fs.readdirSync(dir).forEach(function(filename) {
            // standards protection
            if (!filename.endsWith(suffix)) {
                throw "Invalid filename: " + dir + '/' + filename;
            }
        });
    }


    return {
        loadE2eDependencies: function(dependencyContainer) {
            dependencyContainer.componentObjects = loadDir(PATHS.testObjects.componentObjectsRoot, 'ComponentObject.js');
            dependencyContainer.pageObjects = loadDir(PATHS.testObjects.pageObjectsRoot, 'PageObject.js');
        },

        // loadUnitDependencies: function(dependencyContainer) {
        //     dependencyContainer.mockData = loadDir(PATHS.mocks.dataRoot, 'MockData.js');
        // },

        checkUnitDependencyFileNames: function() {
            checkFileNames(PATHS.mocks.dataRoot, 'MockData.js');
            checkFileNames(PATHS.mocks.daoRoot, 'MockDao.js');
            checkFileNames(PATHS.mocks.serviceRoot, 'MockService.js');
        }

    };
}();
