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
module.exports = function() {

    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to cmssmartedit, happens to cmssmarteditContainer
     *  Try to avoid special cases and exceptions
     */

    var paths = {};


    // ################## CONFIG ##################

    paths.config = {};
    paths.config.root = 'jsTests/config';
    paths.config.protractorConf = paths.config.root + '/protractor-conf.js';

    // unit
    paths.config.unitTestUtilFiles = paths.config.root + '/unitTestUtils/**/*.js';

    // e2e



    // ################## TESTS ##################

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.reports = paths.tests.root + '/reports';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.cmssmarteditTestsRoot = paths.tests.testsRoot + '/cmssmartedit';
    paths.tests.cmssmarteditContainerTestsRoot = paths.tests.testsRoot + '/cmssmarteditContainer';
    paths.tests.cmssmarteditUnitTestsRoot = paths.tests.cmssmarteditTestsRoot + '/unit';
    paths.tests.cmssmarteditContainerUnitTestsRoot = paths.tests.cmssmarteditContainerTestsRoot + '/unit';
    paths.tests.cmssmartedite2eTestsRoot = paths.tests.cmssmarteditTestsRoot + '/e2e';
    paths.tests.cmssmarteditContainere2eTestsRoot = paths.tests.cmssmarteditContainerTestsRoot + '/e2e';

    paths.tests.cmssmarteditUnitTestFiles = paths.tests.cmssmarteditUnitTestsRoot + '/features/**/*.js';
    paths.tests.cmssmarteditContainerUnitTestFiles = paths.tests.cmssmarteditContainerUnitTestsRoot + '/features/**/*.js';
    paths.tests.cmssmartedite2eTestFiles = paths.tests.cmssmartedite2eTestsRoot + '/**/*Test.js';
    paths.tests.cmssmarteditContainere2eTestFiles = paths.tests.cmssmarteditContainere2eTestsRoot + '/**/*Test.js';



    // ################## SOURCES ##################

    paths.sources = {};

    paths.sources.root = 'web';
    paths.sources.features = paths.sources.root + '/features';

    paths.sources.images = paths.sources.root + '/webroot/images/**/*';
    paths.sources.commonsFiles = paths.sources.features + '/cmscommons/**/*.js';
    paths.sources.cmssmarteditFiles = paths.sources.features + '/cmssmartedit/**/*.js';
    paths.sources.cmssmarteditContainerFiles = paths.sources.features + '/cmssmarteditContainer/**/*.js';



    // ################## TARGET ##################

    paths.target = {};

    paths.target.features = 'jsTarget/web/features';

    paths.target.commonsTemplatesFile = paths.target.features + '/cmscommonsTemplates/**/templates.js';
    paths.target.cmssmarteditTemplatesFile = paths.target.features + '/cmssmartedit/**/templates.js';
    paths.target.cmssmarteditContainerTemplatesFile = paths.target.features + '/cmssmarteditContainer/**/templates.js';



    // ################## MOCKS ##################

    paths.mocks = {};
    paths.mocks.root = 'jsTests';

    paths.mocks.dataRoot = paths.mocks.root + '/mockData';
    paths.mocks.serviceRoot = paths.mocks.root + '/mockServices';
    paths.mocks.daoRoot = paths.mocks.root + '/mockDao';

    paths.mocks.dataFiles = paths.mocks.dataRoot + '/**/*.js';
    paths.mocks.serviceFiles = paths.mocks.serviceRoot + '/**/*.js';
    paths.mocks.daoFiles = paths.mocks.daoRoot + '/**/*.js';


    // ########## PAGE OBJECTS / COMPONENT OBJECTS ##########

    paths.testObjects = {};

    paths.testObjects.pageObjectsRoot = 'jsTests/pageObjects';
    paths.testObjects.componentObjectsRoot = 'jsTests/componentObjects';

    paths.testObjects.pageObjectsFiles = paths.testObjects.pageObjectsRoot + '/**/*.js';
    paths.testObjects.componentObjectFiles = paths.testObjects.componentObjectsRoot + '/**/*.js';



    // ################## MISC ##################

    paths.bowerRoot = 'bower_components';
    paths.seLibrariRoot = 'buildArtifacts/seLibraries';



    // ================================================================================================================
    // ================================================================================================================
    // ================================================================================================================


    paths.getCmssmarteditKarmaConfFiles = function getCmssmarteditKarmaConfFiles() {
        return [
            paths.bowerRoot + '/jquery/dist/jquery.js', //load jquery so that angular will leverage it and not serve with jqLite that has poor API
            paths.bowerRoot + '/angular/angular.js',
            paths.bowerRoot + '/angular-resource/angular-resource.js',
            paths.bowerRoot + '/angular-animate/angular-animate.js',
            paths.bowerRoot + '/angular-mocks/angular-mocks.js',
            paths.bowerRoot + '/angular-bootstrap/ui-bootstrap-tpls.min.js', //needed since contains $modal
            paths.bowerRoot + '/angular-translate/angular-translate.min.js',
            paths.bowerRoot + '/angular-translate-loader-static-files/angular-translate-loader-static-files.min.js',
            paths.bowerRoot + '/angular-ui-select/dist/select.js',
            paths.bowerRoot + '/ckeditor/ckeditor.js',
            paths.bowerRoot + '/lodash/dist/lodash.min.js',
            paths.bowerRoot + '/polyfills/**/*.js',
            paths.seLibrariRoot + '/smartEdit/services/lodash/lodashService.js',
            paths.seLibrariRoot + '/smartEdit/services/resourceLocations.js',
            paths.seLibrariRoot + '/smartEdit/services/functions.js',
            paths.config.unitTestUtilFiles,
            paths.mocks.dataFiles,
            paths.mocks.daoFiles,
            paths.mocks.serviceFiles,
            paths.testObjects.componentObjectFiles,
            paths.target.commonsTemplatesFile,
            paths.target.cmssmarteditTemplatesFile,
            paths.sources.commonsFiles,
            paths.sources.cmssmarteditFiles,
            paths.tests.cmssmarteditUnitTestFiles,

            // Images
            {
                pattern: paths.sources.images,
                watched: false,
                included: false,
                served: true
            }
        ];
    };

    paths.getCmssmarteditContainerKarmaConfFiles = function getCmssmarteditContainerKarmaConfFiles() {
        return [
            paths.bowerRoot + '/jquery/dist/jquery.js', //load jquery so that angular will leverage it and not serve with jqLite that has poor API
            paths.bowerRoot + '/angular/angular.js',
            paths.bowerRoot + '/angular-resource/angular-resource.js',
            paths.bowerRoot + '/angular-animate/angular-animate.js',
            paths.bowerRoot + '/angular-mocks/angular-mocks.js',
            paths.bowerRoot + '/angular-bootstrap/ui-bootstrap-tpls.min.js', //needed since contains $modal
            paths.bowerRoot + '/angular-translate/angular-translate.min.js',
            paths.bowerRoot + '/angular-translate-loader-static-files/angular-translate-loader-static-files.min.js',
            paths.bowerRoot + '/angular-ui-select/dist/select.js',
            paths.bowerRoot + '/ckeditor/ckeditor.js',
            paths.bowerRoot + '/lodash/dist/lodash.min.js',
            paths.bowerRoot + '/polyfills/bindPolyfill.js',
            paths.bowerRoot + '/polyfills/endsWithPolyfill.js',
            paths.bowerRoot + '/polyfills/findPolyfill.js',
            paths.bowerRoot + '/polyfills/uint8ArrayReducePolyfill.js',
            paths.seLibrariRoot + '/smartEditContainer/services/functions.js', //only reference function.js, all other dependencies for unit tests must be mocked
            paths.seLibrariRoot + '/smartEditContainer/services/resourceLocations.js',
            paths.seLibrariRoot + '/smartEditContainer/services/lodash/lodashService.js',
            paths.seLibrariRoot + '/smartEditContainer/services/genericEditor/dropdownPopulators/dropdownPopulatorInterface.js',
            paths.config.unitTestUtilFiles,
            paths.mocks.dataFiles,
            paths.mocks.daoFiles,
            paths.mocks.serviceFiles,
            paths.testObjects.componentObjectFiles,
            paths.target.cmssmarteditContainerTemplatesFile,
            paths.target.commonsTemplatesFile,
            paths.sources.cmssmarteditContainerFiles,
            paths.sources.commonsFiles,
            paths.tests.cmssmarteditContainerUnitTestFiles,
            // Images
            {
                pattern: 'web/webroot/images/**/*',
                watched: false,
                included: false,
                served: true
            }
        ];
    };

    paths.getE2eFiles = function getE2eFiles() {
        return [
            paths.tests.cmssmartedite2eTestFiles,
            paths.tests.cmssmarteditContainere2eTestFiles
            // ['jsTests/**/*pageEditorModalTest.js']
        ];
    };


    return paths;

}();
