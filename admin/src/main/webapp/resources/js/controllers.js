'use strict';

/* Controllers */

angular.module('moderationDashboard.controllers', []).
    controller('UgcListCtrl', ['$scope', 'Ugc', function(scope, UgcResource) {
        scope.ugcList = [];
        UgcResource.query(
            {
                moderation: 'UNMODERATED.json',
                tenant: 'craftercms'
            },
            function (list) {
                var txtContent = {};
                angular.forEach(list, function (ugc){
                    txtContent = angular.fromJson(ugc.textContent);

                    scope.ugcList.push({
                        'textContent': txtContent.content,
                        'moderationStatus': ugc.moderationStatus,
                        'id': ugc.id
                    });
                });
            }
        );
    }])
    .controller('MyCtrl2', [function() {

    }]);