'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
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
                            var currentEl = $(ev.currentTarget),
                                action = currentEl.val().toUpperCase(),
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
                                angular.forEach(scope.$parent.$parent.ugcList, function (ugc, index) {
                                    if (ugc.id === data.id) {
                                        // removing active state of clicked button
                                        if (currentEl.hasClass('active')) {
                                            currentEl.removeClass('active');
                                        }
                                        scope.$parent.ugcList[index].updated = true;
                                        scope.$parent.ugcList[index].updateMessage = scope.$parent.ugcList[index].title + " - " + scope.$parent.ugcList[index].dateAdded
                                        scope.$parent.ugcList[index].alertClass = "success";
                                        scope.$parent.ugcList[index].undo = true;
                                    }
                                });
                            }).error(function (data) {
                                scope.$parent.ugcList[index].updated = true;
                                scope.$parent.ugcList[index].updateMessage = "Error tryinh to update"
                                scope.$parent.ugcList[index].alertClass = "error";
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

                    // hiding error message
                    var applyBtn = $('#applyBtn');
                    if (applyBtn.next('div.tooltip:visible').length){
                        applyBtn.tooltip('hide');
                    }
                }else {
                    $('.entries-list input').prop('checked', false);
                }
            });
        };
    }]).
    directive('detailInformation', ['$sanitize', function (sanitize) {
        return {
            restrict: "A",
            link: function (scope, elm, attrs) {
                attrs.$observe('detailInformation', function (val) {
                    var down = elm.find(".icon-chevron-down"),
                        up = elm.find(".icon-chevron-up");

                    elm.bind('click', function (ev) {
                        if (elm.hasClass('collapsed')){
                            angular.forEach(scope.$parent.ugcList, function (ugc) {
                                if (ugc.id === val) {
                                    var wholeContent = sanitize(ugc.completeContent);
                                    elm.find(".ugc-content").html(wholeContent);

                                    elm.toggleClass("collapsed");
                                    elm.toggleClass("expanded");

                                    up.toggleClass('hide');
                                    down.toggleClass('hide');
                                }
                            });
                        }else if (elm.hasClass('expanded')) {
                            angular.forEach(scope.$parent.ugcList, function (ugc) {
                                if (ugc.id === val) {
                                    var partialContent = ugc.textContent;
                                    elm.find(".ugc-content").html(partialContent);

                                    elm.toggleClass("collapsed");
                                    elm.toggleClass("expanded");

                                    down.toggleClass('hide');
                                    up.toggleClass('hide');
                                }
                            });
                        }
                    });
                });
            }
        }
    }]);