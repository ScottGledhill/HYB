describe('personalizationInfiniteScroll', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('Add watcher functions to scope', function() {
        expect($rootScope.$$watchers).toBe(null);
        // given
        var element = $compile("<div personalization-infinite-scroll=\"addMoreItems()\" personalization-infinite-scroll-distance=\"2\"</div>")($rootScope);
        // when
        $rootScope.$digest();
        // then
        expect($rootScope.$$watchers).not.toBe(null);
    });

});
