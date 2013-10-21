'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationStatus', [ '$routeParams', function (rp) {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/moderation_status.html",
            link: function (scope, elm, attrs) {

            }
        }
    }]).
    directive('appVersion', ['version', function(version) {
        return function(scope, elm) {
            elm.text(version);
        };
    }]).
    directive('moderationAction', ['Api', '$http', '$timeout', function (api, http, timeout) {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/moderation_actions.html",

            scope: {
                modstatus: '@',
                ugcid: '@'
            },
            link: {
                pre: function (scope, elm, attrs) {
                    scope.moderationActions = scope.$parent.moderationActions;
                },
                post: function (scope, elm) {
                    // updating moderation value
                    var element = elm;

                    elm.find('.entries-list-btn').bind('click', { options: elm.find('.btn-group') }, function (ev) {
                        var moderationSelected = ev.data.options.find('.active').text(),
                            queryParams = {
                                moderationid: element.attr('ugcid'),
                                moderationstatus : moderationSelected.toUpperCase(),
                                tenant: scope.$parent.confObj.tenant
                            };

                        // validate if any option was selected
                        if (moderationSelected !== ""){
                            //TODO change $resource for restangular
                            http({
                                method: 'POST',
                                url: "/crafter-social/api/2/ugc/moderation/" + queryParams.moderationid + "/status.json?moderationStatus=" + queryParams.moderationstatus + "&tenant=" + queryParams.tenant,
                                data: queryParams,
                                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                            }).success(function (data) {
                                    var key;
                                    angular.forEach(scope.$parent.$parent.ugcList, function (ugc, index) {
                                        if (ugc.id === data.id) {
                                            key = index;
                                        }
                                    });
                                    if (key !== undefined) {
                                        scope.$parent.ugcList.splice(key, 1);

                                        //TODO efect for removed item
                                        scope.$parent.setAlert(
                                            "UGC updated correctly",
                                            "alert-success"
                                        );
                                    }
                                }).error(function (data) {
                                    scope.$parent.setAlert("Error trying to save UGC", "alert-error");
                                });
                        }else{
                            // error message when an action was not selected
                            var displayError = $(this).popover({
                                animation: true,
                                placement: 'right',
                                trigger: 'manual',
                                title: 'Error Title',
                                content: 'Please select an option'
                            });

                            displayError.popover('show');
                        }
                    });

                    // handler when a options is selected and the error message is displayed
                    timeout(function () {
                        elm.find('.btn-group').delegate('.btn', 'click', {saveBtn: elm.find('.entries-list-btn')}, function (ev) {
                            if ($(ev.data.saveBtn).next('div.popover:visible').length){
                                $(ev.data.saveBtn).popover('hide');
                            }
                        });
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
    }]).
    directive('selectallugcs', [function () {
        return function (scope, elm) {
            elm.change(function (ev) {
                if (elm.is(":checked")) {
                    $('.entries-list input').prop('checked', true);
                }else {
                    $('.entries-list input').prop('checked', false);
                }
            });
        };
    }]).
    directive('alert', [function () {
        return {
            restrict: "E",
            templateUrl: "/crafter-social-admin/resources/templates/error.html",
            link: function (scope, elm, attrs) {

            }
        }
    }]);