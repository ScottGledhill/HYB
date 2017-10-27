describe('personalizationsmarteditComponentLightUp', function() {
    var mockModules = {};
    setupMockModules(mockModules);

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditComponentLightUpDecorator'));
    beforeEach(inject(function(_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        var element = $compile("<div class=\"personalizationsmarteditComponentLightUp\"></div>")($rootScope);
        // when
        $rootScope.$digest();
        // then
        var subText = "<div ng-class=\"getPersonalizationComponentBorderClass()\">";
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
