'use strict';

angular.module('moderationDashboard.controllers', []).
    
    /**
     * Moderation Dashboard Controller
     * - get UGCs by moderation status
     **/
    controller('UgcListCtrl',
        ['$rootScope',
         '$scope',
         '$routeParams',
         '$timeout',
         'UgcApi',
         'DeletePopupService',
         'PaginationService',
         'ENV', 
         'CONFIG', function($rootScope, scope, $routeParams, $timeout, UgcApi, DeletePopupService, PaginationService, ENV, CONFIG) {

        // moderationStatus will always be in the URL
        scope.status = $routeParams.moderationStatus.toUpperCase();
        scope.ugcList = [];
        scope.moderationActions = [];
        scope.bulkActions = [];
        scope.sections = ENV.sections;

        function cropText (text, upperLimit) {
            return (text.length > upperLimit) ? text.substring(0, upperLimit) + " [...]" : text;
        }

        // Transform the targetUrl only if there are values for targetUrl.pattern & 
        // targetUrl.replace (in app config file)
        function getTargetUrl (targetUrl, targetUrlConfig) {
            var re;

            if (targetUrl && typeof targetUrlConfig.pattern == 'string' && 
                    typeof targetUrlConfig.replace == 'string') {
                re = new RegExp(targetUrlConfig.pattern);
                return targetUrl.replace(re, targetUrlConfig.replace);
            }
            return targetUrl;
        }

        scope.updateUGCContent = function (ugc) {            
            var callConfig, ugcData;

            $timeout( function() {
                scope.$apply( function() {
                    ugc.teaser = cropText(ugc.textContent, 200);
                    ugc.isExpandable = (ugc.teaser == ugc.textContent) ? false : true;
                });
            });

            callConfig = {
                ugcId: ugc.id,
                tenant: ENV.config.tenant
            };
            ugcData = {
                ugcId: ugc.id,
                targetId: ugc.targetId,
                targetUrl: ugc.targetUrl,
                targetDescription: ugc.targetText,
                textContent: ugc.textContent,
                subject: ugc.title
            };
            UgcApi.updateUGCContent(ugcData, callConfig);
        };        

        // get ugc list by status from rest call
        scope.getUgcList = function (page) {
            
            var conf = {
                tenant: ENV.config.tenant,
                moderation: scope.status + ".json",
                page: page,
                pageSize: ENV.config.pagination.itemsPerPage
            };

            UgcApi.getUgcList(conf).then(function (data) {
                if (data) {
                    var tmpList = [],
                        teaser, isExpandable;

                    angular.forEach(data, function (ugc){

                        teaser = cropText(ugc.textContent, 200);
                        isExpandable = (teaser == ugc.textContent) ? false : true;

                        tmpList.push({
                            'title': ugc.subject,
                            'id': ugc.id,
                            'teaser': teaser,
                            'isExpandable': isExpandable,
                            'textContent': ugc.textContent,
                            'moderationStatus': ugc.moderationStatus,
                            'creationDate': scope.getDateTime(ugc.creationDate),
                            'displayName': ugc.profile.displayName,
                            'userImg': CONFIG.IMAGES_PATH + "profile-photo.jpg",
                            'targetId': ugc.targetId,
                            'targetUrl': ugc.targetUrl,
                            'targetUrlMod': getTargetUrl(ugc.targetUrl, ENV.config.targetUrl),
                            'targetText': ugc.targetDesc,
                            'updated': false,
                            'updateMessage': "",
                            'alertClass': "",
                            'undo': false
                        });
                    });

                    scope.ugcList = tmpList;

                    //if no results available a information messsage will be displayed
                    if (scope.ugcList.length > 0) {
                        scope.displayUgcs = true;
                    }else {
                        scope.displayUgcs = false;
                        scope.infoMessage = "No results found";
                    }
                }else {
                    scope.displayUgcs = false;
                    scope.infoMessage = "error, Comments were not found";
                }
            });
        };

        // get date and hour of last update
        scope.getDateTime = function (dateLong) {
            var date = new Date(dateLong);

            return date.toDateString();
        }

        // set list of moderation actions available according status selected
        var setScopeActions = function () {
            angular.forEach(ENV.sections, function (section) {
                if (section.moderation.value.toUpperCase() === scope.status) {
                    scope.moderationActions = section.actions;

                    // Iterate through the action objects to identify the bulk operations
                    angular.forEach(section.actions, function(actionObj) {
                        if (!actionObj.notBulk) {
                            scope.bulkActions.push(actionObj);
                        }
                    })
                }
            });
        };

        // handler when input checkbox is clicked. Hide error message if is displayed
        scope.bulkItemsUpdate = function (event) {
            if ($(event.currentTarget).prop('checked')) {
                // hiding error message
                var applyBtn = $('#applyBtn');
                if (applyBtn.next('div.tooltip:visible').length){
                    applyBtn.tooltip('hide');
                }
                DeletePopupService.destroy();
            }
        }

        //handler when undo is clicked
        scope.reverseAction = function (event, id) {
            var conf = {
                moderationid : id,
                moderationstatus: scope.status,
                tenant: ENV.config.tenant
            };

            UgcApi.updateUGCStatus(conf).then(function (data) {
                if (data) {
                    angular.forEach(scope.ugcList, function (ugc, index) {
                        if (ugc.id === data.id) {
                            scope.ugcList[index].updated = false;
                            scope.ugcList[index].updateMessage = "";
                            scope.ugcList[index].alertClass = "";
                            scope.ugcList[index].undo = false;
                        }
                    });
                }else {
                    console.log("error trying to bulk update");
                }
            });
        };

        scope.displayResults = function (data, element, conf) {
            if (data) {
                angular.forEach(scope.ugcList, function (ugc, index) {
                    if (ugc.id === data.id) {
                        // removing active state of clicked button
                        var message = "";

                        if (conf.undo) {
                            if (element.hasClass('active')) {
                                element.removeClass('active');
                            }
                            message = scope.ugcList[index].title + " - " + scope.ugcList[index].creationDate;
                        }else {
                            message = conf.message;
                        }
                        scope.ugcList[index].updated = true;
                        scope.ugcList[index].updateMessage = message;
                        scope.ugcList[index].alertClass = "success";
                        scope.ugcList[index].undo = conf.undo;
                    }
                });
            }else {
                //TODO error message: data needs to have information about the error
                console.log("error trying to update");
            }
        };

        scope.getUgcList(1);
        setScopeActions();

        $rootScope.$on('$ugcUpdate', function(){
            var currentPage = PaginationService.getCurrentPage();
            scope.getUgcList(currentPage);
        });
    }]).

    /**
     * bulk action controller
     */
    controller('BulkActionCtrl', 
        ['$scope', 
         'UgcApi',
         'DeletePopupService',
         'PaginationService',
         'ACTIONS',
         'ENV', function (scope, UgcApi, DeletePopupService, PaginationService, ACTIONS, ENV) {

        var commentsSelected = function (event) {
            var listItems = [];
            $('.entries-list .selector').each(function (index) {
                if ($(this).prop('checked')){
                    listItems.push($(this).attr('ugcid'));
                }
            });

            if (listItems.length <= 0){
                var displayError = $(event.currentTarget).tooltip({
                    animation: true,
                    placement: 'right',
                    trigger: 'manual',
                    title: 'Please select at least one comment'
                });
                displayError.tooltip('show');

                return null;
            }

            return listItems;
        };

        //handler for bulk actions update
        scope.bulkUpdate = function (event) {
            var optionSelected = scope.bulkAction,
                listItems = [];

            //check if bulk option is selected
            if (optionSelected === null || typeof optionSelected === 'undefined' ){
                $('#bulkActions').focus();
                return;
            }

            // check if an item has been selected
            listItems = commentsSelected(event);
            if (listItems === null){
                return;
            }

            optionSelected = optionSelected.toUpperCase();

            if (ACTIONS.DELETE.toUpperCase() === optionSelected) {
                DeletePopupService.open(event.currentTarget, {
                    tenant: ENV.config.tenant, 
                    items: listItems
                });
            } else {
                var conf = {
                    moderationstatus: optionSelected,
                    tenant: ENV.config.tenant,
                    ids: listItems
                };

                UgcApi.bulkUpdate(conf).then( function (data) {
                    PaginationService.removeItems(listItems.length);
                    scope.displayResults(data, { undo: true, message: "" } );
                });
            }
        };

        scope.displayResults = function (data, conf) {
            if (data) {
                angular.forEach(data, function (item) {
                    angular.forEach(scope.ugcList, function(ugc, index){
                        if (conf.undo) {
                            conf.message = scope.ugcList[index].title + " - " + scope.ugcList[index].creationDate;
                        }

                        if (item.id === ugc.id) {
                            scope.ugcList[index].updated = true;
                            scope.ugcList[index].updateMessage = conf.message;
                            scope.ugcList[index].alertClass = "success";
                            scope.ugcList[index].undo = conf.undo;

                            return;
                        }
                    });
                });
            }else {
                console.error("error bulk update");
                angular.forEach(data, function (item) {
                    angular.forEach(scope.ugcList, function(ugc, index){
                        if (item.id === ugc.id) {
                            scope.ugcList[index].updated = true;
                            scope.ugcList[index].updateMessage = "Error trying to update"
                            scope.ugcList[index].alertClass = "error";

                            return;
                        }
                    });
                });
            }
        };

        // handler for when a select option has been updated
        scope.bulkOptionSelected = function () {
            var applyBtn = $('#applyBtn');
            if (this.bulkAction !== null) {
                applyBtn.removeClass('disabled');
            }else {
                applyBtn.addClass('disabled');

                // hide error message if has been displayed
                if (applyBtn.next('div.tooltip:visible').length){
                    applyBtn.tooltip('hide');
                }
            }
            DeletePopupService.destroy();
        };
    }]).

    /**
     * pagination controller
     */
    controller('PaginationCtrl', 
        ['$scope',
         '$route',
         'PaginationService', function ($scope, $route, PaginationService) {

        PaginationService.init($route.current.locals.totalItems);
        $scope.pagination = PaginationService.data;

        // change page when user clicks the page button
        $scope.changePage = function (pageNumber) {
            $scope.$parent.getUgcList(pageNumber);
            PaginationService.setCurrentPage(pageNumber);
        };
    }]);