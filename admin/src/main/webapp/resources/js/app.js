/**
 * Angular Module
 */
var app = angular.module('CrafterAdminConsole', ['ngRoute', 'ui.bootstrap', 'ngCkeditor']);

/**
 * Global variables
 */
var defaultContext = 'f5b143c2-f1c0-4a10-b56e-f485f00d3fe9';
var commentsSortBy = 'createdDate';
var commentsSortOrder = 'DESC';
var ts=new Date().getTime();
var moderationStatus = [
    {
        label: 'Unmoderated',
        value: 'UNMODERATED',
        default: true
    },
    {
        label: 'Approved',
        value: 'APPROVED'
    },
    {
        label: 'Pending',
        value: 'PENDING'
    },
    {
        label: 'Spam',
        value: 'SPAM'
    },
    {
        label: 'Trash',
        value: 'TRASH'
    }
];

var configRTEEditor = function(){
    var freemarkerTag=[];
    // Mark Freemarker directives as protected
    freemarkerTag.push( /<@[\s\S]*?\/>/g );
    freemarkerTag.push( /<#[\s\S]*?>/g );
    freemarkerTag.push( /<\/#[\s\S]*?>/g );
    freemarkerTag.push( /\[@[\s\S]*?\/]/g );
    freemarkerTag.push( /\[#[\s\S]*?]/g );
    freemarkerTag.push( /\[\/#[\s\S]*?]/g );
    freemarkerTag.push( /\${[\s\S]*?}/g );
    return {
        protectedSource:freemarkerTag,
        height:250,
        fillEmptyBlocks : false,
        forcePasteAsPlainText:true,
        tabSpaces:4,
        basicEntities:false,
        fullPage : true,
        allowedContent:true
    };
};

var moderationStatusActions = {
    'UNMODERATED': [
        {
            label: 'Approve',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'APPROVED').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Spam',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'SPAM').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Trash',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'TRASH').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Save Changes',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateBody(ctxId, comment).then(function (comment) {
                    $scope.commentBodyUpdatedCallback(comment);
                });
            }
        }
        ,
        {
            label: 'Reset',
            execute: function (ctxId, comment) {
                resetBody(comment);
            }
        }
    ],
    'APPROVED': [
        {
            label: 'Mark as Spam',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'SPAM').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Trash',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'TRASH').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Unmoderated',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'UNMODERATED').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Save Changes',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateBody(ctxId, comment).then(function (comment) {
                    $scope.commentBodyUpdatedCallback(comment);
                });
            }
        }
        ,
        {
            label: 'Reset',
            execute: function (ctxId, comment) {
                resetBody(comment);
            }
        }
    ],
    'PENDING': [
        {
            label: 'Approve',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'APPROVED').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Trash',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'TRASH').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Save Changes',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateBody(ctxId, comment).then(function (comment) {
                    $scope.commentBodyUpdatedCallback(comment);
                });
            }
        },
        {
            label: 'Reset',
            execute: function (ctxId, comment) {
                resetBody(comment);
            }
        }
    ],
    'SPAM': [
        {
            label: 'Permanently delete',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.deleteComment(ctxId, comment).then(function () {
                    $scope.commentDeletedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Unmoderated',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.updateStatus(ctxId, comment, 'UNMODERATED').then(function (comment) {
                    $scope.commentStatusUpdatedCallback(comment);
                });
            }
        }
    ],
    'TRASH': [
        {
            label: 'Permanently delete',
            execute: function (ctxId, comment, $scope) {
                $scope.commentService.deleteComment(ctxId, comment).then(function () {
                    $scope.commentDeletedCallback(comment);
                });
            }
        }
    ]
};

/**
 * Constants
 */
app.constant('paginationConfig', {
    itemsPerPage: 10,
    boundaryLinks: true,
    directionLinks: true,
    previousText: '‹',
    nextText: '›',
    firstText: '«',
    lastText: '»',
    rotate: true
});

/**
 * Filters
 */
app.filter('truncateIfTooLarge', function () {
    return function (input) {
        if (input.length > 15) {
            return input.substring(0, 15) + '...';
        } else {
            return input;
        }
    }
});

