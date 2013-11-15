(function (S) {
    'use strict';

    /* jshint -W106 */
    var _ = {
        /**
         * The director class. Path can be relative to the social namespace or global.
         */
        director: {
            cls: 'component.Director',
            cfg: {  }
        },
        /**
         * URL configurations
         */
        url: {
            base: '/',
            service: '/fixtures/api/2/',
            templates: 'templates/',
            security: '/login',
            notifications: {
                add: '.json?target={target}&title={title}&url={url}',
                remove: '.json?target={target}'
            },
            ugc: {
                target: '.json',
                create: '.json',
                like: '/{id}.json',
                dislike: '/{id}.json',
                flag: '/{id}.json',
                file: '{attachmentId}.json',
                moderation: {
                    update: '/status.json'
                },
                '{id}': {
                    get_attachments: '.json?tenant={tenant}',
                    add_attachment: '.json'
                }
            }
        }
    };

    S.define('Cfg', function ( key, value ) {
        if ( arguments.length === 1 ) {
            return S.get(key, _);
        } else {
            S.define(key, value, _, false);
            // TODO trigger(S.Constants.get('EVENT_SOCIAL_CONFIG_CHANGED'), key, value)
            return true;
        }
    }, 'social.Cfg');

    /* jshint -W106 */
    var cfg = S.window.crafterSocial_cfg;
    if ( typeof cfg !== 'undefined' ) {
        for (var key in cfg) {
            S.Cfg(key, cfg[key]);
        }
    }

}) (crafter.social);