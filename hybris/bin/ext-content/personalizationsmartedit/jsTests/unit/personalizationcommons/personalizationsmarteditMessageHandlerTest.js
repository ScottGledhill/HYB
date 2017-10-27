describe('personalizationsmarteditMessageHandler', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var personalizationsmarteditMessageHandler;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_personalizationsmarteditMessageHandler_) {
        personalizationsmarteditMessageHandler = _personalizationsmarteditMessageHandler_;
    }));


    describe('sendInformation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendInformation).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendInformation("test message");
            //then
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalled();
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalledWith([{
                successful: true,
                message: "test message",
            }]);
        });

    });

    describe('sendError', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendError).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendError("test error");
            // then
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalled();
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalledWith([{
                successful: false,
                message: "test error",
            }]);
        });

    });

    describe('send', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.send).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // given
            var mockMessages = ["first message", "second message"];
            // when
            personalizationsmarteditMessageHandler.send(mockMessages);
            // then
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalled();
            expect(mockModules.alertService.pushAlerts).toHaveBeenCalledWith(mockMessages);
        });

    });

    describe('buildMessage', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.buildMessage).toBeDefined();
        });

        it('properly create message object', function() {
            // given
            var mockMessage = "Mock error message";
            var mockIsSuccess = true;
            var mockMessageObject = {
                successful: mockIsSuccess,
                message: mockMessage
            };
            // when
            var testMessage = personalizationsmarteditMessageHandler.buildMessage(mockMessage, mockIsSuccess);
            // then
            expect(testMessage).toEqual(mockMessageObject);
        });

    });

});
