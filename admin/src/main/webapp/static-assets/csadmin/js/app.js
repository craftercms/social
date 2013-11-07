'use strict';

// Declare app level module which depends on filters, and services
angular.module('moderationDashboard',
        [
            'moderationDashboard.filters', 'moderationDashboard.services',
            'moderationDashboard.directives', 'moderationDashboard.controllers',
            'ngSanitize', 'ngRoute', 'ngAnimate', 'ui.bootstrap'
        ]).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when('/', {templateUrl: 'static-assets/csadmin/templates/ugc_list.html', controller: 'UgcListCtrl'}).
            when('/status/:moderationStatus', {templateUrl: 'static-assets/csadmin/templates/ugc_list.html', controller: 'UgcListCtrl'});
    }]);