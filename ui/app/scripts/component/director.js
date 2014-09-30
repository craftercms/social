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

    function Director (cfg) {

        var me = this;

        var guid = this.guid = U.guid();
        privates[guid] = { CACHE: {} };

        this.cfg = $.extend({}, Director.DEFAULTS, cfg || {});

        $.each(this.cfg.handlers, function (event, conf) {

            var dispatch    = (conf.dispatch ? (conf.dispatch === 'self' ? me : conf.dispatch) : me);
            var context     = (conf.context ? (conf.context === 'self' ? me : conf.context) : me);
            var handler     = (conf.handler);

            me.listenTo(dispatch, event, U.isFunction(handler) ? handler : context[handler] || S.noop);

        });

        $( document ).ajaxComplete(function( event, request ) {
            var modal;
            if (request.status === 403) {
                me.trigger(C.get('EVENT_UNAUTHORISED_RESPONSE'));
            } else if (request.status === 404) {

                // me.trigger(C.get('EVENT_404'));

                modal = new S.view.Modal({
                    events: { 'click [data-dismiss]': 'destroy' },
                    modal: { show: true }
                }).render();

                modal.set({
                    title: 'Not Found',
                    body: 'Sorry, the requested URL was not found.',
                    footer: '<button data-dismiss class="btn btn-default">Close</button>'
                });

            } else if (request.status === 500) {

                // me.trigger(C.get('EVENT_500'));

                modal = new S.view.Modal({
                    events: { 'click [data-dismiss]': 'destroy' },
                    modal: { show: true }
                }).render();

                modal.set({
                    title: 'Error',
                    body: 'The server responded with an error, please verify your data and try again in a few seconds.',
                    footer: '<button data-dismiss class="btn btn-default">Close</button>'
                });

            }  else if (request.status === 403) {

                // me.trigger(C.get('EVENT_500'));

                modal = new S.view.Modal({
                    events: { 'click [data-dismiss]': 'destroy' },
                    modal: { show: true }
                }).render();

                modal.set({
                    title: 'Error',
                    body: 'You don\'t have permissions to access that resource.',
                    footer: '<button data-dismiss class="btn btn-default">Close</button>'
                });

            }
        });

        this.cache('profile', new S.model.Profile());
        this.checkSessionProfile();

    }

    Director.DEFAULTS = {
        socialbar: {
            cls: 'view.SocialBar',
            cfg: {  }
        },
        view: {
            discussion: {
                cls: 'view.Popover',
                cfg: { /* target: null, context: null, collection: null */ }
            },
            parasite: {
                cls: 'view.Commentable',
                cfg: { /* target: null, context: null, collection: null */ }
            }
        },
        ctrl: {
            main: {
                cls: 'controller.Base',
                cfg: { /* target: null, context: null */ }
            }
        },
        handlers: (function () {

            var handlers = { };

            /**
             * A handler is comprised of
             *  {'self' || object} context: the object containing the handler function. If omitted assumes 'self'.
             *  {string || function} handler: the function that handles the event. In the case of a string, should match the name of a function that is part of 'context' object
             *  {'self' || object} dispatch the object that dispatches the event. if omitted assumes 'self'.
             */

            handlers[C.get('EVENT_UNAUTHORISED_RESPONSE')]          = { handler: 'authenticate' };
            handlers[C.get('EVENT_USER_AUTHENTICATION_SUCCESS')]    = { handler: 'setProfile' };

            return handlers;

        }) ()
    };

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
            if ( !(oProfile instanceof S.model.Profile) ) {
                oProfile = new S.model.Profile(oProfile);
            }
            return this.cache('profile', oProfile);
        },
        authenticate: function () {

            var view = new (S.get('view.Auth'))({
                model: this.getProfile(),
                modal: { show: true }
            });

            view.$el.on('shown.bs.modal', function () {
                view.$el.find('input:first').select();
            });

            view.render();

        },
        checkSessionProfile: function () {
            var profile = this.getProfile();
            profile.getFromSession();
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

        setActiveDiscussionViewer: function ( viewPath, cfg ) {

            var view = this.cache(viewPath);
            if ( !view ) {
                view = new (S.get(viewPath))(cfg || {});
                this.cache(viewPath, view);
            }

            this.cache('activeView', viewPath);

            return view;

        },
        getActiveDiscussionViewer: function ( /*render*/ ) {

            // Active view contains a string that that tells what the active discussion
            // displaying view is at the moment. It is also the view class package that
            // can be used to retrieve the view class in the run time, whether that is
            // relative to the "comments" namespace or to the global (window) namespace

            if ( !this.cache('activeView') ) {
                this.setActiveDiscussionViewer(this.cfg.view.discussion.cls, this.cfg.view.discussion.cfg);
            }

            return this.cache('activeView');

        },

        /**
         *
         * @param {Object} oConfig an object of configuration of the comments target
         *      {
         *          viewClass: UI controller that embeds into the elements/page
         *          context: ...
         *          target: ...
         *          viewConfig: configuration object for the view class
         *      }
         */
        socialise: function ( oConfig ) {

            var oCfg = $.extend(true, {}, Director.DEFAULTS, oConfig);

            var Ctrl    = S.get(oCfg.ctrl.main.cls)      || S.get(oCfg.ctrl.main.cls, window),
                View    = S.get(oCfg.view.parasite.cls)  || S.get(oCfg.view.parasite.cls, window),
                Bar     = oCfg.socialbar ? S.get(oCfg.socialbar.cls) : false;

            var controller = new Ctrl(null, {
                target: oCfg.target,
                context: oCfg.context,
                model: S.model.Comment
            });

            this.cache(S.string.fmt('controller.%@.%@', oCfg.context, oCfg.target), controller);

            var parasite = new View($.extend({
                target: oCfg.target,
                context: oCfg.context,
                collection: controller
            }, oCfg.view.parasite.cfg));

            this.cache(S.string.fmt('parasite.%@.%@', oCfg.context, oCfg.target), parasite);

            if (Bar && !socialbar) {
                socialbar = new Bar(oCfg.socialbar.cfg);
                socialbar.$el.appendTo('body');
            }

        }

    });

    S.define('component.Director', Director);

}) (crafter.social);