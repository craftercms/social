'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationAction', ['PERMANENTLY_DELETE', 'CONFIG', 'UgcApi', function (PD, CONFIG, UgcApi) {
        return {
            restrict: "E",
            templateUrl: CONFIG.TEMPLATES_PATH +  "moderation_actions.html",

            link: function (scope, elm, attrs) {
                scope.ugcid = attrs.ugcid;

                var actions = scope.moderationActions;
                angular.forEach(actions, function (action, i) {
                    if (action.status.toUpperCase() === PD.ACTION){
                        action.isDelete = true;
                    }else{
                        action.isDelete = false;
                    }
                });

                elm.find('.mod-actions-btn').delegate('.btn-mod-action', 'click', function (ev) {
                    var currentEl = $(ev.currentTarget),
                        action = currentEl.val().toUpperCase(),
                        queryParams = {
                            moderationid: scope.ugcid,
                            moderationstatus : action,
                            tenant: scope.confObj.tenant
                        };

                    UgcApi.updateUgc(queryParams).then(function (data) {
                        scope.displayResults(data, currentEl, { undo: true, message: "" });
                    });
                });
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
    }]).

    directive('popoverMessage', ['CONFIG', '$timeout', 'PERMANENTLY_DELETE', 'UgcApi', function (CONFIG, timeout, PD, UgcApi) {
        return {
            restrict: 'E',
            templateUrl: CONFIG.TEMPLATES_PATH +  "popover_message.html",
            link: function (scope, elm, attrs) {
                scope.ugcid = attrs.ugcid;
                scope.ugclabel = attrs.ugclabel;

                scope.showPopover = function (event) {
                    if (! $(event.currentTarget).next('div.popover:visible').length) {
                        $(event.currentTarget).popover({
                            animation: true,
                            placement: 'right',
                            trigger: 'manual',
                            'html': true,
                            title: 'Are you sure ?',
                            content: function (){
                                var popover = $(event.currentTarget).next('.popover');
                                popover.removeClass('hidden');
                                return popover.html();
                            }
                        }).popover('show');
                    }
                };

                timeout(function () {
                    elm.delegate('.delete-btn', 'click', function (ev) {
                        var target = $(ev.currentTarget);
                        var popover = $(ev.currentTarget).parents('.popover').prev('.active');

                        if (target.val().toUpperCase() === PD.CONFIRM) {
                            var conf = {
                                tenant: scope.confObj.tenant,
                                ugcids: [scope.ugcid]
                            };

                            UgcApi.permanentlyDelete(conf).then(function (data) {
                                if (data){

                                    popover.popover('destroy');
                                    scope.displayResults(data, currentEl, { undo: false, message: "Comment successfully deleted" });
                                }else {
                                    console.log("error trying to delete comment");
                                    //TODO display message explaining the reason
                                }
                            });
                        }else{
                            popover.removeClass('active');
                            popover.popover('hide');
                        }
                    });
                });
            }
        };
    }]);