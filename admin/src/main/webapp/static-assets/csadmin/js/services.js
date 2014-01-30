'use strict';

/* Services */
angular.module('moderationDashboard.services', ['ui.bootstrap.modal']).

    factory('DeletePopupService', 
        ['$rootScope',
         'PaginationService',
         'UgcApi', 
         function ($rootScope, PaginationService, UgcApi) {

        var popupEl = null;

        function processDelete(config) {
            UgcApi.deleteUGCList(config).then( function(data) {
                var currentPage = PaginationService.getCurrentPage();
                // Because there's no undo operation to delete, we update the 
                // pagination right away
                PaginationService.removeItems(config.items.length);
                PaginationService.setCurrentPage(currentPage);

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

    factory('PaginationService', 
        ['$rootScope',
         '$timeout', 
         'ENV', function($rootScope, $timeout, ENV) {

        var paginationConfig = ENV.config.pagination,
            itemsPerPage, maxPageNumber, itemsToRemove;

            itemsPerPage = paginationConfig && paginationConfig.itemsPerPage ? 
                                paginationConfig.itemsPerPage : 10;
            maxPageNumber = paginationConfig && paginationConfig.maxPageNumber ? 
                                paginationConfig.maxPageNumber : 5;
            itemsToRemove = 0;

        return {
            data: {
                currentPage: 1,
                itemsPerPage: itemsPerPage,
                maxPageNumber: maxPageNumber,
                totalItems: 0,
                numPages: 0,
                showPagination: false
            },
            init: function (numItems) {
                itemsToRemove = 0;
                this.setCurrentPage(1);
                this.setTotalItems(numItems);
            },
            removeItems: function (number) {
                itemsToRemove = itemsToRemove + number;
            },
            getCurrentPage: function () {
                return this.data.currentPage;
            }, 
            setCurrentPage: function (pageNumber) {
                var data = this.data,
                    totalItems;

                if (itemsToRemove) {
                    totalItems = this.data.totalItems - itemsToRemove;
                    this.setTotalItems(totalItems);
                    // Reset number of items to remove
                    itemsToRemove = 0;
                }

                $timeout( function() {
                    $rootScope.$apply( function() {
                        data.currentPage = (pageNumber <= data.numPages) ?
                                                    pageNumber : data.numPages;
                    });
                });
            },
            setTotalItems: function (numItems) {
                var data = this.data;

                $timeout( function() {
                    $rootScope.$apply( function() {
                        data.totalItems = numItems;
                        data.numPages = Math.ceil(data.totalItems / itemsPerPage);
                        data.showPagination = (data.numPages > 1) ? true : false;
                    });
                });  
            }
        };
    }]).

    factory('UgcApi', 
        ['$http', 
         '$q', 
         'CONFIG', 
         'ENV', 
         'ERROR',
         '$modal', function ($http, $q, CONFIG, ENV, ERROR, $modal) {

        function getModelContent (message) {
            return  '<div class="modal-header">' +
                    '  <h3>Error Found</h3>' +
                    '</div>' +
                    '<div class="modal-body">' + message + '</div>' +
                    '<div class="modal-footer">' +
                    '  <button class="btn btn-primary" ng-click="ok()">OK</button>' +
                    '</div>';
        }

        function showErrorModal (errorCode) {
            var errorMessage = '';

            switch (errorCode) {
                case 401: 
                    errorMessage = ERROR['401'];
                    break;
                case 403:
                    errorMessage = ERROR['403'];
                    break;
                default:
                    errorMessage = ERROR['ALL'];
            }

            $modal.open({
                template: getModelContent(errorMessage),
                controller: function($scope, $modalInstance) {
                    $scope.ok = function() {
                        $modalInstance.close();
                    }
                },
                windowClass:'error',
            });
        }

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
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
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
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
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
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
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
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
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
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
                    }
                );

                return deferred.promise;
            },
            getItemsNumber: function (moderationStatus) {
                var deferred = $q.defer();

                $http.get(
                    CONFIG.API_PATH + "moderation/" + moderationStatus + "/count.json",
                    {
                        params: {
                            tenant: ENV.config.tenant
                        },
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                    }
                ).success(
                    function (data) {
                        deferred.resolve(data);
                    }
                ).error(
                    function (data, status) {
                        showErrorModal(status);
                        deferred.reject(data);
                    }
                );
                return deferred.promise;
            }
        };
    }]);
