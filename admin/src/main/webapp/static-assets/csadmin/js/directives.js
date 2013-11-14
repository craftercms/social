'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationAction', ['$timeout', 'UgcApi', 'PERMANENTLY_DELETE', 'CONFIG', function (timeout, UgcApi, PD, CONFIG) {
        return {
            restrict: "E",
            templateUrl: CONFIG.TEMPLATES_PATH +  "moderation_actions.html",

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
                    var element = elm,
                        showPopover = function (element) {
                            if (! $(element).next('div.popover:visible').length) {
                                $(element).popover({
                                    animation: true,
                                    placement: 'right',
                                    trigger: 'manual',
                                    'html': true,
                                    title: 'Are you sure ?',
                                    content: function (){
                                        var popover = $(element).next('.popover');
                                        popover.removeClass('hidden');
                                        return popover.html();
                                    }
                                }).popover('show');
                            }
                        };

                    // handler when a options is selected, changes the status
                    timeout(function () {
                        elm.find('.mod-actions-btn').delegate('.btn-mod-action', 'click', function (ev) {
                            var currentEl = $(ev.currentTarget),
                                action = currentEl.val().toUpperCase(),
                                queryParams = {
                                    moderationid: element.attr('ugcid'),
                                    moderationstatus : action,
                                    tenant: scope.$parent.confObj.tenant
                                };

                            // if action selected is delete / other
                            if (currentEl.val().toUpperCase() === PD.ACTION) {
                                showPopover(this);
                            }else{
                                UgcApi.updateUgc(queryParams).then(function (data) {
                                    scope.$parent.displayResults(data, currentEl, { undo: true, message: "" });
                                });
                            }

                        });

                        // handler when an ugc is going to be deleted
                        elm.find('.mod-actions-btn').delegate('.delete-btn', 'click', function (ev) {
                            var currentEl = $(ev.currentTarget);

                            // if user clicked yes / no
                            if (currentEl.val().toUpperCase() === PD.CONFIRM) {
                                var conf = {
                                    tenant: scope.$parent.confObj.tenant,
                                    ugcids: [element.attr('ugcid')]
                                };

                                UgcApi.permanentlyDelete(conf).then(function (data) {
                                    if (data){
                                        $(this).popover('destroy');
                                        scope.$parent.displayResults(data, currentEl, { undo: false, message: "Comment successfully deleted" });
                                    }else {
                                        console.log("error trying to delete comment");
                                        //TODO display message explaining the reason
                                    }
                                });
                            }else{
                                var popover = $(this).parents('.popover').prev('.active');
                                popover.removeClass('active');
                                popover.popover('hide');
                            }
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