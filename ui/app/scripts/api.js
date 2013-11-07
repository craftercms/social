(function (social) {
    'use strict';

    var UNDEFINED = 'undefined',
        window = social.window;

    // Cache libs.
    social.$ = window.$.noConflict();
    social.Backbone = window.Backbone.noConflict();
    social.underscore = window._.noConflict();
    social.EventProvider = social.underscore.clone(social.Backbone.Events);

    var $ = social.$;

    social.define('request', $.ajax, false);

    social.define('util', $.extend({}, social.underscore, {

        emptyFn: function () {},

        guid: (function () {
            function fn() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            }
            return function () {
                return fn() + fn() + '-' + fn() + '-' + fn() + '-' + fn();
            };
        })(),

        get: function ( object, property ) {
            return arguments.length === 1 ? social.get( object, window ) : social.get( property, object );
        },

        instance: function ( cls ) {

            var Class = social.get(cls) || social.get(cls, window);
            var args  = Array.prototype.splice.call(arguments, 1);

            var Temp = function () { },
                temp, instance;

            Temp.prototype = Class.prototype;

            temp = new Temp();
            instance = Class.apply(temp, args);

            return (Object(instance) === instance) ? instance : temp;

        },

        inherit: function (child, parent, prototype) {

            var F = function() {};
            F.prototype = parent.prototype;

            child.prototype = new F();
            child.prototype.constructor = child;
            child.superclass = parent.prototype;
            $.extend(child.prototype, prototype || {});

            if ( parent.prototype.constructor === Object.prototype.constructor ) {
                parent.prototype.constructor = parent;
            }

            return child;

        },

        template: function (tmpl, context) {
            var template = social.TemplateEngine.compile(tmpl);
            return template(context);
        },

        isHTML: (function () {
            var regexp = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/;
            return function ( selector ) {
                return regexp.exec( selector );
            };
        }) (),

        fromJSON: function (text) {
            return JSON.parse(text);
        },

        log: function ( /* txt, fmt1, fmt2 ... fmtN */ ) {
            if ( console && console.log ) {
                var str = social.string.fmt.apply(social.string, arguments);
                console.log(str);
            } else {
                return false;
            }
        }

    }), false);

    (function (S) {

        var _ = {
            /**
             * The director class. Path can be relative to the social namespace or global.
             */
            director: 'component.Director',
            /**
             * URL configurations
             */
            url: {
                base: '/',
                service: '/fixtures/api/2/',
                templates: 'templates/'
            }
        };

        S.define('Cfg', function ( key, value ) {
            if ( arguments.length === 1 ) {
                return S.get(key, _);
            } else {
                S.define(key, value, _, false);
                return true;
            }
        }, 'social.Cfg');

        /* jshint -W106 */
        var cfg = S.window.crafterSocial_cfg;
        if ( typeof cfg !== 'undefined' ) {
            S.$.each(cfg, function ( key, value ) {
                S.Cfg(key, value);
            });
        }

    }) (social);

    (function (S) {

        var _ = {

            EVENT_AREAS_VISIBILITY_CHANGE: 'crater.social.event.areas.visibility.change',
            AREA_VISIBILITY_MODE_REVEAL: 'area.visibility.mode.reveal',
            AREA_VISIBILITY_MODE_HOVER: 'area.visibility.mode.hover',
            AREA_VISIBILITY_MODE_HIDE: 'area.visibility.mode.hide'

        };

        S.define('Constants', {
            get: function ( key ) { return _[key]; },
            define: function ( key, value ) {
                if ( !(key in _) ) {
                    _[key] = value;
                    return true;
                } else {
                    S.util.log(
                        'Constant %@ is already defined (value: %@). Value not changed.',
                        key, S.Constants.get(key));
                    return true;
                }
            }
        }, 'social.Constants');

    }) (social);

    social.define('string', {
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
            var value = social.util.get(this.LOCALE, key);
            return value;
        },
        LOCALE: {
            months: [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
            days: ['Sunday','Monday','Tuesday','Wednesday', 'Thursday','Friday','Saturday']
        }
    }, 'social.string');

    // Prototype Extensions

    String.prototype.fmt = function () {
        var args = Array.prototype.slice.call(arguments, 0);
        args.splice(0, 0, this + '');
        return social.string.fmt.apply(social.string, args);
    };

    String.prototype.loc = function (  ) {
        return social.string.loc(this);
    };

    Array.prototype.j = function ( c ) {
        return this.join(c || '');
    };

    if ( !Array.prototype.every ) {
        Array.prototype.every = function (fn) {
            var result = true;
            for (var i = 0, l = this.length; i < l; i++) {
                result = fn(this[i], i);
                if (!result) { break; }
            }
            return result;
        };
    }

}) (crafter.social);