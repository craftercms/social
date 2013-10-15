'use strict';

/**
 * Moderation Dashboard Controller
 * - get UGCs by moderation status
 * - Update individual moderation status
 **/

angular.module('moderationDashboard.controllers', []).
    controller('UgcListCtrl', ['$scope', 'Api', '$routeParams', function(scope, api, rp) {
        // variables
        scope.tenantObj;
        scope.defaultModeration;
        scope.moderationList = [];
        scope.ugcList = [];

        var getTenant = function () {
                api.defaultTenant.get(function (conf) {
                    if (conf) {
                        var tmpStatus;
                        if (rp.moderationStatus) {
                            tmpStatus = rp.moderationStatus;
                        }

                        scope.tenantObj = {
                            tenant: conf.tenant,
                            defaultModeration: tmpStatus ? tmpStatus + ".json" : conf.moderation.toUpperCase() + ".json"
                        };
                    }else {
                        //TODO: error message, no tenant was provide
                    }
                });
            },
            getModerationList = function () {
                api.moderationAction.query(function (moderationList) {
                    if ( moderationList !== undefined && moderationList.length > 0 ){
                        angular.forEach(moderationList, function (moderationObj) {
                            if (rp.moderationStatus.toLocaleLowerCase() === moderationObj.moderation.toLowerCase()) {
                                moderationObj.state = 'active';
                            }else{
                                moderationObj.state = '';
                            }
                        });
                        scope.moderationList = moderationList;
                    }else{
                        //TODO: error message, no moderation status provided
                    }
                });
            };

        getTenant();
        getModerationList();

        // get UGCs by moderation status
        scope.$watch('tenantObj', function (newValue) {
            if (newValue && newValue.tenant && newValue.defaultModeration) {

                scope.ugcList = [];
                api.Ugc.query({
                        tenant: newValue.tenant,
                        moderation: newValue.defaultModeration
                    },
                    function (list) {
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
                    }
                );
            }
        }, true);
    }])
    .controller('MyCtrl2', [function() {
        // controller 2
    }]);