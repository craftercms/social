'use strict';

/* Services */
angular.module('moderationDashboard.services', []).
    value('version', '0.1').

    constant('CONFIG', {
        PROPERTIES_PATH: "/crafter-social-admin/static-assets/csadmin/properties/",
        API_PATH: "/crafter-social/api/2/ugc/"
    }).

    /**
     * rest calls for get the configuration properties
     */
    factory('ConfigurationData', function ($http, $q, CONFIG) {
        var confObj = {};
        var moderationActions = [];

        return {
            appDataPromise: function () {
                var deferred = $q.defer();

                $http.get(CONFIG.PROPERTIES_PATH + "app_conf.json", { cache: true }).
                    success(function (data) {
                        confObj = data;
                        deferred.resolve(data);
                    }).
                    error(function (errorMsg) {
                        confObj = null;
                        deferred.reject(null);
                    });

                return deferred.promise;
            },
            getConfData: function () {
                return confObj;
            },
            moderationStatusPromise: function () {
                var deferred = $q.defer();

                $http.get(CONFIG.PROPERTIES_PATH + "moderation_status_action.json", { cache: true }).
                    success(function (data) {
                        moderationActions = data;
                        deferred.resolve(data);
                    }).
                    error(function (errorMsg) {
                        moderationActions = null;
                        deferred.reject(null);
                    });

                return deferred.promise;
            },
            getModerationActions: function () {
                return moderationActions;
            }
        };
    }).

    factory('UgcApi', function ($http, $q, CONFIG) {
        return {
            getUgcList: function (conf) {
                var deferred = $q.defer();

                $http.get(
                    CONFIG.API_PATH + "moderation/" + conf.moderation,
                    {
                        params: {
                            'tenant': conf.tenant,
                            'page': conf.page,
                            'pageSize': conf.pageSize
                        }
                    }
                ).success(
                    function (data) {
                        deferred.resolve(data);
                    }
                ).error(
                    function(errorData) {
                        deferred.reject(errorData);
                    }
                );

                return deferred.promise;
            },
            updateUgc: function (conf) {
                var deferred = $q.defer();

                $http.post(
                    CONFIG.API_PATH + "moderation/" + conf.moderationid + "/status.json",
                    $.param({
                        moderationStatus: conf.moderationstatus,
                        tenant: conf.tenant
                    }),
                    {
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }
                ).success(
                    function (data) {
                        deferred.resolve(data);
                    }
                ).error(
                    function (errorData) {
                        deferred.reject(errorData);
                    }
                );

                return deferred.promise;
            },
            bulkUpdate: function (conf) {
                var deferred = $q.defer();

                $http.post(
                    CONFIG.API_PATH + "moderation/update/status.json",
                    $.param({
                        moderationStatus: conf.moderationstatus,
                        tenant: conf.tenant,
                        ids: conf.ids
                    }, true),
                    {
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }
                ).success(
                    function (data) {
                        deferred.resolve(data);
                    }
                ).error(
                    function (errorData) {
                        deferred.reject(errorData);
                    }
                );

                return deferred.promise;
            }
        };
    });