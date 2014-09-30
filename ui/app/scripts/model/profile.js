(function (S) {
    'use strict';

    var $ = S.$;
    var C = S.Constants;
    var Profile = S.Backbone.Model.extend({

        idAttribute: 'id',
        defaults: { 'username': 'anonymous' },

        url: function () {
            if (this.isNew()) {
                // return S.url('ugc');
            } else {
                // return S.url('ugc');
            }
        },

        parse: function (data) {
            // TODO do we need data.ticket?
            return data ? data.profile : undefined;
        },

        authenticate: function () {
            this.fetch({
                type: 'POST',
                url: S.url('security'),
                data: $.param(this.toJSON()),
                success: function (data) {
                    S.getDirector().trigger(C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), data);
                },
                error: function () {
                    S.getDirector().trigger(C.get('EVENT_USER_AUTHENTICATION_FAILED'));
                }
            });
        },

        getFromSession: function () {

            var options = {}, model = this, xhr;

            xhr = options.xhr = $.ajax({
                type: 'GET',
                url: S.url('security.active'),
                success: function (response) {

                    // User not signed in (204)
                    if (xhr.status === 204) {
                        // TODO: what do we do if user is not logged in?
                        // user not signed in...
                    } else {
                        // TODO:
                        // There's some redundancy here. Director is resetting the same object on
                        // EVENT_USER_AUTHENTICATION_SUCCESS. Perhaps don't reset on EVENT_USER_AUTHENTICATION_SUCCESS
                        // and listen to Director's profile 'sync' event if necessary.
                        model.set(model.parse(response, options));
                        model.trigger('sync', model, response, options);
                        S.getDirector().trigger(C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), model);
                    }

                }
            });

            this.trigger('request', model, xhr, options);

        },

        hasRole: function (role) {
            var found = false;
            var roles = this.get('roles');
            roles && roles.every(function (pRole) {
                ( pRole === role ) && (found = true);
                return !found;
            });
            return found;
        }

    });

    S.define('model.Profile', Profile);

})(crafter.social);