/**
 * Global functions
 */
function findComment(comments, id) {
    for (var i = 0; i < comments.length; i++) {
        if (comments[i]._id == id) {
            return i;
        }
    }

    return -1;
}

function getObject(url, $http) {
    return $http.get(url,{cache:false}).then(function (result) {
        return result.data;
    });
}

function postParams(url, params, $http) {
    return $http.post(url, $.param(params), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).then(
        function (result) {
            return result.data;
        }
    );
}

function putParams(url, params, $http) {
    return $http.put(url, $.param(params), {headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).then(
        function (result) {
            return result.data;
        }
    );
}

function deleteObject(url, $http) {
    return $http.delete(url).then(function (result) {
        return result.data;
    });
}

function showGrowlMessage(type, message) {
    $.growl(message, {
        type: type,
        pause_on_mouseover: true,
        position: {
            from: 'top',
            align: 'center'
        },
        offset: 40
    });
}

function resetBody(comment) {
    comment.body = comment.bodyOrig;
}

function isLoggedIn() {
    $.getJSON(contextPath+"/crafter-security-current-auth",function(data,status,jxhl){
        if(jxhl.status!==200){
            window.location='login'
        }
    });
}

/**
 * Http Interceptors
 */
app.factory('httpErrorHandler', function ($q) {
    return {
        'response': function (response) {
            isLoggedIn();
            return response;
        },
        'responseError': function (rejection) {
                var message;
                if (rejection.status == 0) {
                    message = 'Unable to communicate with the server. Please try again later or contact IT support';
                } else if(rejection.status ===403 || rejection.status === 401){
                    window.location = 'login';
                } else {
                    message = 'Server responded with ' + rejection.status + ' error';
                    if (rejection.data.error) {
                        message += ': <strong>' + rejection.data.error + '</strong>';
                    }

                    message += '. Please contact IT support for more information';
                }

                showGrowlMessage('danger', message);


            return $q.reject(rejection);
        }
    };
});

app.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('httpErrorHandler');
}]);

/**
 * Services
 */
app.factory('contextService', function ($http) {
    return {
        getContexts: function () {
            var url = socialRestBaseUrl + '/system/context/all?context=' + defaultContext;

            return getObject(url, $http);
        },
        createContext: function (name) {
            var url = socialRestBaseUrl + '/system/context?context=' + defaultContext;

            return postParams(url, {contextName: name}, $http);
        },
        addProfileToContext: function (ctxId, profileId, roles) {
            var url = socialRestBaseUrl + '/system/context/' + ctxId + '/' + profileId + '?context=' + defaultContext;

            return postParams(url, {roles: roles.join()}, $http);
        },
        removeProfileFromContext: function (ctxId, profileId) {
            var url = socialRestBaseUrl + '/system/context/' + ctxId + '/' + profileId + '/delete?context=' + defaultContext;
            return postParams(url,{}, $http);
        }
    }
});

app.factory('commentService', function ($http) {
    return {
        getCommentsCount: function (ctxId, status) {
            var url = socialRestBaseUrl + '/comments/moderation/' + status + '/count?context=' + ctxId;

            return getObject(url, $http);
        },
        getComments: function (ctxId, status, pageNumber, pageSize) {
            var url = socialRestBaseUrl + '/comments/moderation/' + status + '?context=' + ctxId;
            if (pageNumber != undefined && pageNumber != null) {
                url += '&pageNumber=' + pageNumber;
            }
            if (pageSize != undefined && pageSize != null) {
                url += '&pageSize=' + pageSize;
            }

            url += '&sortBy=' + commentsSortBy;
            url += '&sortOrder=' + commentsSortOrder;

            return getObject(url, $http);
        },
        updateStatus: function (ctxId, comment, newStatus) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/moderate?context=' + ctxId;

            return postParams(url, {status: newStatus}, $http);
        },
        updateBody: function (ctxId, comment) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/update/?context=' + ctxId;

            return postParams(url, {body: comment.body}, $http);
        },
        deleteComment: function (ctxId, comment) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/delete/?context=' + ctxId;
            return postParams(url, {}, $http);
        }
    }
});

