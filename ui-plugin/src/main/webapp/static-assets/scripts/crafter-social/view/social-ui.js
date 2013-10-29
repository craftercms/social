(function (S) {

    var Dispatcher = S.Orchestrator,
        undefined, counter = 0,
        U = S.util,
        $ = S.$,
        _ = S.underscore;

    var SocialUI = S.Backbone.View.extend({
        className: 'crafter-social-view',
        initialize: function (oConfig) {
            this.guid = U.guid();
            // Extend defaults
            this.extendCfg(SocialUI.DEFAULTS, oConfig);
            // Initialise event listening
            // this.subscriptions();
            // Initialise UI actions
            this.initializeActionDelegation();
            // Create the UI
            this.createUI();
        },
        listen: function () {},
        identify: function (elem, prefix) {
            $(elem).attr({
                'id': '%@_%@'.fmt((prefix || ('elem' + counter++)), U.guid()),
                'data-component-guid': this.guid
            });
        },
        initializeActionDelegation: function () {
            var $elem = this.element(),
                me = this;
            $elem.delegate('[data-action]', 'click', function (evt) {
                evt.preventDefault();
                var action = $( this ).attr('data-action');
                if (action !== '') {
                    me[action]();
                }
            });
        },
        subscriptions: function () {
            var me = this,
                cfg = this.cfg;
            $.each(cfg.events, function (i, event) {
                $.each(Dispatcher.eventStates, function (j, type) {
                    var broadcast = Dispatcher.getBroadcastId(cfg.ctrl, event, type),
                        method = broadcast.substr(broadcast.indexOf('.') + 1);
                    if (me[method]) {
                        me.listenTo(Dispatcher, broadcast, function (data) {
                            me[method](data);
                        });
                    }
                });
            });
            return this;
        },
        extendCfg: function (oDefaults, oConfig) {
            if (!this.cfg) this.cfg = {};
            return $.extend(true, this.cfg, oDefaults, oConfig);
        },
        createUI: function () {
            this.element().html(this.getTemplate('main'));
        },
        element: function () {
            return this.$el;
        },
        getTemplate: function (tmplName) {

            var me = this,
                tmpl = this.cfg.templates[tmplName],
                cache = tmplCache.get(tmpl);

            if (U.isHTML(tmpl)) {
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
        cache: function (name, value) {
            if (!privates[this.guid]) privates[this.guid] = {};
            privates[this.guid][name] = value;
            return true;
        },
        getCache: function (name) {
            if (!privates[this.guid]) privates[this.guid] = {};
            return privates[this.guid][name];
        }
    });

    SocialUI.DEFAULTS = {
        events: [],
        ctrl: null,
        templates: {
            main: null
        }
    };

    var privates = {},
        tmplCache = {
            cache: {},
            set: function (key, value) {
                this.cache[key] = value;
                return true;
            },
            get: function (key) {
                return (this.cache[key] || null);
            }
        };

    // SocialUI.prototype = ;

    S.define('view.SocialUI', SocialUI);

}) (crafter.social);