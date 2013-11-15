(function (window) {
    'use strict';

    var noConflictReference = window.crafter;

    // TODO Eventually we will have a crafter API file which will define this namespace
    var crafter = {

        window: window,

        define: function ( packageName, component, root, AMD ) {

            if ( root === true || root === false || typeof root === 'string' ) {
                AMD = root;
                root = undefined;
            }

            ( typeof AMD === 'undefined' ) && ( AMD = true );

            var current = root || this,
                pieces = packageName.split('.'),
                length = pieces.length,
                max = (length - 1),
                namespace;

            for(var i = 0; i < length; i++) {
                namespace = pieces[i];
                if (i === max && (typeof component !== 'undefined')) {
                    current[namespace] = component;
                } else if (!(namespace in current)) {
                    current[namespace] = {};
                }
                current = current[namespace];
            }

            /* jshint expr:true */
            ( typeof AMD === 'string' ) && ( packageName = AMD );
            ( AMD ) && crafter.amd(packageName, component, (root === window));

            return component;

        },
        noConflict: function ( getPrev ) {
            if ( getPrev ) {
                return noConflictReference;
            } else {
                window.crafter = noConflictReference;
                return this;
            }
        },
        /**
         *
         * @param component the object to register to the amd
         * @param {string} name the name of the module
         * @param {boolean} global if supplied as true, window[name] will be created
         */
        amd: function ( name, component, global ) {
            if ( typeof module === 'object' && module && typeof module.exports === 'object' ) {
                module.exports = component;
            } else {

                if ( global ) { window[name] = component; }

                /* global define */
                if ( typeof define === 'function' && define.amd ) {
                    /* global define */
                    define( name, [], function () { return component; } );
                }
            }
        }
    };

    crafter.amd('crafter', crafter, true);

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // Director class instance (singleton)
    var director;

    crafter.define('social', {

        window: window,
        parentScope: crafter,

        define: function () { crafter.define.apply(this, arguments); },

        get: function ( property, root ) {

            if (arguments.length === 1) {
                root = this;
            }

            var pieces = property.split('.'),
                value = root || window;

            for (var piece, i = 0, l = pieces.length; i < l; i++) {
                piece = pieces[i];
                if (typeof value !== 'undefined') {
                    value = value[piece];
                } else {
                    break;
                }
            }

            return value;

        },

        socialise: function () {
            var D = this.getDirector();
            D.socialise.apply(D, arguments);
        },

        resource: function ( url ) {
            return ('%@%@'.fmt(this.Cfg('url.base'), url));
        },

        url: function ( url, formats ) {

            if ( typeof formats === 'object' ) {
                for (var key in formats) {
                    formats[key] = window.encodeURIComponent(formats[key]);
                }
            } else if ( formats ) {
                formats = window.encodeURIComponent(formats);
            }

            var service     = this.Cfg('url.service');
            var protocol    = (service.match(URL_PROTOCOL_REGEXP) || [''])[0];

            return protocol + this.string.fmt(
                '{base}/{path}/{action}', {
                    base: service.substr(protocol.length),
                    path: url.replace(/\./g, '/'),
                    action: this.Cfg('url.' + url)
                })
                .replace('/.json', '.json')
                .replace(/[\/\/]+/g, '/')
                .fmt(formats || {});

        },

        getDirector: function (  ) {
            if (!director) {

                var direction   = this.Cfg('director');
                var Director    = this.get(direction.cls);

                director = new Director(direction.cfg);

            }
            return director;
        }

    });

    var URL_PROTOCOL_REGEXP = /^(https?:)?\/\//i;

}) (window);