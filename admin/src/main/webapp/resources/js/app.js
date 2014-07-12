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
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'APPROVED', comments);
            }
        },
        {
            label: 'Mark as Spam',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'SPAM', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentsService, comment) {
                commentsService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentsService, comment) {
                commentsService.resetBody(comment);
            }
        }
    ],
    'APPROVED': [
        {
            label: 'Mark as Spam',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'SPAM', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentsService, comment) {
                commentsService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentsService, comment) {
                commentsService.resetBody(comment);
            }
        }
    ],
    'PENDING': [
        {
            label: 'Approve',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'APPROVED', comments);
            }
        },
        {
            label: 'Mark as Trash',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'TRASH', comments);
            }
        },
        {
            label: 'Save Changes',
            action: function(context, commentsService, comment) {
                commentsService.updateBody(context, comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(context, commentsService, comment) {
                commentsService.resetBody(comment);
            }
        }
    ],
    'SPAM': [
        {
            label: 'Permanently delete',
            action: function(context, commentsService, comment, comments) {
                commentsService.deleteComment(context, comment, comments);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        }
    ],
    'TRASH': [
        {
            label: 'Permanently delete',
            action: function(context, commentsService, comment, comments) {
                commentsService.deleteComment(context, comment, comments);
            }
        }/*,
        {
            label: 'Mark as Unmoderated',
            action: function(context, commentsService, comment, comments) {
                commentsService.updateStatus(context, comment, 'UNMODERATED', comments);
            }
        }*/
    ]
};

/**
 * Constants
 */
app.constant('paginationConfig', {
    itemsPerPage: 5,
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
        getAllContexts: function() {
            var url = socialRestBaseUrl + '/system/context/all?tenant=' + defaultContext;

            return getObject(url, $http);
        }
    }
});

app.factory('commentsService', function($http) {
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

/**
 * Routing
 */
app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            socialContexts: function(socialContextServices) {
                return socialContextServices.getAllContexts();
            }
        }
    });

    $routeProvider.when('/moderation-dashboard', {
        controller: 'ModerationDashboardController',
        templateUrl: contextPath + '/moderation-dashboard',
        resolve: {
            socialContexts: function(socialContextServices) {
                return socialContextServices.getAllContexts();
            }
        }
    });

    $routeProvider.when('/security-actions', {
        controller: 'SecurityActionsController',
        templateUrl: contextPath + '/security-actions',
        resolve: {
            socialContexts: function(socialContextServices) {
                return socialContextServices.getAllContexts();
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
app.controller('ModerationDashboardController', function($scope, paginationConfig, commentsService, socialContexts) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;
    $scope.commentsService = commentsService;
    $scope.socialContexts = socialContexts;
    $scope.selectedContext = socialContexts[0]._id;

    $scope.getComments = function() {
        commentsService.getComments($scope.selectedContext, $scope.selectedStatus, $scope.currentPage,
            paginationConfig.itemsPerPage).then(function(comments) {
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
        commentsService.getCommentsCount($scope.selectedContext, $scope.selectedStatus).then(function(count) {
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
