/**
 * Angular Module
 */
var app = angular.module('CrafterAdminConsole', ['ngRoute', 'ui.bootstrap']);

/**
 * Global variables
 */
var moderationStatus = [
    {
        label: 'Unmoderated',
        value: 'Unmoderated',
        default: true
    },
    {
        label: 'Approved',
        value: 'Approved'
    },
    {
        label: 'Pending',
        value: 'Pending'
    },
    {
        label: 'Spam',
        value: 'Spam'
    },
    {
        label: 'Trash',
        value: 'Trash'
    }
];

var moderationStatusActions = {
    Unmoderated: [
        {
            label: 'Approve',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Approved');
            }
        },
        {
            label: 'Mark as Spam',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Spam');
            }
        },
        {
            label: 'Mark as Trash',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Trash');
            }
        },
        {
            label: 'Save Changes',
            action: function(comment, commentsService) {
                commentsService.updateBody(comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(comment, commentsService) {
                commentsService.resetBody(comment);
            }
        }
    ],
    Approved: [
        {
            label: 'Mark as Spam',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Spam');
            }
        },
        {
            label: 'Mark as Trash',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Trash');
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Unmoderated');
            }
        },
        {
            label: 'Save Changes',
            action: function(comment, commentsService) {
                commentsService.updateBody(comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(comment, commentsService) {
                commentsService.resetBody(comment);
            }
        }
    ],
    Pending: [
        {
            label: 'Approve',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Approved');
            }
        },
        {
            label: 'Mark as Trash',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Trash');
            }
        },
        {
            label: 'Save Changes',
            action: function(comment, commentsService) {
                commentsService.updateBody(comment);
            }
        }
        ,
        {
            label: 'Reset',
            action: function(comment, commentsService) {
                commentsService.resetBody(comment);
            }
        }
    ],
    Spam: [
        {
            label: 'Permanently delete',
            action: function(comment, commentsService) {
                commentsService.deleteComment(comment);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Unmoderated');
            }
        }
    ],
    Trash: [
        {
            label: 'Permanently delete',
            action: function(comment, commentsService) {
                commentsService.deleteComment(comment);
            }
        },
        {
            label: 'Mark as Unmoderated',
            action: function(comment, commentsService) {
                commentsService.updateStatus(comment, 'Unmoderated');
            }
        }
    ]
};

var paginationConfig = {
    size: 5,
    itemsPerPage: 10
};

var mockComments = {
    Unmoderated : [
        {
            id: '182930461947591',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '375827582749010',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #2',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'cortiz'
                }
            }
        },
        {
            id: '182930461947501',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        }
        ,
        {
            id: '182930461947502',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        }
        ,
        {
            id: '182930461947503',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        }
        ,
        {
            id: '182930461947504',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '182930461947505',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '182930461947506',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '182930461947507',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '182930461947508',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        },
        {
            id: '182930461947509',
            moderationStatus: 'Unmoderated',
            createdDate: new Date(),
            lastModifiedDate: new Date(),
            subject: 'Comment #1',
            body: 'This is a test comment',
            bodyOrig: 'This is a test comment',
            profile: {
                attributes: {
                    displayName: 'avasquez'
                }
            }
        }
    ],
    Approved: [],
    Pending: [],
    Spam: [],
    Trash: []
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
function getObject(url, $http) {
    return $http.get(contextPath + url).then(function(result){
        return result.data;
    });
}

function postObject(url, obj, $http) {
    return $http.post(contextPath + url, obj).then(function(result){
        return result.data;
    });
}

function deleteObject(url, $http) {
    return $http.delete(contextPath + url).then(function(result){
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
app.factory('commentsService', function($http) {
    return {
        getCommentsCount: function(status) {
            return mockComments[status].length;
        },
        getComments: function(status, start, count) {
            return mockComments[status].slice(start, start + count);
        },
        updateStatus: function(comment, newStatus) {
            var commentIdx = -1;

            for (var i = 0; i < mockComments[comment.moderationStatus].length; i++) {
                if (mockComments[comment.moderationStatus][i].id == comment.id) {
                    commentIdx = i;
                    break;
                }
            }

            if (commentIdx >= 0) {
                mockComments[comment.moderationStatus].splice(commentIdx, 1);
            }

            comment.moderationStatus = newStatus;

            mockComments[newStatus].push(comment);

            showGrowlMessage('info', 'Status of comment \'' + comment.id + '\' changed to \'' + newStatus + '\'');
        },
        updateBody: function(comment) {
            comment.bodyOrig = comment.body;

            showGrowlMessage('info', 'Comment \'' + comment.id + '\' updated');
        },
        resetBody: function(comment) {
            comment.body = comment.bodyOrig;
        },
        deleteComment: function(comment) {
            var commentIdx = -1;

            for (var i = 0; i < mockComments[comment.moderationStatus].length; i++) {
                if (mockComments[comment.moderationStatus][i].id == comment.id) {
                    commentIdx = i;
                    break;
                }
            }

            if (commentIdx >= 0) {
                mockComments[comment.moderationStatus].splice(commentIdx, 1);
            }

            showGrowlMessage('info', 'Comment \'' + comment.id + '\' deleted');
        }
    }
});


/**
 * Routing
 */
app.config(function($routeProvider) {
    $routeProvider.when('/', {
        controller: 'CommentListController',
        templateUrl: contextPath + '/comments-list',
        resolve: {
        }
    });

    $routeProvider.when('/comment/list', {
        controller: 'CommentListController',
        templateUrl: contextPath + '/comments-list',
        resolve: {
        }
    });

    $routeProvider.otherwise({
        redirectTo: '/'
    });
});

/**
 * Controllers
 */
app.controller('CommentListController', function($scope, $location, paginationConfig, commentsService) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;
    $scope.commentsService = commentsService;

    for (var i = 0; i < moderationStatus.length; i++) {
        if (moderationStatus[i].default) {
            $scope.selectedStatus = moderationStatus[i].value;

            break;
        }
    }

    $scope.getComments = function(status, start, count) {
        $scope.comments = commentsService.getComments(status, start, count);
    };

    $scope.pageChanged = function() {
        var start = ($scope.currentPage - 1) * paginationConfig.itemsPerPage;
        var count = paginationConfig.itemsPerPage;

        $scope.getComments($scope.selectedStatus, start, count);
    };

    $scope.resetCommentList = function(status) {
        $scope.totalItems = commentsService.getCommentsCount(status);
        $scope.currentPage = 1;

        $scope.pageChanged();
    };

    $scope.resetCommentList($scope.selectedStatus);
});
