/**
 * Angular Module
 */
var app = angular.module('CrafterAdminConsole', ['ngRoute']);

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
            label: 'Approve'
        },
        {
            label: 'Mark as Spam'
        },
        {
            label: 'Mark as Trash'
        },
        {
            label: 'Edit'
        }
    ],
    Approved: [
        {
            label: 'Mark as Spam'
        },
        {
            label: 'Mark as Trash'
        },
        {
            label: 'Mark as Unmoderated'
        },
        {
            label: 'Edit'
        }
    ],
    Pending: [
        {
            label: 'Approve'
        },
        {
            label: 'Mark as Trash'
        },
        {
            label: 'Edit'
        }
    ],
    Spam: [
        {
            label: 'Permanently delete'
        },
        {
            label: 'Mark as Unmoderated'
        }
    ],
    Trash: [
        {
            label: 'Permanently delete'
        },
        {
            label: 'Mark as Unmoderated'
        }
    ]
};

var paginationConfig = {
    size: 5,
    itemsPerPage: 10
};

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

            $.growl(message, {
                type: 'danger',
                pause_on_mouseover: true,
                position: {
                    from: 'top',
                    align: 'center'
                },
                offset: 40
            });

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
        getComments: function(status) {
            if (status == 'Unmoderated') {
                return [
                    {
                        id: '182930461947591',
                        moderationStatus: 'Unmoderated',
                        createdDate: new Date(),
                        lastModifiedDate: new Date(),
                        subject: 'Comment #1',
                        body: 'This is a test comment',
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
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
                        profile: {
                            attributes: {
                                displayName: 'cortiz'
                            }
                        }
                    }
                ];
            }
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
app.controller('CommentListController', function($scope, $location, commentsService) {
    $scope.moderationStatus = moderationStatus;
    $scope.moderationStatusActions = moderationStatusActions;

    for (var i = 0; i < moderationStatus.length; i++) {
        if (moderationStatus[i].default) {
            $scope.selectedStatus = moderationStatus[i].value;

            break;
        }
    }

    $scope.getComments = function(status) {
        $scope.comments = commentsService.getComments(status);
    };

    $scope.getComments($scope.selectedStatus);
});

/**
 * Directives
 */
app.directive('checkboxList', function() {
    return {
        restrict: 'E',
        scope: {
            name: '@',
            selected: '=',
            options: '='
        },
        controller: function($scope) {
            $scope.toggleOption = function(option) {
                var index = $scope.selected.indexOf(option);
                if (index > -1) {
                    $scope.selected.splice(index, 1);
                } else {
                    $scope.selected.push(option);
                }
            };
        },
        templateUrl: contextPath + '/directives/checkbox-list',
        replace: true
    };
});

app.directive('editableList', function() {
    return {
        restrict: 'E',
        scope: {
            name: '@',
            items: '='
        },
        controller: function($scope) {
            if ($scope.items === undefined || $scope.items === null) {
                $scope.items = [];
            }

            $scope.addItem = function(item) {
                $scope.items.push(item);
            };

            $scope.deleteItemAt = function(index) {
                $scope.items.splice(index, 1);
            };
        },
        templateUrl: contextPath + '/directives/editable-list',
        replace: true
    };
});