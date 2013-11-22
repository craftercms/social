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
                        actionAttr = currentEl.attr('data-action-type'),
                        newStatus, queryParams;

                    switch (actionAttr) {
                        case 'edit':
                            scope.$apply( function () {
                                scope.editMode = !scope.editMode;
                            });
                            break;
                        default:
                            // If no action.type is defined (in the actions config file) then the default 
                            // action is to update the comment's status
                            newStatus = currentEl.val().toUpperCase(),
                            queryParams = {
                                moderationid: scope.ugcid,
                                moderationstatus : newStatus,
                                tenant: scope.confObj.tenant
                            };
                            UgcApi.updateUGCStatus(queryParams).then(function (data) {
                                scope.displayResults(data, currentEl, { undo: true, message: "" });
                            });       
                    }
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