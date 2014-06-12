'use strict';

/* constants */
angular.module('moderationDashboard.constants', []).
    constant('CONFIG', {
        PROPERTIES_PATH: "/crafter-comments-admin/static-assets/csadmin/properties/",
        API_PATH: "/crafter-comments/api/2/ugc/",
        TEMPLATES_PATH: "/crafter-comments-admin/static-assets/csadmin/templates/",
        IMAGES_PATH: "/crafter-comments-admin/static-assets/csadmin/img/"
    }).

    constant('ERROR', {
        '401': 'Your session has expired. Please log in and try this operation again.',
        '403': 'Sorry! You do not have permission to perform this operation',
        'ALL': 'An unidentified error has occured. If the problem persists, please contact your system administrator'
    }).

    constant('ACTIONS', {
        DELETE: 'delete',
        EDIT: 'edit',
        UPDATE: 'update_status'
    });