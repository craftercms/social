/**
 * Angular Module
 */
var app = angular.module('CrafterAdminConsole', ['ngRoute', 'ui.bootstrap']);

/**
 * Global variables
 */
var defaultContext = 'f5b143c2-f1c0-4a10-b56e-f485f00d3fe9';
var commentsSortBy = 'lastModifiedDate';
var commentsSortOrder = 'ASC';

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

var moderationStatusActions = {
    'UNMODERATED': [
        {
            label: 'Approve',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'APPROVED', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Mark as Spam',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'SPAM', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment).then(function(comment) {
                    commentBodyUpdatedCallback(comment);
                });
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                resetBody(comment);
            }
        }
    ],
    'APPROVED': [
        {
            label: 'Mark as Spam',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'SPAM', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'UNMODERATED', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments).then(function(comment) {
                        commentBodyUpdatedCallback(comment);
                    });
                });
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment).then(function(comment) {
                    commentBodyUpdatedCallback(comment);
                });
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                resetBody(comment);
            }
        }
    ],
    'PENDING': [
        {
            label: 'Approve',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'APPROVED', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment).then(function(comment) {
                    commentBodyUpdatedCallback(comment);
                });
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                resetBody(comment);
            }
        }
    ],
    'SPAM': [
        {
            label: 'Permanently delete',
            action: function(context, commentService, comment, comments) {
                commentService.deleteComment(context, comment, comments).then(function(comment) {
                    commentDeletedCallback(comment);
                });
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'UNMODERATED', comments).then(function(comment) {
                    commentStatusUpdatedCallback(comment, comments);
                });
            }
        }
    ],
    'TRASH': [
        {
            label: 'Permanently delete',
            action: function(context, commentService, comment, comments) {
                commentService.deleteComment(context, comment, comments).then(function(comment) {
                    commentDeletedCallback(comment);
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
app.filter('truncateIfTooLarge', function() {
    return function(input) {
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
    return $http.get(url).then(function(result){
        return result.data;
    });
}

function postParams(url, params, $http) {
    return $http.post(url, $.param(params), { headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).then(
        function(result){
            return result.data;
        }
    );
}

function putParams(url, params, $http) {
    return $http.put(url, $.param(params), { headers: {'Content-Type': 'application/x-www-form-urlencoded'}}).then(
        function(result){
            return result.data;
        }
    );
}

function deleteObject(url, $http) {
    return $http.delete(url).then(function(result){
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

function commentStatusUpdatedCallback(comment, comments) {
    var idx = findComment(comments, comment._id);

    comments.splice(idx, 1);

    showGrowlMessage('info', 'Comment \'' + comment._id + '\' moderated to \'' + comment.moderationStatus + '\'');
}

function commentBodyUpdatedCallback(comment) {
    comment.bodyOrig = comment.body;

    showGrowlMessage('info', 'Comment \'' + comment._id + '\' updated');
}

function commentDeletedCallback(comment, comments) {
    var idx = findComment(comments, comment._id);

    comments.splice(idx, 1);

    showGrowlMessage('info', 'Comment \'' + comment._id + '\' deleted');
}

/**
 * Http Interceptors
 */
app.factory('httpErrorHandler', function ($q) {
    return {
        'responseError': function(rejection) {
            var message;

            if (rejection.status == 0) {
                message = 'Unable to communicate with the server. Please try again later or contact IT support';
            } else {
                message = 'Server responded with ' + rejection.status + ' error';
                if (rejection.data.message) {
                    message += ': <strong>' + rejection.data.message + '</strong>';
                }

                message += '. Please contact IT support for more information';
            }

            showGrowlMessage('danger', message);

            return $q.reject(rejection);
        }
    };
});

app.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('httpErrorHandler');
}]);

/**
 * Services
 */
app.factory('contextService', function($http) {
    return {
        getContexts: function() {
            var url = socialRestBaseUrl + '/system/context/all?tenant=' + defaultContext;

            return getObject(url, $http);
        },
        createContext: function(name) {
            var url = socialRestBaseUrl + '/system/context?tenant=' + defaultContext;

            return postParams(url, { contextName: name }, $http);
        },
        addProfileToContext: function(ctxId, profileId, roles) {
            var url = socialRestBaseUrl + '/system/context/' + ctxId + '/' + profileId + '?tenant=' + defaultContext;

            return postParams(url, { roles: roles.join() }, $http);
        },
        removeProfileFromContext: function(ctxId, profileId) {
            var url = socialRestBaseUrl + '/system/context/' + ctxId + '/' + profileId + '?tenant=' + defaultContext;

            return deleteObject(url, $http);
        }
    }
});

app.factory('commentService', function($http) {
    return {
        getCommentsCount: function(context, status) {
            var url = socialRestBaseUrl + '/comments/moderation/' + status + '/count?tenant=' + context;

            return getObject(url, $http);
        },
        getComments: function(context, status, pageNumber, pageSize) {
            var url = socialRestBaseUrl + '/comments/moderation/' + status + '?tenant=' + context;
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
        updateStatus: function(context, comment, newStatus, comments) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '/moderate?tenant=' + context;

            return putParams(url, { status: newStatus }, $http);
        },
        updateBody: function(context, comment) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '?tenant=' + context;

            return putParams(url, { body: comment.body }, $http);
        },
        deleteComment: function(context, comment, comments) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '?tenant=' + context;

            return deleteObject(url, $http);
        }
    }
});

app.factory('actionsService', function($http) {
    return {
        getActions: function(context) {
            var url = socialRestBaseUrl + '/system/actions?tenant=' + context;

            return getObject(url, $http);
        },
        updateAction: function(context, actionName, roles) {
            var url = socialRestBaseUrl + '/system/actions?tenant=' + context;

            putParams(url, { actionName: actionName, roles: roles.join() }, $http).then(function() {
                showGrowlMessage('info', 'Action \'' + actionName + '\' updated');
            });
        }
    }
});

app.factory('tenantService', function($http) {
    return {
        getTenants: function() {
            var url = profileRestBaseUrl + '/tenant/all?accessTokenId=' + profileAccessToken;

            return getObject(url, $http);
        }
    }
});

app.factory('profileService', function($http) {
    return {
        findProfilesByUsername: function(tenantName, username, start, count) {
            var url = profileRestBaseUrl + '/profile/by_query?accessTokenId=' + profileAccessToken;
            url += '&tenantName=' + tenantName;
            url += '&query=' + encodeURIComponent('{username: {$regex: ".*' + username + '.*", $options: "i"}}');

            if (start != undefined && start != null) {
                url += '&start=' + start;
            }
            if (count != undefined && count != null) {
                url += '&count=' + count;
            }

            return getObject(url, $http);
        },
        getProfile: function(id) {
            var url = profileRestBaseUrl + '/profile/' + id + '?accessTokenId=' + profileAccessToken;

            return getObject(url, $http);
        }
    }
});

/**
 * Routing
 */
app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            contexts: function(contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/moderation-dashboard', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            contexts: function(contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/contexts', {
        controller: 'ContextsController',
        templateUrl: contextPath + '/contexts',
        resolve: {
            contexts: function(contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/security-actions', {
        controller: 'SecurityActionsController',
        templateUrl: contextPath + '/security-actions',
        resolve: {
            contexts: function(contextService) {
                return contextService.getContexts();
            }
        }
    });

    $routeProvider.when('/search-profiles', {
        controller: 'SearchProfilesController',
        templateUrl: contextPath + '/search-profiles',
        resolve: {
            tenants: function(tenantService) {
                return tenantService.getTenants();
            }
        }
    });

    $routeProvider.when('/profile/:id', {
        controller: 'ProfileController',
        templateUrl: contextPath + '/profile',
        resolve: {
            profile: function($route, profileService) {
                return profileService.getProfile($route.current.params.id);
            },
            contexts: function(contextService) {
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
app.controller('ModerationDashboardController', function($scope, commentService, contexts) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;
    $scope.commentService = commentService;
    $scope.contexts = contexts;
    $scope.selectedContext = $scope.contexts[0];
    $scope.itemsPerPage = 5;

    $scope.getComments = function() {
        commentService.getComments($scope.selectedContext._id, $scope.selectedStatus, $scope.currentPage,
            $scope.itemsPerPage).then(function(comments) {
                $scope.comments = comments;
            });
    };

    $scope.resetStatus = function() {
        for (var i = 0; i < moderationStatus.length; i++) {
            if (moderationStatus[i].default) {
                $scope.selectedStatus = moderationStatus[i].value;

                break;
            }
        }
    };

    $scope.resetCommentList = function() {
        commentService.getCommentsCount($scope.selectedContext._id, $scope.selectedStatus).then(function(count) {
            $scope.totalItems = count;
            $scope.currentPage = 1;

            $scope.getComments();
        });
    };

    $scope.resetStatusAndCommentList = function() {
        $scope.resetStatus();
        $scope.resetCommentList($scope.selectedContext._id, $scope.selectedStatus);
    };

    $scope.resetStatusAndCommentList();
});

app.controller('ContextsController', function($scope, contexts, contextService) {
    $scope.contexts = contexts;
    $scope.contextName = '';

    $scope.createContext = function() {
        contextService.createContext($scope.contextName).then(function(context) {
            contexts.push(context);

            showGrowlMessage('info', 'Context \'' + context._id + '\' created');
        });
    }
});

app.controller('SecurityActionsController', function($scope, actionsService, contexts) {
    $scope.contexts = contexts;
    $scope.selectedContext = $scope.contexts[0];
    $scope.actionsOrderedBy = '+actionName';

    $scope.getActions = function() {
        actionsService.getActions($scope.selectedContext._id).then(function(actions) {
            $scope.actions = actions;
        });
    };

    $scope.updateAction = function(action) {
        actionsService.updateAction($scope.selectedContext._id, action.actionName, action.roles);
    };

    $scope.getActions();
});

app.controller('SearchProfilesController', function($scope, tenants, paginationConfig, profileService) {
    $scope.tenants = tenants;
    $scope.selectedTenantName = $scope.tenants[0].name;
    $scope.searchText = '';

    $scope.isValidUsername = function(text) {
        return /^\w+$/.test(text);
    };

    $scope.findProfilesByUsername = function() {
        if ($scope.isValidUsername($scope.searchText)) {
            profileService.findProfilesByUsername($scope.selectedTenantName, $scope.searchText, 0,
                paginationConfig.itemsPerPage).then(function(profiles) {
                $scope.profiles = profiles;
            });
        } else {
            showGrowlMessage('info', 'Search term must be a word with no spaces');
        }
    };

    $scope.getSocialContextNames = function(profile) {
        var names = [];

        if (profile.attributes.socialTenants) {
            for (var i = 0; i < profile.attributes.socialTenants.length; i++) {
                names.push(profile.attributes.socialTenants[i].tenant);
            }
        }

        return names;
    };
});

app.controller('ProfileController', function($scope, profile, contexts, contextService) {
    $scope.profile = profile;

    $scope.getNonAssociatedContexts = function() {
        var nonAssociatedContexts = [];

        for (var i = 0; i < contexts.length; i++) {
            var contextAlreadyAssociated = false;

            if ($scope.profile .attributes.socialTenants) {
                for (var j = 0; j < $scope.profile .attributes.socialTenants.length; j++) {
                    if (contexts[i]._id == $scope.profile.attributes.socialTenants[j].id) {
                        contextAlreadyAssociated = true;
                        break;
                    }
                }
            }

            if (!contextAlreadyAssociated) {
                nonAssociatedContexts.push(contexts[i]);
            }
        }

        return nonAssociatedContexts;
    };

    $scope.contexts = $scope.getNonAssociatedContexts();
    $scope.selectedContext = $scope.contexts.length > 0 ? $scope.contexts[0] : null;
    $scope.contextRoles = [];

    $scope.addProfileToContext = function() {
        contextService.addProfileToContext($scope.selectedContext._id, $scope.profile.id, $scope.contextRoles).then(
            function(profile) {
                $scope.profile = profile;
                $scope.contexts = $scope.getNonAssociatedContexts();
                $scope.selectedContext = $scope.contexts.length > 0 ? $scope.contexts[0] : null;

                showGrowlMessage('info', 'Profile added to context \'' + $scope.selectedContext._id + '\'');
            }
        )
    };

    $scope.removeProfileFromContext = function(ctxId) {
        contextService.removeProfileFromContext(ctxId, $scope.profile.id).then(function(profile) {
            $scope.profile = profile;
            $scope.contexts = $scope.getNonAssociatedContexts();
            $scope.selectedContext = $scope.contexts.length > 0 ? $scope.contexts[0] : null;

            showGrowlMessage('info', 'Profile removed from context \'' + ctxId + '\'');
        });
    }
});


