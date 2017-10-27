describe('statusNotDeleted', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var filter;

    beforeEach(module('personalizationsmarteditCommons', function($provide) {}));
    beforeEach(inject(function(_$filter_) {
        filter = _$filter_;
    }));

    describe('statusNotDeleted', function() {

        it('should be defined', function() {
            expect(filter('statusNotDeleted')).toBeDefined();
            expect(filter('statusNotDeleted')).not.toBe(null);
        });

        it('should properly filter array', function() {
            // given
            var mockArray1 = [{
                status: "ENABLED"
            }, {
                status: "ENABLED"
            }];
            var mockArray2 = [{
                status: "DELETED"
            }, {
                status: "DISABLED"
            }, {
                status: "DELETED"
            }];
            var mockArray3 = mockArray1.concat(mockArray2);

            // when
            var mockFilteredArray1 = filter('statusNotDeleted')(mockArray1);
            var mockFilteredArray2 = filter('statusNotDeleted')(mockArray2);
            var mockFilteredArray3 = filter('statusNotDeleted')(mockArray3);

            // then
            expect(mockFilteredArray1.length).toBe(2);
            expect(mockFilteredArray2.length).toBe(1);
            expect(mockFilteredArray3.length).toBe(3);
        });


    });

});
