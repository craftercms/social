(function (S) {
    'use strict';

    /* jshint -W106 */
    var _ = {
        /**
         * The director class. Path can be relative to the comments namespace or global.
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
            files: '/attachment/{attachmentId}?tenant=craftercms',
            service: '/fixtures/api/2/',
            templates: 'templates/',
            security: '/login',
            subscriptions: {
                'subscribe': '{target}.json',
                'unsubscribe': '{target}.json'
            },
            ugc: {
                target: '.json?sortField=createdDate&sortOrder=ASC',
                create: '.json',
                like: '/{id}.json',
                unlike: '/{id}.json',
                dislike: '/{id}.json',
                undislike: '/{id}.json',
                flag: '/{id}.json',
                unflag: '/{id}.json',
                file: '{attachmentId}.json',
                get_ugc: '{id}.json?tenant={tenant}',
                moderation: {
                    '{id}': '/status.json',
                    '{moderationStatus}': {
                        '.json': '',
                        'target': '.json'
                    }
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
            if ( typeof key === 'object' ) {
                for ( var k in key ) {
                    S.Cfg(k, key[k]);
                }
            } else {
                return S.get(key, _);
            }
        } else {
            S.define(key, value, _, false);
            // TODO trigger(S.Constants.get('EVENT_SOCIAL_CONFIG_CHANGED'), key, value)
            return true;
        }
    }, 'social.Cfg');

    var cfg = S.window.crafterSocial_cfg;
    if ( typeof cfg !== 'undefined' ) {
        S.Cfg(cfg);
    }

}) (crafter.social);