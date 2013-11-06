(function (social) {
    'use strict';

    var UNDEFINED = 'undefined',
        window = social.window;

    // Cache libs.
    social.$ = window.$.noConflict();
    social.Backbone = window.Backbone.noConflict();
    social.underscore = window._.noConflict();
    social.TemplateEngine = window.Handlebars;
    social.EventProvider = social.underscore.clone(social.Backbone.Events);

    var $ = social.$;

    social.define('request', $.ajax, false);

    social.define('util', $.extend({}, social.underscore, {
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

            // Give the Temp constructor the Constructor's prototype
            Temp.prototype = Class.prototype;

            // Create a new temp
            temp = new Temp();

            // Call the original Constructor with the temp
            // temp as its context (i.e. its 'this' value)
            instance = Class.apply(temp, args);

            // If an object has been returned then return it otherwise
            // return the original temp.
            // (consistent with behaviour of the new operator)
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
            var template = Handlebars.compile(tmpl);
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
        emptyFn: function () {}
    }), false);

    (function (social) {

        var _ = {

            // TODO should APP_ROOT be here?
            APP_ROOT: '/',
            TEMPLATES_URL: '/templates/',

            SERVICE: '/fixtures/api/2/',

            EVENT_AREAS_VISIBILITY_CHANGE: 'crater.social.event.areas.visibility.change',
            AREA_VISIBILITY_MODE_REVEAL: 'area.visibility.mode.reveal',
            AREA_VISIBILITY_MODE_HOVER: 'area.visibility.mode.hover',
            AREA_VISIBILITY_MODE_HIDE: 'area.visibility.mode.hide'

        };

        social.define('Constants', {

            // TODO substitute all below in favour of the private/immutable constants?
            get: function (key) {
                return _[key];
            },

            APP_ROOT: _.APP_ROOT,
            TEMPLATES_URL: _.TEMPLATES_URL,

            EVENT_AREAS_VISIBILITY_CHANGE: _.EVENT_AREAS_VISIBILITY_CHANGE,
            AREA_VISIBILITY_MODE_REVEAL: _.AREA_VISIBILITY_MODE_REVEAL,
            AREA_VISIBILITY_MODE_HOVER: _.AREA_VISIBILITY_MODE_HOVER,
            AREA_VISIBILITY_MODE_HIDE: _.AREA_VISIBILITY_MODE_HIDE

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

    // Handlebars Helpers

    var Handlebars = social.TemplateEngine;

    Handlebars.registerHelper('html', function( html ) {
        return new Handlebars.SafeString(html);
    });

    /*Handlebars.registerHelper('render', function( tmpl, context ) {
        return new Handlebars.SafeString(tmpl);
    });*/

    Handlebars.registerHelper('date', function( millis ) {
        var date = new Date(millis),
            format;
        var str = (format || '{day} {date}, {month} {year}').fmt({
            month: 'months.%@'.fmt(date.getMonth()).loc(),
            day: 'days.%@'.fmt(date.getDay()).loc(),
            date: date.getDate(),
            year: date.getFullYear()
        });
        return new Handlebars.SafeString(str);
    });

}) (crafter.social);