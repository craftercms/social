'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationAction', 
        ['ACTIONS', 
         'CONFIG', 
         'UgcApi',
         'DeletePopupService', function (ACTIONS, CONFIG, UgcApi, DeletePopupService) {

        return {
            restrict: "E",
            templateUrl: CONFIG.TEMPLATES_PATH +  "moderation_actions.html",

            link: function (scope, elm, attrs) {
                scope.ugcid = attrs.ugcid;

                elm.find('.mod-actions-btn').delegate('.btn-mod-action', 'click', function (ev) {
                    var currentEl = $(ev.currentTarget),
                        actionAttr = currentEl.attr('data-action-type'),
                        newStatus, queryParams;

                    switch (actionAttr) {
                        case ACTIONS.EDIT:
                            scope.$apply( function () {
                                scope.editMode = !scope.editMode;
                            });
                            break;
                        case ACTIONS.DELETE:
                            DeletePopupService.open(ev.currentTarget, {
                                tenant: scope.confObj.tenant, 
                                items: [scope.ugcid]
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
    }]);
