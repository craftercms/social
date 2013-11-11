'use strict';

angular.module('moderationDashboard.controllers', []).
    /**
     * AppCtrl
     * main controller
     **/
    controller('AppCtrl', ['$scope', function (scope) {

    }]).
    /**
     * Moderation Dashboard Controller
     * - get UGCs by moderation status
     **/
    controller('UgcListCtrl', ['$scope', '$routeParams', 'ConfigurationData', 'UgcApi',  function(scope, rp, ConfigurationData, UgcApi) {
        scope.ugcList = [];
        scope.status = "";
        scope.moderationActions = [];
        scope.confObj = ConfigurationData.getConfData();
        scope.moderationList = ConfigurationData.getModerationActions();

        //set actual status
        var setStatus = function () {
            scope.status = rp.moderationStatus ? rp.moderationStatus.toUpperCase() : scope.confObj.moderation;
        };

        // set the active tab
        var getModerationList = function () {
            angular.forEach(scope.moderationList, function (moderationObj) {
                if (scope.status.toLowerCase() === moderationObj.moderation.toLowerCase()) {
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

            UgcApi.getUgcList(conf).then(function (data) {
                if (data) {
                    var txtContent = {},
                        tmpList = [];

                    angular.forEach(data, function (ugc){
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
                }else {
                    scope.displayUgcs = false;
                    scope.infoMessage = "error, Comments were not found";
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
            var conf = {
                moderationid : id,
                moderationstatus: scope.status,
                tenant: scope.confObj.tenant
            };

            UgcApi.updateUgc(conf).then(function (data) {
                if (data) {
                    angular.forEach(scope.ugcList, function (ugc, index) {
                        if (ugc.id === data.id) {
                            scope.ugcList[index].updated = false;
                            scope.ugcList[index].updateMessage = "";
                            scope.ugcList[index].alertClass = "";
                            scope.ugcList[index].undo = false;
                        }
                    });
                }else {
                    console.log("error trying to bulk update");
                }
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
    controller('BulkActionCtrl', ['$scope', 'UgcApi', function (scope, UgcApi) {
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

            var conf = {
                moderationstatus: optionSelected,
                tenant: scope.confObj.tenant,
                ids: listItems
            };

            UgcApi.bulkUpdate(conf).then( function (data) {
                if (data) {
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
                }else {
                    console.error("error bulk update");
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
                }
            })

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