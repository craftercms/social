'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationStatus', [ '$routeParams', function (rp) {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/moderation_status.html",
            link: function (scope, elm, attrs) {
                if (rp.moderationStatus === undefined) {
                    rp.moderationStatus = "unmoderated";
                }
            }
        }
    }]).
    directive('appVersion', ['version', function(version) {
        return function(scope, elm) {
            elm.text(version);
        };
    }]).
    directive('moderationAction', ['Api', '$http', function (api, http) {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/moderation_actions.html",

            scope: {
                modstatus: '@',
                ugcid: '@'
            },
            link: {
                pre: function (scope, elm, attrs) {
                    attrs.$observe('modstatus', function () {
                        angular.forEach(scope.$parent.moderationList, function (modObject) {
                            if (modObject.moderation.toLowerCase() === scope.modstatus.toLocaleLowerCase()){
                                scope.moderationActions = modObject.actions;
                            }
                        });
                    });
                },
                post: function (scope, elm) {
                    // updating moderation value
                    var element = elm;

                    elm.find('.entries-list-btn').bind('click', { options: elm.find('.btn-group') }, function (ev) {
                        var moderationSelected = ev.data.options.find('.active').text(),
                            queryParams = {
                                moderationid: element.attr('ugcid'),
                                moderationstatus : moderationSelected.toUpperCase(),
                                tenant: scope.$parent.tenantObj.tenant
                            };

                        http({
                            method: 'POST',
                            url: "/crafter-social/api/2/ugc/moderation/" + queryParams.moderationid + "/status.json?moderationStatus=" + queryParams.moderationstatus + "&tenant=" + queryParams.tenant,
                            data: queryParams,
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                        }).success(function (data) {
                            var key;
                            angular.forEach(scope.$parent.ugcList, function (ugc, index) {
                                if (ugc.id === data.id) {
                                    key = index;
                                }
                            });
                            if (key !== undefined) {
                                scope.$parent.ugcList.splice(key, 1);

                                //TODO efect for removed item
                            }
                        }).error(function (data) {
                            console.log("error");
                        });

                        //api.updateModeration.save(queryParams, function () {
                        //    console.log('saving');
                        //});

                        //TODO: validate if no moderation is selected
                    });
                }
            }
        };
    }]).
    directive('userProfile', ['Api', function (api) {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/user_profile.html",
            link: function (scope, elm, attrs) {

            }
        }
    }]);