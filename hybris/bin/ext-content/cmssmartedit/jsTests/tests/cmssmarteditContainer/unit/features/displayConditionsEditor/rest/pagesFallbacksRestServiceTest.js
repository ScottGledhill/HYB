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
describe('pagesFallbacksRestService', function() {

    var service;
    var resource;

    beforeEach(function() {
        resource = jasmine.createSpyObj('resource', ['get']);
        var harness = AngularUnitTestHelper.prepareModule('pagesFallbacksRestServiceModule')
            .mock('restServiceFactory', 'get').andReturn(resource)
            .service('pagesFallbacksRestService');
        service = harness.service;

        resource.get.andReturn(harness.injected.$q.when({
            uids: ['someFallbackUid', 'someOtherFallbackUid']
        }));
    });

    describe('getFallbacksForPageId', function() {
        it('should delegate to the resource GET method passing page UID and the current context if no context is provided', function() {
            service.getFallbacksForPageId('somePageUid');

            expect(resource.get).toHaveBeenCalledWith({
                pageId: 'somePageUid',
                siteUID: 'CURRENT_CONTEXT_SITE_ID',
                catalogId: 'CURRENT_CONTEXT_CATALOG',
                catalogVersion: 'CURRENT_CONTEXT_CATALOG_VERSION'
            });
        });

        it('should delegate to the resource GET method passing the given context and page UID', function() {
            service.getFallbacksForPageId('somePageUid', {
                siteUID: 'SOME_CONTEXT_SITE_ID',
                catalogId: 'SOME_CONTEXT_CATALOG',
                catalogVersion: 'SOME_CONTEXT_CATALOG_VERSION'
            });

            expect(resource.get).toHaveBeenCalledWith({
                pageId: 'somePageUid',
                siteUID: 'SOME_CONTEXT_SITE_ID',
                catalogId: 'SOME_CONTEXT_CATALOG',
                catalogVersion: 'SOME_CONTEXT_CATALOG_VERSION'
            });
        });

        it('should return the UID list from the response', function() {
            expect(service.getFallbacksForPageId('somePageUid')).toBeResolvedWithData(['someFallbackUid', 'someOtherFallbackUid']);
        });
    });

});