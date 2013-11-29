'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('moderationAction', 
        ['ENV',
         'ACTIONS', 
         'CONFIG', 
         'UgcApi',
         'DeletePopupService',
         'PaginationService', function (ENV, ACTIONS, CONFIG, UgcApi, DeletePopupService, PaginationService) {

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
                                tenant: ENV.config.tenant, 
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
                                tenant: ENV.config.tenant
                            };
                            UgcApi.updateUGCStatus(queryParams).then(function (data) {
                                // Reduce total number of items in the pagination by 1; however,
                                // page reduction won't actually take place until we click on the 
                                // page button 
                                PaginationService.removeItems(1);
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
                    $('.entries-list .selector').prop('checked', true);

                    // hiding error message
                    var applyBtn = $('#applyBtn');
                    if (applyBtn.next('div.tooltip:visible').length){
                        applyBtn.tooltip('hide');
                    }
                }else {
                    $('.entries-list .selector').prop('checked', false);
                }
            });
        };
    }]);
