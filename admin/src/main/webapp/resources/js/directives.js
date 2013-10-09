'use strict';

/* Directives */
angular.module('moderationDashboard.directives', []).
    directive('status', [ '$routeParams', function (rp) {
        return  function (scope, elm) {
            if (rp.moderationStatus === undefined) {
                rp.moderationStatus = "unmoderated";
            }

            var elmStatus = $(elm).attr('data-status');
            if (elmStatus.toLowerCase() === rp.moderationStatus.toLowerCase()){
                elm.addClass('active');
            }
        };
    }]).
    directive('appVersion', ['version', function(version) {
        return function(scope, elm) {
            elm.text(version);
        };
    }]).
    directive('moderationAction', ['Api', function (api) {
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
                        api.moderationAction.query(function (moderationList) {
                            angular.forEach(moderationList, function (modObject) {
                                if (modObject.moderation.toLowerCase() === scope.modstatus.toLocaleLowerCase()){
                                    scope.moderationActions = modObject.actions;
                                }
                            });
                        });
                    });
                },
                post: function (scope, elm) {
                    // updating moderation value
                    elm.find('button').bind('click', { select: elm.find('select') }, function (ev) {
                        var moderationSelected = ev.data.select.find(':selected').val(),
                            that = this;

                        api.defaultTenant.get(function (conf) {
                            var queryParams = {
                                ugcId: that.getAttribute('data-ugcid'),
                                modstatus : moderationSelected.toUpperCase() + ".json",
                                tenant: conf.tenant
                            };

                            api.updateModeration.update(queryParams, function (updatedUgc){
                                console.log(updatedUgc);
                            });
                        });
                    });
                }
            }
        };
    }]);