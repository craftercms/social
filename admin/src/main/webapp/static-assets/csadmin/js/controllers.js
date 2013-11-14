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
    controller('UgcListCtrl', ['$scope', '$routeParams', 'ConfigurationData', 'UgcApi', 'CONFIG', function(scope, rp, ConfigurationData, UgcApi, CONFIG) {
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
                if (scope.status.toLowerCase() === moderationObj.moderation.label.toLowerCase()) {
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

                        if (ugc.textContent[0] == '{') {
                            txtContent = angular.fromJson(ugc.textContent);
                        } else {
                            txtContent = {content: ugc.textContent, title: 'no title'};
                        }

                        tmpList.push({
                            'title': txtContent.title,
                            'id': ugc.id,
                            'textContent': scope.getTextFromhtml(txtContent.content),
                            'moderationStatus': ugc.moderationStatus,
                            'completeContent': txtContent.content,
                            'dateAdded': scope.getDateTime(ugc.dateAdded),
                            'userName': ugc.profile.userName,
                            'userMail': ugc.profile.email,
                            'userImg': CONFIG.IMAGES_PATH + "profile-photo.jpg",
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
                if (modObject.moderation.value.toLowerCase() === scope.status.toLocaleLowerCase()){
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

        scope.displayResults = function (data, element, conf) {
            if (data) {
                angular.forEach(scope.ugcList, function (ugc, index) {
                    if (ugc.id === data.id) {
                        // removing active state of clicked button
                        var message = "";

                        if (conf.undo) {
                            if (element.hasClass('active')) {
                                element.removeClass('active');
                            }
                            message = scope.ugcList[index].title + " - " + scope.ugcList[index].dateAdded;
                        }else {
                            message = conf.message;
                        }
                        scope.ugcList[index].updated = true;
                        scope.ugcList[index].updateMessage = message;
                        scope.ugcList[index].alertClass = "success";
                        scope.ugcList[index].undo = conf.undo;
                    }
                });
            }else {
                //TODO error message: data needs to have information about the error
                console.log("error trying to update");
            }
        };

        setStatus();
        getModerationList();
        scope.getUgcList(1);
        moderationActions();

    }]).

    /**
     * bulk action controller
     */
    controller('BulkActionCtrl', ['$scope', 'UgcApi', 'PERMANENTLY_DELETE', function (scope, UgcApi, PD) {
        var commentsSelected = function () {
            var listItems = [];
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

                return null;
            }

            return listItems;
        };

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
            listItems = commentsSelected();
            if (listItems === null){
                return;
            }

            optionSelected = optionSelected.toUpperCase();

            if (PD.ACTION === optionSelected) {
                if (! $(event.currentTarget).next('div.popover:visible').length) {
                    $(event.currentTarget).popover({
                        animation: true,
                        placement: 'right',
                        trigger: 'manual',
                        'html': true,
                        title: 'Are you sure ?',
                        content: function (){
                            var popover = $(event.currentTarget).next('.popover');
                            popover.removeClass('hidden');
                            return popover.html();
                        }
                    }).popover('show');
                }

                return;
            }

            var conf = {
                moderationstatus: optionSelected,
                tenant: scope.confObj.tenant,
                ids: listItems
            };

            UgcApi.bulkUpdate(conf).then( function (data) {
                scope.displayResults(data, { undo: true, message: "" } );
            })

        };

        scope.validateDelete = function (event) {
            var elm = $(event.currentTarget);
            if (elm.val().toUpperCase() === PD.CONFIRM) {
                var listItems = commentsSelected();
                if (listItems !== null){
                    var conf = {
                        tenant: scope.confObj.tenant,
                        ugcids: listItems
                    };

                    UgcApi.permanentlyDelete(conf).then(function (data) {
                        if (data){
                            $("#applyBtn").popover('destroy');
                            //scope.displayResults(data, { undo: false, message: "comment deleted successfully" });
                            console.log('deleted');
                        }else {
                            console.log("error trying to delete comment");
                            //TODO display message explaining the reason
                        }
                    });
                }
            }else {
                var popover = $('#applyBtn');
                popover.popover('hide');
            }
        };

        scope.displayResults = function (data, conf) {
            if (data) {
                angular.forEach(data, function (item) {
                    angular.forEach(scope.ugcList, function(ugc, index){
                        if (conf.undo) {
                            conf.message = scope.ugcList[index].title + " - " + scope.ugcList[index].dateAdded;
                        }

                        if (item.id === ugc.id) {
                            scope.ugcList[index].updated = true;
                            scope.ugcList[index].updateMessage = conf.message;
                            scope.ugcList[index].alertClass = "success";
                            scope.ugcList[index].undo = conf.undo;

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