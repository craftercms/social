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

        /* Currently only supporting single global sorting order (not per thread) */
        'global': {
            threadSortBy: 'DESC',
            threadSortOrder: 'createdDate'
        },
        /**
         * URL configurations
         */
        url: {
            base: '/',
            files: '/attachment/{attachmentId}?context={context}',
            service: '/api/3/',
            templates: '/templates/',
            security: {
                value: '/crafter-social/crafter-security-login',
                active: '/crafter-social/crafter-security-current-auth'
            },
            subscriptions: {
                'subscribe': '{target}.json',
                'unsubscribe': '{target}.json'
            },
            profile :{
                avatar:'/{id}?context={context}&ts{ts}'
            },
            threads: {
                '{target}': {
                    'comments': {
                        'value': '?context={context}&sortBy={sortBy}&sortOrder={sortOrder}',
                        '{id}': {}
                    },
                    'subscribe': '?context={context}',
                    'unsubscribe': '?context={context}'
                },
                '{_id}': {
                    'subscribe': '?context={context}',
                    'unsubscribe': '?context={context}'
                }
            },
            comments: {
                'value': '?context={context}',
                '{_id}': {
                    'value': '?context={context}',
                    'votes': {
                        'value': '', // get comment votes
                        'neutral': '',
                        'down': '',
                        'up': ''
                    },
                    'update':{
                        'value':''
                    },
                    'flags': {
                        'value': '?context={context}', // Flag comment flags
                       '{flagId}':'?context={context}'
                    },
                    'attachments': {
                        'value': '',
                        '{fileId}': {
                            'value': '?context={context}',
                            'delete': '?context={context}'
                        }
                    },
                    'moderate': '?context={context}'

                }
            },

            ugc: {
                target: '.json',
                create: '.json',
                like: '/{id}.json',
                unlike: '/{id}.json',
                dislike: '/{id}.json',
                undislike: '/{id}.json',
                flag: '/{id}.json',
                unflag: '/{id}.json',
                file: '{attachmentId}.json',
                get_ugc: '{id}.json?context={context}',
                moderation: {
                    '{id}': '/status.json',
                    '{moderationStatus}': {
                        '.json': '',
                        'target': '.json'
                    }
                },
                '{id}': {
                    get_attachments: '.json?context={context}',
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
