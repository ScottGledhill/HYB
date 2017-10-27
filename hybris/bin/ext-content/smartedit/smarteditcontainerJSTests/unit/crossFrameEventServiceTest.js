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
describe('test cross-frame event service', function() {

    var crossFrameEventService, systemEventService, gatewayFactory, gateway, $q;

    var eventId = 'eventId',
        data = 'some data',
        handler = function() {};

    beforeEach(customMatchers);

    beforeEach(module('eventServiceModule', function($provide) {

        systemEventService = jasmine.createSpyObj('systemEventService', ['sendAsynchEvent', 'registerEventHandler']);
        $provide.value('systemEventService', systemEventService);

    }));

    beforeEach(module('gatewayFactoryModule', function($provide) {

        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
        gateway = jasmine.createSpyObj('gateway', ['publish', 'subscribe']);
        gatewayFactory.createGateway.andReturn(gateway);

        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('crossFrameEventServiceModule'));
    beforeEach(inject(function(_$q_, _crossFrameEventService_) {
        $q = _$q_;
        crossFrameEventService = _crossFrameEventService_;
    }));

    it('publish will publish to the gatewayFactory and then send an event for a given event id and data', function() {

        //GIVEN
        systemEventService.sendAsynchEvent.andReturn($q.when({}));
        gateway.publish.andReturn($q.when({}));

        //WHEN
        crossFrameEventService.publish(eventId, data);

        //THEN
        expect(systemEventService.sendAsynchEvent).toHaveBeenCalledWith(eventId, data);
        expect(gateway.publish).toHaveBeenCalledWith(eventId, data);

    });

    it('subscribe will subscribe to the gatewayFactory and then register an event for the given event id and the provided handler', function() {

        //GIVEN
        systemEventService.registerEventHandler.andReturn($q.when({}));
        gateway.subscribe($q.when({}));

        //WHEN
        crossFrameEventService.subscribe(eventId, handler);

        //THEN
        expect(systemEventService.registerEventHandler).toHaveBeenCalledWith(eventId, handler);
        expect(gateway.subscribe).toHaveBeenCalledWith(eventId, handler);

    });


});