app.factory('attachmentService', function ($http) {
    return {
        getAttachmentUrl: function (ctxId, comment, attachmentInfo) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/attachments/' + attachmentInfo.fileId;
            url += '?context=' + ctxId;

            return url;
        },
        deleteAttachment: function (ctxId, comment, attachmentInfo) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/attachments/' + attachmentInfo.fileId;
            url += '/delete/?context=' + ctxId;

            return postParams(url, {}, $http);
        }
    }
});

app.factory('actionsService', function ($http) {
    return {
        getActions: function (ctxId) {
            var url = socialRestBaseUrl + '/system/actions?context=' + ctxId;

            return getObject(url, $http);
        },
        updateAction: function (ctxId, actionName, roles) {
            var url = socialRestBaseUrl + '/system/actions?context=' + ctxId;

            postParams(url, {actionName: actionName, roles: roles.join()}, $http).then(function () {
                showGrowlMessage('success', 'Action \'' + actionName + '\' updated');
            });
        }
    }
});


app.factory('emailPreferencesService', function ($http) {
    return {
        getNotificationTemplate: function (type, ctxId) {
            var url = socialRestBaseUrl + '/system/context/preferences/email?type=' + type + '&context=' + ctxId;
            return getObject(url, $http);
        },
        saveNotificationTemplate: function (type, ctxId, template) {
            var url = socialRestBaseUrl + '/system/context/preferences/email?context=' + ctxId;
            return postParams(url,{type: type, template: template}, $http)
        },
        getEmailConfig: function (ctxId) {
            var url = socialRestBaseUrl + '/system/context/preferences/email/config?context=' + ctxId;
            return getObject(url, $http);
        },
        saveEmailConfig: function (ctxId,emailConfig) {
            var url = socialRestBaseUrl + '/system/context/preferences/email/config?context=' + ctxId;
            return postParams(url,emailConfig,$http);
        }
    }
});

app.factory('tenantService', function ($http) {
    return {
        getTenantNames: function () {
            var url = contextPath + '/tenant/names';
            return getObject(url, $http);
        }
    }
});

app.factory('profileService', function ($http) {
    return {
        getCountByQuery: function (tenantName, query) {
            var url = contextPath + '/profile/count?tenantName=' + tenantName + '&query=' + query;

            return getObject(url, $http);
        },
        findProfiles: function (tenantName, query, start, count) {
            var url = contextPath + '/profile/find?tenantName=' + tenantName + '&query=' + query;

            if (start != undefined && start != null) {
                url += '&start=' + start;
            }
            if (count != undefined && count != null) {
                url += '&count=' + count;
            }
            return getObject(url, $http);
        },
        getProfile: function (id) {
            var url = contextPath + '/profile/' + id;
            return getObject(url, $http);
        }
    }
});

app.factory('tenantPreferenceService',function($http){
    return {

        getPreferences : function(context){
            var url = socialRestBaseUrl + '/system/context/preferences/?context=' + context;
            return getObject(url, $http);
        },
        updatePreferences : function(context,preferences){
            var url = socialRestBaseUrl + '/system/context/updatePreference/?context=' + context;
            return postParams(url,preferences,$http);
        },
        deletePreference : function(context,preferencesToDelete){
            var url = socialRestBaseUrl + '/system/context/deletePreferences/?context=' + context;
            return postParams(url,{preferences:preferencesToDelete},$http);
        }

    };
});

/**
 * Routing
 */
