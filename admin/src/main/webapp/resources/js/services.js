'use strict';

/* Services */
angular.module('moderationDashboard.services', ['ngResource', 'ngCookies']).
    value('version', '0.1').
    /*
     * Ugc: get all ugc with textContent and moderationStatus
     * TODO: passing moderation status as a parameter to return all UGCs of that moderation status
     * */
    factory('Ugc', function ($resource, $cookies) {
        var TICKET = $cookies.crafterAuthCookie,
            url = "/crafter-social/api/2/ugc/moderation/:moderation?ticket={:ticket}&tenant=:tenant",
            resource = $resource(url, {
                moderation: 'UNMODERATED.json',
                ticket: TICKET,
                tenant: 'craftercms'
            });

        return resource;
    });

/*
 * TODO: ask for default parameters
 */