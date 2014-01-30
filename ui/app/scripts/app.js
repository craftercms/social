(function (window) {
    'use strict';

    var noConflictReference = window.crafter;

    var PLACEHOLDER_STR = '$placeholder$';

    // TODO Eventually we will have a crafter API file which will define this namespace
    var crafter = {

        window: window,

        define: function ( packageName, component, root, AMD ) {

            if ( root === true || root === false || typeof root === 'string' ) {
                AMD = root;
                root = undefined;
            }

            ( typeof AMD === 'undefined' ) && ( AMD = true );

            var me      = this;
            var blocks  = [];
            packageName = packageName.replace(/\{.*?\}/g, function ( match ) {

                match = match.substr(1, (match.length - 2));

                var expr = match.split(':');
                if ( expr.length > 1 ) {
                    if ( expr[0].toUpperCase() === 'CONST' ) {
                        blocks.push( me.Constants.get(expr[1]) );
                    }
                } else {
                    blocks.push( match );
                }

                return (PLACEHOLDER_STR);

            });

            var current = root || this,
                pieces = packageName.split('.'),
                length = pieces.length,
                max = (length - 1),
                namespace;

            for(var i = 0; i < length; i++) {
                namespace = pieces[i];
                if (namespace === PLACEHOLDER_STR) {
                    namespace = blocks.shift();
                }
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

    var windowCopy = {  };
    for (var prop in window) { windowCopy[prop] = window; }

    // Director class instance (singleton)
    var director;
    var UNDEFINED = 'undefined';

    crafter.define('social', {

        window: window,
        windowCopy: windowCopy,
        parentScope: crafter,

        noConflict: function ( variable ) {
            var current = this.window[variable];
            this.window[variable] = this.windowCopy[variable];
            return current;
        },

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

            // if property wasn't found on the social
            // scope, try to find it in the global scope
            if (root !== window && typeof value === 'undefined') {
                return this.get(property, window);
            }

            return value;

        },

        socialise: function () {
            var D = this.getDirector();
            D.socialise.apply(D, arguments);
        },

        resource: function ( url ) {
            return this.string.fmt('%@%@', this.Cfg('url.base'), url);
        },

        url: function ( url, formats ) {

            if ( typeof formats === 'object' ) {
                for (var key in formats) {
                    formats[key] = window.encodeURIComponent(formats[key]);
                }
            } else if ( formats ) {
                formats = window.encodeURIComponent(formats);
            }

            var service;
            var protocol;
            var path        = this.Cfg('url.' + url);
            var absolute    = false;

            if (typeof path === 'object') {
                (path.absolute) && (absolute = true);
                path = path.value;
            }

            if (path.match(URL_PROTOCOL_REGEXP)) {
                absolute = true;
            }

            if (!absolute) {
                service     = this.Cfg('url.service');
                // The protocol's double slash is the only double slash allowed in the formation
                // of URLs so need to separate it for the replace not to break it.
                protocol    = (service.match(URL_PROTOCOL_REGEXP) || [''])[0];
            }

            return (absolute ? path : (
                protocol + this.string.fmt('{base}/{path}/{action}', {
                    base: service.substr(protocol.length),
                    path: url.replace(/\./g, '/'),
                    action: path
                }).replace('/.json', '.json').replace(/[\/\/]+/g, '/')
            )).fmt(formats || {  });
        },

        getDirector: function (  ) {
            if (!director) {

                var direction   = this.Cfg('director');
                var Director    = this.get(direction.cls);

                director = new Director(direction.cfg);

            }
            return director;
        },

        string: {
            fmt: function( str /* [ fmt1, fmt2, fm3 ] */ ) {
                if (typeof arguments[1] === 'object') {
                    var values = arguments[1];
                    return str.replace(/\{.*?\}/g, function( match ){
                        return values[match.substr(1, match.length - 2)];
                    });
                } else {
                    var index  = 0,
                        formats = Array.prototype.splice.call(arguments, 1);
                    return str.replace(/%@([0-9]+)?/g, function(s, argIndex) {
                        argIndex = (argIndex) ? parseInt(argIndex, 10) - 1 : index++;
                        if (index >= formats.length) { index = 0; }
                        s = formats[argIndex];
                        return (s === null) ? '(null)' : (typeof s === UNDEFINED) ? '' : s;
                    });
                }
            },
            loc: function ( key ) {
                var value = crafter.social.util.get(this.LOCALE, key);
                return value;
            },
            LOCALE: {
                months: [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
                days: ['Sunday','Monday','Tuesday','Wednesday', 'Thursday','Friday','Saturday']
            }
        }

    });

    var URL_PROTOCOL_REGEXP = /^(https?:)?\/\//i;

}) (window);