app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/moderation-dashboard', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/contexts', {
        controller: 'ContextsController',
        templateUrl: contextPath + '/contexts',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/security-actions', {
        controller: 'SecurityActionsController',
        templateUrl: contextPath + '/security-actions',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/notification-preferences', {
        controller: 'EmailPreferencesController',
        templateUrl: contextPath + '/notification-preferences',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            },
            emailTypes: function () {
                return [{type: "INSTANT", name: "Instant"}, {type: "DAILY", name: "Daily"}, {
                    type: "WEEKLY",
                    name: "Weekly"
                },{type:"APPROVEREMAIL",name:"Approver email"},
                    {type:"APPROVER_RESULT_TEMPLATE",name:"Approve Ugc Page"}]
            }
        }
    });

    $routeProvider.when('/search-profiles', {
        controller: 'SearchProfilesController',
        templateUrl: contextPath + '/search-profiles',
        resolve: {
            tenantNames: function (tenantService) {
                return tenantService.getTenantNames();
            }
        }
    });

    $routeProvider.when('/profile/:id', {
        controller: 'ProfileController',
        templateUrl: contextPath + '/profile',
        resolve: {
            profile: function ($route, profileService) {
                return profileService.getProfile($route.current.params.id);
            },
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/tenant-preferences', {
        controller: 'TenantPreferencesController',
        templateUrl: contextPath + '/tenant-preferences',
        resolve: {
            contexts: function (contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.otherwise({
        redirectTo: '/'
    });
});

/**
 * Controllers
 */
app.controller('ModerationDashboardController', function ($scope, commentService, attachmentService, contexts,tenantPreferenceService) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;
    $scope.commentService = commentService;
    $scope.contexts = contexts;
    $scope.selectedContext = $scope.contexts[0];
    $scope.itemsPerPage = 5;
    $scope.editorOptions=configRTEEditor();
    $scope.editorOptions["toolbar"]=[['Bold',
        'Italic',
        'Underline', '-',
        'NumberedList',
        'BulletedList', '-',
        'Link', '-',
        'Image', '-','Source']];
    $scope.editorOptions["height"]=100;
    $scope.getCurrentPage = function () {
        ts=new Date().getTime();
        commentService.getComments($scope.selectedContext._id, $scope.selectedStatus, $scope.currentPage,
            $scope.itemsPerPage).then(function (comments) {
                for (var i = 0; i < comments.length; i++) {
                    comments[i].bodyOrig = comments[i].body;
                }
                $scope.comments = comments;
            });
    };

    $scope.resetStatus = function () {
        ts=new Date().getTime();
        for (var i = 0; i < moderationStatus.length; i++) {
            if (moderationStatus[i].default) {
                $scope.selectedStatus = moderationStatus[i].value;
                break;
            }
        }
    };

    $scope.getComments = function () {
        commentService.getCommentsCount($scope.selectedContext._id, $scope.selectedStatus).then(function (count) {
            $scope.totalItems = count;
            $scope.currentPage = 1;
            $scope.getCurrentPage();
        });
    };
    tenantPreferenceService.getPreferences($scope.selectedContext._id).then(function(result){
        $scope.preferences=result.preferences;
        console.log($scope.preferences);
    });

    $scope.profileAvatar = function(profileId){
       return  socialRestBaseUrl + '/profile/avatar/' + profileId + '?context=' + $scope.selectedContext._id+"ts="+ts;
    };

    $scope.resetStatusAndGetComments = function () {
        $scope.resetStatus();
        $scope.getComments();
    };

    $scope.executeAction = function (action, comment) {
        action.execute($scope.selectedContext._id, comment, $scope);
        ts=new Date().getTime();
    };

    $scope.commentStatusUpdatedCallback = function (comment) {
        $scope.getComments();

        showGrowlMessage('success', 'Status of comment \'' + comment._id + '\' changed to \'' +
        comment.moderationStatus + '\'');
    };

    $scope.commentBodyUpdatedCallback = function (comment) {
        comment.bodyOrig = comment.body;

        showGrowlMessage('success', 'Comment \'' + comment._id + '\' updated');
    };

    $scope.commentDeletedCallback = function (comment) {
        $scope.getComments();

        showGrowlMessage('success', 'Comment \'' + comment._id + '\' deleted');
    };

    $scope.showAttachmentsModal = function (comment) {
        $scope.selectedComment = comment;

        $('#attachmentsModal').modal('show');
    };

    $scope.showFlagsModal = function (comment) {
        $scope.selectedComment = comment;
        $('#flagModal').modal('show');
    };


    $scope.getAttachmentUrl = function (attachmentInfo) {
        return attachmentService.getAttachmentUrl($scope.selectedContext._id, $scope.selectedComment, attachmentInfo);
    };

    $scope.deleteAttachment = function (attachmentInfo) {
        attachmentService.deleteAttachment($scope.selectedContext._id, $scope.selectedComment, attachmentInfo).then(
            function () {
                var attachmentIdx = -1;
                for (var i = 0; i < $scope.selectedComment.attachments.length; i++) {
                    if (attachmentInfo.fileId == $scope.selectedComment.attachments[i].fileId) {
                        attachmentIdx = i;
                    }
                }

                $scope.selectedComment.attachments.splice(attachmentIdx, 1);

                $('#attachmentsModal').modal('hide');

                var fileName = attachmentInfo.fileName.substring(attachmentInfo.fileName.lastIndexOf('/') + 1);

                showGrowlMessage('success', 'Attachment \'' + fileName + '\' deleted');
            }
        );
    };

    $scope.resetStatusAndGetComments();
});

app.controller('ContextsController', function ($scope, contexts, contextService) {
    $scope.contexts = contexts;
    $scope.contextName = '';

    $scope.createContext = function () {
        contextService.createContext($scope.contextName).then(function (context) {
            contexts.push(context);

            showGrowlMessage('success', 'Context \'' + context._id + '\' created');
        });
    }
});

app.controller('SecurityActionsController', function ($scope, actionsService, contexts) {
    $scope.contexts = contexts;
    $scope.selectedContext = $scope.contexts[0];
    $scope.actionsOrderedBy = '+actionName';

    $scope.getActions = function () {
        actionsService.getActions($scope.selectedContext._id).then(function (actions) {
            $scope.actions = actions;
        });
    };

    $scope.updateAction = function (action) {
        actionsService.updateAction($scope.selectedContext._id, action.actionName, action.roles);
    };

    $scope.getActions();
});

app.controller('EmailPreferencesController', function ($scope, emailPreferencesService, contexts, emailTypes) {
    $scope.contexts = contexts;
    $scope.emailTypes = emailTypes;
    $scope.selectedType = emailTypes[0];
    $scope.selectedContext = contexts[0];
    $scope.editorOptions=configRTEEditor();
    $scope.saveTemplate = function () {
        emailPreferencesService.saveNotificationTemplate($scope.selectedType.type, $scope.selectedContext._id,
            $scope.emailTemplate).then(function (result) {
                if(result){
                    showGrowlMessage('success', 'Template saved');
                }else{
                    showGrowlMessage('danger', 'Unable to save template');
                }
            });
    };
    $scope.resetContextPreferences = function(){
        emailPreferencesService.getEmailConfig($scope.selectedContext._id).then(function(config){
            $scope.emailConfig=config;
        });
        $scope.reloadEmailTemplate();
    };
    $scope.reloadEmailTemplate = function (oldType) {

        emailPreferencesService.getNotificationTemplate($scope.selectedType.type, $scope.selectedContext._id)
            .then(function (template) {
                $scope.emailTemplate = template.template;
            });
    };
    $scope.emailTemplate = $scope.reloadEmailTemplate($scope.selectedContext);
    emailPreferencesService.getEmailConfig($scope.selectedContext._id).then(function(config){
        $scope.emailConfig=config;
    });
    $scope.$watch('selectedType', function (newValue, oldValue) {
        if(newValue!==oldValue) {
            $scope.reloadEmailTemplate(oldValue);
        }
    });



    $scope.saveEmailConfig = function(){
        emailPreferencesService.saveEmailConfig($scope.selectedContext._id,$scope.emailConfig).then(function (result) {
            if(result){
                showGrowlMessage('success', 'Configuration Saved');
            }else{
                showGrowlMessage('danger', 'Unable to save email config');
            }
        });
    }


    $scope.$on("ckeditor.ready", function( event ) {
        var editor = CKEDITOR.instances.editor1;
        var writer = editor.dataProcessor.writer;
            writer.indentationChars = '';
            writer.lineBreakChars = '';
    });

});

app.controller('TenantPreferencesController',function($scope,tenantPreferenceService,contexts){
    $scope.contexts = contexts;
    $scope.selectedContext = contexts[0];
    tenantPreferenceService.getPreferences($scope.selectedContext._id).then(function(result){
       $scope.preferences=result.preferences;
    });

    $scope.resetContextPreferences = function(){
        tenantPreferenceService.getPreferences($scope.selectedContext._id).then(function(result){
            $scope.preferences=result.preferences;
        });
    };
    $scope.predefineKeys=[{key:'accessControlAllowHeaders',label:'Cors Allow Headers'},
        {key:'accessControlAllowMethods',label:'Cors Allow Methods'},
        {key:'accessControlAllowOrigin',label:'Cors Allow Origin'}];

    $scope.addPreferenceField = function() {
        $scope.preferences[$scope.newPkey]=$scope.newPval;
        $scope.newPval="";
        $scope.newPkey="";
        $('#addNewModal').modal('hide');
    };
    $scope.savePreferences = function() {
        if($.isEmptyObject($scope.preferences)){
            showGrowlMessage('info', 'Cannot save empty properites');
        }else {
            tenantPreferenceService.updatePreferences($scope.selectedContext._id, $scope.preferences).then(function (result) {
                if (result) {
                    showGrowlMessage('success', 'Configuration Saved');
                } else {
                    showGrowlMessage('danger', 'Unable to save email config');
                }
            });
        }
    };

    $scope.removeKey = function(keyToDelete){
        tenantPreferenceService.deletePreference($scope.selectedContext._id,keyToDelete).then(function(result){
            if(result){
                showGrowlMessage('success', 'Property Deletion successfully');
                delete $scope.preferences[keyToDelete];
            } else{
                showGrowlMessage('danger', 'Unable to delete Property');
            }
        });
    };
    $scope.showNewPreferenceModal = function () {
        $('#addNewModal').modal('show');
    };
});

app.controller('SearchProfilesController', function ($scope, tenantNames, profileService) {
    $scope.tenantNames = tenantNames;
    $scope.selectedTenantName = $scope.tenantNames[0];
    $scope.searchText = '';
    $scope.itemsPerPage = 10;

    $scope.isValidUsername = function (text) {
        return /^\w+$/.test(text);
    };

    $scope.getCurrentPage = function () {
        var count = $scope.itemsPerPage;
        var start = ($scope.currentPage - 1) * count;

        profileService.findProfiles($scope.selectedTenantName, $scope.searchText, start, count).then(
            function (profiles) {
                $scope.profiles = profiles;
            }
        );
    };

    $scope.doSearch = function () {
        if ($scope.isValidUsername($scope.searchText)) {
            profileService.getCountByQuery($scope.selectedTenantName, $scope.searchText).then(function (count) {
                $scope.totalItems = count;
                $scope.currentPage = 1;
                $scope.getCurrentPage();
            });
        } else {
            showGrowlMessage('info', 'Search term must be a word with no spaces');
        }
    };

    $scope.getSocialContextNames = function (profile) {
        var names = [];

        if (profile.attributes.socialContexts) {
            for (var i = 0; i < profile.attributes.socialContexts.length; i++) {
                names.push(profile.attributes.socialContexts[i].name);
            }
        }

        return names;
    };
});

app.controller('ProfileController', function ($scope, profile, contexts, contextService) {
    $scope.profile = profile;
    $scope.contexts = contexts;
    $scope.selectedContext = $scope.contexts[0];
    $scope.contextRoles = [];
    $scope.addProfileToContext = function () {
        contextService.addProfileToContext($scope.selectedContext._id, $scope.profile.id, $scope.contextRoles).then(
            function (profile) {
                $scope.profile = profile;
                showGrowlMessage('success', 'Profile added to context \'' + $scope.selectedContext._id + '\'');
            }
        )
    };

    $scope.removeProfileFromContext = function (ctxId) {
        contextService.removeProfileFromContext(ctxId, $scope.profile.id).then(function (profile) {
            $scope.profile = profile;

            showGrowlMessage('success', 'Profile removed from context \'' + ctxId + '\'');
        });
    };
});
