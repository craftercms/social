(function (S) {
    'use strict';

    var C = S.Constants,
        $ = S.$,

        areaVisibilityMode  = C.get('AREA_VISIBILITY_MODE_HOVER'),
        areaVisibilityModes = [
            C.get('AREA_VISIBILITY_MODE_HOVER'),
            C.get('AREA_VISIBILITY_MODE_REVEAL'),
            C.get('AREA_VISIBILITY_MODE_HIDE')
        ],

        socialbar = null; // TODO Single instance, right?

    var Director = $.extend({

        url: {
            base: S.Cfg('url.service'),
            ugc: {
                target: '.json',
                create: '.json',
                like: '/{id}.json',
                dislike: '/{id}.json',
                flag: '/{id}.json',
                moderation: {
                    update: '/status.json'
                }
            }
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

        getUser: function () {
            return {
                name: 'Tony',
                surname: 'Romas',
                userName: 'roman'
            };
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

        },

        actionURL: function (actionPath, replace) {
            var url = this.url;
            return S.string.fmt('{base}/{path}/{action}', {
                base: url.base,
                path: actionPath.replace(/\./g, '/'),
                action: S.util.get(url, actionPath)
            }).replace('/.json', '.json').replace(/[\/\/]+/g, '/').fmt(replace || {});
        }

    }, S.EventProvider);

    S.define('component.Director', Director);

}) (crafter.social);