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
describe("Synchronization polling service when non active version- ", function() {

    var harness, syncPollingService, $q, $rootScope, pageSynchronizationGetRestService, pageSynchronizationPostRestService;
    var mockTimer, systemEventServiceMock, componentHandlerServiceMock, crossFrameEventServiceMock, catalogServiceMock;

    var synchronizationMockData = unit.mockData.synchronization;
    var pageId1_SyncStatus = new synchronizationMockData().PAGE_ID1_SYNC_STATUS;
    var pageId2_SyncStatus = new synchronizationMockData().PAGE_ID2_SYNC_STATUS;

    var SYNCHRONIZATION_SLOW_POLLING_TIME = 20000;
    var SYNCHRONIZATION_FAST_POLLING_TIME = 2000;
    var SYNC_POLLING_SPEED_UP = 'syncPollingSpeedUp';
    var SYNC_POLLING_SLOW_DOWN = 'syncPollingSlowDown';

    beforeEach(function() {

        pageSynchronizationGetRestService = jasmine.createSpyObj('pageSynchronizationGetRestService', ['get']);
        pageSynchronizationPostRestService = jasmine.createSpyObj('pageSynchronizationPostRestService', ['save']);

        mockTimer = jasmine.createSpyObj('Timer', ['start', 'restart']);

        harness = AngularUnitTestHelper.prepareModule('synchronizationPollingServiceModule')
            .mockConstant('OVERLAY_RERENDERED_EVENT', 'mockedOverlayRerenderedEvent')
            .mock('gatewayProxy', 'initForService')
            .mock('timerService', 'createTimer').andReturn(mockTimer)
            .mock('catalogService', 'isContentCatalogVersionNonActive').andReturnResolvedPromise(true)
            .mock('catalogService', 'getContentCatalogActiveVersion').andReturnResolvedPromise('Online')
            .mock('crossFrameEventService', 'publish')
            .mock('crossFrameEventService', 'subscribe')
            .mock('componentHandlerService', 'getPageUID')
            .mock('systemEventService', 'registerEventHandler')
            .mock('synchronizationResource', 'getPageSynchronizationGetRestService').andReturn(pageSynchronizationGetRestService)
            .mock('synchronizationResource', 'getPageSynchronizationPostRestService').andReturn(pageSynchronizationPostRestService)
            .service('syncPollingService');

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        syncPollingService = harness.service;
        systemEventServiceMock = harness.mocks.systemEventService;
        componentHandlerServiceMock = harness.mocks.componentHandlerService;
        catalogServiceMock = harness.mocks.catalogService;
        crossFrameEventServiceMock = harness.mocks.crossFrameEventService;
    });

    it('initSyncPolling will be called on service initialization and will set default values, register event handlers and start timer', function() {

        //GIVEN
        systemEventServiceMock.registerEventHandler.andReturn({});
        mockTimer.start.andCallThrough();

        //THEN
        expect(syncPollingService.refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
        expect(syncPollingService.triggers).toEqual([]);
        expect(syncPollingService.syncStatus).toEqual({});

        expect(systemEventServiceMock.registerEventHandler.calls.length).toEqual(2);
        expect(systemEventServiceMock.registerEventHandler.calls[0].args[0]).toEqual(SYNC_POLLING_SPEED_UP);
        expect(systemEventServiceMock.registerEventHandler.calls[1].args[0]).toEqual(SYNC_POLLING_SLOW_DOWN);

        expect(mockTimer.start).toHaveBeenCalled();

    });

    it('when syncStatus in the scope is empty then getSyncStatus will fetch the sync status by making a rest call and set it to the scope object ', function() {

        //GIVEN
        componentHandlerServiceMock.getPageUID.andReturn('pageId1');
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        //WHEN
        var promise = syncPollingService.getSyncStatus('pageId1');

        //THEN
        expect(promise).toBeResolvedWithData(pageId1_SyncStatus);
        expect(syncPollingService.syncStatus).toEqual(pageId1_SyncStatus);

    });

    it('when syncStatus object is not empty syncStatus but has an unmatched id, then getSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', function() {

        //GIVEN
        componentHandlerServiceMock.getPageUID.andReturn('pageId1');
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));
        syncPollingService.syncStatus = pageId2_SyncStatus;

        //WHEN
        var promise = syncPollingService.getSyncStatus('pageId1');

        //THEN
        expect(promise).toBeResolvedWithData(pageId1_SyncStatus);

        expect(pageSynchronizationGetRestService.get.calls.length).toBe(1);
        expect(pageSynchronizationGetRestService.get).toHaveBeenCalledWith({
            target: 'Online',
            pageUid: 'pageId1'
        });
        expect(syncPollingService.syncStatus).toEqual(pageId1_SyncStatus);
    });

    it('when syncStatus object is not empty syncStatus and matches the id then getSyncStatus with directly return the promise of the syncStatus object', function() {

        //GIVEN
        componentHandlerServiceMock.getPageUID.andReturn('pageId1');
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));
        syncPollingService.syncStatus = pageId1_SyncStatus;

        //WHEN
        var promise = syncPollingService.getSyncStatus('pageId1');

        //THEN
        expect(promise).toBeResolvedWithData(pageId1_SyncStatus);
        expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();

    });

    it('fetchSyncStatus will fetch the sync status by making a rest call and reset the syncStatus scope object', function() {

        //GIVEN
        componentHandlerServiceMock.getPageUID.andReturn('pageId1');
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        //WHEN
        var promise = syncPollingService._fetchSyncStatus();

        //THEN
        expect(promise).toBeResolvedWithData(pageId1_SyncStatus);
        expect(pageSynchronizationGetRestService.get).toHaveBeenCalled();
        expect(syncPollingService.syncStatus).toEqual(pageId1_SyncStatus);

    });

    it('when no page id is available then fetchSyncStatus will return an empty object', function() {

        //GIVEN
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        //WHEN
        var promise = syncPollingService._fetchSyncStatus();

        //THEN
        expect(promise).toBeResolvedWithData({});
        expect(pageSynchronizationGetRestService.get).not.toHaveBeenCalled();
        expect(syncPollingService.syncStatus).toEqual({});

    });

    it('when changePollingSpeed is called with syncPollingSpeedUp then the item is added to the triggers array and refreshInterval is set to speed up interval', function() {

        //GIVEN
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        //WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');

        //THEN
        expect(syncPollingService.triggers).toEqual(['slot1']);
        expect(syncPollingService.refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

        //WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');

        //THEN
        expect(syncPollingService.triggers).toEqual(['slot1', 'slot2']);
        expect(syncPollingService.refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

    });

    it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is set to slow down interval if the array is empty', function() {

        //GIVEN
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
        expect(syncPollingService.triggers).toEqual(['slot1']);

        //WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

        //THEN
        expect(syncPollingService.triggers).toEqual([]);
        expect(syncPollingService.refreshInterval).toBe(SYNCHRONIZATION_SLOW_POLLING_TIME);
        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_SLOW_POLLING_TIME);

    });

    it('when changePollingSpeed is called with syncPollingSlowDown then the item is removed from the triggers array and refreshInterval is unaltered if the array is not empty', function() {

        //GIVEN
        pageSynchronizationGetRestService.get.andReturn($q.when(pageId1_SyncStatus));

        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot1');
        syncPollingService.changePollingSpeed(SYNC_POLLING_SPEED_UP, 'slot2');
        expect(syncPollingService.triggers).toEqual(['slot1', 'slot2']);

        //WHEN
        syncPollingService.changePollingSpeed(SYNC_POLLING_SLOW_DOWN, 'slot1');

        //THEN
        expect(syncPollingService.triggers).toEqual(['slot2']);
        expect(syncPollingService.refreshInterval).toBe(SYNCHRONIZATION_FAST_POLLING_TIME);
        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_FAST_POLLING_TIME);

    });


    it('will listen to OVERLAY_RERENDERED_EVENT events and proceed to one fetch', function() {
        var status = {
            a: 'b'
        };
        spyOn(syncPollingService, '_fetchSyncStatus').andReturn($q.when(status));

        expect(crossFrameEventServiceMock.subscribe).toHaveBeenCalledWith('mockedOverlayRerenderedEvent', jasmine.any(Function));

        var callback = crossFrameEventServiceMock.subscribe.calls[0].args[1];

        callback();
        $rootScope.$digest();
        expect(syncPollingService._fetchSyncStatus).toHaveBeenCalled();

    });


    it('performSync will use activeVersion in REST call', function() {

        //GIVEN
        var uriContext = "uriContext";
        var array = [{
            a: 'b'
        }];
        catalogServiceMock.getContentCatalogActiveVersion.andReturn($q.when('mockedOnline'));

        //WHEN
        var promise = syncPollingService.performSync(array, uriContext);

        //THEN
        //$rootScope.$digest();
        expect(promise).toBeResolvedWithData();
        expect(pageSynchronizationPostRestService.save).toHaveBeenCalledWith({
            target: 'mockedOnline',
            items: array
        });
        expect(catalogServiceMock.getContentCatalogActiveVersion).toHaveBeenCalledWith(uriContext);
    });

});
