'use strict';

// Declare app level module which depends on filters, and services
angular.module('moderationDashboard',
        [
            'moderationDashboard.filters', 'moderationDashboard.services',
            'moderationDashboard.directives', 'moderationDashboard.controllers',
            'moderationDashboard.constants',
            'ngSanitize', 'ngRoute', 'ngAnimate', 'ui.bootstrap'
        ]).

    config(['$routeProvider', 'ENV', function($routeProvider, ENV) {

        $routeProvider.
            when(
                '/status/:moderationStatus',
                {
                    templateUrl: 'static-assets/csadmin/templates/ugc_list.html',
                    controller: 'UgcListCtrl',
                    resolve: {
                        totalItems : function totalItems ($route, UgcApi) {
                            return UgcApi.getItemsNumber($route.current.params.moderationStatus);
                        }
                    }
                }
            ).
            when('/', {
                redirectTo: '/status/' + ENV.defaultSection
            });
    }]).

    run(['$rootScope', 'DeletePopupService', function($rootScope, DeletePopupService) {
        $rootScope.$on('$routeChangeStart', function(event) {
            // Destroy the delete confirmation dialog (if it's open)
            DeletePopupService.destroy();
        });
    }]);
