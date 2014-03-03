(function (S) {
    'use strict';

    var $       = S.$;
    var C       = S.Constants;
    var Profile = S.Backbone.Model.extend({

        idAttribute: 'id',
        defaults: {
            'id': '',
            'userName': 'anonymous',
            'password': '',
            'active': false,
            'created': Date.now(),
            'modified': Date.now(),
            'tenantName': '',
            'email': '',
            'roles': []
        },

        url: function () {
            if (this.isNew()) {
                // return S.url('ugc');
            } else {
                // return S.url('ugc');
            }
        },

        authenticate: function (  ) {
            this.fetch({
                type: 'POST',
                url: S.Cfg('url.security'),
                data: $.param(this.toJSON()),
                success: function ( data ) {
                    S.getDirector().trigger(C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), data);
                },
                error: function () {
                    S.getDirector().trigger(C.get('EVENT_USER_AUTHENTICATION_FAILED'));
                }
            });
        },

        hasRole: function ( role ) {
            var found = false;
            this.get('roles').every(function ( pRole ) {
                ( pRole === role ) && (found = true);
                return !found;
            });
            return found;
        }

    });

    S.define('model.Profile', Profile);

})(crafter.social);