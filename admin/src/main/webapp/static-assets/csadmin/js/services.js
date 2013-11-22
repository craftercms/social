'use strict';

/* Services */
angular.module('moderationDashboard.services', []).
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
            updateUGCStatus: function (conf) {
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
            updateUGCContent: function (ugcData, config) {
                var deferred = $q.defer();

                $http.post(
                    CONFIG.API_PATH + "update.json",
                    ugcData,
                    {
                        params: {
                            ugcId: config.ugcId,
                            tenant: config.tenant
                        },
                        headers: { 'Content-Type': 'application/json' }
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
            },
            permanentlyDelete: function (conf) {
                var deferred = $q.defer(),
                    params = $.param({
                        tenant: conf.tenant,
                        ugcids: conf.ugcids
                    }, true);

                $http.post(
                    CONFIG.API_PATH + "delete.json?" + params,
                    $.param({
                        tenant: conf.tenant,
                        ugcids: conf.ugcids
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