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

                    // handler when a options is selected, changes the status
                    timeout(function () {
                        elm.find('.btn-group').delegate('.btn', 'click', function (ev) {
                            var action = $(ev.currentTarget).val().toUpperCase(),
                                queryParams = {
                                    moderationid: element.attr('ugcid'),
                                    moderationstatus : action,
                                    tenant: scope.$parent.confObj.tenant
                                };

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
                                        "UGC updated correctly - " + action,
                                        "alert-success"
                                    );
                                }
                            }).error(function (data) {
                                scope.$parent.setAlert("Error trying to save UGC", "alert-error");
                            });

                        });
                    });
                }
            }
        };
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
    }]).
    directive('detailInformation', ['$sanitize', function (sanitize) {
        return {
            restrict: "A",
            link: function (scope, elm, attrs) {
                attrs.$observe('detailInformation', function (val) {
                    elm.bind('click', function (ev) {
                        if (elm.hasClass('collapsed')){
                            angular.forEach(scope.$parent.ugcList, function (ugc) {
                                if (ugc.id === val) {
                                    var wholeContent = sanitize(ugc.completeContent);

                                    elm.find(".ugc-content").html(wholeContent);
                                }
                            });
                            elm.toggleClass("collapsed");
                            elm.toggleClass("expanded");
                        }else if (elm.hasClass('expanded')) {
                            angular.forEach(scope.$parent.ugcList, function (ugc) {
                                if (ugc.id === val) {
                                    var partialContent = ugc.textContent;
                                    elm.find(".ugc-content").html(partialContent);
                                }
                            });
                            elm.toggleClass("collapsed");
                            elm.toggleClass("expanded");
                        }
                    });
                });
            }
        }
    }]);