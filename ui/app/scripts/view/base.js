(function (S) {
    'use strict';

    var counter = 0,
        U = S.util,
        $ = S.$;

    var Base = S.Backbone.View.extend({
        className: 'crafter-social-view',
        initialize: function ( oConfig ) {
            if (!this.cache('initialised')) {

                this.guid   = U.guid();

                // Extend defaults
                this.extendCfg(this.constructor.DEFAULTS, oConfig || {});
                this.cfg.classes && this.$el.addClass(
                    U.isArray(this.cfg.classes) ? this.cfg.classes.join(' ') : this.cfg.classes);

                // Initialise UI actions
                this.delegateActions(this, this.element());
                this.listen();

                // Create the UI
                this.createUI && this.createUI();

                // replace any guid found on the event declarations
                this.events && (this.events = this.parseEvents());
                this.handleIdentifyRequests();

                this.cache('initialised', true);

            }
        },
        listen: function () {},
        delegateActions: function (context, $elem) {
            $elem.delegate('[data-action]', 'click', function (evt) {
                evt.preventDefault();
                evt.stopImmediatePropagation();
                evt.stopPropagation();
                var action = $( this ).data('action');
                if (action !== '') {
                    context[action]();
                }
            });
        },
        extendCfg: function ( oDefaults, oConfig ) {
            ( !this.cfg ) && ( this.cfg = {} );
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },
        extendCls: function ( sub, sup ) {
            // Use false as the first item on the classes array to fully override the superclass classes
            return (sub) && (sub[0] === false) ? (sub) : (S.util.isArray(sub) ? sub : sub.split(' ')).concat(sup);
        },
        parseEvents: function ( data ) {
            var evts = { };
            data = $.extend(data || {}, { guid: this.guid });
            this.events && $.each(this.events, function (key, value) {
                var evt = key.fmt(data.guid).fmt(data);
                evts[evt] = value;
            });
            return evts;
        },
        handleIdentifyRequests: function () {
            var me = this;
            this.$('[data-indentifyme]').each(function () {
                me.identify(this, $(this).data('indentifyme'));
                $(this).removeAttr('data-indentifyme');
            });
        },

        identify: function ( elem, prefix ) {
            var id = '%@_%@'.fmt( (prefix || ('elem' + counter++)), this.guid );
            $(elem).attr({ 'id': id });
        },
        createUI: function () {
            if (!this.cache('created')) {
                this.element().html(this.getTemplate('main'));
                this.cache('created', true);
            }
        },
        element: function () {
            return this.$el;
        },
        getTemplate: function ( tmplName ) {

            var tmpl = this.cfg.templates[tmplName];
            if (typeof tmpl === 'function') {
                tmpl = tmpl.call(this, tmplName);
            }

            var cache = tmplCache.get(tmpl);

            if (typeof tmpl === 'undefined' || tmpl === null) {
                // TODO what to do?
                return '';
            } else if (U.isHTML(tmpl)) {
                return tmpl;
            } else if (cache !== null) {
                return cache;
            } else {
                var tmplHTML;
                var me = this;
                S.request({
                    async: false,
                    url: tmpl,
                    success: function (html) {
                        tmplHTML = html.replace(/data-identifyme="(.*?)"/g, 'id="$1_' + me.guid + '"');
                        tmplCache.set(tmpl, tmplHTML);
                    }
                });
                return tmplHTML;
            }

        },

        cache: function ( name, value ) {
            ( !privates[this.guid] ) && ( privates[this.guid] = {} );
            if ( arguments.length > 1 ) {
                privates[this.guid][name] = value;
                return true;
            } else {
                return privates[this.guid][name];
            }
        }
    });

    Base.DEFAULTS = {
        /*
        templates: {
            main: S.string.fmt('%@tplName.hbs', S.Cfg('url.templates')),
            someOtherTplName: ...
        }
        */
    };

    var privates = {},
        tmplCache = {
            cache: {},
            set: function ( key, value ) {
                this.cache[key] = value;
                return true;
            },
            get: function ( key ) {
                return (this.cache[key] || null);
            }
        };

    S.define('view.Base', Base);

}) (crafter.social);