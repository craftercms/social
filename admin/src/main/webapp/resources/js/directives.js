'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('listUgc', [function () {
        return  function (scope, elm, attrs) {
            elm.click(function (ev) {
                ev.preventDefault();
                $(ev.currentTarget).toggleClass('active');
                console.log('here');
            });
        };
    }]).
    directive('appVersion', ['version', function(version) {
        return function(scope, elm, attrs) {
            elm.text(version);
        };
    }]);