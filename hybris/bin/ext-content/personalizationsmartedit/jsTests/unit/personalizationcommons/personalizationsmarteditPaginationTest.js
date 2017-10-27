describe('personalizationsmarteditPagination', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        $rootScope.paginationCallback = function() {};
        var element = $compile("<personalizationsmartedit-pagination pages=\"pagination.pages\" " +
            "current-page=\"pagination.currentPage\" page-sizes=\"pagination.pageSizes\" " +
            "current-size=\"pagination.currentSize\" pages-offset=\"pagination.pagesOffset\" " +
            "callback=\"paginationCallback\" />")($rootScope);
        // when
        $rootScope.$digest();
        // then
        var subText = "<div class=\"row\">";
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
