(function (window, $, Handlebars, Backbone, _) {
    'use strict';

    var UNDEFINED = 'undefined',
        undefined, crafter, social,
        Backbone = window.Backbone;

    if (typeof window.crafter === UNDEFINED) {
        window.crafter = {};
    }

    crafter = window.crafter;

    if (typeof crafter.social === UNDEFINED) {
        crafter.social = {};
    }

    social = crafter.social;

    social.window = window;
    social.$ = $.noConflict();
    social.Backbone = Backbone.noConflict();
    social.underscore = _.noConflict();
    social.TemplateEngine = Handlebars;
    social.EventProvider = _.clone(Backbone.Events);

    Handlebars.registerHelper('html', function(html) {
        return new Handlebars.SafeString(html);
    });

    Handlebars.registerHelper('render', function(tmpl, context) {
        return new Handlebars.SafeString(tmpl);
    });

    Handlebars.registerHelper('date', function(millis) {
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

    social.define = function (packageName, component, root) {
        var current = root || social,
            pieces = packageName.split('.'),
            max = (pieces.length - 1);
        $.each(pieces, function (i, namespace) {
            if (i === max && (typeof component !== 'undefined')) {
                current[namespace] = component;
            } else if (!(namespace in current)) {
                current[namespace] = {};
            }
            current = current[namespace];
        });
        return component;
    };

    social.define('util', {
        guid: (function () {
            function fn() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            }
            return function () {
                return fn() + fn() + '-' + fn() + '-' + fn() + '-' + fn();
            }
        })(),
        traverseObject: function (object, property) {
            var pieces = property.split('.'),
                value = object;
            $.each(pieces, function (i, piece) {
                value = value[piece];
            });
            return value;
        },
        inherit: function (subClass, superClass, prototype) {
            var F = function() {};
            F.prototype = superClass.prototype;
            subClass.prototype = new F();
            subClass.prototype.constructor = subClass;
            subClass.superclass = superClass.prototype;
            $.extend(subClass.prototype, prototype || {});
            if(superClass.prototype.constructor == Object.prototype.constructor) {
                superClass.prototype.constructor = superClass;
            }
            return subClass;
        },
        template: function (tmpl, context) {
            var template = Handlebars.compile(tmpl);
            return template(context);
        },
        isHTML: (function () {
            var regexp = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/;
            return function ( selector ) {
                return regexp.exec( selector );
            }
        }) (),
        eval: function (text) {
            return eval('(%@)'.fmt(text));
        },
        emptyFn: function () {}
    });

    social.define('Constants', {
        TEMPLATES_URL: '../hbs/',

        EVENT_GET_LIST: 'get-list',
        EVENT_STATE_SUCCESS: 'success',
        EVENT_STATE_FAILURE: 'error',
        EVENT_STATE_LOADING: 'loading',
        EVENT_STATE_COMPLETE: 'complete'

    });

    social.define('request', function (data) {
        return $.ajax(data);
    });

    social.define('String', {
        fmt: function( str /* [ fmt1, fmt2, fm3 ] */ ) {
            if (typeof arguments[1] === 'object') {
                var values = arguments[1];
                return str.replace(/\{.*?\}/g, function(match, index){
                    return values[match.substr(1, match.length - 2)];
                });
            } else {
                var index  = 0,
                    formats = Array.prototype.splice.call(arguments, 1);
                return str.replace(/%@([0-9]+)?/g, function(s, argIndex) {
                    argIndex = (argIndex) ? parseInt(argIndex, 10) - 1 : index++;
                    if (index >= formats.length) index = 0;
                    s = formats[argIndex];
                    return (s === null) ? '(null)' : (s === undefined) ? '' : s;
                });
            }
        },
        loc: function ( key ) {
            var value = social.util.traverseObject(this.LOCALE, key);
            return value;
        },
        LOCALE: {
            months: [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ],
            days: ['Sunday','Monday','Tuesday','Wednesday', 'Thursday','Friday','Saturday']
        }
    });

    String.prototype.fmt = function () {
        var args = Array.prototype.slice.call(arguments, 0);
        args.splice(0, 0, this+'');
        return social.String.fmt.apply(social.String, args);
    };

    String.prototype.loc = function (key) {
        return social.String.loc(this);
    };

    Array.prototype.j = function (c) {
        return this.join(c || '');
    };

}) (window, jQuery, Handlebars, Backbone, _);

(function (S) {
    'use strict';

    var instance,
        C = S.Constants,
        $ = S.$;

    var Orchestrator = $.extend({
        url: {
            base: '/crafter/social/social-ui/crafter-social/api/2/',
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
        eventStates: [C.EVENT_STATE_SUCCESS, C.EVENT_STATE_FAILURE, C.EVENT_STATE_LOADING],
        getBroadcastId: function (GUID, event, state) {

            var broadcast = '{guid}.{event}.{state}'.fmt({
                guid: GUID,
                event: event,
                state: state
            });

            broadcast = (typeof state === 'undefined')
                ? broadcast.replace('.undefined', '')
                : broadcast;

            return broadcast;
        },
        publish: function (oMsg, data) {
            var msg = this.getBroadcastId(oMsg.guid, oMsg.event, oMsg.state),
                args = Array.prototype.slice.call(arguments, 1);
            args.splice(0, 0, msg);
            this.trigger.apply(this, args);
        },
        instance: function (oConfg) {
            if (!instance) {
                return new (function () {
                    this.initialize.apply(this, arguments);
                }) (oConfg);
            }
            return instance;
        },
        getUser: function () {
            return {
                name: 'Tony',
                surname: 'Romas',
                userName: 'rart'
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
        socialise: function (oConfig) {

            var oConfig = $.extend({
                // set a default view class
                viewClass: crafter.social.view.Commentable
            }, oConfig);

            var controller = new S.controller.SocialController({
                target: oConfig.target,
                tenant: oConfig.tenant
            });

            var view = new oConfig.viewClass({
                target: oConfig.target,
                tenant: oConfig.tenant,
                collection: controller
            });

        },
        actionURL: function (actionPath, replace) {
            var url = this.url;
            return S.String.fmt('{base}/{path}/{action}', {
                base: url.base,
                path: actionPath.replace(/\./g, '/'),
                action: S.util.traverseObject(url, actionPath)
            }).replace('/.json', '.json').replace(/[\/\/]+/g, '/').fmt(replace || {});
        }
    }, S.EventProvider);

    Orchestrator.DEFAULTS = {

    };

    Orchestrator.prototype = {
        socialise: function (target, tenant) {

            var ctrl = new S.SocialController({
                tenant: tenant,
                target: target
            });

            var indicatorUI = new S.IndicatorBinder({

            });

            var binder = new S.UIBinder({
                target: target,
                ctrl: ctrl.getGUID()
            });

        }
    };

    var jQueryPlugin = function (options) {
        return this.each(function () {
            var $me = $(this),
                target = $me.attr('data-crafter-social-target'),
                tenant = $me.attr('data-crafter-social-tenant');
            // TODO ...
        });
    }

    S.$.fn.crafterSocial = jQueryPlugin;
    if (S.window.jQuery && S.window.jQuery !== S.$) {
        S.window.jQuery.fn.crafterSocial = jQueryPlugin;
    }

    S.Orchestrator = Orchestrator;

}) (crafter.social);