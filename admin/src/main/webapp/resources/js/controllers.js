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
        scope.appToken = "";

        var getTenant = function () {
            api.defaultTenant.get(function (conf) {
                if (conf) {
                    scope.confObj = {
                        tenant: conf.tenant,
                        defaultModeration: conf.moderation.toUpperCase()
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

        var getAppToken = function () {
            var url = "/crafter-profile/api/2/auth/app_token.json?username=craftersocial&password=craftersocial";

            http({
                method: 'GET',
                url: url
            }).success(function (token) {
               scope.appToken = token;
                    console.log(scope.appToken);
            }).error(function () {
                scope.setAlert("Error trying to get app token", "alert-error");
            });
        };

        getAppToken();
        getTenant();
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
        scope.ugcSelectedList = [];
        scope.errorMessage;
        scope.classError;

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
        var getUgcList = function () {
            var conf = {
                tenant: scope.confObj.tenant,
                moderation: scope.status + ".json"
            };

            api.Ugc.query(conf, function (list) {
                var txtContent = {},
                    tmpList = [];

                angular.forEach(list, function (ugc){
                    txtContent = angular.fromJson(ugc.textContent);
                    tmpList.push({
                        'title': txtContent.title,
                        'id': ugc.id,
                        'textContent': getTextFromhtml(txtContent.content),
                        'moderationStatus': ugc.moderationStatus,
                        'completeContent': txtContent.content,
                        'dateAdded': getDateTime(ugc.dateAdded),
                        'userName': ugc.profile.userName,
                        'userMail': ugc.profile.email,
                        'targetUrl': 'target Url',
                        'targetTitle': 'target Title'
                    });
                });

                scope.ugcList = tmpList;
            });
        };

        // get text snippet from html
        var getTextFromhtml = function (html) {
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
        var getDateTime = function (dateLong) {
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

        //handler for bulk actions update
        scope.bulkUpdate = function (event) {
            var optionSelected = scope.bulkAction,
                listItems = [];

            //check if bulk option is selected
            if (optionSelected === undefined){
                var displayError = $(event.currentTarget).popover({
                    animation: true,
                    placement: 'right',
                    trigger: 'manual',
                    title: 'Error Title',
                    content: 'Please select an option'
                });
                displayError.popover('show');

                $('#bulkActions').focus();

                return;
            }


            $('.entries-list input').each(function (index) {
                if ($(this).prop('checked')){
                    listItems.push($(this).attr('ugcid'));
                }
            });

            if (listItems.length > 0) {
                // hiding error message
                var applyBtn = $('#applyBtn');
                if (applyBtn.next('div.popover:visible').length){
                    applyBtn.popover('hide');
                }

                optionSelected = optionSelected.toUpperCase();
                var ids = "&ids=";
                ids += listItems.join("&ids=");

                http({
                    method: 'POST',
                    url: "/crafter-social/api/2/ugc/moderation/update/status.json?moderationStatus=" + optionSelected + "&tenant=" + scope.confObj.tenant + ids
                }).success(function (data) {
                    angular.forEach(data, function (item) {
                        angular.forEach(scope.ugcList, function(ugc, index){
                            if (item.id === ugc.id) {
                                scope.ugcList.splice(index, 1);
                                return;
                            }
                        });
                    });

                    scope.setAlert("UGCs updated correctly", "alert-success");
                }).error(function (data) {
                    scope.setAlert("Error trying to save UGC", "alert-error");
                });

                //TODO change for restangular
            }else{
                var displayError = $(event.currentTarget).popover({
                    animation: true,
                    placement: 'right',
                    trigger: 'manual',
                    title: 'Error Title',
                    content: 'Please check an ugc'
                });
                displayError.popover('show');

            }
        };


        // handler for when a select option has been updated
        scope.optionSelected = function () {
            var applyBtn = $('#applyBtn');
            if (applyBtn.next('div.popover:visible').length){
                applyBtn.popover('destroy');
            }
        };

        // error messages
        scope.setAlert = function (message, cssClass) {
            scope.errorMessage = message;
            scope.classError = cssClass;
        };

        setStatus();
        getModerationList();
        getUgcList();
        moderationActions();

        scope.setAlert("", "hide-error");

    }]);