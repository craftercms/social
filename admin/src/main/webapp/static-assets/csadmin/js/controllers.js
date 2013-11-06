'use strict';

angular.module('moderationDashboard.controllers', []).
    /**
     * AppCtrl
     * main controller
     *  contains tenant, default moderation status and the list of moderations
     *  all this variables are in properties files
     */
    controller('AppCtrl', ['$scope', 'Api', '$http', function (scope, api, http) {
        scope.confObj = {};
        scope.moderationList = [];

        var getAppConf = function () {
            api.defaultTenant.get(function (conf) {
                if (conf) {
                    scope.confObj = {
                        tenant: conf.tenant,
                        defaultModeration: conf.moderation.toUpperCase(),
                        pagination: conf.pagination
                    };
                }else {
                    //TODO: error message, no tenant was provide
                    console.error("Tenant was not provided");
                }
            });
        };

        var getModerationList = function () {
            api.moderationAction.query(function (moderationList) {
                if ( moderationList !== undefined && moderationList.length > 0 ){
                    scope.moderationList = moderationList;
                }else{
                    //TODO: error message, no moderation status provided
                    console.error("Moderation Status was not provided");
                }
            });
        };

        getAppConf();
        getModerationList();

    }]).
    /**
     * Moderation Dashboard Controller
     * - get UGCs by moderation status
     **/
    controller('UgcListCtrl', ['$scope', 'Api', '$routeParams', '$http', function(scope, api, rp, http) {
        scope.ugcList = [];
        scope.status = "";
        scope.moderationActions = [];


        //set actual status
        var setStatus = function () {
            scope.status = rp.moderationStatus ? rp.moderationStatus.toUpperCase() : scope.confObj.defaultModeration;
        };

        // set the active tab
        var getModerationList = function () {
            angular.forEach(scope.moderationList, function (moderationObj) {
                if (scope.status.toLocaleLowerCase() === moderationObj.moderation.toLowerCase()) {
                    moderationObj.state = 'active';
                }else{
                    moderationObj.state = '';
                }
            });
        };

        // get ugc list by status from rest call
        scope.getUgcList = function (page) {
            var conf = {
                tenant: scope.confObj.tenant,
                moderation: scope.status + ".json",
                page: page,
                pageSize: scope.confObj.pagination.commentsPerPage
            };

            api.Ugc.query(conf, function (list) {
                var txtContent = {},
                    tmpList = [];

                angular.forEach(list, function (ugc){
                    txtContent = angular.fromJson(ugc.textContent);
                    tmpList.push({
                        'title': txtContent.title,
                        'id': ugc.id,
                        'textContent': scope.getTextFromhtml(txtContent.content),
                        'moderationStatus': ugc.moderationStatus,
                        'completeContent': txtContent.content,
                        'dateAdded': scope.getDateTime(ugc.dateAdded),
                        'userName': ugc.profile.userName,
                        'userMail': ugc.profile.email,
                        'targetUrl': 'target Url',
                        'targetTitle': 'target Title',
                        'updated': false,
                        'updateMessage': "",
                        'alertClass': "",
                        'undo': false
                    });
                });

                scope.ugcList = tmpList;

                //if no results available a information messsage will be displayed
                if (scope.ugcList.length > 0) {
                    scope.displayUgcs = true;
                }else {
                    scope.displayUgcs = false;
                    scope.infoMessage = "No results found";
                }
            });
        };

        // get text snippet from html
        //TODO if text is less than 200 then no arrow nor red corner has to appear
        scope.getTextFromhtml = function (html) {
            var elmText = $(html).text(),
                snippet;
            if (elmText.length > 200) {
                snippet = elmText.substring(0, 200);
                snippet += " [...]";
            }else{
                snippet = elmText;
            }

            return snippet;
        };

        // get date and hour of last update
        scope.getDateTime = function (dateLong) {
            var date = new Date(dateLong);

            return date.toDateString();
        }

        // set list of moderation actions available according status selected
        var moderationActions = function () {
            angular.forEach(scope.moderationList, function (modObject) {
                if (modObject.moderation.toLowerCase() === scope.status.toLocaleLowerCase()){
                    scope.moderationActions = modObject.actions;
                }
            });
        };

        // handler when input checkbox is clicked. Hide error message if is displayed
        scope.hideCheckUgcError = function (event) {
            if ($(event.currentTarget).prop('checked')) {
                // hiding error message
                var applyBtn = $('#applyBtn');
                if (applyBtn.next('div.tooltip:visible').length){
                    applyBtn.tooltip('hide');
                }
            }
        }

        //handler when undo is clicked
        scope.reverseAction = function (event, id) {
            http({
                method: 'POST',
                url: "/crafter-social/api/2/ugc/moderation/" + id + "/status.json?moderationStatus=" + scope.status + "&tenant=" + scope.confObj.tenant,
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            }).success(function (data) {
                    angular.forEach(scope.ugcList, function (ugc, index) {
                        if (ugc.id === data.id) {
                            scope.ugcList[index].updated = false;
                            scope.ugcList[index].updateMessage = "";
                            scope.ugcList[index].alertClass = "";
                            scope.ugcList[index].undo = false;
                        }
                    });
                }).error(function (data) {
                    scope.$parent.ugcList[index].updated = true;
                    scope.$parent.ugcList[index].updateMessage = "Error trying to update"
                    scope.$parent.ugcList[index].alertClass = "error";
                });

        };

        setStatus();
        getModerationList();
        scope.getUgcList(1);
        moderationActions();

    }]).

    /**
     * bulk action controller
     */
    controller('BulkActionCtrl', ['$scope', '$http', function (scope, http) {
        //handler for bulk actions update
        scope.bulkUpdate = function (event) {
            var optionSelected = scope.bulkAction,
                listItems = [];

            //check if bulk option is selected
            if (optionSelected === null || optionSelected === undefined ){
                $('#bulkActions').focus();
                return;
            }

            // check if an item has been selected
            $('.entries-list input').each(function (index) {
                if ($(this).prop('checked')){
                    listItems.push($(this).attr('ugcid'));
                }
            });

            if (listItems.length <= 0){
                var displayError = $(event.currentTarget).tooltip({
                    animation: true,
                    placement: 'right',
                    trigger: 'manual',
                    title: 'Please select at least one comment'
                });
                displayError.tooltip('show');

                return;
            }

            optionSelected = optionSelected.toUpperCase();
            var ids = "&ids=";
            ids += listItems.join("&ids=");

            //TODO change for restangular
            http({
                method: 'POST',
                url: "/crafter-social/api/2/ugc/moderation/update/status.json?moderationStatus=" + optionSelected + "&tenant=" + scope.confObj.tenant + ids
            }).success(function (data) {
                angular.forEach(data, function (item) {
                    angular.forEach(scope.ugcList, function(ugc, index){
                        if (item.id === ugc.id) {
                            scope.ugcList[index].updated = true;
                            scope.ugcList[index].updateMessage = scope.ugcList[index].title + " - " + scope.ugcList[index].dateAdded
                            scope.ugcList[index].alertClass = "success";
                            scope.ugcList[index].undo = true;

                            return;
                        }
                    });
                });
            }).error(function (data) {
                angular.forEach(data, function (item) {
                    angular.forEach(scope.ugcList, function(ugc, index){
                        if (item.id === ugc.id) {
                            scope.ugcList[index].updated = true;
                            scope.ugcList[index].updateMessage = "Error trying to update"
                            scope.ugcList[index].alertClass = "error";

                            return;
                        }
                    });
                });
            });
        };

        // handler for when a select option has been updated
        scope.optionSelected = function () {
            var applyBtn = $('#applyBtn');
            if (this.bulkAction !== null) {
                applyBtn.removeClass('disabled');
            }else {
                applyBtn.addClass('disabled');

                // hide error message if has been displayed
                if (applyBtn.next('div.tooltip:visible').length){
                    applyBtn.tooltip('hide');
                }
            }
        };
    }]).

    /**
     * pagination controller
     */
    controller('PaginationCtrl', ['$scope', function (scope) {
        scope.currentPage = 1;
        scope.totalItems = 10;

        // if no pagination configuration was detected
        if (scope.confObj.pagination === undefined) {
            // default values
            scope.itemsPerPage = 10;
            scope.maxSize = 5;
        }else{
            scope.itemsPerPage = scope.confObj.pagination.commentsPerPage;
            scope.maxSize = scope.confObj.pagination.maxNumberPagItem;
        }

        // change page when user click pagination
        scope.pageChanged = function (page) {
            scope.$parent.getUgcList(page);
        }

        // set pagination data
        var setPaginationData = function () {
            scope.numPages = Math.ceil(scope.totalItems / scope.itemsPerPage);
        };

        setPaginationData();

    }]);