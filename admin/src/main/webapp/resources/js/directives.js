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

                        http.post(
                            "/crafter-social/api/2/ugc/moderation/" + queryParams.moderationid + "/status.json?moderationStatus=" + queryParams.moderationstatus + "&tenant=" + queryParams.tenant
                        ).success(function (data) {
                                console.log('success');
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
    }]);