'use strict';

// Declare app level module which depends on filters, and services
angular.module('moderationDashboard',
        [
            'moderationDashboard.filters', 'moderationDashboard.services',
            'moderationDashboard.directives', 'moderationDashboard.controllers',
            'ngSanitize'
        ]).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/status/:moderationStatus', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'});
    }]);