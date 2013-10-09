'use strict';

/**
 * Moderation Dashboard Controller
 * - get UGCs by moderation status
 * - Update individual moderation status
 **/

angular.module('moderationDashboard.controllers', []).
    controller('UgcListCtrl', ['$scope', 'Api', '$routeParams', function(scope, api, rp) {
        // get UGCs by moderation status
        var queryParams = {},
            getModerationStatus = function () {
                var moderationStatus = rp.moderationStatus;
                if (moderationStatus !== undefined) {
                    queryParams.moderation = moderationStatus + ".json";
                }
            };

        api.defaultTenant.get(function (conf) {
            queryParams.moderation = conf.moderation + "json";
            queryParams.tenant = conf.tenant;

            getModerationStatus();

            scope.ugcList = [];
            api.Ugc.query(queryParams,
                function (list) {
                    var txtContent = {};
                    angular.forEach(list, function (ugc){
                        txtContent = angular.fromJson(ugc.textContent);

                        scope.ugcList.push({
                            'title': txtContent.title,
                            'id': ugc.id,
                            'textContent': txtContent.content,
                            'moderationStatus': ugc.moderationStatus
                        });
                    });
                }
            );
        });
    }])
    .controller('MyCtrl2', [function() {
        // controller 2
    }]);