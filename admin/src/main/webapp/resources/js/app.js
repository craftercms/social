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
                commentService.updateStatus(context, comment, 'APPROVED', comments);
            }
        },
        {
            label: 'Mark as Spam',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'SPAM', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                commentService.resetBody(comment);
            }
        }
    ],
    'APPROVED': [
        {
            label: 'Mark as Spam',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'SPAM', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                commentService.resetBody(comment);
            }
        }
    ],
    'PENDING': [
        {
            label: 'Approve',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'APPROVED', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentService, comment) {
                commentService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentService, comment) {
                commentService.resetBody(comment);
            }
        }
    ],
    'SPAM': [
        {
            label: 'Permanently delete',
            action: function(context, commentService, comment, comments) {
                commentService.deleteComment(context, comment, comments);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        }
    ],
    'TRASH': [
        {
            label: 'Permanently delete',
            action: function(context, commentService, comment, comments) {
                commentService.deleteComment(context, comment, comments);
            }
        }/*,
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentService, comment, comments) {
                commentService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        }*/
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
app.factory('socialContextServices', function($http) {
    return {
        getContexts: function() {
            var url = socialRestBaseUrl + '/system/context/all?tenant=' + defaultContext;

            return getObject(url, $http);
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

            putParams(url, { status: newStatus }, $http).then(function() {
                var idx = findComment(comments, comment._id);

                comments.splice(idx, 1);

                showGrowlMessage('info', 'Status of comment \'' + comment._id + '\' changed to \'' + newStatus + '\'');
            });
        },
        updateBody: function(context, comment) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '?tenant=' + context;

            putParams(url, { body: comment.body }, $http).then(function() {
                comment.bodyOrig = comment.body;

                showGrowlMessage('info', 'Comment \'' + comment._id + '\' updated');
            });
        },
        resetBody: function(comment) {
            comment.body = comment.bodyOrig;
        },
        deleteComment: function(context, comment, comments) {
            var url = socialRestBaseUrl + '/comments/' + comment._id + '?tenant=' + context;

            deleteObject(url, $http).then(function() {
                var idx = findComment(comments, comment._id);

                comments.splice(idx, 1);

                showGrowlMessage('info', 'Comment \'' + comment._id + '\' deleted');
            });
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
            socialContexts: function(socialContextServices) {
                return socialContextServices.getContexts();
            }
        }
    });

    $routeProvider.when('/moderation-dashboard', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            socialContexts: function(socialContextServices) {
                return socialContextServices.getContexts();
            }
        }
    });

    $routeProvider.when('/security-actions', {
        controller: 'SecurityActionsController',
        templateUrl: contextPath + '/security-actions',
        resolve: {
            socialContexts: function(socialContextServices) {
                return socialContextServices.getContexts();
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
app.controller('ModerationDashboardController', function($scope, commentService, socialContexts) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;
    $scope.commentService = commentService;
    $scope.socialContexts = socialContexts;
    $scope.selectedContext = socialContexts[0]._id;
    $scope.itemsPerPage = 5;

    $scope.getComments = function() {
        commentService.getComments($scope.selectedContext, $scope.selectedStatus, $scope.currentPage,
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
        commentService.getCommentsCount($scope.selectedContext, $scope.selectedStatus).then(function(count) {
            $scope.totalItems = count;
            $scope.currentPage = 1;

            $scope.getComments();
        });
    };

    $scope.resetStatusAndCommentList = function() {
        $scope.resetStatus();
        $scope.resetCommentList($scope.selectedContext, $scope.selectedStatus);
    };

    $scope.resetStatusAndCommentList();
});

app.controller('SecurityActionsController', function($scope, actionsService, socialContexts) {
    $scope.socialContexts = socialContexts;
    $scope.selectedContext = socialContexts[0]._id;
    $scope.actionsOrderedBy = '+actionName';

    $scope.getActions = function() {
        actionsService.getActions($scope.selectedContext).then(function(actions) {
            $scope.actions = actions;
        });
    };

    $scope.updateAction = function(action) {
        actionsService.updateAction($scope.selectedContext, action.actionName, action.roles);
    };

    $scope.getActions();
});

app.controller('SearchProfilesController', function($scope, tenants, paginationConfig, profileService) {
    $scope.tenants = tenants;
    $scope.selectedTenantName = tenants[0].name;
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

app.controller('ProfileController', function($scope, profile) {
    $scope.profile = profile;
});


