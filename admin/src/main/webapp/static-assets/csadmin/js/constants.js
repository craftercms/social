'use strict';

/* constants */
angular.module('moderationDashboard.constants', []).
    constant('CONFIG', {
        PROPERTIES_PATH: "/crafter-social-admin/static-assets/csadmin/properties/",
        API_PATH: "/crafter-social/api/2/ugc/",
        TEMPLATES_PATH: "/crafter-social-admin/static-assets/csadmin/templates/",
        IMAGES_PATH: "/crafter-social-admin/static-assets/csadmin/img/"
    }).

    constant('ACTIONS', {
        DELETE: 'delete',
        EDIT: 'edit',
        UPDATE: 'update_status'
    });