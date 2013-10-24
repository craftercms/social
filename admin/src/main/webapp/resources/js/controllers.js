'use strict';

angular.module('moderationDashboard.controllers', []).
    /**
     * AppCtrl
     * main controller
     *  contains tenant, default moderation status and the list of moderations
     *  all this variables are in properties files
     */
    controller('AppCtrl', ['$scope', 'Api', function (scope, api) {
        scope.confObj = {};
        scope.moderationList = [];

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
                        'textContent': txtContent.content,
                        'moderationStatus': ugc.moderationStatus
                    });
                });

                scope.ugcList = tmpList;
            });
        };

        // set list of moderation actions available according status selected
        var moderationActions = function () {
            angular.forEach(scope.moderationList, function (modObject) {
                if (modObject.moderation.toLowerCase() === scope.status.toLocaleLowerCase()){
                    scope.moderationActions = modObject.actions;
                }
            });
        };

        //handler for bulk actions update
        scope.bulkUpdate = function () {
            var optionSelected = $('#bulkActions').find(':selected').val(),
                listItems = [];

            if (optionSelected !== "0"){
                $('.entries-list input').each(function (index) {
                    if ($(this).prop('checked')){
                        listItems.push($(this).attr('ugcid'));
                    }
                });

                if (listItems.length > 0) {
                    optionSelected = optionSelected.toUpperCase();
                    var ids = "&ids=";
                    ids += listItems.join("&ids=");

                    console.log(ids);

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
                    }).error(function (data) {
                        console.log("error bulk update");
                    });

                    //TODO change for restangular
                }else{
                    //TODO display error message, please check an option
                }
            }else{
                //TODO display error message, please select an option
            }

        };


        setStatus();
        getModerationList();
        getUgcList();
        moderationActions();
    }]);