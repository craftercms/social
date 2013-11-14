'use strict';

// Declare app level module which depends on filters, and services
angular.module('moderationDashboard',
        [
            'moderationDashboard.filters', 'moderationDashboard.services',
            'moderationDashboard.directives', 'moderationDashboard.controllers',
            'moderationDashboard.constants',
            'ngSanitize', 'ngRoute', 'ngAnimate', 'ui.bootstrap'
        ]).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.
            when(
                '/',
                {
                    templateUrl: 'static-assets/csadmin/templates/ugc_list.html',
                    controller: 'UgcListCtrl',
                    resolve: {
                        'appData' : function (ConfigurationData) {
                            return ConfigurationData.appDataPromise();
                        },
                        'moderationActions': function (ConfigurationData) {
                            return ConfigurationData.moderationStatusPromise();
                        }
                    }
                }
            ).
            when(
                '/status/:moderationStatus',
                {
                    templateUrl: 'static-assets/csadmin/templates/ugc_list.html',
                    controller: 'UgcListCtrl',
                    resolve: {
                        'appData' : function (ConfigurationData) {
                            return ConfigurationData.appDataPromise();
                        },
                        'moderationActions': function (ConfigurationData) {
                            return ConfigurationData.moderationStatusPromise();
                        }
                    }
                }
            );
    }]);