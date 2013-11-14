(function (S) {
    'use strict';

    var C = S.Constants,
        U = S.util,
        $ = S.$,

        areaVisibilityMode  = C.get('AREA_VISIBILITY_MODE_HOVER'),
        areaVisibilityModes = [
            C.get('AREA_VISIBILITY_MODE_HOVER'),
            C.get('AREA_VISIBILITY_MODE_REVEAL'),
            C.get('AREA_VISIBILITY_MODE_HIDE')
        ],

        socialbar = null; // TODO Single instance, right?

    var privates = {};

    function Director () {

        var me = this;

        var guid = this.guid = U.guid();
        privates[guid] = { CACHE: {} };

        this.cache('profile', new S.model.Profile());

        this.listenTo(this, C.get('EVENT_UNAUTHORISED_RESPONSE'), this.authenticate);
        this.listenTo(this, C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), this.setProfile);

        $( document ).ajaxComplete(function( event, request ) {
            if (request.status === 401) {
                me.trigger(C.get('EVENT_UNAUTHORISED_RESPONSE'));
            }
        });

    }

    Director.DEFAULTS = {};

    Director.prototype = $.extend({}, S.EventProvider, {

        cache: function ( key, value ) {
            if (arguments.length === 1) {
                return privates[this.guid].CACHE[key];
            } else {
                privates[this.guid].CACHE[key] = value;
            }
        },

        getProfile: function () {
            return this.cache('profile');
        },
        setProfile: function ( oProfile ) {
            return this.cache('profile', oProfile);
        },
        authenticate: function () {

            var view = new (S.get('view.Auth'))({
                model: this.getProfile(),
                modal: {
                    show: true
                }
            });

            view.render();

        },

        getAreaVisibilityMode: function () {
            return areaVisibilityMode;
        },
        setAreaVisibilityMode: function (mode) {
            if (S.util.indexOf(areaVisibilityModes, mode) === -1) {
                throw new Error('Unknown mode %@ supplied to Director (setAreaVisibilityMode)'.fmt(mode));
            }
            areaVisibilityMode = mode;
            this.trigger(C.get('EVENT_AREAS_VISIBILITY_CHANGE'), mode);
        },

        /**
         *
         * @param {Object} oConfig an object of configuration of the social target
         *      {
         *          viewClass: UI controller that embeds into the elements/page
         *          tenant: ...
         *          target: ...
         *          viewConfig: configuration object for the view class
         *      }
         */
        socialise: function ( oConfig ) {

            var oCfg = $.extend({
                // set a default view class
                viewClass: 'view.Commentable',
                socialbar: true,
                viewCfg: { }
            }, oConfig);

            var Ctrl = S.get('controller.Base'),
                View = S.get(oCfg.viewClass) || S.get(oCfg.viewClass, window),
                Bar = S.get('view.SocialBar');

            var controller = new Ctrl({
                target: oCfg.target,
                tenant: oCfg.tenant
            });

            new View($.extend({
                target: oCfg.target,
                tenant: oCfg.tenant,
                collection: controller
            }, oCfg.viewCfg));

            if (oCfg.socialbar && !socialbar) {
                socialbar = new Bar();
                socialbar.$el.appendTo('body');
            }

        }

    });

    S.define('component.Director', Director);

}) (crafter.social);