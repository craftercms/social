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

    factory('DeletePopupService', ['$rootScope', 'UgcApi', function ($rootScope, UgcApi) {
        var popupEl = null;

        function processDelete(config) {
            UgcApi.deleteUGCList(config).then( function(data) {
                if (data) {
                    // TODO: Move model out to a service
                    // UgcListCtrl is in control of the model so we're going to broadcast an event
                    // that will let UgcListCtrl know that the model has been updated.
                    $rootScope.$broadcast('$ugcUpdate');
                } else {
                    console.log("Unable to delete UGCs");
                }
                $(popupEl).popover('destroy');
                popupEl = null;
            })
        }

        function cancelDelete() {
            $(popupEl).popover('destroy');
            popupEl = null;
        }

        function getControls(config) {
            var controlsStr, $controls;

            controlsStr =   '  <div class="btn-group" >' +
                            '    <button class="btn delete-btn accept" value="yes">Yes</button>' +
                            '    <button class="btn delete-btn cancel" value="no">No</button>' +
                            '  </div>';
            $controls = $(controlsStr);

            // Attach the event handlers to the buttons
            $controls.find('.accept').click(function(){
                processDelete(config);
            });
            $controls.find('.cancel').click(function(){
                cancelDelete();
            });

            return $controls;
        }

        function open(srcEl, config) {

            if(!popupEl) {
                // Only allow one delete popup open at a time
                popupEl = srcEl;

                $(srcEl).popover({
                    animation: true,
                    placement: 'right',
                    trigger: 'manual',
                    'html': true,
                    title: 'Are you sure ?',
                    content: function (){
                        return getControls(config);
                    }
                }).popover('show');
            }
        }

        function destroy() {
            popupEl && cancelDelete();
        }

        return {
            open: open,
            destroy: destroy
        }
    }]).

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
            deleteUGCList: function (config) {
                var deferred = $q.defer();

                $http.post(
                    CONFIG.API_PATH + "delete.json",
                    '',
                    {
                        params: {
                            tenant: config.tenant,
                            ugcIds: config.items,
                        },
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