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
            when('/blogs', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/media', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/comments', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/advertising', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/coupons', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/all', {templateUrl: 'resources/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            otherwise({redirectTo: '/' });
    }]);