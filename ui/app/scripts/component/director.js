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

        this.cache('profile', new S.model.Profile());

        this.listenTo(this, C.get('EVENT_UNAUTHORISED_RESPONSE'), this.authenticate);
        this.listenTo(this, C.get('EVENT_USER_AUTHENTICATION_SUCCESS'), this.setProfile);

        $( document ).ajaxComplete(function( event, request ) {
            if (request.status === 401) {
                me.trigger(C.get('EVENT_UNAUTHORISED_RESPONSE'));
            }
        });

    }

    Director.DEFAULTS = {
        socialbar: {
            cls: 'view.SocialBar',
            cfg: {  }
        },
        view: {
            discussion: {
                cls: 'view.Popover',
                cfg: { /* target: null, tenant: null, collection: null */ }
            },
            parasite: {
                cls: 'view.Commentable',
                cfg: { /* target: null, tenant: null, collection: null */ }
            }
        },
        ctrl: {
            main: {
                cls: 'controller.Base',
                cfg: { /* target: null, tenant: null */ }
            }
        }
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
            // relative to the "social" namespace or to the global (window) namespace

            if ( !this.cache('activeView') ) {
                this.setActiveDiscussionViewer(this.cfg.view.discussion.cls, this.cfg.view.discussion.cfg);
            }

            return this.cache('activeView');

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

            var oCfg = $.extend(true, {}, Director.DEFAULTS, oConfig);

            var Ctrl    = S.get(oCfg.ctrl.main.cls)      || S.get(oCfg.ctrl.main.cls, window),
                View    = S.get(oCfg.view.parasite.cls)  || S.get(oCfg.view.parasite.cls, window),
                Bar     = oCfg.socialbar ? S.get(oCfg.socialbar.cls) : false;

            var controller = new Ctrl({
                target: oCfg.target,
                tenant: oCfg.tenant
            });

            this.cache(S.string.fmt('controller.%@.%@', oCfg.tenant, oCfg.target), controller);

            var parasite = new View($.extend({
                target: oCfg.target,
                tenant: oCfg.tenant,
                collection: controller
            }, oCfg.view.parasite.cfg));

            this.cache(S.string.fmt('parasite.%@.%@', oCfg.tenant, oCfg.target), parasite);

            if (Bar && !socialbar) {
                socialbar = new Bar(oCfg.socialbar.cfg);
                socialbar.$el.appendTo('body');
            }

        }

    });

    S.define('component.Director', Director);

}) (crafter.social);