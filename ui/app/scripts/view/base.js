(function (S) {
    'use strict';

    var counter = 0,
        U = S.util,
        $ = S.$;

    var Base = S.Backbone.View.extend({
        className: 'crafter-social-view',
        initialize: function ( oConfig ) {
            this.guid = U.guid();
            // Extend defaults
            this.extendCfg(Base.DEFAULTS, oConfig || {});
            this.cfg.classes && this.$el.addClass(U.isArray(this.cfg.classes) ? this.cfg.classes.join(' ') : this.cfg.classes);
            // Initialise UI actions
            this.initializeActionDelegation(this, this.element());
            this.listen();
            // Create the UI
            this.createUI();
        },
        listen: function () {},
        identify: function ( elem, prefix ) {
            $(elem).attr({
                'id': '%@_%@'.fmt((prefix || ('elem' + counter++)), U.guid()),
                'data-component-guid': this.guid
            });
        },
        initializeActionDelegation: function (context, $elem) {
            $elem.delegate('[data-action]', 'click', function (evt) {
                evt.preventDefault();
                var action = $( this ).attr('data-action');
                if (action !== '') {
                    context[action]();
                }
            });
        },
        extendCfg: function ( oDefaults, oConfig ) {
            ( !this.cfg ) && ( this.cfg = {} );
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },
        createUI: function () {
            this.element().html(this.getTemplate('main'));
        },
        element: function () {
            return this.$el;
        },
        getTemplate: function ( tmplName ) {

            var tmpl = this.cfg.templates[tmplName],
                cache = tmplCache.get(tmpl);

            if (typeof tmpl === 'undefined' || tmpl === null) {
                // TODO what to do?
                return '';
            } else if (U.isHTML(tmpl)) {
                return tmpl;
            } else if (cache !== null) {
                return cache;
            } else {
                var tmplHTML;
                S.request({
                    async: false,
                    url: tmpl,
                    success: function (html) {
                        tmplHTML = html;
                        tmplCache.set(tmpl, html);
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
        events: [],
        ctrl: null,
        templates: {
            main: null
        }
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